package wikiSpeakGUI;

import java.io.IOException;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneSwitcher {

	
	public SceneSwitcher() {
		
	}
	
	
	public Object newScene(String FXML, Event event) {
		
		// type object used as many different object types can be controllers.
		// Casting is used outside this method only when the controller type is known.
		Object controller=null;
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(FXML));
			Parent ViewParent;
			ViewParent = loader.load();
			controller = loader.getController();
			Scene ViewScene = new Scene(ViewParent);
			Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
			window.setScene(ViewScene);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return controller;
	}
}
