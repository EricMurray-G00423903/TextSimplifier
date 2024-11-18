package ie.atu.sw;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TextProcessor {
	
	Map<String, double[]> googleWordsMap = new ConcurrentHashMap<>();	//CCHashMaps for both Embeddings Map and Google Map
	Map<String, double[]> embeddingsMap = new ConcurrentHashMap<>();
	
	public TextProcessor(Map<String, double[]> googleWordsMap, Map<String, double[]> embeddingsMap) {	//Constructor to assign maps
		
		this.googleWordsMap = googleWordsMap;
		this.embeddingsMap = embeddingsMap;
		
	}
	
	public void simplifyText(String inputPath, String outputPath) throws IOException {
		
		try (BufferedReader reader = new BufferedReader(new FileReader(inputPath));		//Wrapped Buffered reader to close even if exception thrown
			 BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
		
			String line;
			
			while((line = reader.readLine()) != null) {
				
				if (line.trim().isEmpty()) {	//Handle if the current Line is Empty
				    writer.newLine();
				    continue;
				}
				
				String[] splitLines = line.split("((?=\\p{Punct})|(?<=\\p{Punct})|\\s+)");	//Regex to split lines by punctuation and by white spaces
				
				StringBuilder sb = new StringBuilder();	//Create a String Builder
				
				for (int i = 0; i < splitLines.length; i++) { // Iterate over the split Line

				    if (splitLines[i].matches("\\p{Punct}+")) {
				    	
				        sb.append(splitLines[i]); // Append punctuation directly without a space
				        continue; // Move to the next iteration after handling punctuation
				        
				    }

				    if (this.googleWordsMap.containsKey(splitLines[i])) {	// If the word is in the Google Map
				    	
				        sb.append(splitLines[i]).append(" "); // Append the word with a space
				        
				    } else { // If it's not in the Google Map
				    	
				        if (this.embeddingsMap.containsKey(splitLines[i])) { // If it's in the embeddings map
				        	
				            String newWord = findClosestWord(splitLines[i]); // Find the closest word
				            sb.append(newWord).append(" "); // Append the new word with a space
				            
				        } else { // If the word doesn't exist in either map
				        	
				            sb.append(splitLines[i]).append(" "); // Append the word as-is with a space
				            
				        }
				    }
				}
				
				writer.write(sb.toString().trim());	//write the current sb String to the line
				writer.newLine();				//Move to the next line
				
			}
		}
		
	}
	
	public String findClosestWord(String wordToChange) {	//Pass in the current word to change
				
		String closestWord = null;	//Init the word to return
		double highestSimilarity = Double.NEGATIVE_INFINITY;	//To hold the score for the highest scored similarity (Double.NEGATIVE_INFINITY) 
																//Sets the placeholder to an ultimate negative to incase the cosine returns negative
		
		double[] wordVector = embeddingsMap.get(wordToChange);	//Get the vectors of the current word to change
		
		for (Map.Entry<String, double[]> entry : googleWordsMap.entrySet()) {	//Foreach loop over all the elements in the google map hashmap
			
			String currentWord = entry.getKey();	//Get the current word
			double[] currentVector = entry.getValue();	//Get its vectors
			
			double currentCosine = calcCosineSim(wordVector, currentVector);	//Get the cosine similarity between the two sets of vectors
			
			if (currentCosine > highestSimilarity) {	//If the current cosine is higher than the highest sim 
				
				highestSimilarity = currentCosine;	//Assign the highest sim to the current
				closestWord = entry.getKey();	//Set the closest word to that of the current key
				
			}
			
		}
		
		return closestWord;	//Return the closest word
		
	}
	
	public static double calcCosineSim(double[] vectorA, double[] vectorB) {	//Calculate the cosine similarity between the two vectors and return the sim value
		
		if(vectorA.length != vectorB.length) {
			throw new IllegalArgumentException("Must be the same amount of Vectors to calculate!");	//Handle if the vector lengths are not the same
		}
		
		double dotProduct = 0.0, magA = 0.0, magB = 0.0;	//Placeholder for the dot product, the two magnitudes
		
		for (int i = 0; i < vectorA.length; i++) {	//Loop over the vector array
			
		    dotProduct += vectorA[i] * vectorB[i];	//Multiply the two current vectors together and add them to the dotProduct
		    magA += vectorA[i] * vectorA[i];	//Same method for mag A & B
		    magB += vectorB[i] * vectorB[i];

		}

		magA = Math.sqrt(magA);	//Square root the values and reassign
		magB = Math.sqrt(magB);
		
		if (magA == 0.0 || magB == 0.0) {
			
		    return 0.0;	//If either magnitudes are 0.0 return 0.0
		    
		}
		
		return dotProduct / (magA * magB);	//return the Dot Product over MagA * MagB
		
	}

}
