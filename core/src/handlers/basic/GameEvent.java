package handlers.basic;

public class GameEvent 
{
	short type;
	short life;
	String args[];
	public GameEvent(short type,short life,String args[])
	{
		this.set(type, life, args);
	}
	public GameEvent()
	{
		type=0;
		life=0;
		args=new String[5];
	}
	public void set(short type,short life,String args[])
	{
		this.type = type;
		this.life = life;
		this.args = args;
	}
	
	public short get_type()
	{
		return type;
	}
	public short get_life()
	{
		return life;
	}
	public String[] get_args()
	{
		return args;
	}
	public void set_life(short life)
	{
		this.life = life;
	}
}
