package gdxgraphics;

import java.util.Iterator;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import render.GameCamera;
import render.RenderEntityHandler;
import render.Renderer;
import render.basic.RenderEntity;

public abstract class GdxRenderer<R extends RenderEntity,H extends RenderEntityHandler<R>> extends Renderer<R,H> {
	protected SpriteBatch spriteBatch;
	
	public GdxRenderer(GdxMainRenderer GdxMRenderer,H RendEntHandle,byte level) {
		super(GdxMRenderer,RendEntHandle,level);
	}
	
	public abstract void render_entity(R rend_ent,SpriteBatch spriteBatch);
	
	public void render_loop(OrthographicCamera screenCamera,OrthographicCamera worldCamera, GameCamera camView, SpriteBatch spriteBatch) {
		for(Iterator<R> iterator = RendEntHandle.get_iterator(camView);iterator.hasNext();) {
			R ent = iterator.next();
			spriteBatch.setColor(ent.get_r(),ent.get_g(),ent.get_b(),ent.get_a());
			render_entity(ent,spriteBatch);
		}
	}

}
