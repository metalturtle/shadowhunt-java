package game.render;

import basic.Rectangle;
import basic.Vector;
import game.render.entity.SparkSim;
import render.RenderEntityHandler;
import render.RenderEntityMainHandler;
import util.ConfigManager;

public class SparkHandler extends RenderEntityHandler<SparkSim> {

	boolean first;
	final int MAX_SPARK;
	public SparkHandler (RenderEntityMainHandler RendEntMainHandle) {
		super(RendEntMainHandle);
		this.MAX_SPARK = 10;
//		for(int i = 0; i < MAX_SPARK; i++) {
//			super.add_resource_object(new Vector(),new Rectangle(),null);
//		}
//		for(int i = 0; i < 4; i++) {
//			SmokePartSim[i] = new SmokeParticleSim(center);
//		}
	}
	
	Vector center = new Vector(250,110);

	@Override
	public void renderer_init(ConfigManager ConfigManage) {
//		whiteTexture = RendHandle.TexHandle.get_texture("confetti");
	}
	
//	@Override
//	public void texture_init(TextureManager TexManage) {
//		SparkSim.sparkTexture = TexManage.get_texture("spark");
//	}
//
//	@Override
//	public void render_entity(SparkSim rend_ent, SpriteBatch spriteBatch) {
//		rend_ent.run(spriteBatch);
//	}
	
	int count = 0;
	@Override
	public SparkSim add_resource_object(Vector pos, Rectangle bound, String params) {
		SparkSim sparkSim = super.add_resource_object(pos,bound,params);
		sparkSim.INIT_DONE=false;
		sparkSim.set_forever(false);
		sparkSim.set_lifespan(150);
		count +=1;
		return sparkSim;
	}

	@Override
	public SparkSim create_new_element() {
		return new SparkSim(new Vector(0,0), new Rectangle(0,0,0,0));
	}

//	@Override
//	protected void disable_marked_entities() {
//		super.disable_marked_entities();
//	}
	
	@Override
	public void dispose() {
		
	}
}