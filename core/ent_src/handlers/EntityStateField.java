package handlers;

import basic.InputPacketProcessor;
import basic.OutputPacketProcessor;
import basic.Vector;

public class EntityStateField implements InputPacketProcessor,OutputPacketProcessor {
	public int ival;
	public float fval;
	public byte bval;
	public long lval;
	public String sval="";
	public char type;
	public Vector vec=new Vector();
	
	public void write_int(int val) {
		this.ival = val;
		type='i';
	}
	
	public void write_float(float val) {
		this.fval = val;
		type='f';
	}
	
	public void write_byte(byte val) {
		this.bval = val;
		type='b';
	}
	
	public void write_long(long val) {
		this.lval = val;
		type='l';
	}
	
	public void write_string(String val) {
		this.sval = val;
		type='s';
	}
	
	public void write_vector(Vector pos) {
		this.vec.set(pos);
		type='v';
	}
	/////////////////////////////////////
	public int read_int() {
		return ival;
	}
	
	public float read_float() {
		return fval;
	}
	
	public byte read_byte() {
		return bval;
	}
	
	public long read_long() {
		return lval;
	}
	
	public String read_string() {
		return sval;
	}
	
	public void read_vector(Vector v) {
		v.set(vec);
	}
	
	public char get_type() {
		return type;
	}
	
	//////////////////////utility functions/////////////////////////////////
	
	public boolean equals(EntityStateField eField) {
		if(get_type() =='f') {
			if(fval == eField.fval) {
				return true;
			}
		}
		if(get_type() =='l') {
			if(lval == eField.lval) {
				return true;
			}
		}
		if(get_type() =='i') {
			if(ival == eField.ival) {
				return true;
			}
		}
		if(get_type() =='b') {
			if(bval == eField.bval) {
				return true;
			}
		}
		if(get_type() == 'v') {
			if(vec.equals(eField.vec)) {
				return true;
			}
		}
		if(get_type() == 's') {
			if(sval.equals(eField.sval)) {
				return true;
			}
		}
		return false;
	}
	
	public void add(EntityStateField eField) {
		if(get_type() =='f') {
			fval+=eField.fval;
		}
		if(get_type() =='l') {
			lval+=eField.lval;
		}
		if(get_type() =='i') {
			ival+=eField.ival;
		}
		if(get_type() =='b') {
			bval+=eField.bval;
		}
		if(get_type() == 'v') {
			vec.add(eField.vec);
		}
	}
	
	public void sub(EntityStateField eField) {
		if(get_type() =='f') {
			fval-=eField.fval;
		}
		if(get_type() =='l') {
			lval-=eField.lval;
		}
		if(get_type() =='i') {
			ival-=eField.ival;
		}
		if(get_type() =='b') {
			bval-=eField.bval;
		}
		if(get_type() == 'v') {
			vec.substract(eField.vec);
		}
	}
	
	public void multiply(EntityStateField eField) {
		if(get_type() =='f') {
			fval*=eField.fval;
		}
		if(get_type() =='l') {
			lval*=eField.lval;
		}
		if(get_type() =='i') {
			ival*=eField.ival;
		}
		if(get_type() =='b') {
			bval*=eField.bval;
		}
		if(get_type() == 'v') {
			vec.multiply(eField.fval);
		}

	}
	
	public void interpolate(EntityStateField eField,float a) {
		if(get_type() =='f') {
//			fval*=eField.fval;
//			float x = prevupdate.x*(1-a)+curupdate.x*(a);
//			float y = prevupdate.y*(1-a)+curupdate.y*(a);
			fval = fval*(1-a)+eField.fval*a;
		}
		if(get_type() =='l') {
//			lval*=eField.lval;
			lval = (long) (lval*(1-a)+eField.lval*a);
		}
		if(get_type() =='i') {
			ival*=eField.ival;
			ival = (int) (ival*(1-a)+eField.ival*a);
		}
		if(get_type() =='b') {
//			bval*=eField.bval;
			bval = (byte) (bval*(1-a)+eField.bval*a);
		}
		if(get_type() =='v') {
			vec.x(vec.x()*(1-a)+eField.vec.x()*a);
			vec.y(vec.y()*(1-a)+eField.vec.y()*a);
		}
	}
}
