package render.basic;

import com.badlogic.gdx.Gdx;

import basic.Rectangle;
import basic.Vector;

public class Sprite extends RenderEntity
{
	public Vector old_pos;
//	Sprite sprite_set[];
	float animation_time;
	
	public Sprite() {
		super();
		this.state = 1;
		this.forever=true;
	}
	
	public Sprite(Vector pos,Rectangle bound,float angle,int sprite_id)
	{
		super(pos,bound,angle,sprite_id);
		this.state = 1;
	}
	public Sprite(Sprite sprite) {
		super();
		set(sprite);
	}
	
	public void set_animation(float anim_time) {
		this.animation_time = anim_time;
	}
	
	public void run_animation(float val) {
		this.animation_time += val;
	}
	
	public float get_animation_time() {
		return this.animation_time;
	}
	
//	public Sprite(Vector pos,Rectangle bound,float angle,byte state,int sprite_id,Sprite sprite_set[],int anim_mode,long lifespan,boolean forever,short blend_option,int r,int b,int g,int a)
//	{
//		super(pos,bound,angle,state,sprite_id,anim_mode,lifespan,forever,blend_option,r,g,b,a);
//		init_sprite_set(sprite_set);
//		//this.sprite_set = sprite_set;
//	}
//	
//	public void set(Vector pos,Rectangle bound,float angle,byte state,int sprite_id,Sprite sprite_set[],int anim_mode,long lifespan,boolean forever,short blend_option,int r,int b,int g,int a) {
//		set(pos,bound,angle,state,sprite_id,anim_mode,lifespan,forever,blend_option,r,g,b,a);
//		init_sprite_set(sprite_set);
//	}
	
//	public void init_sprite_set(Sprite sprite_set[]) {
//		this.sprite_set = new Sprite[sprite_set.length];
//		for(int i=0;i<sprite_set.length;i++) {
//			if(sprite_set[i] !=null)
//				this.sprite_set[i] = new Sprite(sprite_set[i]);
//		}	
//	}
	
//	@Override
//	public void run_animation() {
//		super.run_animation();
//	}
	
//	public void set_sprite_set(Sprite sprite_set[]) {
//		if(this.sprite_set==null) {
//			init_sprite_set(sprite_set);
//		}
//		for(int i=0;i<sprite_set.length;i++) {
//			if(sprite_set[i] !=null)
//				this.sprite_set[i].set(sprite_set[i]);
//			else {
//				this.sprite_set[i].set_state((byte)0);
//				break;
//			}
//		}
//	}
	
//	@Override
//	public void start_animation(int anim_mode,float speed) {
//		super.start_animation(anim_mode, speed);
//		if(sprite_set != null) {
//			for(int i=0;i<sprite_set.length;i++) {
//				if(sprite_set[i]==null || sprite_set[i].get_state()==0)
//					break;
//				sprite_set[i].start_animation(anim_mode, speed);
//			}
//		}
//	}
//	
//	@Override
//	public void stop_animation() {
//		super.stop_animation();
//		if(sprite_set!= null) {
//			for(int i=0;i<sprite_set.length;i++) {
//				if(sprite_set[i]==null)
//					break;
//				sprite_set[i].stop_animation();
//			}
//		}
//	}
//	
//	public Sprite[] get_sprite_set()
//	{
//		return sprite_set;
//	}
}
