package org.eclipse.gef4.mvc.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.gef4.mvc.domain.IEditDomain;

public abstract class AbstractCompositeTool<V> extends AbstractTool<V>
		implements ICompositeTool<V> {

	private List<ITool<V>> subTools = null;

	@Override
	public void setDomain(IEditDomain<V> domain) {
		IEditDomain<V> oldDomain = getDomain();
		super.setDomain(domain);
		
		// propagate the domain to all sub-tools
		for (ITool<V> subTool : getSubTools()) {
			subTool.setDomain(domain);
		}
		
		// (un-)register event listeners for tool selection
		if (oldDomain == null && domain != null) {
			registerListeners();
		} else if (oldDomain != null && domain == null) {
			unregisterListeners();
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

	@SuppressWarnings("unchecked")
	public List<ITool<V>> getSubTools() {
		return (List<ITool<V>>) (subTools == null ? Collections.emptyList()
				: Collections.unmodifiableList(subTools));
	}

	@Override
	public void add(int index, ITool<V> tool) {
		assertNotActive(); 
		initSubToolsList();
		subTools.add(index, tool);
	}

	@Override
	public void add(ITool<V> tool) {
		assertNotActive();
		initSubToolsList();
		subTools.add(tool);
	}

	@Override
	public void remove(int index) {
		assertNotActive();
		if (subTools != null) {
			subTools.remove(index);
			cleanSubToolsList();
		}
	}

	@Override
	public void remove(ITool<V> tool) {
		assertNotActive();
		if (subTools != null) {
			subTools.remove(tool);
			cleanSubToolsList();
		}
	}

	private void cleanSubToolsList() {
		if (subTools.isEmpty()) {
			subTools = null;
		}
	}

	private void initSubToolsList() {
		if (subTools == null) {
			subTools = new ArrayList<ITool<V>>(1);
		}
	}
	
	private void assertNotActive() {
		if (isActive()) {
			throw new IllegalArgumentException(
					"May not manipulate nested tools while being active.");
		}
	}

}
