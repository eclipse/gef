# [GEF 5.0.0 (Oxygen)](https://projects.eclipse.org/projects/tools.gef/releases/5.0.0-oxygen)

Major release containing revisions (5.0.0) of all production components, which have been adopted to the original project namespace ([GEF4 + 1 = GEF 5](http://nyssen.blogspot.de/2017/02/gef4-1-gef-5.html)).

# [GEF 4.1.0 (Neon.1)](https://projects.eclipse.org/projects/tools.gef/releases/4.1.0-neon.1)

Bugfix release containing minor (1.1.0) respectively micro (1.0.1) revisions of all GEF4 production components (Common, Geometry, FX, MVC, Graph, Layout, Zest, DOT, and Cloudio). All API extensions are motivated by bugfixing, the API is fully backwards compatible to that of the previous [GEF 4.0.0 (Neon)](https://projects.eclipse.org/projects/tools.gef/releases/4.0.0-neon) release.

# [GEF 4.0.0 (Neon)](https://projects.eclipse.org/projects/tools.gef/releases/4.0.0-neon)

Graduation release containing the previously only preliminary published GEF4 production components (Common, Geometry, FX, MVC, Graph, Layout, Zest, DOT, and Cloudio) in version 1.0.0.

''As declared in the [4.0.0 (Neon) project plan](https://projects.eclipse.org/projects/tools.gef/releases/4.0.0-neon/plan), the decision about contributing GEF4 in version 1.0.0 (instead of 0.3.0) and an overall 4.0.0 release (instead of 3.11.0) was postponed up to M5. That is, Bugzilla entries commented before Neon M6 will refer to a 3.11.0 release and milestone contributions (including M5) include GEF4 components in version 0.3.0.''

Please note that several incompatible changes to the (up to 0.2.0 provisional) API of GEF4 were made. The list of added and removed classes is documented in the [https://www.eclipse.org/gef/project-info/GEF4-0.2.0-1.0.0-API-Diff.html GEF4 0.2.0-1.0.0 API Diff]. The most notable API changes are outlined below.

### GEF4 Common (1.0.0) ==

* [#484774](https://bugs.eclipse.org/bugs/show_bug.cgi?id=484774) As outlined in detail in ['GEF4 Collections and Properties - Guava goes FX'](http://nyssen.blogspot.de/2016/04/gef4-common-collections-and-properties.html) the property notification support provided by Common has been replaced with JavaFX observable collections and properties. In this turn, the Common component has been augmented to provide observable collections and related collection properties for Guava's SetMultimap and Multiset, as well as replacements for the ObservableSet, ObservableList, and ObservableMap collections and related collection properties provided by JavaFX.

* [#482972](https://bugs.eclipse.org/bugs/show_bug.cgi?id=482972) The adapter map injection support, described in detail in ['Adaptable - GEF4's interpretation of a classic'](http://nyssen.blogspot.com/2014/11/iadaptable-gef4s-interpretation-of.html), has been updated to ensure that adapter can always been retrieved via their actual runtime type, as well as been augmented with support for role-based adapter injection.

* [#481677](https://bugs.eclipse.org/bugs/show_bug.cgi?id=481677) Adapter map bindings can now be restricted to an adaptable of a certain role. Thereby, different bindings can e.g. be provided for a 'content' FXViewer and a 'palette' FXViewer.

<source lang="java" style="border-style:solid;border-color:#f2f2f2;border-width:1px;padding:10px;margin-bottom:10px">
@Override
protected void configure() {
// bind adapters for FXRootPart of role 'FXDomain.CONTENT_VIEWER_ROLE'
bindContentViewerRootPartAdapters(AdapterMaps.getAdapterMapBinder(binder(), FXRootPart.class, FXDomain.CONTENT_VIEWER_ROLE));
... 
}
</source>

### GEF4 FX (1.0.0) ==

* [#471154](https://bugs.eclipse.org/bugs/show_bug.cgi?id=471154) Renamed FX.UI module into FX.SWT module and added FXColorPicker, FXSimpleLinearGradientPicker, and FXAdvancedLinearGradientPicker, which were provided by MVC.FX.UI module before (refactored from AbstractFXColorPicker, FXColorPicker, FXSimpleGradientPicker, and FXAdvancedGradientPicker). Introduced new FX.JFace module, which provides FXPaintCellEditor and FXPaintSelectionDialog, which were provided by MVC.FX.UI module before (refactored from FXFillCellEditor and FXFillSelectionDialog).

* [#479395](https://bugs.eclipse.org/bugs/show_bug.cgi?id=479395) Merged ScrollPaneEx and FXGridCanvas into InfiniteCanvas. The FXGridLayer has been removed and its functionality was moved to the newly created InfiniteCanvas. The InfiniteCanvas does now allow to insert visuals at different "positions" inside of the InfiniteCanvas: underlay group, scrolled underlay group, content group, scrolled overlay group, overlay group.

* [#454681](https://bugs.eclipse.org/bugs/show_bug.cgi?id=454681) Added support to Connection for having a clickable area that is thicker than the connection stroke and allows to select also very thin connections. Its width is exposed by a property of Connection, so it can be bound (e.g. to adjust the width of the clickable area dependent on the scaling). Enhanced the MVC logo example to demonstrate example usage of this feature.

* [#454907](https://bugs.eclipse.org/bugs/show_bug.cgi?id=454907) Ensured that Connection properly clips its start and end decorations.

![Connection Decoration Clipping](/.changelog/Connection_Decoration_Clipping.png)

* [#488356](https://bugs.eclipse.org/bugs/show_bug.cgi?id=488356) Introduced support for orthogonal routing of a Connection, including support for start and end poin hints. While doing so, separated routing (manipulation of control points) from interpolating (visual appearance), so that routers (straight, orthogonal) and interpolators (poly-line, poly-bezier) can be freely mixed.

![Connection Snippet](/.changelog/Connection_Snippet.png)

* Revised DynamicAnchor and its IComputationStrategy to be comparable to a JavaFX binding, where the resulting position value is based on observable computation parameters that might depend on the anchorage (static) or anchored visual (dynamic). This allows to determine the values of computation parameters via JavaFX bindings.

<source lang="java" style="border-style:solid;border-color:#f2f2f2;border-width:1px;padding:10px;margin-bottom:10px">
getComputationParameter(AnchorageReferenceGeometry.class).bind(new ObjectBinding<IGeometry>() {
{
bind(anchorage.layoutBoundsProperty());
}

@Override
protected IGeometry computeValue() {
return NodeUtils.getShapeOutline(anchorage);
}
});
</source>

* [#443954](https://bugs.eclipse.org/bugs/show_bug.cgi?id=443954) GeometryNode has been revised to extend Region instead of Parent, so it no longer relies on overriding the impl_computeLayoutBounds() method that is deprecated and announced to be removed in future JavaFX releases. The API of GeometryNode was enhanced so that it does now allow to use relocate(double, double) and resize(double, double) to update the layout bounds (including layoutX and layoutY) while relocateGeometry(double, double) and resizeGeometry(double, double) can be used to update the geometric bounds. The layout bounds (and layoutX, layoutY) resemble the geometric bounds, expanded by a stroke offset (dependent on stroke width and type) and border insets.

### GEF4 MVC (1.0.0) ==

* [#477352](https://bugs.eclipse.org/bugs/show_bug.cgi?id=477352) The FXTransformPolicy now provides an API for the creation and manipulation of transformation matrices. These matrices are then concatenated, together with the initial host transformation matrix, to yield the new host transformation matrix. Therefore, complex transformations can be set up properly at one point, and the important values can be changed later on, e.g. during user interaction. All transaction and interaction policies related to transforms (FXRelocateConnectionPolicy, FXRelocateOnDragPolicy, FXResizeRelocateOnHandleDragPolicy, FXResizeRelocatePolicy, FXRotatePolicy, FXRotateSelectedOnHandleDragPolicy, FXRotateSelectedOnRotatePolicy, FXScaleRelocateOnHandleDragPolicy, and FXScaleRelocatePolicy) have been renamed to express the actual transformation (e.g. "translate" instead of "relocate"). Additionally, the intermediate FXResizeRelocatePolicy, FXRotatePolicy, and FXScaleRelocatePolicy have been removed. The policies that were dependent on those intermediate policies now directly use the FXTransformPolicy.

* [#479612](https://bugs.eclipse.org/bugs/show_bug.cgi?id=479612) Cleaned up ContentPolicy to only provide operations related to the content of the respective host part. It is intended to be registered at each IContentPart. Moved all "higher-level" operations into CreationPolicy and DeletionPolicy, which are to be registered at the IRootPart alone.

* [#480875](https://bugs.eclipse.org/bugs/show_bug.cgi?id=480875) Updating the selection, hover, and focus models is now handled within the CreationPolicy and the DeletionPolicy and the ClearHoverFocusSelectionOperation has been removed. The CreationPolicy selects and focusses a newly created part. The DeletionPolicy removes the deleted parts from the selection and focus models. Moreover, the CreationPolicy creates the IContentPart before executing operations on the history. Therefore, the content part can be used for live feedback even though the creation is not yet committed.

* [#481600](https://bugs.eclipse.org/bugs/show_bug.cgi?id=481600) Changed the implementation of ContentBehavior to ensure that deactivation does not remove children of its host. This way, deactivation of an FXViewer can now be temporarily applied (to disable all listeners) without loosing the content of the viewer.

* [#488358](https://bugs.eclipse.org/bugs/show_bug.cgi?id=488358) In addition to straight routing based on circle handles, FXBendFirstAnchorageOnSegmentHandleDragPolicy and FXBendConnectionPolicy now support orthogonal routing based on rectangle handles. Analogously to the way point based case, non-filled handle can be used to create new segments, whereas solid handles are used to move segments. Overlay of segments and 'normalization' (i.e. removal of overlaid segments) are performed during interaction.

![Straight Routing - PolyBezier Interpolator](/.changelog/Straight_Routing_PolyBezier_Interpolator.png)![Orthogonal Routing - Polyline Interpolator](/.changelog/Orthogonal_Routing_Polyline_Interpolator.png)

* [#493553](https://bugs.eclipse.org/bugs/show_bug.cgi?id=493553) IDomain now accepts only ITransactionalOperations to execute. This allows to filter non content-related operations (like selection of focus change) in case only content-relevant changes should be undoable. 

* [#481688](https://bugs.eclipse.org/bugs/show_bug.cgi?id=481677) The MVC Logo example has been augmented by a fly-out palette for object creation, to demonstrate how multiple viewers can be combined in a single domain.

![Palette - 1](/.changelog/Palette_for_object_creation_001.png)
![Palette - 2](/.changelog/Palette_for_object_creation_002.png)
![Palette - 3](/.changelog/Palette_for_object_creation_003.png)
![Palette - 4](/.changelog/Palette_for_object_creation_004.png)
![Palette - 5](/.changelog/Palette_for_object_creation_005.png)

### GEF4 Layout (1.0.0) ==

* [#491097](https://bugs.eclipse.org/bugs/show_bug.cgi?id=491097) The LayoutContext and ILayoutAlgorithm now use a Graph data model as input and output model for layout calculations instead of its own layout interface abstractions.

<source lang="java" style="border-style:solid;border-color:#f2f2f2;border-width:1px;padding:10px;margin-bottom:10px">
Graph graph = ...
layoutContext.setGraph(graph);  
layoutContext.setLayoutAlgorithm(layoutAlgorithm);
layoutContext.applyLayout(true);
</source>

### GEF4 Graph (1.0.0) ==

* [#480293](https://bugs.eclipse.org/bugs/show_bug.cgi?id=480293) Enhanced the builder API so that graph-, node-, and edge-builders can be chained appropriately. Node-builders can now be provided with an Object-key, that can be referred to by edge-builders.

<source lang="java" style="border-style:solid;border-color:#f2f2f2;border-width:1px;padding:10px;margin-bottom:10px">
Graph g1 = new Graph.Builder()
.attr(DotAttributes._TYPE__G, DotAttributes._TYPE__G__DIGRAPH)//
.node("n1")//
.attr(DotAttributes.LABEL__GNE, "1")//
.attr(DotAttributes.ID__GNE, "1")//
.node("n2")//
.attr(DotAttributes.LABEL__GNE, "2")//
.attr(DotAttributes.ID__GNE, "2")//
.node("n3")//
.attr(DotAttributes.LABEL__GNE, "3")//
.attr(DotAttributes.ID__GNE, "3")//
.edge("n1", "n2")//
.edge("n1", "n3")//
.build();
</source>

* [#484774](https://bugs.eclipse.org/bugs/show_bug.cgi?id=484774) All relevant properties are now exposed as JavaFX observable collections/properties.

### GEF4 Zest (1.0.0) ==

* [#470636](https://bugs.eclipse.org/bugs/show_bug.cgi?id=470636) Separated out the Zest JFace-API into an own module (Zest.FX.JFace), so it can be consumed without introducing dependencies on the Eclipse Workbench UI.

* [#478944](https://bugs.eclipse.org/bugs/show_bug.cgi?id=478944) Removed IEdgeDecorationProvider from Zest.FX.JFace. Respective ZestProperties.EDGE_SOURCE_DECORATION and ZestProperties.EDGE_TARGET_DECORATION attributes may be provided via IGraphNodeLabelProvider#getEdgeAttributes() instead.

<source lang="java" style="border-style:solid;border-color:#f2f2f2;border-width:1px;padding:10px;margin-bottom:10px">
class MyLabelProvider extends LabelProvider  implements IGraphAttributesProvider {	
@Override
public Map<String, Object> getEdgeAttributes(Object sourceNode, Object targetNode) {
Map<String, Object> edgeAttributes = new HashMap<>();
edgeAttributes.put(ZestProperties.SOURCE_DECORATION__E, new CircleHead());
edgeAttributes.put(ZestProperties.TARGET_DECORATION__E, new DiamondHead());
edgeAttributes.put(ZestProperties.CURVE_CSS_STYLE__E, "-fx-stroke: red;");
return edgeAttributes;
}
...
}
</source>

### GEF4 DOT (1.0.0) ==

The DOT component has been completely revised and has made significant progress towards a full DOT authoring environment. It does not provide public API yet and is currently limited to provide end-user functionality.

* [#446639](https://bugs.eclipse.org/bugs/show_bug.cgi?id=446639) A Graphviz preference page has been added, via which the path to the native dot executable and a Graphviz export format can be specified.

![Graphviz Preference Page](/.changelog/Graphviz_Preference_Page.png)

* [#337644](https://bugs.eclipse.org/bugs/show_bug.cgi?id=337644) A 'Sync Graphviz Export' toggle has been added as a DOT editor action to the main toolbar (it was previously located in the DOT Graph view). If enabled, it will export the persisted state of a currently edited DOT file in the format specified in the Graphviz preferences.

![Sync Graphviz Export](/.changelog/Sync_Graphviz_Export.png)

* The DOT Graph view will now use the native dot executable (if specified via the preferences) for layout (native mode), while it will otherwise use a comparable Layout algorithm (emulated mode).

![DOT Graph View (native mode)](/.changelog/DOT_Graph_view_native.png)
![DOT Graph View (emulated mode)](/.changelog/DOT_Graph_view_emulated.png)

* [#477980](https://bugs.eclipse.org/bugs/show_bug.cgi?id=477980) The DOT Graph view now also provides additional rendering capabilities, including edge decorations, splines, as well as all available kind of labels (for edges and nodes). 

![DOT Graph View - ArrowTypes](/.changelog/DOT_Graph_view_arrowtypes.png)
![DOT Graph View - Splines](/.changelog/DOT_Graph_view_splines.png)
![DOT Graph View - Styles](/.changelog/DOT_Graph_view_styles.png)

* The DOT editor has been enhanced with additional validation support for arrowtype, point, splinetype, shape, and style attribute values.

![DOT Editor - Warnings](/.changelog/DOT_Editor_warnings.png)
![DOT Editor - Errors](/.changelog/DOT_Editor_errors.png)
