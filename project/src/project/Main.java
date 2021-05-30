package project;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        // Get the PRISM file with the non-determinist model
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a path to a PRISM file containing a non-deterministic model: ");
        System.out.flush();
        String filename = scanner.nextLine();

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
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Run the learning algorithm
        System.out.println("Running the learning algorithm for " + className);
        try {
            Implementation implementation= (Implementation) Class.forName("project."+className).getDeclaredConstructor().newInstance();
            Learning learning = new Learning(filename, implementation);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        System.out.println("Learning algorithm finished");

        // Generate the deterministic PRISM file and output where it is
    }
}
