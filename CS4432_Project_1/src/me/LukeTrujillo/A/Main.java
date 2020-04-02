package me.LukeTrujillo.A;

import java.util.Scanner;

public class Main {

	public static void main(String args[]) {
		
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
