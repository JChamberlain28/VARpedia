package wikiSpeakGUI.tasks;

import java.util.List;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import wikiSpeakGUI.CommandFactory;
import wikiSpeakGUI.Creation;
import wikiSpeakGUI.controller.AppGUIController;
import javafx.scene.control.Button;

public class UpdateCreationListTask extends Task<Void> {

	private ObservableList<Creation> creationList = FXCollections.observableArrayList();
	private ObservableList<Creation> generatingList = FXCollections.observableArrayList();

	private TableView<Creation> _listToUpdate;
	private String[] lines;
	private Button _delButton;
	private Button _playButton;
	private Text _creationNoText;
	private List<String> _currentlyGenerating;
	private TableColumn<Creation, String> nameCol;
	private TableColumn<Creation, String> ratingCol;
	private TableColumn<Creation, String> creationDateCol;
	private TableColumn<Creation, String> viewDateCol;
	private AppGUIController _controller;






	public UpdateCreationListTask(TableView<Creation> listToUpdate, TableColumn<Creation, String> col1, TableColumn<Creation, String> col2,
			TableColumn<Creation, String> col3, TableColumn<Creation, String> col4, Button delButton, Button playButton,
			Text creationNoText, List<String> currentlyGenerating, AppGUIController controller) {

		_listToUpdate = listToUpdate;
		_delButton = delButton;
		_playButton = playButton;
		_creationNoText = creationNoText;
		_currentlyGenerating = currentlyGenerating;
		_controller = controller;
		this.nameCol = col1;
		this.ratingCol = col2;
		this.creationDateCol = col3;
		this.viewDateCol = col4;

	}

	@Override
	protected Void call() {
		CommandFactory listFileCommand = new CommandFactory();



		// stores current creations in an array
		List<String> listFileResult = listFileCommand.sendCommand("./listCreations.sh", true);

		lines = listFileResult.get(0).split("\\r?\\n");

		return null;
	}


	@Override
	protected void done() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {

				// adds each creation to the TableView observable list

				for (String i : _currentlyGenerating) {
					Creation creation = new Creation(i + " (Unavailable)", "0/5", "", "");
					generatingList.add(creation);					
				}

				
				
				
				
				

				for (String i : lines) {
					// prevents (No creations currently exist) being a creation
					if (!(i.equals("(No creations currently exist)"))) {
						CommandFactory cf = new CommandFactory();
						
						
						List<String> ratingFileRead = cf.sendCommand("cat \"creations/metadata/" + i + "/confidenceRating.txt\"", false);

						
						List<String> dateFileRead = cf.sendCommand("cat \"creations/metadata/" + i + "/creationDate.txt\"", false);

						
						List<String> lastViewedRead = cf.sendCommand("cat \"creations/metadata/" + i + "/lastViewed.txt\"", false);

						
						
						Creation creation = new Creation(i, ratingFileRead.get(0), dateFileRead.get(0), lastViewedRead.get(0));
						creationList.add(creation);
						


					}


				}


				nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
				ratingCol.setCellValueFactory(new PropertyValueFactory<>("rating"));
				creationDateCol.setCellValueFactory(new PropertyValueFactory<>("creationDate"));
				viewDateCol.setCellValueFactory(new PropertyValueFactory<>("lastViewed"));

				creationList.addAll(generatingList);
				_listToUpdate.setItems(creationList);
				

				// update number of creations Text
				int noOfCreations;
				if (lines[0].equals("(No creations currently exist)")) {
					noOfCreations = 0;
				}
				else {
					noOfCreations = lines.length;
				}
				_creationNoText.setText("Number of creations: " + noOfCreations);

				// auto selects the first item in the table (default selection)
				if (creationList.size() != 0) {
					_listToUpdate.getSelectionModel().select(0);
					
					_delButton.setDisable(false);
					_playButton.setDisable(false);

				}
				// disable play or delete button when no creations exist
				else {
					_delButton.setDisable(true);
					_playButton.setDisable(true);
				}

				// manually triggers sortBy handler to sort the newly updated list
				_controller.sortByChange(new Event(null));
			}

		});

	}

}
