package RESTfulApi.API;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import RESTfulApi.mini_app.CommandId;
import RESTfulApi.mini_app.MiniAppCommandBoundary;
import RESTfulApi.mini_app.MiniAppCommandService;

@RestController
@RequestMapping("/superapp/miniapp")
//@CrossOrigin(origins = "http://localhost:3000")//to connect with the cliend-side (react.js) 
@CrossOrigin(origins = {"http://localhost:3000","http://localhost:3001","http://localhost:3002" }) 
public class MiniAppController {

    private final MiniAppCommandService miniAppService;
    
    private String superappName;


	// this method injects a configuration value of spring
    @Value("${spring.application.name:iAmTheDefaultNameOfTheApplication}")
    public void setSpringApplicationName(String springApplicationName) {
        this.superappName = springApplicationName;
    }
    
    
    @Autowired
    public MiniAppController(MiniAppCommandService miniAppService) {
        this.miniAppService = miniAppService;
    }

    @PostMapping(value = "/{miniAppName}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object invokeMiniAppCommand(@PathVariable("miniAppName") String miniAppName, @RequestBody MiniAppCommandBoundary miniAppBoundary) {
    	
    	
    	miniAppBoundary.setCommandId(new CommandId(superappName, miniAppName, miniAppName));
        return miniAppService.InvokeCommand(miniAppBoundary);
    }
}
