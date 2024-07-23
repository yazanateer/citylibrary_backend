package RESTfulApi.object;

import java.util.List;

public interface ObjectsService {
   public ObjectBoundary createObject(ObjectBoundary objectBoundary); //create new object 
   
   //updated to sprint 3  "added the userSuperapp and userEmail"
   public void updateObject(String superApp, String internalObjectId,ObjectBoundary objectBoundary); //update specefic object
  
   public ObjectBoundary getObject(String superApp, String internalObjectId); // retrieve object by id 
   public List<ObjectBoundary> getAllObjects(); //get all objects
   
   public void deleteAllObjects();
   



}