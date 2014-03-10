import java.io.BufferedWriter;

/**
 * Class that represents the state of the Advanced Haskell Interpreter.
 * 
 * @author Kevin Ly
 * @version 2.4 (12/2/2014)
 */
public class AdvancedHaskellInterpreter
{
    private TestWriter tw;
    private GHCi ghci;
    private String prompt;
    
    private boolean recording;    // For recording mode purpose.
    private boolean blockOutput;    // For recording mode purpose.
    private boolean printing;    // For recording mode purpose.
    
    public AdvancedHaskellInterpreter()
    {
        tw = new TestWriter();
        ghci = new GHCi();
        prompt = "";
        
        recording = false;
        blockOutput = false;
        printing = false;
    }
    
    /**
     * Set the String prompt.
     * @param s    The prompt
     */
    public void setPrompt(String s)
    {
        prompt = s;
    }
    
    /**
     * Get prompt.
     * @return    Prompt
     */
    public String getPrompt()
    {
        return prompt;
    }
    
    /**
     * Checks whether test-logging mode is on or off.
     * @return    true if program is recording, false otherwise.
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
        System.out.print(prompt);
    }
    
    /**
     * Checks whether output from the GHCi should be blocked.
     * isBlocking() is used when the recording mode of the AHI is turned on.
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
     * Return the current state of the GHCi's input state.
     * @return    The GHCi's input current state.
     */
    synchronized public GHCiState getGHCiState()
    {
        return ghci.getState();
    }
    
    /**
     * Set the new input state of the GHCi.
     * @param newState    The new input state of the GHCi.
     */
    synchronized public void setGHCiState(GHCiState newState)
    {
        ghci.setState(newState);
    }
    
    /**
     * Temporarily stores the user input if recording mode is on.
     * @param s    The user input to temporarily store.
     */
    synchronized public void setInput(String input)
    {
        tw.setInput(input);
    }
    
    /**
     * Store the test type for a given function input/output.
     * @param b    If true, store a true test (==),
     *             else store a false test (!=).
     */
    synchronized public void setTestType(boolean b)
    {
        tw.setTestType(b);
    }
    
    /**
     * Set the file path for the test log file.
     * @param filepath    The source code's file path.
     */
    synchronized public void setFilePath(String filePath)
    {
        tw.setFilePath(filePath);
    }
    
    /**
     * Uses fields input and testType to write a true or false test into a file using
     * the GHCi command appendFile.
     * @param bw    The BufferedWriter object used to send an appendFile command to the GHCi.
     */
    synchronized public void writeTest(BufferedWriter bw)
    {
        tw.writeTest(bw);
    }
    
    /**
     * Resets the input and testType field variables to nothing.
     */
    synchronized public void reset()
    {
        tw.reset();
    }
}
