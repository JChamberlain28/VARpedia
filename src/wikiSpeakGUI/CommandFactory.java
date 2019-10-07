package wikiSpeakGUI;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


// eliminates repeated code and returns standard output and error of bash in an ArrayList
// format [stdOutput, stdError]
public class CommandFactory {
	
	public CommandFactory() {
		
	}

	
public List<String> sendCommand(String command, boolean addNewLines) {
		
		
		List<String> output = new ArrayList<String>();
		List<String> error = new ArrayList<String>();
		Process process = null;
		InputStream stdout = null;
		InputStream stderr = null;
		
		
		try {
			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", command);
			
			// set directory for commands to be executed in to runnable jar directory
			builder.directory(new File(AppGUI._jarDir));
			process = builder.start();
			stdout = process.getInputStream();
			stderr = process.getErrorStream();
			
			
			// read standard output and error from Bash
			BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));
			String line = null;
			while ((line = stdoutBuffered.readLine()) != null ) {
				output.add(line);
			}
			
			BufferedReader stderrBuffered = new BufferedReader(new InputStreamReader(stderr));
			String lineErr = null;
			while ((lineErr = stderrBuffered.readLine()) != null ) {
				error.add(lineErr);
			}
			process.waitFor();
			process.destroy();
		}
		catch (InterruptedException | IOException e) {
			
		}


		
		List<String> result = new ArrayList<String>();
		
		
		
		// Convert standard output and error arrays into singular strings.
		// Then adds them to an ArrayList to be returned.
		if (output.size() == 0) {
			result.add("");
		}
		else {
			
			StringBuilder sBuilder = new StringBuilder();
			for (int i = 0; i < output.size(); i++)
			{
			    sBuilder.append(output.get(i));
			    
			    if (addNewLines) {
			    	if (i != output.size())
			    	sBuilder.append("\n");
			    }


			}
			
			result.add(sBuilder.toString());
			
			
		}
		
		if (error.size() == 0) {
			result.add("");
		}
		else {
			
			StringBuilder sBuilder = new StringBuilder();
			for (int i = 0; i < output.size(); i++)
			{
			    sBuilder.append(error.get(i));
			    
			    if (addNewLines) {
			    	if (i != error.size())
			    	sBuilder.append("\n");
			    }


			}
			
			result.add(sBuilder.toString());
			
			
		}


		
		return result;	
	}
	
}
