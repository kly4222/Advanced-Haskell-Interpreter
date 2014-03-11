package en.kly.AdvancedHaskellInterpreter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import Enums.GHCiState;
import Enums.InterpreterState;

/**
* Thread that deals with printing the output of the ghci.
* Assumes only 1 module has been loaded.
*
* @author Kevin Ly
* @version 2.7 (18/02/2014)
*/
public class GHCiOutputThread extends Thread
{
    private final String EXITING = "Leaving GHCi.\n";
    private final String NO_MODULES_LOADED = "Ok, modules loaded: none.\n";
    private final String LOAD_SUCCESSFUL = "Ok, modules loaded: ";
    private final String LOAD_UNSUCCESSFUL = "Failed, modules loaded: ";

    private BufferedReader ghciOutput;
    private String currentLine;
    private AdvancedHaskellInterpreter ahi;

    /**
     * Constructor for the GHCiOutputThread thread.
     * @param name    The name given to this particular thread.
     * @param in    The input stream used to read the output of the GHCi.
     * @param tw    The test writer object.
     */
    public GHCiOutputThread(String name, InputStreamReader in, AdvancedHaskellInterpreter ahi)
    {
        super(name);
        ghciOutput = new BufferedReader(in);
        currentLine = "";
        this.ahi = ahi;
    }

    /**
     * Executes this thread.
     * GHCiOutputThread prints out the output of the GHCi to standard output.
     */
    public void run()
    {
        try {
            loop: while (true) {
                // Wait for input to be sent by the user.
                switch (ahi.getState()) {
                case INPUTTING:
                    continue;
                    
                // Output from GHCi normally without any restrictions
                case OUTPUTTING:
                    do {
                        // Read character and concatenate with currentLine variable.
                        char c = (char) ghciOutput.read();
                        currentLine += c;
                        
                        // Check if a complete line has been outputted by the GHCi.
                        if (c == '\n') {
                            // Check the contents of the current line.
                            switch (ahi.ghci().getState()) {
                            case QUITTING:
                                if (currentLine.equals(EXITING)) {
                                    System.out.print(EXITING);
                                    break loop;
                                }
                            case LOADING:
                                if (currentLine.equals(NO_MODULES_LOADED) || currentLine.startsWith(LOAD_UNSUCCESSFUL)) {
                                    ahi.ghci().setState(GHCiState.PRELUDE);
                                    ahi.clearFilePath();
                                    ahi.deleteTestLogWriter();
                                }
                                else if (currentLine.startsWith(LOAD_SUCCESSFUL)) {
                                    ahi.ghci().setState(GHCiState.MODULES_LOADED);
                                    ahi.createTestLogWriter();
                                }
                                break;
                            default:
                                break;
                            }
                            System.out.print(currentLine);
                            currentLine = "";
                        }
                    }
                    while (ghciOutput.ready());
                    
                    if (isPrompt(currentLine)) {
                        if (!currentLine.equals(ahi.getPrompt())) {
                            ahi.setPrompt(currentLine);
                        }
                        System.out.print(currentLine);
                        currentLine = "";
                        ahi.setState(InterpreterState.INPUTTING);
                    }
                    break;
                    
                // Output from GHCi normally without any restrictions.
                case BLOCK_PROMPT:
                    do {
                        // Read character and concatenate with currentLine variable.
                        char c = (char) ghciOutput.read();
                        currentLine += c;
                        
                        // Check if a complete line has been outputted by the GHCi.
                        if (c == '\n') {
                            System.out.print(currentLine);
                            currentLine = "";
                        }
                    }
                    while (ghciOutput.ready());
                    
                    if (isPrompt(currentLine)) {
                        // Update prompt string.
                        if (!currentLine.equals(ahi.getPrompt())) {
                            ahi.setPrompt(currentLine);
                        }
                        currentLine = "";
                        ahi.setState(InterpreterState.INPUTTING);
                    }
                    break;
                    
                // Read from the GHCi, but do not print.
                case BLOCK_OUTPUT:
                    do {
                        char c = (char) ghciOutput.read();
                        currentLine += c;
                        
                        // Check if a complete line has been outputted by the GHCi.
                        if (c == '\n') {
                            // Check the contents of the current line.
                            switch (ahi.ghci().getState()) {
                            case LOADING:
                                if (currentLine.equals(NO_MODULES_LOADED) || currentLine.startsWith(LOAD_UNSUCCESSFUL)) {
                                    ahi.ghci().setState(GHCiState.PRELUDE);
                                    System.out.println("Error loading HUnit file.");
                                }
                                else if (currentLine.startsWith(LOAD_SUCCESSFUL)) {
                                    ahi.ghci().setState(GHCiState.MODULES_LOADED);
                                    ahi.setState(InterpreterState.INPUTTING);
                                }
                                break;
                            default:
                                break;
                            }
                            currentLine = "";
                        }
                    }
                    while (ghciOutput.ready());
                    break;
                    
                case PRINT_HUNIT:
                    do {
                        // Read character and concatenate with currentLine variable.
                        char c = (char) ghciOutput.read();
                        currentLine += c;
                        
                        // Check if a complete line has been outputted by the GHCi.
                        if (c == '\n') {
                            System.out.print(currentLine);
                            currentLine = "";
                        }
                    }
                    while (ghciOutput.ready());
                    
                    if (isPrompt(currentLine)) {
                        // Update prompt string.
                        if (!currentLine.equals(ahi.getPrompt())) {
                            ahi.setPrompt(currentLine);
                        }
                        currentLine = "";
                        ahi.setState(InterpreterState.INPUTTING);
                    }
                    break;
                default:
                    break;
                }
            }
        }
        catch (IOException e) {
        }
        finally {
            try { ghciOutput.close(); } catch (IOException e) {}
        }
    }

    /**
     * Checks if a string is the prompt indicator.
     * @param s    The string to check.
     * @return    True if s is a prompt, else false.
     */
    public boolean isPrompt(String s)
    {
        return s.endsWith("> ") || s.endsWith("| ");
    }
}
