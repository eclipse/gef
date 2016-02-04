package org.eclipse.gef4.mvc.tests.stubs;

import org.eclipse.gef4.mvc.parts.AbstractRootPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

public class RootPart extends AbstractRootPart<Object, Object> {
	@Override
	protected void addChildVisual(IVisualPart<Object, ? extends Object> child, int index) {
	}

	@Override
	protected Object createVisual() {
		return this;
	}

	@Override
	protected void doRefreshVisual(Object visual) {
	}

	@Override
	protected void removeChildVisual(IVisualPart<Object, ? extends Object> child, int index) {
	}
}