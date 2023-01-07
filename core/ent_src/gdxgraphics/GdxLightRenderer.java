package gdxgraphics;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import render.GameCamera;
import render.LightHandler;
import render.RenderInfo;
import render.basic.Light;

public class GdxLightRenderer {

	LightHandler LightHandle;
	ShaderProgram shadowMapShader,shadowRenderShader;
    FrameBuffer shadarrFBO[],lightFBO, lightMaskFBO;
    FrameBuffer occluderFBOArray[];
	public Texture lightMaskTexture,lightMapTexture;
	
    TextureRegion lightTexRegion;
    GdxTextureHandler GdxTextureHandle;
	OrthographicCamera lightCamera;
	GameCamera GCam;
    float THRESHOLD = 0.2f;
	
	public GdxLightRenderer(GameCamera GCam,GdxShaderManager ShaderManage, GdxTextureHandler TexHandler,LightHandler LightHandle) {
		this.GCam = GCam;
		this.GdxTextureHandle = TexHandler;
		this.LightHandle = LightHandle;
		shadowMapShader = ShaderManage.get_shader("shadow_map");
		shadowRenderShader = ShaderManage.get_shader("shadow_render");
//		this.THRESHOLD = RenderInfo.AMBIENT_LIGHT;
		System.out.println("threshold "+this.THRESHOLD);
		 
		int lightSize = 512;
		shadarrFBO = new FrameBuffer[16];
		for (int p=0;p<16;p++) {
		 	shadarrFBO[p] = new FrameBuffer(Format.RGBA8888,lightSize,1,false);
		 }
		
		 occluderFBOArray = new FrameBuffer[4];
		 int occluderSize = 256;
		 for(int i = 0; i < 4; i++) {
			 occluderFBOArray[i] =  new FrameBuffer(Format.RGBA8888, occluderSize, occluderSize, false);
		 }

		 int screen_size_w = RenderInfo.get_screen_width();
		 int screen_size_h = RenderInfo.get_screen_height();
		 lightFBO = new FrameBuffer(Format.RGBA8888,screen_size_w,screen_size_h,false);
		 lightMaskFBO = new FrameBuffer(Format.RGBA8888,screen_size_w,screen_size_h,false);
		 
		 lightMapTexture = lightFBO.getColorBufferTexture();
		 lightMaskTexture = lightMaskFBO.getColorBufferTexture();
		 
		 lightTexRegion = new TextureRegion(lightMapTexture);
		 lightCamera = new OrthographicCamera();
	}
	
	
	Light screen_lights[] = new Light[16];
	
	void render_light1(SpriteBatch spriteBatch) {
		lightFBO.begin();
		Gdx.gl.glClearColor(THRESHOLD,THRESHOLD,THRESHOLD,THRESHOLD);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		lightFBO.end();

		lightMaskFBO.begin();
		Gdx.gl.glClearColor(0,0,0,1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		lightMaskFBO.end();
	}
	
	void render_light(SpriteBatch spriteBatch) {
		THRESHOLD=0.2f;
		int lightsceneid=0;
		int lightlimit = 16, i = 0;
		lightFBO.begin();
		Gdx.gl.glClearColor(THRESHOLD,THRESHOLD,THRESHOLD,0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		lightFBO.end();

		lightMaskFBO.begin();
		Gdx.gl.glClearColor(0,0,0,1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		lightMaskFBO.end();

		int occludeid=0;
		Light visp=null;
		for(Iterator <Light> light_iterator = LightHandle.get_iterator(GCam); light_iterator.hasNext();) {
			lightsceneid = 0;
			for (i = 0; i < lightlimit && light_iterator.hasNext();i++) {
				for(occludeid=0;occludeid<occluderFBOArray.length && light_iterator.hasNext();occludeid++) {
					visp = light_iterator.next();
					occluderFBOArray[occludeid].begin();
					Gdx.gl.glClearColor(0,0,0,0f);
					Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
					lightCamera.setToOrtho(false, visp.bound.w(), visp.bound.w());
					lightCamera.position.set(visp.pos.x()+visp.bound.w()/2, visp.pos.y()+visp.bound.w()/2,0);
					lightCamera.update();
					spriteBatch.setProjectionMatrix(lightCamera.combined);
					GdxTextureHandle.render_basemap(spriteBatch, visp.get_view_rect(), true);
					occluderFBOArray[occludeid].end();
					screen_lights[lightsceneid+occludeid] = visp;
				}
				
				for(int j = 0; j < occludeid; j++) {
					shadarrFBO[lightsceneid].begin();
					Gdx.gl.glClearColor(0f,0f,0f,0f);
					Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
					spriteBatch.setShader(shadowMapShader);
					spriteBatch.begin();
					float val = 256;
					shadowMapShader.setUniformf("resolution", val,val);
					lightCamera.setToOrtho(false, val, shadarrFBO[lightsceneid].getHeight());
					lightCamera.position.set(0,0,0);
					spriteBatch.setProjectionMatrix(lightCamera.combined);
					spriteBatch.draw(occluderFBOArray[j].getColorBufferTexture(), 0, 0, val, shadarrFBO[lightsceneid].getHeight());
					spriteBatch.end();
					shadarrFBO[lightsceneid].end();
					lightsceneid++;
				}
			}
			
			lightFBO.begin();
			lightCamera.setToOrtho(true,GCam.w(),GCam.h());
			lightCamera.position.set(GCam.center_x(),GCam.center_y(),0);
			lightCamera.update();
			spriteBatch.setProjectionMatrix(lightCamera.combined);
			spriteBatch.setShader(shadowRenderShader);
			spriteBatch.begin();
//			shadowRenderShader.setUniformf("softShadows", true ? 1f : 0f);
//			shadowRenderShader.setUniformf("resolution", 1,1);
			for(i=0;i<lightsceneid;i++) {
				visp = screen_lights[i];
				spriteBatch.setColor(visp.RED,visp.GREEN,visp.BLUE,1);
				spriteBatch.draw(shadarrFBO[i].getColorBufferTexture(), visp.pos.x(), visp.pos.y(), visp.bound.w(), visp.bound.w());
			}

			spriteBatch.end();
			spriteBatch.setShader(null);
			lightFBO.end();
			
			spriteBatch.setColor(1,1,1,1);
		}
	}
	
	public void dispose() {
		for (int p=0;p<16;p++) {
		 	shadarrFBO[p].dispose();
		 }
		
		 for(int i = 0; i < 4; i++) {
			 occluderFBOArray[i].dispose();
		 }
		 lightMaskTexture.dispose();
		 lightMapTexture.dispose();
		 lightFBO.dispose();
		 lightMaskFBO.dispose();
	}
}
