package ie.atu.sw;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FileHandler {
	
	public static Map<String, double[]> loadEmbeddings(String path) {
		
		Map<String, double[]> embeddingsMap = new HashMap<>();
		
		return embeddingsMap;
		
	}
	
	private static void pathScrub(String path) {
				
		//Handle Edge Cases
		if(path == null || path.trim().isEmpty()) {
			throw new IllegalArgumentException("The file path cannot be null or empty!");
		}
		
		File file = new File(path);
		
		if(!file.exists() || !file.canRead()) {
			throw new IllegalArgumentException("File does not exist OR Don't have permission to read file!");
		}
		
		if(!path.endsWith(".txt")) {
			throw new IllegalArgumentException("File needs to end in .txt");
		}
				
	}

}
