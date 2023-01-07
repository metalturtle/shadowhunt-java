package handlers.basic.entity;

import java.util.LinkedList;

import basic.Rectangle;
import basic.Vector;
import basic.resource.WorldResourceHandler;
import basic.resource.WorldResourceObject;
import handlers.basic.EntityMovementObject;
import handlers.basic.GameRay;
import handlers.basic.Timer;
import handlers.basic.packets.EntityState;
import render.basic.Sprite;

public abstract class SyncedGameEntity<T extends EntityState> extends SyncedEntity<T> {
	protected Timer[] timers;
	byte teamId;
	byte worldObjSize, spriteSize,intrSize;
//	EntityMovementObject moveObj;
	
	SyncedGameEntity() {
		super();
	}
	
	SyncedGameEntity(Vector pos, Rectangle bound) {
		super(pos,bound);
	}
	
	public void set_child_sizes(byte worldObjSize,byte spriteSize,byte intrSize) {
		this.worldObjSize = worldObjSize;
		this.spriteSize = spriteSize;
		this.intrSize = intrSize;
	}
	
	public void set_team_id(byte team_id) {this.teamId = team_id;}
	public byte get_team_id() {return teamId;}
	
	
	public void init_timer(int timerSize) {
		timers = new Timer[timerSize];
		for(int i = 0; i < timers.length; i++) {
			timers[i] = new Timer();
		}
	}
	
	public Timer get_timer(int id) {
		return timers[0];
	}

	public EntityMovementObject get_move_object() {
		return (EntityMovementObject)this.get_child(0);
	}
	
	public Sprite get_sprite(int id) {
		return (Sprite)this.get_child(intrSize+id);
	}
	
	public WorldResourceObject get_inventory_object(int id) {
		return this.get_child(intrSize+spriteSize+id);
	}
	
	public void reset() {
		super.reset();
	}
}
