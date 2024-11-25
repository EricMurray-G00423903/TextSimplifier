package ie.atu.sw;

import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

public class Menu {

    private String embeddingsFile;
    private String googleFile;
    private String inputFile;
    private String outputFile = "./out.txt";
    private final Scanner scanner = new Scanner(System.in); // Single scanner instance for the class

    public void run() {
        while (true) {
            displayMenu();
            handleUserInput();
        }
    }

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

    private void setOutputFile() {
    	
        System.out.print("Enter path for output file (default: ./out.txt): ");
        String input = scanner.nextLine().trim();
        
        this.outputFile = input.isEmpty() ? "./out.txt" : input;
        System.out.println("Output file set to: " + this.outputFile);

        pauseAndClear();
        
    }

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

    private void executeSimplification() {
    	
        try {
            // Validate that all required paths are set
            if (embeddingsFile == null || googleFile == null || inputFile == null || outputFile == null) {
                System.out.println(ConsoleColour.RED + "Error: File paths must be set before executing." + ConsoleColour.RESET);
                return;
            }

            clearScreen();
            System.out.println("Simplifying text file...");
            displayProgressBar(100); // Display progress bar

            // Load embeddings and Google map
            Map<String, double[]> embeddingsMap = FileHandler.loadEmbeddings(embeddingsFile);
            Map<String, double[]> googleMap = FileHandler.loadGoogle(googleFile, embeddingsMap);

            // Create TextProcessor and simplify text
            TextProcessor processor = new TextProcessor(googleMap, embeddingsMap);
            processor.simplifyText(inputFile, outputFile);

            System.out.println(ConsoleColour.GREEN + "Simplification complete!" + ConsoleColour.RESET);
        } catch (IOException e) {
        	
            System.out.println(ConsoleColour.RED + "Error during execution: " + e.getMessage() + ConsoleColour.RESET);
            
        }
    }

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

    private void clearScreen() {
    	
        System.out.print("\033[H\033[2J");
        System.out.flush();
        
    }

    private void pauseAndClear() {
    	
        try {
            Thread.sleep(1000); // Sleep for 1 second
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        clearScreen();
        
    }
}
