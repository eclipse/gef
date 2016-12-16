/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG)  - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal

import com.google.inject.Inject
import org.eclipse.emf.common.util.Diagnostic
import org.eclipse.gef.dot.internal.generator.DotAttribute
import org.eclipse.gef.dot.internal.language.DotStandaloneSetup
import org.eclipse.gef.dot.internal.language.arrowtype.ArrowType
import org.eclipse.gef.dot.internal.language.clustermode.ClusterMode
import org.eclipse.gef.dot.internal.language.color.Color
import org.eclipse.gef.dot.internal.language.dir.DirType
import org.eclipse.gef.dot.internal.language.dot.GraphType
import org.eclipse.gef.dot.internal.language.layout.Layout
import org.eclipse.gef.dot.internal.language.outputmode.OutputMode
import org.eclipse.gef.dot.internal.language.pagedir.Pagedir
import org.eclipse.gef.dot.internal.language.point.Point
import org.eclipse.gef.dot.internal.language.rankdir.Rankdir
import org.eclipse.gef.dot.internal.language.shape.Shape
import org.eclipse.gef.dot.internal.language.splines.Splines
import org.eclipse.gef.dot.internal.language.splinetype.SplineType
import org.eclipse.gef.dot.internal.language.style.Style
import org.eclipse.gef.dot.internal.language.terminals.ID
import org.eclipse.gef.dot.internal.language.validation.DotJavaValidator
import org.eclipse.gef.graph.Graph
import org.eclipse.gef.graph.Node
import org.eclipse.gef.dot.internal.DotLanguageSupport.IAttributeValueParser

/**
 * The {@link DotAttributes} class contains all attributes which are supported
 * by {@link DotImport} and {@link DotExport}.
 * 
 * @author anyssen
 */
public class DotAttributes {

	@Inject
	private static DotJavaValidator dotValidator;

	// TODO: Don't validate here. Clients can validate if they want to using the dot java validator (via DotLanguageSupport)
	private static def validate(DotLanguageSupport.Context context, String attributeName, String attributeValue) {
		if (dotValidator == null) {

			// if we are not injected (standalone), create validator instance
			dotValidator = new DotStandaloneSetup().createInjectorAndDoEMFRegistration().getInstance(DotJavaValidator)
		}
 
		val diagnostics = dotValidator.validateAttributeValue(context, attributeName, attributeValue).filter[
			severity >= Diagnostic.ERROR]
		if (!diagnostics.isEmpty()) {
			throw new IllegalArgumentException(
				"Cannot set " + context.name().toLowerCase() + " attribute '" + attributeName + "' to '" +
					attributeValue + "'. " + diagnostics.filter[!message.empty].map[message].join(" "))
		}
	}
	
	/**
	 * Specifies the name of a graph, node, or edge (not an attribute), as
	 * retrieved through the graph, node_id, as well as edge_stmt and edgeRHS
	 * grammar rules.
	 */
	public static final String _NAME__GNE = "_name";

	/**
	 * Specifies the graph type.
	 */
	public static final String _TYPE__G = "_type";

	/**
	 * Returns the value of the {@link #_NAME__GNE} attribute of the given
	 * {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #_NAME__GNE} attribute.
	 * @return The value of the {@link #_NAME__GNE} attribute of the given
	 *         {@link Graph}.
	 */
	public static def ID _getNameRaw(Graph graph) {
		return graph.attributesProperty().get(_NAME__GNE) as ID;
	}

	/**
	 * Returns the value of the {@link #_NAME__GNE} attribute of the given
	 * {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #_NAME__GNE} attribute.
	 * @return The value of the {@link #_NAME__GNE} attribute of the given
	 *         {@link Graph}.
	 */
	public static def String _getName(Graph graph) {
		val ID _nameRaw = _getNameRaw(graph);
		return if(_nameRaw != null) _nameRaw.toValue() else null;
	}

	/**
	 * Returns the value of the {@link #_NAME__GNE} attribute of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #_NAME__GNE} attribute.
	 * @return The value of the {@link #_NAME__GNE} attribute of the given
	 *         {@link Node}.
	 */
	public static def ID _getNameRaw(Node node) {
		return node.attributesProperty().get(_NAME__GNE) as ID;
	}

	/**
	 * Returns the value of the {@link #_NAME__GNE} attribute of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #_NAME__GNE} attribute.
	 * @return The value of the {@link #_NAME__GNE} attribute of the given
	 *         {@link Node}.
	 */
	public static def String _getName(Node node) {
		val ID _nameRaw = _getNameRaw(node);
		return if(_nameRaw != null) _nameRaw.toValue() else null;
	}

	/**
	 * Returns the value of the {@link #_TYPE__G} attribute of the given
	 * {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #_TYPE__G} attribute.
	 * @return The value of the {@link #_TYPE__G} attribute of the given
	 *         {@link Graph}.
	 */
	public static def GraphType _getType(Graph graph) {
		return graph.attributesProperty().get(_TYPE__G) as GraphType;
	}

	/**
	 * Sets the {@link #_NAME__GNE} attribute of the given {@link Graph} to the
	 * given <i>name</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #_NAME__GNE} attribute.
	 * @param name
	 *            The new value for the {@link #_NAME__GNE} attribute.
	 */
	public static def void _setNameRaw(Graph graph, ID name) {
		graph.attributesProperty().put(_NAME__GNE, name);
	}

	/**
	 * Sets the {@link #_NAME__GNE} attribute of the given {@link Graph} to the
	 * given <i>name</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #_NAME__GNE} attribute.
	 * @param name
	 *            The new value for the {@link #_NAME__GNE} attribute.
	 */
	public static def void _setName(Graph graph, String name) {
		_setNameRaw(graph, ID.fromValue(name));
	}

	/**
	 * Sets the {@link #_NAME__GNE} attribute of the given {@link Node} to the
	 * given <i>name</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #_NAME__GNE} attribute.
	 * @param name
	 *            The new value for the {@link #_NAME__GNE} attribute.
	 */
	public static def void _setNameRaw(Node node, ID name) {
		node.attributesProperty().put(_NAME__GNE, name);
	}

	/**
	 * Sets the {@link #_NAME__GNE} attribute of the given {@link Node} to the
	 * given <i>name</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #_NAME__GNE} attribute.
	 * @param name
	 *            The new value for the {@link #_NAME__GNE} attribute.
	 */
	public static def void _setName(Node node, String name) {
		_setNameRaw(node, ID.fromValue(name));
	}

	/**
	 * Sets the {@link #_TYPE__G} attribute of the given {@link Graph} to the
	 * given <i>type</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #_TYPE__G} attribute.
	 * @param type
	 *            The new value for the {@link #_TYPE__G} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>type</i> value is not supported.
	 */
	public static def void _setType(Graph graph, GraphType type) {
		graph.attributesProperty().put(_TYPE__G, type);
	}

	@DotAttribute(rawType="STRING", parsedType=ArrowType)
	public static val String ARROWHEAD__E = "arrowhead";

	@DotAttribute(rawType="NUMERAL", parsedType=Double)
	public static final String ARROWSIZE__E = "arrowsize";

	@DotAttribute(rawType="STRING", parsedType=ArrowType)
	public static final String ARROWTAIL__E = "arrowtail";

	@DotAttribute(parsedType=Color)
	public static final String BGCOLOR__G = "bgcolor";

	@DotAttribute(rawType="STRING", parsedType=ClusterMode)
	public static final String CLUSTERRANK__G = "clusterrank";

	@DotAttribute(parsedType=Color)
	public static final String COLOR__NE = "color";

	@DotAttribute(parsedType=String)
	public static final String COLORSCHEME__GNE = "colorscheme";

	@DotAttribute(rawType="STRING", parsedType=DirType)
	public static final String DIR__E = "dir";

	@DotAttribute(rawType="NUMERAL", parsedType=Double)
	public static final String DISTORTION__N = "distortion";

	@DotAttribute(parsedType=Color)
	public static final String FILLCOLOR__NE = "fillcolor";

	@DotAttribute(rawType="STRING", parsedType=Boolean)
	public static final String FIXEDSIZE__N = "fixedsize";

	@DotAttribute(parsedType=Color)
	public static final String FONTCOLOR__GNE = "fontcolor";

	@DotAttribute(rawType="STRING", parsedType=Boolean)
	public static final String FORCELABELS__G = "forcelabels";

	@DotAttribute(rawType="QUOTED_STRING", parsedType=Point)
	public static final String HEAD_LP__E = "head_lp";

	@DotAttribute(parsedType=String)
	public static final String HEADLABEL__E = "headlabel";

	@DotAttribute(rawType="NUMERAL", parsedType=Double)
	public static final String HEIGHT__N = "height";

	@DotAttribute(parsedType=String)
	public static final String ID__GNE = "id";

	@DotAttribute(parsedType=String)
	public static final String LABEL__GNE = "label";

	@DotAttribute(parsedType=Color)
	public static final String LABELFONTCOLOR__E = "labelfontcolor";

	@DotAttribute(rawType="STRING", parsedType=Layout)
	public static final String LAYOUT__G = "layout";

	@DotAttribute(rawType="QUOTED_STRING", parsedType=Point)
	public static final String LP__GE = "lp";

	@DotAttribute(rawType="STRING", parsedType=OutputMode)
	public static final String OUTPUTORDER__G = "outputorder";

	@DotAttribute(rawType="STRING", parsedType=Pagedir)
	public static final String PAGEDIR__G = "pagedir";

	//XXX: pos is a special case, where different parsed values for Node and Edge attributes (Point, SplineType) and thus 
	//     different parsers and serializers are required
	@DotAttribute(rawType="QUOTED_STRING", parsedType=#[Point, SplineType])
	public static final String POS__NE = "pos";

	@DotAttribute(rawType="STRING", parsedType=Rankdir)
	public static final String RANKDIR__G = "rankdir";

	@DotAttribute(parsedType=Shape)
	public static final String SHAPE__N = "shape";

	@DotAttribute(rawType="NUMERAL", parsedType=Integer)
	public static final String SIDES__N = "sides";

	@DotAttribute(rawType="NUMERAL", parsedType=Double)
	public static final String SKEW__N = "skew";

	@DotAttribute(rawType="QUOTED_STRING", parsedType=Splines)
	public static final String SPLINES__G = "splines";

	@DotAttribute(parsedType=Style)
	public static final String STYLE__GNE = "style";

	@DotAttribute(rawType="QUOTED_STRING", parsedType=Point)
	public static final String TAIL_LP__E = "tail_lp";

	@DotAttribute(parsedType=String)
	public static final String TAILLABEL__E = "taillabel";

	@DotAttribute(rawType="NUMERAL", parsedType=Double)
	public static final String WIDTH__N = "width";

	@DotAttribute(parsedType=String)
	public static final String XLABEL__NE = "xlabel";

	@DotAttribute(rawType="QUOTED_STRING", parsedType=Point)
	public static final String XLP__NE = "xlp";

}
