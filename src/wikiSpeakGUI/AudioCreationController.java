package wikiSpeakGUI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.scene.control.Alert.AlertType;

public class AudioCreationController {


	private String _numberedText;
	private static String _tempDir;
	private static String _wikitTerm;
	private static List<String> audioGenResult;
	private static ArrayList<String> audioSentences=new ArrayList<String>();
	private static ArrayList<String> savedAudio=new ArrayList<String>();
	private static String savedText;
	private SceneSwitcher ss = new SceneSwitcher();
	private int count=savedAudio.size();




	@FXML
	private Button cancelButton;

	@FXML
	private Button previewButton;

	@FXML
	private ComboBox<String> voiceSelect;

	@FXML
	private Button nextButton;

	@FXML
	private TextArea numberedTextArea;


	@FXML
	private Button speakButton;

	@FXML
	private Button addButton;

	@FXML
	private Button delButton;

	@FXML
	private Button upButton;

	@FXML
	private Button downButton;

	@FXML
	private ListView<String> selectedAudio;

	@FXML
	private ImageView combineAudioLoading;

	@FXML
	private void initialize() {
		//formats the TextArea
		combineAudioLoading.setVisible(false);
		numberedTextArea.setWrapText(true);
		numberedTextArea.setEditable(true);
		numberedTextArea.setStyle("-fx-control-inner-background: rgb(049,055,060); "
				+ "-fx-text-fill: rgb(255,255,255); -fx-focus-color: rgb(255,255,255);");
		voiceSelect.setStyle("-fx-background-color: rgb(049,055,060); -fx-control-inner-background: rgb(049,055,060); "
				+ "-fx-text-fill: rgb(255,255,255); -fx-focus-color: rgb(255,255,255);");
		selectedAudio.setStyle("-fx-control-inner-background: rgb(049,055,060); "
				+ "-fx-text-fill: rgb(255,255,255); -fx-focus-color: rgb(255,255,255);");

		//adds voice options in a drop down box
		voiceSelect.getItems().add("Default Voice");
		voiceSelect.getItems().add("Akl_NZ(male) Voice");
		voiceSelect.getItems().add("Akl_NZ(female) Voice");
		voiceSelect.getSelectionModel().selectFirst();
		numberedTextArea.setText(savedText);

		//loads saved audio chunks
		selectedAudio.getItems().clear();
		for(int i=0; i<savedAudio.size();i++) {
			selectedAudio.getItems().add(savedAudio.get(i));
		}

		savedAudio.clear();

		//disables buttons 
		if((selectedAudio.getItems().size() == 0)) {
			previewButton.setDisable(true);
			nextButton.setDisable(true);
			delButton.setDisable(true);
			upButton.setDisable(true);
			downButton.setDisable(true);	
			
			
			

		}
	}




	// Allows other AppGUIController to pass info to this controller
	// and display the text in the create scene.
	public void passInfo(String numberedText, String tempDir, String wikitTerm) {
		_numberedText = numberedText;
		_tempDir = tempDir;
		_wikitTerm = wikitTerm;
		numberedTextArea.setText(_numberedText);
		selectedAudio.getItems().clear();
		savedAudio.clear();
	}

	@FXML
	// Changes scene to main scene
	private void handleBackToMainView(ActionEvent event) {
		//removes the temp directory
		audioSentences.clear();
		Thread delDir = new Thread(new RemoveDirTask(_tempDir));
		delDir.start();
		ss.newScene("AppGUI.fxml", event);
	}



	@FXML
	private void handleNextButton(ActionEvent event) {
		combineAudioLoading.setVisible(true);
		CommandFactory command = new CommandFactory();
		//error handler for creation without any audio selected
		if (selectedAudio.getItems().size()==0) {
			Alert popup = new Alert(AlertType.INFORMATION);
			popup.setTitle("No audio selected");
			popup.setHeaderText("Please add some audio to create an creation");
			popup.show();
		}else {
			String order = "";
			//gets the order the audio files need to be combined in
			for(int i=0; i<selectedAudio.getItems().size();i++) {
				if (i==(selectedAudio.getItems().size() - 1)) {
					order = order +_tempDir +"/audio" + audioSentences.indexOf(selectedAudio.getItems().get(i)) + ".wav";
					savedAudio.add(selectedAudio.getItems().get(i));
					break;
				}
				order = order + _tempDir + "/audio" + audioSentences.indexOf(selectedAudio.getItems().get(i)) + ".wav ";
				savedAudio.add(selectedAudio.getItems().get(i));
			}

			String cmd = "sox "+order+" "+ _tempDir +"/audio.wav";
			savedText=numberedTextArea.getText();

			//combines the audio files in separate thread
			Thread create = new Thread(() -> {
				nextButton.setDisable(true);

				command.sendCommand("rm " +_tempDir + "/audio.wav" , false);
				command.sendCommand(cmd , false);
				command.sendCommand("echo \""+savedText+"\" > "+_tempDir+"/description.txt" , false);
				audioGenResult = command.sendCommand("echo $(soxi -D "+_tempDir+"/audio.wav)" , false);
				nextButton.setDisable(false);
				Platform.runLater(() -> {
					combineAudioLoading.setVisible(false);
					//changes scene when audio finished generating
					VideoCreationController videoCreationController = (VideoCreationController)ss.newScene("VideoCreationGUI.fxml", event);
					videoCreationController.passInfo(_wikitTerm, _tempDir, audioGenResult);
				});

			});
			create.setDaemon(true);
			create.start();
		}


	}


	@FXML
	private void handleSpeakPress(ActionEvent event) { 
		String sel = numberedTextArea.getSelectedText();
		String[] words = sel.split(" ");
		//error handling for when no text selected for speak or when too much text selected
		if (numberedTextArea.getSelectedText().isEmpty()) {
			Alert popup = new Alert(AlertType.INFORMATION);
			popup.setTitle("Select a chuck to add");
			popup.setHeaderText("No selected text detected. please select text to add");
			popup.show();
		}else if (words.length + 1> 40) {
			Alert popup = new Alert(AlertType.INFORMATION);
			popup.setTitle("Too many words selected");
			popup.setHeaderText("Please selected between 1-40 words");
			popup.show();
		}else {
			//removes symbols that might cause errors
			sel = sel.replace("\"", " ");
			sel = sel.replace("\n", " ");
			String cmd = "echo \"" + sel + "\" > selectedText.txt";
			//gets the voice for the audio
			String voice = getVoice();
			CommandFactory command = new CommandFactory();
			//creates and plays an audio file in separate thread
			Thread speak= new Thread(() -> {


				speakButton.setDisable(true);
				cancelButton.setDisable(true);
				previewButton.setDisable(true);
				nextButton.setDisable(true);


				command.sendCommand(cmd , false);
				//error handling in case selected text is not able to generate audio
				List<String> fileCreateCheck = command.sendCommand("text2wave -o "+ _tempDir +"/speakAudio.wav selectedText.txt " + voice 
						+ " && " + "file " + _tempDir +"/speakAudio.wav", false);
				if(fileCreateCheck.get(0).equals(_tempDir + "/speakAudio.wav: empty")) {
					Platform.runLater(() -> {
						Alert popup = new Alert(AlertType.INFORMATION);
						popup.setTitle("Voice Error");
						popup.setHeaderText("Voice cannot pronounce the selected text");
						popup.setContentText("Please try another voice or text selection");
						popup.show();
					});
				}else {
					command.sendCommand("aplay "+ _tempDir +"/speakAudio.wav" , false);
				}
				command.sendCommand("rm " + _tempDir +"/speakAudio.wav" , false);
				command.sendCommand("rm selectedText.txt" , false);



				if (selectedAudio.getItems().size() < 0) {
					previewButton.setDisable(false);
					nextButton.setDisable(false);
				}

				speakButton.setDisable(false);
				cancelButton.setDisable(false);

			});
			speak.setDaemon(true);
			speak.start();
		}
	}

	@FXML
	private void handleAddPress(ActionEvent event) { 
		String selected = numberedTextArea.getSelectedText();
		String[] words = selected.split(" ");
		//error handling for when no text selected for add or when too much text selected
		if (numberedTextArea.getSelectedText().isEmpty()) {
			Alert popup = new Alert(AlertType.INFORMATION);
			popup.setTitle("Select a chuck to add");
			popup.setHeaderText("No selected text detected. please select text to add");
			popup.show();
		} else if (words.length > 40) {
			Alert popup = new Alert(AlertType.INFORMATION);
			popup.setTitle("Too many words selected");
			popup.setHeaderText("Please selected between 1-40 words");
			popup.show();
		}else {

			//if selected text is same as a previous one adds white space to it to help distinguish 
			while (selectedAudio.getItems().contains(selected)) {
				selected = selected + " ";
			}
			//removes symbols that might cause problems
			selected = selected.replace("\"", " ");
			selected = selected.replace("\n", " ");
			//saves text to an array to keep track of audio files
			audioSentences.add(selected);
			String name = "audio" + count;
			count++;
			String cmd = "echo \"" + selected + "\" > selectedText.txt";
			String sel = selected;
			String voice = getVoice();
			CommandFactory command = new CommandFactory();
			//creates audio file of selected text in separate thread
			Thread add = new Thread(() -> {
				addButton.setDisable(true);

				command.sendCommand(cmd , false);
				List<String> fileCreateCheck = command.sendCommand("text2wave -o " + _tempDir + "/" + name + ".wav selectedText.txt " 
						+ voice + " && " + "file " + _tempDir +"/"+ name + ".wav" ,false);

				Platform.runLater(() ->{
					//error handler in case the selected text is not able to generate a audio file
					if(fileCreateCheck.get(0).equals(_tempDir + "/" + name+".wav: empty")) {

						Alert popup = new Alert(AlertType.INFORMATION);
						popup.setTitle("Voice Error");
						popup.setHeaderText("Voice cannot pronounce the selected text");
						popup.setContentText("Please try another voice or text selection");
						popup.show();
					}else {
						selectedAudio.getItems().add(sel);
						previewButton.setDisable(false);
						nextButton.setDisable(false);
						delButton.setDisable(false);
						upButton.setDisable(false);
						downButton.setDisable(false);
					}
				});

				command.sendCommand("rm selectedText.txt" , false);
				addButton.setDisable(false);

			});
			add.setDaemon(true);
			add.start();
		}
	}

	@FXML
	private void handleDelPress(ActionEvent event) { 
		//removes selected audio from the audio list
		String sel=selectedAudio.getSelectionModel().getSelectedItem();
		selectedAudio.getItems().remove(sel);
		if(selectedAudio.getItems().isEmpty()) {
			previewButton.setDisable(true);
			nextButton.setDisable(true);
			delButton.setDisable(true);
			upButton.setDisable(true);
			downButton.setDisable(true);
		}
	}

	@FXML
	private void upPress(ActionEvent event) { 
		int order = selectedAudio.getSelectionModel().getSelectedIndex();
		//Switches the selected audio with the one above if it exists
		if (order <= 0) {

		}else {
			String temp = selectedAudio.getItems().get(order - 1);
			selectedAudio.getItems().set(order - 1, selectedAudio.getSelectionModel().getSelectedItem());
			selectedAudio.getItems().set(order, temp);
			selectedAudio.getSelectionModel().select(order-1);
		}

	}

	@FXML
	private void downPress(ActionEvent event){ 
		int order = selectedAudio.getSelectionModel().getSelectedIndex();
		//Switches the selected audio with the one below if it exists
		if (order + 1 >= selectedAudio.getItems().size()) {

		}else {
			String temp = selectedAudio.getItems().get(order + 1);
			selectedAudio.getItems().set(order + 1, selectedAudio.getSelectionModel().getSelectedItem());
			selectedAudio.getItems().set(order, temp);
			selectedAudio.getSelectionModel().select(order+1);
		}
	}

	@FXML
	private void previewPress(ActionEvent event){ 
		CommandFactory command = new CommandFactory();
		//thread that plays all audio files in the audio list to preview creation
		Thread preview = new Thread(() -> {
			//disable buttons
			previewButton.setDisable(true);
			speakButton.setDisable(true);
			addButton.setDisable(true);
			nextButton.setDisable(true);
			delButton.setDisable(true);
			upButton.setDisable(true);
			downButton.setDisable(true);
			cancelButton.setDisable(true);
			//loops through all audio files and plays them
			for(int i=0; i<selectedAudio.getItems().size();i++) {
				String cmd = "aplay "+ _tempDir + "/audio" + audioSentences.indexOf(selectedAudio.getItems().get(i)) + ".wav";

				command.sendCommand(cmd , false);

			}
			//enable buttons 
			previewButton.setDisable(false);
			speakButton.setDisable(false);
			addButton.setDisable(false);
			nextButton.setDisable(false);
			delButton.setDisable(false);
			upButton.setDisable(false);
			downButton.setDisable(false);
			cancelButton.setDisable(false);
		});
		preview.setDaemon(true);
		preview.start();
	}

	private String getVoice() {
		//gets the voice selected and returns the code for that voice
		String selected = voiceSelect.getSelectionModel().getSelectedItem();
		if (selected.equals("Default Voice")) {
			return "-eval \"(voice_kal_diphone)\"";
		}else if (selected.equals("Akl_NZ(male) Voice")) {
			return "-eval \"(voice_akl_nz_jdt_diphone)\"";
		}else {
			return "-eval \"(voice_akl_nz_cw_cg_cg)\"";
		}
	}
}
