package org.eclipse.gef4.mvc.fx.example;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.scene.Node;

import org.eclipse.gef4.mvc.models.ISelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.tools.CompositeXorTool;
import org.eclipse.gef4.mvc.tools.ITool;

public class SelectionXorTool extends CompositeXorTool<Node> {

	private Map<Class<?>, ITool<Node>> typeToolMap = new HashMap<Class<?>, ITool<Node>>();

	private PropertyChangeListener pcl = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals(
					ISelectionModel.SELECTION_PROPERTY)) {
				Object newValue = evt.getNewValue();
				if (newValue instanceof List) {
					List<IContentPart<Node>> selection = (List<IContentPart<Node>>) newValue;
					if (selection.size() > 0) {
						IContentPart<Node> part = selection.get(0);
						if (typeToolMap.containsKey(part.getClass())) {
							ITool<Node> tool = typeToolMap.get(part.getClass());
							selectTool(tool);
						}
					}
				}
			}
		}
	};

	public SelectionXorTool() {
	}

	@Override
	protected void registerListeners() {
		// parent call first
		super.registerListeners();
		getDomain().getViewer().getContentPartSelection()
				.addPropertyChangeListener(pcl);
	}
	
	public void bindToolToType(Class<?> type, ITool<Node> tool) {
		add(tool);
		typeToolMap.put(type, tool);
	}

	@Override
	protected void unregisterListeners() {
		getDomain().getViewer().getContentPartSelection().removePropertyChangeListener(pcl);
		// parent call last
		super.unregisterListeners();
	}

}
