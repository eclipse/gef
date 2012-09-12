package org.eclipse.gef4.graphics.images;


public class GreyScaleFilter extends GenericMatrixFilter {

	public GreyScaleFilter() {
		this(0.33333, 0.33334, 0.33333);
	}

	public GreyScaleFilter(double redScale, double greenScale, double blueScale) {
		super(1, 0, 0, 0, 0, 0, redScale, greenScale, blueScale, 0, 0,
				redScale, greenScale, blueScale, 0, 0, redScale, greenScale,
				blueScale, 0);
	}

}
