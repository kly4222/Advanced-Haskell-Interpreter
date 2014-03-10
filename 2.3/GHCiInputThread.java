import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Scanner;

/**
 * Thread that deals with sending user commands to the GHCi.
 *
 * @author Kevin Ly
 * @version 2.3 (10/02/2014)
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
                    ahi.setRecording(!ahi.isRecording());
                    continue;
                }
                // Check for ghci exit condition.
                else if (cmd.startsWith(":q")) {
                    ahi.setState(GHCiState.QUITTING);
                    
                    ghciInput.write(cmd + '\n');
                    ghciInput.flush();
                    break;
                }
                // Check for ghci load condition.
                else if (cmd.startsWith(":l")) {
                    ahi.setState(GHCiState.LOADING);
                    
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
                    
                    // Wait for GHCi to finish outputting result.
                    while (ahi.isPrinting()) {
                        ;
                    }
                    ahi.setInput(cmd);
                    
                    // Ask if output is correct, and if user wants to save test.
                    isCorrect();
                    saveTest();
                    
                    ahi.setBlocking(false);
                }
                else {
                    // Send command to ghci.
                    ghciInput.write(cmd + '\n');
                    ghciInput.flush();
                }
            }
            // GHCi has exited, so close all I/O streams.
            scanner.close();
            ghciInput.close();
        }
        catch (IOException e) {
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
                ahi.setTestType(true);
                break loop;
            case "n":
                ahi.setTestType(false);
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
                ahi.writeTest(ghciInput);
                break loop;
            case "n":
                ahi.reset();
                break loop;
            default:
                System.out.print("Please answer with [y] or [n] > ");
                continue;
            }
        }
        System.out.print(ahi.getPrompt());
    }
}
