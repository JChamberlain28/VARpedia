package wikiSpeakGUI.tasks;


import api.FlickrAPI;
import javafx.concurrent.Task;
import wikiSpeakGUI.CommandFactory;


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
		
		// clears all images currently existing and calls flickrAPI to download them
		command.sendCommand("rm -f ./" + _tempDir + "/*.jpg", false);


		FlickrAPI.downloadImages(_wikitTerm, _tempDir, 10);

		return null;
	}




}
