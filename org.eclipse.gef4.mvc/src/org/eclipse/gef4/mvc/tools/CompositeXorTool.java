package org.eclipse.gef4.mvc.tools;

public class CompositeXorTool<V> extends AbstractCompositeTool<V> implements
		ICompositeTool<V> {

	private ITool<V> selectedTool = null;

	protected void selectTool(ITool<V> tool) {
		if (tool == selectedTool) {
			return;
		}
		if (selectedTool != null) {
			if (selectedTool.isActive()) {
				selectedTool.deactivate();
			}
		}
		selectedTool = tool;
		if (selectedTool != null) {
			if (isActive()) {
				selectedTool.activate();
			}
		}
	}

	@Override
	public void deactivate() {
		if (selectedTool != null) {
			selectedTool.deactivate();
		}
		super.deactivate();
	}
	
	@Override
	public void activate() {
		super.activate();
		if (selectedTool != null) {
			selectedTool.activate();
		}
	}

}
