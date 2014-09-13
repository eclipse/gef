package org.eclipse.gef4.common.adapt;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Map;

public class AdapterStore implements IAdaptable {
	
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private AdaptableSupport<AdapterStore> as = new AdaptableSupport<AdapterStore>(
			this, pcs);

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	@Override
	public <T> T getAdapter(Class<? super T> key) {
		return as.getAdapter(key);
	}

	@Override
	public <T> Map<AdapterKey<? extends T>, T> getAdapters(Class<? super T> key) {
		return as.getAdapters(key);
	}

	@Override
	public <T> T getAdapter(AdapterKey<? super T> key) {
		return as.getAdapter(key);
	}

	@Override
	public <T> void setAdapter(AdapterKey<? super T> key, T adapter) {
		as.setAdapter(key, adapter);
	}

	@Override
	public <T> T unsetAdapter(AdapterKey<? super T> key) {
		return as.unsetAdapter(key);
	}
}