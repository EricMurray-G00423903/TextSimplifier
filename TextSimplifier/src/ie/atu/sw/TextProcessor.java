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
				
				String[] splitLines = line.split("((?=\\p{Punct})|(?<=\\p{Punct})|\\s+)");	//Regex to split lines by punctuation and by white spaces
				
				StringBuilder sb = new StringBuilder();	//Create a String Builder
				
				for (int i = 0; i < splitLines.length; i++) {	//Iterate over the split Line
					
					if (splitLines[i].matches("\\p{Punct}+")) {
						sb.append(splitLines[i] + " ");		//If the current string[] index is a punctation char then just append to the stringbuilder
					}
					
					if (this.googleWordsMap.containsKey(splitLines[i])) {	//If the word already in the google Map then just append to the sb
						
						sb.append(splitLines[i] + " ");	//append with a whitespace after the word
						
					} else {	//if its not in the google Map
						
						if (this.embeddingsMap.containsKey(splitLines[i])) {	//and it is in the embeddings map
							
							String newWord = findClosestWord(splitLines[i]);	//find the closest word within the googleMap
							sb.append(newWord + " ");	//append the new word from the google map to the sb
							
						} else {
							
							sb.append(splitLines[i] + " ");	//else if the word doesnt exist in either map just append
							
						}
						
					}
					
				}
				
				writer.write(sb.toString());	//write the current sb String to the line
				writer.newLine();				//Move to the next line
				
			}
		}
		
	}
	
	public String findClosestWord(String wordToChange) {
		
		String closestWord = null;
		
		return closestWord;
		
	}

}
