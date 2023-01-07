package render;

import basic.Rectangle;
import basic.Vector;
import render.basic.Sprite;
import resource.TextureIDManager;
import util.ConfigManager;

public class SpriteHandler extends RenderEntityHandler<Sprite> {
	public SpriteRenderer spriteRenderer[];
//	TextureIDManager TexIDManage;
	
	
	public class SpriteRenderer extends RenderEntityHandler<Sprite> {

		int id;
		protected SpriteRenderer(RenderEntityMainHandler RendEntityMainHandle) {
			super(RendEntityMainHandle);
		}

		@Override
		public void renderer_init(ConfigManager ConfigManage) {
			
		}
		
		@Override
		public Sprite add_resource_object(Vector pos, Rectangle bound, String params) {
			Sprite sprite = super.add_resource_object(pos, bound, params);
			sprite.set_sprite_type(RendEntMainHandle.TexIDManage.get_sprite_id(params));
			return sprite;
		}

		@Override
		public Sprite create_new_element() {
			return new Sprite();
		}
		
		@Override
		public void dispose() {
			
		}
	}

	
	protected SpriteHandler(RenderEntityMainHandler RendEntityMainHandle)
	{
		super(RendEntityMainHandle);
//		this.TexManage = RendHandle.TexManage;
		
//		this.TexIDManage = RendEntityMainHandle.TexIDManage;
		spriteRenderer = new SpriteRenderer[3];
		for(int i = 0; i < 3; i++) {
			spriteRenderer[i] = new SpriteRenderer(RendEntityMainHandle);
			spriteRenderer[i].id = i;
		}
	}
	
	@Override
	public void renderer_init(ConfigManager ConfigHandle) {
	}
	
	
	@Override
	public Sprite create_new_element() {
		return new Sprite();
	}
	
	@Override
	public Sprite add_resource_object(Vector pos, Rectangle bound, String params) {
		int sprite_id = this.RendEntMainHandle.TexIDManage.get_sprite_id(params);
		int typeid = this.RendEntMainHandle.TexIDManage.get_sprite_typeid(sprite_id);
		return spriteRenderer[typeid].add_resource_object(pos,bound,params);
	}
	
	@Override
	public void cleanup() {
		super.cleanup();
		for(int i=0; i < 3;i++) {
			spriteRenderer[i].cleanup();
		}
	}
	
	@Override
	public void dispose() {
		
	}

}



