package ie.atu.sw;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TextProcessor {
	
	Map<String, double[]> embeddings = new ConcurrentHashMap<>();
	Set<String> googleWords = new HashSet<>();
	
	public TextProcessor(Map<String, double[]> embeddings, Set<String> googleWords) {
		
		this.embeddings = embeddings;
		this.googleWords = googleWords;
		
	}
	
	public static void simplifyText(String inputPath, String outputPath, Map<String, double[]> embMap, Set<String> googleSet) throws IOException {
		
		BufferedReader reader = new BufferedReader(new FileReader(inputPath));
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath));
		
		String line;
		
		while((line = reader.readLine()) != null) {
			
			String[] splitLines = line.split("((?=\\p{Punct})|(?<=\\p{Punct})|\\s+)");
			
			StringBuilder sb = new StringBuilder();
			
			for (int i = 0; i < splitLines.length; i++) {
				
				if (splitLines[i].matches("\\p{Punct}+")) {
					sb.append(splitLines[i]);
				}
				
				if (googleSet.contains(splitLines[i])) {
					
					sb.append(splitLines[i] + " ");
					
				} else {
					//implement find closest match from embeddings
				}
				
			}
			
		}
		
		
		reader.close();
		writer.close();
		
	}

}
