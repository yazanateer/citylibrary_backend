package RESTfulApi.user;

import java.util.List;

public interface AllUserServices extends ServicesForUsers {

	//added for sprint 3 
	public void deleteAllUsers(String userSuperapp, String userEmail);
	public List<UserBoundary> getAllUsers(String userSuperapp, String userEmail, int size, int page);
}
