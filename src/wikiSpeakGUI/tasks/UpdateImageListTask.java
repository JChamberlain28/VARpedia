package wikiSpeakGUI.tasks;

import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import wikiSpeakGUI.CellPane;
import wikiSpeakGUI.CommandFactory;
import wikiSpeakGUI.TrackedImage;

public class UpdateImageListTask extends Task<Void>{


	private TableView<CellPane> _imageView;
	private String _tempDir;
	private TableColumn<CellPane, HBox> _colForUpdate;
	private List<String> _imageNameList;
	private Thread _threadToWaitOn;
	private AnchorPane _itemToHide;
	List<String> _imageNames;
	private BooleanBinding _submitBinder;
	private Button _submit;



	public UpdateImageListTask(Thread threadToWaitOn, AnchorPane itemToHide, TableView<CellPane> imageView,
			TableColumn<CellPane, HBox> colForUpdate, String tempDir, BooleanBinding submitBinder, Button submit) {

		_threadToWaitOn = threadToWaitOn;
		_itemToHide = itemToHide;
		_imageView = imageView;
		_tempDir = tempDir;
		_colForUpdate = colForUpdate;
		_submitBinder = submitBinder;
		_submit = submit;

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
				ObservableList<CellPane> imageItems = FXCollections.observableArrayList();

				for(String imageName : _imageNameList) {

					// empty file to get directory of javaFX application
					File file = new File("");

					Image ImageObject = new TrackedImage((file.toURI().toString()) + _tempDir + "/" + imageName);
					ImageView imageView = new ImageView(ImageObject);
					imageView.setFitHeight(200);
					imageView.setPreserveRatio(true);

					// instantiates custom layout pane class used for setting TableView cell value type
					CheckBox checkBox = new CheckBox();
					HBox hbox = new HBox(50, imageView, checkBox);
					
					
					// runs check to see if any image is selected upon changing state of a checkBox 
					// (for greying out submit button when no images selected)
					checkBox.setOnAction(new EventHandler<ActionEvent>() {
					    @Override public void handle(ActionEvent e) {
					    	boolean atLeastOneSel = false;
					    	
					    	for (CellPane cell : imageItems) {
					    		CheckBox currentCheckBox = (CheckBox) cell.getPane().getChildren().get(1);
					    		if (currentCheckBox.isSelected()) {
					    			atLeastOneSel = true;
					    		}
					    	}
					    	if (!atLeastOneSel) {
					        _submit.disableProperty().unbind();
					        _submit.setDisable(true);
					    	}
					        else {
					        	_submit.disableProperty().bind(_submitBinder);
					        }
					    }
					});
					
					hbox.setAlignment(Pos.CENTER);
					CellPane cell = new CellPane(hbox);

					
					imageItems.addAll(cell);



				}

				_colForUpdate.setCellValueFactory(new PropertyValueFactory<CellPane, HBox>("pane"));
				_imageView.setItems(imageItems);
				_itemToHide.setVisible(false);


			}

		});
	}

}
