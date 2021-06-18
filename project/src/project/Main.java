package project;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
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
        System.out.println("Duration of generating implementation: " + duration);

        // Run the learning algorithm
        System.out.println("Running the learning algorithm for " + className);
        try {
            //Implementation implementation= (Implementation) Class.forName("project."+className).getDeclaredConstructor().newInstance();
            Class<?> implementation = Class.forName("project."+className);
            Learning learning = new Learning(filename, implementation);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        System.out.println("Learning algorithm finished");

        // Generate the deterministic PRISM file and output where it is
        long endTotalTime = System.currentTimeMillis();
        long durationTotal = (endTotalTime - startTotalTime);
        System.out.println("Duration of generating implementation: " + durationTotal);
    }
}
