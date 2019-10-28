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

	/**
	 * sets the value of the slider to what it had been previously set to 
	 */
	@FXML
	private void initialize() {
		//gets the rating for the creation and sets the slider to it
		CommandFactory command = new CommandFactory();
		String output=command.sendCommand("cat \"creations/metadata/"+creation+"/confidenceRating.txt\"", false).get(0);
		int rate = Integer.parseInt(output.substring(0,1));
		rating.setValue(rate);
	}

	/**
	 * Closes the rating window when cancel pressed
	 * @param event
	 */
	@FXML
	private void cancelPress(ActionEvent event) {
		Stage stage = (Stage) cancelButton.getScene().getWindow();
		stage.close();
	}
	
	/**
	 * setter used to get the name of the creation
	 * @param nameOfCreation
	 */
	public void passInfo(String nameOfCreation) {
		creation = nameOfCreation;

	}

	/**
	 * Stores the value of the slider in its meta data file and closes the rating window
	 * @param event
	 */
	@FXML
	private void ratePress(ActionEvent event) {
		//gets the value of the slider and updates the value of the meta data file for the creation
		int rate = (int)rating.getValue();
		CommandFactory command = new CommandFactory();
		command.sendCommand("echo \"" + rate +"/5\" > \"creations/metadata/"+creation+"/confidenceRating.txt\"", false);

		//closes the window
		Stage stage = (Stage) cancelButton.getScene().getWindow();
		stage.close();
	}

}
