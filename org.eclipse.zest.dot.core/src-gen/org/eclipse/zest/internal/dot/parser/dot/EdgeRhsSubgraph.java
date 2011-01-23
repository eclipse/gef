/**
 * <copyright>
 * </copyright>
 *
 */
package org.eclipse.zest.internal.dot.parser.dot;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Edge Rhs Subgraph</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.zest.internal.dot.parser.dot.EdgeRhsSubgraph#getSubgraph <em>Subgraph</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.zest.internal.dot.parser.dot.DotPackage#getEdgeRhsSubgraph()
 * @model
 * @generated
 */
public interface EdgeRhsSubgraph extends EdgeRhs
{
  /**
   * Returns the value of the '<em><b>Subgraph</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Subgraph</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Subgraph</em>' containment reference.
   * @see #setSubgraph(Subgraph)
   * @see org.eclipse.zest.internal.dot.parser.dot.DotPackage#getEdgeRhsSubgraph_Subgraph()
   * @model containment="true"
   * @generated
   */
  Subgraph getSubgraph();

  /**
   * Sets the value of the '{@link org.eclipse.zest.internal.dot.parser.dot.EdgeRhsSubgraph#getSubgraph <em>Subgraph</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Subgraph</em>' containment reference.
   * @see #getSubgraph()
   * @generated
   */
  void setSubgraph(Subgraph value);

} // EdgeRhsSubgraph
