package org.eclipse.gef4.mvc.fx.ui.view;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;

public class DefaultSelectionProvider implements ISelectionProvider {

	private ISelection selection;
	private List<ISelectionChangedListener> selectionChangedListeners = new ArrayList<ISelectionChangedListener>();

	@Override
	public void addSelectionChangedListener(
			ISelectionChangedListener listener) {
		selectionChangedListeners.add(listener);
	}

	@Override
	public ISelection getSelection() {
		return selection;
	}

	@Override
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		selectionChangedListeners.remove(listener);
	}

	@Override
	public void setSelection(ISelection selection) {
		final SelectionChangedEvent e = new SelectionChangedEvent(this,
				selection);
		for (final ISelectionChangedListener l : selectionChangedListeners) {
			SafeRunner.run(new SafeRunnable() {
				public void run() {
					l.selectionChanged(e);
				}
			});
		}
	}
}