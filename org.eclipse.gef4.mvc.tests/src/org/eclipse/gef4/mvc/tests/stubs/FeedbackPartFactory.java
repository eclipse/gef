package org.eclipse.gef4.mvc.tests.stubs;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.gef4.mvc.behaviors.IBehavior;
import org.eclipse.gef4.mvc.parts.IFeedbackPart;
import org.eclipse.gef4.mvc.parts.IFeedbackPartFactory;
import org.eclipse.gef4.mvc.parts.IVisualPart;

public class FeedbackPartFactory implements IFeedbackPartFactory<Object> {
	@Override
	public List<IFeedbackPart<Object, ? extends Object>> createFeedbackParts(
			List<? extends IVisualPart<Object, ? extends Object>> targets, IBehavior<Object> contextBehavior,
			Map<Object, Object> contextMap) {
		return Collections.emptyList();
	}
}