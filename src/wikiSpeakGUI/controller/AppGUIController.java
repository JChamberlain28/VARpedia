package wikiSpeakGUI.controller;

import java.io.IOException;
import wikiSpeakGUI.Main;
import java.text.DateFormat;

import java.text.ParseException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import wikiSpeakGUI.CommandFactory;
import wikiSpeakGUI.Creation;
import wikiSpeakGUI.SceneSwitcher;
import wikiSpeakGUI.tasks.UpdateCreationListTask;
import wikiSpeakGUI.tasks.WikitSearchTask;

public class AppGUIController {


	private SceneSwitcher ss = new SceneSwitcher();
	private String searchTerm = null;
	private BooleanBinding bb = null;
	private Thread GetTermImages = null;
	CommandFactory cf = new CommandFactory();



	// create section widgets
	@FXML
	private Button continueButton;

	@FXML
	private Button favButton;

	@FXML
	private CheckBox addFav;

	@FXML
	private Button wikitButton;

	@FXML
	private TextField wikitInput;

	@FXML
	private TextArea wikitResult;


	@FXML
	private TableView<Creation> creationList;

	@FXML
	private TableColumn<Creation, String> name;

	@FXML
	private TableColumn<Creation, String> confidenceRating;

	@FXML
	private TableColumn<Creation, String> creationDate;

	@FXML
	private TableColumn<Creation, String> lastViewed;


	@FXML
	private Button playButton;


	@FXML
	private Button deleteButton;




	@FXML
	private Text creationNoText;

	@FXML
	private ImageView wikitLoading;

	@FXML
	private Text notViewedWarning;

	@FXML
	private AnchorPane notViewedKey;

	@FXML
	private Text fiveDayWarning;

	@FXML
	private AnchorPane fiveDayKey;

	@FXML
	private ComboBox<String> sortBy;





	@FXML
	private void initialize() {

		wikitLoading.setVisible(false);
		wikitButton.setDisable(false);
		continueButton.setDisable(true);
		wikitResult.setWrapText(true);
		wikitResult.setEditable(false);

		sortBy.setStyle("-fx-background-color: rgb(049,055,060); -fx-control-inner-background: rgb(049,055,060); "
				+ "-fx-text-fill: rgb(255,255,255); -fx-focus-color: rgb(255,255,255);");
		sortBy.getItems().addAll("Date Viewed", "Confidence");
		sortBy.getSelectionModel().select(0);
		sortByChange(new Event(null));

		wikitResult.setStyle("-fx-control-inner-background: rgb(049,055,060); "
				+ "-fx-text-fill: rgb(255,255,255); -fx-focus-color: rgb(255,255,255);");
		wikitInput.setStyle("-fx-control-inner-background: rgb(049,055,060);"
				+ " -fx-text-fill: rgb(255,255,255); -fx-focus-color: rgb(255,255,255);");
		creationList.setStyle("-fx-text-fill: white; -fx-control-inner-background: rgb(049,055,060); -fx-focus-color: rgb(255,255,255);");
		creationList.setPlaceholder(new Label("No creations to display, click create to start"));


		updateCreationList();

		// block characters that are not accepted by wikit command
		wikitInput.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.matches("[^\\\\&;\'\"]*")) {
					wikitInput.setText(newValue.replaceAll("[\\\\&;\'\"]", ""));
				}
			}
		});




		// set colouring of rows in creation table depending on if viewed or how long ago viewed
		// also controls what warnings are displayed regarding the reminder system
		creationList.setRowFactory(tv -> new TableRow<Creation>() {
			@Override
			protected void updateItem(Creation item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null || item.getLastViewed() == "" || item.getCreationDate() == "") {
					setStyle("-fx-control-inner-background: rgb(049,055,060); "
							+ " -fx-focus-color: rgb(255,255,255);");
				}
				else if (item.getLastViewed().equals("Never Viewed")) {
					// not viewed warning visible
					notViewedWarning.setVisible(true);
					notViewedKey.setVisible(true);
					setStyle("-fx-control-inner-background: rgb(180, 115, 000); -fx-selection-bar: orange; -fx-selection-bar-non-focused: orange;");
				}
				else {
					// check if date between last viewing and now is more than 5 days
					SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
					Date viewedDate = null;
					Date currentDate = new Date();
					try {
						viewedDate = format.parse(item.getLastViewed());
					} catch (ParseException e) {

						e.printStackTrace();
					}

					long timeElapsed = currentDate.getTime() - viewedDate.getTime();
					long daysElapsed = timeElapsed / (24 * 60 * 60 * 1000);

					// highlight creations red that havent been viewed in 5 days
					if (daysElapsed >= 5) {
						// five day warning visible
						fiveDayWarning.setVisible(true);
						fiveDayKey.setVisible(true);

						setStyle("-fx-control-inner-background: rgb(180, 000, 000); -fx-selection-bar: red; -fx-selection-bar-non-focused: red;");
					}
					else {
						setStyle("-fx-control-inner-background: rgb(049,055,060); "
								+ " -fx-focus-color: rgb(255,255,255);");
					}

				}


			}
		});






		bb = new BooleanBinding() {
			{
				super.bind(wikitInput.textProperty());
			}

			@Override
			protected boolean computeValue() {
				return ((wikitInput.getText().trim().isEmpty()));
			}
		};



		// edits default sorting so Never Viewed is always highest on the list
		// for sort by date viewed
		lastViewed.setComparator((date1, date2) -> {

			int result = 0;
			if (date1.equals("Never Viewed")) {
				result = -1;
			}
			else if (date2.equals("Never Viewed")) {
				result = 1;
			} else {
				result = date1.compareTo(date2);
			}
			return result;
		});

		wikitButton.disableProperty().bind(bb);

	}



	// event handling



	// allows user to change the sort order of creations via a ComboBox
	@FXML
	public void sortByChange(Event event) {
		if (creationList.getItems().size() != 0) {
			if (sortBy.getSelectionModel().getSelectedItem().toString().equals("Date Viewed")) {
				creationList.getSortOrder().clear();
				creationList.getSortOrder().add(lastViewed);
			} else {
				creationList.getSortOrder().clear();
				creationList.getSortOrder().add(confidenceRating);
			}

			creationList.sort();
		}
	}





	// sets TableView to default selection when triggered (first item)
	@FXML
	private void creationListDefaultSelect(Event event) {
		creationList.getSelectionModel().select(0);

	}





	// Changes scene to create scene
	@FXML
	private void handleContinueButton(ActionEvent event) {





		// make temp directory
		List<String> mktempResult = null;
		List<String> descriptionOutput = null;

		mktempResult = cf.sendCommand("mktemp -d TempCreation-XXXXX", false);


		String tempFolder = mktempResult.get(0);


		// process description so each sentence is on a new line.
		cf.sendCommand("cat .description.txt " + " | sed 's/\\([.!?]\\) \\([[:upper:]]\\)/\\1\\n\\2/g' > " + String.format("%s/description.txt ", tempFolder), false);



		// send command 2nd parameter determines if each array item (sentence) should be separated by a new line
		descriptionOutput = cf.sendCommand("cat " +  String.format("%s/description.txt ", tempFolder), true);

		// switch scene to create view (casting to create controller as type of object known)
		AudioCreationController createController = (AudioCreationController)ss.newScene("AudioCreationGUI.fxml", event);

		// pass formatted description to be displayed in create view
		createController.passInfo(descriptionOutput.get(0), tempFolder, searchTerm, GetTermImages);


		// adds search term to favorites list if selected
		if(addFav.isSelected()) {
			CommandFactory command = new CommandFactory();
			List<String> output = command.sendCommand("cat favourites.txt | grep "+wikitInput.getText() +"_", false);
			if(output.get(0).equals(wikitInput.getText()+"_")) {

			}else {
				command.sendCommand("echo \""+wikitInput.getText() +"_\" >> favourites.txt", false);
			}
		}
	}






	@FXML
	private void handleFavSearch(ActionEvent event) { 
		try {

			FXMLLoader fxmlloader = new FXMLLoader(Main.class.getResource("/wikiSpeakGUI/view/favSelection.fxml"));
			Parent root = fxmlloader.load();

			favSelectionController controller = (favSelectionController) fxmlloader.getController();
			controller.setParent(this);

			Stage stage = new Stage();
			stage.setScene(new Scene(root));
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.show();
			stage.setResizable(false);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}


	// is called from favorites screen to search the selected favorites term
	public void Search(String search) { 
		wikitInput.setText(search);
		wikitButton.fire();
	}

	@FXML
	private void handleWikiSearch(ActionEvent event) { 

		wikitLoading.setVisible(true);
		searchTerm = wikitInput.getText();
		wikitButton.disableProperty().unbind();
		wikitButton.setDisable(true);
		favButton.setDisable(true);
		Thread wikiSearchThread = new Thread(new WikitSearchTask(wikitButton, continueButton, favButton, searchTerm, wikitResult, wikitLoading, bb));
		wikiSearchThread.start();

	}


	@FXML
	private void handlePlayButton(Event event) {


		// get selected creation name to play
		String selection = creationList.getSelectionModel().getSelectedItem().getName();

		if (selection.contains(" (Unavailable)")){
			Alert popup = new Alert(AlertType.INFORMATION);
			popup.setTitle("Creation Unavailable");
			popup.setHeaderText("Creation currently genarating");

			setBigFont(popup);

			popup.show();
		} 
		else {
			// selection field is static in PlayController
			PlayController pc = new PlayController();
			pc.passInfo(selection);

			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			Date date = new Date();

			// store creation date
			cf.sendCommand("echo " + dateFormat.format(date) + "> \"creations/metadata/" + selection + "/lastViewed.txt\"", false);

			// switch to play scene
			ss.newScene("PlayGUI.fxml",event);
		}


	}





	@FXML
	private void handleDeleteButton(Event event) { 

		// get selected creation name to delete
		String selection = creationList.getSelectionModel().getSelectedItem().getName();

		if (selection.contains(" (Unavailable)")){
			Alert popup = new Alert(AlertType.INFORMATION);
			popup.setTitle("Creation Unavailable");
			popup.setHeaderText("Creation currently genarating");

			setBigFont(popup);

			popup.show();
		} 
		else {
			deleteButton.setDisable(true);
			playButton.setDisable(true);


			// deletion confirmation dialog
			Alert popup = new Alert(AlertType.CONFIRMATION);
			popup.setTitle("Delete Confirmation");
			popup.setHeaderText("Are you sure you want to delete " + selection);

			ButtonType buttonTypeYes = new ButtonType("Yes");
			ButtonType buttonTypeNo = new ButtonType("No", ButtonData.CANCEL_CLOSE);

			popup.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

			setBigFont(popup);

			Optional<ButtonType> result = popup.showAndWait();
			if (result.get() == buttonTypeYes){


				cf.sendCommand("rm \"creations/" + selection + ".mp4\"", false);
				cf.sendCommand("rm -rf \"creations/metadata/" + selection + "\"", false);

				fiveDayWarning.setVisible(false);
				fiveDayKey.setVisible(false);
				notViewedWarning.setVisible(false);
				notViewedKey.setVisible(false);

				updateCreationList();
			} else {
				deleteButton.setDisable(false);
				playButton.setDisable(false);
			}

		}





	}










	// helper function to update all lists
	public void updateCreationList() {
		Thread updateCreationList = new Thread(new UpdateCreationListTask(creationList, name, confidenceRating, creationDate, lastViewed, deleteButton, playButton, creationNoText, VideoCreationController.getCurrentlyGenerating(), this));
		updateCreationList.start();
	}



	// helper function to change alert font size. (repeated in each class that uses alerts)
	// repetition required as it did not make sense for all controllers to extend a class containing it.
	// It also didn't make sense to have a separate class just for this function
	public void setBigFont(Alert popup) {
		
		
		/* Code adapted by Jack Chamberlain
		 * Original Author: José Pereda
		 * Source: https://stackoverflow.com/questions/28417140/styling-default-javafx-dialogs/28421229#28421229
		 */
		DialogPane dialogPane = popup.getDialogPane();
		dialogPane.getStylesheets().add(
				Main.class.getResource("/wikiSpeakGUI/view/styles.css").toExternalForm());
		dialogPane.getStyleClass().add("dialog-pane");
		/*
		 * attribute ends
		 */
		
		
	}




}
