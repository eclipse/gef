/*******************************************************************************
 * Copyright (c) 2009, 2018 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Fabian Steeg - initial API and implementation (see bug #277380)
 *     Tamas Miklossy (itemis AG) - Add support for all dot attributes (bug #461506)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import java.io.File
import org.eclipse.core.resources.IContainer
import org.eclipse.core.resources.IFolder
import org.eclipse.core.resources.IProject
import org.eclipse.core.resources.ResourcesPlugin
import org.eclipse.core.runtime.IPath
import org.eclipse.core.runtime.IProgressMonitor
import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.core.runtime.Path
import org.eclipse.gef.dot.internal.language.dot.GraphType
import org.eclipse.gef.dot.internal.language.layout.Layout
import org.eclipse.gef.dot.internal.language.style.EdgeStyle
import org.eclipse.gef.graph.Edge
import org.eclipse.gef.graph.Graph
import org.eclipse.gef.graph.Node
import org.eclipse.ui.actions.WorkspaceModifyOperation
import org.eclipse.xtext.junit4.ui.util.IResourcesSetupUtil
import org.eclipse.xtext.ui.XtextProjectHelper
import org.eclipse.xtext.util.StringInputStream

import static extension org.eclipse.gef.dot.internal.DotAttributes.*
import static extension org.eclipse.gef.dot.internal.DotFileUtils.read
import static extension org.eclipse.xtext.junit4.ui.util.IResourcesSetupUtil.addNature
import static extension org.eclipse.xtext.junit4.ui.util.IResourcesSetupUtil.createProject

/** 
 * Util class for different tests.
 * @author Fabian Steeg (fsteeg)
 */
final class DotTestUtils {

	private new() {
		/* Enforce non-instantiability */
	}

	def static content(String fileName) {
		fileName.file.read
	}

	def static file(String fileName) {
		new File("resources/" + fileName)
	}

	def static getLabeledGraph() {
		val graph = new Graph.Builder().
			attr([p1,p2|p1._setName(p2)], "LabeledGraph").
			attr([p1,p2|p1._setType(p2)], GraphType.DIGRAPH)
		
		val n1 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "1").attr([p1,p2|p1.label=p2], 'one "1"').buildNode
		val n2 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "2").attr([p1,p2|p1.label=p2], "two").buildNode
		val n3 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "3").buildNode
		val n4 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "4").buildNode
		
		val e1 = new Edge.Builder(n1, n2).attr([p1,p2|p1.label=p2], "+1").buildEdge
		val e2 = new Edge.Builder(n1, n3).attr([p1,p2|p1.label=p2], "+2").buildEdge
		val e3 = new Edge.Builder(n3, n4).buildEdge
		
		graph.nodes(n1, n2, n3, n4).edges(e1, e2, e3).build
	}

	def static getSimpleDiGraph() {
		val graph = new Graph.Builder().
			attr([p1,p2|p1._setName(p2)], "SimpleDigraph").
			attr([p1,p2|p1._setType(p2)], GraphType.DIGRAPH)
		
		val n1 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "1").buildNode
		val n2 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "2").buildNode
		val n3 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "3").buildNode
		
		val e1 = new Edge.Builder(n1, n2).buildEdge
		val e2 = new Edge.Builder(n2, n3).buildEdge
		
		graph.nodes(n1, n2, n3).edges(e1, e2).build
	}

	def static getSimpleGraph() {
		val graph = new Graph.Builder().
			attr([p1,p2|p1._setName(p2)], "SimpleGraph").
			attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		
		val n1 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "1").buildNode
		val n2 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "2").buildNode
		val n3 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "3").buildNode
		
		val e1 = new Edge.Builder(n1, n2).buildEdge
		val e2 = new Edge.Builder(n1, n3).buildEdge
		
		graph.nodes(n1, n2, n3).edges(e1, e2).build
	}

	def static getStyledGraph() {
		val graph = new Graph.Builder().
			attr([p1,p2|p1._setName(p2)], "StyledGraph").
			attr([p1,p2|p1._setType(p2)], GraphType.DIGRAPH).
			attr([p1,p2|p1.layoutParsed=p2], Layout.DOT)

		val n1 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "1").buildNode
		val n2 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "2").buildNode
		val n3 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "3").buildNode
		val n4 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "4").buildNode
		val n5 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "5").buildNode

		val e1 = new Edge.Builder(n1, n2).attr([p1,p2|p1.style=p2], EdgeStyle.DASHED.toString).buildEdge
		val e2 = new Edge.Builder(n2, n3).attr([p1,p2|p1.style=p2], EdgeStyle.DOTTED.toString).buildEdge
		val e3 = new Edge.Builder(n3, n4).attr([p1,p2|p1.style=p2], EdgeStyle.DASHED.toString).buildEdge
		val e4 = new Edge.Builder(n3, n5).attr([p1,p2|p1.style=p2], EdgeStyle.DASHED.toString).buildEdge
		val e5 = new Edge.Builder(n4, n5).attr([p1,p2|p1.style=p2], EdgeStyle.SOLID.toString).buildEdge

		graph.nodes(n1, n2, n3, n4, n5).edges(e1, e2, e3, e4, e5).build
	}

	def static getClusteredGraph() {
		/*
		 * digraph {
		 * 	subgraph cluster1 { a; b; a -> b; }
		 * 	subgraph cluster2 {
		 * 		p; q; r; s; t;
		 * 		p -> q; q -> r; r -> s; s -> t; t -> p;
		 * 	}
		 * 	b -> q; t -> a;
		 * }
		 */
		val graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.DIGRAPH)

		val cluster1 = new Node.Builder().buildNode
		val a = new Node.Builder().attr([p1,p2|p1._setName(p2)], "a").buildNode
		val b = new Node.Builder().attr([p1,p2|p1._setName(p2)], "b").buildNode
		cluster1.nestedGraph = new Graph.Builder().attr([p1,p2|p1._setName(p2)], "cluster1").
			nodes(a, b).
			edge(a, b).
		build

		val cluster2 = new Node.Builder().buildNode
		val p = new Node.Builder().attr([p1,p2|p1._setName(p2)], "p").buildNode
		val q = new Node.Builder().attr([p1,p2|p1._setName(p2)], "q").buildNode
		val r = new Node.Builder().attr([p1,p2|p1._setName(p2)], "r").buildNode
		val s = new Node.Builder().attr([p1,p2|p1._setName(p2)], "s").buildNode
		val t = new Node.Builder().attr([p1,p2|p1._setName(p2)], "t").buildNode
		cluster2.nestedGraph = 
			new Graph.Builder().attr([p1,p2|p1._setName(p2)], "cluster2").
			nodes(p, q, r, s, t).
			edge(p, q).edge(q, r).edge(r, s).edge(s, t).edge(t, p).
		build
		
		graph.nodes(cluster1, cluster2).edge(b, q).edge(t, a).build
	}

	def static getNestedClusteredGraph() {
		val cluster1_1 = new Node.Builder().buildNode
		val a = new Node.Builder().attr([p1,p2|p1._setName(p2)], "a").buildNode
		val b = new Node.Builder().attr([p1,p2|p1._setName(p2)], "b").buildNode
		cluster1_1.nestedGraph =
			new Graph.Builder().attr([p1,p2|p1._setName(p2)], "cluster1_1").
			nodes(a, b).
			edge(a, b).
		build

		val cluster1 = new Node.Builder().buildNode
		cluster1.nestedGraph = new Graph.Builder().attr([p1,p2|p1._setName(p2)], "cluster1").
			nodes(cluster1_1).
		build

		val cluster2 = new Node.Builder().buildNode
		val p = new Node.Builder().attr([p1,p2|p1._setName(p2)], "p").buildNode
		val q = new Node.Builder().attr([p1,p2|p1._setName(p2)], "q").buildNode
		val r = new Node.Builder().attr([p1,p2|p1._setName(p2)], "r").buildNode
		val s = new Node.Builder().attr([p1,p2|p1._setName(p2)], "s").buildNode
		val t = new Node.Builder().attr([p1,p2|p1._setName(p2)], "t").buildNode
		cluster2.nestedGraph =
			new Graph.Builder().attr([p1,p2|p1._setName(p2)], "cluster2").
			nodes(p, q, r, s, t).
			edge(p, q).edge(q, r).edge(r, s).edge(s, t).edge(t, p).
		build
		
		new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.DIGRAPH).
			nodes(cluster1, cluster2).
			edge(b, q).edge(t, a).
		build
	}

	/** 
	 * Enhances the graph with additional Dot Attributes such as
	 * default Dot attributes values, position, width, height, bounding box
	 * produced by the Dot Executable.
	 */
	def static getSimpleGraphWithAdditionalInformation() {
		val graph = simpleGraph
		graph.bb = "0,108,126,0"

		val nodes = graph.nodes
		nodes.get(0) => [
			label = "\\N"
			height = "0.5"
			pos = "63,18"
			width = "0.75"
		]
		nodes.get(1) => [
			label = "\\N"
			height = "0.5"
			pos = "27,90"
			width = "0.75"
		]
		nodes.get(2) => [
			label = "\\N"
			height = "0.5"
			pos = "99,90"
			width = "0.75"
		]

		val edges = graph.edges
		edges.get(0).pos = "54.65,35.235 48.835,46.544 41.11,61.563 35.304,72.853"
		edges.get(1).pos = "71.35,35.235 77.165,46.544 84.89,61.563 90.696,72.853"

		graph
	}

	/**
	 * Enhances the graph with additional Dot Attributes such as
	 * default Dot attributes values, position, width, height, bounding box
	 * produced by the Dot Executable.
	 */
	def static getSimpleDiGraphWithAdditionalInformation() {
		val graph = simpleDiGraph
		graph.bb = "0,180,54,0"

		val nodes = graph.nodes
		nodes.get(0) => [
			label = "\\N"
			height = "0.5"
			pos = "27,18"
			width = "0.75"
		]
		nodes.get(1) => [
			label = "\\N"
			height = "0.5"
			pos = "27,90"
			width = "0.75"
		]
		nodes.get(2) => [
			label = "\\N"
			height = "0.5"
			pos = "27,162"
			width = "0.75"
		]

		val edges = graph.edges
		edges.get(0).pos = "e,27,71.896 27,36.303 27,44.017 27,53.288 27,61.888"
		edges.get(1).pos = "e,27,143.9 27,108.3 27,116.02 27,125.29 27,133.89"

		graph
	}

	/**
	 * Enhances the graph with additional Dot Attributes such as
	 * default Dot attributes values, position, width, height, bounding box
	 * produced by the Dot Executable.
	 */
	def static getLabeledGraphWithAdditionalInformation() {
		val graph = labeledGraph
		graph.bb = "0,196,126,0"

		val nodes = graph.nodes
		nodes.get(0) => [
			height = "0.5"
			pos = "54,18"
			width = "1.0652"
		]
		nodes.get(1) => [
			height = "0.5"
			pos = "27,105"
			width = "0.75"
		]
		nodes.get(2) => [
			label = "\\N"
			height = "0.5"
			pos = "99,105"
			width = "0.75"
		]
		nodes.get(3) => [
			label = "\\N"
			height = "0.5"
			pos = "99,178"
			width = "0.75"
		]

		val edges = graph.edges
		edges.get(0) => [
			lp = "50,61.5"
			pos = "e,32.364,87.115 48.536,36.201 44.779,48.03 39.715,63.97 35.442,77.422"
		]
		edges.get(1)  => [
			lp = "87,61.5"
			pos = "e,90.433,87.818 62.891,35.793 69.384,48.058 78.299,64.898 85.646,78.776"
		]
		edges.get(2) => [
			pos = "e,99,159.97 99,123.19 99,131.21 99,140.95 99,149.93"
		]

		graph
	}

	/** 
	 * Enhances the graph with additional Dot Attributes such as
	 * default Dot attributes values, position, width, height, bounding box
	 * produced by the Dot Executable.
	 */
	def static getStyledGraphWithAdditionalInformation() {
		val graph = styledGraph
		graph => [
			bb = "0,324,81,0"
			layout = "dot"
		]

		val nodes = graph.nodes
		nodes.get(0) => [
			label = "\\N"
			height = "0.5"
			pos = "54,18"
			width = "0.75"
		]
		nodes.get(1) => [
			label = "\\N"
			height = "0.5"
			pos = "54,90"
			width = "0.75"
		]
		nodes.get(2) => [
			label = "\\N"
			height = "0.5"
			pos = "54,162"
			width = "0.75"
		]
		nodes.get(3) => [
			label = "\\N"
			height = "0.5"
			pos = "27,234"
			width = "0.75"
		]
		nodes.get(4) => [
			label = "\\N"
			height = "0.5"
			pos = "54,306"
			width = "0.75"
		]

		val edges = graph.edges
		edges.get(0) => [
			pos = "e,54,71.896 54,36.303 54,44.017 54,53.288 54,61.888"
			style = "dashed"
		]
		edges.get(1) => [
			pos = "e,54,143.9 54,108.3 54,116.02 54,125.29 54,133.89"
			style = "dotted"
		]
		edges.get(2) => [
			pos = "e,33.54,216.04 47.601,179.59 44.486,187.66 40.666,197.57 37.165,206.65"
			style = "dashed"
		]
		edges.get(3) => [
			pos = "e,57.654,287.91 57.654,180.09 59.676,190.43 61.981,203.91 63,216 64.344,231.94 64.344,236.06 63,252 62.283,260.5 60.931,269.69 59.488, 277.99"
			style = "dashed"
		]
		edges.get(4) => [
			pos = "e,47.46,288.04 33.399,251.59 36.514,259.66 40.334,269.57 43.835,278.65"
			style = "solid"
		]

		graph
	}

	/**
	 * Enhances the graph with additional Dot Attributes (such as default Dot
	 * attributes values, position, width, height, bounding box) produced by the
	 * Dot Executable.
	 */
	def static getClusteredGraphWithAdditionalInformation() {
		val graph = clusteredGraph
		graph.bb = "0,500,118,0"

		val clusters = graph.nodes
		val cluster1 = clusters.get(0).nestedGraph
		cluster1.bb = "28,492,98,368"

		val cluster1Nodes = cluster1.nodes
		cluster1Nodes.get(0) => [
			label = "\\N"
			height = "0.5"
			pos = "63,394"
			width = "0.75"
		]
		cluster1Nodes.get(1) => [
			setLabel = "\\N"
			setHeight = "0.5"
			setPos = "63,466"
			setWidth = "0.75"
		]

		val cluster1Edges = cluster1.edges
		cluster1Edges.get(0).pos = "e,63,447.9 63,412.3 63,420.02 63,429.29 63,437.89"

		val cluster2 = clusters.get(1).nestedGraph
		cluster2.bb = "8,348,98,8"

		val cluster2Nodes = cluster2.nodes
		cluster2Nodes.get(0) => [
			label = "\\N"
			height = "0.5"
			pos = "53,34"
			width = "0.75"
		]
		cluster2Nodes.get(1) => [
			label = "\\N"
			height = "0.5"
			pos = "63,106"
			width = "0.75"
		]
		cluster2Nodes.get(2) => [
			label = "\\N"
			height = "0.5"
			pos = "63,178"
			width = "0.75"
		]
		cluster2Nodes.get(3) => [
			label = "\\N"
			height = "0.5"
			pos = "63,250"
			width = "0.75"
		]
		cluster2Nodes.get(4) => [
			label = "\\N"
			height = "0.5"
			pos = "63,322"
			width = "0.75"
		]

		val cluster2Edges = cluster2.edges
		cluster2Edges.get(0).pos = "e,60.532,87.725 55.421,51.945 56.522,59.654 57.853,68.973 59.091,77.636"
		cluster2Edges.get(1).pos = "e,63,159.9 63,124.3 63,132.02 63,141.29 63,149.89"
		cluster2Edges.get(2).pos = "e,63,231.9 63,196.3 63,204.02 63,213.29 63,221.89"
		cluster2Edges.get(3).pos = "e,63,303.9 63,268.3 63,276.02 63,285.29 63,293.89"
		cluster2Edges.get(4).pos = "e,42.762,50.983 49.25,306.07 41.039,296.1 31.381,282.25 27,268 3.486,191.53 8.9148,165.93 27,88 29.222,78.425 33.428,68.586 37.82, 60.024"

		val edges = graph.edges
		edges.get(0).pos = "e,75.748,122.3 75.748,449.7 83.559,439.59 93.148,425.71 99,412 114.88,374.8 118,363.45 118,323 118,249 118,249 118,249 118,208.55 114.88,197.2 99,160 94.611,149.72 88.12,139.34 81.853,130.53"
		edges.get(1).pos = "e,63,375.9 63,340.3 63,348.02 63,357.29 63,365.89"

		graph
	}

	public static String[] expectedDotColorSchemes = #["x11", "svg", "accent3", "accent4", "accent5", "accent6",
		"accent7", "accent8", "blues3", "blues4", "blues5", "blues6", "blues7", "blues8", "blues9", "brbg10", "brbg11",
		"brbg3", "brbg4", "brbg5", "brbg6", "brbg7", "brbg8", "brbg9", "bugn3", "bugn4", "bugn5", "bugn6", "bugn7",
		"bugn8", "bugn9", "bupu3", "bupu4", "bupu5", "bupu6", "bupu7", "bupu8", "bupu9", "dark23", "dark24", "dark25",
		"dark26", "dark27", "dark28", "gnbu3", "gnbu4", "gnbu5", "gnbu6", "gnbu7", "gnbu8", "gnbu9", "greens3",
		"greens4", "greens5", "greens6", "greens7", "greens8", "greens9", "greys3", "greys4", "greys5", "greys6",
		"greys7", "greys8", "greys9", "oranges3", "oranges4", "oranges5", "oranges6", "oranges7", "oranges8",
		"oranges9", "orrd3", "orrd4", "orrd5", "orrd6", "orrd7", "orrd8", "orrd9", "paired10", "paired11", "paired12",
		"paired3", "paired4", "paired5", "paired6", "paired7", "paired8", "paired9", "pastel13", "pastel14", "pastel15",
		"pastel16", "pastel17", "pastel18", "pastel19", "pastel23", "pastel24", "pastel25", "pastel26", "pastel27",
		"pastel28", "piyg10", "piyg11", "piyg3", "piyg4", "piyg5", "piyg6", "piyg7", "piyg8", "piyg9", "prgn10",
		"prgn11", "prgn3", "prgn4", "prgn5", "prgn6", "prgn7", "prgn8", "prgn9", "pubu3", "pubu4", "pubu5", "pubu6",
		"pubu7", "pubu8", "pubu9", "pubugn3", "pubugn4", "pubugn5", "pubugn6", "pubugn7", "pubugn8", "pubugn9",
		"puor10", "puor11", "puor3", "puor4", "puor5", "puor6", "puor7", "puor8", "puor9", "purd3", "purd4", "purd5",
		"purd6", "purd7", "purd8", "purd9", "purples3", "purples4", "purples5", "purples6", "purples7", "purples8",
		"purples9", "rdbu10", "rdbu11", "rdbu3", "rdbu4", "rdbu5", "rdbu6", "rdbu7", "rdbu8", "rdbu9", "rdgy10",
		"rdgy11", "rdgy3", "rdgy4", "rdgy5", "rdgy6", "rdgy7", "rdgy8", "rdgy9", "rdpu3", "rdpu4", "rdpu5", "rdpu6",
		"rdpu7", "rdpu8", "rdpu9", "rdylbu10", "rdylbu11", "rdylbu3", "rdylbu4", "rdylbu5", "rdylbu6", "rdylbu7",
		"rdylbu8", "rdylbu9", "rdylgn10", "rdylgn11", "rdylgn3", "rdylgn4", "rdylgn5", "rdylgn6", "rdylgn7", "rdylgn8",
		"rdylgn9", "reds3", "reds4", "reds5", "reds6", "reds7", "reds8", "reds9", "set13", "set14", "set15", "set16",
		"set17", "set18", "set19", "set23", "set24", "set25", "set26", "set27", "set28", "set310", "set311", "set312",
		"set33", "set34", "set35", "set36", "set37", "set38", "set39", "spectral10", "spectral11", "spectral3",
		"spectral4", "spectral5", "spectral6", "spectral7", "spectral8", "spectral9", "ylgn3", "ylgn4", "ylgn5",
		"ylgn6", "ylgn7", "ylgn8", "ylgn9", "ylgnbu3", "ylgnbu4", "ylgnbu5", "ylgnbu6", "ylgnbu7", "ylgnbu8", "ylgnbu9",
		"ylorbr3", "ylorbr4", "ylorbr5", "ylorbr6", "ylorbr7", "ylorbr8", "ylorbr9", "ylorrd3", "ylorrd4", "ylorrd5",
		"ylorrd6", "ylorrd7", "ylorrd8", "ylorrd9"]

	/**
	 * The implementation of the following helper methods is mainly taken from
	 * the {@link IResourcesSetupUtil} java class.
	 */
	static val TEST_PROJECT = "dottestproject"

	def static createTestFile(String content) throws Exception {
		'''«TEST_PROJECT»/test.dot'''.toString.createFile(content)
	}

	def static void createTestProjectWithXtextNature() {
		val project = TEST_PROJECT.createProject
		project.addNature(XtextProjectHelper.NATURE_ID)
	}

	private def static createFile(String wsRelativePath, String s) {
		new Path(wsRelativePath).createFile(s)
	}

	private def static createFile(IPath wsRelativePath, String s) {
		val file = root.getFile(wsRelativePath)
		([ IProgressMonitor monitor |
			create(file.getParent)
			file.delete(true, monitor)
			file.create(new StringInputStream(s), true, monitor)
		] as WorkspaceModifyOperation).run(monitor)
		file
	}

	private def static root() {
		ResourcesPlugin.workspace.root
	}

	private def static IProgressMonitor monitor() {
		new NullProgressMonitor
	}

	private def static void create(IContainer container) {
		([ IProgressMonitor monitor |
			if (!container.exists) {
				container.parent.create
				if (container instanceof IFolder) {
					container.create(true, true, monitor)
				} else {
					(container as IProject).createProject
				}
			}
		] as WorkspaceModifyOperation).run(monitor)
	}

	private def static IProject createProject(IProject project) {
		if(!project.exists) {
			project.create(monitor)
		}
		project.open(monitor)
		project
	}
}
