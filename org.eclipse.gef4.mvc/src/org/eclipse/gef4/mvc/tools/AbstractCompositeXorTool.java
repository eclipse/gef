package org.eclipse.gef4.mvc.tools;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef4.mvc.domain.IEditDomain;

public abstract class AbstractCompositeXorTool<V> extends AbstractTool<V> implements ICompositeTool<V> {

	private List<ITool<V>> tools = new ArrayList<ITool<V>>();
	
	private ITool<V> selectedTool = null;
	
	protected void selectTool(ITool<V> tool) {
		if (tool == selectedTool) {
			return;
		} 
		if (selectedTool != null) {
			if (isActive()) {
				selectedTool.deactivate();
			}
		}
		selectedTool = tool;
		if (tool != null) {
			if (isActive()) {
				tool.activate();
			}
		}
	}
	
	@Override
	public void activate() {
		super.activate();
		register();
		if (selectedTool != null) {
			selectedTool.activate();
		}
	}

	protected abstract void register();
	protected abstract void unregister();

	@Override
	public void deactivate() {
		if (selectedTool != null) {
			selectedTool.deactivate();
		}
		unregister();
		super.deactivate();
	}

	@Override
	public void add(ITool<V> tool) {
		assertNotActive();
		tools.add(tool);
	}

	@Override
	public void add(int index, ITool<V> tool) {
		assertNotActive();
		tools.add(index, tool);	
	}

	@Override
	public void remove(ITool<V> tool) {
		assertNotActive();
		tools.remove(tool);
	}

	@Override
	public void remove(int index) {
		assertNotActive();
		tools.remove(index);
	}

	private void assertNotActive() {
		if (isActive()) {
			throw new IllegalArgumentException(
					"May not manipulate nested tools while being active.");
		}
	}
	
	@Override
	public void setDomain(IEditDomain<V> domain) {
		super.setDomain(domain);
		for(ITool<V> tool : tools){
			tool.setDomain(domain);
		}
	}

}
