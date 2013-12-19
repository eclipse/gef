package org.eclipse.gef4.mvc.tools;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef4.mvc.domain.IEditDomain;

public class CompositeTool<V> extends AbstractTool<V> implements ICompositeTool<V> {

	private List<ITool<V>> tools = new ArrayList<ITool<V>>();
	
	@Override
	public void activate() {
		super.activate();
		for(ITool<V> tool : tools){
			tool.activate();
		}
	}

	@Override
	public void deactivate() {
		for(ITool<V> tool : tools){
			tool.deactivate();
		}
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
