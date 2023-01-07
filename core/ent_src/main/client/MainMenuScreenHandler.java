package main.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import basic.InputState;
import basic.Rectangle;
import handlers.basic.Timer;
import menu.GUI;
import menu.Screen;
import menu.ScreenHandler;
import render.RenderInfo;
import util.ConfigManager;

public class MainMenuScreenHandler extends ScreenHandler {

	TextureRegion backTexReg;
	Texture backgroundTex;
	
	GdxGame gdxGame;
	GUI gtconstat;
	GUI gbconback;
	
	public final int CELL_SIZE,Y_CELL_COUNT;
	
	public MainMenuScreenHandler(GdxGame gdxGame) {
		super();
		CELL_SIZE = RenderInfo.get_screen_width()/100;
		Y_CELL_COUNT = RenderInfo.get_screen_height()/CELL_SIZE;
		this.gdxGame = gdxGame;
		backTexReg = new TextureRegion(new Texture(Gdx.files.internal("levels\\gui\\buttons\\back.png")));
		backgroundTex = new Texture(Gdx.files.internal("levels\\gui\\start_menu_background.png"));
		
		Rectangle roption1 = new Rectangle(1,7,15,5);
		Rectangle roption2 = new Rectangle(1,2,15,5);
		Rectangle rcenter = new Rectangle(50,Y_CELL_COUNT/2,1,0);
		Rectangle rdesc = new Rectangle(1,14,1.5f,0);
		
//		Rectangle option1 = new Rectangle(.05f,.1f,.1f,.05f);
//		Rectangle option2 = new Rectangle(.05f,.2f,.1f,.05f);
//		Rectangle center = new Rectangle(.5f,.5f,.6f,.2f);
		
		GUI gbstart = new GUI("BSTART",roption1,GUI.GUI_TYPE.IMAGE);
		GUI gbquit = new GUI("BQUIT",roption2,GUI.GUI_TYPE.IMAGE);
		GUI gbconnect = new GUI("BCONNECT",roption1,GUI.GUI_TYPE.IMAGE);
		GUI gbback = new GUI("BBACK",roption2,GUI.GUI_TYPE.IMAGE);
		GUI gtconfirm = new GUI("BQUIT_CONFIRM",rdesc,GUI.GUI_TYPE.TEXT);
		GUI gbyes = new GUI("BYES",roption1,GUI.GUI_TYPE.IMAGE);
		GUI gbno = new GUI("BNO",roption2,GUI.GUI_TYPE.IMAGE);
		gbconback = new GUI("BBACK",roption2,GUI.GUI_TYPE.IMAGE);
		gtconstat = new GUI("BSTATUS",new Rectangle(rcenter.x(),rcenter.y(),rcenter.w(),0),GUI.GUI_TYPE.TEXT);
		gtconstat.set_text("start");
		
		this.add_image("start", new TextureRegion(new Texture(Gdx.files.internal("levels\\gui\\buttons\\start.png"))));
		this.add_image("quit", new TextureRegion(new Texture(Gdx.files.internal("levels\\gui\\buttons\\quit.png"))));
		this.add_image("join", new TextureRegion(new Texture(Gdx.files.internal("levels\\gui\\buttons\\join.png"))));
		this.add_image("back", new TextureRegion(new Texture(Gdx.files.internal("levels\\gui\\buttons\\back.png"))));
		this.add_image("yes", new TextureRegion(new Texture(Gdx.files.internal("levels\\gui\\buttons\\yes.png"))));
		this.add_image("no", new TextureRegion(new Texture(Gdx.files.internal("levels\\gui\\buttons\\no.png"))));
		
		gbstart.set_text("start");
		gbquit.set_text("quit");
		gbconnect.set_text("join");
		gbback.set_text("back");
		gbyes.set_text("yes");
		gbno.set_text("no");
		gbconback.set_text("back");
		gbconback.set_visible(false);
		
//		new TextureRegion(new Texture(Gdx.files.internal("levels\\gui\\buttons\\quit_confirm.png")))
		
		gtconfirm.set_text("QUIT THE GAME?");
		gtconfirm.set_clickable_flag(false);
		
		Screen mainMenu = new Screen(this,"START");
		mainMenu.add_gui(gbstart);
		mainMenu.add_gui(gbquit);
		
		
		Screen connectMenu = new Screen(this,"CONNECT");
		connectMenu.add_gui(gbconnect);
		connectMenu.add_gui(gbback);
		
		Screen quitMenu = new Screen(this,"QUIT");
		quitMenu.add_gui(gtconfirm);
		quitMenu.add_gui(gbyes);
		quitMenu.add_gui(gbno);
		
		Screen conStatMenu = new Screen(this,"CONSTAT");
		conStatMenu.add_gui(gtconstat);
		conStatMenu.add_gui(gbconback);
		
		Screen ingameMenu = new Screen(this,"INGAME");
		ingameMenu.add_gui(gbback);
		
		add_screen(mainMenu);
		add_screen(connectMenu);
		add_screen(quitMenu);
		add_screen(conStatMenu);
		add_screen(ingameMenu);
		
		set_active_screen("START");
		
//		for(int i = ' '; i <= '~'; i++) {
//			System.out.print((char)i);
//		}
		System.out.println("\n newline char"+(((int)' '))+" "+(((int)'!')));
		
	}

	@Override
	public void handle_gui() {
		String screen_name = get_active_screen().get_name();
		if(screen_name.equals("START")) {
			handle_mainmenu();
		}
		if(screen_name.equals("CONNECT")) {
			handle_connectmenu();
		}
		if(screen_name.equals("QUIT")) {
			handle_quitmenu();
		}
		if(screen_name.equals("CONSTAT")) {
			System.out.println("constat");
			handle_constatmenu();
		}
		if(screen_name.equals("INGAME")) {
			handle_ingamemenu();
		}
	}
	
	void handle_mainmenu() {
		String name = get_active_screen().get_hover_gui().get_name();
		if(name.equals("BSTART")) {
			this.set_active_screen("CONNECT");
		}
		if(name.equals("BQUIT")) {
			this.set_active_screen("QUIT");
		}
	}
	
	void handle_connectmenu() {
		String name = get_active_screen().get_hover_gui().get_name();
		if(name.equals("BCONNECT")) {
			System.out.println("connect is clicked");
			this.set_active_screen("CONSTAT");
			gdxGame.create_game();
			gbconback.set_visible(false);
		}
		if(name.equals("BBACK")) {
			this.set_active_screen("START");
		}
	}
	
	void handle_quitmenu() {
		String name = get_active_screen().get_hover_gui().get_name();
		if(name.equals("BYES")) {
			gdxGame.close();
		}
		if(name.equals("BNO")) {
			this.set_active_screen("START");
		}
	}
	
	void handle_constatmenu() {
//		gtconstat.set_text(gdxGame.game.Coordinate.loader.get_load_message());
		String name = get_active_screen().get_hover_gui().get_name();
		if(name.equals("BBACK")) {
			this.set_active_screen("CONNECT");
		}
	}
	
	void handle_ingamemenu() {
		String name = get_active_screen().get_hover_gui().get_name();
		if(name.equals("BBACK")) {
			this.set_active_screen("CONNECT");
			gdxGame.close_game();
		}
	}
	
	boolean pressed = false;
	@Override
	public void render() {
		if(get_active_screen().get_name().equals("START") || get_active_screen().get_name().equals("QUIT")) {
			spriteBatch.begin();
			spriteBatch.draw(backgroundTex,0,0,RenderInfo.get_screen_width(),RenderInfo.get_screen_height());
			spriteBatch.end();
		}
		if(get_active_screen().get_name().equals("CONSTAT")) {
			if(gdxGame.game.Coordinate != null) {
				if(gdxGame.game.Coordinate.loader.is_ready()) {
					gdxGame.set_state(GdxGame.DISPLAY_STATE.GAME);
					this.set_active_screen("");
				}
				else if(gdxGame.game.Coordinate.loader.is_error()) {
					gtconstat.set_text(gdxGame.game.get_error_message());
					gbconback.set_visible(true);
				}
				else if(gdxGame.game.get_error_message().length() > 0) {
					gtconstat.set_text(gdxGame.game.get_error_message());
					gbconback.set_visible(true);
				}
				else {
					gtconstat.set_text(gdxGame.game.Coordinate.loader.get_load_message());
				}
			}
		}
		
//		if(get_active_screen().get_name().equals("")) {
//			if(InputState.keyboard['m']) {
//				if(!pressed) {
//					this.set_active_screen("INGAME");
//					pressed = true;
//				}
//			} else {
//				pressed = false;
//			}
//		} else if(get_active_screen().get_name().equals("INGAME")) {
//			if( InputState.keyboard['m']) {
//				if(!pressed) {
//					this.set_active_screen("");
//					pressed=true;
//				} else {
//					pressed = false;
//				}
//			}
//		}

		super.render();
	}
}
