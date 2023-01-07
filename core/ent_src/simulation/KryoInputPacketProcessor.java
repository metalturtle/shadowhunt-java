package simulation;

import com.esotericsoftware.kryo.io.Input;

import basic.InputPacketProcessor;
import basic.Vector;
import util.GameInfo;

public class KryoInputPacketProcessor implements InputPacketProcessor {
	public Input input;
	
	public int read_int() {
		return input.readInt(true);
	}
	
	public float read_float() {
		return input.readFloat();
	}
	
	public byte read_byte() {
		return input.readByte();
	}
	
	public long read_long() {
		return input.readLong();
	}
	
	public String read_string() {
		return input.readString();
	}
	
	public void read_vector(Vector vec) {
//		vec.x(GameInfo.i2fdeg2(input.readInt(true)));
//		vec.y(GameInfo.i2fdeg2(input.readInt(true)));
		vec.x(input.readFloat());
		vec.y(input.readFloat());
	}
}
