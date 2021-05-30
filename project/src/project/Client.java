package project;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.Set;

public class Client extends Implementation {

    public int state;
    public int task;

    // Multimap where the key = the condition (as a string), values = list of
    // transitions corresponding to that condition
    Multimap<String, String> multimap;

    // Used currently to choose which action to perform next
    Random random = new Random();

    public static void main(String[] args) {

        Client test = new Client(10);
    }

    public Client(int numSteps) {
        state=0;
        task=0;

        generateConditionMap();

        try {
            runSimulation(numSteps);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public Client(){
        state=0;
        task=0;

        generateConditionMap();
    }

    // Perform a single step in the system
    public String performSingleStep() {

        // Find out which transistions could be taken given the state

        Collection<String> possibleActions = getAllPossibleActions(getstate() ,gettask() );


        String action = "";

        // If there is only 1 possible actions, then that is the action performed by
        // default.
        if (possibleActions.size() == 1) {
            action = possibleActions.iterator().next();
        }
        // Otherwise, we have to choose which of the possible actions to perform next.
        // Use a random number generator to decide which transition to take next
        // Use thread that simulates the user, with different distributions to decide
        // which action to take.
        else {
            action = chooseAction(possibleActions);
        }

        // Perform the transision
        try {
            executeAction(action);
        } catch (Exception e) {
            // Auto-generated catch block
            e.printStackTrace();
        }

        return action;
    }

    // Perform the transision/action given as parameter
    public void executeAction(String action) throws Exception {

        switch (action) {

        case "action0":
            setstate(1);
            settask(1);
            break;
        case "action1":
            setstate(1);
            settask(2);
            break;
        case "action2":
            setstate(1);
            settask(3);
            break;
        case "action3":
            setstate(1);
            settask(4);
            break;
        case "action4":
            setstate(1);
            settask(5);
            break;
        case "action5":
            settask(task-1);
            break;
        case "action6":
            setstate(0);
            break;
        default:
            throw new Exception("No action chosen");
        }

    }

    // Runs the simulation for the system with the desired number of steps
    public void runSimulation(int steps) throws Exception {

        while (steps > 0) {
            // Find out which transistions could be taken given the state

        Collection<String> possibleActions = getAllPossibleActions(getstate() ,gettask() );


            String action = "";

            // If there is only 1 possible actions, then that is the action performed by
            // default.
            if (possibleActions.size() == 1) {
                action = possibleActions.iterator().next();
            }
            // Otherwise, we have to choose which of the possible actions to perform next.
            // Use a random number generator to decide which transition to take next
            // Use thread that simulates the user, with different distributions to decide
            // which action to take.
            else {
                action = chooseAction(possibleActions);
            }

            // Perform the transision
            executeAction(action);

            // Log which transition was taken
            System.out.println("Action: " + action);
            // Could also check if any labels are satisfied and print that out?
            steps--;
        }
    }
    public void setstate(int i) throws IllegalArgumentException {
        if (i < 0 || i > 1) {
            throw new IllegalArgumentException("state can only be between 0 and 1");        }
        state = i;
    }
    public int getstate() {
        return state;
    }

    public void settask(int i) throws IllegalArgumentException {
        if (i < 0 || i > 5) {
            throw new IllegalArgumentException("task can only be between 0 and 5");        }
        task = i;
    }
    public int gettask() {
        return task;
    }


    // Choose which action to perform next out of many possible actions.
    // Currently chooses action by random, but could later by extended to simulate
    // user choose/bias
    public String chooseAction(Collection<String> possibleActions) {
        ArrayList<String> listActions = new ArrayList<>(possibleActions);
        return listActions.get(random.nextInt(listActions.size()));
    }

    // Create the map where we can look up transitions by their conditions
    public void generateConditionMap() {
        multimap = ArrayListMultimap.create();

        multimap.put("state==0", "action0");
        multimap.put("state==0", "action1");
        multimap.put("state==0", "action2");
        multimap.put("state==0", "action3");
        multimap.put("state==0", "action4");
        multimap.put("state==1 && task>0", "action5");
        multimap.put("state==1 && task==0", "action6");
    }

    // Return a list of possible actions that could be performed given the current
    // state (value of variables)
    public Collection<String> getAllPossibleActions(int currentstate,int currenttask) {        Set<String> allKeys = multimap.keySet();

        Collection<String> possibleAction = new ArrayList<String>();

        try {
            ScriptEngine graalEngine = new ScriptEngineManager().getEngineByName("Graal.js");

            graalEngine.eval("state=" + String.valueOf(currentstate));
            graalEngine.eval("task=" + String.valueOf(currenttask));

            for (String condition : allKeys) {
                Object result = graalEngine.eval("eval('" + condition + "');");
                if (Boolean.TRUE.equals(result)) {
                    possibleAction.addAll(multimap.get(condition));
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return possibleAction;

    }

    //// Helper functions for the learning algorithm ////

    public int getNumberofTransitions(){
        return multimap.values().size();
    }

    public Collection<String> getListOfTransitions(){
        return multimap.values();
    }
    public String getSystemState(){
        return  MessageFormat.format("({0},{1})", getstate() ,gettask() );
    }

}