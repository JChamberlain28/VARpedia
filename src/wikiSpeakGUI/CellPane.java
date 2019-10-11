package wikiSpeakGUI;

import javafx.scene.layout.HBox;

// class required in order to store image and tick box for custom TableView values
// (displaying images in TableView)

public class CellPane {

	
	private HBox _pane;

	public CellPane(HBox pane) {
		_pane = pane;
	}
	
	public HBox getPane() {
		return _pane;
	}
}
