Advanced Haskell Interpreter
============================
####Version 2.2
#####Date (10/02/2014)

######Changes Since 2.1
* GHCi prompt printing fixed.
* Implementation of recording mode using enumerated types

######Needs Improving:
* Record tests in the same directory as the loaded module.
* Fix user prompt when in recording mode.

######Does:
* Set recording mode on or off.
* Provides test input/output logging.
* Asks user if function output is correct.
* Asks whether the user wishes to record test.
* Able to write true and false tests.
* Correctly sends all inputs to the GHCi.
* Correctly sends all outputs to the GHCi.

######Next Version Implementations:
* Fix user prompt when in recording mode.
