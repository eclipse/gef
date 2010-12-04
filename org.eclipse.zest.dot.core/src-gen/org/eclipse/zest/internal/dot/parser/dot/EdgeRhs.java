/**
 * <copyright>
 * </copyright>
 *
 */
package org.eclipse.zest.internal.dot.parser.dot;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Edge Rhs</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.zest.internal.dot.parser.dot.EdgeRhs#getOp <em>Op</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.zest.internal.dot.parser.dot.DotPackage#getEdgeRhs()
 * @model
 * @generated
 */
public interface EdgeRhs extends EObject
{
  /**
   * Returns the value of the '<em><b>Op</b></em>' attribute.
   * The literals are from the enumeration {@link org.eclipse.zest.internal.dot.parser.dot.EdgeOp}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Op</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Op</em>' attribute.
   * @see org.eclipse.zest.internal.dot.parser.dot.EdgeOp
   * @see #setOp(EdgeOp)
   * @see org.eclipse.zest.internal.dot.parser.dot.DotPackage#getEdgeRhs_Op()
   * @model
   * @generated
   */
  EdgeOp getOp();

  /**
   * Sets the value of the '{@link org.eclipse.zest.internal.dot.parser.dot.EdgeRhs#getOp <em>Op</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Op</em>' attribute.
   * @see org.eclipse.zest.internal.dot.parser.dot.EdgeOp
   * @see #getOp()
   * @generated
   */
  void setOp(EdgeOp value);

} // EdgeRhs
