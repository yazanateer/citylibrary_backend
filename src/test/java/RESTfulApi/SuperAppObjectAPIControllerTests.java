package RESTfulApi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import RESTfulApi.object.CreatedBy;
import RESTfulApi.object.Location;
import RESTfulApi.object.ObjectBoundary;
import RESTfulApi.object.ObjectId;
import RESTfulApi.user.NewUserBoundary;
import RESTfulApi.user.RoleEnum;
import RESTfulApi.user.UserBoundary;
import RESTfulApi.user.UserId;
import io.swagger.v3.oas.models.PathItem.HttpMethod;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SuperAppObjectAPIControllerTests {

    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private String baseUrl;

    @Value("${server.port:8084}")
    public void setPort(int port) {
        this.restTemplate = new RestTemplate();
        this.baseUrl = "http://localhost:" + port + "/superapp/objects";
    }

    @BeforeEach
    public void setUp() {
        // Ensure the user exists before each test
        createUser("testuser", "testavatar", "testuser@example.com", RoleEnum.SUPERAPP_USER);
        createUser("admin", "adminavatar", "admin@example.com", RoleEnum.ADMIN);
       
    }

  
    private void createUser(String username, String avatar, String email, RoleEnum role) {
        NewUserBoundary newUser = new NewUserBoundary();
        newUser.setUsername(username);
        newUser.setAvatar(avatar);
        newUser.setRole(role);
        newUser.setEmail(email);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            restTemplate.postForEntity("http://localhost:8084/superapp/users", newUser, UserBoundary.class);
            System.out.println("User created: " + email);
        } catch (HttpClientErrorException e) {
            System.err.println("User already exists or error creating user: " + e.getMessage());
        }
    }
    @Test
    public void testGetObjectsByType() {
        String superapp = "citylibrary";
        String email = "testuser@example.com";

        // Create some objects first
        for (int i = 0; i < 3; i++) {
            createUser("admin", "adminavatar", "admin@example.com", RoleEnum.ADMIN);
            ObjectBoundary object = createObject("TestAlias" + i, "TestType", email);
            restTemplate.postForEntity(
                    baseUrl,
                    object,
                    ObjectBoundary.class
            );
        }

        // Get objects by type
        String getUrl = baseUrl + "/search/byType/TestType?userSuperapp=" + superapp + "&userEmail=" + email;
        ResponseEntity<ObjectBoundary[]> response = restTemplate.getForEntity(getUrl, ObjectBoundary[].class);

        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(10, response.getBody().length);
    }


    private void deleteUser(String email) {
        try {
            restTemplate.delete("http://localhost:8084/superapp/users/citylibrary/" + email);
            System.out.println("User deleted: " + email);
        } catch (HttpClientErrorException e) {
            System.err.println("Error deleting user: " + e.getMessage());
        }
    }

    private void deleteAllObjects() {
       
    }

    private ObjectBoundary createObject(String alias, String type, String email) {
        ObjectBoundary object = new ObjectBoundary();
        object.setType(type);
        object.setAlias(alias);
        object.setActive(true);
        object.setCreationTimestamp(new Date());
        object.setLocation(new Location(32.0853, 34.7818));
        object.setCreatedBy(new CreatedBy(new UserId("superapp", email)));
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("key1", "value1");
        object.setObjectDetails(objectDetails);

        return object;
    }

    @Test
    public void testCreateObject() {
        ObjectBoundary object = createObject("TestAlias", "TestType", "testuser@example.com");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<ObjectBoundary> response = restTemplate.postForEntity(
                baseUrl,
                object,
                ObjectBoundary.class
        );

        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        assertNotNull(response.getBody());
       
    }

    @Test
    public void testUpdateObject() {
        String superapp = "citylibrary";
        String email = "testuser@example.com";

        // Create an object first
        ObjectBoundary object = createObject("TestAlias", "TestType", email);

        ResponseEntity<ObjectBoundary> createResponse = restTemplate.postForEntity(
                baseUrl,
                object,
                ObjectBoundary.class
        );

        assertEquals(HttpStatus.OK.value(), createResponse.getStatusCodeValue());
        assertNotNull(createResponse.getBody());
        String objectId = createResponse.getBody().getObjectId().getInternalObjectId();

        // Verify the created object has the correct user
        assertNotNull(createResponse.getBody().getCreatedBy());
        assertNotNull(createResponse.getBody().getCreatedBy().getUserId());
        assertEquals(email, createResponse.getBody().getCreatedBy().getUserId().getEmail());

        // Update the object
        object.setAlias("UpdatedAlias");
        object.setType("UpdatedType");
        object.setCreatedBy(new CreatedBy(new UserId(superapp, email)));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String updateUrl = baseUrl + "/" + superapp + "/" + objectId + "?userSuperapp=" + superapp + "&userEmail=" + email;
        restTemplate.put(updateUrl, new org.springframework.http.HttpEntity<>(object, headers));

        // Verify the update by retrieving the object again
        String getUrl = baseUrl + "/" + superapp + "/" + objectId + "?userSuperapp=" + superapp + "&userEmail=" + email;
        ResponseEntity<ObjectBoundary> response = restTemplate.getForEntity(getUrl, ObjectBoundary.class);

        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("UpdatedAlias", response.getBody().getAlias());
        assertEquals("UpdatedType", response.getBody().getType());
        assertNotNull(response.getBody().getCreatedBy());
        assertNotNull(response.getBody().getCreatedBy().getUserId());
        assertEquals(email, response.getBody().getCreatedBy().getUserId().getEmail());
    }

    
    
    @Test
    public void testGetSpecificObject() {
        // Create an object first
        ObjectBoundary object = createObject("TestAlias", "TestType", "testuser@example.com");

        ResponseEntity<ObjectBoundary> createResponse = restTemplate.postForEntity(
                baseUrl,
                object,
                ObjectBoundary.class
        );

        assertEquals(HttpStatus.OK.value(), createResponse.getStatusCodeValue());
        assertNotNull(createResponse.getBody());
        String objectId = createResponse.getBody().getObjectId().getInternalObjectId();

        // Get the specific object
        String getUrl = baseUrl + "/" + "citylibrary" + "/" + objectId + "?userSuperapp=" + "citylibrary" + "&userEmail=" + "testuser@example.com";
        ResponseEntity<ObjectBoundary> response = restTemplate.getForEntity(getUrl, ObjectBoundary.class);

        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }
    @Test
    public void testGetAllObjects() {
        String superapp = "citylibrary";
        String email = "testuser@example.com";

        // Create some objects first
        for (int i = 0; i < 10; i++) {
            ObjectBoundary object = createObject("TestAlias" + i, "TestType", email);
            ResponseEntity<ObjectBoundary> createResponse = restTemplate.postForEntity(
                    baseUrl,
                    object,
                    ObjectBoundary.class
            );
            assertEquals(HttpStatus.OK.value(), createResponse.getStatusCodeValue());
            assertNotNull(createResponse.getBody());
        }

        // Get all objects
        String getUrl = baseUrl + "?userSuperapp=" + superapp + "&userEmail=" + email;
        ResponseEntity<ObjectBoundary[]> response = restTemplate.getForEntity(getUrl, ObjectBoundary[].class);

        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(10, response.getBody().length);
    }


   

    @Test
    public void testGetObjectsByAlias() {
        String superapp = "citylibrary";
        String email = "testuser@example.com";

        // Create some objects first
        for (int i = 0; i < 3; i++) {
            ObjectBoundary object = createObject("TestAlias" + i, "TestType", email);

            restTemplate.postForEntity(
                    baseUrl,
                    object,
                    ObjectBoundary.class
            );
        }

        // Get objects by alias
        String alias = "TestAlias0";
        String getUrl = baseUrl + "/search/byAlias/" + alias + "?userSuperapp=" + superapp + "&userEmail=" + email;
        ResponseEntity<ObjectBoundary[]> response = restTemplate.getForEntity(getUrl, ObjectBoundary[].class);

        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(10, response.getBody().length);
    }


    @Test
    public void testGetObjectsByAliasPattern() {
        String superapp = "citylibrary";
        String email = "testuser@example.com";

        // Create some objects first
        for (int i = 0; i < 3; i++) {
            ObjectBoundary object = createObject("PatternAlias" + i, "TestType", email);

            restTemplate.postForEntity(
                    baseUrl,
                    object,
                    ObjectBoundary.class
            );
        }

        // Get objects by alias pattern
        String getUrl = baseUrl + "/search/byAliasPattern/Pattern?userSuperapp=" + superapp + "&userEmail=" + email;
        ResponseEntity<ObjectBoundary[]> response = restTemplate.getForEntity(getUrl, ObjectBoundary[].class);

        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(10, response.getBody().length);
    }
}
