package wikiSpeakGUI;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class UpdateImageListTask extends Task<Void>{


	private TableView<CellImage> _imageView;
	private String _tempDir;
	private TableColumn<CellImage, ImageView> _colForUpdate;
	private List<String> _imageNameList;
	private Thread _threadToWaitOn;
	private AnchorPane _itemToHide;
	List<String> _imageNames;



	public UpdateImageListTask(Thread threadToWaitOn, AnchorPane itemToHide, TableView<CellImage> imageView,
			TableColumn<CellImage, ImageView> colForUpdate, String tempDir) {

		_threadToWaitOn = threadToWaitOn;
		_itemToHide = itemToHide;
		_imageView = imageView;
		_tempDir = tempDir;
		_colForUpdate = colForUpdate;

	}

	@Override
	protected Void call() {


		CommandFactory command = new CommandFactory();
		
		// waits until download images thread has finished (as it needs the images to populate TableView)
		if (_threadToWaitOn != null) {
			while (_threadToWaitOn.isAlive()) {

			}
		}


		// create list of images to update TableView with
		_imageNames = command.sendCommand("ls " + _tempDir + " | grep .*\\.jpg", true);
		_imageNameList = Arrays.asList(_imageNames.get(0).split("\n"));
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
				_itemToHide.setVisible(false);


			}

		});
	}

}
