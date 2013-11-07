package org.eclipse.gef4.mvc.domain;

public abstract class AbstractEditDomainProperty<V> implements IEditDomainProperty<V> {

	private IEditDomain<V> domain;

	@Override
	public void setDomain(IEditDomain<V> domain) {
		this.domain = domain;
	}

	@Override
	public IEditDomain<V> getDomain() {
		return domain;
	}

}
