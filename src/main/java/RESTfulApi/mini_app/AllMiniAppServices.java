package RESTfulApi.mini_app;

import java.util.List;

public interface AllMiniAppServices extends MiniAppCommandService {

	public List<MiniAppCommandBoundary> getAllCommands(String userSuperapp, String userEmil, int size, int page);
	
	public List<MiniAppCommandBoundary> getSpecificMiniAppCommands(String miniAppName, String userSuperapp, String userEmail, int size, int page);

	public void deleteAllCommands(String userSuperapp, String userEmail);
				
}
