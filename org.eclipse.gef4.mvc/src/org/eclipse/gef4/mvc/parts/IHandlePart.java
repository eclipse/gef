package org.eclipse.gef4.mvc.parts;

import java.util.List;

import org.eclipse.gef4.mvc.tools.ITool;


// has graphical representation thus visual part
public interface IHandlePart<V> extends IVisualPart<V> {

	// handles are not linked to a single host part, but to the content part selection
	List<IContentPart<V>> getTargetContentParts();
	
	void setTargetContentParts(List<IContentPart<V>> targetContentParts);
	
	// handle is or creates a tool that can be activated on the edit domain
	ITool<V> getHandleTool();
}
