package wikiSpeakGUI;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;

public class VideoCreationController {


	private String _wikitTerm;
	private String _tempDir;
	private List<String> _audioGenResult;
	private SceneSwitcher ss = new SceneSwitcher();

	// stores creations that have not finished generating, to prevent another creation with
	// the same name being created in the meantime
	private static List<String> generationList = new ArrayList<String>();

	@FXML
	private TextField nameInput;

	@FXML
	private ComboBox<String> noOfImages;

	@FXML
	private TableView<CellImage> imageView;

	@FXML
	private TableColumn<CellImage, ImageView> imageCol;


	@FXML
	private Button submitCreationButton;

	@FXML
	private Button backButton;

	@FXML
	private ImageView loadingIcon;
	
	@FXML
	private Button imageNoButton;





	@FXML
	private void initialize() {
		
		loadingIcon.setVisible(false);
		submitCreationButton.setDisable(true);
		imageCol.setStyle( "-fx-alignment: CENTER;");
		nameInput.setStyle("-fx-control-inner-background: rgb(049,055,060); "
				+ "-fx-text-fill: rgb(255,255,255); -fx-focus-color: rgb(255,255,255);");
		imageView.setStyle("-fx-control-inner-background: rgb(049,055,060); "
				+ "-fx-text-fill: rgb(255,255,255); -fx-focus-color: rgb(255,255,255);");
		noOfImages.setStyle("-fx-background-color: rgb(049,055,060); -fx-control-inner-background: rgb(049,055,060); "
				+ "-fx-text-fill: rgb(255,255,255); -fx-focus-color: rgb(255,255,255);");
		noOfImages.getItems().addAll("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
		noOfImages.getSelectionModel().select(0);

		imageView.setPlaceholder(new Label("No images to display"));

		// removes characters that cause hidden file creation 
		nameInput.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.matches("[^\\\\./$&:;]*")) {
					nameInput.setText(newValue.replaceAll("[\\\\./$&:;]", ""));
				}
			}
		});




	}

	public void passInfo(String wikitTerm, String tempDir, List<String> audioGenResult) {
		_wikitTerm = wikitTerm;
		_tempDir = tempDir;
		_audioGenResult = audioGenResult;
	}



	@FXML
	private void handleGetImage() {
		submitCreationButton.setDisable(true);
		imageNoButton.setDisable(true);
		loadingIcon.setVisible(true);
		String noOfImagesSelect = noOfImages.getSelectionModel().getSelectedItem();
		Thread thread = new Thread(new GetImagesTask(_wikitTerm, submitCreationButton, noOfImagesSelect, imageView,
				imageCol, _tempDir, loadingIcon, imageNoButton));
		thread.setDaemon(true);
		thread.start();
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

			Thread generateCreation= new Thread(new GenerateVideoTask(_audioGenResult, name, _tempDir, _wikitTerm, appGUIController));
			generateCreation.setDaemon(true);
			generateCreation.start();



		}
	}
	
	// helper method to allow methods from other classes access the names of currently generating creations
	public static List<String> getCurrentlyGenerating(){
		return generationList;
	}
}
