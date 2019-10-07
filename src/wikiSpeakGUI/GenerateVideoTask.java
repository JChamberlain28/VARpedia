package wikiSpeakGUI;

import java.util.List;

import javafx.application.Platform;
import javafx.concurrent.Task;

public class GenerateVideoTask extends Task<Void>  {
	
	
	private String _creationName;
	private List<String> _AudioGenResult;
	private String _tempDir;
	private String _wikitTerm;
	private AppGUIController _controllerForUpdate;
	List<String> vidControllerGenList = null;

	public GenerateVideoTask(List<String> AudioGenResult, String creationName, String tempDir, String wikitTerm, AppGUIController controllerForUpdate) {
		_AudioGenResult = AudioGenResult;
		_creationName = creationName;
		_tempDir = tempDir;
		_wikitTerm = wikitTerm;
		_controllerForUpdate = controllerForUpdate;
	}

	@Override
	protected Void call() {
		
		
		VideoCreationController.getCurrentlyGenerating().add(_creationName);
		
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				_controllerForUpdate.updateCreationList();
				
			}
			
		});
		
		CommandFactory generationScript = new CommandFactory();
			
			
			// audio generation script returns audio duration
			double audioTime = Double.parseDouble(_AudioGenResult.get(0));
			audioTime = audioTime + 1; // make video 1 second longer than audio

			
			generationScript.sendCommand("./generateVid.sh \"" + _creationName + "\" " + _tempDir + " \"" + _wikitTerm + "\" " + audioTime, false);
		return null;
	}
	
	
	@Override
	protected void done() {
		
		VideoCreationController.getCurrentlyGenerating().remove(VideoCreationController.getCurrentlyGenerating().indexOf(_creationName));

		
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				_controllerForUpdate.updateCreationList();
				
			}
			
		});
	}

	
	
}
