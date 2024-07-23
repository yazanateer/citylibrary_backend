package RESTfulApi.user;

public class UserBoundary {

	private UserId userid;
	private RoleEnum role;
	private String username;
	private String avatar;
	
	
	
	public UserBoundary() {
       

	}
	
	public UserBoundary(NewUserBoundary newUser , String springAppName) {
		
		
		this.userid = new UserId(springAppName, newUser.getEmail());
		this.role=newUser.getRole();
		this.avatar=avatar;
		this.username= username;
		}
		
	
	public UserBoundary(UserId userid, RoleEnum role, String username, String avatar) {
		super();
		this.userid = userid;
		this.role = role;
		this.username = username;
		this.avatar = avatar;
	}
	
	public UserId getUserid() {
		return userid;
	}
	public void setUserid(UserId userid) {
		this.userid = userid;
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
		return "UserBoundary [userid=" + userid + ", role=" + role + ", username=" + username + ", avatar=" + avatar
				+ "]";
	}
	
}
