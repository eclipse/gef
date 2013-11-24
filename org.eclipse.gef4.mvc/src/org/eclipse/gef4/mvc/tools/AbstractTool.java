package org.eclipse.gef4.mvc.tools;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef4.mvc.domain.IEditDomain;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

public abstract class AbstractTool<V> implements ITool<V> {

	private IEditDomain<V> domain;
	private boolean isActive;

	@Override
	public void setDomain(IEditDomain<V> domain) {
		this.domain = domain;
	}

	@Override
	public IEditDomain<V> getDomain() {
		return domain;
	}

	@Override
	public void activate() {
		this.isActive = true;
	}

	@Override
	public void deactivate() {
		this.isActive = false;
	}

	protected boolean isActive() {
		return isActive;
	}
}
