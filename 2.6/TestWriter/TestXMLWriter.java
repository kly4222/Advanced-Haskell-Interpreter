package TestWriter;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLEventFactory;

import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.util.ArrayList;

/**
 * TestXMLWriter class writes recorded tests into an *.xml file.
 * 
 * @author Kevin Ly
 * @version 2.6 (17/02/2014)
 */
public class TestXMLWriter
{
    private final String TESTLOG  = "testLog";
    private final String TESTCASE = "testCase";
    private final String INPUT    = "input";
    private final String OUTPUT   = "ouput";
    
    private String xml;    // File path to *__test.xml file.
    
    /**
     * Create a new TestXMLWriter instance.
     * @param filePath    The file path of the *__test.log file containing the tests
     *                    to record to the *__test.xml file.
     */
    public TestXMLWriter(String filePath)
    {
        xml = filePath.replace("test.log", "test.xml");    
        
        // If an xml file does not exist, create one.
        if (!(new File(xml)).exists()) {
            createNewXML();
        }
    }

    /**
     * Creates a new *.xml file.
     */
    public void createNewXML()
    {
        try {
            XMLEventFactory eventFactory = XMLEventFactory.newInstance();
            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(new FileOutputStream(xml));            
            
            // Document Type Definition declarations.
            XMLEvent newLine = eventFactory.createDTD("\n");

            eventWriter.add(eventFactory.createStartDocument());
            eventWriter.add(newLine);
            eventWriter.add(eventFactory.createStartElement("", "", TESTLOG));
            eventWriter.add(newLine);
            eventWriter.add(eventFactory.createEndElement("", "", TESTLOG));
            eventWriter.add(newLine);
            eventWriter.add(eventFactory.createEndDocument());
            eventWriter.close();
        }
        catch (FileNotFoundException e) {
        }
        catch (XMLStreamException e) {
        }
    }
    
    /**
     * Add a test case into an existing *.xml file.
     * @param input     The input string to store in the *.xml file.
     * @param output    The output string to store in the *.xml file.
     */
    public void updateXML(String input, String output)
    {
        ArrayList<String> inputs = new ArrayList<String>();
        
        try {
            XMLEventFactory eventFactory = XMLEventFactory.newInstance();
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            
            inputFactory.setProperty(XMLInputFactory.IS_COALESCING, true);
            
            // Read from an existing *.xml file and write to a temporary *.xml file.
            XMLEventReader eventReader = inputFactory.createXMLEventReader(new FileInputStream(xml));
            XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(new FileOutputStream(xml.replace("test.xml", "temp.xml"))); 
            
            // Document Type Definition declarations.
            XMLEvent newLine = eventFactory.createDTD("\n");
            XMLEvent tab = eventFactory.createDTD("    ");
            
            // Read from existing *.xml file.
            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();
                
                if (event.isEndElement()) {
                    EndElement endElement = event.asEndElement();
                    
                    // Check if </testLog> tag has been reached
                    if (endElement.getName().getLocalPart().equals(TESTLOG)) { 
                        
                        // Check if test already exists
                        if (!inputs.contains(input)) {
                            // Add new test into *.xml file.
                            
                            // Add opening <testCase> tag.
                            eventWriter.add(tab);
                            eventWriter.add(eventFactory.createStartElement("", "", TESTCASE));
                            eventWriter.add(newLine);
                                
                            // Add function input value.
                            eventWriter.add(tab);
                            eventWriter.add(tab);
                            eventWriter.add(eventFactory.createStartElement("", "", INPUT));
                            eventWriter.add(eventFactory.createCharacters(input));
                            eventWriter.add(eventFactory.createEndElement("", "", INPUT));
                            eventWriter.add(newLine);
                                
                            // Add function output value.
                            eventWriter.add(tab);
                            eventWriter.add(tab);
                            eventWriter.add(eventFactory.createStartElement("", "", OUTPUT));
                            eventWriter.add(eventFactory.createCharacters(output));
                            eventWriter.add(eventFactory.createEndElement("", "", OUTPUT));
                            eventWriter.add(newLine);
    
                            // Add closing </testCase> tag.
                            eventWriter.add(tab);
                            eventWriter.add(eventFactory.createEndElement("", "", TESTCASE));
                            eventWriter.add(newLine);
                        } 
                        else {
                            System.out.println("Test already exists.");
                        }
                    }
                }
                // Check if Event is StartElement.
                else if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    
                    // Check if StartElement is <input> tag
                    if (startElement.getName().getLocalPart().equals(INPUT)) {
                        XMLEvent next = eventReader.peek();
                        
                        // Ensure that the next Event is Characters
                        if (next != null && next.isCharacters()) {
                            String in = next.asCharacters().getData();
                            inputs.add(in);
                        }
                    }
                }
                // Add Event.
                eventWriter.add(event);
            }
            eventReader.close();
            eventWriter.close();
        }
        catch (FileNotFoundException e) {
        }
        catch (XMLStreamException e) {
        }
        finally {
            // Delete the old *__test.xml file
            File file = new File(xml);
            file.delete();
            
            // Rename the new *__temp.xml file to *__test.xml            
            File temp = new File(xml.replace("test.xml", "temp.xml"));
            temp.renameTo(new File(xml));
        }
    }
}