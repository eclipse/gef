package org.eclipse.gef4.mvc.tools;

import org.eclipse.gef4.mvc.domain.IEditDomain;
import org.eclipse.gef4.mvc.partviewer.IEditPartViewer;

public abstract class AbstractTool<V> implements ITool<V> {

	private IEditPartViewer<V> viewer;
	private IEditDomain<V> domain;

	@Override
	public void setEditDomain(IEditDomain<V> domain) {
		this.domain = domain;
	}

	@Override
	public void setViewer(IEditPartViewer<V> viewer) {
		this.viewer = viewer;
	}

	@Override
	public IEditDomain<V> getEditDomain() {
		return domain;
	}

	@Override
	public IEditPartViewer<V> getViewer() {
		return viewer;
	}

}
