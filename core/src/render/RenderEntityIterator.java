package render;

import java.util.ArrayList;

import basic.Rectangle;
import basic.Vector;
import basic.resource.WorldResArrayIterator;
import render.basic.RenderEntity;

public class RenderEntityIterator<T extends RenderEntity> extends WorldResArrayIterator<T> {

	float angle[],dist[];
	Vector diff,point;
	RenderEntityHandler renderer;
	Rectangle entViewRect;
	RenderEntityIterator(RenderEntityHandler renderer,ArrayList<T> worldResArray, Rectangle viewRect) {
		super(worldResArray, viewRect);
		this.renderer = renderer;
		angle = new float[4];
		dist = new float[4];
		diff = new Vector();
		point = new Vector();
		entViewRect = new Rectangle();
	}

	@Override
	public void find_next() {
		i+=1;
		
		while(i<worldResourceObjects.size()) {
			RenderEntity ent = worldResourceObjects.get(i);
			if(worldResourceObjects.get(i).check_enabled()) {
				if(viewRectangle==null) {
					break;
				}
				if(!worldResourceObjects.get(i).check_visible()) {
					i+=1;
					continue;
				}
				entViewRect.set(ent.get_view_rect());
				if(Rectangle.check_rect_intersection(viewRectangle,entViewRect)) {
					break;
				}
			}
			i+=1;
		}
	}
}
