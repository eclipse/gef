/**
 * <copyright>
 * </copyright>
 *
 */
package org.eclipse.zest.internal.dot.parser.dot.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.zest.internal.dot.parser.dot.AttrList;
import org.eclipse.zest.internal.dot.parser.dot.DotPackage;
import org.eclipse.zest.internal.dot.parser.dot.EdgeRhs;
import org.eclipse.zest.internal.dot.parser.dot.EdgeStmtSubgraph;
import org.eclipse.zest.internal.dot.parser.dot.Subgraph;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Edge Stmt Subgraph</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.zest.internal.dot.parser.dot.impl.EdgeStmtSubgraphImpl#getSubgraph <em>Subgraph</em>}</li>
 *   <li>{@link org.eclipse.zest.internal.dot.parser.dot.impl.EdgeStmtSubgraphImpl#getEdgeRHS <em>Edge RHS</em>}</li>
 *   <li>{@link org.eclipse.zest.internal.dot.parser.dot.impl.EdgeStmtSubgraphImpl#getAttributes <em>Attributes</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class EdgeStmtSubgraphImpl extends StmtImpl implements EdgeStmtSubgraph
{
  /**
   * The cached value of the '{@link #getSubgraph() <em>Subgraph</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSubgraph()
   * @generated
   * @ordered
   */
  protected Subgraph subgraph;

  /**
   * The cached value of the '{@link #getEdgeRHS() <em>Edge RHS</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEdgeRHS()
   * @generated
   * @ordered
   */
  protected EdgeRhs edgeRHS;

  /**
   * The cached value of the '{@link #getAttributes() <em>Attributes</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttributes()
   * @generated
   * @ordered
   */
  protected EList<AttrList> attributes;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected EdgeStmtSubgraphImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return DotPackage.Literals.EDGE_STMT_SUBGRAPH;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Subgraph getSubgraph()
  {
    return subgraph;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetSubgraph(Subgraph newSubgraph, NotificationChain msgs)
  {
    Subgraph oldSubgraph = subgraph;
    subgraph = newSubgraph;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, DotPackage.EDGE_STMT_SUBGRAPH__SUBGRAPH, oldSubgraph, newSubgraph);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setSubgraph(Subgraph newSubgraph)
  {
    if (newSubgraph != subgraph)
    {
      NotificationChain msgs = null;
      if (subgraph != null)
        msgs = ((InternalEObject)subgraph).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - DotPackage.EDGE_STMT_SUBGRAPH__SUBGRAPH, null, msgs);
      if (newSubgraph != null)
        msgs = ((InternalEObject)newSubgraph).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - DotPackage.EDGE_STMT_SUBGRAPH__SUBGRAPH, null, msgs);
      msgs = basicSetSubgraph(newSubgraph, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, DotPackage.EDGE_STMT_SUBGRAPH__SUBGRAPH, newSubgraph, newSubgraph));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EdgeRhs getEdgeRHS()
  {
    return edgeRHS;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetEdgeRHS(EdgeRhs newEdgeRHS, NotificationChain msgs)
  {
    EdgeRhs oldEdgeRHS = edgeRHS;
    edgeRHS = newEdgeRHS;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, DotPackage.EDGE_STMT_SUBGRAPH__EDGE_RHS, oldEdgeRHS, newEdgeRHS);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setEdgeRHS(EdgeRhs newEdgeRHS)
  {
    if (newEdgeRHS != edgeRHS)
    {
      NotificationChain msgs = null;
      if (edgeRHS != null)
        msgs = ((InternalEObject)edgeRHS).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - DotPackage.EDGE_STMT_SUBGRAPH__EDGE_RHS, null, msgs);
      if (newEdgeRHS != null)
        msgs = ((InternalEObject)newEdgeRHS).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - DotPackage.EDGE_STMT_SUBGRAPH__EDGE_RHS, null, msgs);
      msgs = basicSetEdgeRHS(newEdgeRHS, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, DotPackage.EDGE_STMT_SUBGRAPH__EDGE_RHS, newEdgeRHS, newEdgeRHS));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<AttrList> getAttributes()
  {
    if (attributes == null)
    {
      attributes = new EObjectContainmentEList<AttrList>(AttrList.class, this, DotPackage.EDGE_STMT_SUBGRAPH__ATTRIBUTES);
    }
    return attributes;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
      case DotPackage.EDGE_STMT_SUBGRAPH__SUBGRAPH:
        return basicSetSubgraph(null, msgs);
      case DotPackage.EDGE_STMT_SUBGRAPH__EDGE_RHS:
        return basicSetEdgeRHS(null, msgs);
      case DotPackage.EDGE_STMT_SUBGRAPH__ATTRIBUTES:
        return ((InternalEList<?>)getAttributes()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
      case DotPackage.EDGE_STMT_SUBGRAPH__SUBGRAPH:
        return getSubgraph();
      case DotPackage.EDGE_STMT_SUBGRAPH__EDGE_RHS:
        return getEdgeRHS();
      case DotPackage.EDGE_STMT_SUBGRAPH__ATTRIBUTES:
        return getAttributes();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case DotPackage.EDGE_STMT_SUBGRAPH__SUBGRAPH:
        setSubgraph((Subgraph)newValue);
        return;
      case DotPackage.EDGE_STMT_SUBGRAPH__EDGE_RHS:
        setEdgeRHS((EdgeRhs)newValue);
        return;
      case DotPackage.EDGE_STMT_SUBGRAPH__ATTRIBUTES:
        getAttributes().clear();
        getAttributes().addAll((Collection<? extends AttrList>)newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
      case DotPackage.EDGE_STMT_SUBGRAPH__SUBGRAPH:
        setSubgraph((Subgraph)null);
        return;
      case DotPackage.EDGE_STMT_SUBGRAPH__EDGE_RHS:
        setEdgeRHS((EdgeRhs)null);
        return;
      case DotPackage.EDGE_STMT_SUBGRAPH__ATTRIBUTES:
        getAttributes().clear();
        return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
      case DotPackage.EDGE_STMT_SUBGRAPH__SUBGRAPH:
        return subgraph != null;
      case DotPackage.EDGE_STMT_SUBGRAPH__EDGE_RHS:
        return edgeRHS != null;
      case DotPackage.EDGE_STMT_SUBGRAPH__ATTRIBUTES:
        return attributes != null && !attributes.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} //EdgeStmtSubgraphImpl
