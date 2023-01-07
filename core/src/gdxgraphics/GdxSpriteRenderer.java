package gdxgraphics;

import java.util.Iterator;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import basic.Rectangle;
import render.GameCamera;
import render.RenderEntityHandler;
import render.Renderer;
import render.SpriteHandler;
import render.basic.Sprite;
import resource.TextureResourceManager;


public class GdxSpriteRenderer extends GdxRenderer<Sprite,SpriteHandler> {

	GdxTextureResourceManager TexResourceManage;
	public GdxSpriteRenderer(GdxMainRenderer GdxMRenderer,SpriteHandler SpriteHandle) {
		super(GdxMRenderer,SpriteHandle,Renderer.RenderLevel.SPRITES);
		this.TexResourceManage = GdxMRenderer.TexResourceManage;
	}

	@Override
	public void render_entity(Sprite sprite, SpriteBatch spriteBatch) {
		TextureRegion currentFrame = TexResourceManage.get_sprite_texture(sprite);
		Rectangle sp_bound = sprite.get_bound();
		spriteBatch.setColor(sprite.get_r(),sprite.get_b(),sprite.get_g(),sprite.get_a());
		spriteBatch.draw(currentFrame,sprite.pos.x()+ sp_bound.x(),sprite.pos.y()+sp_bound.y(),-sp_bound.x(),-sp_bound.y(), sp_bound.w(), sp_bound.h(), 1f, 1f, sprite.get_angle());
	}
	
	@Override
	public void render_loop(OrthographicCamera screenCamera, OrthographicCamera worldCamera, GameCamera camView, SpriteBatch spriteBatch) {
		for(int i=0; i < 3;i++) {
			for(Iterator <Sprite> it = RendEntHandle.spriteRenderer[i].get_iterator(null); it.hasNext();) {
				Sprite sprite = it.next();
				render_entity(sprite,spriteBatch);
			}
		}
	}
	
	@Override
	public void dispose() {
		
	}
	
}
