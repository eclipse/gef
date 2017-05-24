package org.eclipse.gef.mvc.fx.operations;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef.mvc.fx.parts.IBendableContentPart;
import org.eclipse.gef.mvc.fx.parts.IBendableContentPart.BendPoint;

import javafx.scene.Node;

/**
 */
public class BendOperation extends AbstractOperation
		implements ITransactionalOperation {

	private IBendableContentPart<? extends Node> part;
	private List<BendPoint> initialBendPoints = new ArrayList<>();
	private List<BendPoint> finalBendPoints = new ArrayList<>();

	/**
	 *
	 * @param part
	 *            a
	 */
	public BendOperation(IBendableContentPart<? extends Node> part) {
		super("Bend");
		this.part = part;
		initialBendPoints.addAll(part.getVisualBendPoints());
		finalBendPoints.addAll(part.getVisualBendPoints());
	}

	@Override
	public IStatus execute(IProgressMonitor monitor,
			org.eclipse.core.runtime.IAdaptable info)
			throws ExecutionException {
		part.setVisualBendPoints(finalBendPoints);
		return Status.OK_STATUS;
	}

	/**
	 * @return a
	 */
	public List<BendPoint> getFinalBendPoints() {
		return finalBendPoints;
	}

	/**
	 * @return a
	 */
	public List<BendPoint> getInitialBendPoints() {
		return initialBendPoints;
	}

	/**
	 * @return a
	 */
	public IBendableContentPart<? extends Node> getPart() {
		return part;
	}

	@Override
	public boolean isContentRelevant() {
		return false;
	}

	@Override
	public boolean isNoOp() {
		return initialBendPoints.equals(finalBendPoints);
	}

	@Override
	public IStatus redo(IProgressMonitor monitor,
			org.eclipse.core.runtime.IAdaptable info)
			throws ExecutionException {
		return execute(monitor, info);
	}

	/**
	 *
	 * @param finalBendPoints
	 *            a
	 */
	public void setFinalBendPoints(List<BendPoint> finalBendPoints) {
		this.finalBendPoints.clear();
		this.finalBendPoints.addAll(finalBendPoints);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor,
			org.eclipse.core.runtime.IAdaptable info)
			throws ExecutionException {
		part.setVisualBendPoints(initialBendPoints);
		return Status.OK_STATUS;
	}
}