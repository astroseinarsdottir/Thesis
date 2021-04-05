package project;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

import parser.Values;
import parser.ast.ModulesFile;
import parser.ast.PropertiesFile;
import prism.Prism;
import prism.PrismDevNullLog;
import prism.PrismException;
import prism.PrismLog;
import prism.Result;
import prism.UndefinedConstants;


/*
    Class used to utilize PRISM for the learning algorithm
    Based on the PRISM-API: https://github.com/prismmodelchecker/prism-api
 */
public class PrismHandler {

    // Local variables
    private PrismLog mainLog;
    private Prism prism;
    private ModulesFile modulesFile;

    public static void main(String[] args) {
        PrismHandler test =  new PrismHandler("Client.nm");
        test.getModelStates();
        test.getTransitionMatrix(7);
    }

    public PrismHandler(String prismFile) {

        try {
            // Create a log for PRISM output (hidden or stdout)
            mainLog = new PrismDevNullLog();

            // Initialise PRISM engine
            prism = new Prism(mainLog);
            prism.initialise();

            // Parse and load a PRISM model from a file
            modulesFile = prism.parseModelFile(new File("src/project/models/" + prismFile));
            prism.loadPRISMModel(modulesFile);

        } catch (FileNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
            System.exit(1);
        } catch (PrismException e) {
            System.out.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    // Use PRISM to get the states of the model
    public HashMap<String, String> getModelStates(){
        try{
            // Export states from PRISM to a file
            File statesFile = new File("src/project/models/test.sta");
            prism.exportStatesToFile(1,statesFile);

            // Data structure to be filled with state information
            HashMap<String, String> statesMapper = new HashMap<String, String>();

            // Read the states file and fill the data structure
            Scanner myReader = new Scanner(statesFile);
            myReader.nextLine(); // First line (variables)

            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();

                // Split the string to get the ID of the state and the value of the state variables
                String[] parts = data.split(":");
                String id = parts[0];
                String values = parts[1];

                statesMapper.put(id, values);
            }
            myReader.close();

            return statesMapper;

        }
        catch (Exception e){
            System.out.println("Error: " + e.getMessage());
            System.exit(1);
            return null;
        }
    }

    public double[][] getTransitionMatrix(int numberOfStates){

        // Data structure to store all transitions in the model
        double [][] matrix = new double[numberOfStates][numberOfStates];

        try{
            // Export states from PRISM to a file
            File transitionFile = new File("src/project/models/test.tra");
            prism.exportTransToFile(false, 1, transitionFile);

            // Read the states file and fill the data structure
            Scanner myReader = new Scanner(transitionFile);
            myReader.nextLine(); // First line (information about the model)

            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();

                // Split the string to get the ID of the state and the value of the state variables
                String[] parts = data.split(" ");
                int stateFrom = Integer.parseInt(parts[0]);
                int stateTo = Integer.parseInt(parts[2]);

                System.out.println(stateFrom + " " + stateTo);

                matrix[stateFrom][stateTo] = 1;
            }
            myReader.close();

            return matrix;
        }
        catch (Exception e){
            System.out.println("Error: " + e.getMessage());
            System.exit(1);
            return null;
        }
    }

}
