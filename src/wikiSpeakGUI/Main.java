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



public class Main extends Application{


	static String _jarDir;

	/*
	 *@param args  - arguments that the JVM can pass in (String[])
	 *
	 *@return void
	 * 
	 * This method obtains the current working directory of the jar file (assuming this
	 * this is packaged into a runnable jar) to prevent this program looking in another default
	 * location on the users system. It also ensures all bash scritps are executable
	 */
	public static void main(String[] args) {

		// get directory of jar
		CodeSource codeSource = Main.class.getProtectionDomain().getCodeSource();
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
		command.sendCommand("mkdir -p creations/metadata" , false);

		primaryStage.setTitle("VARpedia");

		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("view/AppGUI.fxml"));
		Parent layout = null;
		try {
			layout = loader.load();
		} catch (IOException e2) {

			e2.printStackTrace();
		}
		Scene scene = new Scene(layout);
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setResizable(false);


		// removes .description.txt upon closing application to prevent left over files
		// it also removes all temporary creation folders and destroys any bash pricesses still
		// running.
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent e) {
				command.sendCommand("rm -r TempCreation-*" , false);
				command.sendCommand("rm -f .description.txt", false);
				for( Process p : CommandFactory.pastProcesses) {
					p.destroy();
				}
				Platform.exit();
				System.exit(0);
			}
		});


	}





}






























