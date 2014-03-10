package TestWriter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;

/**
 * TestWriter class is responsible for recording tests into a *__test.log file.
 * Uses Haskell's appendFile function to record tests into the *__test.log file.
 * 
 * @author Kevin Ly
 * @version 2.5 (17/02/2014)
 */
public class TestLogWriter
{
    private String logfile;             // The *__test.log file that stores the recorded tests.
    private TestXMLWriter xmlWriter;    // The TestXMLWriter instance that will write tests into a *__test.xml file.

    private String input;    // Acts as a buffer storage.

    public TestLogWriter(String path)
    {
        input = null;
        logfile = path.replace(".hs", "__test.log");
        xmlWriter = new TestXMLWriter(logfile);
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
     * Uses the input field to write a test into a file using Haskell's built-in appendFile function. 
     * @param bw    The BufferedWriter object used to send an appendFile command to the GHCi.
     */
    public void writeTest(BufferedWriter bw)
    {        
        try {                       
            String cmd = "appendFile \"" + logfile + "\" $ \"" + input + "\" ++ \"\\n\" ++ show (" + input + ") ++ \"\\n\"";
            bw.write(cmd + "\n");
            bw.flush();
             
            // Wait for Haskell to create log file containing recently recorded test.
            while (!(new File(logfile)).exists()) {
                ;
            }            
            BufferedReader reader = new BufferedReader(new FileReader(logfile));
            
            // Read the log file and retrieve the function input and output, then close the BufferedReader.
            String input = reader.readLine();
            String output = reader.readLine();            
            reader.close();
            
            // Write test into an xml file.
            xmlWriter.updateXML(input, output);
            
            // Delete the log file.
            (new File(logfile)).delete();
        }
        catch (IOException e) {
        }
    }

    /**
     * Resets the input and testType field variables to nothing.
     */
    public void clearTest()
    {
        input = null;
    }
}
