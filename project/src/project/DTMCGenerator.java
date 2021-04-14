package project;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class DTMCGenerator {

    private String orinalModel;
    private double[][] learnedMatrix;
    private HashMap<String, String> statesMapper;
    private String[] variables;

    public static void main(String[] args) {
        DTMCGenerator dtmcGenerator = new DTMCGenerator("Client.nm", null, null, null);
        dtmcGenerator.generateDTMC();
    }


    // Need the original prism model file, the learned matrix and the statesmapper
    public DTMCGenerator(String originalModel, double[][] learnedMatrix, HashMap<String, String> statesMapper, String[] variables){
        this.orinalModel = originalModel;
        this.learnedMatrix = learnedMatrix;
        this.statesMapper = statesMapper;
        this.variables = variables;
    }

    public void generateDTMC(){

        // Create a new PRISM file for the DTMC
        // TODO get the name of the file from the originalfile
        File dtmcFile = new File("src/project/models/client.dtmc.pm");

        try{
            dtmcFile.createNewFile();
            FileWriter myWriter = new FileWriter(dtmcFile);

            // Read the original PRISM file
            Scanner myReader = new Scanner(new File("src/project/models/" + orinalModel));
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();

                String strippedData = data.strip();

                // Change mdp to dtcm
                if(strippedData.equals("mdp")) myWriter.write("dtmc");

                // Every line that includes [ as the first letter, skip
                if(strippedData.startsWith("[")) continue;

                // Once "endmodule" is reached, use learnedMatrix and statesmapper to create commands
                if(strippedData.equals("endmodule")) createCommandLines(myWriter);

                // Copy any other line to new file
                myWriter.write(data);

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

    }

    private void createCommandLines(FileWriter writer){

        for (Map.Entry<String, String> entry : statesMapper.entrySet()) {
            String stateValue = entry.getKey();
            int stateID = Integer.parseInt(entry.getValue());

            stateValue = stateValue.replace("(", "");
            stateValue = stateValue.replace(")", "");
            String[] stateValues = stateValue.split(",");

            String command = "[]";
            for(int i = 0; i < variables.length; i++){
                command = command + " " + variables[i] + "=" + stateValues[i];
            }
            command = command + " ->";

            boolean needPlus = false;
            for(int j = 0; j < learnedMatrix.length; j++){
                if(learnedMatrix[stateID][j] == 0) continue;

                if(needPlus) command = command + " + ";

                command = command + " " + learnedMatrix[stateID][j] + " :";
                // TODO add the update to the variables
                needPlus = true;
            }
            try {
                writer.write(command);
            }
            catch (IOException e){
                System.out.println("Error: " + e.getMessage());
                System.exit(1);
            }

        }
    }
}
