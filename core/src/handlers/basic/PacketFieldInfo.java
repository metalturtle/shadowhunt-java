package handlers.basic;

public class PacketFieldInfo {
	String field_name,type;
	int send_type,interpol_type;
	char ctype;
	public static final int ALWAYS=0,BASELINE=1,
			ON_CHANGE=2,ON_CHANGE_ONE_READ=3,NOT_EMPTY=4,MANUAL=5,
			NOTHING=6;
	
	public class Interpolation {
		public static final int PREVIOUS=0,NEXT=2,INTERPOLATE=3,ANGLE=4,SHOOT=5;
	}
	
	public PacketFieldInfo(String field,char data_type, int send_type,int interpol_type) {
		this.field_name = field;
		this.ctype = data_type;
		this.send_type = send_type;
		this.interpol_type = interpol_type;
	}
	
//	public char get_data_type() {
//		return ctype;
//	}
	
	public String get_field_name() {
		return this.field_name;
	}
	
	public int get_send_type() {
		return this.send_type;
	}
	
	public int get_interpolation_type() {
		return this.interpol_type;
	}
}
