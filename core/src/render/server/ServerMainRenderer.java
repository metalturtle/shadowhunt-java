package render.server;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import render.MainRenderer;
import render.RenderEntityMainHandler;
import render.Renderer;
import util.ConfigManager;

public class ServerMainRenderer extends MainRenderer<ServerTextureResourceManager,Renderer> {

	public ServerMainRenderer() {
		super();
	}
	
	@Override
	public void load(ConfigManager ConfigManage, JSONObject levelFile) throws Exception {
		JSONArray textureArr = (JSONArray)levelFile.get("texture_demand");
		String tex_dem[] = new String[textureArr.size()];
		int i=0;
		for(; i < textureArr.size(); i++) {
			tex_dem[i] = (String) textureArr.get(i);
		}
		this.TexResourceManage = this.create_texture_resource_manager();
		this.TexResourceManage.load(ConfigManage, tex_dem);
		this.TexIDManage = this.TexResourceManage.get_texture_id_manager();
		System.out.println("texidmanage:"+TexIDManage);
		this.RenderEntityMainHandle = new RenderEntityMainHandler(this.TexIDManage);
	}

	@Override
	public void init() {
	}

	@Override
	public void render() {
	}

	@Override
	public ServerTextureResourceManager create_texture_resource_manager() {
		return new ServerTextureResourceManager();
	}
	
	@Override
	public void dispose() {
		
	}

}
