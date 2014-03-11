Advanced Haskell Interpreter
============================
####Version 2.6
#####Date 17/02/2014

######Changes Since 2.5
* Prevent duplicate test cases.

######Needs Improving:
* Loading multiple modules.
* Asking the user if output is correct when an error message is thrown by GHCi.

######Does:
* Write tests in a file in the same directory as the loaded module.
* Writes tests to an xml file.
* Set recording mode on or off.
* Provides test input/output logging.
* Asks user if function output is correct.
* Asks whether the user wishes to record test.
* Correctly sends all inputs to the GHCi.
* Correctly sends all outputs to the GHCi.

######Next Version Implementations:
* Creating HUnit Test file
