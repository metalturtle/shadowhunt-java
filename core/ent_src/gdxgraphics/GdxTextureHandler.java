package gdxgraphics;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

import basic.Rectangle;
import basic.resource.static_resource.StaticWorldResourceHandler;
import basic.resource.static_resource.StaticWorldResourceObject;
import render.RenderInfo;
import render.basic.TextureObject;


public class GdxTextureHandler extends StaticWorldResourceHandler<TextureObject> {
	HashMap <String, Integer> textureNameMap;
	Texture baseMapTexture;
	FrameBuffer basemapFBO;
	String FOLDER_NAME;
	GdxTextureResourceManager GdxTexResManage;
	
	Random rand = new Random();
	GdxTextureHandler(GdxTextureResourceManager GdxTexResManage) {
		super();
		this.GdxTexResManage = GdxTexResManage;
		this.CELL_SIZE = 120;
		basemapFBO  = new FrameBuffer(Format.RGBA8888,RenderInfo.SCREEN_WIDTH,RenderInfo.SCREEN_HEIGHT,false);
		baseMapTexture = basemapFBO.getColorBufferTexture();
	}
	
	int maxlim=1;
	long waitval;
	boolean freeze,texturetest=false;
	TextureObject freezetex;
	float tu,tv;
	public void render_basemap(SpriteBatch spriteBatch,Rectangle view_rect,boolean light_block) {
		spriteBatch.begin();
		for(Iterator <TextureObject> it = get_iterator(view_rect); it.hasNext();) {
			TextureObject tex = it.next();
			if(light_block && !tex.lightBlock) continue;
//			int id = TexManage.get_texture_id("forklift");
			float u1=tex.u1,v1=tex.v1,u2=tex.u2,v2=tex.v2;
			
			if(u1 == u2 || v1==v2) {
				continue;
			}
				render_texture(tex,spriteBatch);
		}
		spriteBatch.end();
	}
	

	void render_texture(TextureObject tex,SpriteBatch spriteBatch) {
		float u1=tex.u1,v1=tex.v1,u2=tex.u2,v2=tex.v2;
		
		if(u1 == u2 || v1==v2) {
			return;
		}
		TextureRegion texreg = GdxTexResManage.atlasTextures.get(tex.texid);
		if(Math.signum(u1) == -1 || Math.signum(u2) == -1) {
			float minu = (float)Math.ceil(Math.abs(Math.min(u1, u2)));
			u1 += minu;
			u2 += minu;
		}
		
		if(Math.signum(v1) == -1 || Math.signum(v2) == -1) {
			float minu = (float)Math.ceil(Math.abs(Math.min(v1, v2)));
			v1 += minu;
			v2 += minu;
		}
		
		float transu = texreg.getU2()-texreg.getU(),transv=texreg.getV2()-texreg.getV();
		float curv = v1;
		float prevv = v1;
		boolean dirv = v1<v2;
		boolean vcheck = dirv? curv<v2 : curv>v2;
		float lowv=0;
		float tv1,tv2;
		
		float curu = u1;
		float prevu = u1;
		boolean diru = u1<u2;
		boolean ucheck = diru? curu<u2 : curu>u2;
		float lowu=0;
		float tu1,tu2;
		
		float curw,curh,prevw=0,prevh=0;
		while(vcheck) {
			if(dirv) {
				curv = Math.min((float)Math.floor(curv+1),v2);
				lowv = fract(curv);
				if(!(lowv>0)) lowv = 1;
				tv1=fract(prevv);
				tv2=lowv;
			}
			else {
				curv = Math.max((float)Math.ceil(curv-1),v2);
				lowv = fract(prevv);
				if(!(lowv>0)) lowv = 1;
				tv1=lowv;
				tv2=fract(curv);
			}
			curh = tex.h()*Math.abs((tv2-tv1)/(v2-v1));
			
			curu = u1;
			prevu = u1;
			diru = u1<u2;
			ucheck = diru? curu<u2 : curu>u2;
			lowu=0;
			
			prevw=0;
			
			while(ucheck) {
				if(diru) {
					curu = Math.min((float)Math.floor(curu+1),u2);
					lowu = fract(curu);
					if(!(lowu>0)) lowu = 1;
					tu1=fract(prevu);
					tu2=lowu;
				}
				else {
					curu = Math.max((float)Math.ceil(curu-1),u2);
					lowu = fract(prevu);
					if(!(lowu>0)) lowu = 1;
					tu1=lowu;
					tu2=fract(curu);
				}

				curw = tex.w()*Math.abs((tu2-tu1)/(u2-u1));
				
				float finalu1 = texreg.getU()+transu*tu1;
				float finalu2 = texreg.getU()+transu*tu2;
				float finalv1 = texreg.getV()+transv*tv1;
				float finalv2 = texreg.getV()+transv*tv2;
	
				spriteBatch.draw(texreg.getTexture(),tex.x()+prevw,tex.y()+prevh,curw,curh,finalu1,finalv1,finalu2,finalv2);
				
				prevw+=curw;
				prevu  = curu;
				ucheck = diru? curu<u2 : curu>u2;
			}
			
			prevh+=curh;
			prevv = curv;
			vcheck = dirv? curv<v2 : curv>v2;
		}
	}
	
	public void generate_basemap_fbo(SpriteBatch spriteBatch,Rectangle view_rect,boolean light_block) {
		basemapFBO.begin();
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		spriteBatch.setShader(null);
		render_basemap(spriteBatch,view_rect,light_block);
		basemapFBO.end();
	}
	
	
	void positive_val(float arr[]) {
		float a = arr[0],b=arr[1];
		if(Math.signum(a) == -1 || Math.signum(b) == -1) {
			float maxf = (float)Math.floor(Math.min(a, b));
			a -= maxf;
			b-= maxf;
		}
		arr[0]=a;arr[1]=b;
	}
	
	
	float next_val(float cura,float b) {
		float next = Math.signum(b-cura);
		if(Math.abs(fract(cura))>0) {
			next = next==-1?-fract(cura):1-fract(cura);
		}
//		System.out.println("next: "+next);
		return clamp(cura+next,b,(int)Math.signum(b-cura));
	}
	
	float fract(float v) {
		return v%1f;
	}
	
	float clamp(float a,float c1, int s) {
		if(s<0 && a< c1) {
			return c1;
		}
		if(s>0 && a > c1) {
			return c1;
		}
		return a;
	}
	
	Rectangle intersect = new Rectangle();
	@Override
	public void split_static_objects(StaticWorldResourceObject static_res, StaticWorldResourceObject[][] split_res_arr, Rectangle[][] cell_bound, int size_x, int size_y) {
		TextureObject res = (TextureObject)static_res;
//		res.v2 *=-1;
		float prevu = fract(res.u1),prevv = fract(res.v1);
		float usize = res.u2-res.u1,vsize = res.v2-res.v1;
//		int check_id = 20;
		int check_id = 7;
//		for(int i =size_y-1;i>-1;i--) 
		if(res.texid == check_id) {
			System.out.println("res: "+res);
		}
		for(int i = 0; i < size_y; i++) {
			prevu=fract(res.u1);
			for(int j=0;j<size_x;j++) {
				TextureObject split_res = (TextureObject)split_res_arr[i][j];

//				Rectangle intersect = Rectangle.intersection(res, cell_bound[i][j]);
				Rectangle.intersection(res,cell_bound[i][j],intersect);
				split_res.lightBlock = res.lightBlock;
				split_res.texid = res.texid;
				float px = intersect.w()/res.w();
				float py = intersect.h()/res.h();

				split_res.u2 = usize*px+prevu;
				split_res.v2 = vsize*py+prevv;
				split_res.u1 = prevu;
				split_res.v1 = prevv;
				
				prevu = split_res.u2;
				if(j == size_x-1)
					prevv = split_res.v2;
			}
		}
	}

	@Override
	public TextureObject create_new_element() {
		return new TextureObject();
	}
	

	void dispose() {
		basemapFBO.dispose();
	}
}
