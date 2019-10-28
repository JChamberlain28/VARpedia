package wikiSpeakGUI.controller;

import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import wikiSpeakGUI.CommandFactory;

public class favSelectionController {


	@FXML private Button searchB;
	@FXML private Button delB;
	@FXML private Button cancelB;
	@FXML private ListView<String> favourites;
	private static AppGUIController parent;

	/**
	 * Formats the list view and adds the favourite terms to it as soon as the 
	 * favourite window is launched 
	 */
	public void initialize(){
		//formats the list view
		favourites.setStyle("-fx-control-inner-background: rgb(049,055,060); "
				+ "-fx-text-fill: rgb(255,255,255); -fx-focus-color: rgb(255,255,255);");

		//gets the favourite search terms
		CommandFactory command = new CommandFactory();
		List<String> output = command.sendCommand("cat favourites.txt" , false);
		String[] split = output.get(0).split("_");
		
		//adds the terms to the list view
		favourites.getItems().addAll(split);
		favourites.getSelectionModel().selectFirst();
		if (output.get(0).equals("")){
			searchB.setDisable(true);
			delB.setDisable(true);
		}

	}

	/**
	 * setter for the reference to the instance of the object this was created by
	 * @param p
	 */
	public void setParent(AppGUIController p){
		parent = p;
	}

	/**
	 * deletes the selected term from the list view and updates the file that stores the 
	 * favourite terms
	 * @param event
	 */
	@FXML
	private void delPress(ActionEvent event) {
		//removes the selected term
		favourites.getItems().remove(favourites.getSelectionModel().getSelectedIndex());
		if (favourites.getItems().size() == 0){
			searchB.setDisable(true);
			delB.setDisable(true);
		}
		
		//updates the favourite file
		CommandFactory command = new CommandFactory();
		command.sendCommand("rm favourites.txt" , false);
		command.sendCommand("touch favourites.txt" , false);
		for(int i=0; i< favourites.getItems().size();i++) {
			command.sendCommand("echo \""+ favourites.getItems().get(i)+"_\" >> favourites.txt", false);
		}

	}

	/**
	 * closes the favourites window and searches for term selected when search is pressed
	 * @param event
	 */
	@FXML
	private void searchPress(ActionEvent event) {
		//closes the favourites window
		Stage stage = (Stage) searchB.getScene().getWindow();
		stage.close();
		// calls the search method
		parent.Search(favourites.getSelectionModel().getSelectedItem());
	}

	/**
	 * closes the favourites window when cancel is pressed
	 * @param event
	 */
	@FXML
	private void cancelPress(ActionEvent event) {
		Stage stage = (Stage) searchB.getScene().getWindow();
		stage.close();
	}

}
