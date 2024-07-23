package RESTfulApi.user;

import org.springframework.stereotype.Component;

import RESTfulApi.entity.UsersEntity;

@Component
public class UserConvertor {

	public UsersEntity toEntity(UserBoundary boundary) {
		UsersEntity entity = new UsersEntity();
		
		entity.setId(userIdToString(boundary.getUserid()));
		entity.setRole(boundary.getRole());
		entity.setUsername(boundary.getUsername());
		entity.setAvatar(boundary.getAvatar());		
		
		return entity;
	}
	
	public UserBoundary toBoundary(UsersEntity entity) {
		UserBoundary boundary = new UserBoundary();
		boundary.setUserid(userIdToBoundary(entity.getId()));
		boundary.setRole(entity.getRole());
		boundary.setUsername(entity.getUsername());
		boundary.setAvatar(entity.getAvatar());
		
		
		return boundary;
	}
	
	
	// user id to string
	private String userIdToString(UserId userId) {
		return userId.getSuperapp() + "#" + userId.getEmail();
	}
	// User id to boundary
	public UserId userIdToBoundary(String entityId) {
		String[] id = entityId.split("#");
		return new UserId(id[0], id[1]);
	}
}
