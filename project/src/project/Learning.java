package project;

import java.util.HashMap;
import java.util.Map;

public class Learning {
    
    public static void main(String[] args) {
        Learning learning = new Learning();
    }

    // Variables

    // Error bound 1
    Double epsilon = 0.1;

    // Error bound 2
    Double delta = 0.05;

    // alpha > 0
    Double alpha;

    // Number of states
    int m;

    // Data structure to keep track of which states have already been visited in a given trace
    public HashMap<String, Integer> statesVisited = new HashMap<String, Integer>();

    // Data structure to save the traces for the system

    // Date structure to keep track of n_s
    public HashMap<String, Integer> n_s = new HashMap<String, Integer>();

    // Data structure for A (transition matrix)
    double [][] matrix;

    // Do I know this beforehand?


    public Learning(){
        performLearning();
    }

    // The whole learning algorithm
    public void performLearning(){

        m = getNumberOfStates();

        createTransitionMatrix();

        boolean notReached = true;

        while(notReached){
            // Generate a new trace for the system
            generateTrace();

            // Update n_s
            for (Map.Entry<String, Integer> entry : statesVisited.entrySet()) {
                String key = entry.getKey();
                Integer value = entry.getValue();
                
                // Can be simplified if we know that states beforehand
                if(n_s.containsKey(key)){
                    n_s.put(key, n_s.get(key) + value);
                }
                else{
                    n_s.put(key, value);
                }
            }

            // Update the set of traces

            // Compute A (transition matrix) 
            // How do I translate this into computing probabilites for each update?

            // Compute B (for the stopping condition)

            notReached = !stoppingConditionReached();
        }
         
    }

    // Perform a single simulation to create a trace for the system
    public void generateTrace(){
        // Start the system
        ClientExample client = new ClientExample();

        // Refresh the states visited
        statesVisited = new HashMap<String, Integer>();
        
        // Save the initial state 
        statesVisited.put(client.getSystemState(), 1);

        boolean pathIsLoopFree = true;
        while(pathIsLoopFree){
            // Move one transition in the system
            String actionTaken = client.performSingleStep();

            // TODO save the action performed

            // Save the state visited and stop if we have a loop in our path
            String newState = client.getSystemState();
            if(statesVisited.containsKey(newState)){
                statesVisited.put(newState, statesVisited.get(newState) + 1);
                pathIsLoopFree = false;
            }
            else{
                statesVisited.put(client.getSystemState(), 1);
            }   
        }

    }

    // After each new trace has been added, compute if stopping condition has been reached
    public boolean stoppingConditionReached(){

        // Check for all states s in n_s whether s <  11/10 * B(A)^2 H∗(n_s, ε, δ/m)

        for (Map.Entry<String, Integer> entry : n_s.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            
            if(value < chechState(value)){
                return false;
            }
        }

        return true;
    }

    public int chechState(int value){
        double condition = 11/10; // Add other things here
        // TODO Implement computation of  11/10 * B(A)^2 H∗(n_s, ε, δ/m)
        return value;
    }

    public int getNumberOfStates(){
        // TODO get information from PRISM
        return 10;
    }

    public void createTransitionMatrix(){
        matrix = new double[m][m];
        // TODO get information from PRISM
    }
}
