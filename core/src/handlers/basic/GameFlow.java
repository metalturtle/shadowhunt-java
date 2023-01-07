package handlers.basic;

import util.GameInfo;

public class GameFlow
{
	public Timer gametimer;
	String message;
	byte type;
	boolean init;
	public GameFlow()
	{
		gametimer = new Timer();
		gametimer.set(5*60*1000);
		message = "";
		type = (byte)0;
	}
	public String get_message() {return message;}
	public byte get_type() {return type;}
	public boolean check_init() {return init;}
	
	public void set(GameFlow gameflow)
	{
//		this.gametimer.set(gameflow.gametimer.duration,gameflow.gametimer.down,gameflow.gametimer.elapsed);
		this.message = gameflow.message;
		gameflow.init = false;
	}
	public void set_message(String message2) {this.message = message2;}
	public void set_init(boolean init) {this.init = init;}
	
}