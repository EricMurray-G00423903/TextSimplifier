package ie.atu.sw;

import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;


/**
 * Menu Class - Handles the user interaction via a console menu interface.
 * 
 * This class provides the functionality for:
 * - Displaying menu options to the user.
 * - Allowing the user to specify file paths for embeddings, Google words, input, and output files.
 * - Validating file paths using the `FileHandler.pathScrub` method.
 * - Executing the text simplification process.
 * - Displaying a progress bar for execution feedback.
 * @author Eric Murray - G00423903
 * 
 */
public class Menu {

    private String embeddingsFile;
    private String googleFile;
    private String inputFile;
    private String outputFile = "./out.txt";
    private final Scanner scanner = new Scanner(System.in); // Single scanner instance for the class

    /**
     * Runs the menu loop, continuously displaying the menu and handling user input.
     * This method keeps the program interactive until the user exits.
     */
    public void run() {
        while (true) {
            displayMenu();
            handleUserInput();
        }
    }

    /**
     * Displays the main menu with available options for the user.
     */
    private void displayMenu() {
        System.out.println(ConsoleColour.YELLOW_BOLD_BRIGHT);
        System.out.println("************************************************************");
        System.out.println("*     ATU - Dept. of Computer Science & Applied Physics    *");
        System.out.println("*             Virtual Threaded Text Simplifier             *");
        System.out.println("************************************************************");
        System.out.println("(1) Specify Embeddings File");
        System.out.println("(2) Specify Google 1000 File");
        System.out.println("(3) Specify an Output File (default: ./out.txt)");
        System.out.println("(4) Specify an Input File (This is the file that will be simplified)");
        System.out.println("(5) Execute, Analyse and Report");
        System.out.println("(9) Quit");
    }
    
    /**
     * Handles user input and routes it to the appropriate methods 
     * based on the user's choice.
     */
    private void handleUserInput() {
    	
        System.out.print("Select Option [1-9]> ");
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                setEmbeddingsFile();
                break;
            case "2":
                setGoogleFile();
                break;
            case "3":
                setOutputFile();
                break;
            case "4":
                setInputFile();
                break;
            case "5":
                executeSimplification();
                break;
            case "9":
                System.out.println("Exiting...");
                System.out.println(ConsoleColour.RESET);
                System.exit(0);
            default:
                System.out.println("Invalid option. Please try again.");
                pauseAndClear();
        }
        
    }

    /**
     * Sets the file path for the embeddings file.
     * Validates the file path and provides feedback on success or failure.
     */
    private void setEmbeddingsFile() {
    	
        System.out.print("Enter path for embeddings file: ");
        String input = scanner.nextLine().trim();
        
        if (input.isEmpty()) {
        	
            System.out.println(ConsoleColour.RED + "File path cannot be empty!" + ConsoleColour.RESET);
            pauseAndClear();
            return;
            
        }
        
        this.embeddingsFile = input;

        try {
            FileHandler.pathScrub(this.embeddingsFile); // Validate the file path
            System.out.println("Embeddings file set to: " + this.embeddingsFile);
        } catch (IllegalArgumentException e) {
            System.out.println(ConsoleColour.RED + e.getMessage() + ConsoleColour.RESET);
            this.embeddingsFile = null; // Reset if invalid
        }

        pauseAndClear();
        
    }

    /**
     * Sets the file path for the Google words file.
     * Validates the file path and provides feedback on success or failure.
     */
    private void setGoogleFile() {
    	
        System.out.print("Enter path for Google 1000 file: ");
        String input = scanner.nextLine().trim();
        
        if (input.isEmpty()) {
        	
            System.out.println(ConsoleColour.RED + "File path cannot be empty!" + ConsoleColour.RESET);
            pauseAndClear();
            return;
            
        }
        
        this.googleFile = input;

        try {
        	
            FileHandler.pathScrub(this.googleFile); // Validate the file path
            System.out.println("Google 1000 file set to: " + this.googleFile);
            
        } catch (IllegalArgumentException e) {
        	
            System.out.println(ConsoleColour.RED + e.getMessage() + ConsoleColour.RESET);
            this.googleFile = null; // Reset if invalid
            
        }

        pauseAndClear();
        
    }

    /**
     * Sets the file path for the output file.
     * If no path is specified, it defaults to "./out.txt".
     */	
    private void setOutputFile() {
    	
        System.out.print("Enter path for output file (default: ./out.txt): ");
        String input = scanner.nextLine().trim();
        
        this.outputFile = input.isEmpty() ? "./out.txt" : input;
        System.out.println("Output file set to: " + this.outputFile);

        pauseAndClear();
        
    }
    
    /**
     * Sets the file path for the input file to be simplified.
     * Validates the file path and provides feedback on success or failure.
     */
    private void setInputFile() {
    	
        System.out.print("Enter path for input file: ");
        String input = scanner.nextLine().trim();
        
        if (input.isEmpty()) {
        	
            System.out.println(ConsoleColour.RED + "File path cannot be empty!" + ConsoleColour.RESET);
            pauseAndClear();
            return;
            
        }
        
        this.inputFile = input;

        try {
        	
            FileHandler.pathScrub(this.inputFile); // Validate the file path
            System.out.println("Input file set to: " + this.inputFile);
            
        } catch (IllegalArgumentException e) {
        	
            System.out.println(ConsoleColour.RED + e.getMessage() + ConsoleColour.RESET);
            this.inputFile = null; // Reset if invalid
            
        }

        pauseAndClear();
        
    }

    /**
     * Executes the text simplification process using virtual threads.
     * - Validates all file paths.
     * - Loads embeddings and Google map concurrently using virtual threads.
     * - Invokes the `TextProcessor` to simplify the input file.
     * - Provides feedback on success or failure.
     * 
     * Big-O Notation
     * O(m * d + k + l * W)
     * m: number of words in embedding file
     * d: average amount of embeddings
     * k: number of words in the google list
     * l: number of lines in the input file
     * W: average number of words per line
     * 
     */
    private void executeSimplification() {
        try {
            // Validate file paths
            if (embeddingsFile == null || googleFile == null || inputFile == null || outputFile == null) {
                System.out.println(ConsoleColour.RED + "Error: File paths must be set before executing." + ConsoleColour.RESET);
                return;
            }

            clearScreen();
            System.out.println("Simplifying text file...");
            displayProgressBar(50); // Simulate progress for loading tasks

            // Variables to store results
            final Map<String, double[]> embeddingsMap;
            final Map<String, double[]> googleMap;

            // Use virtual threads to load embeddings and Google words concurrently
            try {
                // Load embeddings
                CompletableFuture<Map<String, double[]>> embeddingsFuture = CompletableFuture.supplyAsync(() -> {
                    try {
                        return FileHandler.loadEmbeddings(embeddingsFile);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to load embeddings: " + e.getMessage(), e);
                    }
                });

                // Load Google map (dependent on embeddings)
                CompletableFuture<Map<String, double[]>> googleFuture = embeddingsFuture.thenApplyAsync(embeddings -> {
                    try {
                        return FileHandler.loadGoogle(googleFile, embeddings);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to load Google words: " + e.getMessage(), e);
                    }
                });

                // Wait for results
                embeddingsMap = embeddingsFuture.join();
                googleMap = googleFuture.join();

            } catch (Exception e) {
                System.out.println(ConsoleColour.RED + "Error during file loading: " + e.getMessage() + ConsoleColour.RESET);
                return;
            }

            // Process the input file with the TextProcessor
            TextProcessor processor = new TextProcessor(googleMap, embeddingsMap);
            processor.simplifyText(inputFile, outputFile);

            // Final success message
            System.out.println(ConsoleColour.GREEN + "Simplification complete!" + ConsoleColour.RESET);

        } catch (Exception e) {
            System.out.println(ConsoleColour.RED + "Unexpected error during execution: " + e.getMessage() + ConsoleColour.RESET);
        }
    }



    /**
     * Displays a progress bar in the terminal to give execution feedback.
     * 
     * @param duration the number of steps to complete the progress bar (e.g., 100).
     */
    public void displayProgressBar(int duration) {
    	
        System.out.print(ConsoleColour.GREEN);
        for (int i = 0; i < duration; i++) {
            try {
                printProgress(i + 1, duration);
                Thread.sleep(10);
            } catch (InterruptedException e) {
                System.out.println(ConsoleColour.RED + "Progress interrupted." + ConsoleColour.RESET);
                Thread.currentThread().interrupt();
                break;
            }
        }
        
    }

    /**
     * Prints the progress bar for the current step.
     * 
     * @param index the current step number.
     * @param total the total number of steps for the progress bar.
     */
    private void printProgress(int index, int total) {
    	
        if (index > total) return; // Out of range
        int size = 50; // Must be less than console width
        char done = '█';
        char todo = '░';

        int complete = (100 * index) / total;
        int completeLen = size * complete / 100;

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < size; i++) {
            sb.append((i < completeLen) ? done : todo);
        }
        System.out.print("\r" + sb + "] " + complete + "%");

        if (index == total) System.out.println("\n");
    }

    /**
     * Clears the console screen for a cleaner user experience.
     */
    private void clearScreen() {
    	
        System.out.print("\033[H\033[2J");
        System.out.flush();
        
    }

    /**
     * Pauses execution for a short duration (e.g., 1 second) 
     * and then clears the screen.
     */
    private void pauseAndClear() {
    	
        try {
            Thread.sleep(1000); // Sleep for 1 second
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        clearScreen();
        
    }
}
