package wikiSpeakGUI.controller;

import java.io.File;
import java.io.IOException;

import wikiSpeakGUI.Main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.media.MediaPlayer.Status;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import wikiSpeakGUI.SceneSwitcher;

public class PlayController {
	
	private static String creation;
	private static String _filePath ="";
	File fileUrl = new File(_filePath).getAbsoluteFile(); 
	@FXML private Button playPauseB;
	@FXML private Button forwardB;
	@FXML private Button backwardsB;
	@FXML private Button rateButton;
	@FXML private MediaView mv;
	@FXML private Button backToMenu;
	private MediaPlayer mp;
	private Media media;
	SceneSwitcher ss = new SceneSwitcher();
	
	public void initialize(){
		playPauseB.setMinWidth(40);
		start();
		
	}
	
	public void start(){
		media= new Media(fileUrl.toURI().toString());
		mp= new MediaPlayer(media);
		mv.setMediaPlayer(mp);
		mp.setAutoPlay(true);
		mp.setVolume(100);
		
		
		
		// return media to start once played
		mp.setOnEndOfMedia(new Runnable() {
		    @Override
		    public void run() {
		    	mp.seek(Duration.ZERO);
				playPauseB.setText("▶");
				mp.pause();
		    }
		});
	}
	
	@FXML
	private void pausePress(ActionEvent event) {
		if (mp.getStatus() == Status.PLAYING) {
			playPauseB.setText("▶");
			mp.pause();
		} else {
			playPauseB.setText("❚❚");
			mp.play();
		}
	}
	
	@FXML
	private void forwardPress(ActionEvent event) {
		mp.seek( mp.getCurrentTime().add( Duration.seconds(3)) );
	}
	
	@FXML
	private void backwardsPress(ActionEvent event) {
		mp.seek( mp.getCurrentTime().add( Duration.seconds(-3)) );
	}
	
	// pass the selected creation from the AppGUIController to the PlayController
	public void passInfo(String nameOfCreation) {
		_filePath = "creations/"+ nameOfCreation + ".mp4"; 
		creation = nameOfCreation;

	}
	
	
	@FXML
	// Changes scene to main scene
	private void handleBackToMainView(ActionEvent event) {
		mp.stop();
		ss.newScene("AppGUI.fxml", event);
	}
	
	@FXML
	// Changes scene to main scene
	private void ratePressed(ActionEvent event) {
		try {
			rateCreationController rate = new rateCreationController();
			rate.passInfo(creation);
			FXMLLoader fxmlloader = new FXMLLoader(Main.class.getResource("/wikiSpeakGUI/view/rateCreation.fxml"));
			Parent root = (Parent)fxmlloader.load();
			Stage stage = new Stage();
			stage.setScene(new Scene(root));
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.show();
			stage.setResizable(false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
