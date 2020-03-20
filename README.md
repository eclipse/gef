# Eclipse Graphical Editing Framework (GEF)

<div>
 <img align="left" src="/gef_eclipse_logo_360.png" width="180px">
 <p>
 The Eclipse <a href="http://www.eclipse.org/gef">Graphical Editing Framework (GEF)</a> provides Eclipse-integrated end-user tools in terms of a <a href="http://www.graphviz.org">Graphviz</a> authoring (<a href="https://github.com/eclipse/gef/wiki/DOT-User-Guide">DOT editor, DOT Graph view</a>) and a word cloud rendering environment (<a href="https://github.com/eclipse/gef/wiki/Cloudio-User-Guide">Tag Cloud view</a>), as well as framework components (<a href="https://github.com/eclipse/gef/wiki/Common">Common</a>, <a href="https://github.com/eclipse/gef/wiki/Geometry">Geometry</a>, <a href="https://github.com/eclipse/gef/wiki/FX">FX</a>, <a href="https://github.com/eclipse/gef/wiki/MVC">MVC</a>, <a href="https://github.com/eclipse/gef/wiki/Graph">Graph</a>, <a href="https://github.com/eclipse/gef/wiki/Layout">Layout</a>, <a href="https://github.com/eclipse/gef/wiki/Zest">Zest</a>, <a href="https://github.com/eclipse/gef/wiki/DOT">DOT</a>, and <a href="https://github.com/eclipse/gef/wiki/Cloudio">Cloudio</a>) to create rich graphical JavaFX- and SWT-based client applications, Eclipse-integrated or standalone.
 </p>
 <p>GEF participates in the annual Eclipse <a href="http://wiki.eclipse.org/Simultaneous_Release">simultaneous release</a>. Governance information can be found at <a href="https://projects.eclipse.org/projects/tools.gef">GEF@projects.eclipse.org</a>, 'New and Noteworthy' in our <a href ="https://github.com/eclipse/gef/blob/master/CHANGELOG.md">CHANGELOG</a>.
 </p>
</div>

<sub>The current code base has been developed in parallel to that of the original [Draw2d 3.x](https://www.eclipse.org/gef/draw2d/index.php), [GEF (MVC) 3.x](https://www.eclipse.org/gef/gef_mvc/index.php), and [Zest 1.x](https://www.eclipse.org/gef/zest/index.php) project components, which have been provided since 2004. Up to its graduation with the [4.0.0 (Neon)](https://projects.eclipse.org/projects/tools.gef/releases/4.0.0-neon) release in 2016, this code base had been referred to as 'GEF4' respectively <code>org.eclipse.gef4</code>, which is why these terms are still used in the <a href="https://projects.eclipse.org/projects/tools.gef/releases/4.1.0-neon.1">4.x (Neon)</a> maintenance stream. In the <a href="https://projects.eclipse.org/projects/tools.gef/releases/5.0.0-oxygen">5.x (Oxygen)</a> development stream, we have adopted the original project namespace to this code base, so that 'GEF' and <code>org.eclipse.gef</code> are now used instead, while we have started to refer to the original project components as 'GEF-Legacy', because they will only be maintained but are not developed further, providing their code base in the separate [eclipse/gef-legacy](https://github.com/eclipse/gef-legacy) repository.</sub>

## Installing the end-user tools ([users](https://www.eclipse.org/projects/dev_process/#2_3_2_Users))
You can install the DOT and Cloudio end-user tools (including the user guides) into your Eclipse installation via "Help -> Install New Software...", then pointing to one of the [GEF update-sites](https://projects.eclipse.org/projects/tools.gef/downloads)<sup>1)</sup> and selecting the *GEF DOT End-User Tools* and *GEF Cloudio End-User Tools* features. Having completed the installation, the user guides can be accessed via *Help -> Help Contents*. They can also be accessed online in the [GitHub Wiki](https://github.com/eclipse/gef/wiki#user-documentation), where they are maintained.

<sub><sup>1)</sup> Please note that explicit end-user features (including the user guides) have only been created in the [5.x (Oxygen)](https://projects.eclipse.org/projects/tools.gef/releases/5.0.0-oxygen) development stream and are (for now) only available via the [GEF (5.x) Master CI](https://hudson.eclipse.org/gef/job/gef-master/lastSuccessfulBuild/artifact/update-site), [GEF (5.x) Integration](http://download.eclipse.org/tools/gef/updates/integration), and [GEF (5.x) Milestones](http://download.eclipse.org/tools/gef/updates/milestones) update-sites. If you want to install the end-user tools from the [4.x (Neon)](https://projects.eclipse.org/projects/tools.gef/releases/4.1.0-neon.1) maintenance stream, using the [GEF4 Maintenance CI](https://hudson.eclipse.org/gef/job/gef4-maintenance/lastSuccessfulBuild/artifact/update-site/), [GEF4 Integration](http://download.eclipse.org/tools/gef/gef4/updates/integration), [GEF4 Milestones](http://download.eclipse.org/tools/gef/gef4/updates/milestones), or [GEF4 Releases](http://download.eclipse.org/tools/gef/gef4/updates/releases) update-site, you will have to select the *GEF4 DOT*, *GEF4 DOT.UI*, and *GEF4 DOT User Guide*, as well as the *GEF4 Cloudio.UI* and *GEF4 Cloudio User Guide* features instead. To access the related user documentation online, please refer to the deployed documentation at [help.eclipse.org](http://help.eclipse.org/neon/index.jsp).</sub>

## Getting started with the framework components ([adopters](https://www.eclipse.org/projects/dev_process/#2_3_3_Adopters))
In order to develop graphical applications with GEF, you should first set up a proper development environment. The following sections shortly lay out how to set up an Eclipse IDE for this purpose. They conclude with running our deployed and undeployed examples to confirm everything is set up properly. 

Having accomplished that, you might want to browse our [developer documentation](https://github.com/eclipse/gef/wiki#developer-documentation) to learn about the framework components in detail. At any time, if you get stuck, feel free to [contact us](https://projects.eclipse.org/projects/tools.gef/contact).

### Set up an Eclipse IDE (using OpenJDK 11 and OpenJFX 11)
1. Install a recent [OpenJDK](http://jdk.java.net/archive/) (e.g. 11.0.2) and a matching [OpenJFX SDK](https://gluonhq.com/products/javafx/) (e.g. 11.0.2) as a prerequisite.

2. Download a recent '[Eclipse IDE for Eclipse Committers](http://www.eclipse.org/downloads/packages)' package (e.g. 2019-09 R) and start it, pointing to an empty workspace folder. 

3. Select "Help -> Install New Software...". Choose to *Work with* [https://download.eclipse.org/efxclipse/updates-nightly/site/](https://download.eclipse.org/efxclipse/updates-nightly/site/), uncheck the *Group items per category* checkbox (the feature is uncategorized), and install *e(fx)clipse - IDE - PDE*.

4. Go to *Windows -> Preferences -> Java/Installed JREs* and ensure the installed OpenJDK is listed (otherwise add it manually). 

5. Go to *Windows -> Preferences -> Java/Installed JREs/Execution Environments* and make sure JavaSE-1.8 is mapped to the installed OpenJDK (the checkbox needs to be checked, otherwise e(fx)clipse will not be able to resolve the JavaFX dependencies.)</sup>

6. Go to *Windows -> Preferences -> JavaFX* and make sure the *JavaFX 11 + SDK* setting points to the lib folder of your OpenJFX SDK.

7. Make sure to close your Eclipse instance and start it again (don't use the *Restart* menu entry).

### Set up a Target Definition containing GEF (development snapshot)
1. Go to *File -> New -> Project...* and select to create a *General/Project*. Name it `gef-integration.target` or as you like, the project is to contain only a target definition.
2. Go to *File -> New -> Other...* then choose *Plug-in Development/Target Definition* and create a new empty (*Nothing: Start with an empty target definition*) target definition file named `gef-integration.target` within the newly created project.
3. Close the *Target Editor* that has automatically opened, open the target file with the *Text Editor* using the *Open With* context menu, then paste the following contents:<sup>3)</sup>
	
	```
	<?xml version="1.0" encoding="UTF-8" standalone="no"?>
	<?pde version="3.8"?>
	<target name="gef-integration" sequenceNumber="1">
	<locations>
	  <location includeAllPlatforms="false" includeConfigurePhase="false" includeMode="planner" includeSource="true" type="InstallableUnit">
	    <unit id="org.eclipse.fx.runtime.min.feature.feature.group" version="0.0.0"/>
	    <unit id="org.eclipse.sdk.ide" version="0.0.0"/>
	    <unit id="org.eclipse.emf.mwe2.runtime.sdk.feature.group" version="0.0.0"/>
	    <unit id="org.eclipse.xtext.sdk.feature.group" version="0.0.0"/>
	    <unit id="org.eclipse.emf.sdk.feature.group" version="0.0.0"/>
	    <repository location="https://download.eclipse.org/releases/2019-03"/>
	  </location>
	  <location includeAllPlatforms="false" includeConfigurePhase="false" includeMode="planner" includeSource="true" type="InstallableUnit">
	    <unit id="org.eclipse.gef.common.sdk.feature.group" version="0.0.0"/>
	    <unit id="org.eclipse.gef.geometry.sdk.feature.group" version="0.0.0"/>
	    <unit id="org.eclipse.gef.fx.sdk.feature.group" version="0.0.0"/>
	    <unit id="org.eclipse.gef.mvc.sdk.feature.group" version="0.0.0"/>
	    <unit id="org.eclipse.gef.mvc.examples.source.feature.group" version="0.0.0"/>
	    <unit id="org.eclipse.gef.layout.sdk.feature.group" version="0.0.0"/>
	    <unit id="org.eclipse.gef.graph.sdk.feature.group" version="0.0.0"/>
	    <unit id="org.eclipse.gef.zest.sdk.feature.group" version="0.0.0"/>
	    <unit id="org.eclipse.gef.zest.examples.source.feature.group" version="0.0.0"/>
	    <unit id="org.eclipse.gef.dot.sdk.feature.group" version="0.0.0"/>
	    <unit id="org.eclipse.gef.cloudio.sdk.feature.group" version="0.0.0"/>
	    <repository location="http://download.eclipse.org/tools/gef/updates/integration"/>
	  </location>
	</locations>
	</target>
	```
4. Now open the `gef-integration.target` file with the *Target Editor* again, using the *Open With* context menu, let it fully resolve, then click *Set as Target Platform* (link in the upper right corner of the editor).

<sub><sup>3)</sup> If you want to develop against the [4.x (Neon)](https://projects.eclipse.org/projects/tools.gef/releases/4.1.0-neon.1) maintenance stream, you will have to use the [GEF4 Integration](http://download.eclipse.org/tools/gef/gef4/updates/integration) update-site instead, and the unit ids have to be prefixed with `org.eclipse.gef4` instead of `org.eclipse.gef`.</sub>

### Run the examples
As the deployed [MVC Logo](https://github.com/eclipse/gef/wiki/MVC-Logo-Example) and [Zest Graph](https://github.com/eclipse/gef/wiki/Zest-Graph-Example) examples are contained in the target definition, you only need to start a new Eclipse Runtime to run them: 

1. Go to *Run -> Run Configurations...* then create a new *Eclipse Application* launch configuration.
2. On the *Main* tab, make sure the *Execution environment* points to at least JavaSE-1.8 .
3. On the *Arguments* tab, add *-Dosgi.framework.extensions=org.eclipse.fx.osgi* to *VM arguments:*, so that all JavaFX dependencies can be resolved wihtin the OSGi environment. If you are using OpenJDK / OpenJFX 11 or higher, further add *-Defxclipse.java-modules.dir=/Library/Java/Extensions/javafx-sdk-11.0.2/lib* (of course adjusting the path to point to your OpenJFX SDK lib folder)
4. Click *Run*.
5. Open the example views via *Window -> Show View -> Other...*, then selecting *Other/GEF MVC Logo Example* or *Other/GEF Zest Graph Example*.

The undeployed [Geometry](https://github.com/eclipse/gef/wiki/Geometry-Examples), [FX](https://github.com/eclipse/gef/wiki/FX-Examples), [FX.SWT](https://github.com/eclipse/gef/wiki/FX-Examples#examplesswt-undeployed), [Graph](https://github.com/eclipse/gef/wiki/Graph-Examples), [Layout](https://github.com/eclipse/gef/wiki/Layout-Examples), [Zest.FX](https://github.com/eclipse/gef/wiki/Zest-Examples), [Zest.FX.JFace](https://github.com/eclipse/gef/wiki/Zest-JFace-Examples), [DOT](https://github.com/eclipse/gef/wiki/DOT-Examples), and [Cloudio.UI](https://github.com/eclipse/gef/wiki/Cloudio-Examples) examples have to be checked out in source before. Using EGit this can easily be achieved as follows:

1. Go to *File -> Import...*, then select *Git/Projects from Git*, press *Next >*.
2. Select *Clone URI*, press *Next >*.
3. Paste `https://github.com/eclipse/gef.git` to the *URI* field , press *Next >*.
3. Select *master* branch, press *Next >*.<sup>4)</sup>
4. Confirm the local directory or change it as needed, press *Next >*.
5. Ensure *Import existing Eclipse projects* is checked, then select *Working Tree* and press *Next >*.
5. Select `org.eclipse.gef.cloudio.examples.ui`, `org.eclipse.gef.dot.examples`, `org.eclipse.gef.fx.examples`, `org.eclipse.gef.fx.examples.swt`, `org.eclipse.gef.geometry.examples`, `org.eclipse.gef.graph.examples`, `org.eclipse.gef.layout.examples`, `org.eclipse.gef.zest.examples`, and `org.eclipse.gef.zest.examples.jface`, press *Finish*.
6. Select an arbitrary example class, e.g. `org.eclipse.gef.fx.examples.ConnectionSnippet`, in the *Package Explorer* view and select *Run As -> Java Application* from the context menu.<sup>5)</sup>

<sub><sup>4)</sup> If you want to develop against the [4.x (Neon)](https://projects.eclipse.org/projects/tools.gef/releases/4.1.0-neon.1) maintenance stream, you will have to select the *R4_0_maintenance* branch instead, and all project names are still prefixed with `org.eclipse.gef4` instead of `org.eclipse.gef`.</sub>

<sub><sup>5)</sup> On MacOS, you will have to ensure that the *Use the -XstartOnFirstThread argument when launching with SWT* option is unchecked on the *Arguments* tab of the launch configuration, which was implicitly created, as pure JavaFX examples will otherwise not startup correctly. When starting examples that are based on the JavaFX-SWT-integration on the other hand (like e.g.  `org.eclipse.gef.fx.examples.swt.ButtonFXControlAdapterSnippet`), the *Use the -XstartOnFirstThread argument when launching with SWT* option has to be enabled.</sub>

## How to proceed from here?
The first thing you will probably want to consult is the developer documentation, which explains the different framework components in detail. It is bundled by the individual SDK features that are available for the framework components and can be accessed via *Help -> Help Contents* if these features are installed into the Eclipse IDE (it is not sufficient to include them in a target definition for this purpose). It is further contributed to [help.eclipse.org](http://help.eclipse.org/) for each release, where it can be accessed online, and can further be accessed online in the [GitHub Wiki](https://github.com/eclipse/gef/wiki#developer-documentation), where it is maintained.<sup>6)</sup>

All further project information (forum, mailing list, issue tracker, update-site locations, release plans) can be retrieved from the project meta-data at [projects.eclipse.org](https://projects.eclipse.org/projects/tools.gef).

If you want to contribute, please consult the [contributor guide](https://github.com/eclipse/gef/blob/master/CONTRIBUTING.md).

<sub><sup>6)</sup> The developer documentation for the [4.x (Neon)](https://projects.eclipse.org/projects/tools.gef/releases/4.1.0-neon.1)  stream is still available in its deployed form at [help.eclipse.org](http://help.eclipse.org/neon/index.jsp). The related online sources in the GEF4 wiki have been removed after the final [4.1.0 (Neon.1)](https://projects.eclipse.org/projects/tools.gef/releases/4.1.0-neon.1) release of this stream had been published.</sub>
