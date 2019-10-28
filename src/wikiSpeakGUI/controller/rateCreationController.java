package wikiSpeakGUI.controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.stage.Stage;
import wikiSpeakGUI.CommandFactory;

public class rateCreationController {

	@FXML private Button cancelButton;
	@FXML private Button rateB;
	@FXML private Slider rating;
	private static String creation;

	@FXML
	private void initialize() {
		CommandFactory command = new CommandFactory();
		String output=command.sendCommand("cat \"creations/metadata/"+creation+"/confidenceRating.txt\"", false).get(0);
		int rate = Integer.parseInt(output.substring(0,1));
		rating.setValue(rate);
	}

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
		CommandFactory command = new CommandFactory();
		command.sendCommand("echo \"" + rate +"/5\" > \"creations/metadata/"+creation+"/confidenceRating.txt\"", false);

		Stage stage = (Stage) cancelButton.getScene().getWindow();
		stage.close();
	}

}
