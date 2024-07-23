package RESTfulApi.user;

import org.springframework.data.jpa.repository.JpaRepository;

import RESTfulApi.entity.UsersEntity;

public interface UserCRUD extends JpaRepository<UsersEntity, String>{

}
