/*******************************************************************************
 * Copyright (c) 2017, 2019 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API & implementation
 *     Tamas Miklossy  (itemis AG) - minor improvements
 *     Zoey Prigge     (itemis AG) - include parsedAsAttribute (bug #548911)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.eclipse.xtend.lib.macro.AbstractFieldProcessor;
import org.eclipse.xtend.lib.macro.TransformationContext;
import org.eclipse.xtend.lib.macro.declaration.AnnotationReference;
import org.eclipse.xtend.lib.macro.declaration.CompilationStrategy;
import org.eclipse.xtend.lib.macro.declaration.MutableFieldDeclaration;
import org.eclipse.xtend.lib.macro.declaration.MutableMethodDeclaration;
import org.eclipse.xtend.lib.macro.declaration.TypeReference;

/**
 * 
 * An {@link AbstractFieldProcessor} for {@link DotAttribute} annotations.
 * 
 * @author anyssen
 *
 */
public class DotAttributeProcessor extends AbstractFieldProcessor {

	private class ParsedAsAttributeBody implements CompilationStrategy {
		Map<Context, StringBuilder> map = new HashMap<>();

		public ParsedAsAttributeBody() {
			for (Context c : Context.values()) {
				map.put(c, new StringBuilder());
			}
		}

		@Override
		public CharSequence compile(CompilationContext ctx) {
			StringBuilder bodyBuilder = new StringBuilder();
			bodyBuilder.append("switch (context) {\n");
			for (Context caseContext : Context.values()) {
				bodyBuilder.append("case " + caseContext.name() + ":\n");
				bodyBuilder.append(
						"  switch (attrName.toLowerCase(java.util.Locale.ENGLISH)) {\n");
				bodyBuilder.append(map.get(caseContext));
				bodyBuilder.append("  }\n");
			}
			bodyBuilder.append(
					"default:\n  return valueRaw != null ? valueRaw.toValue() : null;\n}\n");
			return bodyBuilder.toString();
		}
	}

	private boolean initialized = false;

	private static Pattern NAMING_PATTERN = Pattern
			.compile("[_A-Z]*[A-Z]+__(G?)(S?)(C?)(N?)(E?)");
	private static Pattern CAMEL_CASE_REPLACEMENT_PATTERN = Pattern
			.compile("(_?[a-z]+)_([a-z]+)");

	/**
	 * Indication of the context in which an attribute is used.
	 */
	// TODO: Use G, N, E, S, C and replace parsing with Enum.valueOf()
	private static enum Context {
		GRAPH, NODE, EDGE, SUBGRAPH, CLUSTER
	}

	private ParsedAsAttributeBody parsedAsAttributeBody = new ParsedAsAttributeBody();

	@Override
	public void doTransform(MutableFieldDeclaration field,
			TransformationContext context) {
		// retrieve name (but do not validate it yet)
		String attributeName = attributeName(field);

		// XXX: Retrieve annotation values and cache them, as the annotation
		// will be removed
		// and the procedures are executed lazily
		String[] attributeRawTypes = (String[]) annotationValue(field, context,
				"rawType");
		TypeReference[] attributeParsedTypes = (TypeReference[]) annotationValue(
				field, context, "parsedType");

		// field comment
		field.setDocComment("The '" + attributeName
				+ "' attribute, which is used by: "
				+ usedBy(field).stream()
						.map((f) -> "Cluster".contentEquals(paramTypeName(f))
								|| "Subgraph".contentEquals(paramTypeName(f))
										? paramTypeName(f)
										: "{@link " + paramTypeName(f) + "}")
						.collect(Collectors.joining(", "))
				+ ".");

		// initialization
		if (!initialized) {
			field.getDeclaringType().findDeclaredMethod("parsedAsAttribute",
					context.newTypeReference(
							"org.eclipse.gef.dot.internal.language.terminals.ID"),
					context.getString(),
					context.newTypeReference(
							"org.eclipse.gef.dot.internal.DotAttributes$Context"))
					.setBody(parsedAsAttributeBody);
		}

		// XXX: Naming conventions is checked by usedBy extension
		List<Context> contexts = uniqueGraphTypes(usedBy(field));
		for (int i = 0; i < contexts.size(); i++) {
			Context c = contexts.get(i);

			// we may specify different values for each context (the order has
			// to match)
			String attributeRawType = attributeRawTypes.length > 1
					? attributeRawTypes[i]
					: attributeRawTypes[0];
			TypeReference attributeParsedType = attributeParsedTypes.length > 1
					? attributeParsedTypes[i]
					: attributeParsedTypes[0];

			// raw getter
			field.getDeclaringType().addMethod(rawGetterName(field),
					(MutableMethodDeclaration method) -> {
						field.markAsRead();

						StringBuilder docComment = new StringBuilder();
						docComment.append(
								"Returns the (raw) value of the {@link #"
										+ field.getSimpleName()
										+ "} attribute of the given {@link "
										+ paramTypeName(c) + "}.\n");
						docComment.append("    @param " + paramName(c) + "\n");
						docComment.append("               The {@link "
								+ paramTypeName(c)
								+ "} for which to return the value of the\n");
						docComment.append("               {@link #"
								+ field.getSimpleName() + "} attribute.\n");
						docComment.append(
								"    @return The (raw) value of the {@link #"
										+ field.getSimpleName()
										+ "} attribute of the given\n");
						docComment.append("            {@link "
								+ paramTypeName(c) + "}.\n");
						method.setDocComment(docComment.toString());
						method.setStatic(true);
						method.addParameter(paramName(c),
								paramType(c, context));
						method.setReturnType(context.newTypeReference(
								"org.eclipse.gef.dot.internal.language.terminals.ID"));

						StringBuilder body = new StringBuilder();
						body.append("return (ID) " + paramName(c)
								+ ".attributesProperty().get("
								+ field.getSimpleName() + ");");
						method.setBody((ctx) -> body.toString());
						context.setPrimarySourceElement(method, field);
					});

			// raw setter
			field.getDeclaringType().addMethod(rawSetterName(field),
					(MutableMethodDeclaration method) -> {
						StringBuilder docComment = new StringBuilder();
						docComment.append("Sets the (raw) value of the {@link #"
								+ field.getSimpleName()
								+ "} attribute of the given {@link "
								+ paramTypeName(c) + "}\n");
						docComment.append("to the given <i>" + attributeName
								+ "</i> value.\n");
						docComment.append("    @param " + paramName(c) + "\n");
						docComment.append("               The {@link "
								+ paramTypeName(c)
								+ "} for which to change the value of the\n");
						docComment.append("               {@link #"
								+ field.getSimpleName() + "} attribute.\n");
						docComment.append("    @param " + attributeName + "\n");
						docComment.append(
								"               The new (raw) value of the {@link #"
										+ field.getSimpleName()
										+ "} attribute.\n");
						docComment.append(
								"    @throws IllegalArgumentException\n");
						docComment.append("               when the given <i>"
								+ attributeName
								+ "</i> value is not supported.\n");
						method.setDocComment(docComment.toString());

						method.setStatic(true);
						method.addParameter(paramName(c),
								paramType(c, context));
						method.addParameter(attributeName,
								context.newTypeReference(
										"org.eclipse.gef.dot.internal.language.terminals.ID"));

						StringBuilder body = new StringBuilder();
						body.append("checkAttributeRawValue(Context."
								+ c.name().toUpperCase() + ", "
								+ field.getSimpleName() + ", " + attributeName
								+ ");\n");
						body.append(paramName(c) + ".attributesProperty().put("
								+ field.getSimpleName() + ", " + attributeName
								+ ");");
						method.setBody((ctx) -> body.toString());
						context.setPrimarySourceElement(method, field);

					});

			field.getDeclaringType().addMethod(getterName(field),
					(MutableMethodDeclaration method) -> {
						field.markAsRead();

						StringBuilder docComment = new StringBuilder();
						docComment.append("Returns the value of the {@link #"
								+ field.getSimpleName()
								+ "} attribute of the given {@link "
								+ paramTypeName(c) + "}.\n");
						docComment.append("    @param " + paramName(c) + "\n");
						docComment.append("           The {@link "
								+ paramTypeName(c)
								+ "} for which to return the value of the {@link #"
								+ field.getSimpleName() + "} attribute.\n");
						docComment
								.append("    @return The value of the {@link #"
										+ field.getSimpleName()
										+ "} attribute of the given {@link "
										+ paramTypeName(c) + "}.\n");
						method.setDocComment(docComment.toString());

						method.setStatic(true);
						method.addParameter(paramName(c),
								paramType(c, context));
						method.setReturnType(
								context.newTypeReference(String.class));

						StringBuilder body = new StringBuilder();
						body.append("ID " + attributeName + "Raw = "
								+ rawGetterName(field) + "(" + paramName(c)
								+ ");\n");
						body.append("return " + attributeName + "Raw != null ? "
								+ attributeName + "Raw.toValue() : null;");
						method.setBody((ctx) -> body.toString());
						context.setPrimarySourceElement(method, field);
						context.setPrimarySourceElement(method, field);
					});

			field.getDeclaringType().addMethod(setterName(field),
					(MutableMethodDeclaration method) -> {
						StringBuilder docComment = new StringBuilder();
						docComment.append("Sets the value of the {@link #"
								+ field.getSimpleName()
								+ "} attribute of the given {@link "
								+ paramTypeName(c) + "} to the given <i>"
								+ attributeName + "</i> value.\n");
						docComment.append("    @param " + paramName(c) + "\n");
						docComment.append("               The {@link "
								+ paramTypeName(c)
								+ "} for which to change the value of the {@link #"
								+ field.getSimpleName() + "} attribute.\n");
						docComment.append("    @param " + attributeName + "\n");
						docComment.append(
								"           The new value of the {@link #"
										+ field.getSimpleName()
										+ "} attribute.\n");
						docComment.append(
								"    @throws IllegalArgumentException\n");
						docComment.append(
								"           when the given <i>" + attributeName
										+ "</i> value is not supported.\n");
						method.setDocComment(docComment.toString());

						method.setStatic(true);
						method.addParameter(paramName(c),
								paramType(c, context));
						method.addParameter(attributeName,
								context.newTypeReference(String.class));

						StringBuilder body = new StringBuilder();
						body.append(rawSetterName(field) + "(" + paramName(c)
								+ ", ID.fromValue(" + attributeName);
						if (!attributeRawType.isEmpty()) {
							body.append(
									", org.eclipse.gef.dot.internal.language.terminals.ID.Type."
											+ attributeRawType);
						}
						body.append("));");
						method.setBody((ctx) -> body.toString());
						context.setPrimarySourceElement(method, field);
					});

			// only generate parsed getters and setters if the parsed type
			// does not equal String
			if (!context.newTypeReference(String.class)
					.equals(attributeParsedType)) {

				// parsed getter
				field.getDeclaringType().addMethod(parsedGetterName(field),
						(MutableMethodDeclaration method) -> {
							field.markAsRead();

							StringBuilder docComment = new StringBuilder();
							docComment.append(
									"Returns the (parsed) value of the {@link #"
											+ field.getSimpleName()
											+ "} attribute of the given {@link "
											+ paramTypeName(c) + "}.\n");
							docComment.append(
									"    @param " + paramName(c) + "\n");
							docComment.append("                The {@link "
									+ paramTypeName(c)
									+ "} for which to return the value of the {@link #"
									+ field.getSimpleName() + "} attribute.\n");
							docComment.append(
									"    @return The (parsed) value of the {@link #"
											+ field.getSimpleName()
											+ "} attribute of the given {@link "
											+ paramTypeName(c) + "}.\n");
							method.setDocComment(docComment.toString());

							method.setStatic(true);
							method.addParameter(paramName(c),
									paramType(c, context));
							method.setReturnType(attributeParsedType);

							StringBuilder body = new StringBuilder();
							body.append(
									"return " + parsed(
											getterName(field) + "("
													+ paramName(c) + ")",
											attributeParsedType) + ";");
							method.setBody((ctx) -> body.toString());
							context.setPrimarySourceElement(method, field);
						});

				// parsed setter
				field.getDeclaringType().addMethod(parsedSetterName(field),
						(MutableMethodDeclaration method) -> {
							field.markAsRead();

							StringBuilder docComment = new StringBuilder();
							docComment.append(
									"Sets the (parsed) value of the {@link #"
											+ field.getSimpleName()
											+ "} attribute of the given {@link "
											+ paramTypeName(c)
											+ "} to the given <i>"
											+ attributeName + "</i> value.\n");
							docComment.append(
									"    @param " + paramName(c) + "\n");
							docComment.append("               The {@link "
									+ paramTypeName(c)
									+ "} for which to change the value of the {@link #"
									+ field.getSimpleName() + "} attribute.\n");
							docComment.append(
									"    @param " + attributeName + "\n");
							docComment.append(
									"               The new (parsed) value of the {@link #"
											+ field.getSimpleName()
											+ "} attribute.\n");
							docComment.append(
									"    @throws IllegalArgumentException\n");
							docComment
									.append("               when the given <i>"
											+ attributeName
											+ "</i> value is not supported.\n");
							method.setDocComment(docComment.toString());

							method.setStatic(true);
							method.addParameter(paramName(c),
									paramType(c, context));
							method.addParameter(attributeName,
									attributeParsedType);

							StringBuilder body = new StringBuilder();
							body.append(
									setterName(field) + "(" + paramName(c)
											+ ", "
											+ serialized(attributeName,
													attributeParsedType)
											+ ");");
							method.setBody((ctx) -> body.toString());
							context.setPrimarySourceElement(method, field);
						});

				parsedAsAttributeBody.map.get(c).append("  case "
						+ field.getSimpleName() + ":\n    return "
						+ parsed("valueRaw != null ? valueRaw.toValue() : null",
								attributeParsedType)
						+ ";\n");
			}
		}

		// XXX: Ensure the DotAttribute annotation is removed from the generated
		// field,
		// so there is no runtime dependency on it.
		for (AnnotationReference reference : field.getAnnotations()) {
			if (context.newTypeReference(DotAttribute.class)
					.equals(context.newTypeReference(
							reference.getAnnotationTypeDeclaration()))) {
				field.removeAnnotation(reference);
			}
		}

	}

	private String serialized(String attributeValue,
			TypeReference attributeParsedType) {
		if (String.class.getName().equals(attributeParsedType.getName())) {
			// no further serialization needed for String
			return attributeValue;
		}
		return "serializeAttributeValue(" + serializer(attributeParsedType)
				+ ", " + attributeValue + ")";
	}

	private String parsed(String attributeValue,
			TypeReference attributeParsedType) {
		if (String.class.getName().equals(attributeParsedType.getName())) {
			// no further parsing needed or string
			return attributeValue;
		}
		return "parseAttributeValue(" + parser(attributeParsedType) + ", "
				+ attributeValue + ")";
	}

	// TODO: handle String and enum values distinctively
	private String serializer(TypeReference attributeParsedType) {
		return dotTypeName(attributeParsedType) + "_SERIALIZER";
	}

	// TODO: handle String and enum values distinctively.
	private String parser(TypeReference attributeParsedType) {
		return dotTypeName(attributeParsedType) + "_PARSER";
	}

	private String dotTypeName(TypeReference attributeParsedType) {
		String dotTypeName = attributeParsedType.getSimpleName().toUpperCase();
		String attributeTypeSimpleName = attributeParsedType.getType()
				.getSimpleName();
		if (Integer.class.getSimpleName().equals(attributeTypeSimpleName)) {
			dotTypeName = "INT";
		} else if (Boolean.class.getSimpleName()
				.equals(attributeTypeSimpleName)) {
			dotTypeName = "BOOL";
		}
		return dotTypeName;
	}

	private List<Context> uniqueGraphTypes(List<Context> contexts) {
		List<Context> uniqueContexts = new ArrayList<>();
		for (Context context : contexts) {
			Context c = context;
			if (c == Context.SUBGRAPH || c == Context.CLUSTER) {
				c = Context.GRAPH;
			}
			if (!uniqueContexts.contains(c)) {
				uniqueContexts.add(c);
			}
		}
		return uniqueContexts;
	}

	private String attributeName(MutableFieldDeclaration field) {
		String rawValue = field.getInitializer().toString()
				.replaceAll("^\"|\"$", "");
		Matcher matcher = CAMEL_CASE_REPLACEMENT_PATTERN.matcher(rawValue);
		if (matcher.matches()) {
			return matcher.group(1) + toFirstUpper(matcher.group(2));
		}
		return rawValue;
	}

	private String rawGetterName(MutableFieldDeclaration field) {
		return getterName(field) + "Raw";
	}

	private String rawSetterName(MutableFieldDeclaration field) {
		return setterName(field) + "Raw";
	}

	private String getterName(MutableFieldDeclaration field) {
		return "get" + toFirstUpper(attributeName(field));
	}

	private String setterName(MutableFieldDeclaration field) {
		return "set" + toFirstUpper(attributeName(field));
	}

	private String toFirstUpper(String s) {
		if (s.length() > 1) {
			return s.substring(0, 1).toUpperCase() + s.substring(1);
		} else if (s.length() == 1) {
			return s.toUpperCase();
		} else {
			return "";
		}
	}

	private String parsedSetterName(MutableFieldDeclaration field) {
		return setterName(field) + "Parsed";
	}

	private String parsedGetterName(MutableFieldDeclaration field) {
		return getterName(field) + "Parsed";
	}

	private Object annotationValue(MutableFieldDeclaration field,
			TransformationContext context, String property) {
		for (AnnotationReference reference : field.getAnnotations()) {
			if (DotAttribute.class.getName().equals(reference
					.getAnnotationTypeDeclaration().getQualifiedName())) {
				return reference.getValue(property);
			}
		}
		throw new IllegalArgumentException("No DotAttribute annotation found.");
	}

	private List<Context> usedBy(MutableFieldDeclaration field) {
		List<Context> applicableContexts = new ArrayList<>();
		Matcher matcher = NAMING_PATTERN.matcher(field.getSimpleName());

		if (!matcher.matches()) {
			throw new IllegalArgumentException(
					"Field name does not match naming pattern "
							+ NAMING_PATTERN);
		}

		// determine which contexts apply
		if (!matcher.group(1).isEmpty()) {
			applicableContexts.add(Context.GRAPH);
		}
		if (!matcher.group(2).isEmpty()) {
			applicableContexts.add(Context.SUBGRAPH);
		}
		if (!matcher.group(3).isEmpty()) {
			applicableContexts.add(Context.CLUSTER);
		}
		if (!matcher.group(4).isEmpty()) {
			applicableContexts.add(Context.NODE);
		}
		if (!matcher.group(5).isEmpty()) {
			applicableContexts.add(Context.EDGE);
		}
		return applicableContexts;
	}

	private String paramName(Context context) {
		return context.name().toLowerCase();
	}

	private String paramTypeName(Context context) {
		return toFirstUpper(context.name().toLowerCase());
	}

	private TypeReference paramType(Context c, TransformationContext context) {
		switch (c) {
		case GRAPH:
			return context.newTypeReference("org.eclipse.gef.graph.Graph");
		case NODE:
			return context.newTypeReference("org.eclipse.gef.graph.Node");
		case EDGE:
			return context.newTypeReference("org.eclipse.gef.graph.Edge");
		default:
			throw new IllegalArgumentException(
					"Cluster and Subgraph not yet supported.");
		}
	}
}