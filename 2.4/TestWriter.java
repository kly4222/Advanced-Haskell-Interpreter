import java.io.BufferedWriter;
import java.io.IOException;

/**
 * TestWriter class is responsible for recording tests into a log file.
 * 
 * @author Kevin Ly
 * @version 2.4 (12/02/2014)
 */
public class TestWriter
{
    private String input;    // Temporary storage.
    private String testType;    // Temporary storage.
    private String filePath;    // Temporary storage.
    
    public TestWriter()
    {
        input = "";
        testType = "";
        filePath = "";
    }
    
    /**
     * Temporarily stores the user input if recording mode is on.
     * @param s    The user input to temporarily store.
     */
    public void setInput(String input)
    {
        this.input = input;
    }
    
    /**
     * Store the test type for a given function input/output.
     * @param b If true, store a true test (==),
     * else store a false test (!=).
     */
    public void setTestType(boolean b)
    {
        if (b) {
            testType = " == ";
        }
        else {
            testType = " != ";
        }
    }
    
    /**
     * Set the file path for the test log file.
     * @param filepath    The source code's file path.
     */
    public void setFilePath(String filePath)
    {
        this.filePath = filePath;
    }
    
    /**
     * Uses fields input and testType to write a true or false test into a file using
     * the GHCi command appendFile.
     * @param bw    The BufferedWriter object used to send an appendFile command to the GHCi.
     */
    public void writeTest(BufferedWriter bw)
    {
        try {
            // appendFile "Users/Hypho/Test.hs" $ "<input> <testType> " ++ show (input)\n 
            String logfile = filePath.replace(".hs", "__test_log.txt");
            String cmd = "appendFile \"" + logfile + "\" $ \"" + input + testType + "\"" + " ++ show (" + input + ") ++ \"\\n\"";
            bw.write(cmd + "\n");
            bw.flush();
        }
        catch (IOException e) {
        }
        reset();
    }
    
    /**
     * Resets the input and testType field variables to nothing.
     */
    public void reset()
    {
        input = "";
        testType = "";
    }
}
