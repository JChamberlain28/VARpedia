package wikiSpeakGUI;


import api.FlickrAPI;
import javafx.concurrent.Task;


public class GetImagesTask extends Task<Void>{
	
	private String _wikitTerm;
	private String _tempDir;


	
	
	public GetImagesTask(String wikitTerm, String tempDir) {
		_wikitTerm = wikitTerm;
		_tempDir = tempDir;

		
	}

	@Override
	protected Void call() {
		CommandFactory command = new CommandFactory();
		command.sendCommand("rm -f ./" + _tempDir + "/*.jpg", false);
		
		
		FlickrAPI.downloadImages(_wikitTerm, _tempDir, 10);

		return null;
	}
	

	

}
