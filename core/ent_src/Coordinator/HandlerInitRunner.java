package Coordinator;

import client.ClientImageHandler;
import render.MainRenderer;
import render.RenderEntityMainHandler;
import simulation.SimulationCoordinator;
import simulation.SimulationFlowHandler;
import simulation.SimulationHandler;

public abstract class HandlerInitRunner<T extends MainRenderer> {
	
	abstract public void add_handlers(SimulationCoordinator SimCoord);

	abstract public void add_renderers(T MRenderer);
	
	abstract public void add_render_entity_handlers(RenderEntityMainHandler RendEntMainHandle);
	
	abstract public SimulationFlowHandler add_simflow_handler(SimulationCoordinator SimCoord,ClientImageHandler CIHandle);
}
