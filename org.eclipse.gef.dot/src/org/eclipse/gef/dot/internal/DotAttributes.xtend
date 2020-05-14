/*******************************************************************************
 * Copyright (c) 2016, 2020 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen    (itemis AG) - initial API and implementation
 *     Tamas Miklossy     (itemis AG) - Add support for all dot attributes (bug #461506)
 *     Zoey Gerrit Prigge (itemis AG) - Add support for all dot attributes (bug #461506)
 *                                    - Add clusterrank check in isCluster (bug #547809)
 *                                    - Include parsedAsAttribute (bug #548911)
 *                                    - change double parsing to include all values (bug #559031)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal

import com.google.inject.Injector
import java.io.StringReader
import java.util.Collections
import java.util.Iterator
import java.util.List
import java.util.Map
import org.eclipse.emf.common.util.BasicDiagnostic
import org.eclipse.emf.common.util.Diagnostic
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EStructuralFeature
import org.eclipse.gef.common.reflect.ReflectionUtils
import org.eclipse.gef.dot.internal.generator.DotAttribute
import org.eclipse.gef.dot.internal.language.DotArrowTypeStandaloneSetup
import org.eclipse.gef.dot.internal.language.DotColorListStandaloneSetup
import org.eclipse.gef.dot.internal.language.DotColorStandaloneSetup
import org.eclipse.gef.dot.internal.language.DotEscStringStandaloneSetup
import org.eclipse.gef.dot.internal.language.DotFontNameStandaloneSetup
import org.eclipse.gef.dot.internal.language.DotHtmlLabelStandaloneSetup
import org.eclipse.gef.dot.internal.language.DotPointStandaloneSetup
import org.eclipse.gef.dot.internal.language.DotPortPosStandaloneSetup
import org.eclipse.gef.dot.internal.language.DotRectStandaloneSetup
import org.eclipse.gef.dot.internal.language.DotShapeStandaloneSetup
import org.eclipse.gef.dot.internal.language.DotSplineTypeStandaloneSetup
import org.eclipse.gef.dot.internal.language.DotStyleStandaloneSetup
import org.eclipse.gef.dot.internal.language.arrowtype.ArrowType
import org.eclipse.gef.dot.internal.language.clustermode.ClusterMode
import org.eclipse.gef.dot.internal.language.color.Color
import org.eclipse.gef.dot.internal.language.color.DotColors
import org.eclipse.gef.dot.internal.language.colorlist.ColorList
import org.eclipse.gef.dot.internal.language.dir.DirType
import org.eclipse.gef.dot.internal.language.dot.AttrStmt
import org.eclipse.gef.dot.internal.language.dot.Attribute
import org.eclipse.gef.dot.internal.language.dot.AttributeType
import org.eclipse.gef.dot.internal.language.dot.EdgeOp
import org.eclipse.gef.dot.internal.language.dot.EdgeStmtNode
import org.eclipse.gef.dot.internal.language.dot.EdgeStmtSubgraph
import org.eclipse.gef.dot.internal.language.dot.GraphType
import org.eclipse.gef.dot.internal.language.dot.NodeStmt
import org.eclipse.gef.dot.internal.language.dot.Subgraph
import org.eclipse.gef.dot.internal.language.doubleValues.DotDoubleUtil
import org.eclipse.gef.dot.internal.language.escstring.EscString
import org.eclipse.gef.dot.internal.language.fontname.FontName
import org.eclipse.gef.dot.internal.language.htmllabel.HtmlLabel
import org.eclipse.gef.dot.internal.language.layout.Layout
import org.eclipse.gef.dot.internal.language.outputmode.OutputMode
import org.eclipse.gef.dot.internal.language.pagedir.Pagedir
import org.eclipse.gef.dot.internal.language.point.Point
import org.eclipse.gef.dot.internal.language.portpos.PortPos
import org.eclipse.gef.dot.internal.language.rankdir.Rankdir
import org.eclipse.gef.dot.internal.language.ranktype.RankType
import org.eclipse.gef.dot.internal.language.rect.Rect
import org.eclipse.gef.dot.internal.language.shape.Shape
import org.eclipse.gef.dot.internal.language.splines.Splines
import org.eclipse.gef.dot.internal.language.splinetype.SplineType
import org.eclipse.gef.dot.internal.language.style.Style
import org.eclipse.gef.dot.internal.language.terminals.ID
import org.eclipse.gef.dot.internal.language.validation.DotArrowTypeValidator
import org.eclipse.gef.dot.internal.language.validation.DotColorListValidator
import org.eclipse.gef.dot.internal.language.validation.DotColorValidator
import org.eclipse.gef.dot.internal.language.validation.DotEscStringValidator
import org.eclipse.gef.dot.internal.language.validation.DotHtmlLabelValidator
import org.eclipse.gef.dot.internal.language.validation.DotPointValidator
import org.eclipse.gef.dot.internal.language.validation.DotPortPosValidator
import org.eclipse.gef.dot.internal.language.validation.DotRectValidator
import org.eclipse.gef.dot.internal.language.validation.DotShapeValidator
import org.eclipse.gef.dot.internal.language.validation.DotSplineTypeValidator
import org.eclipse.gef.dot.internal.language.validation.DotStyleValidator
import org.eclipse.gef.graph.Edge
import org.eclipse.gef.graph.Graph
import org.eclipse.gef.graph.Node
import org.eclipse.xtext.IGrammarAccess
import org.eclipse.xtext.nodemodel.INode
import org.eclipse.xtext.parser.IParseResult
import org.eclipse.xtext.parser.IParser
import org.eclipse.xtext.serializer.ISerializer
import org.eclipse.xtext.validation.AbstractDeclarativeValidator
import org.eclipse.xtext.validation.AbstractInjectableValidator
import org.eclipse.xtext.validation.CheckType
import org.eclipse.xtext.validation.RangeBasedDiagnostic
import org.eclipse.xtext.validation.ValidationMessageAcceptor

import static extension org.eclipse.emf.ecore.util.EcoreUtil.*
import static extension org.eclipse.xtext.EcoreUtil2.*

/**
 * The {@link DotAttributes} class contains all attributes which are supported
 * by {@link DotImport} and {@link DotExport}.
 *
 * @author anyssen
 */
class DotAttributes {

	/**
	 * Contexts by which attributes may be used.
	 */
	static enum Context {

		/**
		 * Graph context
		 */
		GRAPH,

		/**
		 * Edge context
		 */
		EDGE,

		/**
		 * Node context
		 */
		NODE,

		/**
		 * Subgraph context
		 */
		SUBGRAPH,

		/**
		 * Cluster subgraph context
		 */
		CLUSTER
	}

	private static class RangeBasedDiagnosticEx extends RangeBasedDiagnostic {
		new(int severity, String message, EObject source,
				int offset, int length, CheckType checkType, String issueCode,
				String[] issueData) {
			super(severity, message, source, offset, length, checkType, issueCode,
					issueData);
		}
}
	static def boolean isCluster(Node node) {
		var Graph rootGraph = null;
		for (var nestingNode = node;
			nestingNode !== null;
			nestingNode = rootGraph?.nestingNode) {
			rootGraph = nestingNode.graph;
		}

		if (node.nestedGraph === null || rootGraph !== null &&
			#[ClusterMode.NONE, ClusterMode.GLOBAL].contains(rootGraph.clusterrankParsed)) {
			return false
		}

		val name = node.nestedGraph._getName
		return name !== null && name.startsWith("cluster")
	}

	/**
	 * Determine the context in which the given {@link EObject} is used.
	 *
	 * @param eObject
	 *            The {@link EObject} for which the context is to be determined.
	 * @return the context in which the given {@link EObject} is used.
	 */
	static def Context getContext(EObject eObject) {

		// attribute nested below EdgeStmtNode or EdgeStmtSubgraph
		if (eObject.getContainerOfType(EdgeStmtNode) !== null ||
			eObject.getContainerOfType(EdgeStmtSubgraph) !== null
		) {
			return Context.EDGE
		}

		// global AttrStmt with AttributeType 'edge'
		val AttrStmt attrStmt = eObject.getContainerOfType(AttrStmt)
		if (attrStmt !== null && AttributeType.EDGE.equals(attrStmt.getType)) {
			return Context.EDGE
		}

		// attribute nested below NodeStmt
		if (eObject.getContainerOfType(NodeStmt) !== null) {
			return Context.NODE
		}

		// global AttrStmt with AttributeType 'node'
		if (attrStmt !== null && AttributeType.NODE.equals(attrStmt.getType)) {
			return Context.NODE
		}

		// attribute nested below Subgraph
		val Subgraph subgraph = eObject.getContainerOfType(Subgraph)
		if (subgraph !== null) {
			if (subgraph.name!==null && subgraph.name.toValue.startsWith("cluster")) {
				return Context.CLUSTER
			}
			return Context.SUBGRAPH
		}

		// attribute is neither edge nor node nor subgraph attribute
		Context.GRAPH
	}

	/**
	 * A validator for attribute values
	 *
	 * @param <T>
	 *            The type of the attribute.
	 */
	private interface IAttributeValueValidator<T> {

		/**
		 * Validates the given attribute value.
		 *
		 * @param attributeContext
		 *            The context of the attribute.
		 *
		 * @param attributeValue
		 *            The value to validate.
		 * @return A list of {@link Diagnostic}s that represent the validation
		 *         result.
		 */
		def List<Diagnostic> validate(Context attributeContext, T attributeValue)
	}

	/**
	 * A parser to parse a DOT primitive value type.
	 *
	 * @param <T>
	 *            The java equivalent of the parsed DOT value.
	 */
	private interface IAttributeValueParser<T> {

		static class ParseResult<T> {

			T parsedValue
			List<Diagnostic> syntaxErrors

			private new(T parsedValue) {
				this(parsedValue, Collections.<Diagnostic>emptyList)
			}

			private new(List<Diagnostic> syntaxErrors) {
				this(null, syntaxErrors)
			}

			private new(T parsedValue, List<Diagnostic> syntaxErrors) {
				this.parsedValue = parsedValue
				this.syntaxErrors = syntaxErrors
			}

			def T getParsedValue() {
				parsedValue
			}

			def List<Diagnostic> getSyntaxErrors() {
				syntaxErrors
			}

			def boolean hasSyntaxErrors() {
				!syntaxErrors.isEmpty
			}
		}

		/**
		 * Parses the given raw value as a DOT primitive value.
		 *
		 * @param attributeValue
		 *            The raw value to parse.
		 * @return A {@link ParseResult} indicating the parse result.
		 */
		def ParseResult<T> parse(String attributeValue)

		/**
		 * Returns the type parsed by this parser.
		 * @return The parsed type.
		 */
		def Class<T> getParsedType()
	}

	/**
	 * A serializer to serialize a DOT primitive value type.
	 *
	 * @param <T>
	 *            The java equivalent type to serialize.
	 */
	private interface IAttributeValueSerializer<T> {

		/**
		 * Serializes the given value.
		 *
		 * @param value
		 *            The value to serialize.
		 * @return The string representation to which the value was
		 *         serialized.
		 */
		def String serialize(T value)
	}

	/**
	 * Serialize the given attribute value using the given serializer.
	 *
	 * @param <T>
	 *            The (primitive) object type of the to be serialized value.
	 * @param serializer
	 *            The {@link IAttributeValueSerializer} to use for serializing.
	 * @param attributeValue
	 *            The value to serialize.
	 * @return The serialized value, or <code>null</code> if the value could not be serialized.
	 */
	private static def <T> String serializeAttributeValue(IAttributeValueSerializer<T> serializer, T attributeValue) {
		if (attributeValue === null) null else serializer.serialize(attributeValue)
	}

	/**
	 * Parses the given (unquoted) attribute, using the given
	 * {@link IAttributeValueParser}.
	 *
	 * @param <T>
	 *            The (primitive) object type of the parsed value.
	 * @param parser
	 *            The parser to be used for parsing.
	 * @param attributeValue
	 *            The attribute value that is to be parsed.
	 * @return The parsed value, or <code>null</code> if the value could not be
	 *         parsed.
	 */
	private static def <T> T parseAttributeValue(IAttributeValueParser<T> parser, String attributeValue) {
		if (attributeValue === null) null else parser.parse(attributeValue).parsedValue
	}

	// TODO: separate validation from parsing
	private static def <T> List<Diagnostic> validateAttributeRawValue(IAttributeValueParser<T> parser,
		IAttributeValueValidator<T> validator, Context attributeContext, String attributeName, ID attributeValue) {

		// determine dot attribute type name from parsed type
		val attributeType = if(parser === null) String else parser.parsedType
		var String attributeTypeName = switch (attributeType) {
			case Integer: "int"
			case Boolean: "bool"
			default: attributeType.simpleName.toFirstLower
		}

		// parse value first (if a parser is given); otherwise take the (String) value
		val T parsedValue =
			if (parser !== null) {
				val parseResult = parser.parse(attributeValue.toValue)
				if (parseResult.hasSyntaxErrors) {

					// handle syntactical problems
					return Collections.<Diagnostic>singletonList(
						new BasicDiagnostic(Diagnostic.ERROR, null, -1,
							"The value '" + attributeValue.toValue + "' is not a syntactically correct " + attributeTypeName +
								": " +
								parseResult.syntaxErrors.map[message.toFirstUpper.replaceAll("\\.$", "")].join(". ") + ".", #[]))
				}
				parseResult.getParsedValue
			} else {
				// for string values there is no parser
				attributeValue.toValue as Object as T
			}

		// handle semantical problems
		val List<Diagnostic> diagnostics = newArrayList
		if (validator !== null) {
			val List<Diagnostic> validationResults = validator.validate(attributeContext, parsedValue)
			val newMessagePrefix = "The " + attributeTypeName + " value '" + attributeValue.toValue + "' is not semantically correct: "
			for (Diagnostic result : validationResults) {
				val newMessage = newMessagePrefix + result.message
				diagnostics.add(
					if(result instanceof RangeBasedDiagnostic){
						new RangeBasedDiagnosticEx(result.severity, newMessage, null, result.offset, result.length, result.checkType, attributeName, result.issueData)
					}
					else{
						new BasicDiagnostic(result.severity, null, -1, newMessage, #[])
					}
				)
			}
		}

		diagnostics
	}

	private static def checkAttributeRawValue(Context context, String attributeName, ID attributeValue) {
		val diagnostics = validateAttributeRawValue(context, attributeName,
			attributeValue).filter[severity >= Diagnostic.ERROR]
		if (!diagnostics.isEmpty) {
			throw new IllegalArgumentException(
				"Cannot set " + context.name.toLowerCase + " attribute '" + attributeName + "' to '" +
					attributeValue.toValue + "'. " + diagnostics.filter[!message.empty].map[message].join(" "))
		}
	}

	/**
	 * Validate the attribute determined via name and value syntactically and
	 * semantically.
	 *
	 * @param attributeContext
	 *            The context element the attribute is related to.
	 * @param attributeName
	 *            The name of the attribute.
	 * @param attributeValue
	 *            The value of the attribute.
	 * @return A list of {@link Diagnostic} objects representing the identified
	 *         issues, or an empty list if no issues were found.
	 */
	// TODO: this can be generated, as well as the validators, parsers and serializers that are needed
	static def List<Diagnostic> validateAttributeRawValue(Context attributeContext, String attributeName,
		ID attributeValue) {

		// use parser (and validator) for respective attribute type
		return switch (attributeName) {
			case ARROWHEAD__E: validateAttributeRawValue(ARROWTYPE_PARSER, ARROWTYPE_VALIDATOR, attributeContext, attributeName, attributeValue)
			case ARROWSIZE__E: validateAttributeRawValue(DOUBLE_PARSER,	ARROWSIZE_VALIDATOR, attributeContext, attributeName, attributeValue)
			case ARROWTAIL__E: validateAttributeRawValue(ARROWTYPE_PARSER, ARROWTYPE_VALIDATOR, attributeContext, attributeName, attributeValue)
			case BB__GC: validateAttributeRawValue(RECT_PARSER, RECT_VALIDATOR, attributeContext, attributeName, attributeValue)
			case BGCOLOR__GC: validateAttributeRawValue(COLORLIST_PARSER, COLORLIST_VALIDATOR, attributeContext, attributeName, attributeValue)
			case CLUSTERRANK__G: validateAttributeRawValue(CLUSTERMODE_PARSER, null, attributeContext, attributeName, attributeValue)
			case COLORSCHEME__GCNE: validateAttributeRawValue(null, COLORSCHEME_VALIDATOR,	attributeContext, attributeName, attributeValue)
			case COLOR__CNE:
				if(attributeValue!==null && !attributeValue.toValue.isEmpty){
					// TODO: remove "attributeContext == Context.GRAPH", since color is not a valid graph attribute
					if(attributeContext == Context.GRAPH || attributeContext == Context.CLUSTER || attributeContext == Context.NODE)
						validateAttributeRawValue(COLOR_PARSER, COLOR_VALIDATOR, attributeContext, attributeName, attributeValue)
					else if (attributeContext == Context.EDGE)
						validateAttributeRawValue(COLORLIST_PARSER, COLORLIST_VALIDATOR, attributeContext, attributeName, attributeValue)
					else
						Collections.emptyList
				}else
					Collections.emptyList
			case DIR__E:
				if(attributeValue!==null && !attributeValue.toValue.isEmpty){
					validateAttributeRawValue(DIRTYPE_PARSER, null, attributeContext, attributeName, attributeValue)
				}else
					Collections.emptyList
			case DISTORTION__N: validateAttributeRawValue(DOUBLE_PARSER, DISTORTION_VALIDATOR, attributeContext, attributeName, attributeValue)
			case EDGETOOLTIP__E: validateAttributeRawValue(ESCSTRING_PARSER, ESCSTRING_VALIDATOR, attributeContext, attributeName, attributeValue)
			case FILLCOLOR__CNE:
				// TODO: remove "attributeContext == Context.GRAPH", since fillcolor is not a valid graph attribute
				if(attributeContext == Context.GRAPH || attributeContext == Context.CLUSTER || attributeContext == Context.NODE)
					validateAttributeRawValue(COLORLIST_PARSER, COLORLIST_VALIDATOR, attributeContext, attributeName, attributeValue)
				else if (attributeContext == Context.EDGE)
					validateAttributeRawValue(COLOR_PARSER, COLOR_VALIDATOR, attributeContext, attributeName, attributeValue)
				else
					Collections.emptyList
			case FIXEDSIZE__N:
				if(attributeValue!==null && !attributeValue.toValue.isEmpty){
					validateAttributeRawValue(BOOL_PARSER, null, attributeContext, FIXEDSIZE__N, attributeValue)
				}else
					Collections.emptyList
			case FONTCOLOR__GCNE: validateAttributeRawValue(COLOR_PARSER, COLOR_VALIDATOR, attributeContext, attributeName, attributeValue)
			case FONTNAME__GCNE: validateAttributeRawValue(FONTNAME_PARSER, null, attributeContext, attributeName, attributeValue)
			case FONTSIZE__GCNE: validateAttributeRawValue(DOUBLE_PARSER, FONTSIZE_VALIDATOR, attributeContext, attributeName, attributeValue)
			case FORCELABELS__G: validateAttributeRawValue(BOOL_PARSER, null, attributeContext, FORCELABELS__G, attributeValue)
			case HEAD_LP__E: validateAttributeRawValue(POINT_PARSER, POINT_VALIDATOR, attributeContext, attributeName, attributeValue)
			case HEADPORT__E: validateAttributeRawValue(PORTPOS_PARSER, PORTPOS_VALIDATOR, attributeContext, attributeName, attributeValue)
			case HEADTOOLTIP__E: validateAttributeRawValue(ESCSTRING_PARSER, ESCSTRING_VALIDATOR, attributeContext, attributeName, attributeValue)
			case HEIGHT__N: validateAttributeRawValue(DOUBLE_PARSER, HEIGHT_VALIDATOR, attributeContext, attributeName, attributeValue)
			case LABEL__GCNE:
				if (attributeValue.type == ID.Type.HTML_STRING)
					validateAttributeRawValue(HTML_LABEL_PARSER, HTML_LABEL_VALIDATOR, attributeContext, attributeName, attributeValue)
				else if (attributeValue.type == ID.Type.QUOTED_STRING)
					validateAttributeRawValue(ESCSTRING_PARSER, ESCSTRING_VALIDATOR, attributeContext, attributeName, attributeValue)
				else
					Collections.emptyList
			case LABELFONTCOLOR__E: validateAttributeRawValue(COLOR_PARSER, COLOR_VALIDATOR, attributeContext, attributeName, attributeValue)
			case LABELFONTNAME__E: validateAttributeRawValue(FONTNAME_PARSER, null, attributeContext, attributeName, attributeValue)
			case LABELFONTSIZE__E: validateAttributeRawValue(DOUBLE_PARSER, FONTSIZE_VALIDATOR, attributeContext, attributeName, attributeValue)
			case LABELTOOLTIP__E: validateAttributeRawValue(ESCSTRING_PARSER, ESCSTRING_VALIDATOR, attributeContext, attributeName, attributeValue)
			case LAYOUT__G: validateAttributeRawValue(LAYOUT_PARSER, null, attributeContext, attributeName, attributeValue)
			case LP__GCE: validateAttributeRawValue(POINT_PARSER, POINT_VALIDATOR, attributeContext, attributeName, attributeValue)
			case NODESEP__G: validateAttributeRawValue(DOUBLE_PARSER, NODESEP_VALIDATOR, attributeContext, attributeName, attributeValue)
			case OUTPUTORDER__G: validateAttributeRawValue(OUTPUTMODE_PARSER, null,	attributeContext, attributeName, attributeValue)
			case PAGEDIR__G: validateAttributeRawValue(PAGEDIR_PARSER, null, attributeContext, attributeName, attributeValue)
			case PENWIDTH__CNE:
				if(attributeValue!==null && !attributeValue.toValue.isEmpty) {
					validateAttributeRawValue(DOUBLE_PARSER, PENWIDTH_VALIDATOR, attributeContext, attributeName, attributeValue)
				} else {
					Collections.emptyList
				}
			case POS__NE:
				if (attributeContext == Context.NODE)
					validateAttributeRawValue(POINT_PARSER, POINT_VALIDATOR, attributeContext, attributeName, attributeValue)
				else if (attributeContext == Context.EDGE)
					validateAttributeRawValue(SPLINETYPE_PARSER, SPLINETYPE_VALIDATOR, attributeContext, attributeName, attributeValue)
				else
					Collections.emptyList
			case RANKDIR__G: validateAttributeRawValue(RANKDIR_PARSER, null, attributeContext, attributeName, attributeValue)
			case RANK__S: validateAttributeRawValue(RANKTYPE_PARSER, null, attributeContext, attributeName, attributeValue)
			case SHAPE__N: validateAttributeRawValue(SHAPE_PARSER, SHAPE_VALIDATOR, attributeContext, attributeName, attributeValue)
			case SIDES__N: validateAttributeRawValue(INT_PARSER, SIDES_VALIDATOR, attributeContext, attributeName, attributeValue)
			case SKEW__N: validateAttributeRawValue(DOUBLE_PARSER, SKEW_VALIDATOR, attributeContext, attributeName, attributeValue)
			case SPLINES__G: validateAttributeRawValue(SPLINES_PARSER, null, attributeContext, attributeName, attributeValue)
			case STYLE__GCNE: validateAttributeRawValue(STYLE_PARSER, STYLE_VALIDATOR, attributeContext, attributeName, attributeValue)
			case TAIL_LP__E: validateAttributeRawValue(POINT_PARSER, POINT_VALIDATOR, attributeContext, attributeName, attributeValue)
			case TAILPORT__E: validateAttributeRawValue(PORTPOS_PARSER, PORTPOS_VALIDATOR, attributeContext, attributeName, attributeValue)
			case TAILTOOLTIP__E: validateAttributeRawValue(ESCSTRING_PARSER, ESCSTRING_VALIDATOR, attributeContext, attributeName, attributeValue)
			case TOOLTIP__CNE: validateAttributeRawValue(ESCSTRING_PARSER, ESCSTRING_VALIDATOR, attributeContext, attributeName, attributeValue)
			case WIDTH__N: validateAttributeRawValue(DOUBLE_PARSER, WIDTH_VALIDATOR, attributeContext, attributeName, attributeValue)
			case XLP__NE: validateAttributeRawValue(POINT_PARSER, POINT_VALIDATOR, attributeContext, attributeName, attributeValue)
			default: {
				Collections.emptyList
			}
		}
	}

	/**
	 * A generic {@link IAttributeValueParser} for enumeration values.
	 *
	 * @param <E>
	 *            The type of enumeration to parse.
	 */
	private static class EnumParser<E extends Enum<E>> implements IAttributeValueParser<E> {

		Class<E> definition

		/**
		 * Creates a new parser for the given enumeration definition
		 *
		 * @param attributeType
		 *            The enumeration class.
		 */
		new(Class<E> attributeType) {
			this.definition = attributeType
		}

		override IAttributeValueParser.ParseResult<E> parse(String attributeValue) {
			if (attributeValue === null) {
				return null
			}
			for (E value : definition.enumConstants) {
				if (value.toString.equals(attributeValue)) {
					return new IAttributeValueParser.ParseResult<E>(value)
				}
			}
			return new IAttributeValueParser.ParseResult<E>(
				Collections.<Diagnostic>singletonList(
					new BasicDiagnostic(Diagnostic.ERROR, attributeValue, -1,
						"Value has to be one of " + definition.getEnumConstants.getFormattedValues, #[])))
		}

		private static def String getFormattedValues(Object[] values) {
			values.map["'"+it+"'"].join(", ")
		}

		override getParsedType() {
			return definition
		}

	}

	/**
	 * A generic {@link IAttributeValueSerializer} for enumeration values.
	 *
	 * @param <E>
	 *            The type of enumeration to serialize.
	 */
	private static class EnumSerializer<E extends Enum<E>> implements IAttributeValueSerializer<E> {

		Class<E> definition

		/**
		 * Creates a new serializer for the given enumeration definition
		 *
		 * @param definition
		 *            The enumeration class.
		 */
		new(Class<E> definition) {
			this.definition = definition
		}

		override serialize(E value) {
			if (!definition.isAssignableFrom(value.getClass)) {
				throw new IllegalArgumentException("Value does not comply to definition " + definition)
			}
			value.toString
		}
	}

	private static class EObjectParser<T extends EObject> implements IAttributeValueParser<T> {

		val Injector injector
		var IParser xtextParser
		var Class<T> parsedType

		new(Injector injector) {
			this.injector = injector
		}

		@SuppressWarnings("unchecked")
		override IAttributeValueParser.ParseResult<T> parse(String attributeValue) {
			val IParseResult xtextParseResult = parser.parse(new StringReader(attributeValue))
			if (xtextParseResult.hasSyntaxErrors) {
				val List<Diagnostic> syntaxProblems = newArrayList
				for (INode xtextSyntaxError : xtextParseResult.getSyntaxErrors) {
					syntaxProblems.add(
						new BasicDiagnostic(Diagnostic.ERROR, attributeValue, -1,
							xtextSyntaxError.syntaxErrorMessage.message, #[]))
				}
				return new IAttributeValueParser.ParseResult<T>(syntaxProblems)
			}
			return new IAttributeValueParser.ParseResult<T>(xtextParseResult.rootASTElement as T)
		}

		protected def IParser getParser() {
			if (xtextParser === null) {
				xtextParser = injector.getInstance(IParser)
			}
			return xtextParser
		}

		override getParsedType() {
			if (parsedType === null) {
				val grammarAccess = injector.getInstance(IGrammarAccess)
				parsedType = grammarAccess.grammar.rules.head.type.classifier.instanceClass as Class<T>
				if(parsedType === null){
					System.err.println("DotAttributes: parsedType cannot be determined for grammar: " + grammarAccess.grammar.name)
				}
			}

			parsedType
		}
	}

	private static class EObjectSerializer<T extends EObject> implements IAttributeValueSerializer<T> {

		val Injector injector
		var ISerializer serializer

		new(Injector injector) {
			this.injector = injector
		}

		override String serialize(T value) {
			val ISerializer serializer = getSerializer
			serializer.serialize(value)
		}

		protected def ISerializer getSerializer() {
			if (serializer === null) {
				serializer = injector.getInstance(ISerializer)
			}

			serializer
		}
	}

	private static class DoubleValidator implements IAttributeValueValidator<Double> {

		double minValue

		new(double minValue) {
			this.minValue = minValue
		}

		override List<Diagnostic> validate(Context attributeContext, Double attributeValue) {
			if (attributeValue.doubleValue < minValue) {
				return Collections.singletonList(
					new BasicDiagnostic(Diagnostic.ERROR, attributeValue.toString, -1,
						"Value may not be smaller than " + minValue + ".", #[]))
			}

			Collections.emptyList
		}
	}

	private static class IntValidator implements IAttributeValueValidator<Integer> {

		int minValue

		new(int minValue) {
			this.minValue = minValue
		}

		override List<Diagnostic> validate(Context attributeContext, Integer attributeValue) {
			if (attributeValue.doubleValue < minValue) {
				return Collections.singletonList(
					new BasicDiagnostic(Diagnostic.ERROR, attributeValue.toString, -1,
						"Value may not be smaller than " + minValue + ".", #[]))
			}

			Collections.emptyList
		}
	}

	private static class StringValidator implements IAttributeValueValidator<String> {

		var Object[] validValues

		new(Object[] validValues) {
			this.validValues = validValues
		}

		override List<Diagnostic> validate(Context attributeContext, String attributeValue) {
			for (Object validValue : validValues) {
				if (validValue.toString.equals(attributeValue)) {
					return Collections.emptyList
				}
			}
			return Collections.singletonList(
				new BasicDiagnostic(Diagnostic.ERROR, attributeValue, -1,
					"Value should be one of " + validValues.getFormattedValues + ".", #[]))
		}

		private def String getFormattedValues(Object[] values) {
			values.sortBy[toString].map["'"+it+"'"].join(", ")
		}
	}

	private static class EObjectValidator<T extends EObject> implements IAttributeValueValidator<T> {

		val Injector injector
		var Class<? extends AbstractDeclarativeValidator> validatorClass
		var AbstractDeclarativeValidator validator

		new(Injector injector, Class<? extends AbstractDeclarativeValidator> validatorClass) {
			this.injector = injector
			this.validatorClass = validatorClass
		}

		protected def AbstractDeclarativeValidator getValidator() {
			if (validator === null) {
				validator = injector.getInstance(validatorClass)
			}

			validator
		}

		override List<Diagnostic> validate(Context attributeContext, T attributeValue) {
			val AbstractDeclarativeValidator validator = getValidator

			val List<Diagnostic> diagnostics = newArrayList

			// validation is optional; if validator is provided, check for
			// semantic problems using it
			if (validator !== null) {

				// we need a specific message acceptor
				validator.setMessageAcceptor(
					/*
					 *  TODO: reuse the {@link org.eclipse.gef.dot.internal.language.validation.DotSubgrammarValidationMessageAcceptor} here
					 */
					new ValidationMessageAcceptor {

						override void acceptError(String message, EObject object,
							EStructuralFeature feature, int index, String code, String... issueData) {
							diagnostics.add(new BasicDiagnostic(Diagnostic.ERROR, null, -1, message, #[]))
						}

						override void acceptError(String message, EObject object, int offset, int length,
							String code, String... issueData) {
							diagnostics.add(new RangeBasedDiagnosticEx(Diagnostic.ERROR,
								message, object, offset, length, CheckType.FAST, code, issueData
							));
						}

						override void acceptInfo(String message, EObject object,
							EStructuralFeature feature, int index, String code, String... issueData) {
							diagnostics.add(new BasicDiagnostic(Diagnostic.INFO, null, -1, message, #[]))
						}

						override void acceptInfo(String message, EObject object, int offset, int length,
							String code, String... issueData) {
							diagnostics.add(new RangeBasedDiagnosticEx(Diagnostic.INFO,
								message, object, offset, length, CheckType.FAST, code, issueData
							));
						}

						override void acceptWarning(String message, EObject object,
							EStructuralFeature feature, int index, String code, String... issueData) {
							diagnostics.add(new BasicDiagnostic(Diagnostic.WARNING, null, -1, message, #[]))
						}

						override void acceptWarning(String message, EObject object, int offset, int length,
							String code, String... issueData) {
							diagnostics.add(new RangeBasedDiagnosticEx(Diagnostic.WARNING,
								message, object, offset, length, CheckType.FAST, code, issueData
							));
						}
					})

				val Map<Object, Object> validationContext = newHashMap
				validationContext.put(AbstractInjectableValidator.CURRENT_LANGUAGE_NAME,
					ReflectionUtils.getPrivateFieldValue(validator, "languageName"))

				// put attribute context information into validation context
				validationContext.put(Context.getName, attributeContext)

				// validate the root element...
				validator.validate(attributeValue, null/* diagnostic chain */, validationContext)

				// ...and all its children
				val Iterator<EObject> iterator = attributeValue.getAllProperContents(true)
				while (iterator.hasNext) {
					validator.validate(iterator.next, null/* diagnostic chain */, validationContext)
				}
			}

			diagnostics
		}
	}

	/**
	 * Parses the given value as a DOT dirType.
	 */
	static val DIRTYPE_PARSER = new EnumParser<DirType>(DirType)

	/**
	 * A serializer for {@link DirType} values.
	 */
	static val DIRTYPE_SERIALIZER = new EnumSerializer<DirType>(DirType)

	/**
	 * Parses the given value as a DOT dirType.
	 */
	static val LAYOUT_PARSER = new EnumParser<Layout>(Layout)

	/**
	 * A serializer for {@link DirType} values.
	 */
	static val LAYOUT_SERIALIZER = new EnumSerializer<Layout>(Layout)

	/**
	 * Parses the given value as a {@link ClusterMode}.
	 */
	static val CLUSTERMODE_PARSER = new EnumParser<ClusterMode>(ClusterMode)

	/**
	 * Serializes the given {@link ClusterMode} value.
	 */
	static val CLUSTERMODE_SERIALIZER = new EnumSerializer<ClusterMode>(ClusterMode)

	/**
	 * Parses the given value as a DOT outputMode.
	 */
	static val OUTPUTMODE_PARSER = new EnumParser<OutputMode>(OutputMode)

	/**
	 * Serializes the given {@link OutputMode} value.
	 */
	static val OUTPUTMODE_SERIALIZER = new EnumSerializer<OutputMode>(OutputMode)

	/**
	 * Parses the given value as a DOT pagedir.
	 */
	static val PAGEDIR_PARSER = new EnumParser<Pagedir>(Pagedir)

	/**
	 * Serializes the given {@link Pagedir} value.
	 */
	static val PAGEDIR_SERIALIZER = new EnumSerializer<Pagedir>(Pagedir)

	/**
	 * A parser used to parse DOT rankdir values.
	 */
	static val RANKDIR_PARSER = new EnumParser<Rankdir>(Rankdir)

	/**
	 * Serializes the given {@link Rankdir} value.
	 */
	static val RANKDIR_SERIALIZER = new EnumSerializer<Rankdir>(Rankdir)

	/**
	 * Parses the given value as a DOT rankType.
	 */
	static val RANKTYPE_PARSER = new EnumParser<RankType>(RankType)

	/**
	 * A serializer for {@link RankType} values.
	 */
	static val RANKTYPE_SERIALIZER = new EnumSerializer<RankType>(RankType)

	/**
	 * A parser used to parse DOT {@link Splines} values.
	 */
	static val SPLINES_PARSER = new IAttributeValueParser<Splines>() {

		val enumParser = new EnumParser<Splines>(Splines)

		override IAttributeValueParser.ParseResult<Splines> parse(String attributeValue) {

			// XXX: splines can either be an enum or a bool value; we try both
			// options here and convert boolean expressions into respective
			// splines
			val IAttributeValueParser.ParseResult<Boolean> boolResult = BOOL_PARSER.parse(attributeValue)
			if (!boolResult.hasSyntaxErrors) {
				return new IAttributeValueParser.ParseResult<Splines>(if(boolResult.getParsedValue) Splines.TRUE else Splines.FALSE)
			}
			val IAttributeValueParser.ParseResult<Splines> enumResult = enumParser.parse(attributeValue)
			if (!enumResult.hasSyntaxErrors) {
				return new IAttributeValueParser.ParseResult<Splines>(enumResult.getParsedValue)
			}

			// TODO: create a better, combined error message here
			val List<Diagnostic> combinedFindings = newArrayList
			combinedFindings.addAll(boolResult.syntaxErrors)
			combinedFindings.addAll(enumResult.syntaxErrors)
			return new IAttributeValueParser.ParseResult<Splines>(combinedFindings)
		}

		override getParsedType() {
			Splines
		}

	}

	/**
	 * Serializes the given {@link Splines} value.
	 */
	static val SPLINES_SERIALIZER = new EnumSerializer<Splines>(Splines)

	/**
	 * A parser for bool values.
	 */
	static val BOOL_PARSER = new IAttributeValueParser<Boolean> {

		override IAttributeValueParser.ParseResult<Boolean> parse(String rawValue) {
			if (rawValue === null) {
				return null
			}

			// case insensitive "true" or "yes"
			if (Boolean.TRUE.toString.equalsIgnoreCase(rawValue) || "yes".equalsIgnoreCase(rawValue)) {
				return new IAttributeValueParser.ParseResult<Boolean>(Boolean.TRUE)
			}

			// case insensitive "false" or "no"
			if (Boolean.FALSE.toString.equalsIgnoreCase(rawValue) || "no".equalsIgnoreCase(rawValue)) {
				return new IAttributeValueParser.ParseResult<Boolean>(Boolean.FALSE)
			}

			// an integer value
			try {
				val int parsedValue = Integer.parseInt(rawValue)
				return new IAttributeValueParser.ParseResult<Boolean>(if(parsedValue > 0) Boolean.TRUE else Boolean.FALSE)
			} catch (NumberFormatException e) {
				return new IAttributeValueParser.ParseResult<Boolean>(
					Collections.<Diagnostic>singletonList(
						new BasicDiagnostic(Diagnostic.ERROR, rawValue, -1,
							"The given value '" + rawValue +
								"' does not (case-insensitively) equal 'true', 'yes', 'false', or 'no' and is also not parsable as an integer value",
							#[])))
			}
		}

		override getParsedType() {
			Boolean
		}

	}

	/**
	 * A serializer for bool values.
	 */
	static val BOOL_SERIALIZER = new IAttributeValueSerializer<Boolean> {

		override String serialize(Boolean value) {
			return Boolean.toString(value)
		}
	}

	/**
	 * A parser for double values.
	 */
	static val DOUBLE_PARSER = new IAttributeValueParser<Double> {

		override IAttributeValueParser.ParseResult<Double> parse(String rawValue) {
			if (rawValue === null) {
				return null
			}
			try {
				val Double parsedValue = DotDoubleUtil.parseDotDouble(rawValue)
				return new IAttributeValueParser.ParseResult<Double>(parsedValue)
			} catch (NumberFormatException exception) {
				return new IAttributeValueParser.ParseResult<Double>(
					Collections.<Diagnostic>singletonList(
						new BasicDiagnostic(Diagnostic.ERROR, rawValue, -1, exception.getMessage, #[])))
			}
		}

		override getParsedType() {
			Double
		}

	}

	/**
	 * A serializer for double values.
	 */
	static val DOUBLE_SERIALIZER = new IAttributeValueSerializer<Double> {

		override String serialize(Double value) {
			Double.toString(value)
		}
	}

	/**
	 * A parser used to parse DOT int values.
	 */
	static val INT_PARSER = new IAttributeValueParser<Integer> {

		override IAttributeValueParser.ParseResult<Integer> parse(String rawValue) {
			if (rawValue === null) {
				return null
			}
			try {
				val int parsedValue = Integer.parseInt(rawValue)
				return new IAttributeValueParser.ParseResult<Integer>(new Integer(parsedValue))
			} catch (NumberFormatException exception) {
				return new IAttributeValueParser.ParseResult<Integer>(
					Collections.<Diagnostic>singletonList(
						new BasicDiagnostic(Diagnostic.ERROR, rawValue, -1, exception.getMessage, #[])))
			}
		}

		override getParsedType() {
			Integer
		}

	}

	/**
	 * A serializer for int values.
	 */
	static val INT_SERIALIZER = new IAttributeValueSerializer<Integer> {

		override String serialize(Integer value) {
			return Integer.toString(value)
		}
	}

	/**
	 * A validator for colorscheme {@link String} attribute values.
	 */
	static val COLORSCHEME_VALIDATOR = new StringValidator(DotColors.getColorSchemes.toArray)

	/**
	 * A validator for sides {@link Integer} attribute values.
	 */
	static val SIDES_VALIDATOR = new IntValidator(0)

	/**
	 * A validator for arrowsize {@link Double} attribute values.
	 */
	static val ARROWSIZE_VALIDATOR = new DoubleValidator(0.0)

	/**
	 * A validator for skew {@link Double} attribute values.
	 */
	static val SKEW_VALIDATOR = new DoubleValidator(-100.0)

	/**
	 * A validator for distortion {@link Double} attribute values.
	 */
	static val DISTORTION_VALIDATOR = new DoubleValidator(-100.0)

	/**
	 * A validator for width {@link Double} attribute values.
	 */
	static val WIDTH_VALIDATOR = new DoubleValidator(0.00)

	/**
	 * A validator for height {@link Double} attribute values.
	 */
	static val HEIGHT_VALIDATOR = new DoubleValidator(0.00)

	/**
	 * A validator for nodesep {@link Double} attribute values.
	 */
	static val NODESEP_VALIDATOR = new DoubleValidator(0)

	/**
	 * The validator for fontsize {@link Double} attribute values.
	 */
	static val FONTSIZE_VALIDATOR = new DoubleValidator(1.0)

	/**
	 * The validator for pendwidth {@link Double} attribute values.
	 */
	static val PENWIDTH_VALIDATOR = new DoubleValidator(0.0)

	static val Injector arrowTypeInjector = new DotArrowTypeStandaloneSetup().
		createInjectorAndDoEMFRegistration

	/**
	 * The validator for arrowtype attribute values.
	 */
	// TODO: move to DotValidator
	static val ARROWTYPE_VALIDATOR = new EObjectValidator<ArrowType>(arrowTypeInjector,
		DotArrowTypeValidator)

	/**
	 * The parser for arrowtype attribute values.
	 */
	static val ARROWTYPE_PARSER = new EObjectParser<ArrowType>(arrowTypeInjector)

	/**
	 * The serializer for arrowtype attribute values.
	 */
	static val ARROWTYPE_SERIALIZER = new EObjectSerializer<ArrowType>(arrowTypeInjector)

	static val Injector colorInjector = new DotColorStandaloneSetup().createInjectorAndDoEMFRegistration

	/**
	 * The parser for color attribute values.
	 */
	static val COLOR_PARSER = new EObjectParser<Color>(colorInjector)

	/**
	 * The serializer for color attribute values.
	 */
	static val COLOR_SERIALIZER = new EObjectSerializer<Color>(colorInjector)

	static val Injector colorListInjector = new DotColorListStandaloneSetup().createInjectorAndDoEMFRegistration

	/**
	 * The parser for colorlist attribute values.
	 */
	static val COLORLIST_PARSER = new EObjectParser<ColorList>(colorListInjector)

	/**
	 * The serializer for colorlist attribute values.
	 */
	static val COLORLIST_SERIALIZER = new EObjectSerializer<ColorList>(colorListInjector)

	static val COLORLIST_VALIDATOR = new EObjectValidator<ColorList>(colorListInjector,
		DotColorListValidator)

	static val Injector htmlLabelInjector = new DotHtmlLabelStandaloneSetup().createInjectorAndDoEMFRegistration

	/**
	 * The parser for (html) label attribute values.
	 */
	static val HTML_LABEL_PARSER = new EObjectParser<HtmlLabel>(htmlLabelInjector)

	static val HTML_LABEL_VALIDATOR = new EObjectValidator<HtmlLabel>(htmlLabelInjector,
		DotHtmlLabelValidator)

	static val Injector escStringInjector = new DotEscStringStandaloneSetup().createInjectorAndDoEMFRegistration

	/**
	 * The parser for (escString) label attribute values.
	 */
	static val ESCSTRING_PARSER = new EObjectParser<EscString>(escStringInjector)

	/**
	 * The serializer for escstring attribute values.
	 */
	static val ESCSTRING_SERIALIZER = new EObjectSerializer<EscString>(escStringInjector)

	static val ESCSTRING_VALIDATOR = new EObjectValidator<EscString>(escStringInjector,
		DotEscStringValidator)

	static val Injector fontNameInjector = new DotFontNameStandaloneSetup().createInjectorAndDoEMFRegistration

	/**
	 * The parser for fontname attribute values.
	 */
	static val FONTNAME_PARSER = new EObjectParser<FontName>(fontNameInjector)

	/**
	 * The serializer for fontname attribute values.
	 */
	static val FONTNAME_SERIALIZER = new EObjectSerializer<FontName>(fontNameInjector)

	static val Injector rectInjector = new DotRectStandaloneSetup().createInjectorAndDoEMFRegistration

	/**
	 * The parser for rect attribute values.
	 */
	static val RECT_PARSER = new EObjectParser<Rect>(rectInjector)

	/**
	 * The serializer for rect attribute values.
	 */
	static val RECT_SERIALIZER = new EObjectSerializer<Rect>(rectInjector)

	static val Injector pointInjector = new DotPointStandaloneSetup().createInjectorAndDoEMFRegistration

	/**
	 * The parser for point attribute values.
	 */
	static val POINT_PARSER = new EObjectParser<Point>(pointInjector)

	/**
	 * The serializer for point attribute values.
	 */
	static val POINT_SERIALIZER = new EObjectSerializer<Point>(pointInjector)

	static val Injector shapeInjector = new DotShapeStandaloneSetup().createInjectorAndDoEMFRegistration

	/**
	 * The parser for shape attribute values.
	 */
	static val SHAPE_PARSER = new EObjectParser<Shape>(shapeInjector)

	/**
	 * The serializer for shape attribute values.
	 */
	static val SHAPE_SERIALIZER = new EObjectSerializer<Shape>(shapeInjector)

	static val Injector splineTypeInjector = new DotSplineTypeStandaloneSetup().
		createInjectorAndDoEMFRegistration

	/**
	 * The parser for splinetype attribute values.
	 */
	static val SPLINETYPE_PARSER = new EObjectParser<SplineType>(splineTypeInjector)

	/**
	 * The serializer for splinetype attribute values.
	 */
	static val SPLINETYPE_SERIALIZER = new EObjectSerializer<SplineType>(splineTypeInjector)

	static val Injector styleInjector = new DotStyleStandaloneSetup().createInjectorAndDoEMFRegistration

	/**
	 * The serializer for style attribute values.
	 */
	static val STYLE_SERIALIZER = new EObjectSerializer<Style>(styleInjector)

	/**
	 * The parser for style attribute values.
	 */
	static val STYLE_PARSER = new EObjectParser<Style>(styleInjector)

	/**
	 * Validator for Color types.
	 */
	static val COLOR_VALIDATOR = new EObjectValidator<Color>(colorInjector, DotColorValidator)

	/**
	 * Validator for SplineType types.
	 */
	static val SPLINETYPE_VALIDATOR = new EObjectValidator<SplineType>(splineTypeInjector,
		DotSplineTypeValidator)

	/**
	 * Validator for Point types.
	 */
	static val POINT_VALIDATOR = new EObjectValidator<Point>(pointInjector, DotPointValidator)

	/**
	 * Validator for Rect types.
	 */
	static val RECT_VALIDATOR = new EObjectValidator<Rect>(rectInjector, DotRectValidator)

	/**
	 * Validator for Shape types.
	 */
	static val SHAPE_VALIDATOR = new EObjectValidator<Shape>(shapeInjector, DotShapeValidator)

	/**
	 * Validator for Style types.
	 */
	static val STYLE_VALIDATOR = new EObjectValidator<Style>(styleInjector, DotStyleValidator)

	static val Injector portPosInjector = new DotPortPosStandaloneSetup().createInjectorAndDoEMFRegistration

	/**
	 * The validator for portpos attribute values.
	 */
	static val PORTPOS_VALIDATOR = new EObjectValidator<PortPos>(portPosInjector,
		DotPortPosValidator)

	/**
	 * The parser for portpos attribute values.
	 */
	static val PORTPOS_PARSER = new EObjectParser<PortPos>(portPosInjector)

	/**
	 * The serializer for portpos attribute values.
	 */
	static val PORTPOS_SERIALIZER = new EObjectSerializer<PortPos>(portPosInjector)

	/**
	 * Specifies the name of a graph, node, or edge (not an attribute), as
	 * retrieved through the graph, node_id, as well as edge_stmt and edgeRHS
	 * grammar rules.
	 */
	public static val String _NAME__GNE = "_name"

	/**
	 * Specifies the graph type.
	 */
	public static val String _TYPE__G = "_type"

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
	static def ID _getNameRaw(Graph graph) {
		graph.attributesProperty.get(_NAME__GNE) as ID
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
	static def String _getName(Graph graph) {
		val ID _nameRaw = _getNameRaw(graph)
		if(_nameRaw !== null) _nameRaw.toValue else null
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
	static def ID _getNameRaw(Node node) {
		node.attributesProperty.get(_NAME__GNE) as ID
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
	static def String _getName(Node node) {
		val ID _nameRaw = _getNameRaw(node)
		if(_nameRaw !== null) _nameRaw.toValue else null
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
	static def GraphType _getType(Graph graph) {
		graph.attributesProperty.get(_TYPE__G) as GraphType
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
	static def void _setNameRaw(Graph graph, ID name) {
		graph.attributesProperty.put(_NAME__GNE, name)
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
	static def void _setName(Graph graph, String name) {
		_setNameRaw(graph, ID.fromValue(name))
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
	static def void _setNameRaw(Node node, ID name) {
		node.attributesProperty.put(_NAME__GNE, name)
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
	static def void _setName(Node node, String name) {
		_setNameRaw(node, ID.fromValue(name))
	}

	/**
	 * Returns the value of the {@link #_NAME__GNE} attribute of the given
	 * {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #_NAME__GNE} attribute.
	 * @return The value of the {@link #_NAME__GNE} attribute of the given
	 *         {@link Edge}.
	 */
	static def String _getName(Edge edge) {
		edge.source._getName +
			(
				switch edge.graph?.rootGraph?._getType {
					case GRAPH: EdgeOp.UNDIRECTED.literal
					case DIGRAPH: EdgeOp.DIRECTED.literal
				}
			)
		+ edge.target._getName
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
	 */
	static def void _setType(Graph graph, GraphType type) {
		graph.attributesProperty.put(_TYPE__G, type)
	}

	@DotAttribute(rawType="STRING", parsedType=ArrowType)
	public static val ARROWHEAD__E = "arrowhead"

	@DotAttribute(rawType="NUMERAL", parsedType=Double)
	public static val ARROWSIZE__E = "arrowsize"

	@DotAttribute(rawType="STRING", parsedType=ArrowType)
	public static val ARROWTAIL__E = "arrowtail"

	@DotAttribute(parsedType=Rect)
	public static val BB__GC = "bb"

	@DotAttribute(parsedType=ColorList)
	public static val BGCOLOR__GC = "bgcolor"

	@DotAttribute(rawType="STRING", parsedType=ClusterMode)
	public static val CLUSTERRANK__G = "clusterrank"

	/**
	 * color is a special case, where different parsed values for Cluster,
	 * Node and Edge attributes (Color, Color, ColorList) and thus different
	 * parsers and serializers are required.
	 */
	@DotAttribute(parsedType=#[Color, Color, ColorList])
	public static val COLOR__CNE = "color"

	@DotAttribute(parsedType=String)
	public static val COLORSCHEME__GCNE = "colorscheme"

	@DotAttribute(rawType="STRING", parsedType=DirType)
	public static val DIR__E = "dir"

	@DotAttribute(rawType="NUMERAL", parsedType=Double)
	public static val DISTORTION__N = "distortion"

	@DotAttribute(parsedType=EscString)
	public static val EDGETOOLTIP__E = "edgetooltip"

	/**
	 * fillcolor is a special case, where different parsed values for Cluster,
	 * Node and Edge attributes (ColorList, ColorList, Color) and thus different
	 * parsers and serializers are required.
	 */
	@DotAttribute(parsedType=#[ColorList, ColorList, Color])
	public static val FILLCOLOR__CNE = "fillcolor"

	@DotAttribute(rawType="STRING", parsedType=Boolean)
	public static val FIXEDSIZE__N = "fixedsize"

	@DotAttribute(parsedType=Color)
	public static val FONTCOLOR__GCNE = "fontcolor"

	@DotAttribute(parsedType=FontName)
	public static val FONTNAME__GCNE = "fontname"

	@DotAttribute(rawType="NUMERAL", parsedType=Double)
	public static val FONTSIZE__GCNE = "fontsize"

	@DotAttribute(rawType="STRING", parsedType=Boolean)
	public static val FORCELABELS__G = "forcelabels"

	@DotAttribute(rawType="QUOTED_STRING", parsedType=Point)
	public static val HEAD_LP__E = "head_lp"

	@DotAttribute(parsedType=String) // TODO: change to lblString
	public static val HEADLABEL__E = "headlabel"

	@DotAttribute(parsedType=PortPos)
	public static val HEADPORT__E = "headport"

	@DotAttribute(parsedType=EscString)
	public static val HEADTOOLTIP__E = "headtooltip"

	@DotAttribute(rawType="NUMERAL", parsedType=Double)
	public static val HEIGHT__N = "height"

	@DotAttribute(parsedType=String) // TODO: change to escString
	public static val ID__GCNE = "id"

	@DotAttribute(parsedType=String) // TODO: change to lblString
	public static val LABEL__GCNE = "label"

	@DotAttribute(parsedType=Color)
	public static val LABELFONTCOLOR__E = "labelfontcolor"

	@DotAttribute(parsedType=FontName)
	public static val LABELFONTNAME__E = "labelfontname"

	@DotAttribute(rawType="NUMERAL", parsedType=Double)
	public static val LABELFONTSIZE__E = "labelfontsize"

	@DotAttribute(parsedType=EscString)
	public static val LABELTOOLTIP__E = "labeltooltip"

	@DotAttribute(rawType="STRING", parsedType=Layout)
	public static val LAYOUT__G = "layout"

	@DotAttribute(rawType="QUOTED_STRING", parsedType=Point)
	public static val LP__GCE = "lp"

	@DotAttribute(rawType="NUMERAL", parsedType=Double)
	public static val NODESEP__G = "nodesep"

	@DotAttribute(rawType="STRING", parsedType=OutputMode)
	public static val OUTPUTORDER__G = "outputorder"

	@DotAttribute(rawType="STRING", parsedType=Pagedir)
	public static val PAGEDIR__G = "pagedir"

	@DotAttribute(rawType="NUMERAL", parsedType=Double)
	public static val PENWIDTH__CNE = "penwidth"

	/**
	 * pos is a special case, where different parsed values for Node and Edge
	 * attributes (Point, SplineType) and thus different parsers and serializers
	 * are required.
	 */
	@DotAttribute(rawType="QUOTED_STRING", parsedType=#[Point, SplineType])
	public static val POS__NE = "pos"

	@DotAttribute(rawType="STRING", parsedType=RankType)
	public static val RANK__S = "rank"

	@DotAttribute(rawType="STRING", parsedType=Rankdir)
	public static val RANKDIR__G = "rankdir"

	@DotAttribute(parsedType=Shape)
	public static val SHAPE__N = "shape"

	@DotAttribute(rawType="NUMERAL", parsedType=Integer)
	public static val SIDES__N = "sides"

	@DotAttribute(rawType="NUMERAL", parsedType=Double)
	public static val SKEW__N = "skew"

	@DotAttribute(rawType="QUOTED_STRING", parsedType=Splines)
	public static val SPLINES__G = "splines"

	@DotAttribute(parsedType=Style)
	public static val STYLE__GCNE = "style"

	@DotAttribute(rawType="QUOTED_STRING", parsedType=Point)
	public static val TAIL_LP__E = "tail_lp"

	@DotAttribute(parsedType=String) // TODO: change to lblString
	public static val TAILLABEL__E = "taillabel"

	@DotAttribute(parsedType=PortPos)
	public static val TAILPORT__E = "tailport"

	@DotAttribute(parsedType=EscString)
	public static val TAILTOOLTIP__E = "tailtooltip"

	@DotAttribute(parsedType=EscString)
	public static val TOOLTIP__CNE = "tooltip"

	@DotAttribute(rawType="NUMERAL", parsedType=Double)
	public static val WIDTH__N = "width"

	@DotAttribute(parsedType=String) // TODO: change to lblString
	public static val XLABEL__NE = "xlabel"

	@DotAttribute(rawType="QUOTED_STRING", parsedType=Point)
	public static val XLP__NE = "xlp"

	//method body is generated using @DotAttribute active annotation
	static def Object parsedAsAttribute(ID valueRaw, String attrName, Context context){}

	static def parsed(Attribute attr) {
		if (attr !== null) parsedAsAttribute(attr.value, attr.name.toValue, getContext(attr))
	}
}
