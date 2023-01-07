package gdxgraphics;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import game.render.SparkHandler;
import game.render.entity.SparkSim;

public class GdxSparkRenderer extends GdxRenderer<SparkSim,SparkHandler> {

	TextureRegion sparkTexture;
	public GdxSparkRenderer(GdxMainRenderer MRenderer, SparkHandler RendEntHandle) {
		super(MRenderer, RendEntHandle, RenderLevel.EFFECTS);
		sparkTexture = MRenderer.TexResourceManage.get_texture("spark");
	}
	@Override
	public void render_entity(SparkSim sparkSim, SpriteBatch spriteBatch) {
		sparkSim.run();
		for(int i = 0; i < sparkSim.get_limit(); i++) {
			spriteBatch.draw(sparkTexture,
			sparkSim.x[i],
			sparkSim.y[i],
			0,0,
			sparkSim.w[i],
			sparkSim.h[i], 1f, 1f,
			sparkSim.ang[i]);
		}
	}
	
	@Override
	public void dispose() {
		
	}
}
