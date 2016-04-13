package org.eclipse.gef4.zest.fx.providers;

import org.eclipse.gef4.fx.anchors.DynamicAnchor;
import org.eclipse.gef4.fx.utils.NodeUtils;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.mvc.fx.providers.DynamicAnchorProvider;
import org.eclipse.gef4.zest.fx.parts.NodePart;

import javafx.scene.Node;

/**
 * A specific {@link DynamicAnchorProvider} that reflects the node shape as the
 * outline to place anchors at.
 *
 * @author anyssen
 *
 */
public class NodePartAnchorProvider extends DynamicAnchorProvider {

	@Override
	protected DynamicAnchor createAnchor() {
		return new DynamicAnchor(getAdaptable().getVisual()) {
			@Override
			public IGeometry getAnchorageReferenceGeometry() {
				Node shape = ((NodePart) getAdaptable()).getShape();
				return NodeUtils.localToParent(shape, NodeUtils.getShapeOutline(shape));
			}
		};
	}

}
