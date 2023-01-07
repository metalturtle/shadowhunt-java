package render;

import java.util.ArrayList;
import java.util.HashMap;

import menu.GUICollection;
import resource.TextureIDManager;

public class RenderEntityMainHandler {

	public TextureIDManager TexIDManage;
	public ArrayList <RenderEntityHandler> renderers = new ArrayList<RenderEntityHandler>();
	HashMap <String,Integer> rendererNameMap = new HashMap<String,Integer>();
 	public SpriteHandler SpriteHandle;
	public EffectHandler EffectHandle;
//	public GUIHandler GUIHandle;
	public GUICollection GGUICollection;
	public LightHandler LightHandle;
	public GameCamera GCamera;
	
	public RenderEntityMainHandler(TextureIDManager TexIDManage) {
		this.TexIDManage = TexIDManage;
		System.out.println("remh checking texidmanage: "+this.TexIDManage);
		this.GCamera = new GameCamera(200,200,RenderInfo.get_world_width(),RenderInfo.get_world_height());
		this.GCamera.set_zoom(1.5f);
		add_handler("SpriteHandler", SpriteHandle = new SpriteHandler(this));
		System.out.println("remh added sprite handler ");
		add_handler("EffectHandler", EffectHandle = new EffectHandler(this));
		GGUICollection = new GUICollection();
		this.LightHandle = new LightHandler();
	}
	
	
	///////////////////////////// Renderer//////////////////////////////////////////
	
	public boolean contains_handler(String name) {
		return rendererNameMap.containsKey(name);
	}
	
	public void add_handler(String name,RenderEntityHandler renderer) {
		name = name.toLowerCase();
		System.out.println("adding handler: "+name);
		renderer.name = name;
		rendererNameMap.put(name, renderers.size());
		renderers.add(renderer);
	}
	
	public RenderEntityHandler get_handler(String name) {
		name = name.toLowerCase();
		int id = rendererNameMap.get(name);
		return renderers.get(id);
	}
	
	public RenderEntityHandler get_handler(int id) {
		return renderers.get(id);
	}
	
	public void cleanup() {
		for(int i = 0; i < renderers.size(); i++) {
			renderers.get(i).cleanup();
		}
		LightHandle.cleanup();
	}
}
