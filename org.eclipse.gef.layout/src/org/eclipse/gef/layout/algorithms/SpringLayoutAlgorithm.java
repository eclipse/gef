/*******************************************************************************
 * Copyright (c) 2005, 2017 The Chisel Group and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors: Jingwei Wu, Rob Lintern, Casey Best, Ian Bull (The Chisel Group) - initial API and implementation
 *               Mateusz Matela - "Tree Views for Zest" contribution, Google Summer of Code 2009
 *               Matthias Wienand (itemis AG) - refactorings
 *               Alexander Nyßen (itemis AG) - refactorings
 * 
 ******************************************************************************/
package org.eclipse.gef.layout.algorithms;

import java.util.HashMap;

import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.layout.ILayoutAlgorithm;
import org.eclipse.gef.layout.LayoutContext;
import org.eclipse.gef.layout.LayoutProperties;

/**
 * The SpringLayoutAlgorithm has its own data repository and relation
 * repository. A user can populate the repository, specify the layout
 * conditions, do the computation and query the computed results.
 * 
 * @author Jingwei Wu
 * @author Rob Lintern
 * @author Casey Best
 * @author Ian Bull
 * @author Mateusz Matela
 * @author mwienand
 */
public class SpringLayoutAlgorithm implements ILayoutAlgorithm {

	/**
	 * The default value for the spring layout number of iterations.
	 */
	private static final int DEFAULT_SPRING_ITERATIONS = 1000;

	/**
	 * the default value for the time algorithm runs.
	 */
	private static final long MAX_SPRING_TIME = 10000;

	/**
	 * The default value for positioning nodes randomly.
	 */
	private static final boolean DEFAULT_SPRING_RANDOM = true;

	/**
	 * The default value for the spring layout move-control.
	 */
	private static final double DEFAULT_SPRING_MOVE = 1.0f;

	/**
	 * The default value for the spring layout strain-control.
	 */
	private static final double DEFAULT_SPRING_STRAIN = 1.0f;

	/**
	 * The default value for the spring layout length-control.
	 */
	private static final double DEFAULT_SPRING_LENGTH = 3.0f;

	/**
	 * The default value for the spring layout gravitation-control.
	 */
	private static final double DEFAULT_SPRING_GRAVITATION = 2.0f;

	/**
	 * Minimum distance considered between nodes
	 */
	private static final double MIN_DISTANCE = 1.0d;

	/**
	 * The variable can be customized to set the number of iterations used.
	 */
	private int sprIterations = DEFAULT_SPRING_ITERATIONS;

	/**
	 * This variable can be customized to set the max number of MS the algorithm
	 * should run
	 */
	private long maxTimeMS = MAX_SPRING_TIME;

	/**
	 * The variable can be customized to set whether or not the spring layout
	 * nodes are positioned randomly before beginning iterations.
	 */
	private boolean sprRandom = DEFAULT_SPRING_RANDOM;

	/**
	 * The variable can be customized to set the spring layout move-control.
	 */
	private double sprMove = DEFAULT_SPRING_MOVE;

	/**
	 * The variable can be customized to set the spring layout strain-control.
	 */
	private double sprStrain = DEFAULT_SPRING_STRAIN;

	/**
	 * The variable can be customized to set the spring layout length-control.
	 */
	private double sprLength = DEFAULT_SPRING_LENGTH;

	/**
	 * The variable can be customized to set the spring layout
	 * gravitation-control.
	 */
	private double sprGravitation = DEFAULT_SPRING_GRAVITATION;

	/**
	 * Variable indicating whether the algorithm should resize elements.
	 */
	private boolean resize = false;

	private int iteration;
	private double[][] srcDestToSumOfWeights;
	private Node[] entities;
	private double[] forcesX, forcesY;
	private double[] locationsX, locationsY;
	private double[] sizeW, sizeH;
	private Rectangle bounds;
	private double boundsScaleX = 0.2;
	private double boundsScaleY = 0.2;

	// XXX: Needed by performNIteration(int), see below.
	private LayoutContext layoutContext;

	// TODO: expose field
	private boolean fitWithinBounds = true;

	public void applyLayout(LayoutContext layoutContext, boolean clean) {
		this.layoutContext = layoutContext;
		initLayout(layoutContext);
		if (!clean) {
			return;
		}

		while (performAnotherNonContinuousIteration()) {
			computeOneIteration();
		}

		saveLocations();
		if (resize)
			AlgorithmHelper.maximizeSizes(entities);

		if (fitWithinBounds) {
			Rectangle bounds2 = new Rectangle(bounds);
			int insets = 4;
			bounds2.setX(bounds2.getX() + insets);
			bounds2.setY(bounds2.getY() + insets);
			bounds2.setWidth(bounds2.getWidth() - 2 * insets);
			bounds2.setHeight(bounds2.getHeight() - 2 * insets);
			AlgorithmHelper.fitWithinBounds(entities, bounds2, resize);
		}
	}

	/**
	 * Performs the given number of iterations.
	 * 
	 * @param n
	 *            The number of iterations to perform.
	 */
	public void performNIteration(int n) {
		layoutContext.preLayout();
		if (iteration == 0) {
			entities = layoutContext.getNodes();
			loadLocations();
			initLayout(layoutContext);
		}
		bounds = LayoutProperties.getBounds(layoutContext.getGraph());
		for (int i = 0; i < n; i++) {
			computeOneIteration();
			saveLocations();
		}
		layoutContext.postLayout();
	}

	/**
	 * Performs one single iteration.
	 * 
	 */
	public void performOneIteration() {
		layoutContext.preLayout();
		if (iteration == 0) {
			entities = layoutContext.getNodes();
			loadLocations();
			initLayout(layoutContext);
		}
		bounds = LayoutProperties.getBounds(layoutContext.getGraph());
		computeOneIteration();
		saveLocations();
		layoutContext.postLayout();
	}

	/**
	 * 
	 * @return true if this algorithm is set to resize elements
	 */
	public boolean isResizing() {
		return resize;
	}

	/**
	 * 
	 * @param resizing
	 *            true if this algorithm should resize elements (default is
	 *            false)
	 */
	public void setResizing(boolean resizing) {
		resize = resizing;
	}

	/**
	 * Sets the spring layout move-control.
	 * 
	 * @param move
	 *            The move-control value.
	 */
	public void setSpringMove(double move) {
		sprMove = move;
	}

	/**
	 * Returns the move-control value of this SpringLayoutAlgorithm in double
	 * precision.
	 * 
	 * @return The move-control value.
	 */
	public double getSpringMove() {
		return sprMove;
	}

	/**
	 * Sets the spring layout strain-control.
	 * 
	 * @param strain
	 *            The strain-control value.
	 */
	public void setSpringStrain(double strain) {
		sprStrain = strain;
	}

	/**
	 * Returns the strain-control value of this SpringLayoutAlgorithm in double
	 * precision.
	 * 
	 * @return The strain-control value.
	 */
	public double getSpringStrain() {
		return sprStrain;
	}

	/**
	 * Sets the spring layout length-control.
	 * 
	 * @param length
	 *            The length-control value.
	 */
	public void setSpringLength(double length) {
		sprLength = length;
	}

	/**
	 * Gets the max time this algorithm will run for
	 * 
	 * @return the timeout up to which this algorithm may run
	 */
	public long getSpringTimeout() {
		return maxTimeMS;
	}

	/**
	 * Sets the spring timeout to the given value (in millis).
	 * 
	 * @param timeout
	 *            The new spring timeout (in millis).
	 */
	public void setSpringTimeout(long timeout) {
		maxTimeMS = timeout;
	}

	/**
	 * Returns the length-control value of this {@link SpringLayoutAlgorithm} in
	 * double precision.
	 * 
	 * @return The length-control value.
	 */
	public double getSpringLength() {
		return sprLength;
	}

	/**
	 * Sets the spring layout gravitation-control.
	 * 
	 * @param gravitation
	 *            The gravitation-control value.
	 */
	public void setSpringGravitation(double gravitation) {
		sprGravitation = gravitation;
	}

	/**
	 * Returns the gravitation-control value of this SpringLayoutAlgorithm in
	 * double precision.
	 * 
	 * @return The gravitation-control value.
	 */
	public double getSpringGravitation() {
		return sprGravitation;
	}

	/**
	 * Sets the number of iterations to be used.
	 * 
	 * @param iterations
	 *            The number of iterations.
	 */
	public void setIterations(int iterations) {
		sprIterations = iterations;
	}

	/**
	 * Returns the number of iterations to be used.
	 * 
	 * @return The number of iterations.
	 */
	public int getIterations() {
		return sprIterations;
	}

	/**
	 * Sets whether or not this SpringLayoutAlgorithm will layout the nodes
	 * randomly before beginning iterations.
	 * 
	 * @param random
	 *            The random placement value.
	 */
	public void setRandom(boolean random) {
		sprRandom = random;
	}

	/**
	 * Returns whether or not this {@link SpringLayoutAlgorithm} will layout the
	 * nodes randomly before beginning iterations.
	 * 
	 * @return <code>true</code> if this algorithm will layout the nodes
	 *         randomly before iterating, otherwise <code>false</code>.
	 */
	public boolean getRandom() {
		return sprRandom;
	}

	private long startTime = 0;

	private void initLayout(LayoutContext context) {
		entities = context.getNodes();
		bounds = LayoutProperties.getBounds(context.getGraph());
		loadLocations();

		srcDestToSumOfWeights = new double[entities.length][entities.length];
		HashMap<Node, Integer> entityToPosition = new HashMap<>();
		for (int i = 0; i < entities.length; i++) {
			entityToPosition.put(entities[i], new Integer(i));
		}

		Edge[] connections = context.getEdges();
		for (int i = 0; i < connections.length; i++) {
			Edge connection = connections[i];
			Integer source = entityToPosition.get(connection.getSource());
			Integer target = entityToPosition.get(connection.getTarget());
			if (source == null || target == null)
				continue;
			double weight = LayoutProperties.getWeight(connection);
			weight = (weight <= 0 ? 0.1 : weight);
			srcDestToSumOfWeights[source.intValue()][target
					.intValue()] += weight;
			srcDestToSumOfWeights[target.intValue()][source
					.intValue()] += weight;
		}

		if (sprRandom)
			placeRandomly(); // put vertices in random places

		iteration = 1;

		startTime = System.currentTimeMillis();
	}

	private void loadLocations() {
		if (locationsX == null || locationsX.length != entities.length) {
			int length = entities.length;
			locationsX = new double[length];
			locationsY = new double[length];
			sizeW = new double[length];
			sizeH = new double[length];
			forcesX = new double[length];
			forcesY = new double[length];
		}
		for (int i = 0; i < entities.length; i++) {
			Point location = LayoutProperties.getLocation(entities[i]);
			locationsX[i] = location.x;
			locationsY[i] = location.y;
			Dimension size = LayoutProperties.getSize(entities[i]);
			sizeW[i] = size.width;
			sizeH[i] = size.height;
		}
	}

	private void saveLocations() {
		if (entities == null)
			return;
		for (int i = 0; i < entities.length; i++) {
			// TODO ensure no dynamic layout passes are triggered as a result of
			// storing the positions
			// TODO: check where NaN values originate from
			if (Double.isNaN(locationsX[i]) || Double.isNaN(locationsY[i])) {
				locationsX[i] = 0;
				locationsY[i] = 0;
			}
			LayoutProperties.setLocation(entities[i],
					new Point(locationsX[i], locationsY[i]));
		}
	}

	/**
	 * Scales the current iteration counter based on how long the algorithm has
	 * been running for. You can set the MaxTime in maxTimeMS!
	 */
	private void setSprIterationsBasedOnTime() {
		if (maxTimeMS <= 0)
			return;

		long currentTime = System.currentTimeMillis();
		double fractionComplete = (double) ((double) (currentTime - startTime)
				/ ((double) maxTimeMS));
		int currentIteration = (int) (fractionComplete * sprIterations);
		if (currentIteration > iteration) {
			iteration = currentIteration;
		}

	}

	/**
	 * Performs one iteration based on time.
	 * 
	 * @return <code>true</code> if the maximum number of iterations was not
	 *         reached yet, otherwise <code>false</code>.
	 */
	protected boolean performAnotherNonContinuousIteration() {
		setSprIterationsBasedOnTime();
		return (iteration <= sprIterations);
	}

	/**
	 * Returns the current iteration.
	 * 
	 * @return The current iteration.
	 */
	protected int getCurrentLayoutStep() {
		return iteration;
	}

	/**
	 * Returns the maximum number of iterations.
	 * 
	 * @return The maximum number of iterations.
	 */
	protected int getTotalNumberOfLayoutSteps() {
		return sprIterations;
	}

	/**
	 * Computes one iteration (forces, positions) and increases the iteration
	 * counter.
	 */
	protected void computeOneIteration() {
		computeForces();
		computePositions();
		Rectangle currentBounds = getLayoutBounds();
		improveBoundScaleX(currentBounds);
		improveBoundScaleY(currentBounds);
		moveToCenter(currentBounds);
		iteration++;
	}

	/**
	 * Puts vertices in random places, all between (0,0) and (1,1).
	 */
	protected void placeRandomly() {
		if (locationsX.length == 0) {
			return;
		}

		// If only one node in the data repository, put it in the middle
		if (locationsX.length == 1) {
			// If only one node in the data repository, put it in the middle
			locationsX[0] = bounds.getX() + 0.5 * bounds.getWidth();
			locationsY[0] = bounds.getY() + 0.5 * bounds.getHeight();
		} else {
			locationsX[0] = bounds.getX();
			locationsY[0] = bounds.getY();
			locationsX[1] = bounds.getX() + bounds.getWidth();
			locationsY[1] = bounds.getY() + bounds.getHeight();
			for (int i = 2; i < locationsX.length; i++) {
				locationsX[i] = bounds.getX()
						+ Math.random() * bounds.getWidth();
				locationsY[i] = bounds.getY()
						+ Math.random() * bounds.getHeight();
			}
		}
	}

	/**
	 * Computes the force for each node in this SpringLayoutAlgorithm. The
	 * computed force will be stored in the data repository
	 */
	protected void computeForces() {

		double forcesX[][] = new double[2][this.forcesX.length];
		double forcesY[][] = new double[2][this.forcesX.length];
		double locationsX[] = new double[this.forcesX.length];
		double locationsY[] = new double[this.forcesX.length];

		// // initialize all forces to zero
		for (int j = 0; j < 2; j++) {
			for (int i = 0; i < this.forcesX.length; i++) {
				forcesX[j][i] = 0;
				forcesY[j][i] = 0;
				locationsX[i] = this.locationsX[i];
				locationsY[i] = this.locationsY[i];
			}
		}
		// TODO: Again really really slow!

		for (int k = 0; k < 2; k++) {
			for (int i = 0; i < this.locationsX.length; i++) {

				for (int j = i + 1; j < locationsX.length; j++) {
					double dx = (locationsX[i] - locationsX[j])
							/ bounds.getWidth() / boundsScaleX;
					double dy = (locationsY[i] - locationsY[j])
							/ bounds.getHeight() / boundsScaleY;
					double distance_sq = dx * dx + dy * dy;
					// make sure distance and distance squared not too small
					distance_sq = Math.max(MIN_DISTANCE * MIN_DISTANCE,
							distance_sq);
					double distance = Math.sqrt(distance_sq);

					// If there are relationships between srcObj and destObj
					// then decrease force on srcObj (a pull) in direction of
					// destObj
					// If no relation between srcObj and destObj then increase
					// force on srcObj (a push) from direction of destObj.
					double sumOfWeights = srcDestToSumOfWeights[i][j];

					double f;
					if (sumOfWeights > 0) {
						// nodes are pulled towards each other
						f = -sprStrain * Math.log(distance / sprLength)
								* sumOfWeights;
					} else {
						// nodes are repelled from each other
						f = sprGravitation / (distance_sq);
					}
					double dfx = f * dx / distance;
					double dfy = f * dy / distance;

					forcesX[k][i] += dfx;
					forcesY[k][i] += dfy;

					forcesX[k][j] -= dfx;
					forcesY[k][j] -= dfy;
				}
			}

			for (int i = 0; i < entities.length; i++) {
				if (LayoutProperties.isMovable(entities[i])) {
					double deltaX = sprMove * forcesX[k][i];
					double deltaY = sprMove * forcesY[k][i];

					// constrain movement, so that nodes don't shoot way off to
					// the
					// edge
					double dist = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
					double maxMovement = 0.2d * sprMove;
					if (dist > maxMovement) {
						deltaX *= maxMovement / dist;
						deltaY *= maxMovement / dist;
					}

					locationsX[i] += deltaX * bounds.getWidth() * boundsScaleX;
					locationsY[i] += deltaY * bounds.getHeight() * boundsScaleY;
				}
			}

		}
		// // initialize all forces to zero
		for (int i = 0; i < this.entities.length; i++) {
			if (forcesX[0][i] * forcesX[1][i] < 0) {
				this.forcesX[i] = 0;
			} else {
				this.forcesX[i] = forcesX[1][i];
			}

			if (forcesY[0][i] * forcesY[1][i] < 0) {
				this.forcesY[i] = 0;
			} else {
				this.forcesY[i] = forcesY[1][i];
			}

		}

	}

	/**
	 * Computes the position for each node in this SpringLayoutAlgorithm. The
	 * computed position will be stored in the data repository. position =
	 * position + sprMove * force
	 */
	protected void computePositions() {
		for (int i = 0; i < entities.length; i++) {
			if (LayoutProperties.isMovable(entities[i])) {
				double deltaX = sprMove * forcesX[i];
				double deltaY = sprMove * forcesY[i];

				// constrain movement, so that nodes don't shoot way off to the
				// edge
				double dist = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
				double maxMovement = 0.2d * sprMove;
				if (dist > maxMovement) {
					deltaX *= maxMovement / dist;
					deltaY *= maxMovement / dist;
				}

				locationsX[i] += deltaX * bounds.getWidth() * boundsScaleX;
				locationsY[i] += deltaY * bounds.getHeight() * boundsScaleY;
			}
		}
	}

	private Rectangle getLayoutBounds() {
		double minX, maxX, minY, maxY;
		minX = minY = Double.POSITIVE_INFINITY;
		maxX = maxY = Double.NEGATIVE_INFINITY;

		for (int i = 0; i < locationsX.length; i++) {
			maxX = Math.max(maxX, locationsX[i] + sizeW[i] / 2);
			minX = Math.min(minX, locationsX[i] - sizeW[i] / 2);
			maxY = Math.max(maxY, locationsY[i] + sizeH[i] / 2);
			minY = Math.min(minY, locationsY[i] - sizeH[i] / 2);
		}
		return new Rectangle(minX, minY, maxX - minX, maxY - minY);
	}

	private void improveBoundScaleX(Rectangle currentBounds) {
		double boundaryProportionX = currentBounds.getWidth()
				/ bounds.getWidth();
		if (boundaryProportionX < 0.9) {
			boundsScaleX *= 1.01;
		} else if (boundaryProportionX > 1) {
			if (boundsScaleX < 0.01)
				return;
			boundsScaleX /= 1.01;
		}
	}

	private void improveBoundScaleY(Rectangle currentBounds) {
		double boundaryProportionY = currentBounds.getHeight()
				/ bounds.getHeight();
		if (boundaryProportionY < 0.9) {
			boundsScaleY *= 1.01;
		} else if (boundaryProportionY > 1) {
			if (boundsScaleY < 0.01)
				return;
			boundsScaleY /= 1.01;
		}
	}

	private void moveToCenter(Rectangle currentBounds) {
		double moveX = (currentBounds.getX() + currentBounds.getWidth() / 2)
				- (bounds.getX() + bounds.getWidth() / 2);
		double moveY = (currentBounds.getY() + currentBounds.getHeight() / 2)
				- (bounds.getY() + bounds.getHeight() / 2);
		for (int i = 0; i < locationsX.length; i++) {
			locationsX[i] -= moveX;
			locationsY[i] -= moveY;
		}
	}
}
