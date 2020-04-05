

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Frame {

	public static final int RECORDS_PER_FRAME = 100;
	
	
	private boolean dirty, pinned, isEmpty;
	private int blockID;
	private String content[];
	
	
	public Frame() {
		this.blockID = -1;
		this.dirty = false;
		this.pinned = false;
		this.content = new String[RECORDS_PER_FRAME];
		this.isEmpty = true;
	}
	
	
	public Frame load(int blockID) {
		this.blockID = blockID;
		this.dirty = false;
		this.pinned = false;
		
		this.isEmpty = false;
		
		
		Path path = Paths.get("Student/F" + blockID);
		
		try {
			ArrayList<String> lines = (ArrayList<String>) Files.readAllLines(path);
			
			this.content = new String[RECORDS_PER_FRAME];
			
			for(int x = 0; x < content.length && x < lines.size(); x++) {
				this.content[x] = lines.get(x);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return this;
	}
	
	public boolean evict() {
		if(pinned) 
			return false;
		
		if(dirty) { //then we need to write out the block
			try {
				FileWriter writer = new FileWriter("Student/F" + blockID);
				
				for(String line : content) {
					writer.write(line + System.lineSeparator());
				}
					
				writer.close();
			
				
			} catch(IOException e) {
				e.printStackTrace();
			}
			
			dirty = false; //no longer a dirty bit
		}
		
		isEmpty = true;
		
		
		return true;
	}
	

	public boolean isDirty() {
		return dirty;
	}


	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}


	public boolean isPinned() {
		return pinned;
	}


	public void setPinned(boolean pinned) {
		this.pinned = pinned;
	}


	public int getBlockID() {
		return blockID;
	}


	public void setBlockID(int blockID) {
		this.blockID = blockID;
	}


	public boolean isEmptyFrame() {
		return isEmpty;
	}
	
	public String getRecord(int offset) {
		if(offset < content.length) {
			return content[offset];
		} 
		return null;
	}
	
	public void setRecord(int offset, String record) {
		if(offset < content.length) {
			content[offset] = record;
			dirty = true;
		}
	}
}
