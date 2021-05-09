package project;

import java.awt.*;
import java.util.*;

public class Learning {
    
    public static void main(String[] args) {
        Learning learning = new Learning("Client.nm");
    }

    // Algorithm variables

    // Error bound 1
    Double epsilon = 0.1;

    // Error bound 2
    Double delta = 0.05;

    // alpha > 0
    int alpha = 200;

    // Number of states
    int m;
    int mStochastic;

    // Data structure to keep track of which states have already been visited in a given trace
    public HashMap<String, Integer> statesVisited = new HashMap<String, Integer>();

    // Data structure to save the traces for the system
    ArrayList<ArrayList<String>> traces;

    // Date structure to keep track of n_s
    public HashMap<String, Integer> n_s = new HashMap<String, Integer>();

    // Data structure for A (transition matrix)
    double [][] learnedMatrix;

    // Data structure for B(A)
    HashMap<String, Double> B;

    // Data structure to keep track of n_ij
    int [][] n_ij;

    // Data structure for k_i
    HashMap<Integer, Integer> k_i = new HashMap<Integer, Integer>();

    // Data structure for backward reachability analysis
    public HashMap<Integer, Set<Integer>> reachability = new HashMap<>();

    // PRISM variables
    PrismHandler prismHandler;
    HashMap<String, String> statesMapper;  // Key: state variables; Value: ID of state;
    int [][] transitionMatrix; // To be able to know all the transitions
    String[] variables;
    String originalPrismFile;

    public Learning(String prismFile){
        prismHandler = new PrismHandler(prismFile);
        originalPrismFile = prismFile;

        performLearning();
    }

    // The whole learning algorithm
    public void performLearning(){

        // Extract information about the model
        statesMapper = getModelStates();
        m = getNumberOfStates();
        transitionMatrix = createTransitionMatrix();
        generateK_i();
        getModelVariables();

        initializeN_s();
        traces = new ArrayList<ArrayList<String>>();
        n_ij = new int[m][m];
        initializeLearnedMatrix();
        performBackwardReachabilty();

        boolean notReached = true;

        while(notReached){
            // Generate a new trace for the system
            traces.add(generateTrace());

            // Update n_s
            for (Map.Entry<String, Integer> entry : statesVisited.entrySet()) {
                String key = entry.getKey();
                Integer value = entry.getValue();

                n_s.put(key, n_s.get(key) + value);
            }
            // Compute A (transition matrix)
            computeTransitionMatrix();

            // Compute B (for the stopping condition)
            computeB();

            notReached = !stoppingConditionReached();
        }

        for (double[] row : learnedMatrix)

            // converting each row as string
            // and then printing in a separate line
            System.out.println(Arrays.toString(row));

        for(double[] row: learnedMatrix) {
            double sum = 0;
            for (int i = 0; i < row.length; i++) {

                sum = sum + row[i];
            }
            System.out.println(sum);
        }
        DTMCGenerator dtmcGenerator = new DTMCGenerator(originalPrismFile, learnedMatrix, statesMapper, variables);
        dtmcGenerator.generateDTMC();
    }

    // Perform a single simulation to create a trace for the system
    public ArrayList<String> generateTrace(){
        // Start the system
        ClientExample client = new ClientExample();
        ArrayList<String> trace = new ArrayList<>();

        // Refresh the states visited
        statesVisited = new HashMap<String, Integer>();
        
        // Save the initial state 
        statesVisited.put(client.getSystemState(), 1);
        trace.add(client.getSystemState());

        boolean pathIsLoopFree = true;
        while(pathIsLoopFree){
            String oldState = client.getSystemState();

            // Move one transition in the system
            String actionTaken = client.performSingleStep();

            String newState = client.getSystemState();
            trace.add(newState);

            // Update n_ij matrix
            int oldS = Integer.parseInt(statesMapper.get(oldState));
            int newS = Integer.parseInt(statesMapper.get(newState));
            n_ij[oldS][newS] = n_ij[oldS][newS] + 1;

            // Save the state visited and stop if we have a loop in our path
            if(statesVisited.containsKey(newState)){
                //statesVisited.put(newState, statesVisited.get(newState) + 1);
                pathIsLoopFree = false;
            }
            else{
                statesVisited.put(client.getSystemState(), 1);
            }   
        }
        return trace;
    }

    // Calculate aˆij for all i,j in the learned matrix
    public void computeTransitionMatrix(){

        for (Map.Entry<String, String> entry : statesMapper.entrySet()) {
            String stateValue = entry.getKey();
            int stateID = Integer.parseInt(entry.getValue());

            // Only need to compute for stochastic states
            if(!isStateStochastic(stateID)) continue;

            int numbSuc = k_i.get(stateID);
            int numbState = n_s.get(stateValue);
            for(int j = 0; j < m; j++){
                if(transitionMatrix[stateID][j] == 0){
                    learnedMatrix[stateID][j] = 0;
                    continue;
                }

                learnedMatrix[stateID][j] =  (n_ij[stateID][j] + alpha) / ((double)numbState +  numbSuc*alpha);
                //learnedMatrix[stateID][j]  = Math.round(learnedMatrix[stateID][j]  * 10.0) / 10.0;
            }
        }
    }

    public Boolean isStateStochastic(int stateID){
        return k_i.get(stateID) > 1;
    }

    // Compute B(A) for the stopping condition
    public void computeB(){
        B = new HashMap<>();

        DTMCGenerator dtmcGenerator = new DTMCGenerator(originalPrismFile, learnedMatrix, statesMapper, variables);
        String dtmc = dtmcGenerator.generateDTMC();

        PrismHandler dtmcHandler = new PrismHandler(dtmc);

        for (String state : statesMapper.keySet()) {
            B.put(state, dtmcHandler.computeCond(state, statesMapper, variables, reachability.get(Integer.parseInt(statesMapper.get(state)))));
        }

    }

    // After each new trace has been added, compute if stopping condition has been reached
    public boolean stoppingConditionReached(){

        // Check for all states s in n_s whether s <  11/10 * B(A)^2 H∗(n_s, ε, δ/m)
        for(Map.Entry<String, Integer> entry : n_s.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();

            if(isStateStochastic(value)) continue;

            if(value == 0) return false;

            if(value < checkState(key, value)){
                return false;
            }
        }
        return true;
    }

    public double checkState(String state, int value){
        // Computation of  11/10 * B(A)^2 H∗(n_s, ε, δ/m)

        double h = computeH(Integer.parseInt(statesMapper.get(state)), value);
        return Math.sqrt( (11/10)*B.get(state) )* h;
    }

    // Computes maxj∈S( H(n_i, n_ij,ε,δ′) where δ′ = δ/m_stoc
    public double computeH(int state, int value){
        double deltaPrime = delta/mStochastic;

        double max = 0;
        for(int i = 0; i < m; i++){
            double chen = (2/Math.pow(epsilon, 2)) * Math.log(2/deltaPrime) *
                    (1/4 - Math.pow(Math.abs((1/2) - (1/value)*n_ij[state][i]) - (2/3)*epsilon, 2) );
            if (chen > max) max = chen;
        }
        return max;
    }

    public void initializeN_s(){
        for (String state : statesMapper.keySet()) {
            n_s.put(state, 0);
        }
    }

    // Set non-stochastic transitions to have probability 1
    public void initializeLearnedMatrix(){

        learnedMatrix = new double[m][m];
        for(int i = 0; i < m; i++){
            if(isStateStochastic(i)) continue;

            for(int j = 0; j < m; j++){
                learnedMatrix[i][j] = transitionMatrix[i][j];
            }
        }
    }

    public void generateK_i(){
        mStochastic = 0;

        int sumRow;
        for(int i = 0; i < m; i++){
            sumRow = 0;
            for(int j = 0; j < m; j++){
                sumRow = sumRow + transitionMatrix[i][j];
            }
            k_i.put(i, sumRow);
            if(sumRow > 1) mStochastic++;
        }
    }

    public int getNumberOfStates(){
        return statesMapper.size();
    }

    public void performBackwardReachabilty(){

        // For each state i in the model
        for(int i = 0; i < m; i++){
            // Start with i as the only state in W and R
            Set<Integer> W = new HashSet<>();
            Set<Integer> R = new HashSet<>();
            W.add(i);
            R.add(i);

            // Repeat for each state in W until no new states can be added to W
            while (!W.isEmpty()){
                int state = W.iterator().next();
                int[] row = transitionMatrix[state];
                // Use transitionMatrix to find all states t that have transition to i
                for(int j = 0; j < row.length; j++){
                    if(row[j] == 0) continue;

                    // If t not already in R, add t to R (reachable) and W (working set)
                    if(!R.contains(j)){
                        R.add(j);
                        W.add(j);
                    }
                }
                // Remove current state from W
                W.remove(state);
            }
            // Save R to some data structure so it can be used later
            reachability.put(i, R);
        }
    }

    public int[][] createTransitionMatrix(){
        return prismHandler.getTransitionMatrix(m);
    }

    public HashMap<String, String> getModelStates(){
        return prismHandler.getModelStates();
    }

    public void getModelVariables(){ variables = prismHandler.getModelVariables();}
}
