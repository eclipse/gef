package org.eclipse.gef4.mvc.tools;

public class CompositeAndTool<V> extends AbstractCompositeTool<V> {
	
	@Override
	public void activate() {
		super.activate();
		for (ITool<V> tool : getSubTools()) {
			tool.activate();
		}
	}

	@Override
	public void deactivate() {
		for(ITool<V> tool : getSubTools()){
			tool.deactivate();
		}
		super.deactivate();
	}
	
}
