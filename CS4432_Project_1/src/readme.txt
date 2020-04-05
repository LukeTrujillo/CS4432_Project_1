Luke Trujillo (251890256)

Section 1: how to compile

	1. use cmd/terminal to navigate to the location of the source files
	
	2. use the command "java CS4432-Project1-lrtrujillo.java #" where  # is the number of frames to run
	
	3. Wait for the "ready for input message" 
	
	4. Enter valid commands and testcases
	
Section 2: Test Results

	My program passed all of the test cases supplied with accuracy. All commands are working.
	
Section 3: Design Decisions
	
	Project guidelines were unclear if we needed to generate the files in the Student 
	directory ourselves or if we assume they are there. If there is no local directory
	called Student my program will generate it and store files accordingly.
	
	I adhered to the project's suggest structure using a BufferPool and a Frame class. In
	the Frame class, I added the variable isEmpty to make a distinction between a recently 
	evicted frame from an empty one.