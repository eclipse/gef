package org.eclipse.gef4.zest.fx.providers;

import org.eclipse.gef4.fx.anchors.DynamicAnchor;
import org.eclipse.gef4.fx.anchors.IComputationStrategy;
import org.eclipse.gef4.fx.utils.NodeUtils;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.mvc.fx.providers.DynamicAnchorProvider;
import org.eclipse.gef4.zest.fx.parts.NodePart;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;

/**
 * A specific {@link DynamicAnchorProvider} that reflects the node shape as the
 * outline to place anchors at.
 *
 * @author anyssen
 *
 */
public class NodePartAnchorProvider extends DynamicAnchorProvider {

	/**
	 * Creates a new {@link NodePartAnchorProvider} that provides a
	 * {@link DynamicAnchor} with the default computation strategy.
	 */
	public NodePartAnchorProvider() {
	}

	/**
	 * Creates a new {@link NodePartAnchorProvider} that provides a
	 * {@link DynamicAnchor} with the provided computation strategy.
	 *
	 * @param computationStrategy
	 *            The {@link IComputationStrategy} to set on the provided
	 *            {@link DynamicAnchor}.
	 */
	public NodePartAnchorProvider(IComputationStrategy computationStrategy) {
		super(computationStrategy);
	}

	@Override
	protected DynamicAnchor createAnchor(IComputationStrategy computationStrategy) {
		final DynamicAnchor anchor = computationStrategy == null ? new DynamicAnchor(getAdaptable().getVisual())
				: new DynamicAnchor(getAdaptable().getVisual(), computationStrategy);
		anchor.referenceGeometryProperty().bind(new ObjectBinding<IGeometry>() {
			{
				// XXX: Binding value needs to be recomputed when the anchorage
				// changes or when the layout bounds of the respective anchorage
				// changes.
				anchor.anchorageProperty().addListener(new ChangeListener<Node>() {
					@Override
					public void changed(ObservableValue<? extends Node> observable, Node oldValue, Node newValue) {
						if (oldValue != null) {
							unbind(oldValue.layoutBoundsProperty());
						}
						if (newValue != null) {
							bind(newValue.layoutBoundsProperty());
						}
						invalidate();
					}
				});
				bind(anchor.getAnchorage().layoutBoundsProperty());
			}

			@Override
			protected IGeometry computeValue() {
				final Node shape = ((NodePart) getAdaptable()).getShape();
				return NodeUtils.localToParent(shape, NodeUtils.getShapeOutline(shape));
			}
		});
		return anchor;
	}

}
