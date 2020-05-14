/*******************************************************************************
 * Copyright (c) 2019, 2020 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.generator;

import org.eclipse.xtext.Grammar;
import org.eclipse.xtext.xtext.generator.DefaultGeneratorModule;
import org.eclipse.xtext.xtext.generator.XtextGeneratorNaming;
import org.eclipse.xtext.xtext.generator.model.TypeReference;

/**
 * A custom generator module to bind a custom naming extension.
 *
 * @author nyssen
 *
 */
public class DotGeneratorModule extends DefaultGeneratorModule {

	/**
	 * Binds the naming of the Xtext generator to the custom naming extension.
	 *
	 * @return A custom naming extension to properly name internal packages.
	 */
	public Class<? extends XtextGeneratorNaming> bindNaming() {
		return DotNaming.class;
	}

	/**
	 * This class enables the customization of the ide and ui package name
	 * within the Xtext code generation workflow for the dot grammar (host
	 * grammar and sub-grammars).
	 */
	public static class DotNaming extends XtextGeneratorNaming {

		@Override
		public String getGenericIdeBasePackage(Grammar g) {
			if (g.getName()
					.startsWith("org.eclipse.gef.dot.internal.language.Dot")) {
				return "org.eclipse.gef.dot.internal.ide.language";
			} else {
				return super.getGenericIdeBasePackage(g);
			}
		}

		@Override
		public String getEclipsePluginBasePackage(Grammar g) {
			if (g.getName()
					.startsWith("org.eclipse.gef.dot.internal.language.Dot")) {
				return "org.eclipse.gef.dot.internal.ui.language";
			} else {
				return super.getEclipsePluginBasePackage(g);
			}
		}

		@Override
		public String getRuntimeTestBasePackage(Grammar g) {
			return "org.eclipse.gef.dot.tests";
		}

		@Override
		public String getEclipsePluginTestBasePackage(Grammar g) {
			return "org.eclipse.gef.dot.tests.ui";
		}

		@Override
		public TypeReference getEclipsePluginActivator() {
			return new TypeReference("org.eclipse.gef.dot.internal.ui.language",
					"DotActivator");
		}
	}
}