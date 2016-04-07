/*******************************************************************************
 * Copyright (c) 2009, 2016 Fabian Steeg and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Fabian Steeg                - initial API and implementation (see bug #277380)
 *     Alexander Nyßen (itemis AG) - major refactorings
 *     
 *******************************************************************************/

package org.eclipse.gef4.dot.internal;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.gef4.dot.internal.parser.DotStandaloneSetup;
import org.eclipse.gef4.dot.internal.parser.dot.DotAst;
import org.eclipse.gef4.dot.internal.parser.dot.DotGraph;
import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Node;

/**
 * Utilities to import DOT files or strings into Graph instances containing
 * attributes defined in {@link DotAttributes}.
 * 
 * @author Fabian Steeg (fsteeg)
 * @author anyssen
 */
public final class DotImport {

	/**
	 * Imports the content of a GEF4 graph generated from DOT into an existing
	 * GEF4 graph.
	 *
	 * @author Fabian Steeg (fsteeg)
	 * @author anyssen
	 */
	private static class GraphMerger {

		private String idAttributeName = null;

		/**
		 * Create a new {@link GraphMerger}, which uses the given attribute key
		 * to retrieve an identification attribute.
		 *
		 * @param idAttributeName
		 *            The name of the attribute that stores an identification
		 *            value.
		 */
		public GraphMerger(String idAttributeName) {
			this.idAttributeName = idAttributeName;
		}

		private Edge copy(Edge edge, Graph.Builder targetGraph,
				Node targetSource, Node targetTarget) {
			// copy edge
			Edge.Builder copy = new Edge.Builder(targetSource, targetTarget);

			// copy attributes
			for (Entry<String, Object> attr : edge.attributesProperty()
					.entrySet()) {
				copy.attr(attr.getKey(), attr.getValue());
			}

			// put into graph
			Edge build = copy.buildEdge();
			targetGraph.edges(build);
			return build;
		}

		private Node copy(Node node, Graph.Builder targetGraph) {
			Node.Builder copy = new Node.Builder();
			// copy attributes
			for (Entry<String, Object> attr : node.attributesProperty()
					.entrySet()) {
				copy.attr(attr.getKey(), attr.getValue());
			}
			Node copiedNode = copy.buildNode();
			targetGraph.nodes(copiedNode);
			return copiedNode;
		}

		private Node find(Map<Object, Node> nodesByIds, Node n) {
			Object id = n.attributesProperty().get(idAttributeName);
			if (id != null && !nodesByIds.containsKey(id)) {
				nodesByIds.put(id, n);
				return null;
			}
			return nodesByIds.get(id);
		}

		/**
		 * Merges the given source {@link Graph} into the target {@link Graph}.
		 *
		 * @param sourceGraph
		 *            The source graph to merge into the target graph.
		 * @param targetGraph
		 *            The target graph to merge content to.
		 */
		public void merge(Graph sourceGraph, Graph.Builder targetGraph) {
			// copy attributes
			for (Entry<String, Object> attr : sourceGraph.attributesProperty()
					.entrySet()) {
				targetGraph.attr(attr.getKey(), attr.getValue());
			}

			// find all existing node IDs in the target graph
			Graph targetGraphBuilt = targetGraph.build();
			List<Node> nodes = targetGraphBuilt.getNodes();
			Map<Object, Node> ids = new HashMap<>();
			for (Node n : nodes) {
				find(ids, n);
			}
			// copy non-existing nodes over
			Map<Node, Node> copiedNodes = new HashMap<>();
			for (Node node : sourceGraph.getNodes()) {
				if (find(ids, node) == null) {
					copiedNodes.put(node, copy(node, targetGraph));
				}
			}

			// copy edges over
			// TODO: what about existing edges
			for (Edge edge : sourceGraph.getEdges()) {
				// determine source and target
				Node srcSource = edge.getSource();
				Node tSource = find(ids, srcSource);
				if (tSource == null) {
					tSource = copiedNodes.get(srcSource);
				}

				Node srcTarget = edge.getTarget();
				Node tTarget = find(ids, srcTarget);
				if (tTarget == null) {
					tTarget = copiedNodes.get(srcTarget);
				}
				copy(edge, targetGraph, tSource, tTarget);
			}
		}
	}

	private Resource resource;
	private String dotString;

	/**
	 * @param dotFile
	 *            The DOT file to import.
	 */
	public DotImport(final File dotFile) {
		this.dotString = DotFileUtils.read(dotFile);
		load();
	}

	/**
	 * @param dotString
	 *            The DOT string to import.
	 */
	public DotImport(final String dotString) {
		init(dotString);
	}

	private void init(final String dotString) {
		if (dotString == null || dotString.trim().length() == 0) {
			throw new IllegalArgumentException(
					"Passed DOT must not be null or empty: " //$NON-NLS-1$
							+ dotString);
		}
		loadFrom(dotString);
		if (getErrors().size() > 0) {
			loadFrom(wrapped(dotString));
		}
	}

	private void loadFrom(final String dotString) {
		this.dotString = dotString;
		load();
	}

	private String wrapped(final String dotString) {
		return String.format("%s Unnamed{%s}", //$NON-NLS-1$
				dotString.contains("->") ? "digraph" : "graph", dotString); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
	}

	private void guardFaultyParse() {
		List<String> errors = getErrors();
		if (errors.size() > 0) {
			throw new IllegalArgumentException(
					String.format("Could not parse DOT: %s (%s)", dotString, //$NON-NLS-1$
							errors.toString()));
		}
	}

	private void load() {
		resource = loadResource(this.dotString);
	}

	/**
	 * @return The name of the DOT graph
	 */
	public String getName() {
		return dotGraph().getName();
	}

	/**
	 * @return The Zest graph instantiated from the imported DOT
	 */
	public Graph toGraph() {
		guardFaultyParse();
		/*
		 * TODO switch to a string as the member holding the DOT to avoid
		 * read-write here, and set that string as the resulting graph's data
		 */
		// TODO: handle multiple graphs
		return new DotInterpreter()
				.interpret((DotAst) resource.getContents().get(0)).get(0);
	}

	private static Resource loadResource(final String dot) {
		if (!Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap()
				.containsKey("dot")) { //$NON-NLS-1$
			DotStandaloneSetup.doSetup();
		}
		ResourceSet set = new ResourceSetImpl();
		Resource res = set.createResource(URI.createURI("*.dot")); //$NON-NLS-1$
		try {
			res.load(new ByteArrayInputStream(dot.getBytes()),
					Collections.EMPTY_MAP);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * @return The graph EObjects to walk or inspect
	 */
	public DotGraph dotGraph() {
		/* We load the input DOT file: */
		EList<EObject> contents = resource.getContents();
		EObject graphs = contents.iterator().next();
		/* We assume one graph per file, i.e. we take the first only: */
		EObject graph = graphs.eContents().iterator().next();
		return (DotGraph) graph;
	}

	/**
	 * @return The errors reported by the parser when parsing the given file
	 */
	public List<String> getErrors() {
		List<String> result = new ArrayList<>();
		EList<Diagnostic> errors = resource.getErrors();
		Iterator<Diagnostic> i = errors.iterator();
		while (i.hasNext()) {
			Diagnostic next = i.next();
			result.add(String.format("Error in line %s: %s ", //$NON-NLS-1$
					next.getLine(), next.getMessage()));
		}
		return result;
	}

	/**
	 * @param graph
	 *            The graph to add the imported dot into
	 */
	public void into(Graph.Builder graph) {
		new GraphMerger(DotAttributes._NAME__GNE).merge(toGraph(), graph);
	}

	@Override
	public String toString() {
		return String.format("%s of %s", getClass().getSimpleName(), //$NON-NLS-1$
				dotString);
	}

}
