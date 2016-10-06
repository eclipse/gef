package org.eclipse.gef.mvc.fx.parts;

import org.eclipse.gef.mvc.parts.ITransformableContentPart;

import javafx.scene.Node;

/**
 *
 * @author wienand
 *
 * @param <V>
 *            the visual type parameter
 */
// FIXME: no-implement
public interface IFXTransformableContentPart<V extends Node> extends
		IFXTransformableVisualPart<V>, ITransformableContentPart<Node, V> {

}
