package org.eclipse.gef.dot.internal.parser.conversion;

import org.eclipse.xtext.conversion.IValueConverter;
import org.eclipse.xtext.conversion.ValueConverterException;
import org.eclipse.xtext.conversion.impl.AbstractValueConverter;
import org.eclipse.xtext.nodemodel.INode;

/**
 * A converter that converts terminals that are valid QUOTED_STRING Dot IDs into
 * Strings.
 * 
 * @author anyssen
 *
 */
public class DotQuotedStringValueConverter extends
		AbstractValueConverter<String> implements IValueConverter<String> {

	@Override
	public String toValue(String string, INode node)
			throws ValueConverterException {
		return DotTerminalConverters.unquote(string);
	}

	@Override
	public String toString(String value) throws ValueConverterException {
		return DotTerminalConverters.quote(value);
	}

}
