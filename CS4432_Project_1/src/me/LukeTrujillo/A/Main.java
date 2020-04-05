package me.LukeTrujillo.A;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Main {

	public static void main(String args[]) {

		File student_table = new File("Student");

		if(student_table.exists()) {
			student_table.delete();
		}
		
		if (!student_table.exists()) {
			student_table.mkdir();

			for (int x = 0; x < 300; x++) {
				try {
					File file = new File("Student/F" + (x + 1));
					file.createNewFile();
					
					FileWriter fw = new FileWriter(file);
					
					for(int y = 0; y < 100; y++) {
						
							String line = "F";
							
							if((x + 1) < 10) {
								line += "0" + (x + 1);
							} else {
								line += (x + 1);
							}
							
							
							if(y < 10) {
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
						
						fw.write(line + System.lineSeparator());
					}
					
					fw.close();
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("done");

		// setup the number of frames from the item

		BufferPool manager = new BufferPool(3);
		
		Scanner scanner = new Scanner(System.in); // get the text input

		System.out.println("Ready for input");
		while (true) {
			try {
				System.out.println();
				String text = scanner.nextLine();

				String input[] = text.split(" ", 3);
				
				int address = Integer.parseInt(input[1]);

				if (input.length >= 2) {
					if (input[0].equalsIgnoreCase("GET")) { // do the get command
						
						manager.getRecord(address);

					} else if (input[0].equalsIgnoreCase("PIN")) {
						manager.pin(address);
						
					} else if (input[0].equalsIgnoreCase("UNPIN")) {
						manager.unpin(address); 
					} else if (input.length >= 3) {
						if (input[0].equalsIgnoreCase("SET")) {
							manager.setRecord(address, input[2]);
						}

					}

				} else {
					System.out.println("Invalid number of arguments");
				}

			} catch (NumberFormatException e) {
				System.out.println("Invalid number argument given");
			}
		}
		
	}
}
