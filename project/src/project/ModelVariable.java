package project;

import java.text.MessageFormat;

public class ModelVariable {

    private String name;
    private char rangeFrom;
    private char rangeTo;
    private char initialState;

    public ModelVariable(){

    }

    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }

    public void setRangeFrom(char rangeFrom){
        this.rangeFrom = rangeFrom;
    }
    public char getRangeFrom(){
        return rangeFrom;
    }

    public void setRangeTo(char rangeTo){
        this.rangeTo = rangeTo;
    }
    public char getRangeTo(){
        return rangeTo;
    }

    public void setInitialState(char initialState){
        this.initialState = initialState;
    }
    public char getInitialState(){
        return initialState;
    }

    @Override
    public String toString(){
        return MessageFormat.format("(Name: {0}, Range:[{1}..{2}], Init: {3})", getName(), getRangeFrom(), getRangeTo(), getInitialState());
    }

}
