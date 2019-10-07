package wikiSpeakGUI;


import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.CodeSource;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.stage.*;


public class AppGUI extends Application{


	static String _jarDir;


	public static void main(String[] args) {

		// get directory of jar
		CodeSource codeSource = AppGUI.class.getProtectionDomain().getCodeSource();
		File runnableJar = null;

		try {
			runnableJar = new File(codeSource.getLocation().toURI().getPath());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		_jarDir = runnableJar.getParentFile().getPath();

		CommandFactory command = new CommandFactory();

		// makes bash scripts executable (in case their permissions are incorrect)
		command.sendCommand("chmod +x *.sh", false);





		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {

		CommandFactory command = new CommandFactory();

		command.sendCommand("mkdir -p creations" , false);

		primaryStage.setTitle("VARpedia");

		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("AppGUI.fxml"));

		Parent layout = null;
		try {
			layout = loader.load();
		} catch (IOException e2) {

			e2.printStackTrace();
		}

		Scene scene = new Scene(layout);
		primaryStage.setScene(scene);
		primaryStage.show();




		// remove .description.txt upon closing application to prevent left over files
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent e) {

				command.sendCommand("rm -f .description.txt", false);
				Platform.exit();
			}
		});


	}




}






























