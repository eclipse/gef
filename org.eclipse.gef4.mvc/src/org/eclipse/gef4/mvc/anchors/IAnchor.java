package org.eclipse.gef4.mvc.anchors;

import java.util.List;

import org.eclipse.gef4.mvc.parts.IContentPart;

public interface IAnchor<V> {

	// TODO: maybe use IVisualPart instead of IContentPart
	
	IContentPart<V> getAnchoragePart();
	
	List<IContentPart<V>> getFixedParts();
}
