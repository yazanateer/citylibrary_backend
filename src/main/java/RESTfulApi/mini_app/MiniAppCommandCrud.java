package RESTfulApi.mini_app;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import RESTfulApi.entity.MiniAppCommandEntity;

public interface MiniAppCommandCrud extends ListCrudRepository<MiniAppCommandEntity, String>, PagingAndSortingRepository<MiniAppCommandEntity, String> {
	List<MiniAppCommandEntity> findAllByMiniApp(@Param("miniApp") String miniAppp, Pageable pageable);

}
