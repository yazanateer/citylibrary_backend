package RESTfulApi.mini_app;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import RESTfulApi.entity.MiniAppCommandEntity;
import jakarta.annotation.PostConstruct;
import RESTfulApi.object.ObjectId;

@Component
public class MiniAppCommandConverter {

	private ObjectMapper jackson;
	
	@PostConstruct
	public void init() {
		this.jackson = new ObjectMapper();
	}


	public MiniAppCommandEntity toEntity(MiniAppCommandBoundary boundary) {
		MiniAppCommandEntity entity = new MiniAppCommandEntity();
		
		entity.setCommandId(boundary.getCommandId().toString());
		entity.setCommand(boundary.getCommand().toString());
		
		   if (boundary.getCommandId().getMiniapp() != null) {
		        entity.setMiniApp(boundary.getCommandId().getMiniapp());
		    }

		   
		   
		if (boundary.getCommandId() == null) {
			entity.setCommandId("");
		}else {
			String convertedCommandId = "";
			//check every string
			//InternalCommandID
			if (boundary.getCommandId().getId() != null) {
				convertedCommandId = boundary.getCommandId().getId();
			}
			//MiniApp
			if (boundary.getCommandId().getMiniapp() != null) {
				if(convertedCommandId.length() != 0)
					convertedCommandId += "#" + boundary.getCommandId().getMiniapp(); 
				else
					convertedCommandId += boundary.getCommandId().getMiniapp(); 
			}
			//SuperApp
			if (boundary.getCommandId().getSuperapp() != null) {
				if(convertedCommandId.length() != 0)
					convertedCommandId += "#" + boundary.getCommandId().getSuperapp();
				else
					convertedCommandId += boundary.getCommandId().getSuperapp();
			}
			entity.setCommandId(convertedCommandId);
		}
		
		//getCommand    ------------------------------------------------------------
		if (boundary.getCommand() == null) { // getCommand? setCommand?
			entity.setCommand("");
		}else {
			entity.setCommand(boundary.getCommand());
		}
		
 		if (boundary.getTargetObject() == null) {
			entity.setTargetObject("");
		}else {
			String convertedTargetObject = "";
 
			if (boundary.getTargetObject().getInternalObjectId() != null) {
				convertedTargetObject += boundary.getTargetObject().getInternalObjectId();
			}
 			if (boundary.getTargetObject().getSuperapp()!= null) {
				if (convertedTargetObject.length() != 0)
					convertedTargetObject += "#" + boundary.getTargetObject().getSuperapp();
				else
					convertedTargetObject += boundary.getTargetObject().getSuperapp();
			}
			entity.setTargetObject(convertedTargetObject);
		}
		
 		if (boundary.getInvokedBy() == null) {
			entity.setInvokedByEmail(" ");
			entity.setInvokedBySuperApp(" ");;
		}else {
	
			if (boundary.getInvokedBy().getEmail() != null) {
				entity.setInvokedByEmail(boundary.getInvokedBy().getEmail());
			}
	
			if(boundary.getInvokedBy().getSuperapp() != null) {
				entity.setInvokedBySuperApp(boundary.getInvokedBy().getSuperapp());;

			}
		}
		entity.setInvokationTimeStamp(boundary.getInvocationTimestamp()); 		
		entity.setCommandAttributes(this.toEntity(boundary.getCommandAttributes()));  
		return entity;
	}



	
	
	public MiniAppCommandBoundary toBoundary (MiniAppCommandEntity entity) {

		MiniAppCommandBoundary boundary = new MiniAppCommandBoundary();
		boundary.setCommandId(commandIdToBoundary(entity.getCommandId()));
		boundary.setCommand(entity.getCommand());
		
		   // Set miniApp
	    boundary.getCommandId().setMiniapp(entity.getMiniApp());

		
		
		boundary.setTargetObject(objectIdToBoundary(entity.getTargetObject()));
		boundary.setInvocationTimestamp(entity.getInvokationTimeStamp());
		System.err.println(boundary.getInvokedBy().getEmail());
		boundary.getInvokedBy().setEmail(entity.getInvokedByEmail());
		boundary.getInvokedBy().setSuperapp(entity.getInvokedBySuperApp());

		try {
			boundary.setCommandAttributes(
				this.jackson.readValue(entity.getCommandAttributes(), Map.class));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return boundary;
	}
	
	
	
	
	
		public String toEntity (Map<String, Object> data) {
		try {
			return this.jackson
				.writeValueAsString(data);
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
		
		
	public CommandId commandIdToBoundary(String entityCommand) {
		String[] id = entityCommand.split("#");
		return new CommandId(id[2], id[1], id[0]);
	}
	public ObjectId objectIdToBoundary(String entityObjId) {
		String[] id = entityObjId.split("#");
		return new ObjectId(id[1], id[0]);
	}
 
   
	
}
