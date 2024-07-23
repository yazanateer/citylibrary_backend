package RESTfulApi.object;

import RESTfulApi.user.UserId;

public class CreatedBy {
	private UserId userId;

	public CreatedBy() {
		this.userId = new UserId();
	}
	public CreatedBy(UserId userId) {
		this.userId = userId;
	}

	public UserId getUserId() {
		return userId;
	}
	public void setUserId(UserId userId) {
		this.userId = userId;
	}
	@Override
	public String toString() {
		return "[userID=" + userId + "]";
	}
}