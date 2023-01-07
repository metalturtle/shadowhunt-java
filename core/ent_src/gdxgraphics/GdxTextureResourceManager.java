package gdxgraphics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.json.simple.JSONObject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import render.basic.Sprite;
import resource.TextureNameMap;
import resource.TextureResourceManager;
import util.ConfigManager;

public class GdxTextureResourceManager extends TextureResourceManager {

	public Animation <TextureRegion> spriteAnimations[];
	ArrayList <TextureRegion>  atlasTextures;
	String [] texture_demand;
	TextureAtlas spriteAtlas,textureAtlas;
	
	
	public GdxTextureResourceManager() {
		super();
		atlasTextures = new ArrayList<TextureRegion>();
	}

	@Override
	protected void load_sprites(ConfigManager ConfigManage) {
		String folder;
		String[] sprite_names = ConfigManage.get_keys("sprite", "sprite");
		String[] anim_sprite_names = ConfigManage.get_keys("sprite", "animated_sprite");
		HashMap <Object,Object> sprite_map = ConfigManage.get_object("sprite", "sprite");
		HashMap <Object,Object> anim_sprite_map = ConfigManage.get_object("sprite", "animated_sprite");
	
		int sprite_index = 0;
		spriteAnimations = new Animation[sprite_names.length + anim_sprite_names.length];
		folder = (String)sprite_map.get("folder");
		
		int size = 1<<8;
		
		PixmapPacker packer = new PixmapPacker(size, size, Format.RGBA8888, 2, true);
		
		for(String sprite_name: sprite_names) {
			if(sprite_name.equals("folder"))
				continue;
			String sprite_file_name = (String)sprite_map.get(sprite_name);
			Texture tex = new Texture(Gdx.files.internal(folder+"/"+sprite_file_name));
			TextureData textureData = tex.getTextureData();
			if (!textureData.isPrepared()) {
			    textureData.prepare();
			}
			Pixmap pixmap = textureData.consumePixmap();
			packer.pack(sprite_name,pixmap);
			tex.dispose();
		}
		
		folder=(String)anim_sprite_map.get("folder");
		for(String sprite_name: anim_sprite_names) {
			if(sprite_name.equals("folder"))
				continue;
			JSONObject anim_sprite_obj = (JSONObject)anim_sprite_map.get(sprite_name);
			String anim_sprite_file_name = (String)anim_sprite_obj.get("file");
			Texture tex = new Texture(Gdx.files.internal(folder+"/"+anim_sprite_file_name));
			TextureData textureData = tex.getTextureData();
			if (!textureData.isPrepared()) {
			    textureData.prepare();
			}
			Pixmap pixmap = textureData.consumePixmap();
			packer.pack(sprite_name,pixmap);
			tex.dispose();
		}
		
		spriteAtlas = packer.generateTextureAtlas(TextureFilter.Nearest, TextureFilter.Nearest, false);
		packer.dispose();
		///////////////////////////////////////////////////////////////
		
		folder = (String)sprite_map.get("folder");
		for(String sprite_name:sprite_names) {
			if(sprite_name.equals("folder"))
				continue;
			spriteAnimations[sprite_index] = add_sprite(sprite_name);
			spriteMap.put(sprite_name, new TextureNameMap((byte)0, sprite_index));
			sprite_index+=1;
		}
		TYPEID_SPRITE=sprite_index;
	
		folder=(String)anim_sprite_map.get("folder");
		long col,row;
		double speed;
		for(String anim_sprite_name:anim_sprite_names) {
			if(anim_sprite_name.equals("folder"))
				continue;
			col = 1;row=1;speed=0.1f;
			
			JSONObject anim_sprite_obj = (JSONObject)anim_sprite_map.get(anim_sprite_name);
			if(anim_sprite_obj.containsKey("col")){
				col = (Long)anim_sprite_obj.get("col");
			}
			if(anim_sprite_obj.containsKey("row")){
				row = (Long)anim_sprite_obj.get("row");
			}
			if(anim_sprite_obj.containsKey("speed")){
				speed = (Double)anim_sprite_obj.get("speed");
			}
			spriteAnimations[sprite_index] = add_animation(anim_sprite_name,(int)col,(int)row,(float)speed,true);
			spriteMap.put(anim_sprite_name, new TextureNameMap((byte)1, sprite_index));
			sprite_index++;
		}
		
		TYPEID_ANIMATED_SPRITE = sprite_index;
	}

	@Override
	protected void load_textures(ConfigManager ConfigManage, String[] texture_demand) {
		int val = 1<<12;
		System.out.println("val "+val);
		PixmapPacker packer = new PixmapPacker(val, val, Format.RGBA8888, 2, true);
		String folder = (String)ConfigManage.get_object("resource", "texture").get("folder");
		int i = 0;
		for (; i < texture_demand.length; i++) {
			String file_name = texture_demand[i];

			Texture texture = new Texture(Gdx.files.internal(folder+"/"+file_name));
			texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
			TextureData textureData = texture.getTextureData();
			if (!textureData.isPrepared()) {
			    textureData.prepare();
			}
			Pixmap pixmap = textureData.consumePixmap();
			packer.pack(file_name,pixmap);
			texture.dispose();
		}
		
		HashMap <Object,Object> basic_texture = ConfigManage.get_object("resource", "basic_texture");
		folder = (String)basic_texture.get("folder");
		Set keySet = basic_texture.keySet();
		
		for(Object keyObj: keySet) {
			String key = (String)keyObj;
			if(key.equals("folder"))
				continue;
			String file_name = (String)basic_texture.get(key);
			Texture texture = new Texture(Gdx.files.internal(folder+"/"+file_name));
			texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
			TextureData textureData = texture.getTextureData();
			if (!textureData.isPrepared()) {
			    textureData.prepare();
			}
			Pixmap pixmap = textureData.consumePixmap();
			packer.pack(key,pixmap);
			texture.dispose();
		}
		
		textureAtlas = packer.generateTextureAtlas(TextureFilter.Nearest, TextureFilter.Nearest, false);
		packer.dispose();
		
		for(i=0;i<texture_demand.length;i++) {
			String file_name = texture_demand[i];
			TextureRegion atlastex = textureAtlas.findRegion(file_name);
			TextureRegion texreg = new TextureRegion(atlastex.getTexture());
			texreg.setRegion(atlastex.getRegionX(),atlastex.getRegionY(),atlastex.getRegionWidth(),atlastex.getRegionHeight());		
			atlasTextures.add(texreg);
			String file_key = file_name.substring(0,file_name.indexOf("."));
			textureNameMap.put(file_key, new TextureNameMap((byte)0, i));
		}
		
		for(Object keyObj: keySet) {
			String key = (String)keyObj;
			if(key.equals("folder"))
				continue;
			
			TextureRegion atlastex = textureAtlas.findRegion(key);
			TextureRegion texreg = new TextureRegion(atlastex.getTexture());
			texreg.setRegion(atlastex.getRegionX(),atlastex.getRegionY(),atlastex.getRegionWidth(),atlastex.getRegionHeight());
			atlasTextures.add(texreg);
			
			textureNameMap.put(key, new TextureNameMap((byte)0, i));
			i+=1;
		}
		
	}

	
	
	Animation<TextureRegion> add_sprite(String name) {
		TextureRegion texreg = spriteAtlas.findRegion(name);
		Animation<TextureRegion> animation = new Animation<TextureRegion>(1, texreg);
		return animation;
	}
	
	
	Animation<TextureRegion> add_animation(String name,int cols,int rows,float speed,boolean repeat) {
		TextureRegion texreg = spriteAtlas.findRegion(name);
		
		int width = texreg.getRegionWidth();
		int height = texreg.getRegionHeight();
		TextureRegion[] walkFrames = new TextureRegion[cols*rows];
		int index = 0;

		for (int j = 0; j < cols; j++)
		{
			for (int i = 0; i < rows; i++) {
				walkFrames[index] = new TextureRegion(texreg.getTexture());
				TextureRegion framereg = walkFrames[index];
				framereg.setRegion(texreg.getRegionX()+i*(width/cols),texreg.getRegionY()+j*(height/rows),width/cols,height/rows);
				index+=1;
			}
		}
		Animation<TextureRegion> animation = new Animation<TextureRegion>(speed, walkFrames);
		return animation;
	}
	
	///////////////////////////////////////////
	
	
	public TextureRegion get_sprite_texture(Sprite sprite) {
		int sprite_id = sprite.get_sprite_type();
		if(sprite_id<TYPEID_ANIMATED_SPRITE)
		return spriteAnimations[sprite_id].getKeyFrame(sprite.get_animation_time(),true);
		else {
			sprite_id = sprite.get_sprite_type()-TYPEID_ANIMATED_SPRITE;
			return get_texture(sprite_id);
		}
	}
	
	public TextureRegion get_texture(String name) {
		if(textureNameMap.containsKey(name))
		return atlasTextures.get(textureNameMap.get(name).get_tex_id());
		else return atlasTextures.get(0);
	}
	
	public TextureRegion get_texture(int tex_id) {
		return atlasTextures.get(tex_id);
	}
	
	public void dispose() {
		spriteAtlas.dispose();
		textureAtlas.dispose();
		spriteAnimations = null;
		atlasTextures.clear();
	}
	
}
