/*******************************************************************************
 * Copyright (c) 2018, 2019 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG)     - initial API and implementation
 *     Zoey Gerrit Prigge (itemis AG) - additional test cases
 *
 *******************************************************************************/
package org.eclipse.gef.dot.tests;

import com.google.inject.Inject
import javafx.beans.property.ReadOnlyMapProperty
import javafx.scene.Group
import javafx.scene.shape.Shape
import org.eclipse.gef.dot.internal.DotImport
import org.eclipse.gef.dot.internal.language.DotInjectorProvider
import org.eclipse.gef.dot.internal.language.dot.DotAst
import org.eclipse.gef.dot.internal.ui.DotNodePart
import org.eclipse.gef.dot.internal.ui.conversion.Dot2ZestGraphCopier
import org.eclipse.gef.fx.nodes.GeometryNode
import org.eclipse.gef.graph.Edge
import org.eclipse.gef.graph.Graph
import org.eclipse.gef.graph.Node
import org.eclipse.gef.zest.fx.ZestProperties
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import static extension org.junit.Assert.*

/*
 * Test class containing test cases for the {@link Dot2ZestGraphCopier} class.
 */
@RunWith(XtextRunner)
@InjectWith(DotInjectorProvider)
class Dot2ZestGraphCopierTest {

	@Rule public val rule = new DotSubgrammarPackagesRegistrationRule

	/**
	 * Ensure the JavaFX toolkit is properly initialized.
	 */
	//@Rule
	//public FXNonApplicationThreadRule ctx = new FXNonApplicationThreadRule

	@Inject extension ParseHelper<DotAst>
	@Inject extension ValidationTestHelper

	static extension DotImport dotImport
	static extension Dot2ZestGraphCopier dot2ZestGraphCopier

	@BeforeClass
	def static void beforeClass() {
		dotImport = new DotImport
		
		dot2ZestGraphCopier = new Dot2ZestGraphCopier
		dot2ZestGraphCopier.attributeCopier.options.emulateLayout = false
	}

	@Test def node_xlabel() {
		'''
			graph {
				1[xlabel=foo]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-external-label : foo
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	//@Ignore("Failing on Travis/Jenkins")
	@Test def node_xlabel_with_layout_information() {
		'''
			graph {
				graph [bb="0,51,81,0"];
				node [label="\N"];
				1	 [height=0.5,
					pos="54,33",
					width=0.75,
					xlabel=foo,
					xlp="13.5,7.5"];
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-external-label : foo
					element-external-label-position : Point(3.467529296875, -0.1552734375)
					element-label : 1
					element-layout-irrelevant : false
					node-position : Point(27.0, 15.0)
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	private def assertZestConversion(CharSequence it, CharSequence expectedZestGraphText) {
		assertZestConversion(new DotGraphPrettyPrinter, expectedZestGraphText)
	}

	/**
	 * use a customized {@link DotGraphPrettyPrinter} to provide a better formatted string representation of certain attributes property
	 */
	private def assertZestConversion(CharSequence it, DotGraphPrettyPrinter prettyPrinter, CharSequence expectedZestGraphText) {
		// ensure that the input text can be parsed and the ast can be created
		val dotAst = parse
		dotAst.assertNoErrors
		
		val dotGraph = dotAst.importDot.get(0)
		val zestGraph = dotGraph.copy
		zestGraph.test(prettyPrinter, expectedZestGraphText)
	}

	private def test(Graph actual, CharSequence expected) {
		test(actual, new DotGraphPrettyPrinter, expected)
	}

	private def test(Graph actual, extension DotGraphPrettyPrinter prettyPrinter, CharSequence expected) {
		// compare the string representation removing the objectIDs
		expected.toString.assertEquals(actual.prettyPrint.removeObjectIDs)
	}

	private def test(Node actual, extension DotGraphPrettyPrinter prettyPrinter, CharSequence expected) {
		// compare the string representation removing the objectIDs
		expected.toString.assertEquals(actual.prettyPrint.removeObjectIDs)
	}

	private def test(Edge actual, extension DotGraphPrettyPrinter prettyPrinter, CharSequence expected) {
		// compare the string representation removing the objectIDs
		expected.toString.assertEquals(actual.prettyPrint.removeObjectIDs)
	}

	private def removeObjectIDs(String text) {
		// recognize substrings between '@' and the end of the line
		val nl = System.lineSeparator
		val regex = '''(@[^\\«nl»]*)'''
		
		text.replaceAll(regex, "")
	}

	private def mockAvailableFonts(String... availableFonts) {
		dot2ZestGraphCopier.attributeCopier.fontUtil.systemFontAccess = new DotFontAccessMock(availableFonts)
	}

	private static class NodeShapePrettyPrinter extends DotGraphPrettyPrinter {
		override protected prettyPrint(String attrKey, Object attrValue) {
			return if (#[
				DotNodePart.DOT_PROPERTY_INNER_SHAPE__N,
				ZestProperties.SHAPE__N
			].contains(attrKey) && attrValue instanceof GeometryNode<?>) {
				val node = attrValue as GeometryNode<?>
				'''«attrKey» : «node.geometryProperty.get»'''
			} else {
				super.prettyPrint(attrKey, attrValue)
			}
		}
	}

	private static class NodeShapeWithStylePrettyPrinter extends DotGraphPrettyPrinter {
		override protected prettyPrint(String attrKey, Object attrValue) {
			return if (#[
				DotNodePart.DOT_PROPERTY_INNER_SHAPE__N,
				ZestProperties.SHAPE__N
			].contains(attrKey) && attrValue instanceof GeometryNode<?>) {
				val node = attrValue as GeometryNode<?>
				'''«attrKey» : «node.geometryProperty.get», style: «node.style»'''
			} else {
				super.prettyPrint(attrKey, attrValue)
			}
		}
	}

	private static class EdgeDecorationPrettyPrinter extends DotGraphPrettyPrinter {
		String startIndent
		
		override protected String prettyPrint(
			ReadOnlyMapProperty<String, Object> attributesProperty,
			String startIndent) {
				this.startIndent = startIndent
				super.prettyPrint(attributesProperty, startIndent)
		}
		
		override protected prettyPrint(String attrKey, Object attrValue) {
			return if (#[
				ZestProperties.SOURCE_DECORATION__E,
				ZestProperties.TARGET_DECORATION__E
			].contains(attrKey)) {
				switch attrValue {
					Shape : '''«super.prettyPrint(attrKey, attrValue)», style: «attrValue.style»'''
					Group : '''«attrKey» : «attrValue.prettyPrint»''' 
				}
			} else {
				super.prettyPrint(attrKey, attrValue)
			}
		}
		
		private def prettyPrint(Group it) '''
			Group[
				«FOR child : children»
					«startIndent»«child», style: «child.style»
				«ENDFOR»
			«startIndent»]'''
	}
}