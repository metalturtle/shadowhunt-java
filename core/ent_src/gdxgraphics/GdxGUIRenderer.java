package gdxgraphics;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import basic.Rectangle;
import menu.GUI;
import menu.Screen;
import menu.ScreenHandler;
import render.GameCamera;
import render.RenderInfo;
public class GdxGUIRenderer extends ScreenHandler {

//	BitmapFont font;
//	FreeTypeFontGenerator generator;
//	FreeTypeFontGenerator.FreeTypeFontParameter parameter;
	Screen screen;
	GdxMainRenderer GdxMRenderer;
	TextureRegion backgroundTex;
	GdxGUIRenderer(GdxMainRenderer GdxMRenderer) {
		super();
		this.GdxMRenderer = GdxMRenderer;
		screen = new Screen(this,"game",GdxMRenderer.RenderEntityMainHandle.GGUICollection);
		this.add_screen(screen);
		backgroundTex = new TextureRegion(new Texture(Gdx.files.internal("levels\\gui\\start_menu_background.png")));
		set_active_screen("game");
//		Rectangle rdesc = new Rectangle(1,14,1.5f,0);
//		screen.add_gui(new GUI("testing",rdesc,GUI.GUI_TYPE.TEXT));
//		generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts\\cute_letters\\Cute Letters.ttf"));
//		parameter  = new FreeTypeFontGenerator.FreeTypeFontParameter();
//		font = generator.generateFont(parameter);
	}

//	@Override
//	public void render_entity(GUIElement rend_ent, SpriteBatch spriteBatch) {
//		// TODO Auto-generated method stub
//		
//	}
//	
//	@Override
//	public void render_loop(OrthographicCamera screenCamera, OrthographicCamera worldCamera, GameCamera camView, SpriteBatch spriteBatch) {
//		for(Iterator <GUIElement> it = RendEntHandle.get_iterator(null); it.hasNext();) {
//			GUIElement gui = it.next();
//			font.draw(spriteBatch,gui.get_text(),gui.pos.x() , gui.pos.y());
////			System.out.println("rendering gui: "+gui.get_text());
////			render_entity(sprite,spriteBatch);
//		}
//	}
	
	
//	@Override
	public void dispose() {
//		font.dispose();
//		generator.dispose();
	}

	@Override
	public void handle_gui() {
		// TODO Auto-generated method stub
		
	}
}
