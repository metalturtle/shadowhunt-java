package game.synced;

import handlers.basic.packets.EntityState;

public class ActorStatePacket extends EntityState {
	public byte team_id=10,health,shoot_count;
	public byte has_light;
	int light_id;
	public long duration;
}