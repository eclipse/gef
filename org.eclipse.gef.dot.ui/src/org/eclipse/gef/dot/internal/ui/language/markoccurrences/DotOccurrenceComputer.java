/*******************************************************************************
 * Copyright (c) 2018 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation (bug #530699)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.markoccurrences;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.gef.dot.internal.language.DotAstHelper;
import org.eclipse.gef.dot.internal.language.dot.DotPackage;
import org.eclipse.gef.dot.internal.language.dot.NodeId;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.xtext.findReferences.TargetURIs;
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
import com.google.inject.Provider;

public class DotOccurrenceComputer extends DefaultOccurrenceComputer {

	@Inject
	private EObjectAtOffsetHelper eObjectAtOffsetHelper;

	@Inject
	private Provider<TargetURIs> targetURIsProvider;

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
								EObject target = eObjectAtOffsetHelper
										.resolveElementAt(resource,
												selection.getOffset());
								if (target != null && !target.eIsProxy()) {
									Iterable<URI> targetURIs = getTargetURIs(
											target);
									if (!(targetURIs instanceof TargetURIs)) {
										TargetURIs result = targetURIsProvider
												.get();
										result.addAllURIs(targetURIs);
										targetURIs = result;
									}

									for (EObject occurrence : getAllOccurrences(
											(TargetURIs) targetURIs,
											resource)) {
										try {
											ITextRegion textRegion = locationInFileProvider
													.getSignificantTextRegion(
															occurrence,
															DotPackage.Literals.NODE_ID__NAME,
															-1);
											addOccurrenceAnnotation(
													OCCURRENCE_ANNOTATION_TYPE,
													document, textRegion,
													annotationMap);
										} catch (Exception exc) {
											// outdated index information.
											// Ignore
										}
									}
									return annotationMap;
								}
							}
							return annotationMap;
						}
					});
		} else {
			return annotationMap;
		}
	}

	private List<? extends EObject> getAllOccurrences(TargetURIs targetURIs,
			Resource resource) {
		for (URI targetURI : targetURIs) {
			EObject eObject = resource.getEObject(targetURI.fragment());
			// currently, only a selection of a nodeId is supported
			if (eObject instanceof NodeId) {
				NodeId selectedNodeId = (NodeId) eObject;
				return DotAstHelper.getAllNodeIds(selectedNodeId);
			}
		}
		return new ArrayList<EObject>();
	}
}
