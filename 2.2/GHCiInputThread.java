import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Scanner;

/**
 * Thread that deals with sending user commands to the GHCi.
 *
 * @author Kevin Ly
 * @version 2.2 (10/02/2014)
 */
public class GHCiInputThread extends Thread
{
    private BufferedWriter ghciInput;
    private Scanner scanner;
    private TestWriter tw;

    /**
     * Constructor for Thread.
     * @param name The name given to this thread.
     * @param out The output stream used to write to the GHCi.
     * @param tw The TestWriter class that will record tests.
     */
    public GHCiInputThread(String name, OutputStreamWriter out, TestWriter tw)
    {
        super(name);
        ghciInput = new BufferedWriter(out);
        this.tw = tw;
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
                    tw.setRecording(!tw.isRecording());
                    continue;
                }
                // Check for ghci exit condition.
                else if (cmd.startsWith(":q")) {
                    tw.setState(GHCiState.QUITTING);
                    
                    ghciInput.write(cmd + '\n');
                    ghciInput.flush();
                    break;
                }
                // Check for ghci load condition.
                else if (cmd.startsWith(":l")) {
                    tw.setState(GHCiState.LOADING);
                    
                    String filePath = removeCommand(cmd);
                    tw.setFilePath(filePath);
                }

                // Check if test logging is on.
                if (tw.isRecording()) {
                    tw.setBlocking(true);    // Prevents the GHCi prompt being being printed prematurely.
                    
                    // Send command to ghci.
                    ghciInput.write(cmd + '\n');
                    ghciInput.flush();
                    tw.setPrinting(true);
                    
                    // Wait for GHCi to finish outputting result.
                    while (tw.isPrinting()) {
                        ;
                    }
                    tw.setInput(cmd);
                    
                    // Ask if output is correct, and if user wants to save test.
                    isCorrect();
                    saveTest();
                    
                    tw.setBlocking(false);
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
        System.out.print("Is output correct? [y/n]\n> ");

        loop: while (true) {
            String answer = scanner.nextLine().trim().toLowerCase();
            
            // Check what the user has answered.
            switch (answer) {
            case "y":
                tw.setTestType(true);
                break loop;
            case "n":
                tw.setTestType(false);
                break loop;
            default:
                System.out.print("Please answer with y or n\n> ");
                continue;
            }
        }
    }

    /**
     * Ask if user wants to save test.
     */
    public void saveTest()
    {
        System.out.print("Save test? [y/n]\n> ");
        
        loop: while (true) {
            String answer = scanner.nextLine().trim().toLowerCase();
            
            // Check what the user has answered.
            switch (answer) {
            case "y":
                tw.writeTest(ghciInput);
                break loop;
            case "n":
                tw.reset();
                break loop;
            default:
                System.out.print("Please answer with y or n\n> ");
                continue;
            }
        }
    }
}
