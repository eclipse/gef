package org.eclipse.gef4.graphics.tests;

import org.eclipse.gef4.graphics.IGraphics;
import org.junit.Before;

/**
 * The AbstractGraphicsTests class defines an attribute <i>graphics</i> which is
 * tested by the extending class. An abstract {@link #initializeGraphics()} method assigns a
 * specific {@link IGraphics} implementation to that <i>graphics</i> attribute.
 * 
 * @author mwienand
 * 
 */
public abstract class AbstractGraphicsTests {

	/**
	 * A specific IGraphics implementation that is tested.
	 */
	protected static IGraphics graphics = null;

	/**
	 * Assigns a specific {@link IGraphics} implementation to the <i>graphics</i>
	 * attribute.
	 */
	@Before
	public abstract void initializeGraphics();

}
