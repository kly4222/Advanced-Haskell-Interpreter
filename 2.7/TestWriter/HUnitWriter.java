package TestWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.events.StartElement;

/**
 * Creates a HUnit file based on an existing *.xml file.
 * 
 * @author Kevin Ly
 * @version 2.7 (18/02/2014)
 */
public class HUnitWriter
{
    private final String INPUT  = "input";
    private final String OUTPUT = "ouput";
    private final String TAB    = "                   ";
    
    private String hunit;    // File path to the hunit file.
    private String temp1;    // File path to the temporary file *.temp1
    private String temp2;    // File path to the temporary file *.temp2
    
    /**
     * Create a new HUnitWriter instance.
     * @param filePath    The filepath of the laoded Haskell module.
     */
    public HUnitWriter(String filePath)
    {
        hunit = filePath.replace(".hs", "__hunit.hs");
        temp1 = hunit.replace(".hs", ".temp1");
        temp2 = hunit.replace(".hs", ".temp2");
    }
    
    /**
     * Return the file path to the HUnit file.
     * @return    The file path to the HUnit file.
     */
    public String getHUnit()
    {
        return hunit;
    }
    
    /**
     * Create *.temp1 and *.temp2 files, which will be used later to create the final HUnit file.
     */
    public void initialiseHUnit()
    {
        try {
            BufferedWriter writer1 = new BufferedWriter(new FileWriter(temp1));
            writer1.write("module RegressTest where\n\nimport Test.HUnit\n\n");
            writer1.close();
            
            BufferedWriter writer2 = new BufferedWriter(new FileWriter(temp2));
            writer2.write("tests = TestList [ ");
            writer2.close();
        }
        catch (IOException e) {
        }
    }
    
    /**
     * Add the test cases from in existing *.xml file into a temp file.
     * addTestCases() is called after initialiseHUnit() method.
     * @param xml    The *.xml file to read.
     */
    public void addTestCases(String xml)
    {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(temp1, true));    // Set append mode to true;
            String input = null;
            String output = null;
            int id = 0;
            
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLEventReader eventReader = inputFactory.createXMLEventReader(new FileInputStream(xml));    
            
            inputFactory.setProperty(XMLInputFactory.IS_COALESCING, true);
            
            // Read from *.xml file.
            while (eventReader.hasNext()) {
                // Get next Event
                XMLEvent event = eventReader.nextEvent();
                
                if (event.isStartElement()) {
                    StartElement start = event.asStartElement();
                    
                    // Check for <input> tag.
                    if (start.getName().getLocalPart().equals(INPUT)) {
                        event = eventReader.nextEvent();
                        
                        if (event != null && event.isCharacters()) {
                            input = event.asCharacters().getData();
                        }
                    }
                    // Check for <output> tag.
                    else if (start.getName().getLocalPart().equals(OUTPUT)) {
                        event = eventReader.nextEvent();
                        
                        if (event != null && event.isCharacters()) {
                            id++;
                            output = event.asCharacters().getData();
                            writer.write("testCase" + id + " = TestCase $ assertEqual (\"" + input + "\") (" + output + ") (" + input + ")\n");
                            input = null;
                            output = null;
                        }
                    }
                }
            }
            writer.close();        
            createTestList(id);
            concatenateTempFiles();
        }
        catch (XMLStreamException e) {
        }
        catch (FileNotFoundException e) {
            System.out.println("XML file does not exist, turn recording mode on and enter some tests.");
        }
        catch (IOException e) {
        }
    }
    
    /**
     * Creates a *.temp2 file based on the contents of the *.temp1 file.
     * createTestList() is called after the addTestCases() method.
     * @param num    The number of tests stored in the *.temp1 file.
     */
    public void createTestList(int num)
    {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(temp1));
            BufferedWriter writer = new BufferedWriter(new FileWriter(temp2, true));    // Set append mode to true;
            int i = 0;
            String line = null;
            
            // Read from the *.temp1 file.
            while (reader.ready()) {
                line = reader.readLine();
                
                if (line.startsWith("testCase")) {
                    if (++i == num) {
                        break;
                    }
                    
                    String[] array = line.split(" ", 2);
                    line = array[0];
                    
                    // Write TestLabel.
                    writer.write("TestLabel \"" + line + "\" " + line + " ,\n");
                    writer.write(TAB);
                }
            }
            
            if (line.startsWith("testCase")) {
                String[] array = line.split(" ", 2);
                line = array[0];
                
                // Write TestLabel.
                writer.write("TestLabel \"" + line + "\" " + line + " ]\n");
            }
            reader.close();
            writer.close();
        }
        catch (FileNotFoundException e) {
        }
        catch (IOException e) {   
        }
    }
    
    /**
     * Concatenate the *.temp1 and *.temp2 files created by the methods:
     * initialiseHUnit(), addTestCases() and createTestList().
     */
    public void concatenateTempFiles()
    {
        try {
            BufferedReader temp1Reader = new BufferedReader(new FileReader(temp1));
            BufferedReader temp2Reader = new BufferedReader(new FileReader(temp2));
            BufferedWriter writer = new BufferedWriter(new FileWriter(hunit));
            
            while (temp1Reader.ready()) {
                String line = temp1Reader.readLine();
                writer.write(line + "\n");
            }
            writer.write("\n");
            
            while (temp2Reader.ready()) {
                String line = temp2Reader.readLine();
                writer.write(line + "\n");
            } 
            temp1Reader.close();
            temp2Reader.close();
            writer.close();
        }
        catch (FileNotFoundException e) {
        }
        catch (IOException e) {
        }
        finally {
            File file1 = new File(temp1);
            file1.delete();
            
            File file2 = new File(temp2);
            file2.delete();
        }
    }
}
