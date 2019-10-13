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
	
	@FXML private Button cancelB;
	@FXML private Button searchB;
	@FXML private ListView<String> favourites;
	private static AppGUIController parent;
	
	public void initialize(){
		CommandFactory command = new CommandFactory();
		List<String> lol = command.sendCommand("cat favourites.txt" , false);
		String[] split = lol.get(0).split(" ");
		favourites.getItems().addAll(split);
		favourites.getSelectionModel().selectFirst();
		
	}
	
	public void setParent(AppGUIController p){
		parent = p;
	}
	
	@FXML
	private void cancelPress(ActionEvent event) {
		Stage stage = (Stage) cancelB.getScene().getWindow();
		stage.close();
	}
	
	
	@FXML
	// Changes scene to main scene
	private void searchPress(ActionEvent event) {
		Stage stage = (Stage) cancelB.getScene().getWindow();
		stage.close();
		parent.Search(favourites.getSelectionModel().getSelectedItem());
	}
	
}
