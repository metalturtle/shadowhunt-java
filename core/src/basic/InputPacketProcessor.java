package basic;

public interface InputPacketProcessor {

	public abstract int read_int();
	
	public abstract float read_float();
	
	public abstract byte read_byte();
	
	public abstract long read_long();
	
	public abstract String read_string();
	
	public abstract void read_vector(Vector v);
}
