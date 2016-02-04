package org.eclipse.gef4.mvc.tests.stubs;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.gef4.mvc.behaviors.IBehavior;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IHandlePartFactory;
import org.eclipse.gef4.mvc.parts.IVisualPart;

public class HandlePartFactory implements IHandlePartFactory<Object> {
	@Override
	public List<IHandlePart<Object, ? extends Object>> createHandleParts(
			List<? extends IVisualPart<Object, ? extends Object>> targets, IBehavior<Object> contextBehavior,
			Map<Object, Object> contextMap) {
		return Collections.emptyList();
	}
}