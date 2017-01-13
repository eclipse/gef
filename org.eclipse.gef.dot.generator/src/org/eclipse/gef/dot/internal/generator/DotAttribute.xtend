/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG)  - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.generator

import java.lang.annotation.ElementType
import java.lang.annotation.Target
import java.util.List
import java.util.regex.Pattern
import org.eclipse.xtend.lib.macro.AbstractFieldProcessor
import org.eclipse.xtend.lib.macro.Active
import org.eclipse.xtend.lib.macro.TransformationContext
import org.eclipse.xtend.lib.macro.declaration.MutableFieldDeclaration
import org.eclipse.xtend.lib.macro.declaration.TypeReference

/**
 * Adds a lazy getter and an initializer method.
 */
@Target(ElementType.FIELD)
@Active(DotAttributeProcessor)
annotation DotAttribute {

	/**
	 * A string matching an ID.Type to use for this attribute
	 */
	String[] rawType = #[""]

	/**
	 * Type of the attribute.
	 * 
	 * @return The type of the attribute.
	 */
	Class<?>[] parsedType
}

class DotAttributeProcessor extends AbstractFieldProcessor {

	private static val NAMING_PATTERN = Pattern.compile("[_A-Z]*[A-Z]+__(G?)(N?)(E?)(S?)(C?)");
	private static val CAMEL_CASE_REPLACEMENT_PATTERN = Pattern.compile("(_?[a-z]+)_([a-z]+)")

	/**
	 * Indication of the context in which an attribute is used.
	 */
	 // TODO: Use G, N, E, S, C and replace parsing with Enum.valueOf()
	private static enum Context {
		GRAPH,
		NODE,
		EDGE,
		SUBGRAPH,
		CLUSTER
	}

	override doTransform(MutableFieldDeclaration field, extension TransformationContext context) {
		// retrieve name (but do not validate it yet)
		val attributeName = field.attributeName
		
		//XXX: Retrieve annotation values and cache them, as the annotation will be removed 
		//     and the procedures are executed lazily
		val attributeRawTypes = field.annotationValue(context, "rawType") as String[]
		val attributeParsedTypes = field.annotationValue(context, "parsedType") as TypeReference[]

		// field comment
		field.docComment = '''The '«attributeName»' attribute, which is used by: «field.usedBy.map[
			"{@link " + paramTypeName + "}"].join(", ")».'''

		// XXX: Naming conventions is checked by usedBy extension
		field.usedBy.forEach [ c, i |
			
			// we may specify different values for each context (the order has to match)
			val attributeRawType = if(attributeRawTypes.length > 1) attributeRawTypes.get(i) else attributeRawTypes.get(0)
			val attributeParsedType = if(attributeParsedTypes.length > 1) attributeParsedTypes.get(i) else attributeParsedTypes.get(0)

			// raw getter
			field.declaringType.addMethod(field.rawGetterName) [
				field.markAsRead
				docComment = '''
				Returns the (raw) value of the {@link #«field.simpleName»} attribute of the given {@link «c.paramTypeName»}.
					@param «c.paramName»
					           The {@link «c.paramTypeName»} for which to return the value of the
					           {@link #«field.simpleName»} attribute.
					@return The (raw) value of the {@link #«field.simpleName»} attribute of the given
					        {@link «c.paramTypeName»}.'''
				static = true
				addParameter(c.paramName, c.paramType(context))
				returnType = "org.eclipse.gef.dot.internal.language.terminals.ID".newTypeReference()
				body = [
					'''
						return (ID) «c.paramName».attributesProperty().get(«field.simpleName»);
					'''
				]
				primarySourceElement = field
			]
			// raw setter
			field.declaringType.addMethod(field.rawSetterName) [
				static = true
				addParameter(c.paramName, c.paramType(context))
				addParameter(attributeName, "org.eclipse.gef.dot.internal.language.terminals.ID".newTypeReference())
				body = [
					'''
						checkAttributeRawValue(Context.«c.name.toUpperCase», «field.simpleName», «attributeName»);
						«c.paramName».attributesProperty().put(«field.simpleName», «attributeName»);
					'''
				]
				primarySourceElement = field
			]
			// getter
			field.declaringType.addMethod(field.getterName) [
				field.markAsRead
				docComment = '''
				Returns the value of the {@link #«field.simpleName»} attribute of the given {@link «c.paramTypeName»}.
				 @param «c.paramName»
				            The {@link «c.paramTypeName»} for which to return the value of the
				            {@link #«field.simpleName»} attribute.
				 @return The value of the {@link #«field.simpleName»} attribute of the given
				         {@link «c.paramTypeName»}.'''
				static = true
				addParameter(c.paramName, c.paramType(context))
				returnType = String.newTypeReference()
				body = [
					'''
						ID «attributeName»Raw = «field.rawGetterName»(«c.paramName»);
						return «attributeName»Raw != null ? «attributeName»Raw.toValue() : null;
					''']
				primarySourceElement = field
			]
			// setter
			field.declaringType.addMethod(field.setterName) [
				static = true
				addParameter(c.paramName, c.paramType(context))
				addParameter(attributeName, String.newTypeReference())
				body = [
					'''
						«field.rawSetterName»(«c.paramName», ID.fromValue(«attributeName»«IF !attributeRawType.empty», org.eclipse.gef.dot.internal.language.terminals.ID.Type.«attributeRawType»«ENDIF»));
					'''
				]
				primarySourceElement = field
			]
			// parsed getter
			field.declaringType.addMethod(field.parsedGetterName) [
				field.markAsRead
				docComment = '''
				Returns the (parsed) value of the {@link #«field.simpleName»} attribute of the given {@link «c.paramTypeName»}.
				 @param «c.paramName»
				            The {@link «c.paramTypeName»} for which to return the value of the
				            {@link #«field.simpleName»} attribute.
				 @return The (parsed) value of the {@link #«field.simpleName»} attribute of the given
				         {@link «c.paramTypeName»}.'''
				static = true
				addParameter(c.paramName, c.paramType(context))
				returnType = attributeParsedType
				body = [
					'''
						return «parsed(field.getterName + "(" + c.paramName + ")", attributeParsedType)»;
					''']
				primarySourceElement = field
			]
			// parsed setter
			field.declaringType.addMethod(field.parsedSetterName) [
				static = true
				addParameter(c.paramName, c.paramType(context))
				addParameter(attributeName, attributeParsedType)
				body = [
					'''
						«field.setterName»(«c.paramName», «attributeName.serialized(attributeParsedType)»);
					'''
				]
				primarySourceElement = field
			]
		]

		//XXX: Ensure the DotAttribute annotation is removed from the generated field, 
		//     so there is no runtime dependency on it.
		field.annotations.filter[annotationTypeDeclaration.newTypeReference() == DotAttribute.newTypeReference()].
			forEach[field.removeAnnotation(it)]
	}
	
	def String serialized(String attributeValue, TypeReference attributeParsedType) {
		if(String.name == attributeParsedType.name) {
			// no further serialization needed for String
			return attributeValue
		}
		return "serializeAttributeValue(" + attributeParsedType.serializer + ", " + attributeValue + ")"
	}
	
	def String parsed(String attributeValue, TypeReference attributeParsedType) {
		if(String.name == attributeParsedType.name) {
			// no further parsing needed or string
			return attributeValue
		}
		return "parseAttributeValue(" + attributeParsedType.parser + ", " + attributeValue + ")"
	}
	
	// TODO: handle String and enum values distinctively
	def String serializer(TypeReference attributeParsedType) {
		return dotTypeName(attributeParsedType) + "_SERIALIZER"
	}
	
	// TODO: handle String and enum values distinctively.
	def String parser(TypeReference attributeParsedType) {
		return dotTypeName(attributeParsedType) + "_PARSER"
	}
	
	def dotTypeName(TypeReference attributeParsedType) {
		var dotTypeName = attributeParsedType.simpleName.toUpperCase;
		switch(attributeParsedType.type.simpleName) {
			case Integer.simpleName : dotTypeName = "INT"
			case Boolean.simpleName : dotTypeName = "BOOL"
		}
		dotTypeName
	}
	
	def List<Context> uniqueGraphTypes(List<Context> contexts) {
		contexts.map [
			switch (it) {
				case Context.SUBGRAPH: Context.GRAPH
				case Context.CLUSTER: Context.GRAPH
				default: it
			}
		].toSet.sortBy[name].toList
	}

	private def attributeName(MutableFieldDeclaration field) {
		val rawValue = field.initializer.toString.replaceAll("^\"|\"$", "")
		val matcher = CAMEL_CASE_REPLACEMENT_PATTERN.matcher(rawValue)
		if(matcher.matches()) {
			return matcher.group(1) + matcher.group(2).toFirstUpper
		}
		return rawValue
	}

	private def rawGetterName(MutableFieldDeclaration field) {
		field.getterName + 'Raw'
	}

	private def rawSetterName(MutableFieldDeclaration field) {
		field.setterName + 'Raw'
	}

	private def getterName(MutableFieldDeclaration field) {
		'get' + field.attributeName.toFirstUpper
	}

	private def setterName(MutableFieldDeclaration field) {
		'set' + field.attributeName.toFirstUpper
	}

	private def parsedSetterName(MutableFieldDeclaration field) {
		field.setterName + "Parsed"
	}

	private def parsedGetterName(MutableFieldDeclaration field) {
		field.getterName + "Parsed"
	}

	private def annotationValue(MutableFieldDeclaration field, extension TransformationContext context, String property) {
		field.annotations.filter[annotationTypeDeclaration.qualifiedName == DotAttribute.name].head.getValue(property)
	}

	private def List<Context> usedBy(MutableFieldDeclaration field) {
		val List<Context> applicableContexts = newArrayList
		val matcher = NAMING_PATTERN.matcher(field.simpleName);
		if (!matcher.matches()) {
			throw new IllegalArgumentException("Field name does not match naming pattern " + NAMING_PATTERN)
		}

		// determine which contexts apply
		if (!matcher.group(1).empty)
			applicableContexts += Context.GRAPH
		if (!matcher.group(2).empty)
			applicableContexts += Context.NODE
		if (!matcher.group(3).empty)
			applicableContexts += Context.EDGE
		if (!matcher.group(4).empty)
			applicableContexts += Context.SUBGRAPH
		if (!matcher.group(5).empty)
			applicableContexts += Context.CLUSTER
		return applicableContexts
	}

	private def paramName(Context it) {
		name.toLowerCase
	}

	private def paramTypeName(Context it) {
		name.toLowerCase.toFirstUpper
	}

	private def paramType(Context c, extension TransformationContext context) {
		switch (c) {
			case Context.GRAPH:
				"org.eclipse.gef.graph.Graph".newTypeReference()
			case Context.NODE:
				"org.eclipse.gef.graph.Node".newTypeReference()
			case Context.EDGE:
				"org.eclipse.gef.graph.Edge".newTypeReference()
			default:
				throw new IllegalArgumentException("Cluster and Subgraph not yet supported.")
		}
	}
}
