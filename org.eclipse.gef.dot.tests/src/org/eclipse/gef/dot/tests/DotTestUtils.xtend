/*******************************************************************************
 * Copyright (c) 2009, 2020 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Fabian Steeg               - initial API and implementation (see bug #277380)
 *     Tamas Miklossy (itemis AG) - Add support for all dot attributes (bug #461506)
 *     Zoey Prigge    (itemis AG) - move expected color/font arrays for reuse (bug #553575)
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
import org.eclipse.xtext.ui.XtextProjectHelper
import org.eclipse.xtext.ui.testing.util.IResourcesSetupUtil
import org.eclipse.xtext.util.StringInputStream

import static extension org.eclipse.gef.dot.internal.DotAttributes.*
import static extension org.eclipse.gef.dot.internal.DotFileUtils.read
import static extension org.eclipse.xtext.ui.testing.util.IResourcesSetupUtil.addNature
import static extension org.eclipse.xtext.ui.testing.util.IResourcesSetupUtil.createProject

/** 
 * Util class for various tests.
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

	package static val expectedX11ColorNames = #["aliceblue", "antiquewhite", "antiquewhite1", "antiquewhite2", "antiquewhite3",
		"antiquewhite4", "aquamarine", "aquamarine1", "aquamarine2", "aquamarine3", "aquamarine4", "azure", "azure1",
		"azure2", "azure3", "azure4", "beige", "bisque", "bisque1", "bisque2", "bisque3", "bisque4", "black",
		"blanchedalmond", "blue", "blue1", "blue2", "blue3", "blue4", "blueviolet", "brown", "brown1", "brown2",
		"brown3", "brown4", "burlywood", "burlywood1", "burlywood2", "burlywood3", "burlywood4", "cadetblue",
		"cadetblue1", "cadetblue2", "cadetblue3", "cadetblue4", "chartreuse", "chartreuse1", "chartreuse2",
		"chartreuse3", "chartreuse4", "chocolate", "chocolate1", "chocolate2", "chocolate3", "chocolate4", "coral",
		"coral1", "coral2", "coral3", "coral4", "cornflowerblue", "cornsilk", "cornsilk1", "cornsilk2", "cornsilk3",
		"cornsilk4", "crimson", "cyan", "cyan1", "cyan2", "cyan3", "cyan4", "darkgoldenrod", "darkgoldenrod1",
		"darkgoldenrod2", "darkgoldenrod3", "darkgoldenrod4", "darkgreen", "darkkhaki", "darkolivegreen",
		"darkolivegreen1", "darkolivegreen2", "darkolivegreen3", "darkolivegreen4", "darkorange", "darkorange1",
		"darkorange2", "darkorange3", "darkorange4", "darkorchid", "darkorchid1", "darkorchid2", "darkorchid3",
		"darkorchid4", "darksalmon", "darkseagreen", "darkseagreen1", "darkseagreen2", "darkseagreen3", "darkseagreen4",
		"darkslateblue", "darkslategray", "darkslategray1", "darkslategray2", "darkslategray3", "darkslategray4",
		"darkslategrey", "darkturquoise", "darkviolet", "deeppink", "deeppink1", "deeppink2", "deeppink3", "deeppink4",
		"deepskyblue", "deepskyblue1", "deepskyblue2", "deepskyblue3", "deepskyblue4", "dimgray", "dimgrey",
		"dodgerblue", "dodgerblue1", "dodgerblue2", "dodgerblue3", "dodgerblue4", "firebrick", "firebrick1",
		"firebrick2", "firebrick3", "firebrick4", "floralwhite", "forestgreen", "gainsboro", "ghostwhite", "gold",
		"gold1", "gold2", "gold3", "gold4", "goldenrod", "goldenrod1", "goldenrod2", "goldenrod3", "goldenrod4", "gray",
		"gray0", "gray1", "gray10", "gray100", "gray11", "gray12", "gray13", "gray14", "gray15", "gray16", "gray17",
		"gray18", "gray19", "gray2", "gray20", "gray21", "gray22", "gray23", "gray24", "gray25", "gray26", "gray27",
		"gray28", "gray29", "gray3", "gray30", "gray31", "gray32", "gray33", "gray34", "gray35", "gray36", "gray37",
		"gray38", "gray39", "gray4", "gray40", "gray41", "gray42", "gray43", "gray44", "gray45", "gray46", "gray47",
		"gray48", "gray49", "gray5", "gray50", "gray51", "gray52", "gray53", "gray54", "gray55", "gray56", "gray57",
		"gray58", "gray59", "gray6", "gray60", "gray61", "gray62", "gray63", "gray64", "gray65", "gray66", "gray67",
		"gray68", "gray69", "gray7", "gray70", "gray71", "gray72", "gray73", "gray74", "gray75", "gray76", "gray77",
		"gray78", "gray79", "gray8", "gray80", "gray81", "gray82", "gray83", "gray84", "gray85", "gray86", "gray87",
		"gray88", "gray89", "gray9", "gray90", "gray91", "gray92", "gray93", "gray94", "gray95", "gray96", "gray97",
		"gray98", "gray99", "green", "green1", "green2", "green3", "green4", "greenyellow", "grey", "grey0", "grey1",
		"grey10", "grey100", "grey11", "grey12", "grey13", "grey14", "grey15", "grey16", "grey17", "grey18", "grey19",
		"grey2", "grey20", "grey21", "grey22", "grey23", "grey24", "grey25", "grey26", "grey27", "grey28", "grey29",
		"grey3", "grey30", "grey31", "grey32", "grey33", "grey34", "grey35", "grey36", "grey37", "grey38", "grey39",
		"grey4", "grey40", "grey41", "grey42", "grey43", "grey44", "grey45", "grey46", "grey47", "grey48", "grey49",
		"grey5", "grey50", "grey51", "grey52", "grey53", "grey54", "grey55", "grey56", "grey57", "grey58", "grey59",
		"grey6", "grey60", "grey61", "grey62", "grey63", "grey64", "grey65", "grey66", "grey67", "grey68", "grey69",
		"grey7", "grey70", "grey71", "grey72", "grey73", "grey74", "grey75", "grey76", "grey77", "grey78", "grey79",
		"grey8", "grey80", "grey81", "grey82", "grey83", "grey84", "grey85", "grey86", "grey87", "grey88", "grey89",
		"grey9", "grey90", "grey91", "grey92", "grey93", "grey94", "grey95", "grey96", "grey97", "grey98", "grey99",
		"honeydew", "honeydew1", "honeydew2", "honeydew3", "honeydew4", "hotpink", "hotpink1", "hotpink2", "hotpink3",
		"hotpink4", "indianred", "indianred1", "indianred2", "indianred3", "indianred4", "indigo", "invis", "ivory",
		"ivory1", "ivory2", "ivory3", "ivory4", "khaki", "khaki1", "khaki2", "khaki3", "khaki4", "lavender",
		"lavenderblush", "lavenderblush1", "lavenderblush2", "lavenderblush3", "lavenderblush4", "lawngreen",
		"lemonchiffon", "lemonchiffon1", "lemonchiffon2", "lemonchiffon3", "lemonchiffon4", "lightblue", "lightblue1",
		"lightblue2", "lightblue3", "lightblue4", "lightcoral", "lightcyan", "lightcyan1", "lightcyan2", "lightcyan3",
		"lightcyan4", "lightgoldenrod", "lightgoldenrod1", "lightgoldenrod2", "lightgoldenrod3", "lightgoldenrod4",
		"lightgoldenrodyellow", "lightgray", "lightgrey", "lightpink", "lightpink1", "lightpink2", "lightpink3",
		"lightpink4", "lightsalmon", "lightsalmon1", "lightsalmon2", "lightsalmon3", "lightsalmon4", "lightseagreen",
		"lightskyblue", "lightskyblue1", "lightskyblue2", "lightskyblue3", "lightskyblue4", "lightslateblue",
		"lightslategray", "lightslategrey", "lightsteelblue", "lightsteelblue1", "lightsteelblue2", "lightsteelblue3",
		"lightsteelblue4", "lightyellow", "lightyellow1", "lightyellow2", "lightyellow3", "lightyellow4", "limegreen",
		"linen", "magenta", "magenta1", "magenta2", "magenta3", "magenta4", "maroon", "maroon1", "maroon2", "maroon3",
		"maroon4", "mediumaquamarine", "mediumblue", "mediumorchid", "mediumorchid1", "mediumorchid2", "mediumorchid3",
		"mediumorchid4", "mediumpurple", "mediumpurple1", "mediumpurple2", "mediumpurple3", "mediumpurple4",
		"mediumseagreen", "mediumslateblue", "mediumspringgreen", "mediumturquoise", "mediumvioletred", "midnightblue",
		"mintcream", "mistyrose", "mistyrose1", "mistyrose2", "mistyrose3", "mistyrose4", "moccasin", "navajowhite",
		"navajowhite1", "navajowhite2", "navajowhite3", "navajowhite4", "navy", "navyblue", "none", "oldlace",
		"olivedrab", "olivedrab1", "olivedrab2", "olivedrab3", "olivedrab4", "orange", "orange1", "orange2", "orange3",
		"orange4", "orangered", "orangered1", "orangered2", "orangered3", "orangered4", "orchid", "orchid1", "orchid2",
		"orchid3", "orchid4", "palegoldenrod", "palegreen", "palegreen1", "palegreen2", "palegreen3", "palegreen4",
		"paleturquoise", "paleturquoise1", "paleturquoise2", "paleturquoise3", "paleturquoise4", "palevioletred",
		"palevioletred1", "palevioletred2", "palevioletred3", "palevioletred4", "papayawhip", "peachpuff", "peachpuff1",
		"peachpuff2", "peachpuff3", "peachpuff4", "peru", "pink", "pink1", "pink2", "pink3", "pink4", "plum", "plum1",
		"plum2", "plum3", "plum4", "powderblue", "purple", "purple1", "purple2", "purple3", "purple4", "red", "red1",
		"red2", "red3", "red4", "rosybrown", "rosybrown1", "rosybrown2", "rosybrown3", "rosybrown4", "royalblue",
		"royalblue1", "royalblue2", "royalblue3", "royalblue4", "saddlebrown", "salmon", "salmon1", "salmon2",
		"salmon3", "salmon4", "sandybrown", "seagreen", "seagreen1", "seagreen2", "seagreen3", "seagreen4", "seashell",
		"seashell1", "seashell2", "seashell3", "seashell4", "sienna", "sienna1", "sienna2", "sienna3", "sienna4",
		"skyblue", "skyblue1", "skyblue2", "skyblue3", "skyblue4", "slateblue", "slateblue1", "slateblue2",
		"slateblue3", "slateblue4", "slategray", "slategray1", "slategray2", "slategray3", "slategray4", "slategrey",
		"snow", "snow1", "snow2", "snow3", "snow4", "springgreen", "springgreen1", "springgreen2", "springgreen3",
		"springgreen4", "steelblue", "steelblue1", "steelblue2", "steelblue3", "steelblue4", "tan", "tan1", "tan2",
		"tan3", "tan4", "thistle", "thistle1", "thistle2", "thistle3", "thistle4", "tomato", "tomato1", "tomato2",
		"tomato3", "tomato4", "transparent", "turquoise", "turquoise1", "turquoise2", "turquoise3", "turquoise4",
		"violet", "violetred", "violetred1", "violetred2", "violetred3", "violetred4", "wheat", "wheat1", "wheat2",
		"wheat3", "wheat4", "white", "whitesmoke", "yellow", "yellow1", "yellow2", "yellow3", "yellow4", "yellowgreen"]

	package static val expectedSvgColorNames = #["aliceblue", "antiquewhite", "aqua", "aquamarine", "azure", "beige", "bisque",
		"black", "blanchedalmond", "blue", "blueviolet", "brown", "burlywood", "cadetblue", "chartreuse", "chocolate",
		"coral", "cornflowerblue", "cornsilk", "crimson", "cyan", "darkblue", "darkcyan", "darkgoldenrod", "darkgray",
		"darkgreen", "darkgrey", "darkkhaki", "darkmagenta", "darkolivegreen", "darkorange", "darkorchid", "darkred",
		"darksalmon", "darkseagreen", "darkslateblue", "darkslategray", "darkslategrey", "darkturquoise", "darkviolet",
		"deeppink", "deepskyblue", "dimgray", "dimgrey", "dodgerblue", "firebrick", "floralwhite", "forestgreen",
		"fuchsia", "gainsboro", "ghostwhite", "gold", "goldenrod", "gray", "grey", "green", "greenyellow", "honeydew",
		"hotpink", "indianred", "indigo", "ivory", "khaki", "lavender", "lavenderblush", "lawngreen", "lemonchiffon",
		"lightblue", "lightcoral", "lightcyan", "lightgoldenrodyellow", "lightgray", "lightgreen", "lightgrey",
		"lightpink", "lightsalmon", "lightseagreen", "lightskyblue", "lightslategray", "lightslategrey",
		"lightsteelblue", "lightyellow", "lime", "limegreen", "linen", "magenta", "maroon", "mediumaquamarine",
		"mediumblue", "mediumorchid", "mediumpurple", "mediumseagreen", "mediumslateblue", "mediumspringgreen",
		"mediumturquoise", "mediumvioletred", "midnightblue", "mintcream", "mistyrose", "moccasin", "navajowhite",
		"navy", "oldlace", "olive", "olivedrab", "orange", "orangered", "orchid", "palegoldenrod", "palegreen",
		"paleturquoise", "palevioletred", "papayawhip", "peachpuff", "peru", "pink", "plum", "powderblue", "purple",
		"red", "rosybrown", "royalblue", "saddlebrown", "salmon", "sandybrown", "seagreen", "seashell", "sienna",
		"silver", "skyblue", "slateblue", "slategray", "slategrey", "snow", "springgreen", "steelblue", "tan", "teal",
		"thistle", "tomato", "turquoise", "violet", "wheat", "white", "whitesmoke", "yellow", "yellowgreen"]

	package static val expectedPostScriptFontNames = #["AvantGarde-Book", "AvantGarde-BookOblique", "AvantGarde-Demi",
		"AvantGarde-DemiOblique", "Bookman-Demi", "Bookman-DemiItalic", "Bookman-Light", "Bookman-LightItalic",
		"Courier", "Courier-Bold", "Courier-BoldOblique", "Courier-Oblique", "Helvetica", "Helvetica-Bold",
		"Helvetica-BoldOblique", "Helvetica-Narrow", "Helvetica-Narrow-Bold", "Helvetica-Narrow-BoldOblique",
		"Helvetica-Narrow-Oblique", "Helvetica-Oblique", "NewCenturySchlbk-Bold", "NewCenturySchlbk-BoldItalic",
		"NewCenturySchlbk-Italic", "NewCenturySchlbk-Roman", "Palatino-Bold", "Palatino-BoldItalic", "Palatino-Italic",
		"Palatino-Roman", "Symbol", "Times-Bold", "Times-BoldItalic", "Times-Italic", "Times-Roman",
		"ZapfChancery-MediumItalic", "ZapfDingbats"]
		
		
	static def String[] combine(String[] array1, String... array2) {
		array1 + array2
	}

	/**
	 * The implementation of the following helper methods is mainly taken from
	 * the {@link IResourcesSetupUtil} java class.
	 */
	static val TEST_PROJECT = "dottestproject"

	def static createTestFile(String content) throws Exception {
		'''«TEST_PROJECT»/test.dot'''.toString.createFile(content)
	}

	def static createTestFile(String content, String ^extension) throws Exception {
		'''«TEST_PROJECT»/test.«extension»'''.toString.createFile(content)
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
