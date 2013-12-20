package org.eclipse.gef4.mvc.tools;

import org.eclipse.gef4.mvc.domain.IEditDomain;

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

	@Override
	public boolean isActive() {
		return isActive;
	}
	
}
