


import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Frame {

	public static final int RECORDS_PER_FRAME = 100;
	
	
	private boolean dirty, pinned, isEmpty;
	private int blockID;
	private String content[];
	
	/**
	 * Setup the default values
	 */
	public Frame() {
		this.blockID = -1;
		this.dirty = false;
		this.pinned = false;
		this.content = new String[RECORDS_PER_FRAME];
		this.isEmpty = true;
	}
	
	/**
	 * This will load the block from the file into the given frame
	 * @param blockID
	 * @return The frame(this instance of the object) which has the blockID loaded into
	 */
	public Frame load(int blockID) {
		this.blockID = blockID;
		this.dirty = false; //reset the flags
		this.pinned = false;
		this.isEmpty = false;
		
		
		Path path = Paths.get("Student/F" + blockID + ".txt"); //setup the path
		
		try {
			this.content = Files.readAllLines(path).get(0).split("(?<=\\G.{40})");
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return this; //return this instance of the object
	}
	
	/**
	 * Evict this instance of Frame
	 * @return false if unsuccessful, true if successfiul
	 */
	public boolean evict() {
		if(pinned) //if its pinned
			return false; //then it can be evicted
		
		if(dirty) { //then we need to write out the block
			try {
				FileWriter writer = new FileWriter("Student/F" + blockID + ".txt");
				
				for(String line : content) {
					writer.write(line); //write out each line of the content
				}
				writer.close();
			
				
			} catch(IOException e) {
				e.printStackTrace();
			}
			
			dirty = false; //no longer a dirty bit
		}
		isEmpty = true;
		return true; //successfully written out
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

	public boolean isEmptyFrame() {
		return isEmpty;
	}
	
	/**
	 * This returns the record at a given offset
	 * @param offset
	 * @return the requested record or null if invalid
	 */
	public String getRecord(int offset) {
		if((offset - 1) < content.length && offset > -1) {
			return content[offset - 1];
		} 
		return null;
	}
	
	/**
	 * This sets the record at the given offset
	 * @param offset the offset to be set at
	 * @param record the record to be set
	 */
	public void setRecord(int offset, String record) {
		if((offset - 1) < content.length && offset > -1) {
			content[offset - 1] = record;
			dirty = true;
		}
	}
}
