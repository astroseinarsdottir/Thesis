package project;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Main {

    public static void main(String[] args) throws IOException {

        Logger logger = Logger.getLogger("MyLog");
        FileHandler fh;
        fh = new FileHandler("./MyLogFile.log");
        logger.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);
        logger.setUseParentHandlers(false);

        long startTotalTime = System.currentTimeMillis();

        // Get the PRISM file with the non-determinist model
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a path to a PRISM file containing a non-deterministic model: ");
        System.out.flush();
        String filename = scanner.nextLine();

        long startTime = System.currentTimeMillis();

        // Generate the code (the implementation)
        System.out.println("Generating implementation from the PRISM model");
        CodeGenerator codeGenerator = new CodeGenerator(filename);
        String className = codeGenerator.generateCode();

        // Build the new class that was generated?
        System.out.println("Compiling implementation");
        String command = "make" ;
        String [] envp = { } ;
        File dir = new File ( "." ) ; // this is the directory where the Makefile is
        Process proc = null;
        try {
            proc = Runtime.getRuntime().exec(command,envp,dir);
            proc.waitFor ( ) ;
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime);

        logger.info("Duration of generating implementation: " + duration);

        // Run the learning algorithm
        System.out.println("Running the learning algorithm for " + className);
        try {
            Class<?> implementation = Class.forName("project."+className);
            Learning learning = new Learning(filename, implementation, logger);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        System.out.println("Learning algorithm finished");

        // Generate the deterministic PRISM file and output where it is
        long endTotalTime = System.currentTimeMillis();
        long durationTotal = (endTotalTime - startTotalTime);

        logger.info("Total duration  " + durationTotal);
    }

}
