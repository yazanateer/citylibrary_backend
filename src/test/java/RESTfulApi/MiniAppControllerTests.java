package RESTfulApi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import RESTfulApi.user.NewUserBoundary;
import RESTfulApi.user.RoleEnum;
import RESTfulApi.user.UserBoundary;
import RESTfulApi.user.UserId;
import RESTfulApi.object.ObjectId;
import RESTfulApi.mini_app.MiniAppCommandBoundary;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class MiniAppControllerTests {

    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private String baseUrl;

    @Value("${server.port:8084}")
    public void setPort(int port) {
        this.restTemplate = new RestTemplate();
        this.baseUrl = "http://localhost:" + port + "/superapp/miniapp";
    }

    @BeforeEach
    public void setUp() {
        // Ensure the user exists before each test
        createUser("testuser", "testavatar", "testuser@example.com", RoleEnum.SUPERAPP_USER);
        createUser("admin", "adminavatar", "admin@example.com", RoleEnum.ADMIN);
    }

   /* @AfterEach
    public void tearDown() {
        // Clean up created commands after each test using admin user
        deleteCommands("superapp", "admin@example.com");

        // Optionally clean up the test user
        deleteUser("testuser@example.com");
        deleteUser("admin@example.com");
    }*/

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

    private void deleteUser(String email) {
        try {
            restTemplate.delete("http://localhost:8084/superapp/users/citylibrary/" + email);
            System.out.println("User deleted: " + email);
        } catch (HttpClientErrorException e) {
            System.err.println("Error deleting user: " + e.getMessage());
        }
    }

    private void deleteCommands(String superapp, String email) {
        try {
            restTemplate.delete("http://localhost:8084/superapp/admin/miniapp?userSuperapp=" + superapp + "&userEmail=" + email);
            System.out.println("Commands deleted for user: " + email);
        } catch (HttpClientErrorException e) {
            System.err.println("Error deleting commands: " + e.getMessage());
        }

        // Add a short delay to ensure the delete operation completes before proceeding
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testInvokeMiniAppCommand() throws Exception {
        String miniAppName = "patron_miniapp";

        MiniAppCommandBoundary command = new MiniAppCommandBoundary();
        command.setCommand("searchbooks");
        command.setInvocationTimestamp(new Date());
        command.setInvokedBy(new UserId("superapp", "testuser@example.com"));
        command.setTargetObject(new ObjectId("superapp", "objectId"));
        Map<String, Object> commandAttributes = new HashMap<>();
        commandAttributes.put("title", "Test Book");
        command.setCommandAttributes(commandAttributes);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Object> response = restTemplate.postForEntity(
                baseUrl + "/" + miniAppName,
                command,
                Object.class
        );

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        // Further assertions based on the expected response
    }

    @Test
    public void testInvokeMiniAppCommandInvalidCommand() throws Exception {
        String miniAppName = "patron_miniapp";

        MiniAppCommandBoundary command = new MiniAppCommandBoundary();
        command.setCommand("invalidcommand");
        command.setInvocationTimestamp(new Date());
        command.setInvokedBy(new UserId("superapp", "testuser@example.com"));
        command.setTargetObject(new ObjectId("superapp", "objectId"));
        Map<String, Object> commandAttributes = new HashMap<>();
        command.setCommandAttributes(commandAttributes);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            restTemplate.postForEntity(baseUrl + "/" + miniAppName, command, Object.class);
        } catch (HttpClientErrorException e) {
            assertEquals(400, e.getStatusCode().value());
        }
    }

    @Test
    public void testInvokeMiniAppCommandMissingAttributes() throws Exception {
        String miniAppName = "patron_miniapp";

        MiniAppCommandBoundary command = new MiniAppCommandBoundary();
        command.setCommand("searchbooks");
        command.setInvocationTimestamp(new Date());
        command.setInvokedBy(new UserId("superapp", "testuser@example.com"));
        command.setTargetObject(new ObjectId("superapp", "objectId"));
      
        Map<String, Object> commandAttributes = new HashMap<>();
        command.setCommandAttributes(commandAttributes);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            restTemplate.postForEntity(baseUrl + "/" + miniAppName, command, Object.class);
        } catch (HttpClientErrorException e) {
            assertEquals(400, e.getStatusCode().value());
            String responseBody = e.getResponseBodyAsString();
            System.out.println("Response body: " + responseBody);
            
        }
    }
}