package gdxgraphics;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import menu.GUITextHandler;
import simulation.ChatHandler;
public class GdxChatRenderer  {

	GUITextHandler GUITextHandle;
	ChatHandler ChatHandle;
	
	GdxChatRenderer(GdxMainRenderer MRenderer) {
		GUITextHandle = new GUITextHandler();
	}
	
	void render(SpriteBatch spriteBatch) {
		
	}
}
