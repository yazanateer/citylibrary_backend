package RESTfulApi.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort.Direction;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import RESTfulApi.BadRequestException;
import RESTfulApi.entity.UsersEntity;
import RESTfulApi.object.CheckFields;



@Service
public class UserServicesImplementation implements AllUserServices {


	private UserCRUD userCrud;
	private UserConvertor userConverter;
	private String superapp;

	@Autowired
	public UserServicesImplementation(UserCRUD userCrud, UserConvertor userConverter) {
		this.userCrud = userCrud;
		this.userConverter = userConverter;
	}

	@Value("${spring.application.name}")
	public void setSuperAppName(String superApp) {
		this.superapp = superApp;
	}
 
	
	
	
	
	
	@Override
	@Transactional
	public UserBoundary createUser(NewUserBoundary new_user) {
		RoleEnum[] roles= {RoleEnum.ADMIN, RoleEnum.MINIAPP_USER, RoleEnum.SUPERAPP_USER};

		if (new_user.getUsername() == null || new_user.getUsername().isEmpty() || new_user.getAvatar() == null
				|| new_user.getAvatar().isEmpty() || new_user.getRole() == null || (isValidRole(new_user.getRole().toString())) == false || new_user.getEmail() == null
				|| new_user.getEmail().isEmpty())
			throw new RuntimeException();
		
		UserBoundary user = new UserBoundary(new_user , superapp);
		user.setUsername(new_user.getUsername());
		user.setRole(new_user.getRole());
		user.setAvatar(new_user.getAvatar());	
		
		UsersEntity entity = this.userConverter.toEntity(user);
		entity = this.userCrud.save(entity);
		return userConverter.toBoundary(entity);

	}

	@Override
	@Transactional
	public UserBoundary login(String superapp, String email) {
	    Optional<UsersEntity> optionalEntity = this.userCrud.findById(superapp + "#" + email);

	    if (optionalEntity.isPresent()) {
	        UsersEntity entity = optionalEntity.get();
	            return userConverter.toBoundary(entity);
	    } else {
	        throw new RuntimeException("User not found");
	    }
	}
	
	

	public UserBoundary getUserBoundary(String id) {
		Optional<UsersEntity> optionalEntity = this.userCrud.findById(id);	
		
		if (!optionalEntity.isEmpty()) {
			UsersEntity entity=optionalEntity.get();
			return userConverter.toBoundary(entity);
		}else {
			throw new RuntimeException("USER NOT FOUND");
		}
		
	}

	@Override
	public void updateUser(String superapp, String email, UserBoundary updatedUser) {
		UsersEntity entity = this.userCrud.findById(superapp + "#" + email)
				.orElseThrow(() -> new RuntimeException("userEntity with id: " +superapp + "#" + email + " Does not exist in database"));
		
		if(updatedUser.getAvatar() != null)
			entity.setAvatar(updatedUser.getAvatar());
		
		if(updatedUser.getRole() != null && isValidRole(updatedUser.getRole( ).toString()))
			entity.setRole(updatedUser.getRole());
		
		if(updatedUser.getUsername() != null)
			entity.setUsername(updatedUser.getUsername());
		 
		this.userCrud.save(entity);
		System.err.println("updated in database: " + entity );
		
		
	}

	public boolean isValidRole(String roleEnum) {
        if (roleEnum == null) {
            return false;
        }

        try {
            RoleEnum.valueOf(roleEnum);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    
	}
	
	@Override
	@Transactional
	 public void deleteAllUsers() {
		 userCrud.deleteAll();
	 }
	
	
	@Override
	@Transactional (readOnly = true)
	public List<UserBoundary> getAllUsers() {
		List<UsersEntity> entities = this.userCrud.findAll();
		List<UserBoundary> rv = new ArrayList<>();
		for (UsersEntity e : entities) {
			rv.add(this.userConverter.toBoundary(e));
		}
		
		return rv;
	}
	
	
	
	//added for sprint 3 
	@Override
	public void deleteAllUsers(String userSuperapp, String userEmail) {
		if(CheckFields.getUserRole(userSuperapp, userEmail) != RoleEnum.ADMIN)
			throw new BadRequestException("only the admin can delete the users");
		userCrud.deleteAll();
	}
	
	@Override
	public List<UserBoundary> getAllUsers(String userSuperapp, String userEmail, int size, int page) {
		if(CheckFields.getUserRole(userSuperapp, userEmail) != RoleEnum.ADMIN)
			throw new BadRequestException("Only admin can get all users");
		return userCrud.findAll(PageRequest.of(page, size, Direction.ASC, "username", "Id"))
					.stream().map(this.userConverter::toBoundary).collect(Collectors.toList());

	}
	


}

	

