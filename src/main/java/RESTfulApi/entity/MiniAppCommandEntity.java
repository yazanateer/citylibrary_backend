package RESTfulApi.entity;

import java.util.Date;
 
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
 
@Entity
@Table(name = "Mini_App_Command_Entity")
public class MiniAppCommandEntity {

	@Id
	private String commandId;
	private String command;
	private String miniApp;
	private String targetObject;
	private String invokedByEmail;
	private String invokedBySuperApp;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date invokationTimeStamp;
	
	@Lob
	private String commandAttributes;
	

	public MiniAppCommandEntity () {
		
	}
	
	public String getCommandId() {
		return commandId;
	}

	public void setCommandId(String commandId) {
		this.commandId = commandId;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getTargetObject() {
		return targetObject;
	}

	public void setTargetObject(String targetObject) {
		this.targetObject = targetObject;
	}

	public String getInvokedByEmail() {
		return invokedByEmail;
	}

	public void setInvokedByEmail(String invokedByEmail) {
		this.invokedByEmail = invokedByEmail;
	}

	public String getInvokedBySuperApp() {
		return invokedBySuperApp;
	}

	public void setInvokedBySuperApp(String invokedBySuperApp) {
		this.invokedBySuperApp = invokedBySuperApp;
	}

	public Date getInvokationTimeStamp() {
		return invokationTimeStamp;
	}

	public void setInvokationTimeStamp(Date invokationTimeStamp) {
		this.invokationTimeStamp = invokationTimeStamp;
	}

	public String getCommandAttributes() {
		return commandAttributes;
	}

	public void setCommandAttributes(String commandAttributes) {
		this.commandAttributes = commandAttributes;
	}

	
	public String getMiniApp() {
		return miniApp;
	}
	
	public void setMiniApp(String miniApp) {
		this.miniApp = miniApp;
	}
	
	@Override
	public String toString() {
		return "MiniAppCommandEntity [commandId=" + commandId + ", command=" + command + ", targetObject="
				+ targetObject + ", invokedByEmail=" + invokedByEmail + ", invokedBySuperApp=" + invokedBySuperApp
				+ ", invokationTimeStamp=" + invokationTimeStamp + ", commandAttributes=" + commandAttributes + "]";
	}
	
	
	
	
	
	
}
