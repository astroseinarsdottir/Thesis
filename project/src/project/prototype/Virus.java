/*package test;

// The Controller is a wrapper that initialises all modules
// Runs the simulations and performs actions.
class Controller{

    private final int s = 0;
    private final int i = 1;
    private final int r = 2;
    private Virus virus;
    private SIR sir;

    public Controller(){
        virus = new Virus();
        sir = new SIR();
        System.out.println("Initial state: " + sir.getState() + "," + virus.getCharge());
        run(10);
    }

    // Runs the simulation for the system
    public void run(int steps){
        while(steps > 0){
            // Find out which actions could be taken given the state
            
            // Use a random number generator to decide which actions to take next
            // Thread that simulates the user, with different distributions.

            // Perform the action
            
            // Log the current state
            System.out.println(sir.getState() + "," + virus.getCharge());
            // Could also check if any labels are satisfied and print that out?
            steps--;
        }
    }
}

// Module Virus
// Contains setters and getters for all varibles related to that module
class Virus {
    private int charge;

    // Set the inital state of all the variable for this module
    public Virus(){
        charge = 1;
    }

    // Make sure that the value of the variable is not out of bounds
    public void setCharge(int i) throws IllegalArgumentException{
        if(i < 0 || i > 1){
            throw new IllegalArgumentException("Charge can only be between 0 and 1");
        }
        charge = i;
    }
    
    public int getCharge(){
        return charge;
    }
    
}

// Module SIR
// Contains setters and getters for all varibles related to that module
class SIR{
    private int state;

    // Set the inital state of all the variable for this module
    public SIR(){
        state = 0;
    }

    // Make sure that the value of the variable is not out of bounds
    public void setState(int i) throws IllegalArgumentException{
        if(i> 2 || i < 0){
            throw new IllegalArgumentException("State can onlu be between 0 and 2");
        }
        state = i;
    }

    public int getState(){
        return state;
    }

}*/
