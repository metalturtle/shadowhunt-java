package handlers.basic;

import basic.InputState;
import basic.Vector;

public class Command
{
	int command_id;
	boolean keys[];
	byte angle;
	short last_fps;
	public Vector pos = new Vector();
	boolean read;
	
	public Command()
	{
		keys = new boolean[1];
		last_fps = 0;
	}
	
	void set_command_id(int command_id)
	{
		this.command_id = command_id;
	}
	
	public int get_command_id()
	{
		return this.command_id;
	}
	
	public Command( short last_fps)
	{
		this.keys = new boolean[InputState.get_cmd_key_length()];
		this.last_fps = last_fps;
	}
	
	public Command(boolean keys[],Vector vec_mouse, short last_fps)
	{
		this.keys = new boolean[InputState.get_cmd_key_length()];
		set(keys,vec_mouse, last_fps);
	}
	
	public void set(boolean keys[],Vector vec_mouse, short last_fps)
	{
		for(int i=0;i<InputState.get_cmd_key_length();i++)
			this.keys[i] = keys[i];
		set_mouse_angle(Vector.GetAngle(vec_mouse));
		this.last_fps = last_fps;
	}
	
	
	public void set(boolean keys[],short angle, short last_fps)
	{
		for(int i=0;i<InputState.get_cmd_key_length();i++)
			this.keys[i] = keys[i];
		set_mouse_angle(angle);
		this.last_fps = last_fps;
	}
	
	public void set_keys(boolean keys[])
	{
		for(int i=0;i<this.keys.length;i++)
			this.keys[i] = keys[i];
	}
	
	public void set_mouse(Vector pos)
	{
		set_mouse_angle(Vector.GetAngle(pos));
	}
	
	public void set_mouse(float x, float y)
	{
		set_mouse_angle(Vector.GetAngle(new Vector(x,y)));
	}
	
	public void set_mouse_angle(float angle)
	{
		angle = (int)Math.floor(angle);
		this.angle = (byte)((angle+180)/2 -128);
	}
	
	public void set_last_fps(short fps)
	{
		this.last_fps = fps;
	}
	
	public void set(Command com) 
	{
		this.set(com.keys,(short)((com.angle+128)*2-180), com.last_fps);
//		this.set_command_id(com.get_command_id());
	}
	
	public boolean[] get_keys()
	{
		return this.keys;
	}
	
	public float get_mouse_angle()
	{
		return (this.angle+128)*2-180;
	}
	
	public byte get_mouse_byte()
	{
		return this.angle;
	}
	
	public void set_mouse_byte(byte b)
	{
		this.angle = b;
	}
	
	public short get_last_fps()
	{
		return last_fps;
	}
	
//	public void set_read_flag(boolean flag)
//	{
//		this.read = flag;
//	}
//	
//	public boolean get_read_flag()
//	{
//		return this.read;
//	}
	
	public void set_read() {
		this.read = true;
	}
	
	public boolean is_read_once() {
		return this.read;
	}
	
	public float get_delta_time()
	{
		return ((float)get_last_fps())/1000f;
	}
}
