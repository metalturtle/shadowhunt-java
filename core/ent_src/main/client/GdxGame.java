package main.client;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.mygdx.game.Game;

import Coordinator.Coordinator;
import Coordinator.HandlerInitRunner;
import client.ClientImageHandler;
import game.render.HidingSpriteHandler;
import game.render.SparkHandler;
import game.synced.ShadowActorHandler;
import gameflow.ShadowGameFlowHandler;
import gdxgraphics.GdxHidingSpriteHandler;
import gdxgraphics.GdxMainRenderer;
import gdxgraphics.GdxSparkRenderer;
import render.RenderEntityMainHandler;
import simulation.SimulationFlowHandler;
import simulation.SimulationHandler;
import simulation.SimulationCoordinator;
import util.ConfigManager;


public class GdxGame extends ApplicationAdapter implements InputProcessor {

	public Game game;
	boolean mpressed,mreleased;
	
    static {
        GdxNativesLoader.load();
    }
    ConfigManager ConfigManage;
    GdxMainRenderer GdxMRenderer;
    MainMenuScreenHandler MenuHandle;
    HandlerInitRunner HandleInitRunner;
    byte STATE;
    
    static class DISPLAY_STATE {
    	static final byte MENU=0,GAME=1;
    	
    }
    
    public GdxGame(ConfigManager ConfigManage, String[] args) {
    	this.ConfigManage = ConfigManage;
    }

   
	@Override
	public void create() {
		
		HandleInitRunner = new HandlerInitRunner<GdxMainRenderer>() {

			@Override
			public void add_renderers( GdxMainRenderer MRenderer) {
				MRenderer.add_renderer(new GdxHidingSpriteHandler(MRenderer,null), "HidingSpriteHandler");
				MRenderer.add_renderer(new GdxSparkRenderer(MRenderer,null), "SparkHandler");
				
			}

			@Override
			public void add_handlers(SimulationCoordinator SimCoord) {
				SimCoord.add_entity_handler(new ShadowActorHandler(SimCoord), "ShadowActorHandler");
			}

			@Override
			public SimulationFlowHandler add_simflow_handler(SimulationCoordinator SimCoord, ClientImageHandler CIHandle) {
				return new ShadowGameFlowHandler(SimCoord,CIHandle);
			}

			@Override
			public void add_render_entity_handlers(RenderEntityMainHandler RendEntMainHandle) {
				HidingSpriteHandler HSH = new HidingSpriteHandler(RendEntMainHandle);
				RendEntMainHandle.add_handler("HidingSpriteHandler", HSH);
				RendEntMainHandle.add_handler("SparkHandler", new SparkHandler(RendEntMainHandle));
			}
		};
		
    	MenuHandle = new MainMenuScreenHandler(this);
		Gdx.input.setInputProcessor(this);
	}
	
	protected void create_game() {
		GdxMRenderer = new GdxMainRenderer();
    	game = new Game(ConfigManage, GdxMRenderer,HandleInitRunner,new String[] {"UP","DOWN","LEFT","RIGHT",
    			"CHANGE_UP","CHANGE_DOWN","SHOOT"},new char[] {'w','s','a','d','n','m','t'});
		game.create();
		game.start_coordinator();
	}
	
	protected void close_game() {
		game.dispose_coordinator(new Exception("Closing game."));
		game.dispose();
		set_state(GdxGame.DISPLAY_STATE.MENU);
		GdxMRenderer.set_ready(false);
		GdxMRenderer.dispose();
		GdxMRenderer = null;
		game = null;
		System.gc();
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0f,0,0,0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		float ax = Gdx.input.getX()-Gdx.graphics.getWidth()/2;
		float ay = (Gdx.graphics.getHeight()/2-Gdx.input.getY());
		
		if(game != null) {
			game.set_mouse(ax,ay);
			game.run_client();
		}
		
		if(STATE == DISPLAY_STATE.GAME) {
			if(GdxMRenderer.is_ready()) {
				GdxMRenderer.render();
			}
		}
		
//		if(STATE==DISPLAY_STATE.MENU) 
		{
			if(!(mpressed&&mreleased)) {
				MenuHandle.mouse_hovered(Gdx.input.getX(), Gdx.graphics.getHeight()-Gdx.input.getY());
			}
			if(mpressed) {
				mpressed = false;
				MenuHandle.mouse_pressed(Gdx.input.getX(), Gdx.graphics.getHeight()-Gdx.input.getY());
			}
			if(mreleased) {
				mreleased = false;
				MenuHandle.mouse_released(Gdx.input.getX(), Gdx.graphics.getHeight()-Gdx.input.getY());
			}
			MenuHandle.render();
		}
	}
	
	public void set_state(byte state) {
		this.STATE = state;
	}

	@Override
	public void dispose() {
		if(game != null)
			game.dispose();
	}

	@Override
	public void pause() {
		System.out.println("paused");
	}

	@Override
	public void resize(int x, int y) {
		if(this.STATE == DISPLAY_STATE.GAME) {
			game.resize(x,y);
		}
		
		MenuHandle.resize(x,y);
	}

	@Override
	public void resume() {
		if(this.STATE == DISPLAY_STATE.GAME)
			game.resume();
	}

	@Override
	public boolean keyDown(int e) {
		if(this.STATE == DISPLAY_STATE.GAME) {
			char c = Input.Keys.toString(e).charAt(0);
			game.keyDown(c);
		}

		return true;
	}

	@Override
	public boolean keyTyped(char e) {
		return false;
	}

	@Override
	public boolean keyUp(int e) {
		if(this.STATE == DISPLAY_STATE.GAME) {
			char c = Input.Keys.toString(e).charAt(0);
			game.keyUp(c);
		}
		return true;
	}

	@Override
	public boolean mouseMoved(int x, int y) {
		return false;
	}

	@Override
	public boolean touchDown(int x, int y, int arg2, int arg3) {
		mpressed = true;
		return true;
	}

	@Override
	public boolean touchDragged(int arg0, int arg1, int arg2) {
		return false;
	}

	@Override
	public boolean touchUp(int x, int y, int arg2, int arg3) {
		mreleased = true;
		return true;
	}

	public boolean scrolled(float amountX, float amountY) {
		return false;
	}

	public void close() {
		Gdx.app.exit();
		System.exit(0);
	}
}
