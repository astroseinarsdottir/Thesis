dtmc 

module client
  state : [0..1] init 0; // State of the job (inactive/active)
  task  : [0..5] init 0; // Length of the job
  
  // Create a new job - length chose non-deterministically

  // Serve the job

  // Complete the job

  //[] state=0 | task=0 -> true;

[] state=0 & task=0 -> 0.196 : (state'=1) & (task'=1) +  0.1912 : (state'=1) & (task'=2) +  0.2116 : (state'=1) & (task'=3) +  0.1992 : (state'=1) & (task'=4) +  0.202 : (state'=1) & (task'=5);
[] state=1 & task=0 -> 1.0 : (state'=0) & (task'=0);
[] state=1 & task=1 -> 1.0 : (state'=1) & (task'=0);
[] state=1 & task=2 -> 1.0 : (state'=1) & (task'=1);
[] state=1 & task=3 -> 1.0 : (state'=1) & (task'=2);
[] state=1 & task=4 -> 1.0 : (state'=1) & (task'=3);
[] state=1 & task=5 -> 1.0 : (state'=1) & (task'=4);
endmodule



