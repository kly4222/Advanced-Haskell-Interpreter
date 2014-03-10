import TestWriter.TestLogWriter;

/**
 * Class that represents the state of the Advanced Haskell Interpreter.
 * 
 * @author Kevin Ly
 * @version 2.4 (12/2/2014)
 */
public class AdvancedHaskellInterpreter
{
    private TestLogWriter tlw;
    private GHCi ghci;
    private String prompt;
    
    private boolean recording;      // For recording mode purpose.
    private boolean blockOutput;    // For recording mode purpose.
    private boolean printing;       // For recording mode purpose.
    
    private String filePath;
    
    public AdvancedHaskellInterpreter()
    {
        tlw = null;
        filePath = null;
        ghci = new GHCi();
        prompt = "";
        
        recording = false;
        blockOutput = false;
        printing = false;
    }
    
    /**
     * Create a new TestLogWriter class.
     */
    synchronized public void createTestLogWriter()
    {
        tlw = new TestLogWriter(filePath.replace(".hs", "__test.log"));
    }
    
    /**
     * Deletes the TestLogWriter instance this class refers to.
     */
    synchronized public void deleteTestLogWriter()
    {
        tlw = null;
    }   
    
    /**
     * Set the String prompt.
     * @param s    The new GHCi prompt
     */
    synchronized public void setPrompt(String s)
    {
        prompt = s;
    }
    
    /**
     * Get prompt.
     * @return    The GHCi prompt as a string.
     */
    synchronized public String getPrompt()
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
     * Get the GHCi instance.
     * @return    GHCi instance.
     */
    synchronized public GHCi ghci()
    {
        return ghci;
    }
    
    /**
     * Get the TestWriter instance.
     * @return    TestWriter instance.
     */
    synchronized public TestLogWriter testWriter()
    {
        return tlw;
    }
    
    /**
     * Temporarily store the pathname of the loaded Haskell module.
     * @param filePath    The file path of the loaded Haskell source code.
     */
    synchronized public void setFilePath(String filePath)
    {
        this.filePath = filePath;
    }
    
    /**
     * Clear the value of the field filePath.
     */
    synchronized public void clearFilePath()
    {
        filePath = null;
    }
    
    /**
     * Creates a new TestLogWriter instance, assuming that a Haskell module
     * has been successfully loaded.
     */
    synchronized public void createLogWriter()
    {
        tlw = new TestLogWriter(filePath);
        filePath = null;
    }
}
