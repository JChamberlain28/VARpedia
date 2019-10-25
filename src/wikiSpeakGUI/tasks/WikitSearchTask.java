package wikiSpeakGUI.tasks;


import java.util.List;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import wikiSpeakGUI.CommandFactory;
import javafx.scene.control.Alert.AlertType;

public class WikitSearchTask extends Task<Void> {

	private Button _wikitButton; // stores button so its state can be changed
	private TextArea _resultField;
	private String _searchTerm;
	private Button _wikitContinue;
	

	List<String> output;
	private ImageView _wikitLoading;
	private BooleanBinding _bb;
	private Button _favButton;


	public WikitSearchTask(Button wikitButton, Button wikitContinue, Button favButton,
			String search, TextArea result, ImageView wikitLoading, BooleanBinding bb) {
		_wikitButton = wikitButton;
		_searchTerm = search;
		_favButton = favButton;
		_resultField = result;
		_wikitContinue = wikitContinue;
		_wikitLoading = wikitLoading;
		_bb = bb;
		
	}

	@Override
	protected Void call() { // searches wikipedia for term

		String term = "wikit " + _searchTerm + " | tee .description.txt";
		CommandFactory command = new CommandFactory();
		output = command.sendCommand(term, false);

		return null;
	}

	@Override
	protected void done() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {


				// informs user if search term not found
				if (output.get(0).equals(_searchTerm + " not found :^(")) {

					_wikitButton.setDisable(false); 
					_favButton.setDisable(false);
					_resultField.setText("");

					Alert popup = new Alert(AlertType.INFORMATION);
					popup.setTitle("Term not found");
					popup.setHeaderText("Please try another term"); popup.show();

				} else { 
				_wikitButton.setDisable(false); 
				_resultField.setText(output.get(0));
				_wikitContinue.setDisable(false);
				_favButton.setDisable(false);

				}
				_wikitButton.disableProperty().bind(_bb);
				_wikitLoading.setVisible(false);


			}

		});
	}

}
