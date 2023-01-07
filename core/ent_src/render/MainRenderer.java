package render;

import java.util.ArrayList;
import java.util.Comparator;

import org.json.simple.JSONObject;

import resource.TextureIDManager;
import resource.TextureResourceManager;
import util.ConfigManager;

public abstract class MainRenderer<T extends TextureResourceManager,R extends Renderer> {
	public TextureIDManager TexIDManage;
	public T TexResourceManage;
	public RenderEntityMainHandler RenderEntityMainHandle;
//	public GameCamera gameCamera;
	protected ArrayList <R> rendererList = new ArrayList<R>();
	private boolean IS_READY;
	
	public MainRenderer() {
//		this.gameCamera = new GameCamera(0,0,RenderInfo.WORLD_X,RenderInfo.WORLD_Y);
	}
	
	abstract public T create_texture_resource_manager();
	abstract public void load(ConfigManager ConfigManage, JSONObject levelFile) throws Exception;
	abstract public void init();
	abstract public void render();
	
	////////////////////////////////////////////////////////////////

	public void sort_renderer() {
		rendererList.sort(new Comparator<Renderer>() {
			@Override
			public int compare(Renderer o1, Renderer o2) {
				if(o1.get_render_level() > o2.get_render_level())
					return 1;
				if(o1.get_render_level() < o2.get_render_level())
					return -1;
				return 0;
			}
		});
	}
	
	public void add_renderer(R renderer,String reh_name) {
		renderer.init(RenderEntityMainHandle.get_handler(reh_name));
		rendererList.add(renderer);
	}
	
	public R get_renderer(int id) {
		return rendererList.get(id);
	}
	
	public void set_ready(boolean flag) {
		this.IS_READY = flag;
	}
	
	public boolean is_ready() {
		return this.IS_READY;
	}
	
	public abstract void dispose();
}
