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
import java.util.Iterator;
import java.util.List;

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
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.GraphMerger;

/**
 * Utilities to import DOT files or strings into Graph instances containing
 * attributes defined in {@link DotAttributes}.
 * 
 * @author Fabian Steeg (fsteeg)
 * @author anyssen
 */
public final class DotImport {

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
		new GraphMerger(toGraph(), DotAttributes._NAME__GNE).into(graph);
	}

	@Override
	public String toString() {
		return String.format("%s of %s", getClass().getSimpleName(), //$NON-NLS-1$
				dotString);
	}

}
