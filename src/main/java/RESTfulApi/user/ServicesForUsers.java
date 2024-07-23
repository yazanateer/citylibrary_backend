package RESTfulApi.user;

import java.util.List;
 
public interface ServicesForUsers {
	
	
	public UserBoundary createUser(NewUserBoundary newUser);
	
	public UserBoundary login(String superapp, String email);
	
	public void updateUser(String superapp, String email, UserBoundary updatedUser);
	
	
	public void deleteAllUsers();
    public List<UserBoundary> getAllUsers() ;
 
 
	
}
