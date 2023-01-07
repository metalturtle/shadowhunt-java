package simulation;

import java.util.ArrayList;

import handlers.basic.packets.EntityState;
import network.packets.Packet;

public class ServerGameStatePacket extends Packet {
	public int sim_sequence,baseline_sim_sequence;
	public int command_id;
	public ArrayList<EntityState> ent_packets[];
	public RequiredUpdateList reqUpdateList;
	byte state;
}