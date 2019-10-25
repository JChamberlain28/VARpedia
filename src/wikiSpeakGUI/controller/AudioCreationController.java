package wikiSpeakGUI.controller;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import wikiSpeakGUI.CommandFactory;
import wikiSpeakGUI.SceneSwitcher;
import wikiSpeakGUI.tasks.GetImagesTask;
import wikiSpeakGUI.tasks.RemoveDirTask;
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
	private Thread _threadToPassToVidController = null;




	@FXML
	private Button cancelButton;
	
	@FXML
	private CheckBox music;

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
	private Thread getTermImages;
	private CommandFactory speakCmd= new CommandFactory();

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
	public void passInfo(String numberedText, String tempDir, String wikitTerm, Thread thread) {
		_numberedText = numberedText;
		_tempDir = tempDir;
		_wikitTerm = wikitTerm;
		_threadToPassToVidController = thread;
		
		passInfoDependents();

	}

	@FXML
	// Changes scene to main scene
	private void handleBackToMainView(ActionEvent event) {
		
		// stop method deprecated,
		// however have not found effective alternative for halting image download
		// without throwing unwanted file not found exception.
		getTermImages.stop();
		speakCmd.killCurrentProcess();
		
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
				if (music.isSelected()) {
					double length=Double.valueOf(command.sendCommand("echo $(soxi -D tempMusic0.wav)" , false).get(0));
					int counter=0;
					while (Double.valueOf(audioGenResult.get(0))>length) {
						command.sendCommand("sox tempMusic"+counter+".wav tempMusic"+counter+".wav tempMusic"+(counter+1)+".wav", false);
						if(counter != 0){
							command.sendCommand("rm tempMusic"+counter+".wav" , false);
						}
						counter++;
						length=Double.valueOf(command.sendCommand("echo $(soxi -D tempMusic"+counter+".wav)" , false).get(0));
					}
					command.sendCommand("sox tempMusic"+counter+".wav "+_tempDir +"/finalMusic.wav trim 0 "+ audioGenResult.get(0), false);
					if(counter != 0){
						command.sendCommand("rm tempMusic"+counter+".wav" , false);
					}
					command.sendCommand("sox -M " + _tempDir + "/finalMusic.wav " +_tempDir + "/audio.wav " +_tempDir +"/withMusic.wav", false);
					command.sendCommand("rm "+ _tempDir +"/finalMusic.wav" , false);
					command.sendCommand("sox "+_tempDir + "/withMusic.wav -c 1 "+_tempDir + "/audioLow.wav ", false);
					command.sendCommand("rm "+ _tempDir +"/audio.wav" , false);
					command.sendCommand("sox "+_tempDir + "/audioLow.wav "+_tempDir + "/audio.wav vol 10 dB 2>/dev/null", false);
				}
				
				nextButton.setDisable(false);
				Platform.runLater(() -> {
					combineAudioLoading.setVisible(false);
					//changes scene when audio finished generating
					VideoCreationController videoCreationController = (VideoCreationController)ss.newScene("VideoCreationGUI.fxml", event);
					videoCreationController.passInfo(_wikitTerm, _tempDir, audioGenResult, _threadToPassToVidController);
				});

			});
			create.setDaemon(true);
			create.start();
		}


	}


	@FXML
	private void handleSpeakPress(ActionEvent event) { 
		if(speakButton.getText().equals("Speak")) {
			String sel = numberedTextArea.getSelectedText();
			String[] words = sel.split(" ");
			//error handling for when no text selected for speak or when too much text selected
			if (numberedTextArea.getSelectedText().isEmpty()) {
				Alert popup = new Alert(AlertType.INFORMATION);
				popup.setTitle("Select a chuck to add");
				popup.setHeaderText("No selected text detected. Please highlight some text");
				popup.show();
			}else if (words.length + 1> 40) {
				Alert popup = new Alert(AlertType.INFORMATION);
				popup.setTitle("Too many words selected");
				popup.setHeaderText("Please selected between 1-40 words");
				popup.show();
			}else {
				speakButton.setDisable(true);
				//removes symbols that might cause errors
				sel = sel.replace("\"", " ");
				sel = sel.replace("\n", " ");
				String cmd = "echo \"" + sel + "\" > selectedText.txt";
				//gets the voice for the audio
				String voice = getVoice();
				CommandFactory command = new CommandFactory();
				//creates and plays an audio file in separate thread
				Thread speak= new Thread(() -> {
					previewButton.setDisable(true);
					nextButton.setDisable(true);
					cancelButton.setDisable(true);


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
						Platform.runLater(()-> {
							speakButton.setDisable(false);
							cancelButton.setDisable(false);
							speakButton.setText("Stop");
						});
						speakCmd.sendCommand("aplay "+ _tempDir +"/speakAudio.wav" , false);
					}
					speakCmd.sendCommand("rm " + _tempDir +"/speakAudio.wav" , false);
					speakCmd.sendCommand("rm selectedText.txt" , false);



					if (selectedAudio.getItems().size() > 0) {
						previewButton.setDisable(false);
						nextButton.setDisable(false);
					}


				});
				speak.setDaemon(true);
				speak.start();
			}
		}else {
			speakCmd.killCurrentProcess();
			speakButton.setText("Speak");
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
						selectedAudio.getSelectionModel().selectFirst();
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
		
		//thread that plays all audio files in the audio list to preview creation
		if(previewButton.getText().equals("Preview")) {
			Thread preview = new Thread(() -> {
				//disable buttons
				previewButton.setDisable(true);
				speakButton.setDisable(true);
				cancelButton.setDisable(true);
				addButton.setDisable(true);
				nextButton.setDisable(true);
				delButton.setDisable(true);
				upButton.setDisable(true);
				downButton.setDisable(true);
				//loops through all audio files and plays them
				for(int i=0; i<selectedAudio.getItems().size();i++) {
					String cmd = "aplay "+ _tempDir + "/audio" + audioSentences.indexOf(selectedAudio.getItems().get(i)) + ".wav";
					Platform.runLater(() -> {
						previewButton.setDisable(false);
						cancelButton.setDisable(false);
						previewButton.setText("Stop");
					});
					speakCmd.sendCommand(cmd , false);

				}
				//enable buttons 
				speakButton.setDisable(false);
				addButton.setDisable(false);
				nextButton.setDisable(false);
				delButton.setDisable(false);
				upButton.setDisable(false);
				downButton.setDisable(false);
			});
			preview.setDaemon(true);
			preview.start();
		}else {
			speakCmd.killCurrentProcess();
			previewButton.setText("Preview");
		}
		
	}

	private String getVoice() {
		//gets the voice selected and returns the code for that voice
		String selected = voiceSelect.getSelectionModel().getSelectedItem();
		if (selected.equals("Default Voice")) {
			return "-eval \"(voice_kal_diphone)\"";
		}else {
			return "-eval \"(voice_akl_nz_jdt_diphone)\"";
		}
	}
	
	
	
	// calls code dependent on variables set in passInfo. Therefore run
	// once these variables are available.
	private void passInfoDependents() {
		numberedTextArea.setText(_numberedText);
		selectedAudio.getItems().clear();
		audioSentences.clear();
		savedAudio.clear();
		count=0;
		
		// start downloading images for creation in background
		getTermImages = new Thread(new GetImagesTask(_wikitTerm, _tempDir));
		getTermImages.start();
	}
}