package project;

import java.text.MessageFormat;

public class ModelVariable {

    private String name;
    private String rangeFrom;
    private String rangeTo;
    private String initialState;

    public ModelVariable(){

    }

    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }

    public void setRangeFrom(String rangeFrom){
        this.rangeFrom = rangeFrom;
    }
    public String getRangeFrom(){
        return rangeFrom;
    }

    public void setRangeTo(String rangeTo){
        this.rangeTo = rangeTo;
    }
    public String getRangeTo(){
        return rangeTo;
    }

    public void setInitialState(String initialState){
        this.initialState = initialState;
    }
    public String getInitialState(){
        return initialState;
    }

    @Override
    public String toString(){
        return MessageFormat.format("(Name: {0}, Range:[{1}..{2}], Init: {3})", getName(), getRangeFrom(), getRangeTo(), getInitialState());
    }

}
