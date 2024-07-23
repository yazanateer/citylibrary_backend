package RESTfulApi.API;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import RESTfulApi.user.LoginBoundary;
import RESTfulApi.user.NewUserBoundary;
import RESTfulApi.user.UserBoundary;
import RESTfulApi.user.UserServicesImplementation;

@RestController
@RequestMapping("/superapp/users")
//@CrossOrigin(origins = "http://localhost:3000") //to connect with the cliend-side (react.js) 
@CrossOrigin(origins = {"http://localhost:3000","http://localhost:3001","http://localhost:3002" }) 
public class UserController {

    private final UserServicesImplementation userServices;

    @Autowired
    public UserController(UserServicesImplementation userServices) {
        this.userServices = userServices;
    }

    // POST method to create a user
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserBoundary createUser(@RequestBody NewUserBoundary boundary) {
        if (boundary == null || !userServices.isValidRole(boundary.getRole().toString())) {
            throw new IllegalArgumentException("Invalid role");
        }
        return this.userServices.createUser(boundary);
    }


    // GET method for login
    @GetMapping(path = "/login/{superapp}/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserBoundary login(@PathVariable("superapp") String superapp, @PathVariable("email") String email) {
        return userServices.login(superapp, email);
    }
    
    
    // PUT method to update user
    @PutMapping(path = "/{superapp}/{email}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateUser(@PathVariable("superapp") String superapp, @PathVariable("email") String email, @RequestBody UserBoundary updatedUser) {
        userServices.updateUser(superapp, email, updatedUser);
    }
}
