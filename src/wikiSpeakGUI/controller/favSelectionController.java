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
	@FXML private ListView<String> favourites;
	private static AppGUIController parent;
	
	public void initialize(){
		
		favourites.setStyle("-fx-control-inner-background: rgb(049,055,060); "
				+ "-fx-text-fill: rgb(255,255,255); -fx-focus-color: rgb(255,255,255);");
		
		
		CommandFactory command = new CommandFactory();
		List<String> output = command.sendCommand("cat favourites.txt" , false);
		String[] split = output.get(0).split("_");
		favourites.getItems().addAll(split);
		favourites.getSelectionModel().selectFirst();
		if (output.get(0).equals("")){
			searchB.setDisable(true);
			delB.setDisable(true);
		}
		
	}
	
	public void setParent(AppGUIController p){
		parent = p;
	}
	

	
	@FXML
	private void delPress(ActionEvent event) {
		favourites.getItems().remove(favourites.getSelectionModel().getSelectedIndex());
		if (favourites.getItems().size() == 0){
			searchB.setDisable(true);
			delB.setDisable(true);
		}
		CommandFactory command = new CommandFactory();
		command.sendCommand("rm favourites.txt" , false);
		command.sendCommand("touch favourites.txt" , false);
		for(int i=0; i< favourites.getItems().size();i++) {
			command.sendCommand("echo \""+ favourites.getItems().get(i)+"_\" >> favourites.txt", false);
		}
		
	}
	
	@FXML
	// Changes scene to main scene
	private void searchPress(ActionEvent event) {
		Stage stage = (Stage) searchB.getScene().getWindow();
		stage.close();
		parent.Search(favourites.getSelectionModel().getSelectedItem());
	}
	
}
