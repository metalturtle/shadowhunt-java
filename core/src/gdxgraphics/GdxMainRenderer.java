package gdxgraphics;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import render.GameCamera;
import render.MainRenderer;
import render.RenderEntityMainHandler;
import render.RenderInfo;
import render.Renderer;
import render.basic.TextureObject;
import resource.TextureResourceManager;
import util.ConfigManager;

public class GdxMainRenderer extends MainRenderer<GdxTextureResourceManager,GdxRenderer> {

	public GdxTextureHandler TexHandle;
	public GdxShaderManager GdxShaderManage;
	public GdxLightRenderer GdxLightRender;
	public GdxGUIRenderer GdxGUIRender;
	SpriteBatch spriteBatch;
	OrthographicCamera worldRenderCamera,screenRenderCamera;
	GameCamera GCamera;
	
	public GdxMainRenderer() {
		super();
//		gameCamera.set_zoom(1.5f);

		worldRenderCamera = new OrthographicCamera(RenderInfo.WORLD_X,RenderInfo.WORLD_Y);
		worldRenderCamera.setToOrtho(false);
		worldRenderCamera.update();
		
		screenRenderCamera = new OrthographicCamera();
		screenRenderCamera.setToOrtho(false);
		screenRenderCamera.position.set(RenderInfo.SCREEN_WIDTH/2,RenderInfo.SCREEN_HEIGHT/2,0);
		screenRenderCamera.update();
		
		spriteBatch = new SpriteBatch();
	}
	
	@Override
	public void load(ConfigManager ConfigManage, JSONObject levelFile) throws Exception {
		this.rendererList.clear();
		this.TexResourceManage = create_texture_resource_manager();
		
		JSONArray textureArr = (JSONArray)levelFile.get("texture_demand");
		String tex_dem[] = new String[textureArr.size()];
		int i=0;
		for(; i < textureArr.size(); i++) {
			tex_dem[i] = (String) textureArr.get(i);
		}
		
		this.TexResourceManage.load(ConfigManage, tex_dem);
		this.TexIDManage = this.TexResourceManage.get_texture_id_manager();

		JSONObject module = (JSONObject)levelFile.get("texture");
		JSONArray array;
		int size=0;
		TextureObject[] tex_objects=null;
		if (module != null)
		{
			size = ((Number)module.get("size")).intValue();
			array = (JSONArray)module.get("object");
			tex_objects = new TextureObject[size];
			for(i=0;i<size;i++) {
				JSONArray texjson = (JSONArray)array.get(i);
				float w = (((Number)texjson.get(3)).floatValue()), h = (((Number)texjson.get(4)).floatValue());
				float u1 = ((Number)texjson.get(5)).floatValue(), v1 = ((Number)texjson.get(6)).floatValue();
				float u2 = ((Number)texjson.get(7)).floatValue(),v2 = ((Number)texjson.get(8)).floatValue();
			
				tex_objects[i] = (new TextureObject(TexIDManage.get_texture_id(texjson.get(0).toString()),
						((Number)texjson.get(1)).floatValue(),
						((Number)texjson.get(2)).floatValue(),
						w,
						h,
						u1,v1,u2,v2,
						((Number)texjson.get(9)).floatValue(),(Boolean)texjson.get(10))
						);
				}
		} else {
			throw new Exception("ERROR: texture module not found in level file");
		}
		
		this.TexHandle = new GdxTextureHandler(TexResourceManage);
		this.TexHandle.init(tex_objects);
		
		this.GdxShaderManage = new GdxShaderManager(ConfigManage);
		
		this.RenderEntityMainHandle = new RenderEntityMainHandler(this.TexIDManage);
		
		this.GdxLightRender = new GdxLightRenderer(RenderEntityMainHandle.GCamera,GdxShaderManage,TexHandle,RenderEntityMainHandle.LightHandle);
		this.GCamera = RenderEntityMainHandle.GCamera;
		this.rendererList.add(new GdxSpriteRenderer(this,this.RenderEntityMainHandle.SpriteHandle));
		this.rendererList.add(new GdxEffectRenderer(this,this.RenderEntityMainHandle.EffectHandle));
		
		GdxGUIRender = new GdxGUIRenderer(this);
	}

	@Override
	public void init() {
		
	}

	float val=0;
	@Override
	public void render() {
		Gdx.gl.glClearColor(0,0,0,1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		spriteBatch.setColor(1f,1f,1f,1f);
		
		GCamera.set(this.RenderEntityMainHandle.GCamera);
		GCamera.update();
		worldRenderCamera.setToOrtho(true,GCamera.w(),GCamera.h());
		worldRenderCamera.position.set(GCamera.center_x(),GCamera.center_y(),0);
//		System.out.println("gcamera: "+(GCamera.center_x()-val));
		val = GCamera.center_x();
		worldRenderCamera.update();
		spriteBatch.setProjectionMatrix(worldRenderCamera.combined);
		TexHandle.generate_basemap_fbo(spriteBatch,GCamera,false);
		
		spriteBatch.enableBlending();
		spriteBatch.begin();
		spriteBatch.setShader(null);
		spriteBatch.setProjectionMatrix(screenRenderCamera.combined);
//		float val = GdxLightRender.THRESHOLD;
		float val = 0.5f;
//		spriteBatch.setColor(val,val,val,val);
		spriteBatch.draw(TexHandle.baseMapTexture,0,0,RenderInfo.SCREEN_WIDTH,RenderInfo.SCREEN_HEIGHT);
		spriteBatch.end();
		
		spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
		GdxLightRender.render_light(spriteBatch);
		
		spriteBatch.setBlendFunction(GL20.GL_ONE,GL20.GL_ONE_MINUS_SRC_ALPHA);
		
		spriteBatch.begin();
		spriteBatch.setProjectionMatrix(screenRenderCamera.combined);
		spriteBatch.setBlendFunction(GL20.GL_ZERO,GL20.GL_SRC_COLOR);
		spriteBatch.draw(GdxLightRender.lightMapTexture,0,0,RenderInfo.SCREEN_WIDTH,RenderInfo.SCREEN_HEIGHT);
		spriteBatch.end();
		
		spriteBatch.begin();
		render_handlers(GCamera,Renderer.RenderLevel.GROUND+1,Renderer.RenderLevel.SKY);
		spriteBatch.end();
		
//		spriteBatch.begin();
//		spriteBatch.setColor(128,128,128,1f);
//		spriteBatch.setProjectionMatrix(screenRenderCamera.combined);
////		spriteBatch.begin();
//		spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
////		GdxGUIRender.render_loop(worldRenderCamera,screenRenderCamera,GCamera,spriteBatch);
//		
//		spriteBatch.end();
		
		GdxGUIRender.render();
//		GdxGUIRender.render(spriteBatch);
	}
	
	public void render_handlers(GameCamera camView, float low_level,float high_level) {
		worldRenderCamera.setToOrtho(false,camView.w(),camView.h());
		worldRenderCamera.position.set(camView.x()+camView.w()/2,camView.y()+camView.h()/2,0);
		worldRenderCamera.update();
		spriteBatch.setProjectionMatrix(worldRenderCamera.combined);

		spriteBatch.setBlendFunction(GL20.GL_ONE,GL20.GL_ONE_MINUS_SRC_ALPHA);
		for(int i=0; i < rendererList.size(); i++) {
			GdxRenderer renderer = rendererList.get(i);
			if(renderer.get_render_level() >= low_level && renderer.get_render_level()<=high_level)
				renderer.render_loop(worldRenderCamera,screenRenderCamera,camView,spriteBatch);
		}
	}
	
	public void render_handlers(GameCamera camView) {	
		worldRenderCamera.setToOrtho(false,camView.w(),camView.h());
		worldRenderCamera.position.set(camView.x()+camView.w()/2,camView.y()+camView.h()/2,0);
		worldRenderCamera.update();
		spriteBatch.setProjectionMatrix(worldRenderCamera.combined);

		spriteBatch.setBlendFunction(GL20.GL_ONE,GL20.GL_ONE_MINUS_SRC_ALPHA);
		
		for(int i=0; i < rendererList.size(); i++) {
			GdxRenderer renderer = rendererList.get(i);
			renderer.render_loop(worldRenderCamera,screenRenderCamera,camView,spriteBatch);
		}
	}

	@Override
	public GdxTextureResourceManager create_texture_resource_manager() {
		return new GdxTextureResourceManager();
	}
	
	@Override
	public void dispose() {
		this.TexResourceManage.dispose();
		GdxShaderManage.dispose();
		GdxLightRender.dispose();
		GdxGUIRender.dispose();
		TexHandle.dispose();
	}

}
