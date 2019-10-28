package wikiSpeakGUI;

import javafx.scene.layout.HBox;

// class required in order to store image and tick box in a pane for custom TableView values
// (displaying images in TableView with a tick box next to it)


public class CellPane {


	private HBox _pane;

	public CellPane(HBox pane) {
		_pane = pane;
	}

	public HBox getPane() {
		return _pane;
	}
}
