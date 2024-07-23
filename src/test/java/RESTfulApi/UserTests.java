package RESTfulApi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import RESTfulApi.user.NewUserBoundary;
import RESTfulApi.user.RoleEnum;
import RESTfulApi.user.UserBoundary;
import RESTfulApi.user.UserId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterEach;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)

public class UserTests {

    private RestTemplate restTemplate;
   

    @Autowired
    private ObjectMapper objectMapper;

    private String baseUrl;

    @Value("${server.port:8084}")
    public void setPort(int port) {
        this.restTemplate = new RestTemplate();
        this.baseUrl = "http://localhost:" + port + "/superapp/users";
    }

    @BeforeEach
    public void setUp() {
        // Ensure the user exists before each test
        NewUserBoundary newUser = new NewUserBoundary();
        newUser.setUsername("testuser");
        newUser.setAvatar("testavatar");
        newUser.setRole(RoleEnum.SUPERAPP_USER);
        newUser.setEmail("testuser@example.com");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            restTemplate.postForEntity(baseUrl, newUser, UserBoundary.class);
        } catch (Exception e) {
            
        }
     
    }

   
    @Test
    public void testCreateUser() throws Exception {
        NewUserBoundary newUser = new NewUserBoundary();
        newUser.setUsername("testuser");
        newUser.setAvatar("testavatar");
        newUser.setRole(RoleEnum.SUPERAPP_USER);
        newUser.setEmail("testuser@example.com");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<UserBoundary> response = restTemplate.postForEntity(
                baseUrl,
                newUser,
                UserBoundary.class
        );
     

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("testuser", response.getBody().getUsername());
        assertEquals("testuser@example.com", response.getBody().getUserid().getEmail());
    }

    @Test
    public void testLogin() throws Exception {
        String superapp = "citylibrary";
        String email = "testuser@example.com";

        String loginUrl = baseUrl + "/login/" + superapp + "/" + email;

        ResponseEntity<UserBoundary> response = restTemplate.getForEntity(
                loginUrl,
                UserBoundary.class
        );

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(email, response.getBody().getUserid().getEmail());
    }

    @Test
    public void testUpdateUser() throws Exception {
        String superapp = "citylibrary";
        String email = "testuser@example.com";

        UserBoundary updatedUser = new UserBoundary();
        updatedUser.setUsername("updateduser");
        updatedUser.setAvatar("updatedavatar");
        updatedUser.setRole(RoleEnum.SUPERAPP_USER);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        restTemplate.put(
                baseUrl + "/" + superapp + "/" + email,
                updatedUser
        );

        // Verify the update by retrieving the user again
        String loginUrl = baseUrl + "/login/" + superapp + "/" + email;
        ResponseEntity<UserBoundary> response = restTemplate.getForEntity(
                loginUrl,
                UserBoundary.class
        );

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("updateduser", response.getBody().getUsername());
        assertEquals("updatedavatar", response.getBody().getAvatar());
    }
}