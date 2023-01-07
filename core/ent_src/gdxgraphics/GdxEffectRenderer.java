package gdxgraphics;

import java.util.Iterator;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import render.EffectHandler;
import render.GameCamera;
import render.SpriteHandler;
import render.basic.Sprite;

public class GdxEffectRenderer extends GdxSpriteRenderer {

	public GdxEffectRenderer(GdxMainRenderer GdxMRenderer, EffectHandler EffectHandle) {
		super(GdxMRenderer, EffectHandle);
	}
	
	@Override
	public void render_loop(OrthographicCamera screenCamera, OrthographicCamera worldCamera, GameCamera camView, SpriteBatch spriteBatch) {
		for(int i=0; i < 3;i++) {
			for(Iterator <Sprite> it = RendEntHandle.spriteRenderer[i].get_iterator(null); it.hasNext();) {
				Sprite sprite = it.next();
//				System.out.println("sprite: "+sprite);
				render_entity(sprite,spriteBatch);
			}
		}
	}

}
