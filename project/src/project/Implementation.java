package project;

import java.util.Collection;

public abstract class Implementation {

    abstract String performSingleStep();
    abstract void executeAction(String action) throws Exception;
    abstract void runSimulation(int steps) throws Exception;
    abstract String chooseAction(Collection<String> possibleActions);
    abstract void generateConditionMap();
}
