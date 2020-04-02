package me.LukeTrujillo.A;

import java.util.ArrayList;

public class BufferPool {
	private Frame contents[];
	
	
	public BufferPool(int max_frames) {
		this.contents = new Frame[max_frames];
		
		
		for(int x = 0; x < contents.length; x++) {
			contents[x] = new Frame();
			contents[x].initialize();
		}
	}
	
	
}
