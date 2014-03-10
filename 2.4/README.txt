Advanced Haskell Interpreter
Version 2.4
Date 12/02/2014

Changes Since 2.3
* Printing GHCi prompt in recording mode fixed.
* Recording mode can only be turned on if a module is loaded.
* Records tests in a file in the same directory as the loaded module

Needs Improving:
* Loading multiple modules.

Does:
* Write tests in a file in the same directory as the loaded module.
* Set recording mode on or off.
* Provides test input/output logging.
* Asks user if function output is correct.
* Asks whether the user wishes to record test.
* Able to write true and false tests.
* Correctly sends all inputs to the GHCi.
* Correctly sends all outputs to the GHCi.

Next Version Implementations:
* Creating HUnit Test file