/*******************************************************************************
 * Copyright (c) 2019 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Zoey Prigge (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.dot.tests;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class DotRecordBasedLabelFxPrettyPrinter {
	protected String indent;
	protected String lineSeparator;

	/**
	 * Creates a Pretty Printer with the default settings.
	 */
	public DotRecordBasedLabelFxPrettyPrinter() {
		this("\t", System.lineSeparator());
	}

	/**
	 * Creates a Pretty Printer with the given indent and lineSeparator
	 * characters.
	 *
	 * @param indent
	 *            characters to use for indenting.
	 * @param lineSeparator
	 *            characters to use for line separation.
	 */
	public DotRecordBasedLabelFxPrettyPrinter(String indent,
			String lineSeparator) {
		this.indent = indent;
		this.lineSeparator = lineSeparator;
	}

	/**
	 * Creates a formatted string representation of a given javafx.scene.Node
	 * (expecting a record based label).
	 *
	 * @param fxNode
	 *            The node visualizing a record based label
	 * @return The formatted string representation (with line separation and
	 *         indentation) of the given node.
	 */
	public String prettyPrint(Node fxNode) {
		return prettyPrint(fxNode, "");
	}

	/**
	 * Creates a formatted string representation of a given javafx.scene.Node
	 * (expecting a record based label).
	 *
	 * @param fxNode
	 *            The node visualizing a record based label
	 * @param startIndent
	 *            The indentation to use when creating the formatted string
	 *            representation.
	 * @return The formatted string representation (with line separation and
	 *         indentation) of the given node.
	 */
	protected String prettyPrint(Node fxNode, String startIndent) {
		StringBuilder sb = new StringBuilder();
		String attributes = printAttributes(fxNode, startIndent + indent);

		sb.append(startIndent);
		sb.append(fxNode.getClass().getSimpleName());
		if (attributes.length() > 0) {
			sb.append(" {");
			sb.append(lineSeparator);

			sb.append(attributes);

			sb.append(startIndent);
			sb.append("}");
		}
		sb.append(lineSeparator);

		return sb.toString();
	}

	private String printAttributes(Node fxNode, String startIndent) {
		StringBuilder sb = new StringBuilder();

		String style = fxNode.getStyle();
		if (style != null && style.length() > 0) {
			sb.append(startIndent);
			sb.append("style : ");
			sb.append(fxNode.getStyle());
			sb.append(lineSeparator);
		}

		if (fxNode instanceof HBox || fxNode instanceof VBox) {
			Pos alignment = fxNode instanceof HBox
					? ((HBox) fxNode).getAlignment()
					: ((VBox) fxNode).getAlignment();
			if (alignment != Pos.CENTER) {
				sb.append(startIndent);
				sb.append("alignment : ");
				sb.append(alignment);
				sb.append(lineSeparator);
			}
		}

		if (fxNode instanceof Pane) {
			for (Node node : ((Pane) fxNode).getChildren()) {
				sb.append(prettyPrint(node, startIndent));
			}
		}

		if (fxNode instanceof Text) {
			sb.append(startIndent);
			sb.append("text : ");
			sb.append(((Text) fxNode).getText());
			sb.append(lineSeparator);
		}

		return sb.toString();
	}
}
