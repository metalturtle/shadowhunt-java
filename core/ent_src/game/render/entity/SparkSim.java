package game.render.entity;

import java.util.Random;

import basic.Rectangle;
import basic.Vector;
import render.basic.RenderEntity;
import util.GameInfo;

public class SparkSim extends RenderEntity {
	public float x[],y[],ang[],a[],w[],h[],pspeed[];
	int seconds[];
	float particle_size = 6;
	int limit;
	Random rand;
	public boolean INIT_DONE=false;
	public SparkSim(Vector pos,Rectangle bound) {
		super();
		this.pos.set(pos);
		this.bound.set(bound);
		rand = new Random();
		
		limit = 6;
		x = new float[limit];
		y = new float[limit];
		ang = new float[limit];
		a = new float[limit];
		w = new float[limit];
		h = new float[limit];
		pspeed = new float[limit];

	}
	
	public int get_limit() {
		return limit;
	}

	void init() {
		Random rand = new Random();
		int angleStep = 120/ limit;
		float angleIter = -90+this.get_angle();
		for(int i = 0; i < limit; i++) {
			x[i] = pos.x();
			y[i] = pos.y();
			w[i] = 0.5f;
			ang[i] = angleIter;
			a[i]=255;
			float val = ((float)rand.nextInt(angleStep*1000))/1000.0f;
			angleIter += val;
			h[i] = (((float)rand.nextInt(450))/100.0f);
//			h[i] = 10f;
			pspeed[i] = (0.14f+ ((float)rand.nextInt(100))/100.0f)*40f;
		}
	}

	void calculate(int i) {	
		x[i] -= pspeed[i]*(float)Math.sin(Math.toRadians(ang[i]))*GameInfo.get_delta_time();
		y[i] += pspeed[i]*(float)Math.cos(Math.toRadians(ang[i]))*GameInfo.get_delta_time();
		a[i] -= 10;
	}
	
	public void run() {
		if(!INIT_DONE) {
			INIT_DONE=true;
			init();
		}
		for(int i = 0; i < limit; i++) {
			calculate(i);
		}
	}
}