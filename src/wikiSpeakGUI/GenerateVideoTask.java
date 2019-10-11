package wikiSpeakGUI;

import java.util.List;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;

public class GenerateVideoTask extends Task<Void>  {


	private String _creationName;
	private List<String> _AudioGenResult;
	private String _tempDir;
	private String _wikitTerm;
	private AppGUIController _controllerForUpdate;
	List<String> vidControllerGenList = null;
	private TableView<CellPane> _imageTable;

	public GenerateVideoTask(List<String> AudioGenResult, String creationName, String tempDir, String wikitTerm, AppGUIController controllerForUpdate, TableView<CellPane> imageView) {
		_AudioGenResult = AudioGenResult;
		_creationName = creationName;
		_tempDir = tempDir;
		_wikitTerm = wikitTerm;
		_controllerForUpdate = controllerForUpdate;
		_imageTable = imageView;
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

		// deletes all images that were not selected to be in the creation	
		for (int i = 0; i <_imageTable.getItems().size(); i++) {
			CellPane cellPane = _imageTable.getItems().get(i);
			CheckBox selectImageBox = (CheckBox)cellPane.getPane().getChildren().get(1);
			
			// if check box not ticked, associated image path is retrieved and deleted
			if (selectImageBox.isSelected() == false) {
				ImageView imageView = (ImageView) cellPane.getPane().getChildren().get(0);
				TrackedImage image = (TrackedImage)imageView.getImage();
				
				// format string in correct way for bash rm command
				String[] urlForDeletion = image.getURL().split("TempCreation");
				generationScript.sendCommand("rm -f ./TempCreation" + urlForDeletion[1], false);
			}

		}



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
