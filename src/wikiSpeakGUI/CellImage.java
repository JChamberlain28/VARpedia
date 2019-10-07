package wikiSpeakGUI;

import javafx.scene.image.ImageView;

// class required in order to store image for custom TableView values
// (displaying images in TableView)

public class CellImage {

	
	private ImageView _image;

	public CellImage(ImageView image) {
		_image = image;
	}
	
	public ImageView getImage() {
		return _image;
	}
}
