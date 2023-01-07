//package game;
//
//import java.util.Iterator;
//import java.util.Random;
//
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//
//import basic.Rectangle;
//import basic.Vector;
//import handlers.GameHandler;
//import handlers.basic.PacketFieldInfo;
//import handlers.basic.entity.Entity;
//import render.AbstractRenderHandler;
//import render.RenderHandler;
//import render.SpriteHandler;
//import render.basic.Sprite;
//import simulation.SimulationCoordinator;
//import simulation.SimulationHandler;
//import simulation.handlers.EntityHandler;
//
//
//class FloatingEntity extends Entity {
//	Sprite sprite;
//	float floating_resize, float_rotate;
//	float resize_amt,rotate_amt;
//	public void set_float_movement(float float_movement,float rotate) {
//		if(float_movement <360)
//			float_movement+=360;
//		if(float_movement >= 360)
//			float_movement-=360;
//		this.floating_resize = float_movement;
//		
//		if(rotate <360)
//			rotate+=360;
//		if(rotate >= 360)
//			rotate-=360;
//		this.float_rotate = rotate;
//	}
//}
//public class FloatingEntityHandler extends EntityHandler<FloatingEntity> {
//
//	AbstractRenderHandler RendHandle;
//	SpriteHandler SH;
//	Random rand;
//	public FloatingEntityHandler(SimulationCoordinator SimCoord) {
//		super(SimHandle);
//		// TODO Auto-generated constructor stub
//		this.RendHandle = GameHandle.RendHandle;
//		this.SH = RendHandle.SpriteHandle;
//		rand = new Random();
//	}
//
//	@Override
//	public void init() {
//		
//	}
//
//	@Override
//	public FloatingEntity create_new_element() {
//		// TODO Auto-generated method stub
//		return new FloatingEntity();
//	}
//
//	@Override
//	public FloatingEntity add_resource_object(Vector pos, Rectangle bound, String params) {
//		FloatingEntity ent = super.add_resource_object(pos, bound, params);
//		String tex_name = params;
//		Sprite sprite = SH.add_resource_object(ent.pos,ent.bound,tex_name);;
//		sprite.set_color(0.1f,0.1f,0.1f,0.1f);
//		ent.sprite = sprite;
//	
//		ent.set_float_movement(rand.nextInt(360),rand.nextInt(360));
//		return ent;
//	}
//
//	@Override
//	protected void before_disable_object(FloatingEntity ent) {
////		ent.sprite.disable();
////		RendHandle.SpriteHandle.disable_resource_object(ent.sprite);
////		ent.sprite.mark_disable();
//	}
//
//	@Override
//	public void loop() {
//		for(Iterator<FloatingEntity> it = get_iterator(null); it.hasNext();) {
//			FloatingEntity ent = it.next();
//			float val = 0.5f*(float)Math.cos(Math.toRadians(ent.floating_resize));
//			Sprite sprite = ent.sprite;
//			sprite.bound.set(ent.bound.x()-val,ent.bound.y()-val,ent.bound.w()+val,ent.bound.h()+val);
//			float ang =1.1f*(float)Math.sin(Math.toRadians(ent.float_rotate));
////			sprite.set_angle(ang);
//			ent.set_float_movement(ent.floating_resize+1.5f,ent.float_rotate+0.5f);
//		}
//	}
//}
