package ie.atu.sw;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

/**
 * TextProcessor Class - Processes input text files by simplifying words based on
 * embeddings and a predefined Google word list. This class handles:
 * - Simplifying text files by replacing words not in the Google Map with their closest match.
 * - Calculating cosine similarity for word vector comparison.
 * - Finding the closest matching word in the Google Map.
 * 
 * @author Eric Murray - G00423903
 * 
 */
public class TextProcessor {
	
	Map<String, double[]> googleWordsMap = new ConcurrentHashMap<>();	//CCHashMaps for both Embeddings Map and Google Map
	Map<String, double[]> embeddingsMap = new ConcurrentHashMap<>();
	
	/**
     * Constructs a TextProcessor with the specified Google Map and embeddings Map.
     * 
     * @param googleWordsMap a Map of Google words with their corresponding embeddings.
     * @param embeddingsMap a Map of embeddings for all words.
     */
	
	public TextProcessor(Map<String, double[]> googleWordsMap, Map<String, double[]> embeddingsMap) {	//Constructor to assign maps
		
		this.googleWordsMap = googleWordsMap;
		this.embeddingsMap = embeddingsMap;
		
	}
	
	/**
	 * Simplifies the input text file by replacing words not in the Google Map 
	 * with their closest match based on cosine similarity. If a word is not found 
	 * in either the Google Map or the embeddings map, it is left unchanged.
	 * The simplified text is then written to the output file.
	 * 
	 * **Virtual Threads**:
	 * Uses virtual threads to process each word concurrently, improving scalability and 
	 * performance when handling large input files.
	 * 
	 * @param inputPath the path to the input text file.
	 * @param outputPath the path to the output text file.
	 * @throws IOException if an I/O error occurs during file reading or writing.
	 * 
	 * **Big-O Time Complexity**:
	 * - Processing each word: O(k * d), where `k` is the number of words in the Google Map, 
	 *   and `d` is the size of the word vectors.
	 * - For the entire file: O(l * W * k * d), where `l` is the number of lines in the input file, 
	 *   and `W` is the average number of words per line.
	 */
	public void simplifyText(String inputPath, String outputPath) throws IOException {
	    try (BufferedReader reader = new BufferedReader(new FileReader(inputPath));
	         BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {

	        var executor = Executors.newThreadPerTaskExecutor(Thread.ofVirtual().factory());

	        String line;
	        while ((line = reader.readLine()) != null) {
	            if (line.trim().isEmpty()) {
	                writer.newLine();
	                continue;
	            }

	            String[] splitLines = line.split("((?=\\p{Punct})|(?<=\\p{Punct})|\\s+)");
	            StringBuilder sb = new StringBuilder();

	            for (String word : splitLines) {
	                executor.execute(() -> {
	                    if (googleWordsMap.containsKey(word)) {
	                        sb.append(word).append(" ");
	                    } else if (embeddingsMap.containsKey(word)) {
	                        String newWord = findClosestWord(word);
	                        sb.append(newWord).append(" ");
	                    } else {
	                        sb.append(word).append(" ");
	                    }
	                });
	            }

	            writer.write(sb.toString().trim());
	            writer.newLine();
	        }
	        executor.close();
	    }
	}

	
	/**
     * Finds the closest word in the Google Map to the specified word 
     * based on cosine similarity of their vector representations.
     * 
     * @param wordToChange the word to find a replacement for.
     * @return the closest matching word from the Google Map.
     * 
     * Big-O Notation
     * O(k)
     * k: number of entries in the googleMap
     */
	
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
	
	/**
     * Calculates the cosine similarity between two vectors.
     * 
     * @param vectorA the first vector to compare.
     * @param vectorB the second vector to compare.
     * @return the cosine similarity between the two vectors.
     * @throws IllegalArgumentException if the vectors have different lengths.
     * 
     * Big-O Notation
     * O(n)
     * n: the number of vectors in each array
     */
	
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
