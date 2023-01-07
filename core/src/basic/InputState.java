package basic;

public class InputState 
{
	public static boolean keyboard[] = new boolean[256];
	public static Vector mouse = new Vector(0,0);
	
	static short COMMAND_INPUT_LENGTH;
	static short KEY_CMD_MAP[];
	static String BIND_NAME[];
	
	public static void init( String[] bind_name,char[] keys )
	{
		COMMAND_INPUT_LENGTH = (short)bind_name.length;
		BIND_NAME = bind_name;
		KEY_CMD_MAP = new short[COMMAND_INPUT_LENGTH];
		for(int i = 0; i < keys.length; i++) {
			KEY_CMD_MAP[i] = (short)keys[i]; 
		}
	}
	
	public static int get_cmd_key_length()
	{
		return COMMAND_INPUT_LENGTH;
	}
	
	public static short get_cmd_id_by_name(String name)
	{
		for(short i = 0; i < BIND_NAME.length; i++) {
			if(BIND_NAME[i].equals(name)) {
				return i;
			}
		}
		return -1;
	}
	
	public static void set_cmd_keys(boolean keys[])
	{
		for(int i=0;i<COMMAND_INPUT_LENGTH;i++) {
			keys[i] = InputState.keyboard[KEY_CMD_MAP[i]];
		}
	}
	
	static public void map_keyboard(boolean keys[])
	{
		for(int i=0;i<256;i++)
			keyboard[i] = keys[i];
	}
	
	static public void map_mouse(float x, float y)
	{
		mouse.set(x,y);
	}
	
	static public void map_input(boolean keys[],float x,float y)
	{
		map_keyboard(keys);
		map_mouse(x,y);
	}
	
	public static void reset()
	{
		for(int i = 0; i < keyboard.length;i++) {
			keyboard[i]=false;
		}
		mouse.set(0,0);
	}
}
