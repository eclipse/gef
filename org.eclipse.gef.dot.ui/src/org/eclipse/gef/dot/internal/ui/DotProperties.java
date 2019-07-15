/************************************************************************************************
 * Copyright (c) 2020 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Zoey Prigge (itemis AG) - initial API and implementation (bug #321775)
 *
 ***********************************************************************************************/
package org.eclipse.gef.dot.internal.ui;

import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.graph.Node;

import com.google.inject.Provider;

public class DotProperties {
	public static String INNER_SHAPE__N = "dotInnerShape__n"; //$NON-NLS-1$
	public static String INNER_SHAPE_DISTANCE__N = "dotInnerShapeDistance__n"; //$NON-NLS-1$
	private static String HTML_LIKE_LABEL__NE = "dotHtmlLikeLabel__ne"; //$NON-NLS-1$
	private static String HTML_LIKE_EXTERNAL_LABEL__NE = "dotHtmlLikeExternalLabel__ne"; //$NON-NLS-1$
	private static String HTML_LIKE_SOURCE_LABEL__E = "dotHtmlLikeSourceLabel__e"; //$NON-NLS-1$
	private static String HTML_LIKE_TARGET_LABEL__E = "dotHtmlLikeTargetLabel__e"; //$NON-NLS-1$

	public static javafx.scene.Node getHtmlLikeExternalLabel(Edge edge) {
		Object value = edge.attributesProperty()
				.get(HTML_LIKE_EXTERNAL_LABEL__NE);
		if (value instanceof Provider) {
			return (javafx.scene.Node) ((Provider<?>) value).get();
		}
		return (javafx.scene.Node) value;
	}

	public static javafx.scene.Node getHtmlLikeExternalLabel(Node node) {
		Object value = node.attributesProperty()
				.get(HTML_LIKE_EXTERNAL_LABEL__NE);
		if (value instanceof Provider) {
			return (javafx.scene.Node) ((Provider<?>) value).get();
		}
		return (javafx.scene.Node) value;
	}

	public static javafx.scene.Node getHtmlLikeLabel(Edge edge) {
		Object value = edge.attributesProperty().get(HTML_LIKE_LABEL__NE);
		if (value instanceof Provider) {
			return (javafx.scene.Node) ((Provider<?>) value).get();
		}
		return (javafx.scene.Node) value;
	}

	public static javafx.scene.Node getHtmlLikeLabel(Node node) {
		Object value = node.attributesProperty().get(HTML_LIKE_LABEL__NE);
		if (value instanceof Provider) {
			return (javafx.scene.Node) ((Provider<?>) value).get();
		}
		return (javafx.scene.Node) value;
	}

	public static javafx.scene.Node getHtmlLikeSourceLabel(Edge edge) {
		Object value = edge.attributesProperty().get(HTML_LIKE_SOURCE_LABEL__E);
		if (value instanceof Provider) {
			return (javafx.scene.Node) ((Provider<?>) value).get();
		}
		return (javafx.scene.Node) value;
	}

	public static javafx.scene.Node getHtmlLikeTargetLabel(Edge edge) {
		Object value = edge.attributesProperty().get(HTML_LIKE_TARGET_LABEL__E);
		if (value instanceof Provider) {
			return (javafx.scene.Node) ((Provider<?>) value).get();
		}
		return (javafx.scene.Node) value;
	}

	public static void setHtmlLikeExternalLabel(Edge edge,
			javafx.scene.Node htmlLikeLabel) {
		if (htmlLikeLabel == null) {
			edge.getAttributes().remove(HTML_LIKE_EXTERNAL_LABEL__NE);
		} else {
			edge.getAttributes().put(HTML_LIKE_EXTERNAL_LABEL__NE,
					htmlLikeLabel);
		}
	}

	public static void setHtmlLikeExternalLabel(Node node,
			javafx.scene.Node htmlLikeLabel) {
		if (htmlLikeLabel == null) {
			node.getAttributes().remove(HTML_LIKE_EXTERNAL_LABEL__NE);
		} else {
			node.getAttributes().put(HTML_LIKE_EXTERNAL_LABEL__NE,
					htmlLikeLabel);
		}
	}

	public static void setHtmlLikeLabel(Edge edge,
			javafx.scene.Node htmlLikeLabel) {
		if (htmlLikeLabel == null) {
			edge.getAttributes().remove(HTML_LIKE_LABEL__NE);
		} else {
			edge.getAttributes().put(HTML_LIKE_LABEL__NE, htmlLikeLabel);
		}
	}

	public static void setHtmlLikeLabel(Node node,
			javafx.scene.Node htmlLikeLabel) {
		if (htmlLikeLabel == null) {
			node.getAttributes().remove(HTML_LIKE_LABEL__NE);
		} else {
			node.getAttributes().put(HTML_LIKE_LABEL__NE, htmlLikeLabel);
		}
	}

	public static void setHtmlLikeSourceLabel(Edge edge,
			javafx.scene.Node htmlLikeLabel) {
		if (htmlLikeLabel == null) {
			edge.getAttributes().remove(HTML_LIKE_SOURCE_LABEL__E);
		} else {
			edge.getAttributes().put(HTML_LIKE_SOURCE_LABEL__E, htmlLikeLabel);
		}
	}

	public static void setHtmlLikeTargetLabel(Edge edge,
			javafx.scene.Node htmlLikeLabel) {
		if (htmlLikeLabel == null) {
			edge.getAttributes().remove(HTML_LIKE_TARGET_LABEL__E);
		} else {
			edge.getAttributes().put(HTML_LIKE_TARGET_LABEL__E, htmlLikeLabel);
		}
	}
}
