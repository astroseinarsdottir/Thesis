package project;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class DTMCGenerator {

    private String originalModel;
    private double[][] learnedMatrix;
    private HashMap<String, String> statesMapper;
    private String[] variables;
    private HashMap<Integer, String[]> statesIDToValue;

    public static void main(String[] args) {
        DTMCGenerator dtmcGenerator = new DTMCGenerator("Client.nm", null, null, null);
        dtmcGenerator.generateDTMC();
    }


    // Need the original prism model file, the learned matrix and the states mapper
    public DTMCGenerator(String originalModel, double[][] learnedMatrix, HashMap<String, String> statesMapper, String[] variables){
        this.originalModel = originalModel;
        this.learnedMatrix = learnedMatrix;
        this.statesMapper = statesMapper;
        this.variables = variables;
    }

    public String generateDTMC(){
        preProcessStatesMapper();

        // Create a new PRISM file for the DTMC
        File originalFile = new File(originalModel);
        String fileName = originalFile.getName().replaceFirst("[.][^.]+$", "");

        File dtmcFile = new File("src/project/models/"+fileName+"-dtmc.pm");

        try{
            dtmcFile.createNewFile();
            FileWriter myWriter = new FileWriter(dtmcFile);

            // Read the original PRISM file
            Scanner myReader = new Scanner(originalFile);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();

                String strippedData = data.strip();

                // Change mdp to dtcm
                if(strippedData.equals("mdp")) {
                    myWriter.write("dtmc \n");
                    continue;
                }

                // Every line that includes [ as the first letter, skip
                if(strippedData.startsWith("[")) continue;

                // Once "endmodule" is reached, use learnedMatrix and states mapper to create commands
                if(strippedData.equals("endmodule")) createCommandLines(myWriter);

                // Copy any other line to new file
                myWriter.write(data + "\n");

            }
            myReader.close();
            myWriter.close();



        }
        catch (FileNotFoundException e){
            System.out.println("Error: " + e.getMessage());
            System.exit(1);
        }
        catch (IOException e){
            System.out.println("Error: " + e.getMessage());
            System.exit(1);
        }
        return "src/project/models/"+fileName+"-dtmc.pm";
    }

    private void createCommandLines(FileWriter writer){

        for (Map.Entry<Integer, String[]> entry : statesIDToValue.entrySet()) {
            int stateID = entry.getKey();
            String[] stateValues = entry.getValue();

            String command = "[]";
            for(int i = 0; i < variables.length; i++){
                command = command + " " + variables[i] + "=" + stateValues[i];

                if(variables.length > i+1) command = command + " &";
            }
            command = command + " ->";

            boolean needPlus = false;
            for(int j = 0; j < learnedMatrix.length; j++){
                if(learnedMatrix[stateID][j] == 0) continue;

                if(needPlus) command = command + " + ";

                command = command + " " + learnedMatrix[stateID][j] + " :";

                String[] newStateValues = statesIDToValue.get(j);
                for(int s = 0; s < variables.length; s++){
                    // TODO add the update to the variables
                    command = command + " (" + variables[s] + "'=" +newStateValues[s] + ")";

                    if(variables.length > s+1) command = command + " &";
                }
                // TODO add the update to the variables
                needPlus = true;
            }
            command = command + ";";
            try {
                writer.write(command + "\n");
            }
            catch (IOException e){
                System.out.println("Error: " + e.getMessage());
                System.exit(1);
            }

        }
    }

    private void preProcessStatesMapper(){

        statesIDToValue = new HashMap<>();

        for (Map.Entry<String, String> entry : statesMapper.entrySet()) {
            String stateValue = entry.getKey();
            int stateID = Integer.parseInt(entry.getValue());

            stateValue = stateValue.replace("(", "");
            stateValue = stateValue.replace(")", "");
            String[] stateValues = stateValue.split(",");

            statesIDToValue.put(stateID, stateValues);
        }
    }
}
