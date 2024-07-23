package RESTfulApi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import RESTfulApi.user.RoleEnum;
import RESTfulApi.user.UserId;

@Entity
@Table(name = "USERS_TABLE")
public class UsersEntity {

	@Id private String id;
	private RoleEnum role;
	private String username;
	private String avatar;
	

	
	public UsersEntity() {
		
	}

	public UsersEntity(String id, UserId userId, RoleEnum role, String username, String avatar) {
		this.id = id;
		this.role = role;
		this.username = username;
		this.avatar = avatar;

	}
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
  
	public RoleEnum getRole() {
		return role;
	}

	public void setRole(RoleEnum role) {
		this.role = role;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	

	@Override
	public String toString() {
		return "UsersEntity [id=" + id + ", userId="  + ", role=" + role + ", username=" + username
				+ ", avatar=" + avatar + "]";
	}
	
	
 
	

	
}
