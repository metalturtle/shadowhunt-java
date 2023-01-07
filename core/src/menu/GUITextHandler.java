package menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class GUITextHandler {

	Texture fontTex;
	TextureRegion letterTexReg[];
	int wsize,hsize;
	float asp;
	
	public GUITextHandler() {
		fontTex = new Texture(Gdx.files.internal("levels\\gui\\fonttest.png"));
		
		int gx = 32,gy = 3;
		wsize = fontTex.getWidth()/gx;
		hsize = fontTex.getHeight()/gy;
		asp = ((float)hsize)/((float)wsize);
		System.out.println("asp: "+asp);
		
		letterTexReg = new TextureRegion[96];
		for(int i = 0; i < gy; i++) {
			for(int j = 0; j < gx; j++) {
				letterTexReg[j+gx*i] = new TextureRegion(fontTex);
				letterTexReg[j+gx*i].setRegion(wsize*j,hsize*i,wsize,hsize);
			}

		}
		
		
//		letterTexReg[0].setRegion(33,1,47,77);
	}
	
	public void render(String text,SpriteBatch spriteBatch, float x, float y, float w) {
		int c=0;
		for(int i = 0; i < text.length();i++) {
			c = (char) (text.charAt(i) - ' ');
			spriteBatch.draw(letterTexReg[c],x+(w)*i,y,w,asp*w);
		}
	}
}
