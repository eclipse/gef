/*******************************************************************************
 * Copyright (c) 2016, 2018 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation (bug #461506)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.language.color;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

/**
 * The color information contained by this class has been taken from the
 * graphviz website: http://www.graphviz.org/content/color-names
 *
 * @author miklossy
 *
 */
public class DotColors {

	/**
	 * Returns the valid dot color scheme names.
	 *
	 * @return The list of valid dot color scheme names.
	 */
	public static List<String> getColorSchemes() {
		List<String> colorSchemes = new ArrayList<>();

		colorSchemes.add("x11"); //$NON-NLS-1$
		colorSchemes.add("svg"); //$NON-NLS-1$
		colorSchemes.addAll(brewerColorSchemes.keySet());

		return colorSchemes;
	}

	/**
	 * Returns the valid dot color names defined within the given
	 * <i>colorScheme</i>.
	 *
	 * @param colorScheme
	 *            The name of the color scheme.
	 * @return The list of valid dot color names defined within the given
	 *         <i>colorScheme</i>.
	 */
	public static List<String> getColorNames(String colorScheme) {
		List<String> colorNames = new ArrayList<>();

		switch (colorScheme) {
		case "x11": //$NON-NLS-1$
			colorNames.addAll(x11ColorScheme.keySet());
			break;
		case "svg": //$NON-NLS-1$
			colorNames.addAll(svgColorScheme.keySet());
			break;
		default:
			String[] colorValuesArray = brewerColorSchemes.get(colorScheme);
			if (colorValuesArray != null) {
				for (int i = 0; i < colorValuesArray.length; i++) {
					colorNames.add(Integer.toString(i + 1));
				}
			}
		}

		return colorNames;
	}

	/**
	 * Calculates the detailed description in html form providing more
	 * information about the given color.
	 *
	 * @param colorScheme
	 *            The name of the color scheme, can be null.
	 *
	 * @param colorName
	 *            The name of the color, can be null.
	 *
	 * @param colorCode
	 *            The hex code of the color, should not be null.
	 *
	 * @return the detailed description in html form
	 */
	public static String getColorDescription(String colorScheme,
			String colorName, String colorCode) {
		String nl = System.lineSeparator();
		StringBuilder sb = new StringBuilder();

		sb.append("<table border=1>" + nl);
		sb.append("	<tr>" + nl);
		sb.append("		<td><b>color preview</b></td>" + nl);
		if (colorScheme != null) {
			sb.append("		<td><b>color scheme</b></td>" + nl);
		}
		if (colorName != null) {
			sb.append("		<td><b>color name</b></td>" + nl);
		}
		sb.append("		<td><b>color code</b></td>" + nl);
		sb.append("	</tr>" + nl);
		sb.append("	<tr>" + nl);
		sb.append(
				"		<td border=0 align=\"center\"><div style=\"border:1px solid black;width:50px;height:16px;background-color:");
		sb.append(colorCode);
		sb.append(";\"</div></td>" + nl);
		if (colorScheme != null) {
			sb.append("		<td align=\"center\">" + colorScheme + "</td>"
					+ nl);
		}
		if (colorName != null) {
			sb.append("		<td align=\"center\">" + colorName + "</td>" + nl);
		}
		sb.append("		<td align=\"center\">" + colorCode + "</td>" + nl);
		sb.append("	</tr>" + nl);
		sb.append("</table>" + nl);

		return sb.toString();
	}

	/**
	 * Returns the color code (in hexadecimal form) of the given
	 * <i>colorName</i> considering the given <i>colorScheme</i>, or null if the
	 * color code cannot be determined.
	 *
	 * @param colorScheme
	 *            The name of the color scheme.
	 *
	 * @param colorName
	 *            The name of the color.
	 *
	 * @return the color code or null if the color code cannot be determined.
	 */
	public static String get(String colorScheme, String colorName) {
		switch (colorScheme) {
		case "x11": //$NON-NLS-1$
			return x11ColorScheme.get(colorName);
		case "svg": //$NON-NLS-1$
			return svgColorScheme.get(colorName);
		default:
			String[] colorValuesArray = brewerColorSchemes.get(colorScheme);
			if (colorValuesArray != null) {
				int colorID;
				try {
					colorID = Integer.parseInt(colorName);
				} catch (NumberFormatException e) {
					return null;
				}
				if (colorID > 0 && colorValuesArray.length >= colorID) {
					return colorValuesArray[colorID - 1];
				}
			}
		}
		return null;
	}

	/**
	 * @return The default color used for filling the nodes having the styled
	 *         'filled', but neither 'fillcolor' nor 'color' values is
	 *         explicitly specified.
	 */
	public static Color getDefaultNodeFillColor() {
		StringColor color = ColorFactory.eINSTANCE.createStringColor();
		color.setScheme("svg");
		color.setName("lightgrey");
		return color;
	}

	private static Map<String, String> x11ColorScheme = ImmutableMap
			.<String, String> builder().put("aliceblue", "#f0f8ff") //$NON-NLS-1$ //$NON-NLS-2$
			.put("antiquewhite", "#faebd7").put("antiquewhite1", "#ffefdb") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("antiquewhite2", "#eedfcc").put("antiquewhite3", "#cdc0b0") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("antiquewhite4", "#8b8378").put("aquamarine", "#7fffd4") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("aquamarine1", "#7fffd4").put("aquamarine2", "#76eec6") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("aquamarine3", "#66cdaa").put("aquamarine4", "#458b74") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("azure", "#f0ffff").put("azure1", "#f0ffff") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("azure2", "#e0eeee").put("azure3", "#c1cdcd") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("azure4", "#838b8b").put("beige", "#f5f5dc") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("bisque", "#ffe4c4").put("bisque1", "#ffe4c4") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("bisque2", "#eed5b7").put("bisque3", "#cdb79e") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("bisque4", "#8b7d6b").put("black", "#000000") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("blanchedalmond", "#ffebcd").put("blue", "#0000ff") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("blue1", "#0000ff").put("blue2", "#0000ee") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("blue3", "#0000cd").put("blue4", "#00008b") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("blueviolet", "#8a2be2").put("brown", "#a52a2a") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("brown1", "#ff4040").put("brown2", "#ee3b3b") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("brown3", "#cd3333").put("brown4", "#8b2323") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("burlywood", "#deb887").put("burlywood1", "#ffd39b") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("burlywood2", "#eec591").put("burlywood3", "#cdaa7d") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("burlywood4", "#8b7355").put("cadetblue", "#5f9ea0") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("cadetblue1", "#98f5ff").put("cadetblue2", "#8ee5ee") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("cadetblue3", "#7ac5cd").put("cadetblue4", "#53868b") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("chartreuse", "#7fff00").put("chartreuse1", "#7fff00") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("chartreuse2", "#76ee00").put("chartreuse3", "#66cd00") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("chartreuse4", "#458b00").put("chocolate", "#d2691e") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("chocolate1", "#ff7f24").put("chocolate2", "#ee7621") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("chocolate3", "#cd661d").put("chocolate4", "#8b4513") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("coral", "#ff7f50").put("coral1", "#ff7256") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("coral2", "#ee6a50").put("coral3", "#cd5b45") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("coral4", "#8b3e2f").put("cornflowerblue", "#6495ed") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("cornsilk", "#fff8dc").put("cornsilk1", "#fff8dc") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("cornsilk2", "#eee8cd").put("cornsilk3", "#cdc8b1") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("cornsilk4", "#8b8878").put("crimson", "#dc143c") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("cyan", "#00ffff").put("cyan1", "#00ffff") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("cyan2", "#00eeee").put("cyan3", "#00cdcd") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("cyan4", "#008b8b").put("darkgoldenrod", "#b8860b") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("darkgoldenrod1", "#ffb90f").put("darkgoldenrod2", "#eead0e") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("darkgoldenrod3", "#cd950c").put("darkgoldenrod4", "#8b6508") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("darkgreen", "#006400").put("darkkhaki", "#bdb76b") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("darkolivegreen", "#556b2f").put("darkolivegreen1", "#caff70") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("darkolivegreen2", "#bcee68").put("darkolivegreen3", "#a2cd5a") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("darkolivegreen4", "#6e8b3d").put("darkorange", "#ff8c00") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("darkorange1", "#ff7f00").put("darkorange2", "#ee7600") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("darkorange3", "#cd6600").put("darkorange4", "#8b4500") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("darkorchid", "#9932cc").put("darkorchid1", "#bf3eff") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("darkorchid2", "#b23aee").put("darkorchid3", "#9a32cd") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("darkorchid4", "#68228b").put("darksalmon", "#e9967a") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("darkseagreen", "#8fbc8f").put("darkseagreen1", "#c1ffc1") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("darkseagreen2", "#b4eeb4").put("darkseagreen3", "#9bcd9b") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("darkseagreen4", "#698b69").put("darkslateblue", "#483d8b") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("darkslategray", "#2f4f4f").put("darkslategray1", "#97ffff") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("darkslategray2", "#8deeee").put("darkslategray3", "#79cdcd") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("darkslategray4", "#528b8b").put("darkslategrey", "#2f4f4f") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("darkturquoise", "#00ced1").put("darkviolet", "#9400d3") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("deeppink", "#ff1493").put("deeppink1", "#ff1493") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("deeppink2", "#ee1289").put("deeppink3", "#cd1076") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("deeppink4", "#8b0a50").put("deepskyblue", "#00bfff") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("deepskyblue1", "#00bfff").put("deepskyblue2", "#00b2ee") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("deepskyblue3", "#009acd").put("deepskyblue4", "#00688b") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("dimgray", "#696969").put("dimgrey", "#696969") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("dodgerblue", "#1e90ff").put("dodgerblue1", "#1e90ff") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("dodgerblue2", "#1c86ee").put("dodgerblue3", "#1874cd") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("dodgerblue4", "#104e8b").put("firebrick", "#b22222") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("firebrick1", "#ff3030").put("firebrick2", "#ee2c2c") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("firebrick3", "#cd2626").put("firebrick4", "#8b1a1a") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("floralwhite", "#fffaf0").put("forestgreen", "#228b22") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gainsboro", "#dcdcdc").put("ghostwhite", "#f8f8ff") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gold", "#ffd700").put("gold1", "#ffd700") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gold2", "#eec900").put("gold3", "#cdad00") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gold4", "#8b7500").put("goldenrod", "#daa520") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("goldenrod1", "#ffc125").put("goldenrod2", "#eeb422") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("goldenrod3", "#cd9b1d").put("goldenrod4", "#8b6914") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gray", "#c0c0c0").put("gray0", "#000000") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gray1", "#030303").put("gray10", "#1a1a1a") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gray100", "#ffffff").put("gray11", "#1c1c1c") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gray12", "#1f1f1f").put("gray13", "#212121") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gray14", "#242424").put("gray15", "#262626") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gray16", "#292929").put("gray17", "#2b2b2b") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gray18", "#2e2e2e").put("gray19", "#303030") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gray2", "#050505").put("gray20", "#333333") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gray21", "#363636").put("gray22", "#383838") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gray23", "#3b3b3b").put("gray24", "#3d3d3d") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gray25", "#404040").put("gray26", "#424242") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gray27", "#454545").put("gray28", "#474747") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gray29", "#4a4a4a").put("gray3", "#080808") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gray30", "#4d4d4d").put("gray31", "#4f4f4f") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gray32", "#525252").put("gray33", "#545454") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gray34", "#575757").put("gray35", "#595959") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gray36", "#5c5c5c").put("gray37", "#5e5e5e") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gray38", "#616161").put("gray39", "#636363") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gray4", "#0a0a0a").put("gray40", "#666666") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gray41", "#696969").put("gray42", "#6b6b6b") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gray43", "#6e6e6e").put("gray44", "#707070") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gray45", "#737373").put("gray46", "#757575") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gray47", "#787878").put("gray48", "#7a7a7a") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gray49", "#7d7d7d").put("gray5", "#0d0d0d") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gray50", "#7f7f7f").put("gray51", "#828282") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gray52", "#858585").put("gray53", "#878787") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gray54", "#8a8a8a").put("gray55", "#8c8c8c") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gray56", "#8f8f8f").put("gray57", "#919191") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gray58", "#949494").put("gray59", "#969696") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gray6", "#0f0f0f").put("gray60", "#999999") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gray61", "#9c9c9c").put("gray62", "#9e9e9e") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gray63", "#a1a1a1").put("gray64", "#a3a3a3") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gray65", "#a6a6a6").put("gray66", "#a8a8a8") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gray67", "#ababab").put("gray68", "#adadad") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gray69", "#b0b0b0").put("gray7", "#121212") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gray70", "#b3b3b3").put("gray71", "#b5b5b5") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gray72", "#b8b8b8").put("gray73", "#bababa") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gray74", "#bdbdbd").put("gray75", "#bfbfbf") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gray76", "#c2c2c2").put("gray77", "#c4c4c4") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gray78", "#c7c7c7").put("gray79", "#c9c9c9") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gray8", "#141414").put("gray80", "#cccccc") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gray81", "#cfcfcf").put("gray82", "#d1d1d1") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gray83", "#d4d4d4").put("gray84", "#d6d6d6") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gray85", "#d9d9d9").put("gray86", "#dbdbdb") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gray87", "#dedede").put("gray88", "#e0e0e0") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gray89", "#e3e3e3").put("gray9", "#171717") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gray90", "#e5e5e5").put("gray91", "#e8e8e8") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gray92", "#ebebeb").put("gray93", "#ededed") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gray94", "#f0f0f0").put("gray95", "#f2f2f2") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gray96", "#f5f5f5").put("gray97", "#f7f7f7") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gray98", "#fafafa").put("gray99", "#fcfcfc") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("green", "#00ff00").put("green1", "#00ff00") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("green2", "#00ee00").put("green3", "#00cd00") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("green4", "#008b00").put("greenyellow", "#adff2f") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("grey", "#c0c0c0").put("grey0", "#000000") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("grey1", "#030303").put("grey10", "#1a1a1a") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("grey100", "#ffffff").put("grey11", "#1c1c1c") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("grey12", "#1f1f1f").put("grey13", "#212121") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("grey14", "#242424").put("grey15", "#262626") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("grey16", "#292929").put("grey17", "#2b2b2b") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("grey18", "#2e2e2e").put("grey19", "#303030") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("grey2", "#050505").put("grey20", "#333333") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("grey21", "#363636").put("grey22", "#383838") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("grey23", "#3b3b3b").put("grey24", "#3d3d3d") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("grey25", "#404040").put("grey26", "#424242") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("grey27", "#454545").put("grey28", "#474747") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("grey29", "#4a4a4a").put("grey3", "#080808") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("grey30", "#4d4d4d").put("grey31", "#4f4f4f") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("grey32", "#525252").put("grey33", "#545454") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("grey34", "#575757").put("grey35", "#595959") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("grey36", "#5c5c5c").put("grey37", "#5e5e5e") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("grey38", "#616161").put("grey39", "#636363") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("grey4", "#0a0a0a").put("grey40", "#666666") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("grey41", "#696969").put("grey42", "#6b6b6b") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("grey43", "#6e6e6e").put("grey44", "#707070") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("grey45", "#737373").put("grey46", "#757575") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("grey47", "#787878").put("grey48", "#7a7a7a") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("grey49", "#7d7d7d").put("grey5", "#0d0d0d") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("grey50", "#7f7f7f").put("grey51", "#828282") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("grey52", "#858585").put("grey53", "#878787") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("grey54", "#8a8a8a").put("grey55", "#8c8c8c") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("grey56", "#8f8f8f").put("grey57", "#919191") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("grey58", "#949494").put("grey59", "#969696") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("grey6", "#0f0f0f").put("grey60", "#999999") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("grey61", "#9c9c9c").put("grey62", "#9e9e9e") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("grey63", "#a1a1a1").put("grey64", "#a3a3a3") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("grey65", "#a6a6a6").put("grey66", "#a8a8a8") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("grey67", "#ababab").put("grey68", "#adadad") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("grey69", "#b0b0b0").put("grey7", "#121212") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("grey70", "#b3b3b3").put("grey71", "#b5b5b5") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("grey72", "#b8b8b8").put("grey73", "#bababa") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("grey74", "#bdbdbd").put("grey75", "#bfbfbf") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("grey76", "#c2c2c2").put("grey77", "#c4c4c4") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("grey78", "#c7c7c7").put("grey79", "#c9c9c9") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("grey8", "#141414").put("grey80", "#cccccc") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("grey81", "#cfcfcf").put("grey82", "#d1d1d1") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("grey83", "#d4d4d4").put("grey84", "#d6d6d6") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("grey85", "#d9d9d9").put("grey86", "#dbdbdb") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("grey87", "#dedede").put("grey88", "#e0e0e0") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("grey89", "#e3e3e3").put("grey9", "#171717") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("grey90", "#e5e5e5").put("grey91", "#e8e8e8") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("grey92", "#ebebeb").put("grey93", "#ededed") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("grey94", "#f0f0f0").put("grey95", "#f2f2f2") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("grey96", "#f5f5f5").put("grey97", "#f7f7f7") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("grey98", "#fafafa").put("grey99", "#fcfcfc") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("honeydew", "#f0fff0").put("honeydew1", "#f0fff0") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("honeydew2", "#e0eee0").put("honeydew3", "#c1cdc1") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("honeydew4", "#838b83").put("hotpink", "#ff69b4") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("hotpink1", "#ff6eb4").put("hotpink2", "#ee6aa7") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("hotpink3", "#cd6090").put("hotpink4", "#8b3a62") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("indianred", "#cd5c5c").put("indianred1", "#ff6a6a") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("indianred2", "#ee6363").put("indianred3", "#cd5555") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("indianred4", "#8b3a3a").put("indigo", "#4b0082") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("invis", "#fffffe").put("ivory", "#fffff0") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("ivory1", "#fffff0").put("ivory2", "#eeeee0") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("ivory3", "#cdcdc1").put("ivory4", "#8b8b83") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("khaki", "#f0e68c").put("khaki1", "#fff68f") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("khaki2", "#eee685").put("khaki3", "#cdc673") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("khaki4", "#8b864e").put("lavender", "#e6e6fa") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("lavenderblush", "#fff0f5").put("lavenderblush1", "#fff0f5") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("lavenderblush2", "#eee0e5").put("lavenderblush3", "#cdc1c5") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("lavenderblush4", "#8b8386").put("lawngreen", "#7cfc00") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("lemonchiffon", "#fffacd").put("lemonchiffon1", "#fffacd") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("lemonchiffon2", "#eee9bf").put("lemonchiffon3", "#cdc9a5") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("lemonchiffon4", "#8b8970").put("lightblue", "#add8e6") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("lightblue1", "#bfefff").put("lightblue2", "#b2dfee") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("lightblue3", "#9ac0cd").put("lightblue4", "#68838b") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("lightcoral", "#f08080").put("lightcyan", "#e0ffff") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("lightcyan1", "#e0ffff").put("lightcyan2", "#d1eeee") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("lightcyan3", "#b4cdcd").put("lightcyan4", "#7a8b8b") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("lightgoldenrod", "#eedd82").put("lightgoldenrod1", "#ffec8b") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("lightgoldenrod2", "#eedc82").put("lightgoldenrod3", "#cdbe70") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("lightgoldenrod4", "#8b814c") //$NON-NLS-1$ //$NON-NLS-2$
			.put("lightgoldenrodyellow", "#fafad2").put("lightgray", "#d3d3d3") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("lightgrey", "#d3d3d3").put("lightpink", "#ffb6c1") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("lightpink1", "#ffaeb9").put("lightpink2", "#eea2ad") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("lightpink3", "#cd8c95").put("lightpink4", "#8b5f65") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("lightsalmon", "#ffa07a").put("lightsalmon1", "#ffa07a") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("lightsalmon2", "#ee9572").put("lightsalmon3", "#cd8162") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("lightsalmon4", "#8b5742").put("lightseagreen", "#20b2aa") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("lightskyblue", "#87cefa").put("lightskyblue1", "#b0e2ff") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("lightskyblue2", "#a4d3ee").put("lightskyblue3", "#8db6cd") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("lightskyblue4", "#607b8b").put("lightslateblue", "#8470ff") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("lightslategray", "#778899").put("lightslategrey", "#778899") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("lightsteelblue", "#b0c4de").put("lightsteelblue1", "#cae1ff") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("lightsteelblue2", "#bcd2ee").put("lightsteelblue3", "#a2b5cd") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("lightsteelblue4", "#6e7b8b").put("lightyellow", "#ffffe0") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("lightyellow1", "#ffffe0").put("lightyellow2", "#eeeed1") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("lightyellow3", "#cdcdb4").put("lightyellow4", "#8b8b7a") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("limegreen", "#32cd32").put("linen", "#faf0e6") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("magenta", "#ff00ff").put("magenta1", "#ff00ff") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("magenta2", "#ee00ee").put("magenta3", "#cd00cd") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("magenta4", "#8b008b").put("maroon", "#b03060") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("maroon1", "#ff34b3").put("maroon2", "#ee30a7") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("maroon3", "#cd2990").put("maroon4", "#8b1c62") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("mediumaquamarine", "#66cdaa").put("mediumblue", "#0000cd") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("mediumorchid", "#ba55d3").put("mediumorchid1", "#e066ff") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("mediumorchid2", "#d15fee").put("mediumorchid3", "#b452cd") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("mediumorchid4", "#7a378b").put("mediumpurple", "#9370db") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("mediumpurple1", "#ab82ff").put("mediumpurple2", "#9f79ee") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("mediumpurple3", "#8968cd").put("mediumpurple4", "#5d478b") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("mediumseagreen", "#3cb371").put("mediumslateblue", "#7b68ee") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("mediumspringgreen", "#00fa9a") //$NON-NLS-1$ //$NON-NLS-2$
			.put("mediumturquoise", "#48d1cc").put("mediumvioletred", "#c71585") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("midnightblue", "#191970").put("mintcream", "#f5fffa") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("mistyrose", "#ffe4e1").put("mistyrose1", "#ffe4e1") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("mistyrose2", "#eed5d2").put("mistyrose3", "#cdb7b5") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("mistyrose4", "#8b7d7b").put("moccasin", "#ffe4b5") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("navajowhite", "#ffdead").put("navajowhite1", "#ffdead") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("navajowhite2", "#eecfa1").put("navajowhite3", "#cdb38b") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("navajowhite4", "#8b795e").put("navy", "#000080") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("navyblue", "#000080").put("none", "#fffffe") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("oldlace", "#fdf5e6").put("olivedrab", "#6b8e23") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("olivedrab1", "#c0ff3e").put("olivedrab2", "#b3ee3a") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("olivedrab3", "#9acd32").put("olivedrab4", "#698b22") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("orange", "#ffa500").put("orange1", "#ffa500") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("orange2", "#ee9a00").put("orange3", "#cd8500") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("orange4", "#8b5a00").put("orangered", "#ff4500") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("orangered1", "#ff4500").put("orangered2", "#ee4000") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("orangered3", "#cd3700").put("orangered4", "#8b2500") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("orchid", "#da70d6").put("orchid1", "#ff83fa") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("orchid2", "#ee7ae9").put("orchid3", "#cd69c9") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("orchid4", "#8b4789").put("palegoldenrod", "#eee8aa") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("palegreen", "#98fb98").put("palegreen1", "#9aff9a") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("palegreen2", "#90ee90").put("palegreen3", "#7ccd7c") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("palegreen4", "#548b54").put("paleturquoise", "#afeeee") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("paleturquoise1", "#bbffff").put("paleturquoise2", "#aeeeee") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("paleturquoise3", "#96cdcd").put("paleturquoise4", "#668b8b") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("palevioletred", "#db7093").put("palevioletred1", "#ff82ab") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("palevioletred2", "#ee799f").put("palevioletred3", "#cd6889") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("palevioletred4", "#8b475d").put("papayawhip", "#ffefd5") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("peachpuff", "#ffdab9").put("peachpuff1", "#ffdab9") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("peachpuff2", "#eecbad").put("peachpuff3", "#cdaf95") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("peachpuff4", "#8b7765").put("peru", "#cd853f") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("pink", "#ffc0cb").put("pink1", "#ffb5c5") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("pink2", "#eea9b8").put("pink3", "#cd919e") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("pink4", "#8b636c").put("plum", "#dda0dd") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("plum1", "#ffbbff").put("plum2", "#eeaeee") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("plum3", "#cd96cd").put("plum4", "#8b668b") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("powderblue", "#b0e0e6").put("purple", "#a020f0") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("purple1", "#9b30ff").put("purple2", "#912cee") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("purple3", "#7d26cd").put("purple4", "#551a8b") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("red", "#ff0000").put("red1", "#ff0000").put("red2", "#ee0000") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			.put("red3", "#cd0000").put("red4", "#8b0000") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("rosybrown", "#bc8f8f").put("rosybrown1", "#ffc1c1") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("rosybrown2", "#eeb4b4").put("rosybrown3", "#cd9b9b") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("rosybrown4", "#8b6969").put("royalblue", "#4169e1") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("royalblue1", "#4876ff").put("royalblue2", "#436eee") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("royalblue3", "#3a5fcd").put("royalblue4", "#27408b") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("saddlebrown", "#8b4513").put("salmon", "#fa8072") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("salmon1", "#ff8c69").put("salmon2", "#ee8262") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("salmon3", "#cd7054").put("salmon4", "#8b4c39") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("sandybrown", "#f4a460").put("seagreen", "#2e8b57") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("seagreen1", "#54ff9f").put("seagreen2", "#4eee94") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("seagreen3", "#43cd80").put("seagreen4", "#2e8b57") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("seashell", "#fff5ee").put("seashell1", "#fff5ee") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("seashell2", "#eee5de").put("seashell3", "#cdc5bf") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("seashell4", "#8b8682").put("sienna", "#a0522d") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("sienna1", "#ff8247").put("sienna2", "#ee7942") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("sienna3", "#cd6839").put("sienna4", "#8b4726") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("skyblue", "#87ceeb").put("skyblue1", "#87ceff") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("skyblue2", "#7ec0ee").put("skyblue3", "#6ca6cd") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("skyblue4", "#4a708b").put("slateblue", "#6a5acd") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("slateblue1", "#836fff").put("slateblue2", "#7a67ee") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("slateblue3", "#6959cd").put("slateblue4", "#473c8b") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("slategray", "#708090").put("slategray1", "#c6e2ff") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("slategray2", "#b9d3ee").put("slategray3", "#9fb6cd") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("slategray4", "#6c7b8b").put("slategrey", "#708090") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("snow", "#fffafa").put("snow1", "#fffafa") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("snow2", "#eee9e9").put("snow3", "#cdc9c9") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("snow4", "#8b8989").put("springgreen", "#00ff7f") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("springgreen1", "#00ff7f").put("springgreen2", "#00ee76") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("springgreen3", "#00cd66").put("springgreen4", "#008b45") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("steelblue", "#4682b4").put("steelblue1", "#63b8ff") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("steelblue2", "#5cacee").put("steelblue3", "#4f94cd") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("steelblue4", "#36648b").put("tan", "#d2b48c") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("tan1", "#ffa54f").put("tan2", "#ee9a49") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("tan3", "#cd853f").put("tan4", "#8b5a2b") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("thistle", "#d8bfd8").put("thistle1", "#ffe1ff") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("thistle2", "#eed2ee").put("thistle3", "#cdb5cd") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("thistle4", "#8b7b8b").put("tomato", "#ff6347") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("tomato1", "#ff6347").put("tomato2", "#ee5c42") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("tomato3", "#cd4f39").put("tomato4", "#8b3626") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("transparent", "#fffffe").put("turquoise", "#40e0d0") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("turquoise1", "#00f5ff").put("turquoise2", "#00e5ee") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("turquoise3", "#00c5cd").put("turquoise4", "#00868b") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("violet", "#ee82ee").put("violetred", "#d02090") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("violetred1", "#ff3e96").put("violetred2", "#ee3a8c") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("violetred3", "#cd3278").put("violetred4", "#8b2252") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("wheat", "#f5deb3").put("wheat1", "#ffe7ba") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("wheat2", "#eed8ae").put("wheat3", "#cdba96") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("wheat4", "#8b7e66").put("white", "#ffffff") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("whitesmoke", "#f5f5f5").put("yellow", "#ffff00") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("yellow1", "#ffff00").put("yellow2", "#eeee00") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("yellow3", "#cdcd00").put("yellow4", "#8b8b00") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("yellowgreen", "#9acd32").build(); //$NON-NLS-1$ //$NON-NLS-2$

	private static Map<String, String> svgColorScheme = ImmutableMap
			.<String, String> builder().put("aliceblue", "#f0f8ff") //$NON-NLS-1$ //$NON-NLS-2$
			.put("antiquewhite", "#faebd7").put("aqua", "#00ffff") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("aquamarine", "#7fffd4").put("azure", "#f0ffff") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("beige", "#f5f5dc").put("bisque", "#ffe4c4") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("black", "#000000").put("blanchedalmond", "#ffebcd") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("blue", "#0000ff").put("blueviolet", "#8a2be2") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("brown", "#a52a2a").put("burlywood", "#deb887") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("cadetblue", "#5f9ea0").put("chartreuse", "#7fff00") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("chocolate", "#d2691e").put("coral", "#ff7f50") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("cornflowerblue", "#6495ed").put("cornsilk", "#fff8dc") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("crimson", "#dc143c").put("cyan", "#00ffff") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("darkblue", "#00008b").put("darkcyan", "#008b8b") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("darkgoldenrod", "#b8860b").put("darkgray", "#a9a9a9") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("darkgreen", "#006400").put("darkgrey", "#a9a9a9") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("darkkhaki", "#bdb76b").put("darkmagenta", "#8b008b") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("darkolivegreen", "#556b2f").put("darkorange", "#ff8c00") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("darkorchid", "#9932cc").put("darkred", "#8b0000") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("darksalmon", "#e9967a").put("darkseagreen", "#8fbc8f") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("darkslateblue", "#483d8b").put("darkslategray", "#2f4f4f") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("darkslategrey", "#2f4f4f").put("darkturquoise", "#00ced1") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("darkviolet", "#9400d3").put("deeppink", "#ff1493") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("deepskyblue", "#00bfff").put("dimgray", "#696969") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("dimgrey", "#696969").put("dodgerblue", "#1e90ff") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("firebrick", "#b22222").put("floralwhite", "#fffaf0") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("forestgreen", "#228b22").put("fuchsia", "#ff00ff") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gainsboro", "#dcdcdc").put("ghostwhite", "#f8f8ff") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gold", "#ffd700").put("goldenrod", "#daa520") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gray", "#808080").put("grey", "#808080") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("green", "#008000").put("greenyellow", "#adff2f") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("honeydew", "#f0fff0").put("hotpink", "#ff69b4") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("indianred", "#cd5c5c").put("indigo", "#4b0082") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("ivory", "#fffff0").put("khaki", "#f0e68c") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("lavender", "#e6e6fa").put("lavenderblush", "#fff0f5") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("lawngreen", "#7cfc00").put("lemonchiffon", "#fffacd") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("lightblue", "#add8e6").put("lightcoral", "#f08080") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("lightcyan", "#e0ffff").put("lightgoldenrodyellow", "#fafad2") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("lightgray", "#d3d3d3").put("lightgreen", "#90ee90") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("lightgrey", "#d3d3d3").put("lightpink", "#ffb6c1") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("lightsalmon", "#ffa07a").put("lightseagreen", "#20b2aa") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("lightskyblue", "#87cefa").put("lightslategray", "#778899") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("lightslategrey", "#778899").put("lightsteelblue", "#b0c4de") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("lightyellow", "#ffffe0").put("lime", "#00ff00") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("limegreen", "#32cd32").put("linen", "#faf0e6") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("magenta", "#ff00ff").put("maroon", "#800000") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("mediumaquamarine", "#66cdaa").put("mediumblue", "#0000cd") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("mediumorchid", "#ba55d3").put("mediumpurple", "#9370db") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("mediumseagreen", "#3cb371").put("mediumslateblue", "#7b68ee") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("mediumspringgreen", "#00fa9a") //$NON-NLS-1$ //$NON-NLS-2$
			.put("mediumturquoise", "#48d1cc").put("mediumvioletred", "#c71585") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("midnightblue", "#191970").put("mintcream", "#f5fffa") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("mistyrose", "#ffe4e1").put("moccasin", "#ffe4b5") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("navajowhite", "#ffdead").put("navy", "#000080") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("oldlace", "#fdf5e6").put("olive", "#808000") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("olivedrab", "#6b8e23").put("orange", "#ffa500") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("orangered", "#ff4500").put("orchid", "#da70d6") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("palegoldenrod", "#eee8aa").put("palegreen", "#98fb98") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("paleturquoise", "#afeeee").put("palevioletred", "#db7093") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("papayawhip", "#ffefd5").put("peachpuff", "#ffdab9") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("peru", "#cd853f").put("pink", "#ffc0cb") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("plum", "#dda0dd").put("powderblue", "#b0e0e6") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("purple", "#800080").put("red", "#ff0000") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("rosybrown", "#bc8f8f").put("royalblue", "#4169e1") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("saddlebrown", "#8b4513").put("salmon", "#fa8072") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("sandybrown", "#f4a460").put("seagreen", "#2e8b57") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("seashell", "#fff5ee").put("sienna", "#a0522d") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("silver", "#c0c0c0").put("skyblue", "#87ceeb") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("slateblue", "#6a5acd").put("slategray", "#708090") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("slategrey", "#708090").put("snow", "#fffafa") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("springgreen", "#00ff7f").put("steelblue", "#4682b4") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("tan", "#d2b48c").put("teal", "#008080") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("thistle", "#d8bfd8").put("tomato", "#ff6347") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("turquoise", "#40e0d0").put("violet", "#ee82ee") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("wheat", "#f5deb3").put("white", "#ffffff") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("whitesmoke", "#f5f5f5").put("yellow", "#ffff00") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("yellowgreen", "#9acd32").build(); //$NON-NLS-1$ //$NON-NLS-2$

	private static Map<String, String[]> brewerColorSchemes = ImmutableMap
			.<String, String[]> builder()
			.put("accent3", new String[] { "#7fc97f", "#beaed4", "#fdc086" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("accent4", //$NON-NLS-1$
					new String[] { "#7fc97f", "#beaed4", "#fdc086", "#ffff99" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("accent5", //$NON-NLS-1$
					new String[] { "#7fc97f", "#beaed4", "#fdc086", "#ffff99", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#386cb0" }) //$NON-NLS-1$
			.put("accent6", //$NON-NLS-1$
					new String[] { "#7fc97f", "#beaed4", "#fdc086", "#ffff99", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#386cb0", "#f0027f" }) //$NON-NLS-1$ //$NON-NLS-2$
			.put("accent7", //$NON-NLS-1$
					new String[] { "#7fc97f", "#beaed4", "#fdc086", "#ffff99", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#386cb0", "#f0027f", "#bf5b17" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			.put("accent8", //$NON-NLS-1$
					new String[] { "#7fc97f", "#beaed4", "#fdc086", "#ffff99", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#386cb0", "#f0027f", "#bf5b17", "#666666" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("blues3", new String[] { "#deebf7", "#9ecae1", "#3182bd" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("blues4", //$NON-NLS-1$
					new String[] { "#eff3ff", "#bdd7e7", "#6baed6", "#2171b5" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("blues5", //$NON-NLS-1$
					new String[] { "#eff3ff", "#bdd7e7", "#6baed6", "#3182bd", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#08519c" }) //$NON-NLS-1$
			.put("blues6", //$NON-NLS-1$
					new String[] { "#eff3ff", "#c6dbef", "#9ecae1", "#6baed6", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#3182bd", "#08519c" }) //$NON-NLS-1$ //$NON-NLS-2$
			.put("blues7", //$NON-NLS-1$
					new String[] { "#eff3ff", "#c6dbef", "#9ecae1", "#6baed6", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#4292c6", "#2171b5", "#084594" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			.put("blues8", //$NON-NLS-1$
					new String[] { "#f7fbff", "#deebf7", "#c6dbef", "#9ecae1", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#6baed6", "#4292c6", "#2171b5", "#084594" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("blues9", //$NON-NLS-1$
					new String[] { "#f7fbff", "#deebf7", "#c6dbef", "#9ecae1", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#6baed6", "#4292c6", "#2171b5", "#08519c", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#08306b" }) //$NON-NLS-1$
			.put("brbg10", //$NON-NLS-1$
					new String[] { "#543005", "#8c510a", "#bf812d", "#dfc27d", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#f6e8c3", "#c7eae5", "#80cdc1", "#35978f", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#01665e", "#003c30" }) //$NON-NLS-1$ //$NON-NLS-2$
			.put("brbg11", //$NON-NLS-1$
					new String[] { "#543005", "#8c510a", "#bf812d", "#dfc27d", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#f6e8c3", "#f5f5f5", "#c7eae5", "#80cdc1", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#35978f", "#01665e", "#003c30" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			.put("brbg3", new String[] { "#d8b365", "#f5f5f5", "#5ab4ac" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("brbg4", //$NON-NLS-1$
					new String[] { "#a6611a", "#dfc27d", "#80cdc1", "#018571" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("brbg5", //$NON-NLS-1$
					new String[] { "#a6611a", "#dfc27d", "#f5f5f5", "#80cdc1", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#018571" }) //$NON-NLS-1$
			.put("brbg6", //$NON-NLS-1$
					new String[] { "#8c510a", "#d8b365", "#f6e8c3", "#c7eae5", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#5ab4ac", "#01665e" }) //$NON-NLS-1$ //$NON-NLS-2$
			.put("brbg7", //$NON-NLS-1$
					new String[] { "#8c510a", "#d8b365", "#f6e8c3", "#f5f5f5", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#c7eae5", "#5ab4ac", "#01665e" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			.put("brbg8", //$NON-NLS-1$
					new String[] { "#8c510a", "#bf812d", "#dfc27d", "#f6e8c3", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#c7eae5", "#80cdc1", "#35978f", "#01665e" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("brbg9", //$NON-NLS-1$
					new String[] { "#8c510a", "#bf812d", "#dfc27d", "#f6e8c3", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#f5f5f5", "#c7eae5", "#80cdc1", "#35978f", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#01665e" }) //$NON-NLS-1$
			.put("bugn3", new String[] { "#e5f5f9", "#99d8c9", "#2ca25f" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("bugn4", //$NON-NLS-1$
					new String[] { "#edf8fb", "#b2e2e2", "#66c2a4", "#238b45" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("bugn5", //$NON-NLS-1$
					new String[] { "#edf8fb", "#b2e2e2", "#66c2a4", "#2ca25f", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#006d2c" }) //$NON-NLS-1$
			.put("bugn6", //$NON-NLS-1$
					new String[] { "#edf8fb", "#ccece6", "#99d8c9", "#66c2a4", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#2ca25f", "#006d2c" }) //$NON-NLS-1$ //$NON-NLS-2$
			.put("bugn7", //$NON-NLS-1$
					new String[] { "#edf8fb", "#ccece6", "#99d8c9", "#66c2a4", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#41ae76", "#238b45", "#005824" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			.put("bugn8", //$NON-NLS-1$
					new String[] { "#f7fcfd", "#e5f5f9", "#ccece6", "#99d8c9", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#66c2a4", "#41ae76", "#238b45", "#005824" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("bugn9", //$NON-NLS-1$
					new String[] { "#f7fcfd", "#e5f5f9", "#ccece6", "#99d8c9", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#66c2a4", "#41ae76", "#238b45", "#006d2c", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#00441b" }) //$NON-NLS-1$
			.put("bupu3", new String[] { "#e0ecf4", "#9ebcda", "#8856a7" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("bupu4", //$NON-NLS-1$
					new String[] { "#edf8fb", "#b3cde3", "#8c96c6", "#88419d" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("bupu5", //$NON-NLS-1$
					new String[] { "#edf8fb", "#b3cde3", "#8c96c6", "#8856a7", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#810f7c" }) //$NON-NLS-1$
			.put("bupu6", //$NON-NLS-1$
					new String[] { "#edf8fb", "#bfd3e6", "#9ebcda", "#8c96c6", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#8856a7", "#810f7c" }) //$NON-NLS-1$ //$NON-NLS-2$
			.put("bupu7", //$NON-NLS-1$
					new String[] { "#edf8fb", "#bfd3e6", "#9ebcda", "#8c96c6", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#8c6bb1", "#88419d", "#6e016b" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			.put("bupu8", //$NON-NLS-1$
					new String[] { "#f7fcfd", "#e0ecf4", "#bfd3e6", "#9ebcda", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#8c96c6", "#8c6bb1", "#88419d", "#6e016b" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("bupu9", //$NON-NLS-1$
					new String[] { "#f7fcfd", "#e0ecf4", "#bfd3e6", "#9ebcda", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#8c96c6", "#8c6bb1", "#88419d", "#810f7c", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#4d004b" }) //$NON-NLS-1$
			.put("dark23", new String[] { "#1b9e77", "#d95f02", "#7570b3" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("dark24", //$NON-NLS-1$
					new String[] { "#1b9e77", "#d95f02", "#7570b3", "#e7298a" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("dark25", //$NON-NLS-1$
					new String[] { "#1b9e77", "#d95f02", "#7570b3", "#e7298a", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#66a61e" }) //$NON-NLS-1$
			.put("dark26", //$NON-NLS-1$
					new String[] { "#1b9e77", "#d95f02", "#7570b3", "#e7298a", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#66a61e", "#e6ab02" }) //$NON-NLS-1$ //$NON-NLS-2$
			.put("dark27", //$NON-NLS-1$
					new String[] { "#1b9e77", "#d95f02", "#7570b3", "#e7298a", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#66a61e", "#e6ab02", "#a6761d" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			.put("dark28", //$NON-NLS-1$
					new String[] { "#1b9e77", "#d95f02", "#7570b3", "#e7298a", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#66a61e", "#e6ab02", "#a6761d", "#666666" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gnbu3", new String[] { "#e0f3db", "#a8ddb5", "#43a2ca" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gnbu4", //$NON-NLS-1$
					new String[] { "#f0f9e8", "#bae4bc", "#7bccc4", "#2b8cbe" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gnbu5", //$NON-NLS-1$
					new String[] { "#f0f9e8", "#bae4bc", "#7bccc4", "#43a2ca", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#0868ac" }) //$NON-NLS-1$
			.put("gnbu6", //$NON-NLS-1$
					new String[] { "#f0f9e8", "#ccebc5", "#a8ddb5", "#7bccc4", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#43a2ca", "#0868ac" }) //$NON-NLS-1$ //$NON-NLS-2$
			.put("gnbu7", //$NON-NLS-1$
					new String[] { "#f0f9e8", "#ccebc5", "#a8ddb5", "#7bccc4", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#4eb3d3", "#2b8cbe", "#08589e" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			.put("gnbu8", //$NON-NLS-1$
					new String[] { "#f7fcf0", "#e0f3db", "#ccebc5", "#a8ddb5", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#7bccc4", "#4eb3d3", "#2b8cbe", "#08589e" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("gnbu9", //$NON-NLS-1$
					new String[] { "#f7fcf0", "#e0f3db", "#ccebc5", "#a8ddb5", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#7bccc4", "#4eb3d3", "#2b8cbe", "#0868ac", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#084081" }) //$NON-NLS-1$
			.put("greens3", new String[] { "#e5f5e0", "#a1d99b", "#31a354" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("greens4", //$NON-NLS-1$
					new String[] { "#edf8e9", "#bae4b3", "#74c476", "#238b45" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("greens5", //$NON-NLS-1$
					new String[] { "#edf8e9", "#bae4b3", "#74c476", "#31a354", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#006d2c" }) //$NON-NLS-1$
			.put("greens6", //$NON-NLS-1$
					new String[] { "#edf8e9", "#c7e9c0", "#a1d99b", "#74c476", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#31a354", "#006d2c" }) //$NON-NLS-1$ //$NON-NLS-2$
			.put("greens7", //$NON-NLS-1$
					new String[] { "#edf8e9", "#c7e9c0", "#a1d99b", "#74c476", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#41ab5d", "#238b45", "#005a32" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			.put("greens8", //$NON-NLS-1$
					new String[] { "#f7fcf5", "#e5f5e0", "#c7e9c0", "#a1d99b", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#74c476", "#41ab5d", "#238b45", "#005a32" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("greens9", //$NON-NLS-1$
					new String[] { "#f7fcf5", "#e5f5e0", "#c7e9c0", "#a1d99b", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#74c476", "#41ab5d", "#238b45", "#006d2c", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#00441b" }) //$NON-NLS-1$
			.put("greys3", new String[] { "#f0f0f0", "#bdbdbd", "#636363" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("greys4", //$NON-NLS-1$
					new String[] { "#f7f7f7", "#cccccc", "#969696", "#525252" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("greys5", //$NON-NLS-1$
					new String[] { "#f7f7f7", "#cccccc", "#969696", "#636363", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#252525" }) //$NON-NLS-1$
			.put("greys6", //$NON-NLS-1$
					new String[] { "#f7f7f7", "#d9d9d9", "#bdbdbd", "#969696", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#636363", "#252525" }) //$NON-NLS-1$ //$NON-NLS-2$
			.put("greys7", //$NON-NLS-1$
					new String[] { "#f7f7f7", "#d9d9d9", "#bdbdbd", "#969696", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#737373", "#525252", "#252525" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			.put("greys8", //$NON-NLS-1$
					new String[] { "#ffffff", "#f0f0f0", "#d9d9d9", "#bdbdbd", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#969696", "#737373", "#525252", "#252525" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("greys9", //$NON-NLS-1$
					new String[] { "#ffffff", "#f0f0f0", "#d9d9d9", "#bdbdbd", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#969696", "#737373", "#525252", "#252525", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#000000" }) //$NON-NLS-1$
			.put("oranges3", new String[] { "#fee6ce", "#fdae6b", "#e6550d" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("oranges4", //$NON-NLS-1$
					new String[] { "#feedde", "#fdbe85", "#fd8d3c", "#d94701" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("oranges5", //$NON-NLS-1$
					new String[] { "#feedde", "#fdbe85", "#fd8d3c", "#e6550d", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#a63603" }) //$NON-NLS-1$
			.put("oranges6", //$NON-NLS-1$
					new String[] { "#feedde", "#fdd0a2", "#fdae6b", "#fd8d3c", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#e6550d", "#a63603" }) //$NON-NLS-1$ //$NON-NLS-2$
			.put("oranges7", //$NON-NLS-1$
					new String[] { "#feedde", "#fdd0a2", "#fdae6b", "#fd8d3c", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#f16913", "#d94801", "#8c2d04" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			.put("oranges8", //$NON-NLS-1$
					new String[] { "#fff5eb", "#fee6ce", "#fdd0a2", "#fdae6b", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#fd8d3c", "#f16913", "#d94801", "#8c2d04" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("oranges9", //$NON-NLS-1$
					new String[] { "#fff5eb", "#fee6ce", "#fdd0a2", "#fdae6b", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#fd8d3c", "#f16913", "#d94801", "#a63603", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#7f2704" }) //$NON-NLS-1$
			.put("orrd3", new String[] { "#fee8c8", "#fdbb84", "#e34a33" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("orrd4", //$NON-NLS-1$
					new String[] { "#fef0d9", "#fdcc8a", "#fc8d59", "#d7301f" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("orrd5", //$NON-NLS-1$
					new String[] { "#fef0d9", "#fdcc8a", "#fc8d59", "#e34a33", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#b30000" }) //$NON-NLS-1$
			.put("orrd6", //$NON-NLS-1$
					new String[] { "#fef0d9", "#fdd49e", "#fdbb84", "#fc8d59", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#e34a33", "#b30000" }) //$NON-NLS-1$ //$NON-NLS-2$
			.put("orrd7", //$NON-NLS-1$
					new String[] { "#fef0d9", "#fdd49e", "#fdbb84", "#fc8d59", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#ef6548", "#d7301f", "#990000" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			.put("orrd8", //$NON-NLS-1$
					new String[] { "#fff7ec", "#fee8c8", "#fdd49e", "#fdbb84", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#fc8d59", "#ef6548", "#d7301f", "#990000" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("orrd9", //$NON-NLS-1$
					new String[] { "#fff7ec", "#fee8c8", "#fdd49e", "#fdbb84", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#fc8d59", "#ef6548", "#d7301f", "#b30000", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#7f0000" }) //$NON-NLS-1$
			.put("paired10", //$NON-NLS-1$
					new String[] { "#a6cee3", "#1f78b4", "#b2df8a", "#33a02c", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#fb9a99", "#e31a1c", "#fdbf6f", "#ff7f00", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#cab2d6", "#6a3d9a" }) //$NON-NLS-1$ //$NON-NLS-2$
			.put("paired11", //$NON-NLS-1$
					new String[] { "#a6cee3", "#1f78b4", "#b2df8a", "#33a02c", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#fb9a99", "#e31a1c", "#fdbf6f", "#ff7f00", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#cab2d6", "#6a3d9a", "#ffff99" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			.put("paired12", //$NON-NLS-1$
					new String[] { "#a6cee3", "#1f78b4", "#b2df8a", "#33a02c", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#fb9a99", "#e31a1c", "#fdbf6f", "#ff7f00", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#cab2d6", "#6a3d9a", "#ffff99", "#b15928" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("paired3", new String[] { "#a6cee3", "#1f78b4", "#b2df8a" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("paired4", //$NON-NLS-1$
					new String[] { "#a6cee3", "#1f78b4", "#b2df8a", "#33a02c" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("paired5", //$NON-NLS-1$
					new String[] { "#a6cee3", "#1f78b4", "#b2df8a", "#33a02c", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#fb9a99" }) //$NON-NLS-1$
			.put("paired6", //$NON-NLS-1$
					new String[] { "#a6cee3", "#1f78b4", "#b2df8a", "#33a02c", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#fb9a99", "#e31a1c" }) //$NON-NLS-1$ //$NON-NLS-2$
			.put("paired7", //$NON-NLS-1$
					new String[] { "#a6cee3", "#1f78b4", "#b2df8a", "#33a02c", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#fb9a99", "#e31a1c", "#fdbf6f" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			.put("paired8", //$NON-NLS-1$
					new String[] { "#a6cee3", "#1f78b4", "#b2df8a", "#33a02c", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#fb9a99", "#e31a1c", "#fdbf6f", "#ff7f00" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("paired9", //$NON-NLS-1$
					new String[] { "#a6cee3", "#1f78b4", "#b2df8a", "#33a02c", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#fb9a99", "#e31a1c", "#fdbf6f", "#ff7f00", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#cab2d6" }) //$NON-NLS-1$
			.put("pastel13", new String[] { "#fbb4ae", "#b3cde3", "#ccebc5" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("pastel14", //$NON-NLS-1$
					new String[] { "#fbb4ae", "#b3cde3", "#ccebc5", "#decbe4" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("pastel15", //$NON-NLS-1$
					new String[] { "#fbb4ae", "#b3cde3", "#ccebc5", "#decbe4", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#fed9a6" }) //$NON-NLS-1$
			.put("pastel16", //$NON-NLS-1$
					new String[] { "#fbb4ae", "#b3cde3", "#ccebc5", "#decbe4", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#fed9a6", "#ffffcc" }) //$NON-NLS-1$ //$NON-NLS-2$
			.put("pastel17", //$NON-NLS-1$
					new String[] { "#fbb4ae", "#b3cde3", "#ccebc5", "#decbe4", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#fed9a6", "#ffffcc", "#e5d8bd" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			.put("pastel18", //$NON-NLS-1$
					new String[] { "#fbb4ae", "#b3cde3", "#ccebc5", "#decbe4", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#fed9a6", "#ffffcc", "#e5d8bd", "#fddaec" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("pastel19", //$NON-NLS-1$
					new String[] { "#fbb4ae", "#b3cde3", "#ccebc5", "#decbe4", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#fed9a6", "#ffffcc", "#e5d8bd", "#fddaec", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#f2f2f2" }) //$NON-NLS-1$
			.put("pastel23", new String[] { "#b3e2cd", "#fdcdac", "#cbd5e8" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("pastel24", //$NON-NLS-1$
					new String[] { "#b3e2cd", "#fdcdac", "#cbd5e8", "#f4cae4" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("pastel25", //$NON-NLS-1$
					new String[] { "#b3e2cd", "#fdcdac", "#cbd5e8", "#f4cae4", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#e6f5c9" }) //$NON-NLS-1$
			.put("pastel26", //$NON-NLS-1$
					new String[] { "#b3e2cd", "#fdcdac", "#cbd5e8", "#f4cae4", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#e6f5c9", "#fff2ae" }) //$NON-NLS-1$ //$NON-NLS-2$
			.put("pastel27", //$NON-NLS-1$
					new String[] { "#b3e2cd", "#fdcdac", "#cbd5e8", "#f4cae4", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#e6f5c9", "#fff2ae", "#f1e2cc" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			.put("pastel28", //$NON-NLS-1$
					new String[] { "#b3e2cd", "#fdcdac", "#cbd5e8", "#f4cae4", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#e6f5c9", "#fff2ae", "#f1e2cc", "#cccccc" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("piyg10", //$NON-NLS-1$
					new String[] { "#8e0152", "#c51b7d", "#de77ae", "#f1b6da", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#fde0ef", "#e6f5d0", "#b8e186", "#7fbc41", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#4d9221", "#276419" }) //$NON-NLS-1$ //$NON-NLS-2$
			.put("piyg11", //$NON-NLS-1$
					new String[] { "#8e0152", "#c51b7d", "#de77ae", "#f1b6da", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#fde0ef", "#f7f7f7", "#e6f5d0", "#b8e186", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#7fbc41", "#4d9221", "#276419" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			.put("piyg3", new String[] { "#e9a3c9", "#f7f7f7", "#a1d76a" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("piyg4", //$NON-NLS-1$
					new String[] { "#d01c8b", "#f1b6da", "#b8e186", "#4dac26" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("piyg5", //$NON-NLS-1$
					new String[] { "#d01c8b", "#f1b6da", "#f7f7f7", "#b8e186", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#4dac26" }) //$NON-NLS-1$
			.put("piyg6", //$NON-NLS-1$
					new String[] { "#c51b7d", "#e9a3c9", "#fde0ef", "#e6f5d0", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#a1d76a", "#4d9221" }) //$NON-NLS-1$ //$NON-NLS-2$
			.put("piyg7", //$NON-NLS-1$
					new String[] { "#c51b7d", "#e9a3c9", "#fde0ef", "#f7f7f7", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#e6f5d0", "#a1d76a", "#4d9221" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			.put("piyg8", //$NON-NLS-1$
					new String[] { "#c51b7d", "#de77ae", "#f1b6da", "#fde0ef", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#e6f5d0", "#b8e186", "#7fbc41", "#4d9221" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("piyg9", //$NON-NLS-1$
					new String[] { "#c51b7d", "#de77ae", "#f1b6da", "#fde0ef", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#f7f7f7", "#e6f5d0", "#b8e186", "#7fbc41", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#4d9221" }) //$NON-NLS-1$
			.put("prgn10", //$NON-NLS-1$
					new String[] { "#40004b", "#762a83", "#9970ab", "#c2a5cf", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#e7d4e8", "#d9f0d3", "#a6dba0", "#5aae61", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#1b7837", "#00441b" }) //$NON-NLS-1$ //$NON-NLS-2$
			.put("prgn11", //$NON-NLS-1$
					new String[] { "#40004b", "#762a83", "#9970ab", "#c2a5cf", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#e7d4e8", "#f7f7f7", "#d9f0d3", "#a6dba0", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#5aae61", "#1b7837", "#00441b" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			.put("prgn3", new String[] { "#af8dc3", "#f7f7f7", "#7fbf7b" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("prgn4", //$NON-NLS-1$
					new String[] { "#7b3294", "#c2a5cf", "#a6dba0", "#008837" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("prgn5", //$NON-NLS-1$
					new String[] { "#7b3294", "#c2a5cf", "#f7f7f7", "#a6dba0", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#008837" }) //$NON-NLS-1$
			.put("prgn6", //$NON-NLS-1$
					new String[] { "#762a83", "#af8dc3", "#e7d4e8", "#d9f0d3", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#7fbf7b", "#1b7837" }) //$NON-NLS-1$ //$NON-NLS-2$
			.put("prgn7", //$NON-NLS-1$
					new String[] { "#762a83", "#af8dc3", "#e7d4e8", "#f7f7f7", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#d9f0d3", "#7fbf7b", "#1b7837" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			.put("prgn8", //$NON-NLS-1$
					new String[] { "#762a83", "#9970ab", "#c2a5cf", "#e7d4e8", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#d9f0d3", "#a6dba0", "#5aae61", "#1b7837" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("prgn9", //$NON-NLS-1$
					new String[] { "#762a83", "#9970ab", "#c2a5cf", "#e7d4e8", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#f7f7f7", "#d9f0d3", "#a6dba0", "#5aae61", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#1b7837" }) //$NON-NLS-1$
			.put("pubu3", new String[] { "#ece7f2", "#a6bddb", "#2b8cbe" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("pubu4", //$NON-NLS-1$
					new String[] { "#f1eef6", "#bdc9e1", "#74a9cf", "#0570b0" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("pubu5", //$NON-NLS-1$
					new String[] { "#f1eef6", "#bdc9e1", "#74a9cf", "#2b8cbe", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#045a8d" }) //$NON-NLS-1$
			.put("pubu6", //$NON-NLS-1$
					new String[] { "#f1eef6", "#d0d1e6", "#a6bddb", "#74a9cf", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#2b8cbe", "#045a8d" }) //$NON-NLS-1$ //$NON-NLS-2$
			.put("pubu7", //$NON-NLS-1$
					new String[] { "#f1eef6", "#d0d1e6", "#a6bddb", "#74a9cf", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#3690c0", "#0570b0", "#034e7b" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			.put("pubu8", //$NON-NLS-1$
					new String[] { "#fff7fb", "#ece7f2", "#d0d1e6", "#a6bddb", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#74a9cf", "#3690c0", "#0570b0", "#034e7b" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("pubu9", //$NON-NLS-1$
					new String[] { "#fff7fb", "#ece7f2", "#d0d1e6", "#a6bddb", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#74a9cf", "#3690c0", "#0570b0", "#045a8d", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#023858" }) //$NON-NLS-1$
			.put("pubugn3", new String[] { "#ece2f0", "#a6bddb", "#1c9099" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("pubugn4", //$NON-NLS-1$
					new String[] { "#f6eff7", "#bdc9e1", "#67a9cf", "#02818a" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("pubugn5", //$NON-NLS-1$
					new String[] { "#f6eff7", "#bdc9e1", "#67a9cf", "#1c9099", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#016c59" }) //$NON-NLS-1$
			.put("pubugn6", //$NON-NLS-1$
					new String[] { "#f6eff7", "#d0d1e6", "#a6bddb", "#67a9cf", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#1c9099", "#016c59" }) //$NON-NLS-1$ //$NON-NLS-2$
			.put("pubugn7", //$NON-NLS-1$
					new String[] { "#f6eff7", "#d0d1e6", "#a6bddb", "#67a9cf", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#3690c0", "#02818a", "#016450" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			.put("pubugn8", //$NON-NLS-1$
					new String[] { "#fff7fb", "#ece2f0", "#d0d1e6", "#a6bddb", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#67a9cf", "#3690c0", "#02818a", "#016450" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("pubugn9", //$NON-NLS-1$
					new String[] { "#fff7fb", "#ece2f0", "#d0d1e6", "#a6bddb", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#67a9cf", "#3690c0", "#02818a", "#016c59", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#014636" }) //$NON-NLS-1$
			.put("puor10", //$NON-NLS-1$
					new String[] { "#7f3b08", "#b35806", "#e08214", "#fdb863", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#fee0b6", "#d8daeb", "#b2abd2", "#8073ac", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#542788", "#2d004b" }) //$NON-NLS-1$ //$NON-NLS-2$
			.put("puor11", //$NON-NLS-1$
					new String[] { "#7f3b08", "#b35806", "#e08214", "#fdb863", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#fee0b6", "#f7f7f7", "#d8daeb", "#b2abd2", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#8073ac", "#542788", "#2d004b" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			.put("puor3", new String[] { "#f1a340", "#f7f7f7", "#998ec3" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("puor4", //$NON-NLS-1$
					new String[] { "#e66101", "#fdb863", "#b2abd2", "#5e3c99" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("puor5", //$NON-NLS-1$
					new String[] { "#e66101", "#fdb863", "#f7f7f7", "#b2abd2", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#5e3c99" }) //$NON-NLS-1$
			.put("puor6", //$NON-NLS-1$
					new String[] { "#b35806", "#f1a340", "#fee0b6", "#d8daeb", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#998ec3", "#542788" }) //$NON-NLS-1$ //$NON-NLS-2$
			.put("puor7", //$NON-NLS-1$
					new String[] { "#b35806", "#f1a340", "#fee0b6", "#f7f7f7", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#d8daeb", "#998ec3", "#542788" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			.put("puor8", //$NON-NLS-1$
					new String[] { "#b35806", "#e08214", "#fdb863", "#fee0b6", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#d8daeb", "#b2abd2", "#8073ac", "#542788" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("puor9", //$NON-NLS-1$
					new String[] { "#b35806", "#e08214", "#fdb863", "#fee0b6", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#f7f7f7", "#d8daeb", "#b2abd2", "#8073ac", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#542788" }) //$NON-NLS-1$
			.put("purd3", new String[] { "#e7e1ef", "#c994c7", "#dd1c77" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("purd4", //$NON-NLS-1$
					new String[] { "#f1eef6", "#d7b5d8", "#df65b0", "#ce1256" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("purd5", //$NON-NLS-1$
					new String[] { "#f1eef6", "#d7b5d8", "#df65b0", "#dd1c77", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#980043" }) //$NON-NLS-1$
			.put("purd6", //$NON-NLS-1$
					new String[] { "#f1eef6", "#d4b9da", "#c994c7", "#df65b0", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#dd1c77", "#980043" }) //$NON-NLS-1$ //$NON-NLS-2$
			.put("purd7", //$NON-NLS-1$
					new String[] { "#f1eef6", "#d4b9da", "#c994c7", "#df65b0", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#e7298a", "#ce1256", "#91003f" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			.put("purd8", //$NON-NLS-1$
					new String[] { "#f7f4f9", "#e7e1ef", "#d4b9da", "#c994c7", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#df65b0", "#e7298a", "#ce1256", "#91003f" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("purd9", //$NON-NLS-1$
					new String[] { "#f7f4f9", "#e7e1ef", "#d4b9da", "#c994c7", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#df65b0", "#e7298a", "#ce1256", "#980043", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#67001f" }) //$NON-NLS-1$
			.put("purples3", new String[] { "#efedf5", "#bcbddc", "#756bb1" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("purples4", //$NON-NLS-1$
					new String[] { "#f2f0f7", "#cbc9e2", "#9e9ac8", "#6a51a3" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("purples5", //$NON-NLS-1$
					new String[] { "#f2f0f7", "#cbc9e2", "#9e9ac8", "#756bb1", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#54278f" }) //$NON-NLS-1$
			.put("purples6", //$NON-NLS-1$
					new String[] { "#f2f0f7", "#dadaeb", "#bcbddc", "#9e9ac8", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#756bb1", "#54278f" }) //$NON-NLS-1$ //$NON-NLS-2$
			.put("purples7", //$NON-NLS-1$
					new String[] { "#f2f0f7", "#dadaeb", "#bcbddc", "#9e9ac8", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#807dba", "#6a51a3", "#4a1486" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			.put("purples8", //$NON-NLS-1$
					new String[] { "#fcfbfd", "#efedf5", "#dadaeb", "#bcbddc", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#9e9ac8", "#807dba", "#6a51a3", "#4a1486" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("purples9", //$NON-NLS-1$
					new String[] { "#fcfbfd", "#efedf5", "#dadaeb", "#bcbddc", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#9e9ac8", "#807dba", "#6a51a3", "#54278f", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#3f007d" }) //$NON-NLS-1$
			.put("rdbu10", //$NON-NLS-1$
					new String[] { "#67001f", "#b2182b", "#d6604d", "#f4a582", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#fddbc7", "#d1e5f0", "#92c5de", "#4393c3", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#2166ac", "#053061" }) //$NON-NLS-1$ //$NON-NLS-2$
			.put("rdbu11", //$NON-NLS-1$
					new String[] { "#67001f", "#b2182b", "#d6604d", "#f4a582", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#fddbc7", "#f7f7f7", "#d1e5f0", "#92c5de", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#4393c3", "#2166ac", "#053061" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			.put("rdbu3", new String[] { "#ef8a62", "#f7f7f7", "#67a9cf" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("rdbu4", //$NON-NLS-1$
					new String[] { "#ca0020", "#f4a582", "#92c5de", "#0571b0" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("rdbu5", //$NON-NLS-1$
					new String[] { "#ca0020", "#f4a582", "#f7f7f7", "#92c5de", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#0571b0" }) //$NON-NLS-1$
			.put("rdbu6", //$NON-NLS-1$
					new String[] { "#b2182b", "#ef8a62", "#fddbc7", "#d1e5f0", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#67a9cf", "#2166ac" }) //$NON-NLS-1$ //$NON-NLS-2$
			.put("rdbu7", //$NON-NLS-1$
					new String[] { "#b2182b", "#ef8a62", "#fddbc7", "#f7f7f7", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#d1e5f0", "#67a9cf", "#2166ac" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			.put("rdbu8", //$NON-NLS-1$
					new String[] { "#b2182b", "#d6604d", "#f4a582", "#fddbc7", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#d1e5f0", "#92c5de", "#4393c3", "#2166ac" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("rdbu9", //$NON-NLS-1$
					new String[] { "#b2182b", "#d6604d", "#f4a582", "#fddbc7", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#f7f7f7", "#d1e5f0", "#92c5de", "#4393c3", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#2166ac" }) //$NON-NLS-1$
			.put("rdgy10", //$NON-NLS-1$
					new String[] { "#67001f", "#b2182b", "#d6604d", "#f4a582", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#fddbc7", "#e0e0e0", "#bababa", "#878787", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#4d4d4d", "#1a1a1a" }) //$NON-NLS-1$ //$NON-NLS-2$
			.put("rdgy11", //$NON-NLS-1$
					new String[] { "#67001f", "#b2182b", "#d6604d", "#f4a582", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#fddbc7", "#ffffff", "#e0e0e0", "#bababa", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#878787", "#4d4d4d", "#1a1a1a" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			.put("rdgy3", new String[] { "#ef8a62", "#ffffff", "#999999" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("rdgy4", //$NON-NLS-1$
					new String[] { "#ca0020", "#f4a582", "#bababa", "#404040" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("rdgy5", //$NON-NLS-1$
					new String[] { "#ca0020", "#f4a582", "#ffffff", "#bababa", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#404040" }) //$NON-NLS-1$
			.put("rdgy6", //$NON-NLS-1$
					new String[] { "#b2182b", "#ef8a62", "#fddbc7", "#e0e0e0", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#999999", "#4d4d4d" }) //$NON-NLS-1$ //$NON-NLS-2$
			.put("rdgy7", //$NON-NLS-1$
					new String[] { "#b2182b", "#ef8a62", "#fddbc7", "#ffffff", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#e0e0e0", "#999999", "#4d4d4d" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			.put("rdgy8", //$NON-NLS-1$
					new String[] { "#b2182b", "#d6604d", "#f4a582", "#fddbc7", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#e0e0e0", "#bababa", "#878787", "#4d4d4d" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("rdgy9", //$NON-NLS-1$
					new String[] { "#b2182b", "#d6604d", "#f4a582", "#fddbc7", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#ffffff", "#e0e0e0", "#bababa", "#878787", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#4d4d4d" }) //$NON-NLS-1$
			.put("rdpu3", new String[] { "#fde0dd", "#fa9fb5", "#c51b8a" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("rdpu4", //$NON-NLS-1$
					new String[] { "#feebe2", "#fbb4b9", "#f768a1", "#ae017e" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("rdpu5", //$NON-NLS-1$
					new String[] { "#feebe2", "#fbb4b9", "#f768a1", "#c51b8a", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#7a0177" }) //$NON-NLS-1$
			.put("rdpu6", //$NON-NLS-1$
					new String[] { "#feebe2", "#fcc5c0", "#fa9fb5", "#f768a1", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#c51b8a", "#7a0177" }) //$NON-NLS-1$ //$NON-NLS-2$
			.put("rdpu7", //$NON-NLS-1$
					new String[] { "#feebe2", "#fcc5c0", "#fa9fb5", "#f768a1", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#dd3497", "#ae017e", "#7a0177" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			.put("rdpu8", //$NON-NLS-1$
					new String[] { "#fff7f3", "#fde0dd", "#fcc5c0", "#fa9fb5", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#f768a1", "#dd3497", "#ae017e", "#7a0177" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("rdpu9", //$NON-NLS-1$
					new String[] { "#fff7f3", "#fde0dd", "#fcc5c0", "#fa9fb5", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#f768a1", "#dd3497", "#ae017e", "#7a0177", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#49006a" }) //$NON-NLS-1$
			.put("rdylbu10", //$NON-NLS-1$
					new String[] { "#a50026", "#d73027", "#f46d43", "#fdae61", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#fee090", "#e0f3f8", "#abd9e9", "#74add1", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#4575b4", "#313695" }) //$NON-NLS-1$ //$NON-NLS-2$
			.put("rdylbu11", //$NON-NLS-1$
					new String[] { "#a50026", "#d73027", "#f46d43", "#fdae61", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#fee090", "#ffffbf", "#e0f3f8", "#abd9e9", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#74add1", "#4575b4", "#313695" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			.put("rdylbu3", new String[] { "#fc8d59", "#ffffbf", "#91bfdb" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("rdylbu4", //$NON-NLS-1$
					new String[] { "#d7191c", "#fdae61", "#abd9e9", "#2c7bb6" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("rdylbu5", //$NON-NLS-1$
					new String[] { "#d7191c", "#fdae61", "#ffffbf", "#abd9e9", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#2c7bb6" }) //$NON-NLS-1$
			.put("rdylbu6", //$NON-NLS-1$
					new String[] { "#d73027", "#fc8d59", "#fee090", "#e0f3f8", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#91bfdb", "#4575b4" }) //$NON-NLS-1$ //$NON-NLS-2$
			.put("rdylbu7", //$NON-NLS-1$
					new String[] { "#d73027", "#fc8d59", "#fee090", "#ffffbf", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#e0f3f8", "#91bfdb", "#4575b4" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			.put("rdylbu8", //$NON-NLS-1$
					new String[] { "#d73027", "#f46d43", "#fdae61", "#fee090", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#e0f3f8", "#abd9e9", "#74add1", "#4575b4" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("rdylbu9", //$NON-NLS-1$
					new String[] { "#d73027", "#f46d43", "#fdae61", "#fee090", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#ffffbf", "#e0f3f8", "#abd9e9", "#74add1", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#4575b4" }) //$NON-NLS-1$
			.put("rdylgn10", //$NON-NLS-1$
					new String[] { "#a50026", "#d73027", "#f46d43", "#fdae61", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#fee08b", "#d9ef8b", "#a6d96a", "#66bd63", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#1a9850", "#006837" }) //$NON-NLS-1$ //$NON-NLS-2$
			.put("rdylgn11", //$NON-NLS-1$
					new String[] { "#a50026", "#d73027", "#f46d43", "#fdae61", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#fee08b", "#ffffbf", "#d9ef8b", "#a6d96a", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#66bd63", "#1a9850", "#006837" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			.put("rdylgn3", new String[] { "#fc8d59", "#ffffbf", "#91cf60" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("rdylgn4", //$NON-NLS-1$
					new String[] { "#d7191c", "#fdae61", "#a6d96a", "#1a9641" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("rdylgn5", //$NON-NLS-1$
					new String[] { "#d7191c", "#fdae61", "#ffffbf", "#a6d96a", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#1a9641" }) //$NON-NLS-1$
			.put("rdylgn6", //$NON-NLS-1$
					new String[] { "#d73027", "#fc8d59", "#fee08b", "#d9ef8b", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#91cf60", "#1a9850" }) //$NON-NLS-1$ //$NON-NLS-2$
			.put("rdylgn7", //$NON-NLS-1$
					new String[] { "#d73027", "#fc8d59", "#fee08b", "#ffffbf", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#d9ef8b", "#91cf60", "#1a9850" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			.put("rdylgn8", //$NON-NLS-1$
					new String[] { "#d73027", "#f46d43", "#fdae61", "#fee08b", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#d9ef8b", "#a6d96a", "#66bd63", "#1a9850" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("rdylgn9", //$NON-NLS-1$
					new String[] { "#d73027", "#f46d43", "#fdae61", "#fee08b", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#ffffbf", "#d9ef8b", "#a6d96a", "#66bd63", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#1a9850" }) //$NON-NLS-1$
			.put("reds3", new String[] { "#fee0d2", "#fc9272", "#de2d26" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("reds4", //$NON-NLS-1$
					new String[] { "#fee5d9", "#fcae91", "#fb6a4a", "#cb181d" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("reds5", //$NON-NLS-1$
					new String[] { "#fee5d9", "#fcae91", "#fb6a4a", "#de2d26", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#a50f15" }) //$NON-NLS-1$
			.put("reds6", //$NON-NLS-1$
					new String[] { "#fee5d9", "#fcbba1", "#fc9272", "#fb6a4a", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#de2d26", "#a50f15" }) //$NON-NLS-1$ //$NON-NLS-2$
			.put("reds7", //$NON-NLS-1$
					new String[] { "#fee5d9", "#fcbba1", "#fc9272", "#fb6a4a", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#ef3b2c", "#cb181d", "#99000d" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			.put("reds8", //$NON-NLS-1$
					new String[] { "#fff5f0", "#fee0d2", "#fcbba1", "#fc9272", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#fb6a4a", "#ef3b2c", "#cb181d", "#99000d" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("reds9", //$NON-NLS-1$
					new String[] { "#fff5f0", "#fee0d2", "#fcbba1", "#fc9272", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#fb6a4a", "#ef3b2c", "#cb181d", "#a50f15", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#67000d" }) //$NON-NLS-1$
			.put("set13", new String[] { "#e41a1c", "#377eb8", "#4daf4a" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("set14", //$NON-NLS-1$
					new String[] { "#e41a1c", "#377eb8", "#4daf4a", "#984ea3" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("set15", //$NON-NLS-1$
					new String[] { "#e41a1c", "#377eb8", "#4daf4a", "#984ea3", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#ff7f00" }) //$NON-NLS-1$
			.put("set16", //$NON-NLS-1$
					new String[] { "#e41a1c", "#377eb8", "#4daf4a", "#984ea3", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#ff7f00", "#ffff33" }) //$NON-NLS-1$ //$NON-NLS-2$
			.put("set17", //$NON-NLS-1$
					new String[] { "#e41a1c", "#377eb8", "#4daf4a", "#984ea3", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#ff7f00", "#ffff33", "#a65628" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			.put("set18", //$NON-NLS-1$
					new String[] { "#e41a1c", "#377eb8", "#4daf4a", "#984ea3", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#ff7f00", "#ffff33", "#a65628", "#f781bf" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("set19", //$NON-NLS-1$
					new String[] { "#e41a1c", "#377eb8", "#4daf4a", "#984ea3", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#ff7f00", "#ffff33", "#a65628", "#f781bf", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#999999" }) //$NON-NLS-1$
			.put("set23", new String[] { "#66c2a5", "#fc8d62", "#8da0cb" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("set24", //$NON-NLS-1$
					new String[] { "#66c2a5", "#fc8d62", "#8da0cb", "#e78ac3" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("set25", //$NON-NLS-1$
					new String[] { "#66c2a5", "#fc8d62", "#8da0cb", "#e78ac3", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#a6d854" }) //$NON-NLS-1$
			.put("set26", //$NON-NLS-1$
					new String[] { "#66c2a5", "#fc8d62", "#8da0cb", "#e78ac3", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#a6d854", "#ffd92f" }) //$NON-NLS-1$ //$NON-NLS-2$
			.put("set27", //$NON-NLS-1$
					new String[] { "#66c2a5", "#fc8d62", "#8da0cb", "#e78ac3", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#a6d854", "#ffd92f", "#e5c494" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			.put("set28", //$NON-NLS-1$
					new String[] { "#66c2a5", "#fc8d62", "#8da0cb", "#e78ac3", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#a6d854", "#ffd92f", "#e5c494", "#b3b3b3" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("set310", //$NON-NLS-1$
					new String[] { "#8dd3c7", "#ffffb3", "#bebada", "#fb8072", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#80b1d3", "#fdb462", "#b3de69", "#fccde5", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#d9d9d9", "#bc80bd" }) //$NON-NLS-1$ //$NON-NLS-2$
			.put("set311", //$NON-NLS-1$
					new String[] { "#8dd3c7", "#ffffb3", "#bebada", "#fb8072", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#80b1d3", "#fdb462", "#b3de69", "#fccde5", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#d9d9d9", "#bc80bd", "#ccebc5" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			.put("set312", //$NON-NLS-1$
					new String[] { "#8dd3c7", "#ffffb3", "#bebada", "#fb8072", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#80b1d3", "#fdb462", "#b3de69", "#fccde5", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#d9d9d9", "#bc80bd", "#ccebc5", "#ffed6f" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("set33", new String[] { "#8dd3c7", "#ffffb3", "#bebada" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("set34", //$NON-NLS-1$
					new String[] { "#8dd3c7", "#ffffb3", "#bebada", "#fb8072" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("set35", //$NON-NLS-1$
					new String[] { "#8dd3c7", "#ffffb3", "#bebada", "#fb8072", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#80b1d3" }) //$NON-NLS-1$
			.put("set36", //$NON-NLS-1$
					new String[] { "#8dd3c7", "#ffffb3", "#bebada", "#fb8072", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#80b1d3", "#fdb462" }) //$NON-NLS-1$ //$NON-NLS-2$
			.put("set37", //$NON-NLS-1$
					new String[] { "#8dd3c7", "#ffffb3", "#bebada", "#fb8072", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#80b1d3", "#fdb462", "#b3de69" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			.put("set38", //$NON-NLS-1$
					new String[] { "#8dd3c7", "#ffffb3", "#bebada", "#fb8072", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#80b1d3", "#fdb462", "#b3de69", "#fccde5" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("set39", //$NON-NLS-1$
					new String[] { "#8dd3c7", "#ffffb3", "#bebada", "#fb8072", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#80b1d3", "#fdb462", "#b3de69", "#fccde5", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#d9d9d9" }) //$NON-NLS-1$
			.put("spectral10", //$NON-NLS-1$
					new String[] { "#9e0142", "#d53e4f", "#f46d43", "#fdae61", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#fee08b", "#e6f598", "#abdda4", "#66c2a5", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#3288bd", "#5e4fa2" }) //$NON-NLS-1$ //$NON-NLS-2$
			.put("spectral11", //$NON-NLS-1$
					new String[] { "#9e0142", "#d53e4f", "#f46d43", "#fdae61", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#fee08b", "#ffffbf", "#e6f598", "#abdda4", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#66c2a5", "#3288bd", "#5e4fa2" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			.put("spectral3", new String[] { "#fc8d59", "#ffffbf", "#99d594" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("spectral4", //$NON-NLS-1$
					new String[] { "#d7191c", "#fdae61", "#abdda4", "#2b83ba" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("spectral5", //$NON-NLS-1$
					new String[] { "#d7191c", "#fdae61", "#ffffbf", "#abdda4", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#2b83ba" }) //$NON-NLS-1$
			.put("spectral6", //$NON-NLS-1$
					new String[] { "#d53e4f", "#fc8d59", "#fee08b", "#e6f598", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#99d594", "#3288bd" }) //$NON-NLS-1$ //$NON-NLS-2$
			.put("spectral7", //$NON-NLS-1$
					new String[] { "#d53e4f", "#fc8d59", "#fee08b", "#ffffbf", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#e6f598", "#99d594", "#3288bd" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			.put("spectral8", //$NON-NLS-1$
					new String[] { "#d53e4f", "#f46d43", "#fdae61", "#fee08b", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#e6f598", "#abdda4", "#66c2a5", "#3288bd" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("spectral9", //$NON-NLS-1$
					new String[] { "#d53e4f", "#f46d43", "#fdae61", "#fee08b", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#ffffbf", "#e6f598", "#abdda4", "#66c2a5", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#3288bd" }) //$NON-NLS-1$
			.put("ylgn3", new String[] { "#f7fcb9", "#addd8e", "#31a354" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("ylgn4", //$NON-NLS-1$
					new String[] { "#ffffcc", "#c2e699", "#78c679", "#238443" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("ylgn5", //$NON-NLS-1$
					new String[] { "#ffffcc", "#c2e699", "#78c679", "#31a354", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#006837" }) //$NON-NLS-1$
			.put("ylgn6", //$NON-NLS-1$
					new String[] { "#ffffcc", "#d9f0a3", "#addd8e", "#78c679", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#31a354", "#006837" }) //$NON-NLS-1$ //$NON-NLS-2$
			.put("ylgn7", //$NON-NLS-1$
					new String[] { "#ffffcc", "#d9f0a3", "#addd8e", "#78c679", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#41ab5d", "#238443", "#005a32" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			.put("ylgn8", //$NON-NLS-1$
					new String[] { "#ffffe5", "#f7fcb9", "#d9f0a3", "#addd8e", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#78c679", "#41ab5d", "#238443", "#005a32" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("ylgn9", //$NON-NLS-1$
					new String[] { "#ffffe5", "#f7fcb9", "#d9f0a3", "#addd8e", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#78c679", "#41ab5d", "#238443", "#006837", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#004529" }) //$NON-NLS-1$
			.put("ylgnbu3", new String[] { "#edf8b1", "#7fcdbb", "#2c7fb8" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("ylgnbu4", //$NON-NLS-1$
					new String[] { "#ffffcc", "#a1dab4", "#41b6c4", "#225ea8" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("ylgnbu5", //$NON-NLS-1$
					new String[] { "#ffffcc", "#a1dab4", "#41b6c4", "#2c7fb8", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#253494" }) //$NON-NLS-1$
			.put("ylgnbu6", //$NON-NLS-1$
					new String[] { "#ffffcc", "#c7e9b4", "#7fcdbb", "#41b6c4", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#2c7fb8", "#253494" }) //$NON-NLS-1$ //$NON-NLS-2$
			.put("ylgnbu7", //$NON-NLS-1$
					new String[] { "#ffffcc", "#c7e9b4", "#7fcdbb", "#41b6c4", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#1d91c0", "#225ea8", "#0c2c84" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			.put("ylgnbu8", //$NON-NLS-1$
					new String[] { "#ffffd9", "#edf8b1", "#c7e9b4", "#7fcdbb", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#41b6c4", "#1d91c0", "#225ea8", "#0c2c84" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("ylgnbu9", //$NON-NLS-1$
					new String[] { "#ffffd9", "#edf8b1", "#c7e9b4", "#7fcdbb", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#41b6c4", "#1d91c0", "#225ea8", "#253494", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#081d58" }) //$NON-NLS-1$
			.put("ylorbr3", new String[] { "#fff7bc", "#fec44f", "#d95f0e" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("ylorbr4", //$NON-NLS-1$
					new String[] { "#ffffd4", "#fed98e", "#fe9929", "#cc4c02" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("ylorbr5", //$NON-NLS-1$
					new String[] { "#ffffd4", "#fed98e", "#fe9929", "#d95f0e", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#993404" }) //$NON-NLS-1$
			.put("ylorbr6", //$NON-NLS-1$
					new String[] { "#ffffd4", "#fee391", "#fec44f", "#fe9929", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#d95f0e", "#993404" }) //$NON-NLS-1$ //$NON-NLS-2$
			.put("ylorbr7", //$NON-NLS-1$
					new String[] { "#ffffd4", "#fee391", "#fec44f", "#fe9929", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#ec7014", "#cc4c02", "#8c2d04" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			.put("ylorbr8", //$NON-NLS-1$
					new String[] { "#ffffe5", "#fff7bc", "#fee391", "#fec44f", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#fe9929", "#ec7014", "#cc4c02", "#8c2d04" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("ylorbr9", //$NON-NLS-1$
					new String[] { "#ffffe5", "#fff7bc", "#fee391", "#fec44f", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#fe9929", "#ec7014", "#cc4c02", "#993404", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#662506" }) //$NON-NLS-1$
			.put("ylorrd3", new String[] { "#ffeda0", "#feb24c", "#f03b20" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("ylorrd4", //$NON-NLS-1$
					new String[] { "#ffffb2", "#fecc5c", "#fd8d3c", "#e31a1c" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("ylorrd5", //$NON-NLS-1$
					new String[] { "#ffffb2", "#fecc5c", "#fd8d3c", "#f03b20", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#bd0026" }) //$NON-NLS-1$
			.put("ylorrd6", //$NON-NLS-1$
					new String[] { "#ffffb2", "#fed976", "#feb24c", "#fd8d3c", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#f03b20", "#bd0026" }) //$NON-NLS-1$ //$NON-NLS-2$
			.put("ylorrd7", //$NON-NLS-1$
					new String[] { "#ffffb2", "#fed976", "#feb24c", "#fd8d3c", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#fc4e2a", "#e31a1c", "#b10026" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			.put("ylorrd8", //$NON-NLS-1$
					new String[] { "#ffffcc", "#ffeda0", "#fed976", "#feb24c", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#fd8d3c", "#fc4e2a", "#e31a1c", "#b10026" }) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.put("ylorrd9", //$NON-NLS-1$
					new String[] { "#ffffcc", "#ffeda0", "#fed976", "#feb24c", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#fd8d3c", "#fc4e2a", "#e31a1c", "#bd0026", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"#800026" }) //$NON-NLS-1$
			.build();
}
