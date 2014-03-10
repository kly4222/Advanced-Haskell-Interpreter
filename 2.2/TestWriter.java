import java.io.BufferedWriter;
import java.io.IOException;

/**
 * TestWriter class is responsible for recording tests into a log file.
 * 
 * @author Kevin Ly
 * @version 2.2 (10/02/2014)
 */
public class TestWriter
{
    private boolean recording;
    private boolean blockOutput;
    private boolean printing;
    private String input;    // Temporary storage.
    private String testType;    // Temporary storage.
    private String filePath;    // Temporary storage.
    private GHCi ghci;
    
    public TestWriter()
    {
        recording = false;
        blockOutput = false;
        printing = false;
        input = "";
        testType = "";
        filePath = "";
        ghci = new GHCi();
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
     * @param b    Whether recording is turned on or off.
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
    }
    
    /**
     * Checks whether output from the GHCi should be blocked.
     * The isBlocking() method is used when the recording mode of the AHI is turned on.
     * @return    Whether the output of the GHCi should be prevented from printing.
     */
    synchronized public boolean isBlocking()
    {
        return blockOutput;
    }
    
    /**
     * Set whether output from the GHCi should be prevented from being printed.
     * @param b    true if output should not be printed, else false.
     */
    synchronized public void setBlocking(boolean b) 
    {
        blockOutput = b;
    }
    
    synchronized public boolean isPrinting()
    {
        return printing;
    }
    
    synchronized public void setPrinting(boolean b)
    {
        printing = b;
    }
    
    /**
     * Temporarily stores the user input if recording mode is on.
     * @param s    The user input to temporarily store.
     */
    synchronized public void setInput(String input)
    {
        this.input = input;
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
     * Set the file path for the test log file.
     * @param filepath    The source code's file path.
     */
    synchronized public void setFilePath(String filePath)
    {
        this.filePath = filePath;
    }
    
    /**
     * Return the current state of the GHCi.
     * @return    The GHCi's current state.
     */
    synchronized public GHCiState getState()
    {
        return ghci.getState();
    }
    
    /**
     * Set the new state of the GHCi.
     * @param newState    The new state of the GHCi.
     */
    synchronized public void setState(GHCiState newState)
    {
        ghci.setState(newState);
    }
    
    /**
     * Uses fields input and testType to write a true or false test into a file using
     * the GHCi command appendFile.
     * @param bw    The BufferedWriter object used to send an appendFile command to the GHCi.
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
        reset();
    }
    
    /**
     * Resets the input and testType field variables to nothing.
     */
    synchronized public void reset()
    {
        input = "";
        testType = "";
    }
}
