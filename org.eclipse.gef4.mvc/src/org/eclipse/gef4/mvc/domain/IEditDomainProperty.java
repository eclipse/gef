package org.eclipse.gef4.mvc.domain;


public interface IEditDomainProperty<V> {

	public void setDomain(IEditDomain<V> domain);

	public IEditDomain<V> getDomain();
}
