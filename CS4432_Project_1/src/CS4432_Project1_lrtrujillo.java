

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class CS4432_Project1_lrtrujillo {

	public static void main(String args[]) {

		File student_table = new File("Student");

		if (!student_table.exists()) { //if the files arent set
			student_table.mkdir(); //make the dir

			for (int x = 0; x < 300; x++) { //and generate the files
				try {
					File file = new File("Student/F" + (x + 1));
					file.createNewFile();
					
					FileWriter fw = new FileWriter(file);
					
					for(int y = 0; y < 100; y++) { //for each file write the default records
						
							String line = "F";
							
							if((x + 1) < 10) { //formatting things
								line += "0" + (x + 1);
							} else {
								line += (x + 1);
							}
							
							
							if(y < 10) { //formatting things
								line += "-Rec" + x + "0" + y + ", ";
								line += "Name" +  x + "0" + y + ", ";
								line += "address" +  x + "0" + y + ", ";
								line += "age" +  x + "0" + y + ".";
							} else {
								line += "-Rec" + x + "" + y + ", ";
								line += "Name" +  x + "" + y + ", ";
								line += "address" +  x + "" + y + ", ";
								line += "age" +  x + "" + y + ".";
							}
						
						fw.write(line + System.lineSeparator());  //make sure to add a line break
					}
					
					fw.close();
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		if(args.length != 1) { //invalid amount of arguments
			System.out.println("A numeric argument must be supplied to specify the number of frames. Please try again.");
			return;
		}
		
		BufferPool manager;
		try {
			 manager = new BufferPool(Integer.parseInt(args[0]));
		} catch(NumberFormatException e) {
			System.out.println("A numeric argument must be supplied to specify the number of frames. Please try again.");
			return;
		}
		System.out.println("The program is ready for the next command"); //ready for input
		
		Scanner scanner = new Scanner(System.in);

		while (true) {
			try {
				System.out.println(); //print line to match formatting
				String text = scanner.nextLine(); //wait for user input

				String input[] = text.split(" ", 3); //split up the arguments
				
				int address = Integer.parseInt(input[1]); //the second argument is the address

				if (input.length >= 2) { //if args
					if (input[0].equalsIgnoreCase("GET")) { // do the get command
						
						manager.getRecord(address);

					} else if (input[0].equalsIgnoreCase("PIN")) { //do the pin command
						manager.pin(address);
						
					} else if (input[0].equalsIgnoreCase("UNPIN")) { //do the unpin commnad
						manager.unpin(address); 
					} else if (input.length >= 3) { //if there are more than 2 arguments
						if (input[0].equalsIgnoreCase("SET")) { //do the set command
							manager.setRecord(address, input[2]);
						}

					}

				} else {
					System.out.println("Invalid number of arguments. Please try again"); //error message will just go to next command
				}

			} catch (NumberFormatException e) {
				System.out.println("Invalid number argument given");
			} catch(Exception e) {
				System.out.println("An exception has occurred. Please try again.");
			}
		}
		
	}
}
