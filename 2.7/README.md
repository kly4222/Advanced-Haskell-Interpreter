Advanced Haskell Interpreter
============================
#### Version 2.7
##### Date (18/02/2014)

######Changes Since 2.6
* Creates a HUnit file.
* Performs regression testing on a single module.

######Needs Improving:
* Loading multiple modules.
* Asking the user if output is correct when an error message is thrown by GHCi.

######Does:
* Write tests in a file in the same directory as the loaded module.
* Writes tests to an xml file.
* Prevents duplicate xml entries.
* Set recording mode on or off.
* Provides test input/output logging.
* Asks user if function output is correct.
* Asks whether the user wishes to record test.
* Correctly sends all inputs to the GHCi.
* Correctly sends all outputs to the GHCi.

######Next Version Implementations:
* Recording when error messages occur.
