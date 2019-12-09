/*******************************************************************************
 * Copyright (c) 2017, 2019 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Zoey Gerrit Prigge (itemis AG) - initial API and implementation (bug #454629)
 *    
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.conversion;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.dot.internal.language.parser.antlr.DotRecordLabelParser;
import org.eclipse.gef.dot.internal.language.rankdir.Rankdir;
import org.eclipse.gef.dot.internal.language.recordlabel.Field;
import org.eclipse.gef.dot.internal.ui.language.DotActivator;
import org.eclipse.gef.fx.nodes.GeometryNode;
import org.eclipse.gef.geometry.planar.Line;
import org.eclipse.gef.zest.fx.parts.NodePart;
import org.eclipse.gef.zest.fx.parts.ZestFxRootPart;
import org.eclipse.xtext.parser.IParseResult;

import com.google.inject.Injector;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * Class wrapping a JavaFX node for visualizing a record based node shape,
 * including label.
 */
class DotRecordBasedJavaFxNode {

	private static Insets TEXT_MARGINS = new Insets(1.5f, 7, 1.5f, 7);

	private final LabelNode root;

	private String zestNodeLabelCssStyle;

	private String lineCssStyle;

	/**
	 * Constructor for record based node shapes. Requires the string
	 * representation of the record shape label.
	 * 
	 * @param dotLabel
	 *            The string representation of the record shape label.
	 * @param rankdir
	 *            The rankdir set for the current graph
	 * @param zestNodeLabelCssStyle
	 *            The zestNodeLabelCssStyle set for the text of the record-based
	 *            node
	 * @param lineCssStyle
	 *            The css style set for the lines of the record-based node
	 */
	public DotRecordBasedJavaFxNode(String dotLabel, Rankdir rankdir,
			String zestNodeLabelCssStyle, String lineCssStyle) {
		root = rootNodeConstructor(rankdir).get().rootNode();
		this.zestNodeLabelCssStyle = zestNodeLabelCssStyle;
		this.lineCssStyle = lineCssStyle;
		addToFx(parseLabel(dotLabel), root);
	}

	/**
	 * Returns the wrapped JavaFX Pane
	 * 
	 * @return the wrapped JavaFX pane for this node shape
	 */
	public Pane getFxElement() {
		return root.getFxElement();
	}

	/**
	 * Applies CSS and returns the calculated bounds for the FX Pane
	 * 
	 * @return Bounds of the JavaFX pane after CSS/layout run.
	 */
	public Bounds getBounds() {
		Group fxElement = new Group(getFxElement());
		Scene scene = new Scene(fxElement);
		scene.getStylesheets().add(ZestFxRootPart.STYLES_CSS_FILE);
		fxElement.applyCss();
		fxElement.layout();
		return fxElement.getBoundsInParent();
	}

	private EObject parseLabel(final String dotLabel) {
		Injector recordLabelInjector = DotActivator.getInstance().getInjector(
				DotActivator.ORG_ECLIPSE_GEF_DOT_INTERNAL_LANGUAGE_DOTRECORDLABEL);
		DotRecordLabelParser parser = recordLabelInjector
				.getInstance(DotRecordLabelParser.class);
		IParseResult result = parser.parse(
				new StringReader(dotLabel != null ? dotLabel : new String()));
		return result.getRootASTElement();
	}

	private void addToFx(EObject parsedObject, LabelNode treeNode) {
		for (EObject child : parsedObject.eContents()) {
			if (child instanceof Field && ((Field) child).getLabel() != null) {
				addToFx(((Field) child).getLabel(), treeNode.childNode());
			} else if (child instanceof Field
					&& ((Field) child).getFieldID() != null) {
				treeNode.addText(((Field) child).getFieldID().getName(),
						zestNodeLabelCssStyle);
			}
		}
	}

	private Supplier<LabelNode> rootNodeConstructor(Rankdir rankdir) {
		switch (rankdir != null ? rankdir : Rankdir.TB) {
		case LR:
		case RL:
			return VerticalLabelNode::new;
		case TB:
		case BT:
		default:
			return HorizontalLabelNode::new;
		}
	}

	private abstract class LabelNode {
		private boolean firstField = true;

		abstract public Pane getFxElement();

		abstract public LabelNode provideChildNode();

		public LabelNode childNode() {
			LabelNode child = provideChildNode();
			addRotatedLabel(child);
			return child;
		}

		public LabelNode rootNode() {
			getFxElement().getStyleClass().addAll(NodePart.CSS_CLASS);
			return this;
		}

		public void addText(String string, String zestNodeLabelCssStyle) {
			separatorUnlessFirstField();
			TextHelper text = new TextHelper(string, zestNodeLabelCssStyle);
			setMargin(text.getFxElement(), TEXT_MARGINS);
			setGrow(text.getFxElement());
			getFxElement().getChildren().add(text.getFxElement());
		}

		public void addRotatedLabel(LabelNode label) {
			separatorUnlessFirstField();
			Node labelNode = label.getFxElement();
			setGrow(labelNode);
			getFxElement().getChildren().add(labelNode);
		}

		private void separatorUnlessFirstField() {
			if (!firstField)
				addLine();
			else
				firstField = false;
		}

		private void addLine() {
			getFxElement().getChildren()
					.add(new RecordBasedLabelLine(getOrientation()));
		}

		abstract protected Orientation getOrientation();

		abstract protected void setMargin(Node text, Insets insets);

		abstract protected void setGrow(Node node);
	}

	private class HorizontalLabelNode extends LabelNode {
		private HBox fxElement = new HBox();

		public HorizontalLabelNode() {
			fxElement.setAlignment(Pos.CENTER);
		}

		@Override
		public HBox getFxElement() {
			return fxElement;
		}

		@Override
		public LabelNode provideChildNode() {
			return new VerticalLabelNode();
		}

		@Override
		protected Orientation getOrientation() {
			return Orientation.HORIZONTAL;
		}

		@Override
		protected void setMargin(Node text, Insets insets) {
			HBox.setMargin(text, insets);
		}

		@Override
		protected void setGrow(Node node) {
			HBox.setHgrow(node, Priority.ALWAYS);
		}
	}

	private class VerticalLabelNode extends LabelNode {
		private VBox fxElement = new VBox();

		public VerticalLabelNode() {
			fxElement.setAlignment(Pos.CENTER);
		}

		@Override
		public VBox getFxElement() {
			return fxElement;
		}

		@Override
		public LabelNode provideChildNode() {
			return new HorizontalLabelNode();
		}

		@Override
		protected Orientation getOrientation() {
			return Orientation.VERTICAL;
		}

		@Override
		protected void setMargin(Node text, Insets insets) {
			VBox.setMargin(text, insets);
		}

		@Override
		protected void setGrow(Node node) {
			VBox.setVgrow(node, Priority.ALWAYS);
		}
	}

	private static class TextLine {
		private Pos pos;
		private String line;

		/**
		 * Used to initialize this text line with the first line of a record
		 * based label
		 * 
		 * @param lines
		 *            label of which this object should represent the first line
		 * @return remaining string with the first line removed
		 */
		public String consumeFirstLineOf(String lines) {
			int indexLast = 0;
			do {
				try {
					final int indexNew = lines.indexOf('\\', indexLast);
					if (indexNew < 0)
						throw new IndexOutOfBoundsException();
					switch (lines.charAt(indexNew + 1)) {
					case 'n':
						pos = Pos.CENTER;
						break;
					case 'l':
						pos = Pos.CENTER_LEFT;
						break;
					case 'r':
						pos = Pos.CENTER_RIGHT;
						break;
					default:
						indexLast = indexNew + 1;
						continue;
					}
					line = lines.substring(0, indexNew);
					if (lines.length() > indexNew + 2)
						return lines.substring(indexNew + 2);
					break;
				} catch (IndexOutOfBoundsException e) {
					line = lines;
					break;
				}
			} while (indexLast >= 0);
			return null;
		}

		public Pane getFxElement(String style) {
			if (line == null)
				throw new RuntimeException(
						"Unable to get FXElement: TextLine not initialized."); //$NON-NLS-1$
			final HBox alignmentBox = new HBox();
			alignmentBox.setAlignment(pos != null ? pos : Pos.CENTER);
			Text text = new Text(line);
			text.setStyle(style);
			HBox.setHgrow(text, Priority.NEVER);
			text.getStyleClass().add(NodePart.CSS_CLASS_LABEL);
			alignmentBox.getChildren().add(text);
			return alignmentBox;
		}
	}

	private static class TextHelper {
		private final Pane fxElement;
		private final String style;

		public TextHelper(String string, String style) {
			this.style = style;
			fxElement = fxElementFromString(unescapeString(string));
		}

		public Node getFxElement() {
			return fxElement;
		}

		private Pane fxElementFromString(final String string) {
			final VBox textContainer = new VBox();
			textContainer.setAlignment(Pos.CENTER);
			makeLines(string).forEach(line -> textContainer.getChildren()
					.add(line.getFxElement(style)));
			return textContainer;
		}

		private String unescapeString(String string) {
			// Any field should contain at least " ", for minimum width & height
			if (string == null || string.length() == 0)
				string = " "; //$NON-NLS-1$
			// HTML unescaped
			string = StringEscapeUtils.unescapeHtml(string);
			// Whitespace removed
			string = string.replaceAll("\t\\s+", "\t").replaceAll(" \\s+", " ") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					.replaceAll("[^ \t&&\\s]+", ""); //$NON-NLS-1$ //$NON-NLS-2$
			// String unescaped
			return string.replaceAll("\\\\([^lrn])", "$1"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		private List<TextLine> makeLines(String string) {
			List<TextLine> lines = new ArrayList<TextLine>();
			while (string != null) {
				TextLine line = new TextLine();
				string = line.consumeFirstLineOf(string);
				lines.add(line);
			}
			return lines;
		}
	}

	private class RecordBasedLabelLine extends GeometryNode<Line> {
		private final Line line;

		public RecordBasedLabelLine(Orientation orientation) {
			switch (orientation != null ? orientation
					: Orientation.HORIZONTAL) {
			case VERTICAL:
				line = new Line(0, 0, 1, 0);
				break;
			case HORIZONTAL:
			default:
				line = new Line(0, 0, 0, 1);
			}
			setGeometry(line);
			if (lineCssStyle != null && !lineCssStyle.isEmpty())
				setStyle(lineCssStyle);
		}
	}
}
