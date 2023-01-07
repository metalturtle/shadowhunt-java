package gdxgraphics;

import java.util.Iterator;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import basic.Rectangle;
import game.render.HidingSpriteHandler;
import render.GameCamera;
import render.RenderInfo;
import render.SpriteHandler;
import render.basic.Sprite;

public class GdxHidingSpriteHandler extends GdxRenderer<Sprite,HidingSpriteHandler> {

	GdxLightRenderer LightRenderer;
	ShaderProgram spriteLightShader;
	GdxTextureResourceManager TexResourceManage;
	
	public GdxHidingSpriteHandler(GdxMainRenderer GdxMRenderer, HidingSpriteHandler HASH) {
		super(GdxMRenderer, HASH,RenderLevel.SPRITES);
		spriteLightShader = GdxMRenderer.GdxShaderManage.get_shader("sprite_light");
		LightRenderer = GdxMRenderer.GdxLightRender;
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
		spriteBatch.setShader(spriteLightShader);
		LightRenderer.lightMapTexture.bind(1);
		LightRenderer.lightMapTexture.bind(0);
		spriteLightShader.begin();
		spriteLightShader.setUniformi("u_lightmap",1);
		spriteLightShader.setUniformf("resolution",RenderInfo.get_screen_width(),RenderInfo.get_screen_height());
		spriteLightShader.setUniformf("transparent", this.RendEntHandle.get_mode());
		spriteLightShader.end();

		for(int i=0; i < 3;i++) {
			for(Iterator <Sprite> it = RendEntHandle.spriteRenderer[i].get_iterator(camView); it.hasNext();) {
				Sprite sprite = it.next();
				render_entity(sprite,spriteBatch);
			}
		}
		
		spriteBatch.setShader(null);
	}
	
	@Override
	public void dispose() {
		
	}
}
