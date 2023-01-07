package com.mygdx.game;
import Coordinator.Coordinator;
import Coordinator.HandlerInitRunner;
import client.ClientImageHandler;
import game.render.HidingSpriteHandler;
import game.render.SparkHandler;
import game.synced.ShadowActorHandler;
import gameflow.ShadowGameFlowHandler;
import gdxgraphics.GdxHidingSpriteHandler;
import gdxgraphics.GdxMainRenderer;
import render.RenderEntityMainHandler;
import render.server.ServerMainRenderer;
import render.server.ServerTextureResourceManager;
import simulation.SimulationFlowHandler;
import simulation.SimulationHandler;
import simulation.SimulationCoordinator;
import util.ConfigManager;


class RunningAvg {
    private final long[] slots;
    private int offset;
     
    private static final long DAMPEN_THRESHOLD = 10 * 1000L * 1000L; // 10ms
    private static final float DAMPEN_FACTOR = 0.9f; // don't change: 0.9f is exactly right!

    public RunningAvg(int slotCount) {
        this.slots = new long[slotCount];
        this.offset = 0;
    }

    public void init(long value) {
        while (this.offset < this.slots.length) {
            this.slots[this.offset++] = value;
        }
    }

    public void add(long value) {
        this.slots[this.offset++ % this.slots.length] = value;
        this.offset %= this.slots.length;
    }

    public long avg() {
        long sum = 0;
        for (int i = 0; i < this.slots.length; i++) {
            sum += this.slots[i];
        }
        return sum / this.slots.length;
    }
     
    public void dampenForLowResTicker() {
        if (this.avg() > DAMPEN_THRESHOLD) {
            for (int i = 0; i < this.slots.length; i++) {
                this.slots[i] *= DAMPEN_FACTOR;
            }
        }
    }
}

public class ServerGame{
	Game game;
    long timestamp;
    final long interval = 16;
    ServerTextureResourceManager ServTexResManage;
    
    public ServerGame(ConfigManager ConfigManage, String[] args) {
    	ServerMainRenderer ServMRenderer = new ServerMainRenderer();
    	
    	HandlerInitRunner HandleInitRunner = new HandlerInitRunner<GdxMainRenderer>() {

			@Override
			public void add_renderers( GdxMainRenderer MRenderer) {
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
		
    	game = new Game(ConfigManage,ServMRenderer,HandleInitRunner,new String[] {"UP","DOWN","LEFT","RIGHT",
    			"CHANGE_UP","CHANGE_DOWN","SHOOT"},new char[] {'w','s','a','d','n','m','t'});
    	game.start_coordinator();
    	run();
    }

    private static final long NANOS_IN_SECOND = 1000L * 1000L * 1000L;
    
    private long nextFrame = 0;

    private RunningAvg sleepDurations = new RunningAvg(10);
    private RunningAvg yieldDurations = new RunningAvg(10);
    
	public void run() {
		while(game.run_server()) {
			 sync(60);
		}
	}
	
	public void set_input_processor() {
		
	}
	
    public void sync(int fps) {
        try {
            // sleep until the average sleep time is greater than the time remaining till nextFrame
            for (long t0 = getTime(), t1; (nextFrame - t0) > sleepDurations.avg(); t0 = t1) {
                Thread.sleep(1);
                sleepDurations.add((t1 = getTime()) - t0); // update average sleep time
            }
     
            // slowly dampen sleep average if too high to avoid yielding too much
            sleepDurations.dampenForLowResTicker();
     
            // yield until the average yield time is greater than the time remaining till nextFrame
            for (long t0 = getTime(), t1; (nextFrame - t0) > yieldDurations.avg(); t0 = t1) {
                Thread.yield();
                yieldDurations.add((t1 = getTime()) - t0); // update average yield time
            }
        } catch (InterruptedException e) {
             
        }
         
        // schedule next frame, drop frame(s) if already too late for next frame
        nextFrame = Math.max(nextFrame + NANOS_IN_SECOND / fps, getTime());
    }

    
    long getTime() {
    	return System.nanoTime();
    }
}
