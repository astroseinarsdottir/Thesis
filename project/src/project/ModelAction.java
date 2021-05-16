package project;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class ModelAction {

    private String condition;
    private List<String> variablesToUpdate;
    private List<String> updates;

    public ModelAction(){
        variablesToUpdate = new ArrayList<>();
        updates = new ArrayList<>();
    }

    public void setCondition(String condition){
        this.condition = condition;
    }
    public String getCondition(){
        return condition;
    }

    public void addVariableToUpdate(String variable){
        variablesToUpdate.add(variable);
    }
    public List<String> getVariablesToUpdate(){
        return variablesToUpdate;
    }
    public void addUpdate(String update){
        this.updates.add(update);
    }
    public List<String> getUpdates(){
        return updates;
    }

    @Override
    public String toString(){
        return MessageFormat.format("(Condition: {0}, VariablesToUpdate: {1}, Updates: {2})", getCondition(), getVariablesToUpdate(), getUpdates());
    }

}
