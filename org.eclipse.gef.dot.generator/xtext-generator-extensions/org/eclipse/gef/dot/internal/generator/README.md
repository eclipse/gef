These generator extensions are referenced from within the Xtext generator of the MWE2 workflow and are used to customize the Xtext generation process for the DOT languages.

In order to enable that Xtext artifacts can also be generated headlessly using Maven/Tycho, we need to deploy the generator extensions as a jar archive, which can be specified as a dependency of the *exec-maven-plugin* that executes the MWE2 workflow. This is necessary because the Xtext generation is executed
during the *generate-sources* phase when class files are not yet available.

If changes are applied to the generator extensions, the **xtext-generator-extensions.jar** file has to
be re-created as follows:
1. Generate an Ant build file by choosing *'Plug-in Tools -> Create Ant Build File'* from the context menu
of the selected **org.eclipse.gef.dot** project.
2. Executing the Ant build by choosing *'Run As -> Ant Build'* from the context menu of the selected **build.xml** file.

The created build and temporary files can be deleted afterwards.