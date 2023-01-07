package network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import Coordinator.SystemPacket;

public class SystemPacketSerializer extends Serializer<SystemPacket> {

	@Override
	public SystemPacket read(Kryo kryo, Input input, Class<SystemPacket> arg2) {
		SystemPacket packet = new SystemPacket();
		packet.sequence = input.readInt();
		packet.ack_sequence = input.readInt();
		packet.type = input.readByte();
		packet.code = input.readInt();
		packet.message = input.readString();
		return packet;
	}

	@Override
	public void write(Kryo kryo, Output output, SystemPacket packet) {
		output.writeInt(packet.sequence);
		output.writeInt(packet.ack_sequence);
		output.writeByte(packet.type);
		output.writeInt(packet.code);
		output.writeString(packet.message);
	}

}
