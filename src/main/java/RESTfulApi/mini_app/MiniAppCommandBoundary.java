package RESTfulApi.mini_app;

import java.util.Date;
import java.util.Map;

import RESTfulApi.object.ObjectId;
import RESTfulApi.user.UserId;
public class MiniAppCommandBoundary {

	private CommandId commandId;
	private String command;
	private ObjectId targetObject;
	private Date invocationTimestamp;
	private UserId invokedBy;
	private Map<String, Object> commandAttributes;
	
	
	
	public MiniAppCommandBoundary() {
		invokedBy=new UserId();
		this.commandId=new CommandId();

	}
	
	
	public MiniAppCommandBoundary(CommandId commandId, String command, ObjectId targetObject, Date invocationTimestamp,
			UserId invokedBy, String superApp, String email, Map<String, Object> commandAttributes) {
		this.commandId = commandId;
		this.command = command;
		this.targetObject = targetObject;
		this.invocationTimestamp = invocationTimestamp;
		this.invokedBy = new UserId(superApp, email);
		this.commandAttributes = commandAttributes;
	}
	
	
	public CommandId getCommandId() {
		return commandId;
	}
	public void setCommandId(CommandId commandId) {
		this.commandId = commandId;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public ObjectId getTargetObject() {
		return targetObject;
	}
	public void setTargetObject(ObjectId targetObject) {
		this.targetObject = targetObject;
	}
	public Date getInvocationTimestamp() {
		return invocationTimestamp;
	}
	public void setInvocationTimestamp(Date invocationTimestamp) {
		this.invocationTimestamp = invocationTimestamp;
	}

	public Map<String, Object> getCommandAttributes() {
		return commandAttributes;
	}

	public void setCommandAttributes(Map<String, Object> commandAttributes) {
		this.commandAttributes = commandAttributes;
	}
	
	public UserId getInvokedBy() {
		return invokedBy;
	}
	public void setInvokedBy(UserId invokedBy) {
		this.invokedBy = invokedBy;
	}
	@Override
	public String toString() {
		return "MiniAppCommandBoundary [commandId=" + commandId + ", command=" + command + ", targetObject=" + targetObject
				+ ", invocationTimestamp=" + invocationTimestamp + ", invokedBy=" + invokedBy + ", commandAttributes="
				+ commandAttributes + "]";
	}

	
 
	
	
	
	
	
	
	
	
}
