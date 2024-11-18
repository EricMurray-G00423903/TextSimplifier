package ie.atu.sw;

//Imports
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

public class Menu {
	
	//Private instances for file locations
    private String embeddingsFile;
    private String googleFile;
    private String inputFile;
    private String outputFile = "./out.txt";

    public void run() {	//Run method to handle all the menu sub methods and keep looping
    	
        Scanner scanner = new Scanner(System.in);
        while (true) {
            displayMenu();
            handleUserInput(scanner);
        }
        
    }

    private void displayMenu() {		//Method for displaying the menu options block
    	
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

    private void handleUserInput(Scanner scanner) {	//Method for handling the users selection
    	
        System.out.print("Select Option [1-9]> ");
        String choice = scanner.nextLine();
        switch (choice) {		//Switch statement to route to the correct methods
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
                System.out.println(ConsoleColour.RESET);	//Reset console colour before exiting
                System.exit(0);
            default:
                System.out.println("Invalid option. Please try again.");
        }
        
    }

    private void setEmbeddingsFile() {		//Specify paths for Embedding file, google, input and output
    	
        System.out.print("Enter path for embeddings file: ");
        Scanner scanner = new Scanner(System.in);
        this.embeddingsFile = scanner.nextLine();
        System.out.println("Embeddings file set to: " + this.embeddingsFile);

        try {
            Thread.sleep(1000); // Sleep for 1 second
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupted status
        }
        
        clearScreen(); // Clear the console
        
    }

    private void setGoogleFile() {		//Same method as above for google
    	
        System.out.print("Enter path for Google 1000 file: ");
        Scanner scanner = new Scanner(System.in);
        this.googleFile = scanner.nextLine();
        System.out.println("Google 1000 file set to: " + this.googleFile);
        
        try {
            Thread.sleep(1000); // Sleep for 1 second
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupted status
        }
        
        clearScreen(); // Clear the console
        
    }

    private void setOutputFile() {		//Same method as above for output
    	
        System.out.print("Enter path for output file: ");
        Scanner scanner = new Scanner(System.in);
        this.outputFile = scanner.nextLine();
        System.out.println("Output file set to: " + this.outputFile);

        try {
            Thread.sleep(1000); // Sleep for 1 second
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupted status
        }
        
        clearScreen(); // Clear the console
        
    }
    
	private void setInputFile() {		//Same method as above for input
    	
        System.out.print("Enter path for input file: ");
        Scanner scanner = new Scanner(System.in);
        this.inputFile = scanner.nextLine();
        System.out.println("Input file set to: " + this.inputFile);

        try {
            Thread.sleep(1000); // Sleep for 1 second
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupted status
        }
        
        clearScreen(); // Clear the console
        
    }

    private void executeSimplification() {
    	
        try {
        	
            // Validate that file paths are set
            if (embeddingsFile == null || googleFile == null || outputFile == null) {
                System.out.println(ConsoleColour.RED + "Error: File paths must be set before executing." + ConsoleColour.RESET);
                return;
            }
            
            clearScreen();	//Clear screen to only show the progress bar
            
            System.out.println("Simplifying text file....");
            displayProgressBar(100);	//Displaying progress bar for 100

            // Load embeddings and Google map
            Map<String, double[]> embeddingsMap = FileHandler.loadEmbeddings(embeddingsFile);
            Map<String, double[]> googleMap = FileHandler.loadGoogle(googleFile, embeddingsMap);

            // Create TextProcessor and simplify text
            TextProcessor processor = new TextProcessor(googleMap, embeddingsMap);
            processor.simplifyText(inputFile, outputFile);

            System.out.println(ConsoleColour.GREEN + " Simplification complete!" + ConsoleColour.RESET);
            
        } catch (IOException e) {
        	
            System.out.println(ConsoleColour.RED + "Error during execution: " + e.getMessage() + ConsoleColour.RESET);
            
        }
        
    }
    
    public void displayProgressBar(int duration) {		//Display progress bar
    	
        System.out.print(ConsoleColour.GREEN);
        for (int i = 0; i < duration; i++) {
            try {
                printProgress(i + 1, duration);
                Thread.sleep(10);
            } catch (InterruptedException e) {
                System.out.println(ConsoleColour.RED + "Progress interrupted." + ConsoleColour.RESET);
                Thread.currentThread().interrupt(); // Restore interrupted status
                break; // Stop the loop if interrupted
            }
        }
    }

    private void printProgress(int index, int total) {	//Ripped from starter code snippet
    	
		if (index > total) return;	//Out of range
        int size = 50; 				//Must be less than console width
	    char done = '█';			//Change to whatever you like.
	    char todo = '░';			//Change to whatever you like.
	    
	    //Compute basic metrics for the meter
        int complete = (100 * index) / total;
        int completeLen = size * complete / 100;
        
        /*
         * A StringBuilder should be used for string concatenation inside a 
         * loop. However, as the number of loop iterations is small, using
         * the "+" operator may be more efficient as the instructions can
         * be optimized by the compiler. Either way, the performance overhead
         * will be marginal.  
         */
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < size; i++) {
        	sb.append((i < completeLen) ? done : todo);
        }
        
        /*
         * The line feed escape character "\r" returns the cursor to the 
         * start of the current line. Calling print(...) overwrites the
         * existing line and creates the illusion of an animation.
         */
        System.out.print("\r" + sb + "] " + complete + "%");
        
        //Once the meter reaches its max, move to a new line.
        if (done == total) System.out.println("\n");
    }
    
    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

}
