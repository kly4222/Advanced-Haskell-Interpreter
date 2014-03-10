import java.io.BufferedWriter;
import java.io.IOException;

/**
 * TestWriter class is responsible for recording tests into a log file.
 *
 * @author Kevin Ly
 * @version 2.1 (05/02/2014)
 */
public class TestWriter
{
    private boolean recording;
    private String input;
    private String testType;
    
    public TestWriter()
    {
        recording = false;
        input = "";
        testType = "";
    }
    
    /**
     * Checks whether test-logging mode is on or off.
     * @return    true if program is recording,
     *            false otherwise.
     */
    synchronized public boolean isRecording()
    {
        return recording;
    }
    
    /**
     * Sets recording mode on or off.
     * @param b Whether recording is turned on or off.
     */
    synchronized public void setRecording(boolean b)
    {
        recording = b;
        
        if (recording) {
            System.out.println("Test logging is on.");
        }
        else {
            System.out.println("Test logging is off.");
        }
        // Print a debug or temporary purposes.
        System.out.print("Debug> ");
    }
    
    /**
     * Temporarily stores the user input if recording mode is on.
     * @param s The user input to temporarily store.
     */
    synchronized public void setInput(String s)
    {
        input = s;
    }
    
    /**
     * Store the test type for a given function input/output.
     * @param b If true, store a true test (==),
     * else store a false test (!=).
     */
    synchronized public void setTestType(boolean b)
    {
        if (b) {
            testType = " == ";
        }
        else {
            testType = " != ";
        }
    }
    
    /**
     * Uses fields input and testType to write a true or false test into a file using
     * the GHCi command appendFile.
     * @param bw The BufferedWriter object used to send an appendFile command to the GHCi.
     */
    synchronized public void writeTest(BufferedWriter bw)
    {
        try {
            // appendFile "Users/Hypho/Test.hs" $ "<input> <testType> " ++ show (input)\n 
            String cmd = "appendFile \"/Users/Hypho/Test.hs\" $ \"" + input + testType + "\"" + " ++ show (" + input + ") ++ \"\\n\"";
            bw.write(cmd + "\n");
            bw.flush();
        }
        catch (IOException e) {
        }
        input = "";
        testType = "";
    }
}
