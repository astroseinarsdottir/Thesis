package project;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CodeGenerator {

    private String prismFileString;
    private List<ModelVariable> variables;
    private List<ModelAction> actions;

    public static void main(String[] args) {
        CodeGenerator codeGenerator = new CodeGenerator("src/project/models/Client.nm");
    }

    public CodeGenerator(String prismFileString){
        this.prismFileString = prismFileString;

        generateCode();
    }

    public void generateCode(){
        File prismFile = new File(prismFileString);

        readPrismFile(prismFile);

        // Create new java file for the model

        try {
            FileWriter myWriter = new FileWriter("src/project/"+ prismFile.getName()+ ".java");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readPrismFile(File prismFile){
        variables = new ArrayList<>();
        actions = new ArrayList<>();

        try {
            // Start reading through the PRISM file
            Scanner myReader = new Scanner(prismFile);
            while (myReader.hasNextLine()){
                String data = myReader.nextLine();

                String strippedData = data.strip();

                // Model variables
                if(strippedData.contains(":")){
                    String[] varData = strippedData.split(":");

                    ModelVariable variable = new ModelVariable();
                    variable.setName(varData[0].strip());

                    String varRange = varData[1].stripLeading();
                    variable.setRangeFrom(varRange.charAt(1));
                    variable.setRangeTo(varRange.charAt(4));

                    if(varRange.contains("init")){
                        String init = varRange.substring(varRange.indexOf("init")+4).stripLeading();
                        variable.setInitialState(init.charAt(0));
                    }
                    variables.add(variable);
                }

                // Model actions
                if(strippedData.startsWith("[")){
                    String actionString = strippedData.substring(strippedData.indexOf("]")+1).stripLeading();
                    String[] actionSplit = actionString.split("->");

                    ModelAction action = new ModelAction();

                    // Condition
                    String condition = actionSplit[0].strip();
                    condition = condition.replace("=", "==")
                            .replace("&", "&&")
                            .replace("|", "||");
                    action.setCondition(condition);

                    // Update
                    String update = actionSplit[1].strip();
                    System.out.println("Before replace " + update);
                    update = update.replace("(", "")
                            .replace(")", "")
                            .replace(";", "");

                    System.out.println("After replace " +update);

                    if(update.contains("&")){
                        String[] updates = update.split("&");
                        // Save many updates
                        for (String part: updates) {
                            part.strip();
                            String[] parts = part.split("'=");
                            action.addVariableToUpdate(parts[0]);
                            action.addUpdate(parts[1]);
                        }
                    }
                    else{
                        // Only one update
                        String[] parts = update.split("'=");
                        action.addVariableToUpdate(parts[0]);
                        action.addUpdate(parts[1]);
                    }
                    actions.add(action);
                }
            }
            System.out.println(variables);
            for (ModelAction action: actions) {
                System.out.println(action);
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
