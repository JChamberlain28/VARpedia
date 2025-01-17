package api;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.flickr4java.flickr.*;
import com.flickr4java.flickr.photos.*;



/* Code adapted by Jack Chamberlain
 * Original Author: Nasser Giacaman
 * Source: The University of Auckland ACP SE206
 */
public class FlickrAPI {

	public static String getAPIKey(String key) {


		String config = System.getProperty("user.dir") 
				+ System.getProperty("file.separator")+ "flickr-api-keys.txt"; 


		File file = new File(config); 
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));


			String line;
			try {
				while ( (line = br.readLine()) != null ) {
					if (line.trim().startsWith(key)) {
						br.close();
						return line.substring(line.indexOf("=")+1).trim();
					}
				}
				br.close();
				throw new RuntimeException("Couldn't find " + key +" in config file "+file.getName());

			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		return null;
	}


	/*
	 *@param query  - the term to search flicker for (String)
	 *@param tempDir  - temporary directory to download to. Is the same as the 
	 *					temp directory for creating a creation. (String)
	 *@param numOfImages - number of images to download (int)
	 *
	 *@return void
	 * 
	 * Method initiates image downloading via flickrAPI.
	 */
	public static void downloadImages(String query, String tempDir, int numOfImages) {
		try {
			String apiKey = getAPIKey("apiKey"); 
			String sharedSecret = getAPIKey("sharedSecret");

			Flickr flickr = new Flickr(apiKey, sharedSecret, new REST());

			int resultsPerPage = numOfImages;
			int page = 0;

			PhotosInterface photos = flickr.getPhotosInterface();
			SearchParameters params = new SearchParameters();
			params.setSort(SearchParameters.RELEVANCE);
			params.setMedia("photos"); 
			params.setText(query);

			PhotoList<Photo> results = photos.search(params, resultsPerPage, page);

			for (Photo photo: results) {

				BufferedImage image = photos.getImage(photo,Size.LARGE);
				String filename = photo.getId()+".jpg";
				File outputfile = new File(tempDir,filename);
				ImageIO.write(image, "jpg", outputfile);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}


	}

}
/*
 * attribute ends
 */
