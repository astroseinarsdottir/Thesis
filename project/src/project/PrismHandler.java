package project;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import parser.Values;
import parser.ast.ModulesFile;
import parser.ast.PropertiesFile;
import prism.*;


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
            // TODO filename
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

                statesMapper.put(values, id);
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

    public int[][] getTransitionMatrix(int numberOfStates){

        // Data structure to store all transitions in the model
        int [][] matrix = new int[numberOfStates][numberOfStates];

        try{
            // TODO filename
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

    public String[] getModelVariables(){
        try {
            // TODO get the file name
            File statesFile = new File("src/project/models/test.sta");
            if (!statesFile.exists()) prism.exportStatesToFile(1,statesFile);

            Scanner myReader = new Scanner(statesFile);
            String variables = myReader.nextLine();

            variables = variables.replace("(", "");
            variables = variables.replace(")", "");
            return variables.split(",");


        } catch (FileNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
            System.exit(1);
            return null;
        } catch (PrismException e) {
            System.out.println("Error: " + e.getMessage());
            System.exit(1);
            return null;
        }
    }

    public double computeCond(String state, HashMap<String, String> statesMapper, String[] variables, Set<Integer> R){
        int l = 0;
        double cond = 0;

        int stateID = Integer.parseInt(statesMapper.get(state));

        //  the set of states R*("state"), that can reach "state", not including "state"
        Set<Integer> RStar = new HashSet<>();
        RStar.addAll(R);
        RStar.remove(stateID);

        HashMap<Integer, String[]> statesIDToValue = preProcessStatesMapper(statesMapper);

        List<String> states = new ArrayList<>();
        for(int rStates : RStar){
            String[] stateValues = statesIDToValue.get(rStates);
            String command = "(";
            for(int i = 0; i < variables.length; i++){
                command = command + variables[i] + "=" + stateValues[i];

                if(variables.length > i+1) command = command + " &";
                else command = command + ")";
            }
            states.add(command);
        }
        while(cond <= 0){
            try {

                // Compute cond for all states in R*, continue until cond >0
                l = l+1;
                String propertyString = "filter(min, P=? [F<="+ l+ "!(" + String.join("|", states) + ")], " + String.join(" | ", states) + ")";

                PropertiesFile propertiesFile = prism.parsePropertiesString(modulesFile, propertyString);
                //System.out.println(propertiesFile.getPropertyObject(0));
                Result result = prism.modelCheck(propertiesFile, propertiesFile.getPropertyObject(0));

                cond = Double.parseDouble(result.toString());

            } catch (PrismLangException e) {
                System.out.println("Error: " + e.getMessage());
                System.exit(1);
            }
            catch (PrismException e){
                System.out.println("Error: " + e.getMessage());
                System.exit(1);
            }
        }
        return (double) l/cond;
    }

    private HashMap<Integer, String[]> preProcessStatesMapper(HashMap<String, String> statesMapper){

        HashMap<Integer, String[]> statesIDToValue = new HashMap<>();

        for (Map.Entry<String, String> entry : statesMapper.entrySet()) {
            String stateValue = entry.getKey();
            int stateID = Integer.parseInt(entry.getValue());

            stateValue = stateValue.replace("(", "");
            stateValue = stateValue.replace(")", "");
            String[] stateValues = stateValue.split(",");

            statesIDToValue.put(stateID, stateValues);
        }
        return statesIDToValue;
    }

}
