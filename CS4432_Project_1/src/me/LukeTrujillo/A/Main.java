package me.LukeTrujillo.A;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {

	public static void main(String args[]) {
		
		File student_table = new File("Student");
		
		if(!student_table.exists()) {
			student_table.mkdir();
			
			for(int x = 0; x < 300; x++) {
				try {
					new File("Student/F" + (x + 1)).createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("done");
		
		//setup the number of frames from the item
		
		BufferManager manager = new BufferManager(3);
		
		Scanner scanner = new Scanner(System.in); //get the text input
		while(true) {
			String input = scanner.nextLine();
			
			
			
			if(input.startsWith("GET")) { //do the get command 
				
			} else if(input.startsWith("SET")) {
				
			} else if(input.startsWith("PIN")) {
				
			} else if(input.startsWith("UNPIN")) {
				
			} else { //invalid command
				//do something
			}
			
			
			
		}
		
		
	}
}
