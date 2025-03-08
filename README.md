# CPSC 473 Data Mining - Assignment 2: FP-growth
## Team: Artificial Stupidity
### Team Members:
Ben Dempsey - 230150566

Katie Killburn - 230140531

Rohan Soares - 230150718

## About
The purpose of this assignment is to implement the FP-growth algorithm for frequent pattern mining. FP-growth is an alternative datamining method that instead of creating costly candidate tables and scanning the database many times like in Apriori, it recursively grows frequent patterns by pattern and database partitioning.

In our progem, users pass the data base as a text file and the minimum support threshold to the program as arguments from the console. When the program has finished executing it ouputs the results (frequent itemsets) in a file named 'MiningResults_<database_name>.txt'. 

Additionally, the **run-time**, **minimum support** and **number of frequent patterns** are also calculated and written as output to the terminal.

## Directions

The main algorithm is implemented in the file: fpgrowth.java

To execute the program from the terminal run the command:

    java fpgrowth.java [Dataset_Name] [Minimum_Support]

        Where:
        - [Dataset_Name] is the name of a valid transaction database in the 'Data' folder
        - [Minimum_Support] is a real number that represents the minimum support threshold as a percent

The data files should be in a subdirectory called "./Data" 
## Examples

e.g. #1
(Insert image)

e.g.#2
(Insert image)

(add more examples...)
