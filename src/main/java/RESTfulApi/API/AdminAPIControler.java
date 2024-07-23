package RESTfulApi.API;

 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import RESTfulApi.mini_app.MiniAppCommandBoundary;
import RESTfulApi.mini_app.MiniAppCommandServiceImplementaion;
import RESTfulApi.object.ObjectsServiceImplementation;
import RESTfulApi.user.UserBoundary;
import RESTfulApi.user.UserServicesImplementation;

@RestController
@RequestMapping("/superapp/admin")
//@CrossOrigin(origins = "http://localhost:3000") //to connect with the cliend-side (react.js) 
@CrossOrigin(origins = {"http://localhost:3000","http://localhost:3001","http://localhost:3002" }) 
public class AdminAPIControler {

    private final ObjectsServiceImplementation objectsService;
    private final MiniAppCommandServiceImplementaion miniAppService;
    private final UserServicesImplementation userServicesImplementation;
    private static final String DEFAULT_SIZE = "10";
    private static final String DEFAULT_PAGE = "0";

    @Autowired
    public AdminAPIControler(ObjectsServiceImplementation objectsService, MiniAppCommandServiceImplementaion miniAppService, UserServicesImplementation userServicesImplementation) {
        this.objectsService = objectsService;
        this.miniAppService = miniAppService;
        this.userServicesImplementation = userServicesImplementation;
    }

    // Delete all users in superapp
    @DeleteMapping("/users")
    public void deleteAllUsers(
            @RequestParam(name = "userSuperapp") String userSuperapp,
            @RequestParam(name = "userEmail") String userEmail) {
        userServicesImplementation.deleteAllUsers(userSuperapp, userEmail);
    }

    // Delete all objects in superapp
    @DeleteMapping("/objects")
    public void deleteAllObjects(
            @RequestParam(name = "userSuperapp") String userSuperapp,
            @RequestParam(name = "userEmail") String userEmail) {
        objectsService.deleteAllObjects(userSuperapp, userEmail);
    }

    // Delete all commands
    @DeleteMapping("/miniapp")
    public void deleteAllCommands(
            @RequestParam(name = "userSuperapp") String userSuperapp,
            @RequestParam(name = "userEmail") String userEmail) {
        miniAppService.deleteAllCommands(userSuperapp, userEmail);
    }

    // Export all users
    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserBoundary[] getAllUsers(
            @RequestParam(name = "userSuperapp") String userSuperapp,
            @RequestParam(name = "userEmail") String userEmail,
            @RequestParam(name = "size", defaultValue = DEFAULT_SIZE) int size,
            @RequestParam(name = "page", defaultValue = DEFAULT_PAGE) int page) {
        return userServicesImplementation.getAllUsers(userSuperapp, userEmail, size, page).toArray(new UserBoundary[0]);
    }

    // Export all commands
    @GetMapping(value = "/miniapp", produces = MediaType.APPLICATION_JSON_VALUE)
    public MiniAppCommandBoundary[] getAllCommands(
            @RequestParam(name = "userSuperapp") String userSuperapp,
            @RequestParam(name = "userEmail") String userEmail,
            @RequestParam(name = "size", defaultValue = DEFAULT_SIZE) int size,
            @RequestParam(name = "page", defaultValue = DEFAULT_PAGE) int page) {
        return miniAppService.getAllCommands(userSuperapp, userEmail, size, page).toArray(new MiniAppCommandBoundary[0]);
    }

    // Get specific commands
    @GetMapping(value = "/miniapp/{miniAppName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public MiniAppCommandBoundary[] getSpecificCommands(
            @PathVariable("miniAppName") String miniAppName,
            @RequestParam(name = "userSuperapp") String userSuperapp,
            @RequestParam(name = "userEmail") String userEmail,
            @RequestParam(name = "size", defaultValue = DEFAULT_SIZE) int size,
            @RequestParam(name = "page", defaultValue = DEFAULT_PAGE) int page) {
        return miniAppService.getSpecificMiniAppCommands(miniAppName, userSuperapp, userEmail, size, page).toArray(new MiniAppCommandBoundary[0]);
    }
}
