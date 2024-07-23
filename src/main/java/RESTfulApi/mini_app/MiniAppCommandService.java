package RESTfulApi.mini_app;

import java.util.List;

public interface MiniAppCommandService {

	public Object InvokeCommand(MiniAppCommandBoundary miniAppCommandBoundary);
	public void deleteAllCommands();
	public List<MiniAppCommandBoundary> getAllCommands();
	public List<MiniAppCommandBoundary> getSpecificCommands(String miniApp);
}
