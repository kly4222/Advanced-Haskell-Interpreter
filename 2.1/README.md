Advanced Haskell Interpreter
============================
#####Version 2.1
####Date (06/02/2014)

######Changes Since 2.0
* Set recording mode on or off.
* Asks user whether function output is correct.
* Asks user whether they wish to record test in log file.
* Writes tests to a static log file "/Users/Hypho.Test.hs"
* Test recording done within Haskell instead of Java.

######Needs Improving:
* Record tests in the same directory as the loaded module.
* GHCi prompt is printed before the function output is given when recording mode is on.

######Does:
* Set recording mode on or off.
* Provides test input/output logging.
* Asks user if function output is correct.
* Asks whether the user wishes to record test.
* Able to write true and false tests.
* Correctly sends all inputs to the GHCi.
* Correctly sends all outputs to the GHCi.

######Next Version Implementations:
* Fix GHCi printing prompts when in recording mode.
