package me.LukeTrujillo.A;

public class BufferPool {
	private Frame contents[];
	private int evictIndex;

	public BufferPool(int max_frames) {
		this.contents = new Frame[max_frames];
		this.evictIndex = 0;

		for (int x = 0; x < contents.length; x++) {
			contents[x] = new Frame();
		}
	}

	public boolean isAlreadyLoadedInMemory(int blockID) {
		for (int x = 0; x < contents.length; x++) {
			if (contents[x].getBlockID() == blockID) {
				return true;
			}
		}

		return false;
	}

	public boolean evictableFramePresent() {
		for (int x = 0; x < contents.length; x++) {
			if (contents[x].isPinned() == false) {
				return true;
			}
		}
		return false;
	}


	public boolean isEmptyFramePresent() {
		for (int x = 0; x < contents.length; x++) {
			if (contents[x].isEmptyFrame()) {
				return true;
			}
		}
		return false;
	}

	public int getEmptyFrameIndex() {
		for (int x = 0; x < contents.length; x++) {
			if (contents[x].isEmptyFrame()) {
				return x;
			}
		}
		return -1;
	}

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

	public void getRecord(int recordID) {
		int blockID = (recordID / Frame.RECORDS_PER_FRAME) + 1;
		int offset = recordID % Frame.RECORDS_PER_FRAME;

		if (isAlreadyLoadedInMemory(blockID)) { // case 1 for get

			int frameIndex = getLoadedBlockFrameIndex(blockID);
			Frame target = contents[frameIndex];

			System.out.println("Output: " + target.getRecord(offset) + "; File " + blockID
					+ " already in memory; Located in Frame " + (frameIndex + 1));

			return;
		} else if (!isAlreadyLoadedInMemory(blockID) && isEmptyFramePresent()) { // case 2
			int emptyFrameIndex = getEmptyFrameIndex();
			contents[emptyFrameIndex].load(blockID);

			Frame target = contents[emptyFrameIndex];

			System.out.println("Output: " + target.getRecord(offset) + "; Bought File " + blockID
					+ " from disk; Placed in Frame " + (emptyFrameIndex + 1));

			return;
		} else if (!isAlreadyLoadedInMemory(blockID) && !isEmptyFramePresent() && evictableFramePresent()) { // case 3

			int evictID = evictBlock();
			int index = evictIndex - 1;

			contents[index].load(blockID);
			Frame target = contents[index];

			System.out.println(
					"Output: " + target.getRecord(offset) + "; Bought File " + blockID + " from disk; Placed in Frame "
							+ (index + 1) + "; Evicted file " + evictID + " from Frame " + (index + 1));

		} else if (!isAlreadyLoadedInMemory(blockID) && !isEmptyFramePresent() && !evictableFramePresent()) { // case 4
			System.out.println("The corresponding block #" + blockID
					+ " cannot be accessed from disk because the memory buffers are full");
			return;
		}
	}

	public void setRecord(int recordID, String record) {
		int blockID = (recordID / Frame.RECORDS_PER_FRAME) + 1;
		int offset = recordID % Frame.RECORDS_PER_FRAME;

		Frame target = null;

		if (isAlreadyLoadedInMemory(blockID)) { // case 1 for get
			int frameIndex = getLoadedBlockFrameIndex(blockID);
			target = contents[frameIndex];

			System.out.println("Output: Write was successful; File " + blockID + " already in memory; Located in Frame "
					+ (frameIndex + 1));

		} else if (!isAlreadyLoadedInMemory(blockID) && isEmptyFramePresent()) { // case 2

			int emptyFrameIndex = getEmptyFrameIndex();
			contents[emptyFrameIndex].load(blockID);
			
			target = contents[emptyFrameIndex];


			System.out.println("Output: Write was successful; Bought File " + blockID + " from disk; Placed in Frame "
					+ (emptyFrameIndex + 1));

		} else if (!isAlreadyLoadedInMemory(blockID) && !isEmptyFramePresent() && evictableFramePresent()) { // case 3
			int evictID = evictBlock();
			int index = evictIndex - 1;

			contents[index].load(blockID);
			target = contents[index];

			System.out.println(
					"Output: Write was successful; Bought File " + blockID + " from disk; Placed in Frame "
							+ (index + 1) + "; Evicted file " + evictID + " from Frame " + (index + 1));
			
		} else if (!isAlreadyLoadedInMemory(blockID) && !isEmptyFramePresent() && !evictableFramePresent()) { // case 4
			System.out.println("The corresponding block #" + blockID
					+ " cannot be accessed from disk because the memory buffers are full; Write was successful");
			return;
		}
		
		
		if(record.startsWith("\"")) record = record.substring(1);
		if(record.endsWith("\"")) record = record.substring(0, record.length() - 2);
		
		target.setRecord(offset, record);
	}

	public int evictBlock() {
		while (true) {
			if (evictIndex == contents.length) {
				evictIndex = 0;
			}

			if (!contents[evictIndex].isPinned()) {
				int evictID = contents[evictIndex].getBlockID();
				contents[evictIndex].evict();

				evictIndex++;

				return evictID;
			}
			evictIndex++;
		}
	}

	public void pin(int recordID) {
		int blockID = recordID;
	

		Frame target = null;
		String pinStatus = "";
		
		if (isAlreadyLoadedInMemory(blockID)) { // case 1 for get
			int frameIndex = getLoadedBlockFrameIndex(blockID);
			target = contents[frameIndex];

			if(target.isPinned()) pinStatus = "Already pinned";
			else pinStatus = "Not already pinned";
			
			System.out.println("Output: File " + blockID + " pinned in Frame "
					+ (frameIndex + 1) + "; " + pinStatus);

		} else if (!isAlreadyLoadedInMemory(blockID) && isEmptyFramePresent()) { // case 2

			int emptyFrameIndex = getEmptyFrameIndex();
			contents[emptyFrameIndex].load(blockID);
			
			target = contents[emptyFrameIndex];

			if(target.isPinned()) pinStatus = "Already pinned";
			else pinStatus = "Not already pinned";


			System.out.println("Output: File " + blockID + " pinned in Frame " + (emptyFrameIndex + 1) + "; " + pinStatus + "; Bought File " + blockID + " from disk; Placed in Frame "
					+ (emptyFrameIndex + 1));

		} else if (!isAlreadyLoadedInMemory(blockID) && !isEmptyFramePresent() && evictableFramePresent()) { // case 3
			int evictID = evictBlock();
			int index = evictIndex - 1;

			contents[index].load(blockID);
			target = contents[index];
			
			if(target.isPinned()) pinStatus = "Already pinned";
			else pinStatus = "Not already pinned";


			System.out.println(
					"Output: File " + blockID + " pinned in Frame " + (index + 1) + "; " + pinStatus + "; Evicted file " + evictID + " from Frame " + (index + 1));
			
		} else if (!isAlreadyLoadedInMemory(blockID) && !isEmptyFramePresent() && !evictableFramePresent()) { // case 4
			System.out.println("The corresponding block #" + blockID
					+ " cannot be pinned because the memory buffers are full");
			return;
		}
		
		target.setPinned(true);
	}

	public void unpin(int recordID) {
		int blockID = recordID;

		Frame target = null;
		String pinStatus = "";
		
		if (isAlreadyLoadedInMemory(blockID)) { // case 1 for get
			int frameIndex = getLoadedBlockFrameIndex(blockID);
			target = contents[frameIndex];

			if(target.isPinned()) pinStatus = "Frame was already pinned";
			else pinStatus = "Frame was not already unpinned";
			
			System.out.println("Output: File " + blockID + " in Frame "
					+ (frameIndex + 1) + " is unpinned; " + pinStatus);

		}  else {
			System.out.println("The corresponding block " + blockID
					+ " cannot be unpinned because it is not in memory");
			return;
		}
		
		target.setPinned(false);
	}

}
