package project.prototype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Switch {

    public static void main( String[] args )
    {
        Switch switch1 = new Switch(10);
    }
    
    // Local variables:
    public int state;

    // Hashmap where the key = value of condition, value = list of transitions that fulfill condition
    // Could be extended such that multiple types of conditions result in different hashmaps.
    // Use:
    //List<Integer> common = new ArrayList<Integer>(listA);
    //common.retainAll(listB);
    // common now contains only the elements which are contained in listA and listB.
    // To find out which transitions fulfill all conditions.
    public HashMap<Integer, List<String>> actionsByConditionMap = new HashMap<Integer, List<String>>();

    Random random = new Random();

    public Switch(int numSteps) {
        generateHashMaps();

        state = 0;
        try{
            runSimulation(numSteps);
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
        
    }

    // Runs the simulation for the system with the desired number of steps
    public void runSimulation(int steps) throws Exception {
        System.out.println("Initial state: " + getState());
        while(steps > 0){
            // Find out which transistions could be taken given the state
            List<String> possibleActions = getPossibleActions(getState());
            
            String action;

            // If there is only 1 possible actions, then that is the action performed by default.
            if(possibleActions.size() == 1){
                action = possibleActions.get(0);
            }
            // Otherwise, we have to choose which of the possible actions to perform next.
            // Use a random number generator to decide which transition to take next
            // Use thread that simulates the user, with different distributions to decide which action to take.
            else{
                action = chooseAction(possibleActions);
            }
            
            // Perform the transision
            switch(action) {
                case "action1":
                    setState(1);
                    break;
                case "action2":
                    setState(0);
                    break;
                case "action3":
                    // Self loop
                    break;
                case "action4":
                    // Self loop
                    break;
                
                default:
                    throw new Exception("No action chosen");
            }
            
            // Log which transition was taken
            System.out.println("Action: " + action + " State: " + getState());
            // Could also check if any labels are satisfied and print that out?
            steps--;
        }
    }

    public void setState(int i) throws IllegalArgumentException{
        if(i < 0 || i > 1){
            throw new IllegalArgumentException("State can only be between 0 and 1");
        }
        state = i;
    }
    
    public int getState(){
        return state;
    }

    // Create the map where we can look up transitions by their conditions
    public void generateHashMaps(){
        List<String> condition1 = new ArrayList<>();
        condition1.add("action1");
        condition1.add("action3");
        actionsByConditionMap.put(0, condition1);

        List<String> condition2 = new ArrayList<>();
        condition2.add("action2");
        condition2.add("action4");
        actionsByConditionMap.put(1, condition2);
    }

    // Return a list of possible actions that could be performed given the state
    // Could be extended to include more parameters given more variables
    public List<String> getPossibleActions(int state){
        return actionsByConditionMap.get(state);
    }

    // Choose which action to perform next out of many possible actions.
    // Currently chooses action by random, but could later by extended to simulate user choose/bias
    public String chooseAction(List<String> possibleActions){
        return possibleActions.get(random.nextInt(possibleActions.size()));   
    }

            
}
