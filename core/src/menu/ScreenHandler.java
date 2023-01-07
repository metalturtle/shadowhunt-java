package menu;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import render.RenderInfo;

public abstract class ScreenHandler {

	protected HashMap <String,Screen> screens = new HashMap<String,Screen>();
	
	protected SpriteBatch spriteBatch;
	protected OrthographicCamera cam;
	protected Viewport viewport;
	protected Texture startTex,bgtex;
	
	private Screen activeScreen;
	protected GUITextHandler guiText;
	HashMap <String, TextureRegion> imageNameMap = new HashMap <String,TextureRegion>();
//	
//	public static int CELL_SIZE,Y_CELL_COUNT;
//	
	
	public ScreenHandler() {
//		float aspect = RenderInfo.get_screen_width()/RenderInfo.get_screen_height();
		spriteBatch = new SpriteBatch();
		
		cam = new OrthographicCamera();
		cam.setToOrtho(false,RenderInfo.get_screen_width(),RenderInfo.get_screen_height());
		cam.update();
		
		viewport = new FillViewport(RenderInfo.get_screen_width(),RenderInfo.get_screen_height(), cam);
		viewport.apply();

		spriteBatch.setProjectionMatrix(cam.combined);
		bgtex = new Texture(Gdx.files.internal("levels\\gui\\buttons\\bkg_on.png"));
		
		guiText = new GUITextHandler();
		screens.put("",new Screen(this,""));
	}
	
	public void add_screen(Screen screen) {
		screens.put(screen.name,screen);
	}
	
	public Screen get_active_screen() {
		return activeScreen;
	}
	
	public void set_active_screen(String name) {
		this.activeScreen = screens.get(name);
	}
	
	public void render() {
		render(spriteBatch);
	}
	
	public void render(SpriteBatch spriteBatch) {
		int cell_size=20,thick=5;
		boolean display_grid=false;
		
//		System.out.println("size:"+(RenderInfo.get_screen_width()/cell_size)+" "+(RenderInfo.get_screen_height()/cell_size));
		
		if(display_grid) {
			spriteBatch.begin();
			for(int i = 0; i < RenderInfo.get_screen_width(); i ++) {
				spriteBatch.draw(bgtex,i*cell_size,0,thick,RenderInfo.get_screen_height());
			}
			for(int i = 0; i < RenderInfo.get_screen_height(); i ++) {
				spriteBatch.draw(bgtex,0,i*cell_size,RenderInfo.get_screen_width(),thick);
			}
			spriteBatch.end();
		}
		
		if(activeScreen != null) {
			activeScreen.render(spriteBatch);
		}
	}
	
	public void add_image(String name, TextureRegion texreg) {
		imageNameMap.put(name,texreg);
	}
	
	
	
	public void mouse_hovered(int x, int y) {
		if(activeScreen != null) {
			activeScreen.hover(x,y);
		}
	}
	
	public void mouse_pressed(int x,int y) {
		if(activeScreen != null) {
			activeScreen.pressed(x,y);
		}
	}
	
	public void mouse_released(int x, int y) {
		if(activeScreen != null) {
			activeScreen.released(x,y);
			if(activeScreen.hoverGUI != null) {
				handle_gui();
			}
		}
	}
	
	public void resize(int x, int y) {
		viewport.update(x, y);
		viewport.apply();
		cam.update();
	}
	
	public abstract void handle_gui();
}
