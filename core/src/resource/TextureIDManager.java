package resource;

import java.util.HashMap;
import java.util.Set;
import util.ConfigManager;

public class TextureIDManager {

	public int TYPEID_SPRITE,TYPEID_ANIMATED_SPRITE;

	HashMap <String, TextureNameMap> spriteMap = new HashMap<String,TextureNameMap>();
	HashMap <String,TextureNameMap> textureNameMap = new HashMap<String,TextureNameMap>();
	
	void set_maps(HashMap <String, TextureNameMap> spriteMap, HashMap <String,TextureNameMap> textureNameMap,int TYPEID_SPRITE,int TYPEID_ANIMATED_SPRITE) {
		this.spriteMap = spriteMap;
		this.textureNameMap = textureNameMap;
		this.TYPEID_SPRITE = TYPEID_SPRITE;
		this.TYPEID_ANIMATED_SPRITE = TYPEID_ANIMATED_SPRITE;
	}

	
	public int get_sprite_id(String name) {
		try {
			if(spriteMap.containsKey(name))
				return spriteMap.get(name).get_tex_id();
			else {
				return get_texture_id(name)+TYPEID_ANIMATED_SPRITE;
			}
		} catch(Exception e) {
			System.out.println("texture does not exist: "+name);
			throw e;
		}
	}
	
	public int get_sprite_typeid(int sprite_type) {
		if(sprite_type > TYPEID_ANIMATED_SPRITE) {
			return 2;
		} else if (sprite_type > TYPEID_SPRITE) {
			return 1;
		} else {
			return 0;
		}
	}
	
	public int get_texture_id (String name) {
		if(textureNameMap.containsKey(name))
		return textureNameMap.get(name).get_tex_id();
		else return 0;
	}
}
