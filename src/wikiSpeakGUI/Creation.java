package wikiSpeakGUI;

public class Creation {
	
	private String name;
	private String rating;
	private String creationDate;
	private String lastViewed;

	public Creation(String name, String rating, String creationDate, String lastViewed) {
		this.name = name;
		this.rating = rating;
		this.creationDate = creationDate;
		this.lastViewed = lastViewed;
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
