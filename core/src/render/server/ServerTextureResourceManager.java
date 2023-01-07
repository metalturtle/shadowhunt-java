package render.server;

import java.util.HashMap;
import java.util.Set;

import resource.TextureNameMap;
import resource.TextureResourceManager;
import util.ConfigManager;

public class ServerTextureResourceManager extends TextureResourceManager {

	int TYPEID_SPRITE,TYPEID_ANIMATED_SPRITE;
	
	public ServerTextureResourceManager() {
		super();
	}

	
	protected void load_sprites (ConfigManager ConfigManage) {
		String[] sprite_names = ConfigManage.get_keys("sprite", "sprite");
		String[] anim_sprite_names = ConfigManage.get_keys("sprite", "animated_sprite");
		int sprite_index = 0;
		
		for(String sprite_name:sprite_names) {
			if(sprite_name.equals("folder"))
				continue;
			spriteMap.put(sprite_name, new TextureNameMap((byte)0, sprite_index));
			sprite_index+=1;
		}
		TYPEID_SPRITE=sprite_index;

		for(String anim_sprite_name:anim_sprite_names) {
			if(anim_sprite_name.equals("folder"))
				continue;
			spriteMap.put(anim_sprite_name, new TextureNameMap((byte)1, sprite_index));
			sprite_index++;
		}
		
		TYPEID_ANIMATED_SPRITE = sprite_index;
	}
	
	
	
	protected void load_textures(ConfigManager ConfigManage, String texture_demand[]) {
		int i = 0;
		
		HashMap <Object,Object> basic_texture = ConfigManage.get_object("resource", "basic_texture");
		Set keySet = basic_texture.keySet();
		
		for(i=0;i<texture_demand.length;i++) {
			String file_name = texture_demand[i];
			String file_key = file_name.substring(0,file_name.indexOf("."));
			textureNameMap.put(file_key, new TextureNameMap((byte)0, i));
		}
		
		for(Object keyObj: keySet) {
			String key = (String)keyObj;
			if(key.equals("folder"))
				continue;
			
			textureNameMap.put(key, new TextureNameMap((byte)0, i));
			i+=1;
		}
	}
}
