package wikiSpeakGUI;

public class Creation {
	
	private String name;
	private String rating = "null";
	private String creationDate = "null";
	private String lastViewed = "null";

	public Creation(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getRating() {
		return rating;
	}

	public String getCreationDate() {
		return creationDate;
	}

	public String getLastViewed() {
		return lastViewed;
	}
	
	
}
