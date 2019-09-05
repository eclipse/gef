/*******************************************************************************
 * Copyright (c) 2018, 2019 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation (bug #530699)
 *     Zoey Prigge (itemis AG)    - rewrite to include attribute names and values (bug #548911)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.markoccurrences;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.dot.internal.language.DotAstHelper;
import org.eclipse.gef.dot.internal.language.dot.Attribute;
import org.eclipse.gef.dot.internal.language.dot.DotPackage;
import org.eclipse.gef.dot.internal.language.dot.NodeId;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.EObjectAtOffsetHelper;
import org.eclipse.xtext.resource.ILocationInFileProvider;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.ui.editor.occurrences.DefaultOccurrenceComputer;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.util.ITextRegion;
import org.eclipse.xtext.util.concurrent.CancelableUnitOfWork;

import com.google.inject.Inject;

public class DotOccurrenceComputer extends DefaultOccurrenceComputer {

	@Inject
	private EObjectAtOffsetHelper eObjectAtOffsetHelper;

	@Inject
	private ILocationInFileProvider locationInFileProvider;

	/**
	 * The implementation of this method is mainly taken from its super
	 * implementation
	 */
	@Override
	public Map<Annotation, Position> createAnnotationMap(XtextEditor editor,
			final ITextSelection selection, final SubMonitor monitor) {

		Map<Annotation, Position> annotationMap = super.createAnnotationMap(
				editor, selection, monitor);

		final IXtextDocument document = editor.getDocument();
		if (document != null) {
			return document.readOnly(
					new CancelableUnitOfWork<Map<Annotation, Position>, XtextResource>() {

						@Override
						public Map<Annotation, Position> exec(
								XtextResource resource,
								final CancelIndicator cancelIndicator)
								throws Exception {
							if (resource != null) {
								INode node = NodeModelUtils
										.findLeafNodeAtOffset(
												NodeModelUtils.getNode(resource
														.getContents().get(0)),
												selection.getOffset());
								EObject target = eObjectAtOffsetHelper
										.resolveElementAt(resource,
												selection.getOffset());

								List<ITextRegion> textRegions = new ArrayList<>();

								if (target instanceof NodeId
										&& !target.eIsProxy()) {
									textRegions = nodeId((NodeId) target);
								} else if (target instanceof Attribute
										&& !target.eIsProxy()) {
									textRegions = attribute((Attribute) target,
											node);
								}

								for (ITextRegion occurrence : textRegions) {
									try {
										addOccurrenceAnnotation(
												OCCURRENCE_ANNOTATION_TYPE,
												document, occurrence,
												annotationMap);
									} catch (Exception exc) {
										// outdated index information. Ignore
									}
								}
							}
							return annotationMap;
						}

					});
		} else {
			return annotationMap;
		}
	}

	private List<ITextRegion> nodeId(NodeId target) {
		return textRegions(DotAstHelper.getAllNodeIds(target),
				DotPackage.Literals.NODE_ID__NAME);
	}

	private List<ITextRegion> attribute(Attribute target, INode node) {
		if ((target.getName().toValue().equalsIgnoreCase(node.getText()))) {
			return attributeName(target);
		} else {
			return attributeValue(target);
		}
	}

	private List<ITextRegion> attributeValue(Attribute target) {
		return textRegions(DotAstHelper.getAllAttributesSameValue(target),
				DotPackage.Literals.ATTRIBUTE__VALUE);
	}

	private List<ITextRegion> attributeName(Attribute target) {
		return textRegions(DotAstHelper.getAllAttributesSameName(target),
				DotPackage.Literals.ATTRIBUTE__NAME);
	}

	private List<ITextRegion> textRegions(
			List<? extends EObject> allOccurrences, EAttribute feature) {
		List<ITextRegion> textRegions = new ArrayList<>();
		for (EObject occurrence : allOccurrences) {
			try {
				ITextRegion textRegion = locationInFileProvider
						.getSignificantTextRegion(occurrence, feature, -1);
				textRegions.add(textRegion);
			} catch (Exception exc) {
				// outdated index information. Ignore
			}
		}
		return textRegions;
	}
}
