package wikiSpeakGUI;

import java.io.File;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;

public class favSelectionController {
	

	@FXML private Button searchB;
	@FXML private Button delB;
	@FXML private ListView<String> favourites;
	private static AppGUIController parent;
	
	public void initialize(){
		CommandFactory command = new CommandFactory();
		List<String> output = command.sendCommand("cat favourites.txt" , false);
		String[] split = output.get(0).split(" ");
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
			command.sendCommand("echo \""+ favourites.getItems().get(i)+" \" >> favourites.txt", false);
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
