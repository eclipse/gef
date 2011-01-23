/**
 * <copyright>
 * </copyright>
 *
 */
package org.eclipse.zest.internal.dot.parser.dot.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipse.zest.internal.dot.parser.dot.AList;
import org.eclipse.zest.internal.dot.parser.dot.AttrList;
import org.eclipse.zest.internal.dot.parser.dot.AttrStmt;
import org.eclipse.zest.internal.dot.parser.dot.Attribute;
import org.eclipse.zest.internal.dot.parser.dot.AttributeType;
import org.eclipse.zest.internal.dot.parser.dot.CompassPt;
import org.eclipse.zest.internal.dot.parser.dot.DotFactory;
import org.eclipse.zest.internal.dot.parser.dot.DotPackage;
import org.eclipse.zest.internal.dot.parser.dot.EdgeOp;
import org.eclipse.zest.internal.dot.parser.dot.EdgeRhs;
import org.eclipse.zest.internal.dot.parser.dot.EdgeRhsNode;
import org.eclipse.zest.internal.dot.parser.dot.EdgeRhsSubgraph;
import org.eclipse.zest.internal.dot.parser.dot.EdgeStmtNode;
import org.eclipse.zest.internal.dot.parser.dot.EdgeStmtSubgraph;
import org.eclipse.zest.internal.dot.parser.dot.GraphType;
import org.eclipse.zest.internal.dot.parser.dot.GraphvizModel;
import org.eclipse.zest.internal.dot.parser.dot.MainGraph;
import org.eclipse.zest.internal.dot.parser.dot.NodeId;
import org.eclipse.zest.internal.dot.parser.dot.NodeStmt;
import org.eclipse.zest.internal.dot.parser.dot.Stmt;
import org.eclipse.zest.internal.dot.parser.dot.Subgraph;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class DotPackageImpl extends EPackageImpl implements DotPackage
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass graphvizModelEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass mainGraphEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass stmtEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass edgeStmtNodeEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass edgeStmtSubgraphEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass nodeStmtEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass attributeEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass attrStmtEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass attrListEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass aListEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass subgraphEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass edgeRhsEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass edgeRhsNodeEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass edgeRhsSubgraphEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass nodeIdEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EEnum edgeOpEEnum = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EEnum graphTypeEEnum = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EEnum attributeTypeEEnum = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EEnum compassPtEEnum = null;

  /**
   * Creates an instance of the model <b>Package</b>, registered with
   * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
   * package URI value.
   * <p>Note: the correct way to create the package is via the static
   * factory method {@link #init init()}, which also performs
   * initialization of the package, or returns the registered package,
   * if one already exists.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.ecore.EPackage.Registry
   * @see org.eclipse.zest.internal.dot.parser.dot.DotPackage#eNS_URI
   * @see #init()
   * @generated
   */
  private DotPackageImpl()
  {
    super(eNS_URI, DotFactory.eINSTANCE);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private static boolean isInited = false;

  /**
   * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
   * 
   * <p>This method is used to initialize {@link DotPackage#eINSTANCE} when that field is accessed.
   * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static DotPackage init()
  {
    if (isInited) return (DotPackage)EPackage.Registry.INSTANCE.getEPackage(DotPackage.eNS_URI);

    // Obtain or create and register package
    DotPackageImpl theDotPackage = (DotPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof DotPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new DotPackageImpl());

    isInited = true;

    // Create package meta-data objects
    theDotPackage.createPackageContents();

    // Initialize created meta-data
    theDotPackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theDotPackage.freeze();

  
    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(DotPackage.eNS_URI, theDotPackage);
    return theDotPackage;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getGraphvizModel()
  {
    return graphvizModelEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getGraphvizModel_Graphs()
  {
    return (EReference)graphvizModelEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getMainGraph()
  {
    return mainGraphEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getMainGraph_Strict()
  {
    return (EAttribute)mainGraphEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getMainGraph_Type()
  {
    return (EAttribute)mainGraphEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getMainGraph_Name()
  {
    return (EAttribute)mainGraphEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getMainGraph_Stmts()
  {
    return (EReference)mainGraphEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getStmt()
  {
    return stmtEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getEdgeStmtNode()
  {
    return edgeStmtNodeEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getEdgeStmtNode_Node_id()
  {
    return (EReference)edgeStmtNodeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getEdgeStmtNode_EdgeRHS()
  {
    return (EReference)edgeStmtNodeEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getEdgeStmtNode_Attributes()
  {
    return (EReference)edgeStmtNodeEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getEdgeStmtSubgraph()
  {
    return edgeStmtSubgraphEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getEdgeStmtSubgraph_Subgraph()
  {
    return (EReference)edgeStmtSubgraphEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getEdgeStmtSubgraph_EdgeRHS()
  {
    return (EReference)edgeStmtSubgraphEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getEdgeStmtSubgraph_Attributes()
  {
    return (EReference)edgeStmtSubgraphEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getNodeStmt()
  {
    return nodeStmtEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getNodeStmt_Name()
  {
    return (EAttribute)nodeStmtEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getNodeStmt_Attributes()
  {
    return (EReference)nodeStmtEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAttribute()
  {
    return attributeEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getAttribute_Name()
  {
    return (EAttribute)attributeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getAttribute_Value()
  {
    return (EAttribute)attributeEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAttrStmt()
  {
    return attrStmtEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getAttrStmt_Type()
  {
    return (EAttribute)attrStmtEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAttrStmt_Attributes()
  {
    return (EReference)attrStmtEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAttrList()
  {
    return attrListEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAttrList_A_list()
  {
    return (EReference)attrListEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAList()
  {
    return aListEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getAList_Name()
  {
    return (EAttribute)aListEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getAList_Value()
  {
    return (EAttribute)aListEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getSubgraph()
  {
    return subgraphEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getSubgraph_Name()
  {
    return (EAttribute)subgraphEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getSubgraph_Stmts()
  {
    return (EReference)subgraphEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getEdgeRhs()
  {
    return edgeRhsEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getEdgeRhs_Op()
  {
    return (EAttribute)edgeRhsEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getEdgeRhsNode()
  {
    return edgeRhsNodeEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getEdgeRhsNode_Node()
  {
    return (EReference)edgeRhsNodeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getEdgeRhsSubgraph()
  {
    return edgeRhsSubgraphEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getEdgeRhsSubgraph_Subgraph()
  {
    return (EReference)edgeRhsSubgraphEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getNodeId()
  {
    return nodeIdEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getNodeId_Name()
  {
    return (EAttribute)nodeIdEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EEnum getEdgeOp()
  {
    return edgeOpEEnum;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EEnum getGraphType()
  {
    return graphTypeEEnum;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EEnum getAttributeType()
  {
    return attributeTypeEEnum;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EEnum getCompassPt()
  {
    return compassPtEEnum;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DotFactory getDotFactory()
  {
    return (DotFactory)getEFactoryInstance();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private boolean isCreated = false;

  /**
   * Creates the meta-model objects for the package.  This method is
   * guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void createPackageContents()
  {
    if (isCreated) return;
    isCreated = true;

    // Create classes and their features
    graphvizModelEClass = createEClass(GRAPHVIZ_MODEL);
    createEReference(graphvizModelEClass, GRAPHVIZ_MODEL__GRAPHS);

    mainGraphEClass = createEClass(MAIN_GRAPH);
    createEAttribute(mainGraphEClass, MAIN_GRAPH__STRICT);
    createEAttribute(mainGraphEClass, MAIN_GRAPH__TYPE);
    createEAttribute(mainGraphEClass, MAIN_GRAPH__NAME);
    createEReference(mainGraphEClass, MAIN_GRAPH__STMTS);

    stmtEClass = createEClass(STMT);

    edgeStmtNodeEClass = createEClass(EDGE_STMT_NODE);
    createEReference(edgeStmtNodeEClass, EDGE_STMT_NODE__NODE_ID);
    createEReference(edgeStmtNodeEClass, EDGE_STMT_NODE__EDGE_RHS);
    createEReference(edgeStmtNodeEClass, EDGE_STMT_NODE__ATTRIBUTES);

    edgeStmtSubgraphEClass = createEClass(EDGE_STMT_SUBGRAPH);
    createEReference(edgeStmtSubgraphEClass, EDGE_STMT_SUBGRAPH__SUBGRAPH);
    createEReference(edgeStmtSubgraphEClass, EDGE_STMT_SUBGRAPH__EDGE_RHS);
    createEReference(edgeStmtSubgraphEClass, EDGE_STMT_SUBGRAPH__ATTRIBUTES);

    nodeStmtEClass = createEClass(NODE_STMT);
    createEAttribute(nodeStmtEClass, NODE_STMT__NAME);
    createEReference(nodeStmtEClass, NODE_STMT__ATTRIBUTES);

    attributeEClass = createEClass(ATTRIBUTE);
    createEAttribute(attributeEClass, ATTRIBUTE__NAME);
    createEAttribute(attributeEClass, ATTRIBUTE__VALUE);

    attrStmtEClass = createEClass(ATTR_STMT);
    createEAttribute(attrStmtEClass, ATTR_STMT__TYPE);
    createEReference(attrStmtEClass, ATTR_STMT__ATTRIBUTES);

    attrListEClass = createEClass(ATTR_LIST);
    createEReference(attrListEClass, ATTR_LIST__ALIST);

    aListEClass = createEClass(ALIST);
    createEAttribute(aListEClass, ALIST__NAME);
    createEAttribute(aListEClass, ALIST__VALUE);

    subgraphEClass = createEClass(SUBGRAPH);
    createEAttribute(subgraphEClass, SUBGRAPH__NAME);
    createEReference(subgraphEClass, SUBGRAPH__STMTS);

    edgeRhsEClass = createEClass(EDGE_RHS);
    createEAttribute(edgeRhsEClass, EDGE_RHS__OP);

    edgeRhsNodeEClass = createEClass(EDGE_RHS_NODE);
    createEReference(edgeRhsNodeEClass, EDGE_RHS_NODE__NODE);

    edgeRhsSubgraphEClass = createEClass(EDGE_RHS_SUBGRAPH);
    createEReference(edgeRhsSubgraphEClass, EDGE_RHS_SUBGRAPH__SUBGRAPH);

    nodeIdEClass = createEClass(NODE_ID);
    createEAttribute(nodeIdEClass, NODE_ID__NAME);

    // Create enums
    edgeOpEEnum = createEEnum(EDGE_OP);
    graphTypeEEnum = createEEnum(GRAPH_TYPE);
    attributeTypeEEnum = createEEnum(ATTRIBUTE_TYPE);
    compassPtEEnum = createEEnum(COMPASS_PT);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private boolean isInitialized = false;

  /**
   * Complete the initialization of the package and its meta-model.  This
   * method is guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void initializePackageContents()
  {
    if (isInitialized) return;
    isInitialized = true;

    // Initialize package
    setName(eNAME);
    setNsPrefix(eNS_PREFIX);
    setNsURI(eNS_URI);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes
    edgeStmtNodeEClass.getESuperTypes().add(this.getStmt());
    edgeStmtSubgraphEClass.getESuperTypes().add(this.getStmt());
    nodeStmtEClass.getESuperTypes().add(this.getStmt());
    attributeEClass.getESuperTypes().add(this.getStmt());
    attrStmtEClass.getESuperTypes().add(this.getStmt());
    subgraphEClass.getESuperTypes().add(this.getStmt());
    edgeRhsNodeEClass.getESuperTypes().add(this.getEdgeRhs());
    edgeRhsSubgraphEClass.getESuperTypes().add(this.getEdgeRhs());

    // Initialize classes and features; add operations and parameters
    initEClass(graphvizModelEClass, GraphvizModel.class, "GraphvizModel", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getGraphvizModel_Graphs(), this.getMainGraph(), null, "graphs", null, 0, -1, GraphvizModel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(mainGraphEClass, MainGraph.class, "MainGraph", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getMainGraph_Strict(), ecorePackage.getEBoolean(), "strict", null, 0, 1, MainGraph.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getMainGraph_Type(), this.getGraphType(), "type", null, 0, 1, MainGraph.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getMainGraph_Name(), ecorePackage.getEString(), "name", null, 0, 1, MainGraph.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getMainGraph_Stmts(), this.getStmt(), null, "stmts", null, 0, -1, MainGraph.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(stmtEClass, Stmt.class, "Stmt", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(edgeStmtNodeEClass, EdgeStmtNode.class, "EdgeStmtNode", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getEdgeStmtNode_Node_id(), this.getNodeId(), null, "node_id", null, 0, 1, EdgeStmtNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getEdgeStmtNode_EdgeRHS(), this.getEdgeRhs(), null, "edgeRHS", null, 0, -1, EdgeStmtNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getEdgeStmtNode_Attributes(), this.getAttrList(), null, "attributes", null, 0, -1, EdgeStmtNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(edgeStmtSubgraphEClass, EdgeStmtSubgraph.class, "EdgeStmtSubgraph", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getEdgeStmtSubgraph_Subgraph(), this.getSubgraph(), null, "subgraph", null, 0, 1, EdgeStmtSubgraph.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getEdgeStmtSubgraph_EdgeRHS(), this.getEdgeRhs(), null, "edgeRHS", null, 0, 1, EdgeStmtSubgraph.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getEdgeStmtSubgraph_Attributes(), this.getAttrList(), null, "attributes", null, 0, -1, EdgeStmtSubgraph.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(nodeStmtEClass, NodeStmt.class, "NodeStmt", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getNodeStmt_Name(), ecorePackage.getEString(), "name", null, 0, 1, NodeStmt.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getNodeStmt_Attributes(), this.getAttrList(), null, "attributes", null, 0, -1, NodeStmt.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(attributeEClass, Attribute.class, "Attribute", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getAttribute_Name(), ecorePackage.getEString(), "name", null, 0, 1, Attribute.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getAttribute_Value(), ecorePackage.getEString(), "value", null, 0, 1, Attribute.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(attrStmtEClass, AttrStmt.class, "AttrStmt", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getAttrStmt_Type(), this.getAttributeType(), "type", null, 0, 1, AttrStmt.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getAttrStmt_Attributes(), this.getAttrList(), null, "attributes", null, 0, -1, AttrStmt.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(attrListEClass, AttrList.class, "AttrList", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getAttrList_A_list(), this.getAList(), null, "a_list", null, 0, -1, AttrList.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(aListEClass, AList.class, "AList", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getAList_Name(), ecorePackage.getEString(), "name", null, 0, 1, AList.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getAList_Value(), ecorePackage.getEString(), "value", null, 0, 1, AList.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(subgraphEClass, Subgraph.class, "Subgraph", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getSubgraph_Name(), ecorePackage.getEString(), "name", null, 0, 1, Subgraph.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getSubgraph_Stmts(), this.getStmt(), null, "stmts", null, 0, -1, Subgraph.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(edgeRhsEClass, EdgeRhs.class, "EdgeRhs", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getEdgeRhs_Op(), this.getEdgeOp(), "op", null, 0, 1, EdgeRhs.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(edgeRhsNodeEClass, EdgeRhsNode.class, "EdgeRhsNode", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getEdgeRhsNode_Node(), this.getNodeId(), null, "node", null, 0, 1, EdgeRhsNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(edgeRhsSubgraphEClass, EdgeRhsSubgraph.class, "EdgeRhsSubgraph", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getEdgeRhsSubgraph_Subgraph(), this.getSubgraph(), null, "subgraph", null, 0, 1, EdgeRhsSubgraph.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(nodeIdEClass, NodeId.class, "NodeId", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getNodeId_Name(), ecorePackage.getEString(), "name", null, 0, 1, NodeId.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    // Initialize enums and add enum literals
    initEEnum(edgeOpEEnum, EdgeOp.class, "EdgeOp");
    addEEnumLiteral(edgeOpEEnum, EdgeOp.DIRECTED);
    addEEnumLiteral(edgeOpEEnum, EdgeOp.UNDIRECTED);

    initEEnum(graphTypeEEnum, GraphType.class, "GraphType");
    addEEnumLiteral(graphTypeEEnum, GraphType.GRAPH);
    addEEnumLiteral(graphTypeEEnum, GraphType.DIGRAPH);

    initEEnum(attributeTypeEEnum, AttributeType.class, "AttributeType");
    addEEnumLiteral(attributeTypeEEnum, AttributeType.GRAPH);
    addEEnumLiteral(attributeTypeEEnum, AttributeType.NODE);
    addEEnumLiteral(attributeTypeEEnum, AttributeType.EDGE);

    initEEnum(compassPtEEnum, CompassPt.class, "CompassPt");
    addEEnumLiteral(compassPtEEnum, CompassPt.NORTH);
    addEEnumLiteral(compassPtEEnum, CompassPt.NORTHEAST);
    addEEnumLiteral(compassPtEEnum, CompassPt.EAST);
    addEEnumLiteral(compassPtEEnum, CompassPt.SOUTHEAST);
    addEEnumLiteral(compassPtEEnum, CompassPt.SOUTH);
    addEEnumLiteral(compassPtEEnum, CompassPt.SOUTHWEST);
    addEEnumLiteral(compassPtEEnum, CompassPt.WEST);
    addEEnumLiteral(compassPtEEnum, CompassPt.NORTHWEST);

    // Create resource
    createResource(eNS_URI);
  }

} //DotPackageImpl
