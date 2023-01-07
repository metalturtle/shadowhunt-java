package game.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import basic.Rectangle;
import render.GameCamera;
import render.RenderEntityMainHandler;
import render.RenderInfo;
import render.SpriteHandler;
import simulation.SimulationHandler;
import util.ConfigManager;

public class HidingSpriteHandler extends SpriteHandler {

	ShaderProgram spriteLightShader;
	float transparentFlag = 0f;
	public HidingSpriteHandler(RenderEntityMainHandler RendEntMainHandle) {
		super(RendEntMainHandle);
	}
	
	public void set_mode(boolean hide_team) {
		if(hide_team) {
			transparentFlag = 0.6f;
		} else {
			transparentFlag = 0f;
		}
	}
	
	public float get_mode() {
		return transparentFlag;
	}

}
