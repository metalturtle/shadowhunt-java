package render;

import render.basic.RenderEntity;

public abstract class Renderer <R extends RenderEntity,H extends RenderEntityHandler<R>> {

	protected H RendEntHandle;
	protected byte RENDER_LEVEL;
	MainRenderer MRenderer;
	
	public class RenderLevel{
		public static final byte LOWER=0,GROUND=1,LIGHT=2,SPRITES=3,EFFECTS=4,AIR=5,PARALLAX=6,SKY=7,SCREEN=9;
	};
	
	public Renderer(MainRenderer MRenderer,H RendEntHandle,byte level) {
		this.MRenderer = MRenderer;
		this.RendEntHandle = RendEntHandle;
		this.RENDER_LEVEL = level;
	}
	
	public void init(H RendEntHandle) {
		this.RendEntHandle = RendEntHandle;
	}
	
	public float get_render_level() {return this.RENDER_LEVEL;}
	
	public abstract void dispose();
}
