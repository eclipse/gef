package org.eclipse.gef4.graphics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.gef4.geometry.planar.Rectangle;

public class PrintConfiguration {

	private List<Rectangle> pages = new ArrayList<Rectangle>();

	/**
	 * Creates a new {@link PrintConfiguration} and associates the passed-in
	 * page bounds with it.
	 * 
	 * @param pageBounds
	 *            {@link Rectangle}s representing the bounds of the individual
	 *            pages
	 */
	public PrintConfiguration(Rectangle... pageBounds) {
		pages.addAll(Arrays.asList(pageBounds));
	}

	public Rectangle[] getPageBounds() {
		return pages.toArray(new Rectangle[] {});
	}

	public Rectangle getPageBounds(int i) {
		return pages.get(i);
	}

}