/*

NOT USED - PROTOTYPE

package project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class Client {

    // Local variables
    public int state;
    public int task;

    // Hashmap where the key = value of condition, value = list of transitions that fulfill condition
    // Could be extended such that multiple types of conditions result in different hashmaps.
    // Use:
    //List<Integer> common = new ArrayList<Integer>(listA);
    //common.retainAll(listB);
    // common now contains only the elements which are contained in listA and listB.
    // To find out which transitions fulfill all conditions.
    public HashMap<Integer, List<String>> actionsBySateConditionMap = new HashMap<Integer, List<String>>();
    public HashMap<Integer, List<String>> actionsByTaskConditionMap = new HashMap<Integer, List<String>>();
    public HashMap<StateTaskKey, List<String>> actionsByStateAndTaskConditionMap = new HashMap<StateTaskKey, List<String>>();
    //public HashMap<ConditionKey, String> conditionMap = new HashMap<ConditionKey, String>();
    Multimap<String, String> multimap; 

    Random random = new Random();

    public static void main( String[] args )
    {
        Client client = new Client(10);
    }

    public Client(int numSteps) {
        //generateHashMaps();

        state = 1; // State of the job (inactive/active)
        task = 0; // Length of the job

        /*try{
            runSimulation(numSteps);
        } catch(Exception e){
            System.out.println(e.getMessage());
        }*/

        //generateConditionMap();

        // Test the condition map:

        //System.out.println(conditionMap.get(new ConditionKey(getState(), getTask())));

        //System.out.println(conditionMap.get(new ConditionKey("state==0")));

        /*ScriptEngine graalEngine = new ScriptEngineManager().getEngineByName("graal.js");
        try{
            graalEngine.eval("state=0");
            graalEngine.eval("task=0");

            Object result = graalEngine.eval("eval(state==1);");
            System.out.println(Boolean.TRUE.equals(result));
        } catch(Exception e){

        }*/

        /*generateMultimap();

        getAllPossibleActions();
        
        
    }

    // Runs the simulation for the system with the desired number of steps
    public void runSimulation(int steps) throws Exception {
        System.out.println("Initial state: " + getState() + ", " + getTask());
        while(steps > 0){
            // Find out which transistions could be taken given the state
            List<String> possibleActions = getPossibleActions(getState(), getTask());
            
            String action = "";

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
                    setTask(1);
                    break;
                case "action2":
                    setState(1);
                    setTask(2);
                    break;
                case "action3":
                    setState(1);
                    setTask(3);
                    break;
                case "action4":
                    setState(1);
                    setTask(4);
                    break;
                case "action5":
                    setState(1);
                    setTask(5);
                    break;
                case "action6":
                    setTask(getTask()-1);
                    break;
                case "action7":
                    setState(0);
                    break;
                case "action8":
                    // Do nothing
                    break;
                default:
                    throw new Exception("No action chosen");
            }
            
            // Log which transition was taken
            System.out.println("Action: " + action + " State: " + getState() + " Task: " + getTask());
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

    public void setTask(int i) throws IllegalArgumentException{
        if(i < 0 || i > 5){
            throw new IllegalArgumentException("Task can only be between 0 and 5");
        }
        task = i;
    }
    
    public int getTask(){
        return task;
    }

    // Create the map where we can look up transitions by their conditions
    public void generateHashMaps(){
        List<String> condition1 = new ArrayList<>();
        condition1.add("action1");
        condition1.add("action2");
        condition1.add("action3");
        condition1.add("action4");
        condition1.add("action5");
        condition1.add("action8");
        actionsBySateConditionMap.put(0, condition1);

        List<String> condition2 = new ArrayList<>();
        condition2.add("action8");
        actionsByTaskConditionMap.put(0, condition2);


        actionsByStateAndTaskConditionMap.put(new StateTaskKey(1,5), new ArrayList<String>(Arrays.asList("action6")));
        actionsByStateAndTaskConditionMap.put(new StateTaskKey(1,4), new ArrayList<String>(Arrays.asList("action6")));
        actionsByStateAndTaskConditionMap.put(new StateTaskKey(1,3), new ArrayList<String>(Arrays.asList("action6")));
        actionsByStateAndTaskConditionMap.put(new StateTaskKey(1,2), new ArrayList<String>(Arrays.asList("action6")));
        actionsByStateAndTaskConditionMap.put(new StateTaskKey(1,1), new ArrayList<String>(Arrays.asList("action6")));
        actionsByStateAndTaskConditionMap.put(new StateTaskKey(1,0), new ArrayList<String>(Arrays.asList("action7")));
    }

    // Return a list of possible actions that could be performed given the state
    // Could be extended to include more parameters given more variables
    public List<String> getPossibleActions(int state, int task){
        List<String> list1 = actionsBySateConditionMap.get(state);
        List<String> list2 = actionsByStateAndTaskConditionMap.get(new StateTaskKey(state, task));
        List<String> list3 = actionsByTaskConditionMap.get(task);
        
        // Combine all possible actions and make sure there are no duplicates
        Set<String> set = new LinkedHashSet<>();
        if(list1 != null) set.addAll(list1);
        if(list2 != null) set.addAll(list2);
        if(list3 != null) set.addAll(list3);
        List<String> combinedList = new ArrayList<>(set);

        return combinedList;
    }

    // Choose which action to perform next out of many possible actions.
    // Currently chooses action by random, but could later by extended to simulate user choose/bias
    public String chooseAction(List<String> possibleActions){
        return possibleActions.get(random.nextInt(possibleActions.size()));   
    }

    /*public void generateConditionMap(){
        
        // Create a new job - length chose non-deterministically
        conditionMap.put(new ConditionKey("state==0"), "action1");
        conditionMap.put(new ConditionKey("state==0"), "action2");
        conditionMap.put(new ConditionKey("state==0"), "action3");
        conditionMap.put(new ConditionKey("state==0"), "action4");
        conditionMap.put(new ConditionKey("state==0"), "action5");

        // Serve the job
        conditionMap.put(new ConditionKey("state==1 & task>0"), "action6");

        // Complete the job
        conditionMap.put(new ConditionKey("state==1 & task==0"), "action7");

        //[] state=0 | task=0 -> true;
    }*/

    /*public void generateMultimap(){
        multimap = ArrayListMultimap.create();

        // Create a new job - length chose non-deterministically
        multimap.put("state==0", "action1");
        multimap.put("state==0", "action2");
        multimap.put("state==0", "action3");
        multimap.put("state==0", "action4");
        multimap.put("state==0", "action5");

        // Serve the job
        multimap.put("state==1 && task>0", "action6");

        // Complete the job
        multimap.put("state==1 && task==0", "action7");

        multimap.put("state==0 || task==0", "action8");
    }

    public Collection<String> getAllPossibleActions(){
        Set<String> allKeys = multimap.keySet();

        Collection<String> possibleAction = new ArrayList<String>();

        ScriptEngine graalEngine = new ScriptEngineManager().getEngineByName("graal.js");
        try{
            graalEngine.eval("state="+ String.valueOf(getState()));
            graalEngine.eval("task="+ String.valueOf(getTask()));

            for (String condition : allKeys) {
                Object result = graalEngine.eval("eval('"+condition+"');");
                if(Boolean.TRUE.equals(result)){
                    possibleAction.addAll(multimap.get(condition));
                }
            }    
        } catch(Exception e){
            System.out.println(e.getMessage());
        }

        return possibleAction;

    }
    
}*/

    

    

            

