package simulation;

import handlers.basic.Command;
import network.packets.Packet;

public class ClientGameStatePacket extends Packet
{
//	int ack_baseline_sequence;
	int ack_baseline_sequence;
	public Command commands[];
	public int start_command_id,array_size;
//	public byte[] data;
}