/*******************************************************************************
 * Copyright (c) 2009, 2010 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/

package org.eclipse.gef4.zest.internal.dot;

import java.io.File;
import java.net.MalformedURLException;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.gef4.zest.core.widgets.Graph;
import org.eclipse.gef4.zest.internal.dot.DotFileUtils;
import org.eclipse.gef4.zest.internal.dot.DotMessages;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**
 * Transformation of DOT files or strings to Zest Graph instances.
 * 
 * @author Fabian Steeg (fsteeg)
 */
public final class DotImport {
	private String dotString;
	private DotAst dotAst;

	/**
	 * @param dotFile
	 *            The DOT file to import
	 */
	public DotImport(final File dotFile) {
		this.dotString = DotFileUtils.read(dotFile);
		load();
	}

	/**
	 * @param dotFile
	 *            The DOT file to import
	 */
	public DotImport(final IFile dotFile) {
		try {
			this.dotString = DotFileUtils.read(DotFileUtils.resolve(dotFile
					.getLocationURI().toURL()));
			load();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param dotString
	 *            The DOT graph to import
	 */
	public DotImport(final String dotString) {
		init(dotString);
	}

	private void init(final String dotString) {
		if (dotString == null || dotString.trim().length() == 0) {
			throw new IllegalArgumentException(DotMessages.DotImport_2 + ": " //$NON-NLS-1$
					+ dotString);
		}
		loadFrom(dotString);
		if (dotAst.errors().size() > 0) {
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
		List<String> errors = this.dotAst.errors();
		if (errors.size() > 0) {
			throw new IllegalArgumentException(String.format(
					DotMessages.DotImport_1 + ": %s (%s)", dotString, //$NON-NLS-1$
					errors.toString()));
		}
	}

	private void load() {
		this.dotAst = new DotAst(this.dotString);
	}

	/**
	 * @return The errors the parser reported when parsing the given DOT graph
	 */
	public List<String> getErrors() {
		return dotAst.errors();
	}

	/**
	 * @return The name of the DOT graph
	 */
	public String getName() {
		return dotAst.graphName();
	}

	/**
	 * @param parent
	 *            The parent to create the Zest graph in
	 * @param style
	 *            The style bits for the Zest graph
	 * @return The Zest graph instantiated from the imported DOT
	 */
	public Graph newGraphInstance(final Composite parent, final int style) {
		guardFaultyParse();
		/*
		 * TODO switch to a string as the member holding the DOT to avoid
		 * read-write here, and set that string as the resulting graph's data
		 */
		return new GraphCreatorInterpreter().create(parent, style, dotAst);
	}

	/**
	 * @param graph
	 *            The graph to add the imported dot into
	 */
	public void into(Graph graph) {
		Shell shell = new Shell();
		new ZestGraphImport(newGraphInstance(shell, graph.getStyle()))
				.into(graph);
		shell.dispose();
	}

	/**
	 * @return The DOT AST parsed from the DOT source
	 */
	public DotAst getDotAst() {
		return this.dotAst;
	}

	@Override
	public String toString() {
		return String.format("%s of %s at %s", getClass().getSimpleName(), //$NON-NLS-1$
				dotAst, dotString);
	}
}
