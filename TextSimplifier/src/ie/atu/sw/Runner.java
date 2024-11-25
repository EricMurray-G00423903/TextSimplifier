package ie.atu.sw;

/**
 * Runner Class - Main class just used to run the program from here
 * Only exposed to the Menu class
 * 
 * @author Eric Murray - G00423903
 * @version 1.0
 * 
 */
public class Runner {

	public static void main(String[] args) throws Exception {
		
		Menu menu = new Menu();
		menu.run();	//Call on the menu to run
		
	}
	
}