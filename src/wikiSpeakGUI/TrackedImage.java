package wikiSpeakGUI;

import javafx.scene.image.Image;

public class TrackedImage extends Image{

	
	// created custom Image class to store the Image URL
	// (as methods of getting it from normal Image are depreciated)
	private String _url;

	
	public TrackedImage(String url) {
		super(url);
		_url = url;
		
	}
	
	public String getURL() {
		return _url;
	}

}
