package ie.atu.sw;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FileHandler {
	
	public static Map<String, double[]> loadEmbeddings(String path) throws IOException {
		
		pathScrub(path);	//Scrub the path to handle any errors before method continues
		
		Map<String, double[]> embeddingsMap = new ConcurrentHashMap<>();	//Map of embeddings to populate and return
		BufferedReader reader = new BufferedReader(new FileReader(path));	//BufferedReader for reading the lines of the file
		
		String line;	//String to hold the current line in the file
		
		while((line = reader.readLine()) != null) {	//Read the current line into the line String, continue with the loop if its not Null
			
			String[] lineParts = line.split(",");	//Split the line String into an array split by commas
			
			double[] vectorValues = new double[lineParts.length - 1];	//double array to store the vector values of each line same length as the linePart array
			
			for (int i = 1; i < lineParts.length; i++) {	//Iterate over the lineParts array starting at index 1 (index 0  is the word)
				
				double temp = Double.parseDouble(lineParts[i]);	//Add the double to a temp variable, parse as double
				vectorValues[i - 1] = temp;	//Add the vector to the double[] array (i-1 to control going out of bounds)
				
			}
			
			embeddingsMap.put(lineParts[0], vectorValues);	//After all the vectors have been added, add the word as the key and the vectorValues[] as the value to the CCHashMap
			
		}
		
		reader.close();	//Close the reader after all the embeddings have been loaded
		
		return embeddingsMap;	//Return the finished map
		
	}
	
	private static void pathScrub(String path) {
				
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
