package simulation;

import com.esotericsoftware.kryo.io.Output;

import basic.OutputPacketProcessor;
import basic.Vector;
import util.GameInfo;

public class KryoOutputPacketProcessor implements OutputPacketProcessor {
	public Output output;
	
	public void write_int(int val) {
		output.writeInt(val,true);
	}
	
	public void write_float(float val) {
		output.writeFloat(val);
	}
	
	public void write_byte(byte val) {
		output.writeByte(val);
	}
	
	public void write_long(long val) {
		output.writeLong(val);
	}
	
	public void write_string(String val) {
		output.writeString(val);
	}
	
	public void write_vector(Vector vec) {
//		output.writeInt(GameInfo.f2ideg2(vec.x()),true);
//		output.writeInt(GameInfo.f2ideg2(vec.y()),true);
		output.writeFloat(vec.x());
		output.writeFloat(vec.y());
	}
}
