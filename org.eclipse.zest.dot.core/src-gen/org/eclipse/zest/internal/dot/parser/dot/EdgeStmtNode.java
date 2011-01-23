/**
 * <copyright>
 * </copyright>
 *
 */
package org.eclipse.zest.internal.dot.parser.dot;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Edge Stmt Node</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.zest.internal.dot.parser.dot.EdgeStmtNode#getNode_id <em>Node id</em>}</li>
 *   <li>{@link org.eclipse.zest.internal.dot.parser.dot.EdgeStmtNode#getEdgeRHS <em>Edge RHS</em>}</li>
 *   <li>{@link org.eclipse.zest.internal.dot.parser.dot.EdgeStmtNode#getAttributes <em>Attributes</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.zest.internal.dot.parser.dot.DotPackage#getEdgeStmtNode()
 * @model
 * @generated
 */
public interface EdgeStmtNode extends Stmt
{
  /**
   * Returns the value of the '<em><b>Node id</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Node id</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Node id</em>' containment reference.
   * @see #setNode_id(NodeId)
   * @see org.eclipse.zest.internal.dot.parser.dot.DotPackage#getEdgeStmtNode_Node_id()
   * @model containment="true"
   * @generated
   */
  NodeId getNode_id();

  /**
   * Sets the value of the '{@link org.eclipse.zest.internal.dot.parser.dot.EdgeStmtNode#getNode_id <em>Node id</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Node id</em>' containment reference.
   * @see #getNode_id()
   * @generated
   */
  void setNode_id(NodeId value);

  /**
   * Returns the value of the '<em><b>Edge RHS</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.zest.internal.dot.parser.dot.EdgeRhs}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Edge RHS</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Edge RHS</em>' containment reference list.
   * @see org.eclipse.zest.internal.dot.parser.dot.DotPackage#getEdgeStmtNode_EdgeRHS()
   * @model containment="true"
   * @generated
   */
  EList<EdgeRhs> getEdgeRHS();

  /**
   * Returns the value of the '<em><b>Attributes</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.zest.internal.dot.parser.dot.AttrList}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Attributes</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Attributes</em>' containment reference list.
   * @see org.eclipse.zest.internal.dot.parser.dot.DotPackage#getEdgeStmtNode_Attributes()
   * @model containment="true"
   * @generated
   */
  EList<AttrList> getAttributes();

} // EdgeStmtNode
