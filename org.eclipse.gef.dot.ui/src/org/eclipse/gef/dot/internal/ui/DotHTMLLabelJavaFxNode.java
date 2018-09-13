/*******************************************************************************
 * Copyright (c) 2018 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Zoey Gerrit Prigge - Initial API and implementation (bug #321775)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.gef.dot.internal.language.DotHtmlLabelStandaloneSetup;
import org.eclipse.gef.dot.internal.language.htmllabel.DotHtmlLabelHelper;
import org.eclipse.gef.dot.internal.language.htmllabel.HtmlAttr;
import org.eclipse.gef.dot.internal.language.htmllabel.HtmlContent;
import org.eclipse.gef.dot.internal.language.htmllabel.HtmlLabel;
import org.eclipse.gef.dot.internal.language.htmllabel.HtmlTag;
import org.eclipse.gef.dot.internal.language.parser.antlr.DotHtmlLabelParser;
import org.eclipse.gef.zest.fx.parts.NodePart;
import org.eclipse.gef.zest.fx.parts.ZestFxRootPart;
import org.eclipse.xtext.parser.IParseResult;

import com.google.inject.Injector;

import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/*
 * This class will only look into the inner HTML label (i.e. not including the shape)
 * 
 * The graphviz grammar at http://www.graphviz.org/doc/info/shapes.html#html diverges in naming
 * from the DotHtmlLabel grammar due to a choice for higher availability of syntax help in the UI
 * 
 * Hence, this class relies on validation of the model and cites the graphviz grammar to align
 * behaviour with what the graphviz grammar permits.
 * 
 * Comments will refer to the graphviz grammar at the URL above.
 *
 * Known Limitations, TODO:
 * - consider outside-label formatting and default size, padding
 * - consider implementing Port attribute on TD
 */
public class DotHTMLLabelJavaFxNode {

	final private HtmlLabel root;

	final private DotColorUtil colorUtil = new DotColorUtil();

	/**
	 * Creates a DotHTMLLabelJavaNode creator
	 * 
	 * @param label
	 *            HTML label attribute as string
	 */
	public DotHTMLLabelJavaFxNode(final String label) {
		root = parseLabel(label);
	}

	/**
	 * Method to retrieve the JavaFX pane
	 * 
	 * @return Java Fx Pane
	 */
	public Pane getFxElement() {
		Pane element = drawLabel(root);
		element.getStyleClass().add(NodePart.CSS_CLASS_LABEL);
		return element;
	}

	/**
	 * Bounds for the JavaFxElement after CSS
	 * 
	 * @return Bounds
	 */
	public Bounds getBounds() {
		Group fxElement = new Group(getFxElement());
		Scene scene = new Scene(fxElement);
		scene.getStylesheets().add(ZestFxRootPart.STYLES_CSS_FILE);
		fxElement.applyCss();
		fxElement.layout();
		return fxElement.getBoundsInParent();
	}

	private HtmlLabel parseLabel(final String label) {
		Injector labelInjector = new DotHtmlLabelStandaloneSetup()
				.createInjectorAndDoEMFRegistration();
		DotHtmlLabelParser parser = labelInjector
				.getInstance(DotHtmlLabelParser.class);
		IParseResult result = parser
				.parse(new StringReader(label != null ? label : new String()));
		return (HtmlLabel) result.getRootASTElement();
	}

	private Pane drawLabel(HtmlLabel label) {
		// TODO consider making TagStyleContainer a public class, use this to
		// get the styles set as attributes on the node itself
		return drawContents(label.getParts(), null, null);
	}

	private Pane drawContents(List<HtmlContent> contents,
			TagStyleContainer parentStyle, Pos bAlign) {
		if (contents.size() <= 0)
			return new Pane(); // an empty HTML label, ought not to occur

		// if there is more than one label, this is, strictly speaking a dot
		// syntax error; however, the grammar does not recognize this.
		// hence, we need to check if the first tag is a table and else treat
		// contents as text.
		HtmlContent content0 = contents.get(0);

		// we need to ignore leading whitespace
		if (isWhitespaceOnlyTag(content0) && contents.size() > 1)
			content0 = contents.get(1);

		HtmlTag tag = content0.getTag();

		if (tag != null && tag.getName().equalsIgnoreCase("table")) //$NON-NLS-1$
			return drawTable(tag, parentStyle);

		// FONT is a special case where it could be either a textitem or a table
		// The documented graphviz grammar specifies the table rule as follows
		// > table : [ <FONT> ] <TABLE> rows </TABLE> [ </FONT> ]
		// We need to differentiate between the FONT keyword in table and
		// in textitem because the tag could have multiple children in the
		// latter case. Hence, to avoid reimplementing a loop here, we hand this
		// down to the text handling.

		// TODO implement styleTag support as grammar now allows these to occur
		// before tables (changes described in the #23 comment on 321775) which
		// is in line with (undocumented) graphviz behaviour.
		else if (tag != null && tag.getName().equalsIgnoreCase("font")) { //$NON-NLS-1$
			if (tag.getChildren().size() != 1
					|| tag.getChildren().get(0).getTag() == null
					|| !tag.getChildren().get(0).getTag().getName()
							.equalsIgnoreCase("table")) //$NON-NLS-1$
				return drawText(contents, parentStyle, bAlign);
			return drawTable(tag.getChildren().get(0).getTag(),
					fontTagStyleContainer(tag, parentStyle));
		}
		// by the grammar we assume it's all text, if the first non-FONT-tag
		// does not indicate a table
		else
			return drawText(contents, parentStyle, bAlign);
	}

	private boolean isWhitespaceOnlyTag(HtmlContent content) {
		return content.getTag() == null && content.getText() != null
				&& content.getText().matches("\\A\\s*\\z") //$NON-NLS-1$
		;
	}

	private Pane drawText(List<HtmlContent> contents,
			TagStyleContainer parentStyle, Pos bAlign) {
		TextFXBuilder builder = new TextFXBuilder(bAlign);
		contents.forEach(
				content -> handleTextContent(builder, content, parentStyle));
		return builder.getFxElement();
	}

	/**
	 * 
	 * @param builder
	 *            NOT null
	 * @param tag
	 *            NOT null
	 * @param parentStyle
	 *            May be null
	 */
	private void handleTextTag(TextFXBuilder builder, HtmlTag tag,
			TagStyleContainer parentStyle) {
		TagStyleContainer tagStyleContainer;
		switch (tag.getName().toLowerCase()) {
		case "br": //$NON-NLS-1$
			builder.breakLine(getPosForBr(tag));
			return;
		case "table": //$NON-NLS-1$
			// table tags in this place are illegal
			return;
		case "font": //$NON-NLS-1$
			tagStyleContainer = fontTagStyleContainer(tag, parentStyle);
			break;
		// we can assume it's a style Tag
		default:
			tagStyleContainer = simpleTagStyleContainer(tag, parentStyle);
			break;
		}
		for (HtmlContent child : tag.getChildren())
			handleTextContent(builder, child, tagStyleContainer);
	}

	private TagStyleContainer simpleTagStyleContainer(HtmlTag tag,
			TagStyleContainer parentStyle) {
		TagStyle tagStyle = null;
		try {
			tagStyle = TagStyle.valueOf(tag.getName().toUpperCase());
		} catch (IllegalArgumentException e) {
			// we have an illegal tag, which is odd but can continue
		}
		return new TagStyleContainer(parentStyle, tagStyle);
	}

	private TagStyleContainer fontTagStyleContainer(HtmlTag tag,
			TagStyleContainer parentStyle) {
		final String color = unquotedValueForAttr(
				DotHtmlLabelHelper.getAttributeForTag(tag, "color")); //$NON-NLS-1$
		final String face = unquotedValueForAttr(
				DotHtmlLabelHelper.getAttributeForTag(tag, "face")); //$NON-NLS-1$
		final String size = unquotedValueForAttr(
				DotHtmlLabelHelper.getAttributeForTag(tag, "point-size")); //$NON-NLS-1$
		return new TagStyleContainer(parentStyle, face, size, color);
	}

	/**
	 * 
	 * @param builder
	 *            NOT null
	 * @param content
	 *            NOT null
	 * @param parentStyle
	 *            May be null
	 */
	private void handleTextContent(TextFXBuilder builder, HtmlContent content,
			TagStyleContainer parentStyle) {
		if (content.getTag() != null)
			handleTextTag(builder, content.getTag(), parentStyle);
		else
			builder.addFormattedString(new FormattedString(
					parentStyle != null ? parentStyle
							: new TagStyleContainer(null, null),
					StringEscapeUtils.unescapeHtml(content.getText())));
	}

	private Pos getPosForBr(HtmlTag brTag) {
		return getAlignPosForAttributeNameAndTag("align", brTag); //$NON-NLS-1$
	}

	private Pos getPosForTdBalign(HtmlTag tdTag) {
		return getAlignPosForAttributeNameAndTag("balign", tdTag); //$NON-NLS-1$
	}

	private Pos getAlignPosForAttributeNameAndTag(String attributeName,
			HtmlTag tag) {
		HtmlAttr attr = DotHtmlLabelHelper.getAttributeForTag(tag,
				attributeName);
		if (attr == null) {
			return null;
		}
		switch (unquotedValueForAttr(attr).toLowerCase()) {
		case "right": //$NON-NLS-1$
			return Pos.CENTER_RIGHT;
		case "left": //$NON-NLS-1$
			return Pos.CENTER_LEFT;
		case "center": //$NON-NLS-1$
		default:
			return Pos.CENTER;
		}
	}

	private Pane drawTable(HtmlTag tag, TagStyleContainer parentStyle) {
		// TODO VR, HR support

		GridPane fullPane = new GridPane();
		applyCssAttributesOnTablePane(fullPane, tag);
		List<HtmlTag> trTags = childHtmlTagsOfKind(tag, "TR"); //$NON-NLS-1$
		Map<Integer, BitSet> rowsToFilledCellsMap = initializedRowsToFilledCellsMap(
				trTags.size());
		for (HtmlTag tr : trTags)
			addRowToPane(tag, rowsToFilledCellsMap, tr, trTags.indexOf(tr),
					fullPane, parentStyle);
		return fullPane;
	}

	private void addRowToPane(HtmlTag tag,
			Map<Integer, BitSet> rowsToFilledCellsMap, HtmlTag tr, int rowIndex,
			GridPane fullPane, TagStyleContainer parentStyle) {
		for (HtmlTag td : childHtmlTagsOfKind(tr, "TD")) { //$NON-NLS-1$
			addCellToPane(tag, rowsToFilledCellsMap, rowIndex, fullPane,
					parentStyle, td);
		}
	}

	private void addCellToPane(HtmlTag tag,
			Map<Integer, BitSet> rowsToFilledCellsMap, int rowIndex,
			GridPane fullPane, TagStyleContainer parentStyle, HtmlTag td) {
		// "label" rule in the original graphviz grammar
		// > cell : <TD> label </TD>
		Pane labelPane = styledTdContent(tag, parentStyle, td);

		int colspan = getIntSpanAttrValue(td, "colspan"); //$NON-NLS-1$
		int rowspan = getIntSpanAttrValue(td, "rowspan"); //$NON-NLS-1$
		int index = rowsToFilledCellsMap.get(rowIndex).nextClearBit(0);

		for (int row = rowIndex; row < rowIndex + rowspan
				&& rowsToFilledCellsMap.containsKey(row); row++)
			rowsToFilledCellsMap.get(row).set(index, index + colspan);

		fullPane.add(labelPane, index, rowIndex, colspan, rowspan);
	}

	private GridPane styledTdContent(HtmlTag tag, TagStyleContainer parentStyle,
			HtmlTag td) {
		Pos bAlign = getPosForTdBalign(td);
		Pane unstyled = drawContents(td.getChildren(), parentStyle, bAlign);

		// to set Alignment and Growth the labelPane content must be wrapped
		GridPane wrapper = new GridPane();
		wrapper.add(unstyled, 0, 0);

		applyAlignAttributesOnTdPane(wrapper, td);
		applyCssAttributesOnTdPane(wrapper, td, tag);

		return wrapper;
	}

	private List<HtmlTag> childHtmlTagsOfKind(HtmlTag tag, String name) {
		return tag.getChildren().stream()
				.filter(child -> child.getTag() != null)
				.map(HtmlContent::getTag)
				.filter(trCandidate -> trCandidate.getName()
						.equalsIgnoreCase(name)) // $NON-NLS-1$
				.collect(Collectors.toList());
	}

	private Map<Integer, BitSet> initializedRowsToFilledCellsMap(int size) {
		Map<Integer, BitSet> tableCellMap = new HashMap<Integer, BitSet>();
		for (int key = 0; key < size; key++)
			tableCellMap.put(key, new BitSet());
		return tableCellMap;
	}

	private void applyCssAttributesOnTablePane(GridPane fullPane, HtmlTag tag) {
		StringBuilder css = new StringBuilder();
		/*
		 * Attributes TODO
		 * 
		 * Align (Note: For table ALIGN="CENTER|LEFT|RIGHT"), Cellspacing,
		 * COLUMNS="*" for a vertical line between every column, ROWS="*" for a
		 * horizontal line between every line, VALIGN="MIDDLE|BOTTOM|TOP"
		 * 
		 */

		// FIXEDSIZE="FALSE|TRUE" used in HEIGHT and WIDTH attributes
		HtmlAttr fixedSize = DotHtmlLabelHelper.getAttributeForTag(tag,
				"fixedsize"); //$NON-NLS-1$

		appendBgcolorAttribute(css,
				DotHtmlLabelHelper.getAttributeForTag(tag, "bgcolor"), //$NON-NLS-1$
				DotHtmlLabelHelper.getAttributeForTag(tag, "style"), //$NON-NLS-1$
				DotHtmlLabelHelper.getAttributeForTag(tag, "gradientangle")); //$NON-NLS-1$
		appendBorderAttribute(css,
				DotHtmlLabelHelper.getAttributeForTag(tag, "border")); //$NON-NLS-1$
		appendColorAttribute(css,
				DotHtmlLabelHelper.getAttributeForTag(tag, "color")); //$NON-NLS-1$
		appendHeightAttribute(css,
				DotHtmlLabelHelper.getAttributeForTag(tag, "height"), //$NON-NLS-1$
				fixedSize);
		appendSidesAttribute(css,
				DotHtmlLabelHelper.getAttributeForTag(tag, "sides")); //$NON-NLS-1$
		appendStyleTableAttribute(css,
				DotHtmlLabelHelper.getAttributeForTag(tag, "style")); //$NON-NLS-1$
		appendWidthAttribute(css,
				DotHtmlLabelHelper.getAttributeForTag(tag, "width"), fixedSize); //$NON-NLS-1$

		fullPane.setStyle(css.toString());
	}

	private void applyCssAttributesOnTdPane(Pane labelPane, HtmlTag tdTag,
			HtmlTag tableTag) {
		StringBuilder css = new StringBuilder();
		/*
		 * TODO Cellpadding
		 * 
		 * Attributes that are not relevant for layouting of the HTML label
		 * Href, Port, Title, Tooltip
		 * 
		 * Currently unsupported throughout: Gefdot, Id
		 */

		// FIXEDSIZE="FALSE|TRUE" used in HEIGHT and WIDTH attributes
		HtmlAttr fixedSize = DotHtmlLabelHelper.getAttributeForTag(tdTag,
				"fixedsize"); //$NON-NLS-1$

		appendBgcolorAttribute(css,
				DotHtmlLabelHelper.getAttributeForTag(tdTag, "bgcolor"), //$NON-NLS-1$
				DotHtmlLabelHelper.getAttributeForTag(tdTag, "style"), //$NON-NLS-1$
				DotHtmlLabelHelper.getAttributeForTag(tdTag, "gradientangle")); //$NON-NLS-1$
		appendBorderAttribute(css, borderAttributeForTd(tdTag, tableTag));
		appendColorAttribute(css,
				DotHtmlLabelHelper.getAttributeForTags("color", //$NON-NLS-1$
						tdTag, tableTag));
		appendHeightAttribute(css,
				DotHtmlLabelHelper.getAttributeForTag(tdTag, "height"), //$NON-NLS-1$
				fixedSize);
		appendSidesAttribute(css,
				DotHtmlLabelHelper.getAttributeForTags("sides", tdTag, //$NON-NLS-1$
						tableTag));
		appendWidthAttribute(css,
				DotHtmlLabelHelper.getAttributeForTag(tdTag, "width"), //$NON-NLS-1$
				fixedSize);

		labelPane.setStyle(css.toString());
	}

	private void appendStyleTableAttribute(StringBuilder css, HtmlAttr style) {
		if (style != null) {
			String styleValue = unquotedValueForAttr(style).toLowerCase();
			if (styleValue.contains("rounded")) { //$NON-NLS-1$
				css.append("-fx-border-radius: 5%;"); //$NON-NLS-1$
				css.append("-fx-background-radius: 5%;"); //$NON-NLS-1$
			}
		}
	}

	private void appendSidesAttribute(StringBuilder css, HtmlAttr sides) {
		if (sides != null) {
			String sidesShown = unquotedValueForAttr(sides).toLowerCase();
			if (!sidesShown.contains("l")) //$NON-NLS-1$
				css.append("-fx-border-left: hidden;"); //$NON-NLS-1$
			if (!sidesShown.contains("t")) //$NON-NLS-1$
				css.append("-fx-border-top: hidden;"); //$NON-NLS-1$
			if (!sidesShown.contains("r")) //$NON-NLS-1$
				css.append("-fx-border-right: hidden;"); //$NON-NLS-1$
			if (!sidesShown.contains("b")) //$NON-NLS-1$
				css.append("-fx-border-bottom: hidden;"); //$NON-NLS-1$
		}
	}

	private void appendHeightAttribute(StringBuilder css, HtmlAttr height,
			HtmlAttr fixedSize) {
		appendDimensionAttribute(css, "height", height, fixedSize); //$NON-NLS-1$
	}

	private void appendWidthAttribute(StringBuilder css, HtmlAttr width,
			HtmlAttr fixedSize) {
		appendDimensionAttribute(css, "width", width, fixedSize); //$NON-NLS-1$
	}

	private void appendDimensionAttribute(StringBuilder css, String kind,
			HtmlAttr dimension, HtmlAttr fixedSize) {
		if (dimension != null) {
			css.append("-fx-min-"); //$NON-NLS-1$
			css.append(kind); // $NON-NLS-1$
			css.append(":"); //$NON-NLS-1$
			css.append(unquotedValueForAttr(dimension));
			css.append(";"); //$NON-NLS-1$
			if (fixedSize != null && unquotedValueForAttr(fixedSize)
					.toLowerCase().equals("true")) { //$NON-NLS-1$
				css.append("-fx-max-"); //$NON-NLS-1$
				css.append(kind); // $NON-NLS-1$
				css.append(":"); //$NON-NLS-1$
				css.append(unquotedValueForAttr(dimension));
				css.append(";"); //$NON-NLS-1$
			}
		}
	}

	private void appendColorAttribute(StringBuilder css, HtmlAttr bordercolor) {
		css.append("-fx-border-color:"); //$NON-NLS-1$
		// TODO colorscheme
		css.append(bordercolor != null ? colorUtil.computeHtmlColor(null,
				unquotedValueForAttr(bordercolor)) : "black"); //$NON-NLS-1$
		css.append(";"); //$NON-NLS-1$
	}

	private void appendBorderAttribute(StringBuilder css, HtmlAttr border) {
		if (border != null) {
			css.append("-fx-border-width:"); //$NON-NLS-1$
			css.append(unquotedValueForAttr(border));
			css.append("pt;"); //$NON-NLS-1$
		}
	}

	private HtmlAttr borderAttributeForTd(HtmlTag tdTag, HtmlTag tableTag) {
		HtmlAttr border = DotHtmlLabelHelper.getAttributeForTag(tdTag,
				"border"); //$NON-NLS-1$
		if (border == null)
			border = DotHtmlLabelHelper.getAttributeForTag(tableTag,
					"cellborder"); //$NON-NLS-1$
		if (border == null)
			border = DotHtmlLabelHelper.getAttributeForTag(tableTag, "border"); //$NON-NLS-1$
		return border;
	}

	private void appendBgcolorAttribute(StringBuilder css, HtmlAttr bgcolor,
			HtmlAttr style, HtmlAttr gradientAngle) {
		if (bgcolor != null) {
			css.append("-fx-background-color:"); //$NON-NLS-1$
			// TODO colorscheme
			List<String> colors = Arrays
					.stream(unquotedValueForAttr(bgcolor).split(":")) //$NON-NLS-1$
					.map(e -> colorUtil.computeHtmlColor(null, e))
					.collect(Collectors.toList());
			if (colors.size() > 1) {
				if (style != null && unquotedValueForAttr(style).toLowerCase()
						.contains("radial")) { //$NON-NLS-1$
					css.append("radial-gradient("); //$NON-NLS-1$
					// TODO gradientangle
					css.append("center "); //$NON-NLS-1$
					css.append("50% 50%"); //$NON-NLS-1$
					css.append(", "); //$NON-NLS-1$
					css.append("radius 50%, "); //$NON-NLS-1$
				} else {
					// TODO gradientangle
					css.append("linear-gradient("); //$NON-NLS-1$
					css.append("from 0% 0% to 100% 0%, "); //$NON-NLS-1$
				}
				css.append(colors.get(0));
				css.append(", "); //$NON-NLS-1$
				css.append(colors.get(1));
				css.append(")"); //$NON-NLS-1$
			} else {
				css.append(colors.get(0));
			}
			css.append(";"); //$NON-NLS-1$
		}
	}

	private void applyAlignAttributesOnTdPane(GridPane wrapper, HtmlTag td) {
		String hAlign = unquotedValueForAttr(
				DotHtmlLabelHelper.getAttributeForTag(td, "align")); //$NON-NLS-1$
		String vAlign = unquotedValueForAttr(
				DotHtmlLabelHelper.getAttributeForTag(td, "valign")); //$NON-NLS-1$

		if ("text".equalsIgnoreCase(hAlign)) { //$NON-NLS-1$
			GridPane.setHgrow(wrapper.getChildren().get(0), Priority.ALWAYS);
		}

		wrapper.setAlignment(posForTd(hAlign, vAlign));
	}

	private Pos posForTd(String hAlign, String vAlign) {
		switch (hAlign != null ? hAlign.toLowerCase() : "") { //$NON-NLS-1$
		case "left": //$NON-NLS-1$
			switch (vAlign != null ? vAlign.toLowerCase() : "") { //$NON-NLS-1$
			case "top": //$NON-NLS-1$
				return Pos.TOP_LEFT;
			case "bottom": //$NON-NLS-1$
				return Pos.BOTTOM_LEFT;
			case "middle": //$NON-NLS-1$
			default:
				return Pos.CENTER_LEFT;
			}
		case "right": //$NON-NLS-1$
			switch (vAlign != null ? vAlign.toLowerCase() : "") { //$NON-NLS-1$
			case "top": //$NON-NLS-1$
				return Pos.TOP_RIGHT;
			case "bottom": //$NON-NLS-1$
				return Pos.BOTTOM_RIGHT;
			case "middle": //$NON-NLS-1$
			default:
				return Pos.CENTER_RIGHT;
			}
		case "center": //$NON-NLS-1$
		case "text": //$NON-NLS-1$
		default:
			switch (vAlign != null ? vAlign.toLowerCase() : "") { //$NON-NLS-1$
			case "top": //$NON-NLS-1$
				return Pos.TOP_CENTER;
			case "bottom": //$NON-NLS-1$
				return Pos.BOTTOM_CENTER;
			case "middle": //$NON-NLS-1$
			default:
				return Pos.CENTER;
			}
		}
	}

	private String unquotedValueForAttr(HtmlAttr attr) {
		if (attr == null)
			return null;
		String value = attr.getValue();
		return value.replace("\"", ""); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private int getIntSpanAttrValue(HtmlTag tag, String name) {
		HtmlAttr attribute = DotHtmlLabelHelper.getAttributeForTag(tag, name);
		if (attribute == null)
			// the span of a cell is 1 if the attribute is not set
			return 1;
		else
			return Integer.valueOf(unquotedValueForAttr(attribute));
	}

	private enum TagStyle {
		I, B, U, O, SUB, SUP, S;

		public String cssStringForTag() {
			switch (this) {
			case I:
				return "-fx-font-style: italic;"; //$NON-NLS-1$
			case B:
				return "-fx-font-weight: bold;"; //$NON-NLS-1$
			case U:
				return "-fx-text-decoration: underline;"; //$NON-NLS-1$
			case O:
				return "-fx-text-decoration: overline;"; //$NON-NLS-1$
			case SUB:
				return "-fx-font-size: .83em; -fx-vertical-align: sub"; //$NON-NLS-1$
			case SUP:
				return "-fx-font-size: .83em; -fx-vertical-align: super"; //$NON-NLS-1$
			case S:
				return "-fx-text-decoration: line-through;"; //$NON-NLS-1$
			default:
				return ""; //$NON-NLS-1$
			}
		}
	}

	private class TagStyleContainer {
		final private TagStyleContainer parent;
		final private TagStyle style;
		final private String face;
		final private String size;
		final private String color;

		public TagStyleContainer(TagStyleContainer parent, TagStyle style) {
			this.parent = parent;
			this.style = style;
			this.face = null;
			this.size = null;
			this.color = null;
		}

		public TagStyleContainer(TagStyleContainer parent, String face,
				String size, String color) {
			this.parent = parent;
			this.style = null;
			this.face = face;
			this.size = size;
			this.color = color;
		}

		private String face() {
			if (face != null)
				return face;
			if (parent != null)
				return parent.face();
			return null;
		}

		private String size() {
			if (size != null)
				return size;
			if (parent != null)
				return parent.size();
			return null;
		}

		private String color() {
			if (color != null)
				return color;
			if (parent != null)
				return parent.color();
			return null;
		}

		private String fontCss() {
			StringBuilder css = new StringBuilder();
			String face = face();
			if (face != null) {
				css.append("-fx-font-family:\""); //$NON-NLS-1$
				css.append(face);
				css.append("\";"); //$NON-NLS-1$
			}

			String size = size();
			if (size != null) {
				css.append("-fx-font-size:"); //$NON-NLS-1$
				css.append(size);
				css.append(";"); //$NON-NLS-1$
			}

			// TODO colorscheme
			String color = colorUtil.computeHtmlColor(null, color());
			if (color != null) {
				css.append("-fx-fill:"); //$NON-NLS-1$
				css.append(color);
				css.append(";"); //$NON-NLS-1$
			}

			return css.toString();
		}

		private Set<TagStyle> tagStyles() {
			Set<TagStyle> styles = parent == null ? new HashSet<TagStyle>()
					: parent.tagStyles();
			if (style != null)
				styles.add(style);
			return styles;
		}

		public String getCSS() {
			StringBuilder stringBuilder = new StringBuilder();
			tagStyles().forEach(
					style -> stringBuilder.append(style.cssStringForTag()));
			stringBuilder.append(fontCss());

			return stringBuilder.toString();
		}
	}

	private class FormattedString {
		private final TagStyleContainer style;
		private final String text;

		public FormattedString(TagStyleContainer style, String text) {
			this.style = style;
			this.text = text;
		}

		public Node getFxElement() {
			Text text = new Text(this.text != null ? this.text : ""); //$NON-NLS-1$
			text.setStyle(style.getCSS());
			return text;
		}
	}

	private class FormattedLine {
		private final List<FormattedString> textitems = new ArrayList<>();
		private Pos alignment = null;
		private Pos defaultAlignment;

		public FormattedLine(Pos bAlign) {
			defaultAlignment = bAlign != null ? bAlign : Pos.CENTER;
		}

		public void addFormattedString(FormattedString textItem) {
			textitems.add(textItem);
		}

		public void setAlignment(Pos pos) {
			if (pos != null) {
				alignment = pos;
			}
		}

		public Pane getFxElement() {
			final HBox hbox = new HBox();
			hbox.setAlignment(alignment != null ? alignment : defaultAlignment);
			textitems.forEach(textitem -> hbox.getChildren()
					.add(textitem.getFxElement()));
			return hbox;
		}
	}

	private class TextFXBuilder {
		private final List<FormattedLine> lines = new ArrayList<>();
		private FormattedLine current;
		private Pos bAlign;

		public TextFXBuilder(Pos bAlign) {
			this.bAlign = bAlign;
			newLine();
		}

		public void breakLine(Pos align) {
			current.setAlignment(align);
			newLine();
		}

		private void newLine() {
			FormattedLine line = new FormattedLine(bAlign);
			lines.add(line);
			current = line;
		}

		public void addFormattedString(FormattedString textItem) {
			current.addFormattedString(textItem);
		}

		public Pane getFxElement() {
			VBox vbox = new VBox();
			lines.forEach(line -> vbox.getChildren().add(line.getFxElement()));
			return vbox;
		}
	}
}
