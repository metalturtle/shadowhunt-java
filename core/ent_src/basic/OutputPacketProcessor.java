package basic;

public interface OutputPacketProcessor {
	public abstract void write_int(int val);
	
	public abstract void write_float(float val);
	
	public abstract void write_byte(byte val);
	
	public abstract void write_long(long val);
	
	public abstract void write_string(String val);
	
	public abstract void write_vector(Vector vec);
}
