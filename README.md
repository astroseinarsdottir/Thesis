# Learning probabilistic models from generated implementations of non-deterministic models
Implementation of a workflow for learning stochastic models by generating implementations of non-deterministic models and using a learning algorithm to estimate the stochastic properties of the model.

This project uses the PRISM API, documentation can be found on their [GitHub site](https://github.com/prismmodelchecker/prism-api)

Created for Master Thesis at DTU by Ástrós Einarsdóttir, June 2021.

## Setup instructions

Download a copy of the repository

* ``git clone https://github.com/astroseinarsdottir/Thesis``

Build prism

* ``cd prism/prism``
* ``make``

Build the project 

* ``cd ../..``
* ``cd project``
* ``make``

## Test the project

* ``make test``

You will be prompted to input an absolute path to a PRISM model file.
The model needs to be a non-probabilistic mdp and the file needs to have a .nm ending.

Some example models can be found under

``project/src/project/models``