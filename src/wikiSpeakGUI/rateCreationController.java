package wikiSpeakGUI;

import java.io.File;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;

public class rateCreationController {
	
	@FXML private Button cancelButton;
	@FXML private Button rateB;
	@FXML private Slider rating;
	private static String creation;
	
	@FXML
	private void cancelPress(ActionEvent event) {
		Stage stage = (Stage) cancelButton.getScene().getWindow();
		stage.close();
	}
	
	public void passInfo(String nameOfCreation) {
		creation = nameOfCreation;

	}
	
	@FXML
	// Changes scene to main scene
	private void ratePress(ActionEvent event) {
		int rate = (int)rating.getValue();
		System.out.println(creation);
		CommandFactory command = new CommandFactory();
		command.sendCommand("echo \"" + rate +"/5\" > creations/metadata/"+creation+"/confidenceRating.txt", false);

		Stage stage = (Stage) cancelButton.getScene().getWindow();
		stage.close();
	}
	
}
