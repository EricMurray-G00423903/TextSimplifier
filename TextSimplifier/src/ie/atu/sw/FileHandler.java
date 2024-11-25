package ie.atu.sw;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * FileHandler Class - Handles the loading and processing of `.txt` files 
 * into appropriate data structures such as Maps for embeddings and Google words.
 * 
 * This class includes methods for:
 * - Loading embeddings into a Map.
 * - Loading a filtered Google word set with corresponding embeddings.
 * - Validating file paths to ensure correctness.
 * 
 * @author Eric Murray - G00423903
 * 
 */
public class FileHandler {
	
	/**
     * Loads the embeddings from the specified file path into a Map.
     * 
     * Each line in the file should be formatted as: 
     * word, vectorvalues[]
     * 
     * @param path to the embeddings file.
     * @return a Map where keys are words and values are their corresponding embedding vectors.
     * @throws IOException if an I/O error occurs during file reading.
     * 
     * Big-O Notation
     * O(n * m)
     * n: Number of words
     * m: Number of vectors per word
     */
	
	public static Map<String, double[]> loadEmbeddings(String path) throws IOException {
		
		Map<String, double[]> embeddingsMap = new ConcurrentHashMap<>();	//Map of embeddings to populate and return
		
		try (BufferedReader reader = new BufferedReader(new FileReader(path))) { 	//BufferedReader for reading the lines of the file
		
			String line = reader.readLine();	//String to hold the current line in the file
			
			if(line == null) {
				
				System.out.println(ConsoleColour.RED + "Embeddings Map.txt File Empty - Using Empty Map" + ConsoleColour.RESET);
				return embeddingsMap;
			}
			
			do {	//Read the current line into the line String, continue with the loop if its not Null
				
				String[] lineParts = line.split(",");	//Split the line String into an array split by commas
				
				double[] vectorValues = new double[lineParts.length - 1];	//double array to store the vector values of each line same length as the linePart array
				
				for (int i = 1; i < lineParts.length; i++) {	//Iterate over the lineParts array starting at index 1 (index 0  is the word)
					
					double temp = Double.parseDouble(lineParts[i]);	//Add the double to a temp variable, parse as double
					vectorValues[i - 1] = temp;	//Add the vector to the double[] array (i-1 to control going out of bounds)
					
				}
				
				embeddingsMap.put(lineParts[0], vectorValues);	//After all the vectors have been added, add the word as the key and the vectorValues[] as the value to the CCHashMap
				
			} while((line = reader.readLine()) != null);
			
			return embeddingsMap;	//Return the finished map
			
		}
			
	}
	
	/**
     * Loads the Google words from the specified file path and maps them to 
     * their corresponding embeddings from the provided embeddings Map.
     * 
     * Only words that exist in the embeddings Map will be included.
     * 
     * @param path to the Google words file.
     * @param embMap the embeddings Map containing word vectors.
     * @return a Map of Google words with their corresponding embeddings.
     * @throws IOException if an I/O error occurs during file reading.
     * 
     * Big-O Notation
     *O(n)
     *n: Number of words in the google list
     */
	
	public static Map<String, double[]> loadGoogle(String path, Map<String, double[]> embMap) throws IOException {
		
		Map<String, double[]> googleWords = new ConcurrentHashMap<>();	//CCHashMap of the Google words with embeddings
		
		try (BufferedReader reader = new BufferedReader(new FileReader(path))) {	//BufferedFileReader for the passed path | Wrap in try - to close even on exception thrown
		
			String line = reader.readLine();	//String to hold the current word | Read the first line to check for Empty Files
			
			if(line == null) {
				
				System.out.println(ConsoleColour.RED + "Google Words.txt File Empty - Using Empty Map" + ConsoleColour.RESET);
				return googleWords;
				
			}
			
			do {	// Process each line, trim whitespaces, add to map if matches
				
				line = line.trim();
				
				if(embMap.containsKey(line)) {
					googleWords.put(line, embMap.get(line));
				}
				
			} while((line = reader.readLine()) != null);
						
			return googleWords;	
			
		}	//end of try block
		
	}
	
	/**
     * Validates the provided file path to ensure it is not null, non-empty,
     * ends with `.txt`, and refers to an existing readable file.
     * 
     * @param path the path to validate.
     * @throws IllegalArgumentException if the path is invalid.
     * 
     * Big-O Notation
     * O(1)
     */
	
	static void pathScrub(String path) {
				
		if(path == null || path.trim().isEmpty()) {
			throw new IllegalArgumentException("The file path cannot be null or empty!");	//If the path is null or String is empty throw exception
		}
		
		if(!path.endsWith(".txt")) {
			throw new IllegalArgumentException("File needs to end in .txt");	//If the String path does not end with .txt throw exception
		}
		
		File file = new File(path);	//File object for exists() & canRead() methods on the String path
		
		if(!file.exists() || !file.canRead()) {
			throw new IllegalArgumentException("File does not exist OR Don't have permission to read file!");	//If we cant access the file or it doesnt exist throw exception
		}
					
	}

}
