package bgu.spl.mics.application.passiveObjects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 *  That's where Q holds his gadget (e.g. an explosive pen was used in GoldenEye, a geiger counter in Dr. No, etc).
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Inventory {
	private List<String> gadgets;
	private static Inventory inventoryInstance = null;

	/**
	 * constructor
	 */

	private Inventory(){
		gadgets = new LinkedList<String>();
	}

	/**
     * Retrieves the single instance of this class.
     */

	public static Inventory getInstance() {
		if (inventoryInstance == null)
			inventoryInstance = new Inventory();
		return inventoryInstance;
	}

	/**
     * Initializes the inventory. This method adds all the items given to the gadget
     * inventory.
     * <p>
     * @param inventory 	Data structure containing all data necessary for initialization
     * 						of the inventory.
     */
	public void load (String[] inventory) {
		for (int i=0;i < inventory.length; i++){
			gadgets.add(inventory[i]);
		}
	}
	
	/**
     * acquires a gadget and returns 'true' if it exists.
     * <p>
     * @param gadget 		Name of the gadget to check if available
     * @return 	‘false’ if the gadget is missing, and ‘true’ otherwise
     */
	public boolean getItem(String gadget){
		if (gadget != null)
			return gadgets.contains(gadget);
		else return false;
	}

	/**
	 *
	 * <p>
	 * Prints to a file name @filename a serialized object List<Gadget> which is a
	 * List of all the reports in the diary.
	 * This method is called by the main method in order to generate the output.
	 */
	public void printToFile(String filename){
		if (!filename.contains(".json"))
			System.out.println("file name is not from type json");

		else {
			File file = new File(filename);
			if (file.exists()) // if file with the same name already exists print error
				System.out.println("file name " + filename +  " already exists");
			Gson gson = new GsonBuilder().create();
			try (FileWriter fw = new FileWriter(filename)) { //write the gadgets to json file
				gson.toJson(gadgets, fw);
			} catch (IOException e) {
			}

		}
	}
}
