package org.eclipse.gef4.graphics.awt;

import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.PrinterGraphics;

import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.graphics.IPrinterGraphics;
import org.eclipse.gef4.graphics.PrintConfiguration;

/**
 * <p>
 * The AwtPrinterGraphics class implements the {@link IPrinterGraphics}
 * interface of GEF4 graphics.
 * </p>
 * 
 * <p>
 * For every page that is printed, a new AwtPrinterGraphics is created.
 * </p>
 * 
 * @author mwienand
 * 
 */
public class AwtPrinterGraphics extends AwtGraphics implements IPrinterGraphics {

	private static final double DISPLAY_DPI = 96d;
	private static final double PRINTER_DPI = 300d;
	private PrintConfiguration printConfiguration;
	private PageFormat pageFormat;
	private int pageNumber = 0;

	/**
	 * Creates a new {@link AwtPrinterGraphics} to print the page indexed by
	 * <i>pageNumber</i> (starting at <code>0</code>) with the specified
	 * {@link PrintConfiguration}, AWT {@link PageFormat}, and AWT
	 * {@link PrinterGraphics}.
	 * 
	 * @param printConfig
	 *            the {@link PrintConfiguration} that specifies page bounds
	 * @param pageNumber
	 *            index of the page to print (starting at <code>0</code>)
	 * @param pf
	 *            the AWT {@link PageFormat}
	 * @param graphics
	 */
	public AwtPrinterGraphics(PrintConfiguration printConfig, int pageNumber,
			PageFormat pf, Graphics2D graphics) {
		super(graphics);
		this.pageNumber = pageNumber;
		this.printConfiguration = printConfig;
		this.pageFormat = pf;
		prepareGraphics();
	}

	private double computeScaleFactor(Rectangle visualBounds) {
		double pageWidth = pageFormat.getImageableWidth();
		double pageHeight = pageFormat.getImageableHeight();

		double sw = pageWidth / visualBounds.getWidth();
		double sh = pageHeight / visualBounds.getHeight();
		double min = sw < sh ? sw : sh;

		return min > 1 ? 1 : min;
	}

	@Override
	public int getPageNumber() {
		return pageNumber;
	}

	@Override
	public PrintConfiguration getPrintConfiguration() {
		return printConfiguration;
	}

	private void prepareGraphics() {
		// correct resolution
		double s = PRINTER_DPI / DISPLAY_DPI;
		scale(s, s);

		// respect margins
		Rectangle visualBounds = printConfiguration.getPageBounds(pageNumber);
		translate(pageFormat.getImageableX() - visualBounds.getX(),
				pageFormat.getImageableY() - visualBounds.getY());

		// clip to the visible area
		clip(new Rectangle(0, 0, visualBounds.getWidth(),
				visualBounds.getHeight()));

		// scale to fit the page size (TODO: zoom if possible?)
		double scaleFactor = computeScaleFactor(visualBounds);
		scale(scaleFactor, scaleFactor);

		// save this configuration on the states stack
		pushState();
	}

}
