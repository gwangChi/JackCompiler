# JackCompiler
Compiles the high level Jack language files under a directory to the Virtual Machine language specified in the textbook Nand2Tetris (https://www.nand2tetris.org/).
# COMPILATION AND RUNNING OF THE JACK COMPILER PROGRAM
* To compile .jack files, first navigate inside the directory (../src/) in your command line/terminal where Driver.java is located (inside src/), then type in:
	javac Driver.java

* To run the compiled DriverDriver.class file on JVM (Java Virtual Machine), type in:
	java Driver
  
  Then a message will then appear to prompt you enter the file directory where .jack files are
  located.
  There are two options at this stage:

  1. Paste the whole directory in the output terminal and press enter. The compiled .vm files will
     show up in that same directory where .jack files are located.
  2. Hit enter directly without entering any string, then the compiler will compile all .jack files
     in the ../src/ directory, and the compiled .vm files will also be in the ../src/ directory.

# NOTE
*** Should have JAVA 8 or later versions installed.
* Our desired output is <filename>.vm, you can ignore the <filename>.xml files that is generated in
  the same directory. The <filename>.xml file is the parse tree of that <filename>.jack class file.
* It works on Linux/Windows systems with JVM installed.
* The size of the input files should be small enough (<< memory) for the program to work, as it reads
  all .jack files in the entered directory and parse them into a big syntax tree at once.
