package RESTfulApi.object;

import java.util.List;
public interface AllObjectServices extends ObjectsService {
	
	public List<ObjectBoundary> getObjectsByType(String userSuperapp, String userEmail, String type, int size, int page);
	public List<ObjectBoundary> getObjectsByAlias(String userSuperapp, String userEmail, String alias, int size, int page);
	public List<ObjectBoundary> getObjectsByAliasPattern(String userSuperapp, String userEmail, String pattern, int size, int page);
	
	public List<ObjectBoundary> getObjectsByLocation(String userSuperapp, String userEmail, double lat, double lng, double distance, int size, int page);
	public void deleteAllObjects(String userSuperapp, String userEmail);

	//the bonus for sprint 3  ( circle search location ) 
	public List<ObjectBoundary> getObjectsByCircleLocation(String userSuperapp, String userEmail, double lat, double lng, double distance, int size, int page);




	//the second table 
	public ObjectBoundary getSpecificObject(String objectSuperApp, String internalObjectId, String userSuperapp, String userEmail);
	public ObjectBoundary updateObject(String objectSuperApp, String internalObjectId, ObjectBoundary update, String userSuperapp, String userEmail);
	public List<ObjectBoundary> getAllObjects(String userSuperapp, String userEmail, int size, int page);


 
}
