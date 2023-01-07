package resource;

import java.util.HashMap;

import util.ConfigManager;

public abstract class TextureResourceManager {

	public int TYPEID_SPRITE,TYPEID_ANIMATED_SPRITE;
	
	TextureIDManager TexIDManage;
	
	protected HashMap <String, TextureNameMap> spriteMap = new HashMap<String,TextureNameMap>();
	protected HashMap <String,TextureNameMap> textureNameMap = new HashMap<String,TextureNameMap>();
	
	protected TextureResourceManager() {
		this.TexIDManage = new TextureIDManager();
	}
	
	public TextureIDManager get_texture_id_manager() {
		return TexIDManage;
	}
	
	abstract protected void load_sprites(ConfigManager ConfigManage);
	
	abstract protected void load_textures(ConfigManager ConfigManage,String[] texture_demand);
	
	public void load(ConfigManager ConfigManage, String texture_demand[]) {
		load_sprites(ConfigManage);
		load_textures(ConfigManage,texture_demand);
		TexIDManage.set_maps(spriteMap, textureNameMap, TYPEID_SPRITE,TYPEID_ANIMATED_SPRITE);
	}
}
