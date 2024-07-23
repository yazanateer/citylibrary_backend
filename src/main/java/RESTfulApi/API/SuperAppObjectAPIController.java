package RESTfulApi.API;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import RESTfulApi.object.ObjectBoundary;
 import RESTfulApi.object.ObjectsServiceImplementation;

@RestController
@RequestMapping("/superapp/objects")
//@CrossOrigin(origins = "http://localhost:3000") //to connect with the cliend-side (react.js) 
@CrossOrigin(origins = {"http://localhost:3000","http://localhost:3001","http://localhost:3002" }) 
public class SuperAppObjectAPIController {

    private final ObjectsServiceImplementation objectsService;
    private static final String DEFAULT_SIZE = "10";
    private static final String DEFAULT_PAGE = "0";
    private static final String KM = "km";
    private static final String MIL = "mil";

    @Autowired
    public SuperAppObjectAPIController(ObjectsServiceImplementation objectsService) {
        this.objectsService = objectsService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ObjectBoundary createObject(@RequestBody ObjectBoundary objectBoundary) {
        return objectsService.createObject(objectBoundary);
    }

    
    
    @PutMapping(path = "/{superapp}/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateObject(@PathVariable("superapp") String superApp,
                             @PathVariable("id") String objectId,  //internalObjectId
                             @RequestBody ObjectBoundary object,
                             @RequestParam(name = "userSuperapp", required = true) String userSuperapp,
                             @RequestParam(name = "userEmail", required = true) String userEmail) {
        objectsService.updateObject(superApp, objectId, object, userSuperapp, userEmail);
    }

    @GetMapping(path = "/{superapp}/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ObjectBoundary getSpecificObject(@RequestParam("userSuperapp") String userSuperapp,
                                            @RequestParam("userEmail") String userEmail,
                                            @PathVariable("superapp") String superapp, 
                                            @PathVariable("id") String objectId) {
        return objectsService.getSpecificObject(superapp, objectId, userSuperapp, userEmail);
    }
    
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ObjectBoundary[] getAllObjects(@RequestParam(name = "userSuperapp", required = true) String userSuperapp,
                                          @RequestParam(name = "userEmail", required = true) String userEmail,
                                          @RequestParam(name = "size", required = false, defaultValue = DEFAULT_SIZE) int size,
                                          @RequestParam(name = "page", required = false, defaultValue = DEFAULT_PAGE) int page) {
        return objectsService.getAllObjects(userSuperapp, userEmail, size, page).toArray(new ObjectBoundary[0]);
    }

    @GetMapping(path = "/search/byType/{type}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ObjectBoundary[] getObjectsByType(@PathVariable("type") String type,
                                             @RequestParam(name = "userSuperapp", required = true) String userSuperapp,
                                             @RequestParam(name = "userEmail", required = true) String userEmail,
                                             @RequestParam(name = "size", required = false, defaultValue = DEFAULT_SIZE) int size,
                                             @RequestParam(name = "page", required = false, defaultValue = DEFAULT_PAGE) int page) {
        return objectsService.getObjectsByType(userSuperapp, userEmail, type, size, page).toArray(new ObjectBoundary[0]);
    }

    @GetMapping(path = "/search/byAlias/{alias}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ObjectBoundary[] getObjectsByAlias(@PathVariable("alias") String alias,
                                              @RequestParam(name = "userSuperapp", required = true) String userSuperapp,
                                              @RequestParam(name = "userEmail", required = true) String userEmail,
                                              @RequestParam(name = "size", required = false, defaultValue = DEFAULT_SIZE) int size,
                                              @RequestParam(name = "page", required = false, defaultValue = DEFAULT_PAGE) int page) {
        return objectsService.getObjectsByAlias(userSuperapp, userEmail, alias, size, page).toArray(new ObjectBoundary[0]);
    }

    @GetMapping(path = "/search/byAliasPattern/{pattern}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ObjectBoundary[] getObjectsByAliasPattern(@PathVariable("pattern") String pattern,
                                                     @RequestParam(name = "userSuperapp", required = true) String userSuperapp,
                                                     @RequestParam(name = "userEmail", required = true) String userEmail,
                                                     @RequestParam(name = "size", required = false, defaultValue = DEFAULT_SIZE) int size,
                                                     @RequestParam(name = "page", required = false, defaultValue = DEFAULT_PAGE) int page) {
        return objectsService.getObjectsByAliasPattern(userSuperapp, userEmail, pattern, size, page).toArray(new ObjectBoundary[0]);
    }

    @GetMapping(path = "/search/byLocation/{lat}/{lng}/{distance}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ObjectBoundary[] getObjectsByLocation(@PathVariable("lat") double lat,
                                                 @PathVariable("lng") double lng,
                                                 @PathVariable("distance") double distance,
                                                 @RequestParam(name = "units", defaultValue = "NEUTRAL") String distanceUnits,
                                                 @RequestParam(name = "userSuperapp", required = true) String userSuperapp,
                                                 @RequestParam(name = "userEmail", required = true) String userEmail,
                                                 @RequestParam(name = "size", required = false, defaultValue = "20") int size,
                                                 @RequestParam(name = "page", required = false, defaultValue = "0") int page) {
        double metersUnit = convertToMeters(distance, distanceUnits);
        return objectsService.getObjectsByLocation(userSuperapp, userEmail, lat, lng, metersUnit, size, page).toArray(new ObjectBoundary[0]);
    }

    @GetMapping(path = "/search/byCircle/{lat}/{lng}/{distance}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ObjectBoundary[] getObjectsByCircleLocation(@PathVariable("lat") double lat,
                                                       @PathVariable("lng") double lng,
                                                       @PathVariable("distance") double distance,
                                                       @RequestParam(name = "units", defaultValue = "NEUTRAL") String distanceUnits,
                                                       @RequestParam(name = "userSuperapp", required = true) String userSuperapp,
                                                       @RequestParam(name = "userEmail", required = true) String userEmail,
                                                       @RequestParam(name = "size", required = false, defaultValue = "20") int size,
                                                       @RequestParam(name = "page", required = false, defaultValue = "0") int page) {
        double metersUnit = convertToMeters(distance, distanceUnits);
        return objectsService.getObjectsByCircleLocation(userSuperapp, userEmail, lat, lng, metersUnit, size, page).toArray(new ObjectBoundary[0]);
    }

    private double convertToMeters(double distance, String distanceUnits) {
        switch (distanceUnits.toLowerCase()) {
            case KM:
                return 1000 * distance;
            case MIL:
                return 1600 * distance;
            case "neutral":
                return distance;
            default:
                throw new IllegalArgumentException("Invalid distance unit: " + distanceUnits);
        }
    }
}
