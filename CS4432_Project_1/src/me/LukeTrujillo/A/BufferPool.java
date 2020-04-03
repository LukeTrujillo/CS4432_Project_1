package me.LukeTrujillo.A;


public class BufferPool {
	private Frame contents[];
	
	private int evictIndex;
	
	public BufferPool(int max_frames) {
		this.contents = new Frame[max_frames];
		this.evictIndex = 0;
		
		for(int x = 0; x < contents.length; x++) {
			contents[x] = new Frame();
		}
	}
	
	
	public boolean isAlreadyLoadedInMemory(int blockID) {
		for(int x = 0; x < contents.length; x++) {
			if(contents[x].getBlockID() == blockID) {
				return true;
			}
		}
		
		return false;
	}


	public boolean evictableFramePresent() {
		for(int x = 0; x < contents.length; x++) {
			if(contents[x].isPinned() == false) {
				return true;
			}
		}
		return false;
	}
	
	public boolean insertBlock(int blockID, int insertIndex) {
		return true;
	}


	public Frame loadBlock(int blockID) {
		
		int insertIndex = -1;
		
		for(int x = 0; x < contents.length; x++) {
			if(contents[x].getBlockID() == blockID) { //check if a block exists
				return contents[x];
			}
			
			if(contents[x].isEmptyFrame()) {
				insertIndex = x; 
				break;
			}
		}
		
		if(insertIndex == -1) { //one of the items must be evicted
			if(evictableFramePresent()) {
				
				while(true) {
					if(evictIndex == contents.length) {
						evictIndex = 0;
					}
					
					if(!contents[evictIndex].isPinned()) {
						contents[evictIndex].evict();
						insertIndex = evictIndex;
						evictIndex++;
						break;
					}
					
					evictIndex++;
				}
			 } else {
				 //block can not be loaded because all frames are pinned
				 return null;
			 }
			
		}
		
		//now we know where to insert
		
		contents[insertIndex].load(blockID); //the block has been loaded
		System.out.println("Block #" + blockID + " has been loaded into Frame #" + insertIndex);
		
		return contents[insertIndex];
	}
	
	public String getRecord(int recordID) {
		int blockID = recordID / Frame.RECORDS_PER_FRAME;
		int offset = recordID % Frame.RECORDS_PER_FRAME;
		
		Frame result = loadBlock(blockID);
			
		if(result == null) return null;
		
		return result.getRecord(offset);
	
	}
	
	public boolean setRecord(int recordID, String record) {
		int blockID = recordID / Frame.RECORDS_PER_FRAME;
		int offset = recordID % Frame.RECORDS_PER_FRAME;
		
		Frame target = loadBlock(blockID);
		
		if(target == null) return false;
		
		target.setRecord(offset, record);
		
		
		return true;
	}
	
}
