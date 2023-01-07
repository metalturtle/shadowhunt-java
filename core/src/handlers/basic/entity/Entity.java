package handlers.basic.entity;

import java.util.ArrayList;
import java.util.Iterator;

import basic.Rectangle;
import basic.RoundQueue;
import basic.Vector;
import basic.resource.WorldResourceObject;
import network.NetworkInfo;
import network.packets.Packet;
import util.GameInfo;



public class Entity extends WorldResourceObject
{
	public Vector vdir=new Vector(0,0);
	private float angle;
	protected float speed;
	protected byte state;
	int handler_id;
	boolean new_state;
	protected static Vector move_vec = new Vector(0,0);
	
	public Entity(){
		super(new Vector(0,0),new Rectangle(0,0,0,0));
		angle=0;
		speed=0;
		state = (byte)0;
	}
	
	public Entity(Vector p,Rectangle bound){
			super(p,bound);
			pos = new Vector(p);
			angle=0;
			speed=5;
			state = (byte)0;
	}
	
	public int get_handler_id() {
		return handler_id;
	}
	
	public void set_handler_id(int handler_id) {
		this.handler_id = handler_id;
	}
	///////////////////////////MOVEMENT///////////////////////////////////
	public float get_speed() {return speed;}

	public void set_speed(float speed) {
		this.speed = speed;
	}
	
	
	public void set_angle(float angle) {
		if(angle <360)
			angle+=360;
		if(angle >= 360)
			angle-=360;
		this.angle = GameInfo.f2deg(angle);
	}
	
	public float get_angle() {
		return angle; 
	}
	
	public void set_angle(Vector ang) {
		float angle = Vector.GetAngle(ang);
		set_angle(angle);
	}
	
	
	public byte get_state() {return state;}
	
	public void set_state(byte state) {
		if(this.state != state) {
//			if(this instanceof GameActor)
//				System.out.println("state change: "+state);
			new_state = true;
			this.state = state;
		}
	}
	public void read_first_state() {
		if(this.new_state == true) {
			this.new_state = false;
//			System.out.println("read first state");	
		}
	}
	public boolean is_first_state() {return this.new_state;}
	
	public void reset() {
		pos.set(0, 0);
		bound.set(0,0,0,0);;
		this.set_angle(0);
		this.speed = 0;
		this.vdir.set(0, 0);
		state = (byte)0;
		this.new_state = false;
	}
	
	///////////////////////////COMMANDS///////////////////////////////////
}
