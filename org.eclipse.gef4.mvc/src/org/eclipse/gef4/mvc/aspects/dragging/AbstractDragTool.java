package org.eclipse.gef4.mvc.aspects.dragging;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.parts.IEditPart;
import org.eclipse.gef4.mvc.tools.AbstractTool;

public abstract class AbstractDragTool<V> extends AbstractTool<V> {

	@SuppressWarnings("unchecked")
	protected IDragPolicy<V> getDragPolicy(IEditPart<V> editPart) {
		return editPart.getEditPolicy(IDragPolicy.class);
	}
	
	public void initDrag(IEditPart<V> editPart, Point position){
		getDragPolicy(editPart).initDrag(position);
	}

	public void commitDrag(IEditPart<V> editPart, Point position){
		getDragPolicy(editPart).commitDrag(position);	
	}
}
