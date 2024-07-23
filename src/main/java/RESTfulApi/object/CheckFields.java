package RESTfulApi.object;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import RESTfulApi.ObjectNotFoundException;
import RESTfulApi.entity.SuperAppObjectEntity;
import RESTfulApi.mini_app.MiniAppCommandBoundary;
import RESTfulApi.user.RoleEnum;
import RESTfulApi.user.UserCRUD;
 
@Component
public class CheckFields {

	 private static UserCRUD userCrud;
	 private static ObjectsCrud objectCrud;

	    @Autowired
	    public CheckFields(UserCRUD userCrud, ObjectsCrud objectCrud) {
	        this.userCrud = userCrud;
	        this.objectCrud = objectCrud;
	    }
	// Check if the string is valid email
		public static boolean isValidEmail(String email) {
			String emailRegex = "^[\\w\\.-]+@([\\da-z\\.-]+)\\.([a-z\\.]{2,6})$"; // Valid pattern of email
			Pattern pattern = Pattern.compile(emailRegex);
			Matcher matcher = pattern.matcher(email);
			return matcher.matches();
		}

	// Check if the input is valid user role
		public static boolean isValidUserRole(String input) {
			try {
				RoleEnum.valueOf(input);
				return true;
			} catch (IllegalArgumentException e) {
				return false;
			}
		}
		
	// Check invoke command input
		public static boolean checkInvokeCommandInput(MiniAppCommandBoundary miniAppCommandBoundary) {
		    if (miniAppCommandBoundary == null) {
		        return false;
		    }
		    if (miniAppCommandBoundary.getCommand() == null 
		            || miniAppCommandBoundary.getCommand().isBlank()
		            || miniAppCommandBoundary.getInvokedBy() == null
		            || miniAppCommandBoundary.getInvokedBy().getEmail() == null
		            || !CheckFields.isValidEmail(miniAppCommandBoundary.getInvokedBy().getEmail())
		            || miniAppCommandBoundary.getInvokedBy().getSuperapp() == null
		            || miniAppCommandBoundary.getInvokedBy().getSuperapp().isBlank()
		            || miniAppCommandBoundary.getTargetObject() == null
		            || miniAppCommandBoundary.getTargetObject().getInternalObjectId() == null
		            || miniAppCommandBoundary.getTargetObject().getInternalObjectId().isBlank()
		            || miniAppCommandBoundary.getTargetObject().getSuperapp() == null
		            || miniAppCommandBoundary.getTargetObject().getSuperapp().isBlank()
		            || miniAppCommandBoundary.getCommandId() == null
		            || miniAppCommandBoundary.getCommandId().getMiniapp() == null
		            || miniAppCommandBoundary.getCommandId().getMiniapp().isBlank()) {
		        return false;
		    }
		    return true;
		}
		
	// Check if location is less than radius from center
		public static  boolean checkIfInTheCircle(double center_lat, double center_long, double lat, double lng, double radius) {
			double distance = Math.sqrt(Math.pow(center_lat - lat, 2) + Math.pow(center_long - lng, 2));
			return distance <= radius;
		}
		
	// Get user role
		public static RoleEnum getUserRole(String userSuperapp, String userEmail) {
			return userCrud.findById(userSuperapp + "#" + userEmail) // Find the user
					.orElseThrow(() -> new ObjectNotFoundException("User is not exist")).getRole(); // Not find user	
		}
		
 
		
	// Check if target object is exist and active
		public static boolean checkIfTargetObjectIsExistAndActive(MiniAppCommandBoundary miniAppCommandBoundary) {
		    ObjectId targetObject = miniAppCommandBoundary.getTargetObject();

		    if (targetObject == null ) {
		        return false; // Target object or object ID is missing
		    }

		    String superApp = targetObject.getSuperapp();
		    String internalID = targetObject.getInternalObjectId();

		    Optional<SuperAppObjectEntity> objectOptional = objectCrud.findById(superApp + "#" + internalID);
		    if (objectOptional.isEmpty() ) {
		        return false; // Object not found or not active
		    }

		    return true; // Target object is found and active
		}

}
