import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import java.util.Scanner;

import Enums.GHCiState;
import Enums.InterpreterState;

/**
 * Thread that deals with sending user commands to the GHCi.
 *
 * @author Kevin Ly
 * @version 2.7 (18/02/2014)
 */
public class GHCiInputThread extends Thread
{
    private BufferedWriter ghciInput;
    private Scanner scanner;
    private AdvancedHaskellInterpreter ahi;

    /**
     * Constructor for Thread.
     * @param name    The name given to this thread.
     * @param out     The output stream used to write to the GHCi.
     * @param tw      The TestWriter class that will record tests.
     */
    public GHCiInputThread(String name, OutputStreamWriter out, AdvancedHaskellInterpreter ahi)
    {
        super(name);
        ghciInput = new BufferedWriter(out);
        this.ahi = ahi;
    }

    /**
     * Execute the GHCiInput thread.
     */
    public void run()
    {
        try {
            scanner = new Scanner(System.in);

            while (true) {
                // Read user input.
                String cmd = scanner.nextLine().trim();

                // Check if recording mode is active.
                if (cmd.equals(":record")) {
                    switch (ahi.ghci().getState()) {
                    case PRELUDE:
                        System.out.println("A module must be loaded to turn recording mode on.");
                        System.out.print(ahi.getPrompt());
                        continue;
                    case MODULES_LOADED:
                        ahi.setRecording(!ahi.isRecording());
                        ahi.createTestLogWriter();
                        continue;
                    default:
                        continue;
                    }
                }
                // Check for regression testing command.
                else if (cmd.equals(":regress")) {
                    switch (ahi.ghci().getState()) {
                    case PRELUDE:
                        System.out.println("A module must be loaded to turn perform regression testing.");
                        System.out.print(ahi.getPrompt());
                        continue;
                    case MODULES_LOADED:
                        String hunit = ahi.createHUnitFile();
                        ahi.setState(InterpreterState.BLOCK_OUTPUT);
                        
                        ghciInput.write(":l " + ahi.getFilePath() + " " + hunit + "\n");
                        ghciInput.flush();
                        ahi.ghci().setState(GHCiState.LOADING);
                        
                        // Wait for GHCi to load HUnit file.
                        loop :while (true) {
                            switch (ahi.getState()) {
                            case INPUTTING:
                                break loop;
                            default:
                                break;
                            }
                        }
                        
                        // Perform regression test.
                        ghciInput.write("Test.HUnit.runTestTT RegressTest.tests\n");
                        ghciInput.flush();
                        ahi.setState(InterpreterState.OUTPUTTING);
                        continue;
                    default:
                        continue;
                    }
                }
                
                // Check for ghci exit condition.
                else if (cmd.startsWith(":q")) {
                    ahi.ghci().setState(GHCiState.QUITTING);
                    
                    // Send command to ghci.
                    sendCommand(cmd);
                    break;
                }
                // Check for ghci load condition.
                else if (cmd.startsWith(":l")) {
                    ahi.ghci().setState(GHCiState.LOADING);
                    
                    // Send command to ghci.
                    sendCommand(cmd);
                    
                    String filePath = removeCommand(cmd);
                    ahi.setFilePath(filePath);
                    continue;
                }

                // Check if test logging is on.
                if (ahi.isRecording()) {
                    
                    // Send command to GHCi, but block the printing of the prompt.
                    ghciInput.write(cmd + '\n');
                    ghciInput.flush();
                    ahi.setState(InterpreterState.BLOCK_PROMPT);
                    
                    // Wait for GHCi to finish outputting result, but before prompt.
                    loop: while (true) {
                        switch (ahi.getState()) {
                        case INPUTTING:
                            break loop;
                        default:
                            continue;  
                        }
                    }
                    // Store input command.
                    ahi.testWriter().setInput(cmd);
                    
                    // Ask if output is correct.
                    isCorrect();
                }
                else {
                    // Send command to ghci.
                    sendCommand(cmd);
                }
            }
        }
        catch (IOException e) {
        }
        finally {
            // GHCi has exited, so close all I/O streams.
            try { scanner.close(); ghciInput.close(); } catch (IOException e) {}
        }
    }

    /**
     * Removes the GHCi command prefix.
     * @param cmd    The user input.
     * @return    The user input with the haskell command removed.
     */
    public String removeCommand(String cmd)
    {
        String[] array = cmd.replaceAll("\\\\", "").split(" ", 2);
        
        if (array.length > 1) {
            return array[1];
        }
        else {
            return null;
        }
    }

    /**
     * Ask the user if a function output is correct.
     */
    public void isCorrect()
    {
        System.out.print("Is output correct? [y/n] > ");

        loop: while (true) {
            String answer = scanner.nextLine().trim().toLowerCase();
            
            // Check what the user has answered.
            switch (answer) {
            case "y":
                saveTest();
                break loop;
            case "n":
                ahi.testWriter().clearTest();
                System.out.print(ahi.getPrompt());
                break loop;
            default:
                System.out.print("Please answer with [y] or [n] > ");
                continue;
            }
        }
    }

    /**
     * Ask if user wants to save test.
     */
    public void saveTest()
    {
        System.out.print("Save test? [y/n] > ");
        
        loop: while (true) {
            String answer = scanner.nextLine().trim().toLowerCase();
            
            // Check what the user has answered.
            switch (answer) {
            case "y":
                ahi.testWriter().writeTest(ghciInput);
                break loop;
            case "n":
                ahi.testWriter().clearTest();
                break loop;
            default:
                System.out.print("Please answer with [y] or [n] > ");
                continue;
            }
        }
        ahi.setState(InterpreterState.OUTPUTTING);
    }
    
    /**
     * Sends a command to the GHCi.
     * @param cmd    The Haskell command to send to the GHCi.
     */
    public void sendCommand(String cmd)
    {
        try {
            // Send command to ghci.
            ghciInput.write(cmd + '\n');
            ghciInput.flush();
            ahi.setState(InterpreterState.OUTPUTTING);
        }
        catch (IOException e) {
        }
    }
}
