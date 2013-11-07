package org.eclipse.gef4.mvc.parts;

import java.util.List;

public interface ICanvasEditPart<V> extends INodeEditPart<V> {

	List<IConnectionEditPart<V>> getConnectionChildren();
	
	void synchronizeConnectionChildren(); // getModelConnectionChildren();
}
