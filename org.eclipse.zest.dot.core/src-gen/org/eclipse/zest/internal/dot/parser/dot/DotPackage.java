/**
 * <copyright>
 * </copyright>
 *
 */
package org.eclipse.zest.internal.dot.parser.dot;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eclipse.zest.internal.dot.parser.dot.DotFactory
 * @model kind="package"
 * @generated
 */
public interface DotPackage extends EPackage
{
  /**
   * The package name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNAME = "dot";

  /**
   * The package namespace URI.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_URI = "http://www.eclipse.org/zest/internal/dot/parser/Dot";

  /**
   * The package namespace name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_PREFIX = "dot";

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  DotPackage eINSTANCE = org.eclipse.zest.internal.dot.parser.dot.impl.DotPackageImpl.init();

  /**
   * The meta object id for the '{@link org.eclipse.zest.internal.dot.parser.dot.impl.GraphvizModelImpl <em>Graphviz Model</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.zest.internal.dot.parser.dot.impl.GraphvizModelImpl
   * @see org.eclipse.zest.internal.dot.parser.dot.impl.DotPackageImpl#getGraphvizModel()
   * @generated
   */
  int GRAPHVIZ_MODEL = 0;

  /**
   * The feature id for the '<em><b>Graphs</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GRAPHVIZ_MODEL__GRAPHS = 0;

  /**
   * The number of structural features of the '<em>Graphviz Model</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GRAPHVIZ_MODEL_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.zest.internal.dot.parser.dot.impl.MainGraphImpl <em>Main Graph</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.zest.internal.dot.parser.dot.impl.MainGraphImpl
   * @see org.eclipse.zest.internal.dot.parser.dot.impl.DotPackageImpl#getMainGraph()
   * @generated
   */
  int MAIN_GRAPH = 1;

  /**
   * The feature id for the '<em><b>Strict</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MAIN_GRAPH__STRICT = 0;

  /**
   * The feature id for the '<em><b>Type</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MAIN_GRAPH__TYPE = 1;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MAIN_GRAPH__NAME = 2;

  /**
   * The feature id for the '<em><b>Stmts</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MAIN_GRAPH__STMTS = 3;

  /**
   * The number of structural features of the '<em>Main Graph</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MAIN_GRAPH_FEATURE_COUNT = 4;

  /**
   * The meta object id for the '{@link org.eclipse.zest.internal.dot.parser.dot.impl.StmtImpl <em>Stmt</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.zest.internal.dot.parser.dot.impl.StmtImpl
   * @see org.eclipse.zest.internal.dot.parser.dot.impl.DotPackageImpl#getStmt()
   * @generated
   */
  int STMT = 2;

  /**
   * The number of structural features of the '<em>Stmt</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STMT_FEATURE_COUNT = 0;

  /**
   * The meta object id for the '{@link org.eclipse.zest.internal.dot.parser.dot.impl.EdgeStmtNodeImpl <em>Edge Stmt Node</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.zest.internal.dot.parser.dot.impl.EdgeStmtNodeImpl
   * @see org.eclipse.zest.internal.dot.parser.dot.impl.DotPackageImpl#getEdgeStmtNode()
   * @generated
   */
  int EDGE_STMT_NODE = 3;

  /**
   * The feature id for the '<em><b>Node id</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EDGE_STMT_NODE__NODE_ID = STMT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Edge RHS</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EDGE_STMT_NODE__EDGE_RHS = STMT_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Attributes</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EDGE_STMT_NODE__ATTRIBUTES = STMT_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>Edge Stmt Node</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EDGE_STMT_NODE_FEATURE_COUNT = STMT_FEATURE_COUNT + 3;

  /**
   * The meta object id for the '{@link org.eclipse.zest.internal.dot.parser.dot.impl.EdgeStmtSubgraphImpl <em>Edge Stmt Subgraph</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.zest.internal.dot.parser.dot.impl.EdgeStmtSubgraphImpl
   * @see org.eclipse.zest.internal.dot.parser.dot.impl.DotPackageImpl#getEdgeStmtSubgraph()
   * @generated
   */
  int EDGE_STMT_SUBGRAPH = 4;

  /**
   * The feature id for the '<em><b>Subgraph</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EDGE_STMT_SUBGRAPH__SUBGRAPH = STMT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Edge RHS</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EDGE_STMT_SUBGRAPH__EDGE_RHS = STMT_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Attributes</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EDGE_STMT_SUBGRAPH__ATTRIBUTES = STMT_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>Edge Stmt Subgraph</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EDGE_STMT_SUBGRAPH_FEATURE_COUNT = STMT_FEATURE_COUNT + 3;

  /**
   * The meta object id for the '{@link org.eclipse.zest.internal.dot.parser.dot.impl.NodeStmtImpl <em>Node Stmt</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.zest.internal.dot.parser.dot.impl.NodeStmtImpl
   * @see org.eclipse.zest.internal.dot.parser.dot.impl.DotPackageImpl#getNodeStmt()
   * @generated
   */
  int NODE_STMT = 5;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NODE_STMT__NAME = STMT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Attributes</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NODE_STMT__ATTRIBUTES = STMT_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Node Stmt</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NODE_STMT_FEATURE_COUNT = STMT_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link org.eclipse.zest.internal.dot.parser.dot.impl.AttributeImpl <em>Attribute</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.zest.internal.dot.parser.dot.impl.AttributeImpl
   * @see org.eclipse.zest.internal.dot.parser.dot.impl.DotPackageImpl#getAttribute()
   * @generated
   */
  int ATTRIBUTE = 6;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE__NAME = STMT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE__VALUE = STMT_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Attribute</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_FEATURE_COUNT = STMT_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link org.eclipse.zest.internal.dot.parser.dot.impl.AttrStmtImpl <em>Attr Stmt</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.zest.internal.dot.parser.dot.impl.AttrStmtImpl
   * @see org.eclipse.zest.internal.dot.parser.dot.impl.DotPackageImpl#getAttrStmt()
   * @generated
   */
  int ATTR_STMT = 7;

  /**
   * The feature id for the '<em><b>Type</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTR_STMT__TYPE = STMT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Attributes</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTR_STMT__ATTRIBUTES = STMT_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Attr Stmt</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTR_STMT_FEATURE_COUNT = STMT_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link org.eclipse.zest.internal.dot.parser.dot.impl.AttrListImpl <em>Attr List</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.zest.internal.dot.parser.dot.impl.AttrListImpl
   * @see org.eclipse.zest.internal.dot.parser.dot.impl.DotPackageImpl#getAttrList()
   * @generated
   */
  int ATTR_LIST = 8;

  /**
   * The feature id for the '<em><b>Alist</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTR_LIST__ALIST = 0;

  /**
   * The number of structural features of the '<em>Attr List</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTR_LIST_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.zest.internal.dot.parser.dot.impl.AListImpl <em>AList</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.zest.internal.dot.parser.dot.impl.AListImpl
   * @see org.eclipse.zest.internal.dot.parser.dot.impl.DotPackageImpl#getAList()
   * @generated
   */
  int ALIST = 9;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ALIST__NAME = 0;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ALIST__VALUE = 1;

  /**
   * The number of structural features of the '<em>AList</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ALIST_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.zest.internal.dot.parser.dot.impl.SubgraphImpl <em>Subgraph</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.zest.internal.dot.parser.dot.impl.SubgraphImpl
   * @see org.eclipse.zest.internal.dot.parser.dot.impl.DotPackageImpl#getSubgraph()
   * @generated
   */
  int SUBGRAPH = 10;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SUBGRAPH__NAME = STMT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Stmts</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SUBGRAPH__STMTS = STMT_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Subgraph</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SUBGRAPH_FEATURE_COUNT = STMT_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link org.eclipse.zest.internal.dot.parser.dot.impl.EdgeRhsImpl <em>Edge Rhs</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.zest.internal.dot.parser.dot.impl.EdgeRhsImpl
   * @see org.eclipse.zest.internal.dot.parser.dot.impl.DotPackageImpl#getEdgeRhs()
   * @generated
   */
  int EDGE_RHS = 11;

  /**
   * The feature id for the '<em><b>Op</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EDGE_RHS__OP = 0;

  /**
   * The number of structural features of the '<em>Edge Rhs</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EDGE_RHS_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.zest.internal.dot.parser.dot.impl.EdgeRhsNodeImpl <em>Edge Rhs Node</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.zest.internal.dot.parser.dot.impl.EdgeRhsNodeImpl
   * @see org.eclipse.zest.internal.dot.parser.dot.impl.DotPackageImpl#getEdgeRhsNode()
   * @generated
   */
  int EDGE_RHS_NODE = 12;

  /**
   * The feature id for the '<em><b>Op</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EDGE_RHS_NODE__OP = EDGE_RHS__OP;

  /**
   * The feature id for the '<em><b>Node</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EDGE_RHS_NODE__NODE = EDGE_RHS_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Edge Rhs Node</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EDGE_RHS_NODE_FEATURE_COUNT = EDGE_RHS_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.zest.internal.dot.parser.dot.impl.EdgeRhsSubgraphImpl <em>Edge Rhs Subgraph</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.zest.internal.dot.parser.dot.impl.EdgeRhsSubgraphImpl
   * @see org.eclipse.zest.internal.dot.parser.dot.impl.DotPackageImpl#getEdgeRhsSubgraph()
   * @generated
   */
  int EDGE_RHS_SUBGRAPH = 13;

  /**
   * The feature id for the '<em><b>Op</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EDGE_RHS_SUBGRAPH__OP = EDGE_RHS__OP;

  /**
   * The feature id for the '<em><b>Subgraph</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EDGE_RHS_SUBGRAPH__SUBGRAPH = EDGE_RHS_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Edge Rhs Subgraph</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EDGE_RHS_SUBGRAPH_FEATURE_COUNT = EDGE_RHS_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.zest.internal.dot.parser.dot.impl.NodeIdImpl <em>Node Id</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.zest.internal.dot.parser.dot.impl.NodeIdImpl
   * @see org.eclipse.zest.internal.dot.parser.dot.impl.DotPackageImpl#getNodeId()
   * @generated
   */
  int NODE_ID = 14;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NODE_ID__NAME = 0;

  /**
   * The number of structural features of the '<em>Node Id</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NODE_ID_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.zest.internal.dot.parser.dot.EdgeOp <em>Edge Op</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.zest.internal.dot.parser.dot.EdgeOp
   * @see org.eclipse.zest.internal.dot.parser.dot.impl.DotPackageImpl#getEdgeOp()
   * @generated
   */
  int EDGE_OP = 15;

  /**
   * The meta object id for the '{@link org.eclipse.zest.internal.dot.parser.dot.GraphType <em>Graph Type</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.zest.internal.dot.parser.dot.GraphType
   * @see org.eclipse.zest.internal.dot.parser.dot.impl.DotPackageImpl#getGraphType()
   * @generated
   */
  int GRAPH_TYPE = 16;

  /**
   * The meta object id for the '{@link org.eclipse.zest.internal.dot.parser.dot.AttributeType <em>Attribute Type</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.zest.internal.dot.parser.dot.AttributeType
   * @see org.eclipse.zest.internal.dot.parser.dot.impl.DotPackageImpl#getAttributeType()
   * @generated
   */
  int ATTRIBUTE_TYPE = 17;

  /**
   * The meta object id for the '{@link org.eclipse.zest.internal.dot.parser.dot.CompassPt <em>Compass Pt</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.zest.internal.dot.parser.dot.CompassPt
   * @see org.eclipse.zest.internal.dot.parser.dot.impl.DotPackageImpl#getCompassPt()
   * @generated
   */
  int COMPASS_PT = 18;


  /**
   * Returns the meta object for class '{@link org.eclipse.zest.internal.dot.parser.dot.GraphvizModel <em>Graphviz Model</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Graphviz Model</em>'.
   * @see org.eclipse.zest.internal.dot.parser.dot.GraphvizModel
   * @generated
   */
  EClass getGraphvizModel();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.zest.internal.dot.parser.dot.GraphvizModel#getGraphs <em>Graphs</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Graphs</em>'.
   * @see org.eclipse.zest.internal.dot.parser.dot.GraphvizModel#getGraphs()
   * @see #getGraphvizModel()
   * @generated
   */
  EReference getGraphvizModel_Graphs();

  /**
   * Returns the meta object for class '{@link org.eclipse.zest.internal.dot.parser.dot.MainGraph <em>Main Graph</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Main Graph</em>'.
   * @see org.eclipse.zest.internal.dot.parser.dot.MainGraph
   * @generated
   */
  EClass getMainGraph();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.zest.internal.dot.parser.dot.MainGraph#isStrict <em>Strict</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Strict</em>'.
   * @see org.eclipse.zest.internal.dot.parser.dot.MainGraph#isStrict()
   * @see #getMainGraph()
   * @generated
   */
  EAttribute getMainGraph_Strict();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.zest.internal.dot.parser.dot.MainGraph#getType <em>Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Type</em>'.
   * @see org.eclipse.zest.internal.dot.parser.dot.MainGraph#getType()
   * @see #getMainGraph()
   * @generated
   */
  EAttribute getMainGraph_Type();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.zest.internal.dot.parser.dot.MainGraph#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.zest.internal.dot.parser.dot.MainGraph#getName()
   * @see #getMainGraph()
   * @generated
   */
  EAttribute getMainGraph_Name();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.zest.internal.dot.parser.dot.MainGraph#getStmts <em>Stmts</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Stmts</em>'.
   * @see org.eclipse.zest.internal.dot.parser.dot.MainGraph#getStmts()
   * @see #getMainGraph()
   * @generated
   */
  EReference getMainGraph_Stmts();

  /**
   * Returns the meta object for class '{@link org.eclipse.zest.internal.dot.parser.dot.Stmt <em>Stmt</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Stmt</em>'.
   * @see org.eclipse.zest.internal.dot.parser.dot.Stmt
   * @generated
   */
  EClass getStmt();

  /**
   * Returns the meta object for class '{@link org.eclipse.zest.internal.dot.parser.dot.EdgeStmtNode <em>Edge Stmt Node</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Edge Stmt Node</em>'.
   * @see org.eclipse.zest.internal.dot.parser.dot.EdgeStmtNode
   * @generated
   */
  EClass getEdgeStmtNode();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.zest.internal.dot.parser.dot.EdgeStmtNode#getNode_id <em>Node id</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Node id</em>'.
   * @see org.eclipse.zest.internal.dot.parser.dot.EdgeStmtNode#getNode_id()
   * @see #getEdgeStmtNode()
   * @generated
   */
  EReference getEdgeStmtNode_Node_id();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.zest.internal.dot.parser.dot.EdgeStmtNode#getEdgeRHS <em>Edge RHS</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Edge RHS</em>'.
   * @see org.eclipse.zest.internal.dot.parser.dot.EdgeStmtNode#getEdgeRHS()
   * @see #getEdgeStmtNode()
   * @generated
   */
  EReference getEdgeStmtNode_EdgeRHS();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.zest.internal.dot.parser.dot.EdgeStmtNode#getAttributes <em>Attributes</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Attributes</em>'.
   * @see org.eclipse.zest.internal.dot.parser.dot.EdgeStmtNode#getAttributes()
   * @see #getEdgeStmtNode()
   * @generated
   */
  EReference getEdgeStmtNode_Attributes();

  /**
   * Returns the meta object for class '{@link org.eclipse.zest.internal.dot.parser.dot.EdgeStmtSubgraph <em>Edge Stmt Subgraph</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Edge Stmt Subgraph</em>'.
   * @see org.eclipse.zest.internal.dot.parser.dot.EdgeStmtSubgraph
   * @generated
   */
  EClass getEdgeStmtSubgraph();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.zest.internal.dot.parser.dot.EdgeStmtSubgraph#getSubgraph <em>Subgraph</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Subgraph</em>'.
   * @see org.eclipse.zest.internal.dot.parser.dot.EdgeStmtSubgraph#getSubgraph()
   * @see #getEdgeStmtSubgraph()
   * @generated
   */
  EReference getEdgeStmtSubgraph_Subgraph();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.zest.internal.dot.parser.dot.EdgeStmtSubgraph#getEdgeRHS <em>Edge RHS</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Edge RHS</em>'.
   * @see org.eclipse.zest.internal.dot.parser.dot.EdgeStmtSubgraph#getEdgeRHS()
   * @see #getEdgeStmtSubgraph()
   * @generated
   */
  EReference getEdgeStmtSubgraph_EdgeRHS();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.zest.internal.dot.parser.dot.EdgeStmtSubgraph#getAttributes <em>Attributes</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Attributes</em>'.
   * @see org.eclipse.zest.internal.dot.parser.dot.EdgeStmtSubgraph#getAttributes()
   * @see #getEdgeStmtSubgraph()
   * @generated
   */
  EReference getEdgeStmtSubgraph_Attributes();

  /**
   * Returns the meta object for class '{@link org.eclipse.zest.internal.dot.parser.dot.NodeStmt <em>Node Stmt</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Node Stmt</em>'.
   * @see org.eclipse.zest.internal.dot.parser.dot.NodeStmt
   * @generated
   */
  EClass getNodeStmt();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.zest.internal.dot.parser.dot.NodeStmt#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.zest.internal.dot.parser.dot.NodeStmt#getName()
   * @see #getNodeStmt()
   * @generated
   */
  EAttribute getNodeStmt_Name();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.zest.internal.dot.parser.dot.NodeStmt#getAttributes <em>Attributes</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Attributes</em>'.
   * @see org.eclipse.zest.internal.dot.parser.dot.NodeStmt#getAttributes()
   * @see #getNodeStmt()
   * @generated
   */
  EReference getNodeStmt_Attributes();

  /**
   * Returns the meta object for class '{@link org.eclipse.zest.internal.dot.parser.dot.Attribute <em>Attribute</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Attribute</em>'.
   * @see org.eclipse.zest.internal.dot.parser.dot.Attribute
   * @generated
   */
  EClass getAttribute();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.zest.internal.dot.parser.dot.Attribute#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.zest.internal.dot.parser.dot.Attribute#getName()
   * @see #getAttribute()
   * @generated
   */
  EAttribute getAttribute_Name();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.zest.internal.dot.parser.dot.Attribute#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see org.eclipse.zest.internal.dot.parser.dot.Attribute#getValue()
   * @see #getAttribute()
   * @generated
   */
  EAttribute getAttribute_Value();

  /**
   * Returns the meta object for class '{@link org.eclipse.zest.internal.dot.parser.dot.AttrStmt <em>Attr Stmt</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Attr Stmt</em>'.
   * @see org.eclipse.zest.internal.dot.parser.dot.AttrStmt
   * @generated
   */
  EClass getAttrStmt();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.zest.internal.dot.parser.dot.AttrStmt#getType <em>Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Type</em>'.
   * @see org.eclipse.zest.internal.dot.parser.dot.AttrStmt#getType()
   * @see #getAttrStmt()
   * @generated
   */
  EAttribute getAttrStmt_Type();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.zest.internal.dot.parser.dot.AttrStmt#getAttributes <em>Attributes</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Attributes</em>'.
   * @see org.eclipse.zest.internal.dot.parser.dot.AttrStmt#getAttributes()
   * @see #getAttrStmt()
   * @generated
   */
  EReference getAttrStmt_Attributes();

  /**
   * Returns the meta object for class '{@link org.eclipse.zest.internal.dot.parser.dot.AttrList <em>Attr List</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Attr List</em>'.
   * @see org.eclipse.zest.internal.dot.parser.dot.AttrList
   * @generated
   */
  EClass getAttrList();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.zest.internal.dot.parser.dot.AttrList#getA_list <em>Alist</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Alist</em>'.
   * @see org.eclipse.zest.internal.dot.parser.dot.AttrList#getA_list()
   * @see #getAttrList()
   * @generated
   */
  EReference getAttrList_A_list();

  /**
   * Returns the meta object for class '{@link org.eclipse.zest.internal.dot.parser.dot.AList <em>AList</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>AList</em>'.
   * @see org.eclipse.zest.internal.dot.parser.dot.AList
   * @generated
   */
  EClass getAList();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.zest.internal.dot.parser.dot.AList#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.zest.internal.dot.parser.dot.AList#getName()
   * @see #getAList()
   * @generated
   */
  EAttribute getAList_Name();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.zest.internal.dot.parser.dot.AList#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see org.eclipse.zest.internal.dot.parser.dot.AList#getValue()
   * @see #getAList()
   * @generated
   */
  EAttribute getAList_Value();

  /**
   * Returns the meta object for class '{@link org.eclipse.zest.internal.dot.parser.dot.Subgraph <em>Subgraph</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Subgraph</em>'.
   * @see org.eclipse.zest.internal.dot.parser.dot.Subgraph
   * @generated
   */
  EClass getSubgraph();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.zest.internal.dot.parser.dot.Subgraph#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.zest.internal.dot.parser.dot.Subgraph#getName()
   * @see #getSubgraph()
   * @generated
   */
  EAttribute getSubgraph_Name();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.zest.internal.dot.parser.dot.Subgraph#getStmts <em>Stmts</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Stmts</em>'.
   * @see org.eclipse.zest.internal.dot.parser.dot.Subgraph#getStmts()
   * @see #getSubgraph()
   * @generated
   */
  EReference getSubgraph_Stmts();

  /**
   * Returns the meta object for class '{@link org.eclipse.zest.internal.dot.parser.dot.EdgeRhs <em>Edge Rhs</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Edge Rhs</em>'.
   * @see org.eclipse.zest.internal.dot.parser.dot.EdgeRhs
   * @generated
   */
  EClass getEdgeRhs();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.zest.internal.dot.parser.dot.EdgeRhs#getOp <em>Op</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Op</em>'.
   * @see org.eclipse.zest.internal.dot.parser.dot.EdgeRhs#getOp()
   * @see #getEdgeRhs()
   * @generated
   */
  EAttribute getEdgeRhs_Op();

  /**
   * Returns the meta object for class '{@link org.eclipse.zest.internal.dot.parser.dot.EdgeRhsNode <em>Edge Rhs Node</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Edge Rhs Node</em>'.
   * @see org.eclipse.zest.internal.dot.parser.dot.EdgeRhsNode
   * @generated
   */
  EClass getEdgeRhsNode();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.zest.internal.dot.parser.dot.EdgeRhsNode#getNode <em>Node</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Node</em>'.
   * @see org.eclipse.zest.internal.dot.parser.dot.EdgeRhsNode#getNode()
   * @see #getEdgeRhsNode()
   * @generated
   */
  EReference getEdgeRhsNode_Node();

  /**
   * Returns the meta object for class '{@link org.eclipse.zest.internal.dot.parser.dot.EdgeRhsSubgraph <em>Edge Rhs Subgraph</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Edge Rhs Subgraph</em>'.
   * @see org.eclipse.zest.internal.dot.parser.dot.EdgeRhsSubgraph
   * @generated
   */
  EClass getEdgeRhsSubgraph();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.zest.internal.dot.parser.dot.EdgeRhsSubgraph#getSubgraph <em>Subgraph</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Subgraph</em>'.
   * @see org.eclipse.zest.internal.dot.parser.dot.EdgeRhsSubgraph#getSubgraph()
   * @see #getEdgeRhsSubgraph()
   * @generated
   */
  EReference getEdgeRhsSubgraph_Subgraph();

  /**
   * Returns the meta object for class '{@link org.eclipse.zest.internal.dot.parser.dot.NodeId <em>Node Id</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Node Id</em>'.
   * @see org.eclipse.zest.internal.dot.parser.dot.NodeId
   * @generated
   */
  EClass getNodeId();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.zest.internal.dot.parser.dot.NodeId#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.zest.internal.dot.parser.dot.NodeId#getName()
   * @see #getNodeId()
   * @generated
   */
  EAttribute getNodeId_Name();

  /**
   * Returns the meta object for enum '{@link org.eclipse.zest.internal.dot.parser.dot.EdgeOp <em>Edge Op</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>Edge Op</em>'.
   * @see org.eclipse.zest.internal.dot.parser.dot.EdgeOp
   * @generated
   */
  EEnum getEdgeOp();

  /**
   * Returns the meta object for enum '{@link org.eclipse.zest.internal.dot.parser.dot.GraphType <em>Graph Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>Graph Type</em>'.
   * @see org.eclipse.zest.internal.dot.parser.dot.GraphType
   * @generated
   */
  EEnum getGraphType();

  /**
   * Returns the meta object for enum '{@link org.eclipse.zest.internal.dot.parser.dot.AttributeType <em>Attribute Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>Attribute Type</em>'.
   * @see org.eclipse.zest.internal.dot.parser.dot.AttributeType
   * @generated
   */
  EEnum getAttributeType();

  /**
   * Returns the meta object for enum '{@link org.eclipse.zest.internal.dot.parser.dot.CompassPt <em>Compass Pt</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>Compass Pt</em>'.
   * @see org.eclipse.zest.internal.dot.parser.dot.CompassPt
   * @generated
   */
  EEnum getCompassPt();

  /**
   * Returns the factory that creates the instances of the model.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the factory that creates the instances of the model.
   * @generated
   */
  DotFactory getDotFactory();

  /**
   * <!-- begin-user-doc -->
   * Defines literals for the meta objects that represent
   * <ul>
   *   <li>each class,</li>
   *   <li>each feature of each class,</li>
   *   <li>each enum,</li>
   *   <li>and each data type</li>
   * </ul>
   * <!-- end-user-doc -->
   * @generated
   */
  interface Literals
  {
    /**
     * The meta object literal for the '{@link org.eclipse.zest.internal.dot.parser.dot.impl.GraphvizModelImpl <em>Graphviz Model</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.zest.internal.dot.parser.dot.impl.GraphvizModelImpl
     * @see org.eclipse.zest.internal.dot.parser.dot.impl.DotPackageImpl#getGraphvizModel()
     * @generated
     */
    EClass GRAPHVIZ_MODEL = eINSTANCE.getGraphvizModel();

    /**
     * The meta object literal for the '<em><b>Graphs</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference GRAPHVIZ_MODEL__GRAPHS = eINSTANCE.getGraphvizModel_Graphs();

    /**
     * The meta object literal for the '{@link org.eclipse.zest.internal.dot.parser.dot.impl.MainGraphImpl <em>Main Graph</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.zest.internal.dot.parser.dot.impl.MainGraphImpl
     * @see org.eclipse.zest.internal.dot.parser.dot.impl.DotPackageImpl#getMainGraph()
     * @generated
     */
    EClass MAIN_GRAPH = eINSTANCE.getMainGraph();

    /**
     * The meta object literal for the '<em><b>Strict</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MAIN_GRAPH__STRICT = eINSTANCE.getMainGraph_Strict();

    /**
     * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MAIN_GRAPH__TYPE = eINSTANCE.getMainGraph_Type();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MAIN_GRAPH__NAME = eINSTANCE.getMainGraph_Name();

    /**
     * The meta object literal for the '<em><b>Stmts</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MAIN_GRAPH__STMTS = eINSTANCE.getMainGraph_Stmts();

    /**
     * The meta object literal for the '{@link org.eclipse.zest.internal.dot.parser.dot.impl.StmtImpl <em>Stmt</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.zest.internal.dot.parser.dot.impl.StmtImpl
     * @see org.eclipse.zest.internal.dot.parser.dot.impl.DotPackageImpl#getStmt()
     * @generated
     */
    EClass STMT = eINSTANCE.getStmt();

    /**
     * The meta object literal for the '{@link org.eclipse.zest.internal.dot.parser.dot.impl.EdgeStmtNodeImpl <em>Edge Stmt Node</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.zest.internal.dot.parser.dot.impl.EdgeStmtNodeImpl
     * @see org.eclipse.zest.internal.dot.parser.dot.impl.DotPackageImpl#getEdgeStmtNode()
     * @generated
     */
    EClass EDGE_STMT_NODE = eINSTANCE.getEdgeStmtNode();

    /**
     * The meta object literal for the '<em><b>Node id</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference EDGE_STMT_NODE__NODE_ID = eINSTANCE.getEdgeStmtNode_Node_id();

    /**
     * The meta object literal for the '<em><b>Edge RHS</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference EDGE_STMT_NODE__EDGE_RHS = eINSTANCE.getEdgeStmtNode_EdgeRHS();

    /**
     * The meta object literal for the '<em><b>Attributes</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference EDGE_STMT_NODE__ATTRIBUTES = eINSTANCE.getEdgeStmtNode_Attributes();

    /**
     * The meta object literal for the '{@link org.eclipse.zest.internal.dot.parser.dot.impl.EdgeStmtSubgraphImpl <em>Edge Stmt Subgraph</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.zest.internal.dot.parser.dot.impl.EdgeStmtSubgraphImpl
     * @see org.eclipse.zest.internal.dot.parser.dot.impl.DotPackageImpl#getEdgeStmtSubgraph()
     * @generated
     */
    EClass EDGE_STMT_SUBGRAPH = eINSTANCE.getEdgeStmtSubgraph();

    /**
     * The meta object literal for the '<em><b>Subgraph</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference EDGE_STMT_SUBGRAPH__SUBGRAPH = eINSTANCE.getEdgeStmtSubgraph_Subgraph();

    /**
     * The meta object literal for the '<em><b>Edge RHS</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference EDGE_STMT_SUBGRAPH__EDGE_RHS = eINSTANCE.getEdgeStmtSubgraph_EdgeRHS();

    /**
     * The meta object literal for the '<em><b>Attributes</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference EDGE_STMT_SUBGRAPH__ATTRIBUTES = eINSTANCE.getEdgeStmtSubgraph_Attributes();

    /**
     * The meta object literal for the '{@link org.eclipse.zest.internal.dot.parser.dot.impl.NodeStmtImpl <em>Node Stmt</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.zest.internal.dot.parser.dot.impl.NodeStmtImpl
     * @see org.eclipse.zest.internal.dot.parser.dot.impl.DotPackageImpl#getNodeStmt()
     * @generated
     */
    EClass NODE_STMT = eINSTANCE.getNodeStmt();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute NODE_STMT__NAME = eINSTANCE.getNodeStmt_Name();

    /**
     * The meta object literal for the '<em><b>Attributes</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference NODE_STMT__ATTRIBUTES = eINSTANCE.getNodeStmt_Attributes();

    /**
     * The meta object literal for the '{@link org.eclipse.zest.internal.dot.parser.dot.impl.AttributeImpl <em>Attribute</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.zest.internal.dot.parser.dot.impl.AttributeImpl
     * @see org.eclipse.zest.internal.dot.parser.dot.impl.DotPackageImpl#getAttribute()
     * @generated
     */
    EClass ATTRIBUTE = eINSTANCE.getAttribute();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ATTRIBUTE__NAME = eINSTANCE.getAttribute_Name();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ATTRIBUTE__VALUE = eINSTANCE.getAttribute_Value();

    /**
     * The meta object literal for the '{@link org.eclipse.zest.internal.dot.parser.dot.impl.AttrStmtImpl <em>Attr Stmt</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.zest.internal.dot.parser.dot.impl.AttrStmtImpl
     * @see org.eclipse.zest.internal.dot.parser.dot.impl.DotPackageImpl#getAttrStmt()
     * @generated
     */
    EClass ATTR_STMT = eINSTANCE.getAttrStmt();

    /**
     * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ATTR_STMT__TYPE = eINSTANCE.getAttrStmt_Type();

    /**
     * The meta object literal for the '<em><b>Attributes</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ATTR_STMT__ATTRIBUTES = eINSTANCE.getAttrStmt_Attributes();

    /**
     * The meta object literal for the '{@link org.eclipse.zest.internal.dot.parser.dot.impl.AttrListImpl <em>Attr List</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.zest.internal.dot.parser.dot.impl.AttrListImpl
     * @see org.eclipse.zest.internal.dot.parser.dot.impl.DotPackageImpl#getAttrList()
     * @generated
     */
    EClass ATTR_LIST = eINSTANCE.getAttrList();

    /**
     * The meta object literal for the '<em><b>Alist</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ATTR_LIST__ALIST = eINSTANCE.getAttrList_A_list();

    /**
     * The meta object literal for the '{@link org.eclipse.zest.internal.dot.parser.dot.impl.AListImpl <em>AList</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.zest.internal.dot.parser.dot.impl.AListImpl
     * @see org.eclipse.zest.internal.dot.parser.dot.impl.DotPackageImpl#getAList()
     * @generated
     */
    EClass ALIST = eINSTANCE.getAList();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ALIST__NAME = eINSTANCE.getAList_Name();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ALIST__VALUE = eINSTANCE.getAList_Value();

    /**
     * The meta object literal for the '{@link org.eclipse.zest.internal.dot.parser.dot.impl.SubgraphImpl <em>Subgraph</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.zest.internal.dot.parser.dot.impl.SubgraphImpl
     * @see org.eclipse.zest.internal.dot.parser.dot.impl.DotPackageImpl#getSubgraph()
     * @generated
     */
    EClass SUBGRAPH = eINSTANCE.getSubgraph();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute SUBGRAPH__NAME = eINSTANCE.getSubgraph_Name();

    /**
     * The meta object literal for the '<em><b>Stmts</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference SUBGRAPH__STMTS = eINSTANCE.getSubgraph_Stmts();

    /**
     * The meta object literal for the '{@link org.eclipse.zest.internal.dot.parser.dot.impl.EdgeRhsImpl <em>Edge Rhs</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.zest.internal.dot.parser.dot.impl.EdgeRhsImpl
     * @see org.eclipse.zest.internal.dot.parser.dot.impl.DotPackageImpl#getEdgeRhs()
     * @generated
     */
    EClass EDGE_RHS = eINSTANCE.getEdgeRhs();

    /**
     * The meta object literal for the '<em><b>Op</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute EDGE_RHS__OP = eINSTANCE.getEdgeRhs_Op();

    /**
     * The meta object literal for the '{@link org.eclipse.zest.internal.dot.parser.dot.impl.EdgeRhsNodeImpl <em>Edge Rhs Node</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.zest.internal.dot.parser.dot.impl.EdgeRhsNodeImpl
     * @see org.eclipse.zest.internal.dot.parser.dot.impl.DotPackageImpl#getEdgeRhsNode()
     * @generated
     */
    EClass EDGE_RHS_NODE = eINSTANCE.getEdgeRhsNode();

    /**
     * The meta object literal for the '<em><b>Node</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference EDGE_RHS_NODE__NODE = eINSTANCE.getEdgeRhsNode_Node();

    /**
     * The meta object literal for the '{@link org.eclipse.zest.internal.dot.parser.dot.impl.EdgeRhsSubgraphImpl <em>Edge Rhs Subgraph</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.zest.internal.dot.parser.dot.impl.EdgeRhsSubgraphImpl
     * @see org.eclipse.zest.internal.dot.parser.dot.impl.DotPackageImpl#getEdgeRhsSubgraph()
     * @generated
     */
    EClass EDGE_RHS_SUBGRAPH = eINSTANCE.getEdgeRhsSubgraph();

    /**
     * The meta object literal for the '<em><b>Subgraph</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference EDGE_RHS_SUBGRAPH__SUBGRAPH = eINSTANCE.getEdgeRhsSubgraph_Subgraph();

    /**
     * The meta object literal for the '{@link org.eclipse.zest.internal.dot.parser.dot.impl.NodeIdImpl <em>Node Id</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.zest.internal.dot.parser.dot.impl.NodeIdImpl
     * @see org.eclipse.zest.internal.dot.parser.dot.impl.DotPackageImpl#getNodeId()
     * @generated
     */
    EClass NODE_ID = eINSTANCE.getNodeId();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute NODE_ID__NAME = eINSTANCE.getNodeId_Name();

    /**
     * The meta object literal for the '{@link org.eclipse.zest.internal.dot.parser.dot.EdgeOp <em>Edge Op</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.zest.internal.dot.parser.dot.EdgeOp
     * @see org.eclipse.zest.internal.dot.parser.dot.impl.DotPackageImpl#getEdgeOp()
     * @generated
     */
    EEnum EDGE_OP = eINSTANCE.getEdgeOp();

    /**
     * The meta object literal for the '{@link org.eclipse.zest.internal.dot.parser.dot.GraphType <em>Graph Type</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.zest.internal.dot.parser.dot.GraphType
     * @see org.eclipse.zest.internal.dot.parser.dot.impl.DotPackageImpl#getGraphType()
     * @generated
     */
    EEnum GRAPH_TYPE = eINSTANCE.getGraphType();

    /**
     * The meta object literal for the '{@link org.eclipse.zest.internal.dot.parser.dot.AttributeType <em>Attribute Type</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.zest.internal.dot.parser.dot.AttributeType
     * @see org.eclipse.zest.internal.dot.parser.dot.impl.DotPackageImpl#getAttributeType()
     * @generated
     */
    EEnum ATTRIBUTE_TYPE = eINSTANCE.getAttributeType();

    /**
     * The meta object literal for the '{@link org.eclipse.zest.internal.dot.parser.dot.CompassPt <em>Compass Pt</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.zest.internal.dot.parser.dot.CompassPt
     * @see org.eclipse.zest.internal.dot.parser.dot.impl.DotPackageImpl#getCompassPt()
     * @generated
     */
    EEnum COMPASS_PT = eINSTANCE.getCompassPt();

  }

} //DotPackage
