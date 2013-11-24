package org.eclipse.gef4.mvc.parts;

import java.util.List;

public interface IHandlePartFactory<V> {

	public List<IHandlePart<V>> createHandleParts(
			List<IContentPart<V>> selection);
}
