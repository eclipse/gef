/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Zoey Gerrit Prigge  - initial API and implementation (bug #454629)
 *    
 *******************************************************************************/
package org.eclipse.gef.dot.internal.language.validation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.SyntaxErrorMessage;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.validation.ValidationMessageAcceptor;

/**
 * This class provides a ValidationMessageAcceptor to be used with sub grammars.
 * It can be used when validating the main grammar to translate validation
 * errors.
 * 
 * @author zgerritprigge
 *
 */
public class ConvertingValidationMessageAcceptor
		implements ValidationMessageAcceptor {

	private final EObject hostingEObject;
	private final ValidationMessageAcceptor hostMessageAcceptor;
	private final String userReadableIdentifier;
	private final int initialOffset;

	/**
	 * Constructs a Validation Message acceptor to 'translate' issues while
	 * validating a sub grammar.
	 * 
	 * @param hostingEObject
	 *            The Object in the main grammar that hosts the sub grammar
	 * @param hostingFeature
	 *            Can be null. If set, needs to be unique within hostingEObject
	 *            (not a list, ...).
	 * @param userReadableIdentifier
	 *            A name for the sub grammar understood by the user
	 * @param hostMessageAcceptor
	 *            The validationMessageAcceptor of the host grammar
	 * @param internalOffset
	 *            Offset from begin of feature
	 */
	public ConvertingValidationMessageAcceptor(EObject hostingEObject,
			EStructuralFeature hostingFeature, String userReadableIdentifier,
			ValidationMessageAcceptor hostMessageAcceptor, int internalOffset) {

		this.hostingEObject = hostingEObject;
		this.hostMessageAcceptor = hostMessageAcceptor;
		this.userReadableIdentifier = userReadableIdentifier;

		this.initialOffset = calculateInitialOffset(hostingFeature,
				internalOffset);
	}

	/**
	 * Helper Method to allow this message acceptor to handle syntax errors
	 * issued while parsing the sub grammar as validation issues of the main
	 * grammar.
	 * 
	 * @param error
	 *            Error node from parsing.
	 */
	public void acceptSyntaxError(INode error) {
		SyntaxErrorMessage errorMessage = error.getSyntaxErrorMessage();
		hostMessageAcceptor.acceptError(
				buildMessage("Syntax error", errorMessage.getMessage()),
				hostingEObject, calculateOffset(error.getOffset()),
				error.getLength(), errorMessage.getIssueCode(),
				errorMessage.getIssueData());
	}

	@Override
	public void acceptError(String message, EObject object,
			EStructuralFeature feature, int index, String code,
			String... issueData) {
		for (INode node : getNodesForEObject(object, feature))
			hostMessageAcceptor.acceptError(
					buildMessage("Semantic error", message), hostingEObject,
					calculateOffset(node.getOffset()), node.getLength(), code,
					issueData);
	}

	@Override
	public void acceptError(String message, EObject object, int offset,
			int length, String code, String... issueData) {
		hostMessageAcceptor.acceptError(buildMessage("Semantic error", message),
				hostingEObject, calculateOffset(offset), length, code,
				issueData);
	}

	@Override
	public void acceptInfo(String message, EObject object,
			EStructuralFeature feature, int index, String code,
			String... issueData) {
		for (INode node : getNodesForEObject(object, feature))
			hostMessageAcceptor.acceptInfo(
					buildMessage("Semantic issue info", message),
					hostingEObject, calculateOffset(node.getOffset()),
					node.getLength(), code, issueData);
	}

	@Override
	public void acceptInfo(String message, EObject object, int offset,
			int length, String code, String... issueData) {
		hostMessageAcceptor.acceptInfo(
				buildMessage("Semantic issue info", message), hostingEObject,
				calculateOffset(offset), length, code, issueData);
	}

	@Override
	public void acceptWarning(String message, EObject object,
			EStructuralFeature feature, int index, String code,
			String... issueData) {
		for (INode node : getNodesForEObject(object, feature))
			hostMessageAcceptor.acceptWarning(
					buildMessage("Semantic warning", message), hostingEObject,
					calculateOffset(node.getOffset()), node.getLength(), code,
					issueData);

	}

	@Override
	public void acceptWarning(String message, EObject object, int offset,
			int length, String code, String... issueData) {
		hostMessageAcceptor.acceptWarning(
				buildMessage("Semantic warning", message), hostingEObject,
				calculateOffset(offset), length, code, issueData);
	}

	private int calculateInitialOffset(EStructuralFeature hostingFeature,
			int internalOffset) {
		List<INode> nodes = getNodesForEObject(hostingEObject, hostingFeature);
		if (nodes.size() != 1)
			throw new RuntimeException(
					"EObject has no node or feature not unique");
		return nodes.get(0).getOffset() + internalOffset;
	}

	private int calculateOffset(int offset) {
		// in theory, initialOffset and offset should always be positive.
		// but if one is negative, adding them seems useless.
		// TODO checked if and when and how to handle
		return offset >= 0 && initialOffset >= 0 ? offset + initialOffset
				: offset;
	}

	private List<INode> getNodesForEObject(EObject eObject,
			EStructuralFeature eStructuralFeature) {
		List<INode> nodes = NodeModelUtils.findNodesForFeature(eObject,
				eStructuralFeature);
		// if the result is empty, possibly the feature is not set
		// hence we try to find the note for the entire eObject
		if (nodes.size() == 0) {
			nodes = new ArrayList<INode>();
			nodes.add(NodeModelUtils.findActualNodeFor(eObject));
		}
		return nodes;
	}

	private String buildMessage(String errorType, String errorMessage) {
		StringBuilder message = new StringBuilder();
		message.append(errorType).append(" on ")
				.append(hostingEObject.eClass().getName()).append(" ")
				.append(userReadableIdentifier).append(": ");
		message.append(errorMessage);
		String userMessage = message.toString();
		return userMessage;
	}

}
