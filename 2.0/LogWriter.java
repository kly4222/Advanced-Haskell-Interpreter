import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;

/**
 * LogWriter class records functions calls and it's output.
 * Records tests in the same file as the source code, or the home directory
 * if no files are loaded.
 *
 * @author    Kevin Ly
 * @version    2.0    (14/01/2014)
 */
public class LogWriter
{
    private File log;
    private String tempCurrentCommand; // Acts as temporary storage.
    private String tempLogPath; // Acts as a temporary storage.
    private boolean writingToLog;

    /**
     * Create a new LogWriter class that will record tests in a log file.
     * @param file The name of the log file to write.
     */
    public LogWriter()
    {
        log = null;
        tempCurrentCommand = "";
        tempLogPath = "";
        writingToLog = false;
    }
    
    /**
     * Change the log file to write to.
     */
    public synchronized void tempSetLogFilePath(String pathToSourceCode)
    {
        pathToSourceCode = removeCommand(pathToSourceCode);
        tempLogPath = pathToSourceCode.replaceAll(".hs", "__test.log");
    }
    
    /**
     * Create a new log file according to the pathname stored in the tempLogPath field.
     */
    public synchronized void createNewLogFile()
    {
        log = new File(tempLogPath);
        tempLogPath = "";
        writingToLog = true;
    }

    /**
     * Temporarily store the user input.
     */
    public synchronized void writeInput(String input)
    {
        tempCurrentCommand = input + " == ";
    }
    
    /**
     * Writes the input and output of a function to a log file.
     */
    public synchronized void writeOutput(String output)
    {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(log, true));
            writer.write(tempCurrentCommand + output);
            writer.close();
            tempCurrentCommand = "";
        }
        catch (IOException e) {
        }
    }
    
    /**
     * Removes the ghci command prefix
     */
    public synchronized String removeCommand(String cmd)
    {
        String[] s = cmd.split(" ");
        cmd = "";
        
        for (int i = 1; i < s.length; i++) {
            cmd += s[i] + " ";
        }
        cmd = cmd.replaceAll("\"", "");
        return cmd;
    }
    
    /**
     * Check if this LogWriter is currently writing to a log file.
     */
    public synchronized boolean isWritingToLog()
    {
        return writingToLog;
    }
}
