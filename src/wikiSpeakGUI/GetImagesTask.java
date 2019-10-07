package wikiSpeakGUI;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import api.FlickrAPI;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class GetImagesTask extends Task<Void>{
	
	private String _wikitTerm;
	private Button _submitCreationButton;
	private String _noOfImages;
	private TableView<CellImage> _imageView;
	private String _tempDir;
	private TableColumn<CellImage, ImageView> _colForUpdate;
	private List<String> _imageNameList = null;
	private ImageView _loadingIcon;
	private Button _imageNoButton;

	
	
	public GetImagesTask(String wikitTerm, Button submitCreationButton, String noOfImages, TableView<CellImage> imageView,
			TableColumn<CellImage, ImageView> colForUpdate, String tempDir, ImageView loadingIcon, Button imageNoButton) {
		_wikitTerm = wikitTerm;
		_submitCreationButton = submitCreationButton;
		_noOfImages = noOfImages;
		_imageView = imageView;
		_tempDir = tempDir;
		_colForUpdate = colForUpdate;
		_loadingIcon = loadingIcon;
		_imageNoButton = imageNoButton;
		
	}

	@Override
	protected Void call() {
		CommandFactory command = new CommandFactory();
		command.sendCommand("rm -f ./" + _tempDir + "/*.jpg", false);
		
		// will need loading animation for this ####
		FlickrAPI.downloadImages(_wikitTerm, _tempDir, Integer.parseInt(_noOfImages));
		
		
		// create list of images to update TableView with
		List<String> imageNames = command.sendCommand("ls " + _tempDir + " | grep .*\\.jpg", true);
		_imageNameList = new ArrayList<String>(Arrays.asList(imageNames.get(0).split("\n")));

		return null;
	}
	
	// instruct JavaFX GUI thread to populate TableView with images
	@Override
	protected void done() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				ObservableList<CellImage> imageItems = FXCollections.observableArrayList();
				
				for(String imageName : _imageNameList) {
					
					// empty file to get directory of javaFX application
					File file = new File("");
					
					Image ImageObject = new Image((file.toURI().toString()) + _tempDir + "/" + imageName);
					ImageView imageView = new ImageView(ImageObject);
					imageView.setFitHeight(180);
					imageView.setPreserveRatio(true);
					
					// instantiates custom image class used for setting TableView cell value type
					CellImage cell = new CellImage(imageView);
					imageItems.addAll(cell);

					
				}
				
				_colForUpdate.setCellValueFactory(new PropertyValueFactory<CellImage, ImageView>("image"));
				_imageView.setItems(imageItems);
				_submitCreationButton.setDisable(false);
				_loadingIcon.setVisible(false);
				_imageNoButton.setDisable(false);
				
			}
			
		});
	}

}
