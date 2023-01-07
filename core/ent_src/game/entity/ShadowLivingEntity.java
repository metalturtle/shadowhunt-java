package game.entity;

import basic.Rectangle;
import basic.Vector;
import handlers.basic.entity.SyncedLivingEntity;

public final class ShadowLivingEntity extends SyncedLivingEntity {

	public ShadowLivingEntity() {
		super();
	}
	
	public ShadowLivingEntity(Vector pos, Rectangle bound) {
		super(pos,bound);
	}

}
