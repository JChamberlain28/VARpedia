
package wikiSpeakGUI;

import java.util.List;
import javafx.concurrent.Task;

public class GenerateAudioTask extends Task<List<String>>  {


	private String _lineNo;
	private String _tempDir;
	List<String> result;

	public GenerateAudioTask(String lineNo, String tempDir) {
		_lineNo = lineNo;
		_tempDir = tempDir;


	}

	@Override
	protected List<String> call() throws Exception {
		CommandFactory generationScript = new CommandFactory();


		result = generationScript.sendCommand("./generateAudio.sh " + _lineNo + " " + _tempDir, false);

		return result;
	}






}

