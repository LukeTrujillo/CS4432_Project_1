

public class BufferPool {
	private Frame contents[];
	private int evictIndex;

	public BufferPool(int max_frames) {
		this.contents = new Frame[max_frames];
		this.evictIndex = 0;

		for (int x = 0; x < contents.length; x++) {
			contents[x] = new Frame(); //create the new frames
		}
	}

	/**
	 * This function checks if a given blockId is already in memory
	 * @param blockID
	 * @return true of in memory, false if not
	 */
	public boolean isAlreadyLoadedInMemory(int blockID) {
		for (int x = 0; x < contents.length; x++) {
			if (contents[x].getBlockID() == blockID) {
				return true;
			}
		}

		return false;
	}
	
	/**
	 * Checks if there is a unpinned frame present which can be evicted
	 * @return True if yes, false if not
	 */
	public boolean evictableFramePresent() {
		for (int x = 0; x < contents.length; x++) {
			if (contents[x].isPinned() == false) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if there is an empty frame present in the buffer pool
	 * @return True if there is, false if not
	 */
	public boolean isEmptyFramePresent() {
		for (int x = 0; x < contents.length; x++) {
			if (contents[x].isEmptyFrame()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This returns the index of the first empty frame found
	 * @return the index of the first empty frame found, returns -1 if no empty frame found
	 */
	public int getEmptyFrameIndex() {
		for (int x = 0; x < contents.length; x++) {
			if (contents[x].isEmptyFrame()) {
				return x;
			}
		}
		return -1;
	}

	/**
	 * Get the pool index of a block that has been loaded into memory from its blockID
	 * @param blockID
	 * @return index of the blockID in contents[], -1 if not found
	 */
	public int getLoadedBlockFrameIndex(int blockID) {
		int frameIndex = -1;

		for (int x = 0; x < contents.length; x++) {
			if (contents[x].getBlockID() == blockID) {
				frameIndex = x;
				break;
			}
		}
		return frameIndex;
	}

	/**
	 * This function will retrieve and report a given record
	 * @param recordID the record address to be reported
	 */
	public void getRecord(int recordID) {
		int blockID = (recordID / Frame.RECORDS_PER_FRAME) + 1;
		int offset = recordID % Frame.RECORDS_PER_FRAME;

		if (isAlreadyLoadedInMemory(blockID)) { // if it is already in memory

			int frameIndex = getLoadedBlockFrameIndex(blockID);
			Frame target = contents[frameIndex];

			System.out.println("Output: " + target.getRecord(offset) + "; File " + blockID
					+ " already in memory; Located in Frame " + (frameIndex + 1));

			return;
		} else if (!isAlreadyLoadedInMemory(blockID) && isEmptyFramePresent()) { // case 2: if it is not in memory but empty frame is present
			int emptyFrameIndex = getEmptyFrameIndex();
			contents[emptyFrameIndex].load(blockID);

			Frame target = contents[emptyFrameIndex];

			System.out.println("Output: " + target.getRecord(offset) + "; Bought File " + blockID
					+ " from disk; Placed in Frame " + (emptyFrameIndex + 1));

			return;
		} else if (!isAlreadyLoadedInMemory(blockID) && !isEmptyFramePresent() && evictableFramePresent()) { // case 3: if it is not in memory, no empty frames, but there is a evictable frame
			int evictID = evictBlock();
			int index = evictIndex - 1;

			contents[index].load(blockID);
			Frame target = contents[index];

			System.out.println(
					"Output: " + target.getRecord(offset) + "; Bought File " + blockID + " from disk; Placed in Frame "
							+ (index + 1) + "; Evicted file " + evictID + " from Frame " + (index + 1));

		} else if (!isAlreadyLoadedInMemory(blockID) && !isEmptyFramePresent() && !evictableFramePresent()) { // case 4: everything is pinned, the item can not be reported
			System.out.println("The corresponding block #" + blockID
					+ " cannot be accessed from disk because the memory buffers are full");
			return;
		}
	}

	/**
	 * This will set the record at the given address, if possible
	 * @param recordID the address for the record to be set at
	 * @param record the record content to be set
	 */
	public void setRecord(int recordID, String record) {
		int blockID = (recordID / Frame.RECORDS_PER_FRAME) + 1;
		int offset = recordID % Frame.RECORDS_PER_FRAME;

		Frame target = null;

		if (isAlreadyLoadedInMemory(blockID)) { // case 1: recordID is already in memory
			int frameIndex = getLoadedBlockFrameIndex(blockID);
			target = contents[frameIndex];

			System.out.println("Output: Write was successful; File " + blockID + " already in memory; Located in Frame "
					+ (frameIndex + 1));

		} else if (!isAlreadyLoadedInMemory(blockID) && isEmptyFramePresent()) { // case 2: record not in memory but there is a empty frame

			int emptyFrameIndex = getEmptyFrameIndex();
			contents[emptyFrameIndex].load(blockID);
			
			target = contents[emptyFrameIndex];


			System.out.println("Output: Write was successful; Bought File " + blockID + " from disk; Placed in Frame "
					+ (emptyFrameIndex + 1));

		} else if (!isAlreadyLoadedInMemory(blockID) && !isEmptyFramePresent() && evictableFramePresent()) { // case 3: record not in memory, no empty frame, but there is a frame we can evict
			int evictID = evictBlock();
			int index = evictIndex - 1;

			contents[index].load(blockID);
			target = contents[index];

			System.out.println(
					"Output: Write was successful; Bought File " + blockID + " from disk; Placed in Frame "
							+ (index + 1) + "; Evicted file " + evictID + " from Frame " + (index + 1));
			
		} else if (!isAlreadyLoadedInMemory(blockID) && !isEmptyFramePresent() && !evictableFramePresent()) { // case 4: everything is pinned so no record can be set
			System.out.println("The corresponding block #" + blockID
					+ " cannot be accessed from disk because the memory buffers are full; Write was successful");
			return;
		}
		
		
		if(record.startsWith("\"")) record = record.substring(1); //if the input startts with a quote remove it
		if(record.endsWith("\"")) record = record.substring(0, record.length() - 2); //if the record starts with a quote remove it
		
		target.setRecord(offset, record); //set the record
	}
	
	/**
	 * This function uses a clock-like algorithm to choose a frame to evict and then evicts it
	 * @return the blockID of the evicted frame, -1 if nothing can be evicted
	 */
	public int evictBlock() {
		while (evictableFramePresent()) { //while true, assumes tha
			if (evictIndex == contents.length) { //if the evict index is over the limit loop back around
				evictIndex = 0;
			}

			if (!contents[evictIndex].isPinned()) { //if this is a valid index
				int evictID = contents[evictIndex].getBlockID(); //save the id
				contents[evictIndex].evict(); ///and then evict it

				evictIndex++; //increase the index for the next eviction

				return evictID; //and return the id
			}
			evictIndex++; //pinned frame, so move to the next one
		}
		
		return -1; //something went wrong
	}

	/**
	 * This will pin the specified frame if possible
	 * @param recordID
	 */
	public void pin(int recordID) {
		int blockID = recordID;
	

		Frame target = null;
		String pinStatus = "";
		
		if (isAlreadyLoadedInMemory(blockID)) { // case 1: it is already in memeory
			int frameIndex = getLoadedBlockFrameIndex(blockID);
			target = contents[frameIndex];

			if(target.isPinned()) pinStatus = "Already pinned";
			else pinStatus = "Not already pinned";
			
			System.out.println("Output: File " + blockID + " pinned in Frame "
					+ (frameIndex + 1) + "; " + pinStatus);

		} else if (!isAlreadyLoadedInMemory(blockID) && isEmptyFramePresent()) { // case 2: not in memory but there is an empty frame

			int emptyFrameIndex = getEmptyFrameIndex();
			contents[emptyFrameIndex].load(blockID);
			
			target = contents[emptyFrameIndex];

			if(target.isPinned()) pinStatus = "Already pinned";
			else pinStatus = "Not already pinned";


			System.out.println("Output: File " + blockID + " pinned in Frame " + (emptyFrameIndex + 1) + "; " + pinStatus + "; Bought File " + blockID + " from disk; Placed in Frame "
					+ (emptyFrameIndex + 1));

		} else if (!isAlreadyLoadedInMemory(blockID) && !isEmptyFramePresent() && evictableFramePresent()) { // case 3: not in memory, no empty frame, but there is a evictable frame
			int evictID = evictBlock();
			int index = evictIndex - 1;

			contents[index].load(blockID);
			target = contents[index];
			
			if(target.isPinned()) pinStatus = "Already pinned";
			else pinStatus = "Not already pinned";


			System.out.println(
					"Output: File " + blockID + " pinned in Frame " + (index + 1) + "; " + pinStatus + "; Evicted file " + evictID + " from Frame " + (index + 1));
			
		} else if (!isAlreadyLoadedInMemory(blockID) && !isEmptyFramePresent() && !evictableFramePresent()) { // case 4: all pinned so it can not be pinned
			System.out.println("The corresponding block #" + blockID
					+ " cannot be pinned because the memory buffers are full");
			return;
		}
		
		target.setPinned(true); //set it to pinned
	}

	/**
	 * This function will specify the frame containind the given block id
	 * @param recordID
	 */
	public void unpin(int recordID) {
		int blockID = recordID;

		Frame target = null;
		String pinStatus = "";
		
		if (isAlreadyLoadedInMemory(blockID)) { // case 1: if the block is in memory then it can be unpinned
			int frameIndex = getLoadedBlockFrameIndex(blockID);
			target = contents[frameIndex];

			if(target.isPinned()) pinStatus = "Frame was already pinned";
			else pinStatus = "Frame was not already unpinned";
			
			System.out.println("Output: File " + blockID + " in Frame "
					+ (frameIndex + 1) + " is unpinned; " + pinStatus);

		}  else { //or you are trying to unpinn something not in memory so we cant do it
			System.out.println("The corresponding block " + blockID
					+ " cannot be unpinned because it is not in memory");
			return;
		}
		
		target.setPinned(false); //set it as unpinned.
	}

}
