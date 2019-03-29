/*******************************************************************************
 * Copyright (c) 2018, 2019 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import org.eclipse.emf.ecore.EPackage
import org.eclipse.gef.dot.internal.language.arrowtype.ArrowtypePackage
import org.eclipse.gef.dot.internal.language.color.ColorPackage
import org.eclipse.gef.dot.internal.language.colorlist.ColorlistPackage
import org.eclipse.gef.dot.internal.language.escstring.EscstringPackage
import org.eclipse.gef.dot.internal.language.fontname.FontnamePackage
import org.eclipse.gef.dot.internal.language.htmllabel.HtmllabelPackage
import org.eclipse.gef.dot.internal.language.point.PointPackage
import org.eclipse.gef.dot.internal.language.portpos.PortposPackage
import org.eclipse.gef.dot.internal.language.recordlabel.RecordlabelPackage
import org.eclipse.gef.dot.internal.language.rect.RectPackage
import org.eclipse.gef.dot.internal.language.shape.ShapePackage
import org.eclipse.gef.dot.internal.language.splinetype.SplinetypePackage
import org.eclipse.gef.dot.internal.language.style.StylePackage
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * A {@link TestRule} to ensure that the Dot subgrammar packages
 * are registered properly before test texecution.
 */
class DotSubgrammarPackagesRegistrationRule implements TestRule {

	override apply(Statement base, Description description) {
		new Statement() {
			override evaluate() {
				registerDotSubgrammarPackages
				base.evaluate
			}
		}
	}

	private def registerDotSubgrammarPackages() {
		registerEPackage(ArrowtypePackage.eNS_URI, ArrowtypePackage.eINSTANCE)
		registerEPackage(ColorPackage.eNS_URI, ColorPackage.eINSTANCE)
		registerEPackage(ColorlistPackage.eNS_URI, ColorlistPackage.eINSTANCE)
		registerEPackage(EscstringPackage.eNS_URI, EscstringPackage.eINSTANCE)
		registerEPackage(FontnamePackage.eNS_URI, FontnamePackage.eINSTANCE)
		registerEPackage(HtmllabelPackage.eNS_URI, HtmllabelPackage.eINSTANCE)
		registerEPackage(PointPackage.eNS_URI, PointPackage.eINSTANCE)
		registerEPackage(PortposPackage.eNS_URI, PortposPackage.eINSTANCE)
		registerEPackage(RecordlabelPackage.eNS_URI, RecordlabelPackage.eINSTANCE)
		registerEPackage(RectPackage.eNS_URI, RectPackage.eINSTANCE)
		registerEPackage(ShapePackage.eNS_URI, ShapePackage.eINSTANCE)
		registerEPackage(SplinetypePackage.eNS_URI, SplinetypePackage.eINSTANCE)
		registerEPackage(StylePackage.eNS_URI, StylePackage.eINSTANCE)
	}

	private def registerEPackage(String packageNamespaceURI, EPackage packageInstance) {
		if (!EPackage.Registry.INSTANCE.containsKey(packageNamespaceURI)) {
			EPackage.Registry.INSTANCE.put(packageNamespaceURI, packageInstance)
		}
	}
}