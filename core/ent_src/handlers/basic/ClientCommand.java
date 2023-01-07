package handlers.basic;

import java.util.ArrayList;
import java.util.Iterator;

import basic.InputState;
import basic.RoundQueue;
import basic.RoundQueueIterator;
import network.NetworkInfo;
import util.GameInfo;

public class ClientCommand 
{
	public RoundQueue<Command> commandsNew;
	int COMMAND_SENDING_COUNT;
	Command commandsForSending[];
	boolean enabled;

	int START_COMMAND_ID,LAST_COMMAND_ID;
	
	public ClientCommand()
	{
		COMMAND_SENDING_COUNT=0;
//		NEXT_COMMAND_ID = 0;
		init_command();
	}
	
	void enable() 
	{
		this.enabled = true;
	}
	
	void disable()
	{
		this.enabled = false;
	}
	
	boolean check_enabled()
	{
		return this.enabled;
	}
	
	public int get_start_id() {
		return this.START_COMMAND_ID;
	}
//	
//	public int get_last_id() {
//		return this.LAST_COMMAND_ID;
//	}
	
	Command[] add_command_array(int size)
	{
		Command[] commands = new Command[size];
		for(int i=0;i<size;i++) {
			commands[i] = new Command((short)0);
		}
		return commands;
	}
	
	public void init_command()
	{
		int store_ent_commands_size = NetworkInfo.COMMAND_BUFFER_SIZE;
		commandsNew = new RoundQueue<Command>(add_command_array(store_ent_commands_size),false);
		commandsForSending = new Command[store_ent_commands_size];
		for(int i=0;i<store_ent_commands_size;i++)
		{
			commandsForSending[i] = new Command((short)0);
		}
	}
	
	public boolean add_command(Command command)
	{
		Command entcmd = commandsNew.add_mset_element();
		if(entcmd == null) {
			return false;
		}
		
		entcmd.set(command);
		entcmd.set_command_id(LAST_COMMAND_ID);
		LAST_COMMAND_ID+=1;
		entcmd.read = false;
		return true;
	}
	
	public Command get_command()
	{
		return commandsNew.get_first_element();
	}
	
	public Command pop_command()
	{
		return commandsNew.remove_first_element();
	}
	
	public void acknowledge_command(int end_cmd_id)
	{	
//		int max_id = Math.min(Math.max(end_cmd_id-START_COMMAND_ID,0), commandsNew.size());
		for(int i=START_COMMAND_ID;i<=end_cmd_id;i++) {
			commandsNew.remove_first_element();
		}
		this.START_COMMAND_ID = end_cmd_id+1;
	}
	
	public void read_all() {
		for(Iterator <Command> it = commandsNew.get_iterator(false); it.hasNext();) {
			Command command = it.next();
			command.set_read();
		}
	}
	
	public void clear() {
		this.commandsNew.clear();
	}
	
	public Command [] get_processed_commands()
	{
		COMMAND_SENDING_COUNT=0;
		for(Iterator<Command> iterator = commandsNew.get_iterator(false);iterator.hasNext();)
		{
			COMMAND_SENDING_COUNT+=1;
			iterator.next();
		}
		commandsForSending = new Command[COMMAND_SENDING_COUNT];
		int i=0;
		for(Iterator<Command> iterator = commandsNew.get_iterator(false);iterator.hasNext();)
		{
			Command command = iterator.next();
			commandsForSending[i] = command;
			i++;
		}
		return commandsForSending;
	}
	
	public RoundQueue<Command> get_command_queue()
	{
		return commandsNew;
	}
	
	public Iterator<Command> get_iterator(boolean revese)
	{
		return commandsNew.get_iterator(revese);
	}
	
	public boolean push_cmd_from_inputs()
	{
		Command new_command = commandsNew.add_mset_element();
		if(new_command == null) {
			return false;
		}
		
		InputState.set_cmd_keys(new_command.keys);
		new_command.set_mouse(InputState.mouse);
		new_command.set_last_fps(GameInfo.get_last_fps());
		new_command.set_command_id(LAST_COMMAND_ID);
		LAST_COMMAND_ID+=1;
		
		return true;
	}
}
