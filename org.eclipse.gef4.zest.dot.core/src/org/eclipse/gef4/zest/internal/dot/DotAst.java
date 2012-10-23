/*******************************************************************************
 * Copyright (c) 2009, 2010 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/
package org.eclipse.gef4.zest.internal.dot;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.gef4.zest.internal.dot.DotMessages;
import org.eclipse.gef4.zest.internal.dot.parser.DotStandaloneSetup;
import org.eclipse.gef4.zest.layouts.LayoutAlgorithm;
import org.eclipse.gef4.zest.layouts.algorithms.GridLayoutAlgorithm;
import org.eclipse.gef4.zest.layouts.algorithms.RadialLayoutAlgorithm;
import org.eclipse.gef4.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.gef4.zest.layouts.algorithms.TreeLayoutAlgorithm;
import org.eclipse.swt.SWT;

/**
 * Creation and access to the parsed object tree.
 * 
 * @author Fabian Steeg (fsteeg)
 */
public final class DotAst {

	// private static final int STYLE = LayoutStyles.NO_LAYOUT_NODE_RESIZING;

	/** Edge style attributes in the DOT input and their Zest/SWT styles. */
	enum Style {
		DASHED(SWT.LINE_DASH), DOTTED(SWT.LINE_DOT), SOLID(SWT.LINE_SOLID), DASHDOT(
				SWT.LINE_DASHDOT), DASHDOTDOT(SWT.LINE_DASHDOTDOT);
		int style;

		Style(final int style) {
			this.style = style;
		}
	}

	/**
	 * Graph layout attributes in the DOT input and their Zest layout
	 * algorithms.
	 */
	enum Layout {
		/***/
		DOT(new TreeLayoutAlgorithm()),
		/***/
		OSAGE(new GridLayoutAlgorithm()),
		/***/
		TWOPI(new RadialLayoutAlgorithm()),
		/***/
		CIRCO(new RadialLayoutAlgorithm()),
		/***/
		NEATO(new RadialLayoutAlgorithm()),
		/***/
		FDP(new SpringLayoutAlgorithm()),
		/***/
		SFDP(new SpringLayoutAlgorithm());
		LayoutAlgorithm algorithm;

		Layout(final LayoutAlgorithm algorithm) {
			this.algorithm = algorithm;
		}
	}

	private Resource resource;

	/**
	 * @param dotString
	 *            The DOT string to parse
	 */
	public DotAst(final String dotString) {
		this.resource = loadResource(dotString);
	}

	/**
	 * @param dotFile
	 *            The DOT file to parse
	 * @return The name of the DOT graph described in the given file
	 */
	public String graphName() {
		EObject graph = graph();
		Iterator<EAttribute> graphAttributes = graph.eClass()
				.getEAllAttributes().iterator();
		while (graphAttributes.hasNext()) {
			EAttribute a = graphAttributes.next();
			/* We return the name attribute of the graph: */
			if (a.getName().equals("name")) { //$NON-NLS-1$
				return (String) graph.eGet(a);
			}
		}
		System.err.println("Could not find name attribute in: " + graph); //$NON-NLS-1$
		return ""; //$NON-NLS-1$
	}

	/**
	 * @return The errors reported by the parser when parsing the given file
	 */
	public List<String> errors() {
		List<String> result = new ArrayList<String>();
		EList<Diagnostic> errors = resource.getErrors();
		Iterator<Diagnostic> i = errors.iterator();
		while (i.hasNext()) {
			Diagnostic next = i.next();
			result.add(String.format(
					DotMessages.DotAst_0 + " %s: %s ", next.getLine(), //$NON-NLS-1$
					next.getMessage()));
		}
		return result;
	}

	/**
	 * @param dotFile
	 *            The DOT file to parse
	 * @return The graph EObjects to walk or inspect
	 */
	EObject graph() {
		/* We load the input DOT file: */
		EList<EObject> contents = resource.getContents();
		EObject graphs = contents.iterator().next();
		/* We assume one graph per file, i.e. we take the first only: */
		EObject graph = graphs.eContents().iterator().next();
		return graph;
	}

	/**
	 * @return The loaded resource for the given DOT input
	 */
	Resource resource() {
		return resource;
	}

	private static Resource loadResource(final String dot) {
		DotStandaloneSetup.doSetup();
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

	@Override
	public String toString() {
		return String.format(
				"%s named '%s' with %s errors, resource: %s", //$NON-NLS-1$
				getClass().getSimpleName(), graphName(), errors().size(),
				resource);
	}

}
