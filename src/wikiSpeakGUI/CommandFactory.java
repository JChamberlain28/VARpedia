package wikiSpeakGUI;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/*
 * This class eliminates repeated code and returns standard output
 * and error of bash in an ArrayListformat [stdOutput, stdError]
 */

public class CommandFactory {

	// used to kill bash processes on exiting system (accessable from main class)
	public static List<Process> pastProcesses = new ArrayList<Process>();

	private Process process = null;

	public CommandFactory() {

	}



	/*
	 *@param command  - the bash command that must be executed by process builder (String)
	 *@param addNewLines  - indicates weather the standard output and error must have new lines between
	 *						each separate output from bash.
	 *
	 *@return List<String>  - a list containing [Standard output, Standard error]
	 * 
	 * This method executes a supplied command in bash via a ProcessBuilder. As VARpedia uses a lot
	 * of bash commands, this class reduces the amount of duplicate code. Simplifying a command to a call
	 * to this method.
	 */
	public List<String> sendCommand(String command, boolean addNewLines) {


		List<String> output = new ArrayList<String>();
		List<String> error = new ArrayList<String>();
		InputStream stdout = null;
		InputStream stderr = null;


		try {
			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", command);

			// set directory for commands to be executed in to runnable jar directory
			builder.directory(new File(Main._jarDir));
			process = builder.start();
			pastProcesses.add(process);

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


	/*
	 * @return void
	 * 
	 * This method kills the CommandFactorys currently executing bash process
	 * 
	 */
	public void killCurrentProcess() {
		process.destroy();
	}

}
