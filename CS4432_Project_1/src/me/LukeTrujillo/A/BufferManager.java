package me.LukeTrujillo.A;

public class BufferManager {
	
	private BufferPool pool;

	public BufferManager(int numFrames) {
		pool = new BufferPool(numFrames);
	}
	
	void get(int recordID) {
		String record = pool.getRecord(recordID);
		System.out.println("Record #" + recordID + ": " + record);
	}
	
	void set(int recordID, String record) {
		boolean result = pool.setRecord(recordID, record);
		
		if(result)
			System.out.println("Record #" + recordID + ": set to '" + record + "'");
		
	}
	
}
