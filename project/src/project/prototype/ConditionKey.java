package project.prototype;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ConditionKey {
    // The condition for the action
    private String condition;

    // The variables to be evaluated
    private Integer state;
    private Integer task;
    
    
    //private ScriptEngine engine;

    public ConditionKey(String condition){
        this.condition = condition;
    }

    public ConditionKey(Integer state, Integer task){
        this.state = state;
        this.task = task;
    }

    public String getCondition(){
        return condition;
    }

    @Override
    public boolean equals(Object o) {
        try{
            if(o instanceof ConditionKey){
                Integer state = ((ConditionKey)o).state;
                Integer task = ((ConditionKey)o).task;
                //System.out.println("State: " + state + " Task: " + task);
                //System.out.println("This is the condition: " +condition);


                //ScriptEngineManager manager = new ScriptEngineManager(null);
                //ScriptEngine engine = manager.getEngineByExtension("javascript");
                //Object result = engine.eval("4*5");
                //ScriptEngine engine =  new org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory().getScriptEngine();

                ScriptEngine graalEngine = new ScriptEngineManager().getEngineByName("graal.js");

                graalEngine.eval("state=" + String.valueOf(state));
                graalEngine.eval("task= " + String.valueOf(task));

                Object result = graalEngine.eval("eval('" + condition + "');");
                return Boolean.TRUE.equals(result);
            }
            return false;
        }catch (ScriptException e)
        {
          throw new IllegalArgumentException("invalid format");
        }
        
    }

    @Override
	public int hashCode() {
        if(condition != null) return condition.hashCode();
        else return state.hashCode() + 31 * task.hashCode();
	}

    @Override
    public String toString() {
        return condition;
    }
    
}
