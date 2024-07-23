package RESTfulApi.object;

import java.util.List;

 import org.springframework.data.domain.Pageable;
 import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

import RESTfulApi.entity.SuperAppObjectEntity;

public interface ObjectsCrud extends ListCrudRepository<SuperAppObjectEntity, String> {

	List<SuperAppObjectEntity> findAll(Pageable pageable);
	List<SuperAppObjectEntity> findAllByType(@Param("type") String type, Pageable pageable);
	List<SuperAppObjectEntity> findAllByAlias(@Param("alias") String alias, Pageable pageable);

	@Query("SELECT p FROM SuperAppObjectEntity p WHERE p.alias LIKE :pattern")
	List<SuperAppObjectEntity> findAllByAliasPattern(@Param("pattern") String pattern, Pageable pageable);
	
 
	List<SuperAppObjectEntity> findAllByLatBetweenAndLngBetween(@Param("latMin") Double latMin,
			@Param("latMax") Double latMax, @Param("lngMin") Double lngMin, @Param("lngMax") Double lngMax,
			Pageable pageable);

	List<SuperAppObjectEntity> findAllByLatBetweenAndLngBetweenAndActive(@Param("latMin") Double latMin,
			@Param("latMax") Double latMax, @Param("lngMin") Double lngMin, @Param("lngMax") Double lngMax,
			@Param("active") boolean active, Pageable pageable);

	
    List<SuperAppObjectEntity> findAllByActiveTrue(Pageable pageable);
    List<SuperAppObjectEntity> findAllByAliasAndActiveTrue(String alias, Pageable pageable);
    List<SuperAppObjectEntity> findAllByTypeAndActiveTrue(@Param("type") String type, Pageable pageable);
    

}