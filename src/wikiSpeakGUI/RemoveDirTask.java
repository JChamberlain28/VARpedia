package wikiSpeakGUI;

import javafx.concurrent.Task;

public class RemoveDirTask  extends Task<Void> {
	
	private String _dir;

	public RemoveDirTask(String dir) {
		_dir = dir;
	}

	@Override
	protected Void call() { // removes specified directory and its contents
		CommandFactory command = new CommandFactory();
		command.sendCommand("rm -r " + _dir, false);
		return null;
	}

	
	
}
