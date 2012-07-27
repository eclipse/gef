package org.eclipse.gef4.graphics.awt;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.eclipse.gef4.geometry.convert.Geometry2AWT;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.IMultiShape;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.geometry.planar.Path;
import org.eclipse.gef4.graphics.AbstractGraphics;
import org.eclipse.gef4.graphics.IGraphics;
import org.eclipse.gef4.graphics.Image;

/**
 * The AWT {@link IGraphics} implementation used to draw to the screen.
 * 
 * @author mwienand
 * 
 */
public class DisplayGraphics extends AbstractGraphics {

	private Graphics2D g;

	/**
	 * Constructs a {@link DisplayGraphics} from the given {@link Graphics2D}.
	 * 
	 * @param g2d
	 */
	public DisplayGraphics(Graphics2D g2d) {
		this.g = g2d;

		CanvasProperties cp = new CanvasProperties();
		DrawProperties dp = new DrawProperties();
		FillProperties fp = new FillProperties();
		BlitProperties bp = new BlitProperties();
		WriteProperties wp = new WriteProperties();

		pushInitialState(cp, dp, fp, bp, wp);
	}

	@Override
	protected void doBlit(Image image) {
		java.awt.Image awtImage = null;
		try {
			awtImage = ImageIO.read(image.getImageFile());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		g.drawImage(awtImage, 0, 0, null);
	}

	@Override
	protected void doDraw(ICurve curve) {
		doDraw(curve.toPath());
	}

	@Override
	protected void doDraw(Path path) {
		g.draw(Geometry2AWT.toAWTPath(path));
	}

	@Override
	protected void doFill(IMultiShape multishape) {
		doFill(multishape.toPath());
	}

	@Override
	protected void doFill(IShape shape) {
		doFill(shape.toPath());
	}

	@Override
	protected void doFill(Path path) {
		g.fill(Geometry2AWT.toAWTPath(path));
	}

	@Override
	protected void doWrite(String text) {
		FontMetrics fontMetrics = g.getFontMetrics();
		g.drawString(text, 0, fontMetrics.getMaxAscent());
	}

	/**
	 * Returns the {@link Graphics2D} that is associated with this
	 * {@link DisplayGraphics}.
	 * 
	 * @return the {@link Graphics2D} that is associated with this
	 *         {@link DisplayGraphics}
	 */
	public Graphics2D getGraphics2D() {
		return g;
	}

}
