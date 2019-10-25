package wikiSpeakGUI.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import wikiSpeakGUI.CellPane;
import wikiSpeakGUI.CommandFactory;
import wikiSpeakGUI.SceneSwitcher;
import wikiSpeakGUI.tasks.GenerateVideoTask;
import wikiSpeakGUI.tasks.UpdateImageListTask;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;

public class VideoCreationController {


	private String _wikitTerm;
	private String _tempDir;
	private List<String> _audioGenResult;
	private SceneSwitcher ss = new SceneSwitcher();
	private BooleanBinding bb = null;

	

	// stores creations that have not finished generating, to prevent another creation with
	// the same name being created in the meantime
	private static List<String> generationList = new ArrayList<String>();

	
	@FXML
	private AnchorPane loadingPane;
	@FXML
	private TextField nameInput;


	@FXML
	private TableView<CellPane> imageView;

	@FXML
	private TableColumn<CellPane, HBox> imageCol;


	@FXML
	private Button submitCreationButton;

	@FXML
	private Button backButton;
	
	private Thread _imageDownloadThread = null;

	






	@FXML
	private void initialize() {
		
		imageCol.setStyle( "-fx-alignment: CENTER;");
		nameInput.setStyle("-fx-control-inner-background: rgb(049,055,060); "
				+ "-fx-text-fill: rgb(255,255,255); -fx-focus-color: rgb(255,255,255);");
		imageView.setStyle("-fx-control-inner-background: rgb(049,055,060); "
				+ "-fx-text-fill: rgb(255,255,255); -fx-focus-color: rgb(255,255,255);");

		imageView.setPlaceholder(new Label("No images to display"));

		// removes characters that cause hidden file creation 
		nameInput.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.matches("[^\\\\./$&:;\"]*")) {
					nameInput.setText(newValue.replaceAll("[\\\\./$&:;\"]", ""));
				}
			}
		});
		
		// disable submit button and make booleanBinding linked to name text field available.
		// this binding is only bound to the button when at least one image is selected, as to prevent
		// no images being included
		submitCreationButton.setDisable(true);
		
		bb = new BooleanBinding() {
		    {

		        super.bind(nameInput.textProperty());

		    }

		    @Override
		    protected boolean computeValue() {
		    	
		        return (nameInput.getText().trim().isEmpty());
		    }
		};


		
		

	}

	public void passInfo(String wikitTerm, String tempDir, List<String> audioGenResult, Thread imageDownloadThread) {
		_wikitTerm = wikitTerm;
		_tempDir = tempDir;
		_audioGenResult = audioGenResult;
		_imageDownloadThread  = imageDownloadThread;
		runTasksWaitingOnInfo();
	}






	@FXML
	private void handleBackButton(ActionEvent event) {
		ss.newScene("AudioCreationGUI.fxml", event);
	}


	@FXML
	private void handleSubmitCreation(ActionEvent event) {


		// abort flag cancels creation generation when set to true
		boolean abort = false;

		String name = nameInput.getText();






		CommandFactory command = new CommandFactory();

		List<String> nameCheckResult = command.sendCommand("./nameCheck.sh \"" + name + "\"", false);


		// error checking

		// checks supplied creation name has valid file name (informs user if invalid)
		// (as some characters are blocked, only case this triggers is no name or white space only)
		if (nameCheckResult.get(0).equals("Invalid Name") || (name == null)) {
			abort = true;
			Alert popup = new Alert(AlertType.INFORMATION);
			popup.setTitle("Invalid Name");
			popup.setHeaderText("The name \"" + name + "\" is invalid");
			popup.showAndWait();
			abort = true;
		}	

		// Informs user if creation with same name exists
		else if ((nameCheckResult.get(0).equals("Exists"))) {
			Alert popup = new Alert(AlertType.CONFIRMATION);
			popup.setTitle("Creation Exists");
			popup.setHeaderText("A creation with the name \"" + name + "\" aleardy exists");

			ButtonType buttonTypeYes = new ButtonType("Overwrite");
			ButtonType buttonTypeNo = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

			popup.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

			Optional<ButtonType> result = popup.showAndWait();

			// deletes file so the new creation can be made
			if (result.get() == buttonTypeYes){

				command.sendCommand("rm \"creations/" + name + ".mp4\"", false);
				command.sendCommand("rm -r \"creations/" + name + "\"", false);
			} 
			else {
				abort = true;
			}
		}

		// Prevents user creating a creation that has the same name as one already generating
		else if (generationList.contains(name)) {
			Alert popup = new Alert(AlertType.ERROR);
			popup.setTitle("Invalid name");
			popup.setHeaderText("A creation with the name \"" + name + "\" is still generating, please use another name");
			popup.show();
			abort = true;
		}



		// start creation generation in the background and return to main app GUI
		if (!abort) {
			

			

			AppGUIController appGUIController = (AppGUIController)ss.newScene("AppGUI.fxml", event);

			Thread generateCreation= new Thread(new GenerateVideoTask(_audioGenResult, name, _tempDir, _wikitTerm, appGUIController, imageView));
			generateCreation.setDaemon(true);
			generateCreation.start();



		}
	}
	
	// helper method to allow methods from other classes access the names of currently generating creations
	public static List<String> getCurrentlyGenerating(){
		return generationList;
	}
	
	
	private void runTasksWaitingOnInfo(){
		Thread updateImageListThread = new Thread(new UpdateImageListTask(_imageDownloadThread, loadingPane, imageView,
				imageCol, _tempDir, bb, submitCreationButton));
		updateImageListThread.setDaemon(true);
		updateImageListThread.start();
	}
}
