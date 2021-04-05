package project;

import java.io.File;
import java.io.FileNotFoundException;

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

    // Local variable
    private PrismLog mainLog;
    private Prism prism;

    public PrismHandler(String prismFile) {

        try {
            // Create a log for PRISM output (hidden or stdout)
            mainLog = new PrismDevNullLog();
            // PrismLog mainLog = new PrismFileLog("stdout");

            // Initialise PRISM engine
            prism = new Prism(mainLog);
            prism.initialise();

            // Parse and load a PRISM model from a file
            ModulesFile modulesFile = prism.parseModelFile(new File(prismFile));
            prism.loadPRISMModel(modulesFile);

        } catch (FileNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
            System.exit(1);
        } catch (PrismException e) {
            System.out.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

}
