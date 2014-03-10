import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Scanner;
import Enums.GHCiState;

/**
 * Thread that deals with sending user commands to the GHCi.
 *
 * @author Kevin Ly
 * @version 2.5 (17/02/2014)
 */
public class GHCiInputThread extends Thread
{
    private BufferedWriter ghciInput;
    private Scanner scanner;
    private AdvancedHaskellInterpreter ahi;

    /**
     * Constructor for Thread.
     * @param name The name given to this thread.
     * @param out The output stream used to write to the GHCi.
     * @param tw The TestWriter class that will record tests.
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
                        continue;
                    default:
                        break;
                    }
                }
                // Check for ghci exit condition.
                if (cmd.startsWith(":q")) {
                    ahi.ghci().setState(GHCiState.QUITTING);
                    
                    ghciInput.write(cmd + '\n');
                    ghciInput.flush();
                    break;
                }
                // Check for ghci load condition.
                if (cmd.startsWith(":l")) {
                    ahi.ghci().setState(GHCiState.LOADING);
                    
                    String filePath = removeCommand(cmd);
                    ahi.setFilePath(filePath);
                }

                // Check if test logging is on.
                if (ahi.isRecording()) {
                    ahi.setBlocking(true);    // Prevents the GHCi prompt being being printed prematurely.
                    
                    // Send command to ghci.
                    ghciInput.write(cmd + '\n');
                    ghciInput.flush();
                    ahi.setPrinting(true);
                    
                    // Wait for GHCi to finish outputting result, but before prompt.
                    while (ahi.isPrinting()) {
                        ;
                    }
                    ahi.testWriter().setInput(cmd);
                    
                    // Ask if output is correct, and if user wants to save test.
                    isCorrect();
                    
                    ahi.setBlocking(false);
                }
                else {
                    // Send command to ghci.
                    ghciInput.write(cmd + '\n');
                    ghciInput.flush();
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
     * @param cmd The user input.
     * @return The user input with the haskell command removed.
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
        System.out.print(ahi.getPrompt());
    }
}
