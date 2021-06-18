package project;

import java.io.*;
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

    public String generateCode(){
        File prismFile = new File(prismFileString);

        try {
            readPrismFile(prismFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create new java file for the model
        String className = prismFile.getName().replaceFirst("[.][^.]+$", "");
        try {
            FileWriter myWriter = new FileWriter("src/project/"+ className+ ".java");
            BufferedWriter outputStream = new BufferedWriter(myWriter);

            readAndWriteFromFile("src/project/templates/File1.txt", outputStream);

            outputStream.newLine();
            outputStream.write("public class " + className +  " extends Implementation {");
            outputStream.newLine();
            outputStream.newLine();

            for (ModelVariable variable: variables) {
                outputStream.write("    public int "  + variable.getName()  +";\n");
            }

            readAndWriteFromFile("src/project/templates/File2.txt", outputStream);

            outputStream.newLine();
            outputStream.write("        "+ className + " test = new "+ className + "(10);");
            outputStream.newLine();
            outputStream.write("    }");

            outputStream.newLine();
            outputStream.newLine();
            outputStream.write("    public "+ className+"(int numSteps) {");
            outputStream.newLine();
            for (ModelVariable variable: variables) {
                outputStream.write("        "+ variable.getName()+"="+ variable.getInitialState()+ ";");
                outputStream.newLine();
            }

            readAndWriteFromFile("src/project/templates/File3.txt", outputStream);

            outputStream.newLine();
            outputStream.write("    public "+ className+"(){");
            outputStream.newLine();
            for (ModelVariable variable: variables) {
                outputStream.write("        "+ variable.getName()+"="+ variable.getInitialState()+ ";");
                outputStream.newLine();
            }
            List<String> states = new ArrayList<>();
            for (ModelVariable variable: variables) {
                String state = "get"+ variable.getName() + "() ";
                states.add(state);
            }

            readAndWriteFromFile("src/project/templates/File4.txt", outputStream);
            outputStream.newLine();

            outputStream.write("        Collection<String> possibleActions = getAllPossibleActions("+String.join(",", states) +");\n");
            outputStream.newLine();

            readAndWriteFromFile("src/project/templates/File4.5.txt", outputStream);
            outputStream.newLine();

            if(actions.size()> 1500){
                splitExecuteActions(outputStream);
            }
            else{
                outputStream.write("    // Perform the transision/action given as parameter\n");
                outputStream.write("    public void executeAction(String action) throws Exception {\n");
                outputStream.write("        switch (action) {\n");
                int count = 0;
                for (ModelAction action: actions) {
                    outputStream.write("        case \"action"+count+"\":\n");

                    List<String> variable = action.getVariablesToUpdate();
                    List<String> update = action.getUpdates();
                    for (int i = 0; i < action.getVariablesToUpdate().size(); i++) {
                        outputStream.write("            set"+variable.get(i)+"("+update.get(i)+");\n");
                    }
                    outputStream.write("            break;");
                    outputStream.newLine();
                    count++;
                }
                outputStream.write("        default:\n");
                outputStream.write("            throw new Exception(\"No action chosen\");\n");
                outputStream.write("        }\n");
                outputStream.write("    }\n");
            }

            readAndWriteFromFile("src/project/templates/File5.txt", outputStream);
            outputStream.newLine();

            outputStream.write("        Collection<String> possibleActions = getAllPossibleActions("+String.join(",", states) +");\n");
            outputStream.newLine();

            readAndWriteFromFile("src/project/templates/File6.txt", outputStream);

            for (ModelVariable variable: variables) {
                outputStream.write("    public void set"+variable.getName() +"(int i) throws IllegalArgumentException {\n");
                outputStream.write("        if (i < "+variable.getRangeFrom()+" || i > "+variable.getRangeTo()+") {\n");
                outputStream.write("            throw new IllegalArgumentException(\""+variable.getName()+
                        " can only be between "+variable.getRangeFrom()+" and "+variable.getRangeTo()+"\");");
                outputStream.write("        }\n" +
                        "        "+variable.getName()+" = i;\n" +
                        "    }\n");
                outputStream.write("    public int get"+variable.getName()+"() {\n" +
                        "        return "+variable.getName()+";\n" +
                        "    }\n");
                outputStream.newLine();
            }
            readAndWriteFromFile("src/project/templates/File7.txt", outputStream);

            if(actions.size()> 1500){
                splitGenerateConditionMap(outputStream);
            }else {
                outputStream.write("    // Create the map where we can look up transitions by their conditions\n");
                outputStream.write("    public void generateConditionMap() {\n");
                outputStream.write("        multimap = ArrayListMultimap.create();");
                for (int i = 0; i < actions.size(); i++) {
                    outputStream.write("        multimap.put(\""+actions.get(i).getCondition()+"\", \"action"+i+"\");");
                    outputStream.newLine();
                }
            }


            readAndWriteFromFile("src/project/templates/File8.txt", outputStream);

            List<String> currStates = new ArrayList<>();
            for (ModelVariable variable: variables) {
                String state = "int current"+ variable.getName();
                currStates.add(state);
            }
            outputStream.write("    public Collection<String> getAllPossibleActions("+String.join( ",", currStates)+") {");

            readAndWriteFromFile("src/project/templates/File9.txt", outputStream);
            outputStream.newLine();

            for (ModelVariable variable: variables) {
                outputStream.write("            graalEngine.eval(\""+variable.getName()+"=\" + String.valueOf(current"+variable.getName()+"));");
                outputStream.newLine();
            }

            readAndWriteFromFile("src/project/templates/File10.txt", outputStream);

            List<String> counter = new ArrayList<>();
            for (int i = 0; i < variables.size(); i++) {
                String numb = "{"+i+"}";
                counter.add(numb);
            }
            outputStream.write("    public String getSystemState(){\n" +
                    "        return  MessageFormat.format(\"("+String.join(",", counter)+")\", "+String.join(",", states)+");\n" +
                    "    }\n" +
                    "\n" +
                    "}");

            outputStream.flush();
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return className;
    }

    public void readAndWriteFromFile(String inputFile, BufferedWriter outputStream) throws IOException {
        BufferedReader inputStream = new BufferedReader(new FileReader(inputFile));

        //BufferedWriter outputStream = new BufferedWriter(myWriter);
        String count;
        while ((count = inputStream.readLine()) != null) {
            outputStream.write(count);
            outputStream.newLine();
        }
        inputStream.close();
    }

    private void readPrismFile(File prismFile) throws Exception {
        variables = new ArrayList<>();
        actions = new ArrayList<>();

        if(!prismFile.getName().endsWith(".nm")){
            throw new Exception("File not of correct format. Input can only be .nm files.");
        }
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
                    variable.setRangeFrom(varRange.substring(varRange.indexOf("[") + 1, varRange.indexOf("..")));
                    variable.setRangeTo(varRange.substring(varRange.indexOf("..") + 2, varRange.indexOf("]")));

                    if(varRange.contains("init")){
                        String init = varRange.substring(varRange.indexOf("init")+4).stripLeading();
                        variable.setInitialState(init.substring(0, init.indexOf(";")));
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
                    condition = condition.replace(">==", ">=")
                            .replace("<==", "<=")
                            .replace("==>", "=>")
                            .replace("==<", "=<");
                    action.setCondition(condition);

                    // Update
                    String update = actionSplit[1].strip();
                    update = update.replace("(", "")
                            .replace(")", "")
                            .replace(";", "");

                    if(update.contains("&")){
                        String[] updates = update.split("&");
                        // Save many updates
                        for (String part: updates) {
                            part = part.strip();
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

            myReader.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void splitExecuteActions(BufferedWriter outputStream) throws IOException {

        int count = 0;
        int numbMethods = 0;
        for (ModelAction action: actions) {
            if(count % 1500 == 0){
                if(count > 0){
                    outputStream.write("        }\n");
                    outputStream.write("    }\n");
                }
                outputStream.write("    // Perform the transision/action given as parameter\n");
                outputStream.write("    public void executeAction"+numbMethods+"(String action) throws Exception {\n");
                outputStream.write("        switch (action) {\n");
                numbMethods++;
            }
            outputStream.write("        case \"action"+count+"\":\n");

            List<String> variable = action.getVariablesToUpdate();
            List<String> update = action.getUpdates();
            for (int i = 0; i < action.getVariablesToUpdate().size(); i++) {
                outputStream.write("            set"+variable.get(i)+"("+update.get(i)+");\n");
            }
            outputStream.write("            break;");
            outputStream.newLine();
            count++;
        }
        outputStream.write("        }\n");
        outputStream.write("    }\n");

        outputStream.write("    // Perform the transision/action given as parameter\n");
        outputStream.write("    public void executeAction(String action) throws Exception {\n");
        for(int i=0;i<numbMethods;i++){
            outputStream.write("        executeAction"+i+"(action);\n");
        }
        outputStream.write("    }\n");

    }

    private void splitGenerateConditionMap(BufferedWriter outputStream) throws IOException{
        int numbMethods = 0;
        for (int i = 0; i < actions.size(); i++) {
            if(i % 1500 == 0) {
                if (i > 0) {
                    outputStream.write("    }\n");
                }
                outputStream.write("    // Create the map where we can look up transitions by their conditions\n");
                outputStream.write("    public void generateConditionMap"+numbMethods+"() {\n");
                numbMethods++;
            }
            outputStream.write("        multimap.put(\""+actions.get(i).getCondition()+"\", \"action"+i+"\");");
            outputStream.newLine();
        }
        outputStream.write("    }\n");
        outputStream.write("    // Create the map where we can look up transitions by their conditions\n");
        outputStream.write("    public void generateConditionMap() {\n");
        outputStream.write("        multimap = ArrayListMultimap.create();\n");
        for(int i=0;i<numbMethods;i++){
            outputStream.write("        generateConditionMap"+i+"();\n");
        }
    }

}
