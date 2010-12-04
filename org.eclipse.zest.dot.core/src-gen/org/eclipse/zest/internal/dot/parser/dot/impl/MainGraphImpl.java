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
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.zest.internal.dot.parser.dot.DotPackage;
import org.eclipse.zest.internal.dot.parser.dot.GraphType;
import org.eclipse.zest.internal.dot.parser.dot.MainGraph;
import org.eclipse.zest.internal.dot.parser.dot.Stmt;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Main Graph</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.zest.internal.dot.parser.dot.impl.MainGraphImpl#isStrict <em>Strict</em>}</li>
 *   <li>{@link org.eclipse.zest.internal.dot.parser.dot.impl.MainGraphImpl#getType <em>Type</em>}</li>
 *   <li>{@link org.eclipse.zest.internal.dot.parser.dot.impl.MainGraphImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.zest.internal.dot.parser.dot.impl.MainGraphImpl#getStmts <em>Stmts</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class MainGraphImpl extends MinimalEObjectImpl.Container implements MainGraph
{
  /**
   * The default value of the '{@link #isStrict() <em>Strict</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isStrict()
   * @generated
   * @ordered
   */
  protected static final boolean STRICT_EDEFAULT = false;

  /**
   * The cached value of the '{@link #isStrict() <em>Strict</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isStrict()
   * @generated
   * @ordered
   */
  protected boolean strict = STRICT_EDEFAULT;

  /**
   * The default value of the '{@link #getType() <em>Type</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getType()
   * @generated
   * @ordered
   */
  protected static final GraphType TYPE_EDEFAULT = GraphType.GRAPH;

  /**
   * The cached value of the '{@link #getType() <em>Type</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getType()
   * @generated
   * @ordered
   */
  protected GraphType type = TYPE_EDEFAULT;

  /**
   * The default value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected static final String NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected String name = NAME_EDEFAULT;

  /**
   * The cached value of the '{@link #getStmts() <em>Stmts</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getStmts()
   * @generated
   * @ordered
   */
  protected EList<Stmt> stmts;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected MainGraphImpl()
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
    return DotPackage.Literals.MAIN_GRAPH;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean isStrict()
  {
    return strict;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setStrict(boolean newStrict)
  {
    boolean oldStrict = strict;
    strict = newStrict;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, DotPackage.MAIN_GRAPH__STRICT, oldStrict, strict));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public GraphType getType()
  {
    return type;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setType(GraphType newType)
  {
    GraphType oldType = type;
    type = newType == null ? TYPE_EDEFAULT : newType;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, DotPackage.MAIN_GRAPH__TYPE, oldType, type));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getName()
  {
    return name;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setName(String newName)
  {
    String oldName = name;
    name = newName;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, DotPackage.MAIN_GRAPH__NAME, oldName, name));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<Stmt> getStmts()
  {
    if (stmts == null)
    {
      stmts = new EObjectContainmentEList<Stmt>(Stmt.class, this, DotPackage.MAIN_GRAPH__STMTS);
    }
    return stmts;
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
      case DotPackage.MAIN_GRAPH__STMTS:
        return ((InternalEList<?>)getStmts()).basicRemove(otherEnd, msgs);
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
      case DotPackage.MAIN_GRAPH__STRICT:
        return isStrict();
      case DotPackage.MAIN_GRAPH__TYPE:
        return getType();
      case DotPackage.MAIN_GRAPH__NAME:
        return getName();
      case DotPackage.MAIN_GRAPH__STMTS:
        return getStmts();
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
      case DotPackage.MAIN_GRAPH__STRICT:
        setStrict((Boolean)newValue);
        return;
      case DotPackage.MAIN_GRAPH__TYPE:
        setType((GraphType)newValue);
        return;
      case DotPackage.MAIN_GRAPH__NAME:
        setName((String)newValue);
        return;
      case DotPackage.MAIN_GRAPH__STMTS:
        getStmts().clear();
        getStmts().addAll((Collection<? extends Stmt>)newValue);
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
      case DotPackage.MAIN_GRAPH__STRICT:
        setStrict(STRICT_EDEFAULT);
        return;
      case DotPackage.MAIN_GRAPH__TYPE:
        setType(TYPE_EDEFAULT);
        return;
      case DotPackage.MAIN_GRAPH__NAME:
        setName(NAME_EDEFAULT);
        return;
      case DotPackage.MAIN_GRAPH__STMTS:
        getStmts().clear();
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
      case DotPackage.MAIN_GRAPH__STRICT:
        return strict != STRICT_EDEFAULT;
      case DotPackage.MAIN_GRAPH__TYPE:
        return type != TYPE_EDEFAULT;
      case DotPackage.MAIN_GRAPH__NAME:
        return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
      case DotPackage.MAIN_GRAPH__STMTS:
        return stmts != null && !stmts.isEmpty();
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy()) return super.toString();

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (strict: ");
    result.append(strict);
    result.append(", type: ");
    result.append(type);
    result.append(", name: ");
    result.append(name);
    result.append(')');
    return result.toString();
  }

} //MainGraphImpl
