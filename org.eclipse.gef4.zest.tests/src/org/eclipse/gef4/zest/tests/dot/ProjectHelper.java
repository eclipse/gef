/*******************************************************************************
 * Copyright (c) 2009, 2010 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/
package org.eclipse.gef4.zest.tests.dot;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.junit.Assert;

/**
 * Static helper methods for setting up a project during testing.
 * 
 * @author Fabian Steeg (fsteeg)
 */
public final class ProjectHelper {

	static final String PROJECT_NAME = "ZestTest"; //$NON-NLS-1$
	static final Path PROJECT_PATH = new Path(Platform.getLocation().toString()
			+ "/" + PROJECT_NAME); //$NON-NLS-1$

	private ProjectHelper() {/* enforce non-instantiability */
	}

	/**
	 * @param path
	 *            The path to the project that should not exist
	 */
	static void assertProjectDoesntExist(final IPath path) {
		Assert.assertFalse(
				"Container path must not exist before running tests: " //$NON-NLS-1$
						+ path.toFile(), path.toFile().exists());
	}

	/**
	 * @param path
	 *            The path to the project that should exist
	 */
	static void assertProjectExists(final IPath path) {
		Assert.assertTrue(
				"Container path must exist to continue running tests: " //$NON-NLS-1$
						+ path.toFile(), path.toFile().exists());
	}

	/**
	 * @param projectName
	 *            The simple name of the project to create in the workspace
	 * @return The created project
	 */
	static IProject createProject(final String projectName) {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(projectName);
		try {
			project.refreshLocal(-1, null);
			if (project.exists()) {
				project.delete(true, null);
				assertProjectDoesntExist(project.getLocation());
			}
			project.create(null);
			if (!project.isOpen()) {
				project.open(null);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		assertProjectExists(project.getLocation());
		return project;
	}

	/**
	 * @param project
	 *            The project to delete
	 */
	static void deleteProject(final IProject project) {
		try {
			project.refreshLocal(-1, null);
			project.delete(true, true, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
}
