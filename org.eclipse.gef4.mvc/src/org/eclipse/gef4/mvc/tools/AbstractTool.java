package org.eclipse.gef4.mvc.tools;

import org.eclipse.gef4.mvc.domain.IEditDomain;

public abstract class AbstractTool<V> implements ITool<V> {

	private IEditDomain<V> domain;
	private boolean isActive;

	@Override
	public void setDomain(IEditDomain<V> domain) {
		IEditDomain<V> oldDomain = getDomain();
		
		if (oldDomain != null && domain == null) {
			unregisterListeners();
		}
		
		this.domain = domain;
		
		if (oldDomain == null && domain != null) {
			registerListeners();
		}
	}
	

	/**
	 * This method is called when a valid {@link IEditDomain} is attached to
	 * this tool so that you can register event listeners for various inputs
	 * (keyboard, mouse) or model changes (selection, scroll offset / viewport).
	 */
	protected void registerListeners() {
	}

	/**
	 * This method is called when the attached {@link IEditDomain} is reset to
	 * <code>null</code> so that you can unregister previously registered event
	 * listeners.
	 */
	protected void unregisterListeners() {
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
