package en.kly.AdvancedHaskellInterpreter;

import TestWriter.TestLogWriter;
import TestWriter.HUnitWriter;

import Enums.InterpreterState;

/**
 * Class that represents the state of the Advanced Haskell Interpreter.
 * 
 * @author Kevin Ly
 * @version 2.7 (18/2/2014)
 */
public class AdvancedHaskellInterpreter
{
    private TestLogWriter tlw;
    private GHCi ghci;
    private String prompt;
    private InterpreterState state;
    
    private boolean recording;      // For recording mode purpose.    
    private String filePath;    // Stores the file path to the *.hs file.
    
    /**
     * Create an AdvancedHaskellInterpreter instance.
     */
    public AdvancedHaskellInterpreter()
    {
        tlw = null;
        filePath = null;
        ghci = new GHCi();
        prompt = "";
        state = InterpreterState.OUTPUTTING;
        
        recording = false;
    }
    
    /**
     * Set the state of the Advanced Haskell Interpreter.
     * @param newState    The new state of this instance.
     */
    synchronized public void setState(InterpreterState newState) 
    {
        state = newState;
    }
    
    /**
     * Get the current state of the Advanced Haskell Interpreter.
     * @return    The current state of the AdvancedHaskellInterpreter.
     */
    synchronized public InterpreterState getState() 
    {
        return state;
    }
    
    /**
     * Create a new TestLogWriter class.
     */
    synchronized public void createTestLogWriter()
    {
        tlw = new TestLogWriter(filePath);
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
    public void setFilePath(String filePath)
    {
        this.filePath = filePath;
    }
    
    /**
     * Return the file path of the loaded Haskell module.
     * @return    The file path of the loaded Haskell module.
     */
    public String getFilePath()
    {
        return filePath;
    }
    
    /**
     * Clear the value of the field filePath.
     */
    synchronized public void clearFilePath()
    {
        filePath = null;
    }
    
    /**
     * Create the HUnit file.
     * @return    The newly created *hunit.hs file.
     */
    public String createHUnitFile()
    {
        HUnitWriter hunit = new HUnitWriter(filePath);
        hunit.initialiseHUnit();
        hunit.addTestCases(filePath.replace(".hs", "__test.xml"));
        
        return hunit.getHUnit();
    }
}
