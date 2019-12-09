/*******************************************************************************
 * Copyright (c) 2017, 2019 itemis AG and others.
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

import static com.google.common.collect.Iterables.concat;
import static org.eclipse.xtext.EcoreUtil2.getAllContentsOfType;
import static org.eclipse.xtext.xbase.lib.IterableExtensions.filter;

import org.eclipse.gef.dot.internal.language.recordlabel.Field;
import org.eclipse.gef.dot.internal.language.recordlabel.FieldID;
import org.eclipse.gef.dot.internal.language.recordlabel.RLabel;
import org.eclipse.gef.dot.internal.language.recordlabel.RecordlabelPackage;
import org.eclipse.xtext.validation.Check;

import com.google.common.collect.HashMultimap;

/**
 * This class contains custom validation rules.
 *
 * See
 * https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#validation
 */
public class DotRecordLabelValidator extends AbstractDotRecordLabelValidator {

	/**
	 * Snippet including the subgrammar issue code prefix
	 */
	public final static String ISSUE_CODE_PREFIX = "org.eclipse.gef.dot.internal.language.dotRecordLabel.";

	/**
	 * Issue code for a duplicate port name error
	 */
	public final static String PORT_NAME_DUPLICATE = (ISSUE_CODE_PREFIX
			+ "PortNameDuplicate");

	/**
	 * Issue code for a unset port name warning
	 */
	public final static String PORT_NAME_NOT_SET = (ISSUE_CODE_PREFIX
			+ "PortNameNotSet");

	/**
	 * Checks that if a port is specified, it has a name which allows
	 * referencing.
	 *
	 * @param fieldID
	 *            FieldID to be checked.
	 */
	@Check
	public void checkPortNameIsNotNull(final FieldID fieldID) {
		if (!fieldID.isPortNamed())
			return;
		String name = fieldID.getPort();
		if (name == null) {
			warning("Port unnamed: port cannot be referenced",
					RecordlabelPackage.Literals.FIELD_ID__PORT,
					PORT_NAME_NOT_SET);
		}
	}

	/**
	 * Checks that if a record based label has multiple ports none have the same
	 * name
	 * 
	 * @param label
	 *            RecordLabel to be checked.
	 */
	@Check
	public void checkMultiplePortsNotSameName(final RLabel label) {
		/*
		 * We shall only check the top layer label
		 */
		if (label.eContainer() instanceof Field)
			return;

		final HashMultimap<String, FieldID> fieldsMappedToPortname = HashMultimap
				.create();

		Iterable<FieldID> allPortsWithNames = filter(
				getAllContentsOfType(label, FieldID.class),
				field -> field.getPort() != null);

		for (final FieldID field : allPortsWithNames) {
			fieldsMappedToPortname.put(field.getPort(), field);
		}

		/*
		 * No two ports are allowed the same name, the multimap's values are
		 * grouped by key (asMap), then filtered by collection size. All
		 * remaining ports shall yield an error.
		 */
		Iterable<FieldID> allMisnamedPorts = concat(
				filter(fieldsMappedToPortname.asMap().values(),
						collection -> collection.size() > 1));

		for (final FieldID misnamedPort : allMisnamedPorts) {
			error("Port name not unique: " + misnamedPort.getPort(),
					misnamedPort,
					RecordlabelPackage.eINSTANCE.getFieldID_Port(),
					PORT_NAME_DUPLICATE);
		}
	}

}
