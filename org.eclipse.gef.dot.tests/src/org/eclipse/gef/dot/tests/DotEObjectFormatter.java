/*******************************************************************************
 * Copyright (c) 2017, 2018 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.dot.tests;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * The implementation of this class is mainly taken from the
 * org.eclipse.xpect.xtext.lib.util.EObjectFormatter java class.
 *
 * Modification added: usage of System.lineSeparator instead of '\n' for line
 * separation; usage of tabs instead of two spaces for indentation; format all
 * EStructuralFeatures of an EObject, not only those that are explicitly set
 */
class DotEObjectFormatter implements Function<EObject, String> {

	protected boolean resolveCrossReferences = false;

	protected boolean showIndex = false;

	protected boolean sortFeatures = false;

	protected String lineSep = getLineSeparator();

	public String apply(EObject from) {
		return format(from) + lineSep;
	}

	protected String assignmentOperator(EObject object,
			EStructuralFeature feature) {
		if (feature instanceof EReference
				&& !((EReference) feature).isContainment())
			return "->";
		else
			return "=";
	}

	public String format(EObject object) {
		if (object == null)
			return "null";
		StringBuilder result = new StringBuilder();
		result.append(object.eClass().getName());
		result.append(" {");
		for (EStructuralFeature feature : getAllFeatures(object))
			if (shouldFormat(object, feature))
				result.append(indent(lineSep + format(object, feature)));
		result.append(lineSep + "}");
		return result.toString();
	}

	protected List<EStructuralFeature> getAllFeatures(EObject object) {
		if (!sortFeatures)
			return object.eClass().getEAllStructuralFeatures();
		List<EStructuralFeature> result = Lists
				.newArrayList(object.eClass().getEAllStructuralFeatures());
		Collections.sort(result, new Comparator<EStructuralFeature>() {
			public int compare(EStructuralFeature o1, EStructuralFeature o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		return result;
	}

	protected String format(EObject object, EStructuralFeature feature) {
		StringBuilder result = new StringBuilder();
		result.append(feature.getName());
		result.append(" ");
		result.append(assignmentOperator(object, feature));
		result.append(" ");
		Object val = object.eGet(feature, resolveCrossReferences);
		if (feature.isMany()) {
			result.append("[");
			List<?> vals = (List<?>) val;
			List<?> sortedVals = sort(object, feature, vals);
			if (vals == sortedVals) {
				for (int i = 0; i < vals.size(); i++)
					if (shouldFormat(object, feature, i, vals.get(i)))
						result.append(indent(lineSep
								+ format(object, feature, i, vals.get(i))));
			} else {
				for (int i = 0; i < sortedVals.size(); i++)
					if (shouldFormat(object, feature, sortedVals.get(i)))
						result.append(indent("\n"
								+ format(object, feature, sortedVals.get(i))));
			}
			result.append(lineSep + "]");
		} else
			result.append(format(object, feature, val));
		return result.toString();
	}

	protected String format(EObject object, EStructuralFeature feature,
			int index, Object value) {
		if (showIndex)
			return index + ": " + format(object, feature, value);
		return format(object, feature, value);
	}

	protected String format(EObject object, EStructuralFeature feature,
			Object value) {
		if (feature instanceof EAttribute)
			return formatAttributeValue(object, (EAttribute) feature, value);
		else if (feature instanceof EReference) {
			EReference ref = (EReference) feature;
			if (ref.isContainment())
				return format((EObject) value);
			return formatCrossRefValue(object, ref, (EObject) value);
		}
		return "";
	}

	public String format(Iterable<? extends EObject> object) {
		return Joiner.on(System.lineSeparator())
				.join(Iterables.transform(object, this));
	}

	protected String formatAttributeValue(EObject object, EAttribute feature,
			Object value) {
		if (value == null)
			return "null";
		EFactory factory = feature.getEAttributeType().getEPackage()
				.getEFactoryInstance();
		String stringVal = factory.convertToString(feature.getEAttributeType(),
				value);
		return "'" + stringVal + "'";
	}

	protected String formatCrossRefValue(EObject object, EReference feature,
			EObject value) {
		if (value == null)
			return "null";
		if (value.eIsProxy())
			return "proxy (URI: " + ((InternalEObject) value).eProxyURI() + ")";
		if (value.eResource() == object.eResource())
			return value.eClass().getName() + " "
					+ object.eResource().getURIFragment(value);
		URI uri = EcoreUtil.getURI(value);
		uri = uri.deresolve(object.eResource().getURI());
		return value.eClass().getName() + " " + uri.toString();
	}

	protected String indent(String string) {
		return string.replaceAll("\\n", "\n\t");
	}

	public DotEObjectFormatter resolveCrossReferences() {
		this.resolveCrossReferences = true;
		return this;
	}

	protected boolean shouldFormat(EObject object, EStructuralFeature feature) {
		if (feature.isDerived())
			return false;
		if (feature instanceof EReference
				&& ((EReference) feature).isContainer())
			return false;
		/*
		 * Format all EStructuralFeatures of an EObject, not only those that are
		 * explicitly set (e.g. the default values are implicitly set)
		 */
		return /* object.eIsSet(feature) */ true;
	}

	protected boolean shouldFormat(EObject object, EStructuralFeature feature,
			int index, Object value) {
		return true;
	}

	protected boolean shouldFormat(EObject object, EStructuralFeature feature,
			Object value) {
		return true;
	}

	public DotEObjectFormatter showIndex() {
		this.showIndex = true;
		return this;
	}

	public DotEObjectFormatter sortFeaturesByName() {
		this.sortFeatures = true;
		return this;
	}

	protected List<?> sort(EObject obj, EStructuralFeature feature,
			List<?> values) {
		return values;
	}

	protected String getLineSeparator() {
		return System.lineSeparator();
	}

}