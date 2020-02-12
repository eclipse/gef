pipeline {
  // TODO: Add a way to archive old releases sites/drops
  // TODO: Add a way to remove old integration/milestones sites/drops
  // TODO: Download, prepare and archive eclipse installation in another job
    // then use copyArtifacts to retrieve. => This job will continue to work if
    // the download link / update site becomes unavailable
  // TODO: Create better notifications

  agent {
    label "migration"
  }

  options {
    disableConcurrentBuilds()
    timeout(time: 30, unit: "MINUTES")
  }

  triggers {
    // Promote an integration build automatically each Friday
    // Using default parameters: 
    // lastStableBuild, i(ntegration), y(es), (y)es, y(es)
    cron("30 23 * * 5")
  }

  parameters {
    string(
      name: "PROMOTED_BUILD",
      defaultValue: "lastStableBuild",
      description: """
        Which build of gef-master to promote.
        Use the build number or a permalink,
        e.g. lastStableBuild, lastSuccessfulBuild, etc.
      """
    )
    choice(
      name: "RELEASE_TYPE",
      choices: ["I", "S", "R"], // Default: i
      description: "The build type: I(ntegration), S(table), R(elease)"
    )
    choice(
      name: "PROMOTE_TO_UPDATE_SITE",
      choices: ["y", "n"], // Default: y
      description: "Whether to promote to a remote update-site: y(es), n(o)"
    )
    choice(
      name: "MERGE_WITH_EXISTING_UPDATE_SITE",
      choices: ["y", "n"], // Default: y
      description: "Whether to merge with existing update site: (y)es, (n)o"
    )
    choice(
      name: "PROMOTE_TO_DROP_LOCATION",
      choices: ["y", "n"], // Default: y
      description: "Whether to generate drop files: y(es), n(o)"
    )
    string(
      name: "RELEASE_LABEL",
      defaultValue: "5.4.0",
      description: """
        The release label used to label the (nested) update site,
        e.g.: 3.10.0, 3.10.1, 4.0.0
      """
    )
    string(
      name: "RELEASE_LABEL_SUFFIX",
      defaultValue: "",
      description: """
        An optional release label suffix to be appended to drop files and
        (nested) update site name, e.g. M1, RC1
      """
    )
  }

  environment {
    PROJECT_STORAGE_ROOT = "/home/data/httpd/download.eclipse.org/tools/gef"
    REMOTE_DROP_LOCATION = "${PROJECT_STORAGE_ROOT}/downloads/drops"
    REMOTE_UPDATE_SITE = getRemoteUpdateSite("${RELEASE_TYPE}", "${PROJECT_STORAGE_ROOT}")
    REMOTE_UPDATE_SITE_BASE = getRemoteUpdateSite("${RELEASE_TYPE}", "tools/gef")
    RELEASE_TYPE_LABEL = getReleaseTypeLabel("${RELEASE_TYPE}")
    ESDK_PATH = "/eclipse/downloads/drops4/R-4.9-201809060745/eclipse-SDK-4.9-linux-gtk-x86_64.tar.gz"
    PROMOTED_BUILD_NUMBER = lookUpBuildNumber("${PROMOTED_BUILD}")
    COPIED_ARTIFACTS = "${PROMOTED_BUILD_NUMBER}-artifacts"
    LOCAL_UPDATE_SITE = "${COPIED_ARTIFACTS}/update-site"
    UPDATE_SITE_LABEL = "${RELEASE_LABEL}${RELEASE_LABEL_SUFFIX}_gef-master_${PROMOTED_BUILD_NUMBER}"
    MIRROR_FILE = "/${REMOTE_UPDATE_SITE_BASE}/${UPDATE_SITE_LABEL}"
  }

  stages {
    stage("Promotion") {
      steps {
        sshagent(['projects-storage.eclipse.org-bot-ssh']) {

          copyArtifacts(
            projectName: "gef-master",
            selector: specific("${PROMOTED_BUILD_NUMBER}"),
            excludes: "update-site/web/**, update-site/index.html, update-site/site.xml, update-site/site.properties",
            target: "${COPIED_ARTIFACTS}",
            fingerprintArtifacts: true
          )

          sh label: "Logging", script: """
            set -eo pipefail
            echo "Artifacts to publish: ${LOCAL_UPDATE_SITE}"
            echo "Will publish as ${RELEASE_TYPE}-build"
            if [ "${PROMOTE_TO_UPDATE_SITE}" == "y" ];
            then
              echo "Will publish to ${REMOTE_UPDATE_SITE}"
              if [ "${MERGE_WITH_EXISTING_UPDATE_SITE}" == "y" ];
              then
                echo "Will merge with ${REMOTE_UPDATE_SITE}"
              fi
            fi
            if [ "${PROMOTE_TO_DROP_LOCATION}" == "y" ];
            then
              echo "Will promote to drop location"
            fi
            echo "Release Label: ${RELEASE_LABEL}"
            echo "Release Label Suffix: ${RELEASE_LABEL_SUFFIX}"
          """

          sh label: "Download and Prepare Eclipse SDK", script: """
            set -eo pipefail
            echo "Downloading eclipse"
            curl -L -o eclipse-SDK.tar.gz https://www.eclipse.org/downloads/download.php?file=${ESDK_PATH}
            tar -xzf eclipse-SDK.tar.gz

            # Prepare Eclipse SDK to provide WTP releng tools 
            # (used to postprocess repository, i.e set p2.mirrorsURL property)
            echo "Installing WTP Releng tools"
            ./eclipse/eclipse \
              -nosplash \
              --launcher.suppressErrors \
              -clean \
              -debug \
              -application org.eclipse.equinox.p2.director \
              -repository http://download.eclipse.org/webtools/releng/repository/ \
              -installIUs org.eclipse.wtp.releng.tools.feature.feature.group
          """

          sh label: "Promote to Drop Location", script: """
            set -eo pipefail
            if [ "${PROMOTE_TO_DROP_LOCATION}" == "y" ];
            then
              
              mkdir -p update-site
	            cp -R ${LOCAL_UPDATE_SITE}/* update-site/

              qualifiedVersion=\$(find update-site/features/ -maxdepth 1 | grep "org.eclipse.gef.common.sdk")
	            qualifiedVersion=\${qualifiedVersion%.jar}
              qualifiedVersion=\${qualifiedVersion#*_}
              qualifier=\${qualifiedVersion##*.}
              dropDir="${RELEASE_LABEL}/${RELEASE_TYPE}\${qualifier}"
              localDropDir=drops/\${dropDir}
              remoteDropDir=${REMOTE_DROP_LOCATION}/\${dropDir}

              if ssh genie.gef@projects-storage.eclipse.org "test -e \${remoteDropDir}";
              then
                echo "WARNING: Skipping promotion to drop location, because target already exists!"
                echo "WARNING: If you want to redeploy, (re)move or rename the target first."
              else

                echo "Creating drop files in local directory \${localDropDir}"
                mkdir -p \${localDropDir}

                # Create ZIP file
                cd update-site
                zipFile="GEF-Update-${RELEASE_LABEL}${RELEASE_LABEL_SUFFIX}.zip"
                zip -r ../\${localDropDir}/\${zipFile} features plugins artifacts.jar content.jar
                md5sum ../\${localDropDir}/\${zipFile} > ../\${localDropDir}/\${zipFile}.md5
                echo "Created \${zipFile}"
                cd .. 

                # Cleanup local update site (for drop files generation)
                rm -fr update-site

                # Generate build.cfg file to be referenced from downloads web page
                echo "hudson.job.name=gef-master" > \${localDropDir}/build.cfg
                echo "hudson.job.id=${PROMOTED_BUILD_NUMBER}" >> \${localDropDir}/build.cfg
                echo "hudson.job.url=${JENKINS_URL}job/gef-master/${PROMOTED_BUILD_NUMBER}" >> \${localDropDir}/build.cfg

                # Show content of \${localDropDir} before uploading
                ls -lah \${localDropDir}
                cat \${localDropDir}/build.cfg

                echo "Deploying to \${remoteDropDir}"
                ssh genie.gef@projects-storage.eclipse.org "mkdir -p \${remoteDropDir}"
                scp -r \${localDropDir}/* genie.gef@projects-storage.eclipse.org:\${remoteDropDir}/
              fi
            fi
          """

          sh label: "Promote to Update Site", script: """
            set -eo pipefail
            if [ "${PROMOTE_TO_UPDATE_SITE}" == "y" ];
            then

              if ssh genie.gef@projects-storage.eclipse.org "test -e ${REMOTE_UPDATE_SITE}/${UPDATE_SITE_LABEL}";
              then
                echo "WARNING: Skipping promotion to drop location, because target already exists!"
                echo "WARNING: If you want to redeploy, (re)move or rename the target first."
              else

                mkdir -p update-site/${UPDATE_SITE_LABEL}
                cp -R ${LOCAL_UPDATE_SITE}/* update-site/${UPDATE_SITE_LABEL}

                if [ "${MERGE_WITH_EXISTING_UPDATE_SITE}" == "y" ];
                then
                  ssh genie.gef@projects-storage.eclipse.org '''
                    if [ -e ${REMOTE_UPDATE_SITE}/compositeArtifacts.xml ];
                    then
                      echo "Target already composite - No need to merge"
                    else
                      echo "Did not find composite update site / preparing remote to be merged"
                      mkdir -p ${REMOTE_UPDATE_SITE}/pre_${UPDATE_SITE_LABEL}
                      find ${REMOTE_UPDATE_SITE} -mindepth 1 -maxdepth 1 ! -name pre_${UPDATE_SITE_LABEL} \
                        -exec mv {} ${REMOTE_UPDATE_SITE}/pre_${UPDATE_SITE_LABEL}/ \\;
                    fi
                  '''
                else
                  echo "Skipping merge operation."
                fi

                cp -R ${LOCAL_UPDATE_SITE}/* update-site/${UPDATE_SITE_LABEL}/

                # Ensure p2.mirrorURLs property is used in update site
                echo "Updating p2.mirrorURLs property."
                ./eclipse/eclipse \
                  -nosplash \
                  --launcher.suppressErrors \
                  -clean \
                  -debug \
                  -application org.eclipse.wtp.releng.tools.addRepoProperties \
                  -vmargs -DartifactRepoDirectory=${PWD}/update-site/${UPDATE_SITE_LABEL} \
                  -Dp2MirrorsURL="http://www.eclipse.org/downloads/download.php?format=xml&file=${MIRROR_FILE}"
                
                ls -la update-site/${UPDATE_SITE_LABEL}/

                scp -r update-site/${UPDATE_SITE_LABEL} genie.gef@projects-storage.eclipse.org:${REMOTE_UPDATE_SITE}/

                ssh genie.gef@projects-storage.eclipse.org '''
                  set -eo pipefail
                  cd ${REMOTE_UPDATE_SITE}
                  children=\$(find . -maxdepth 1 -type d -print | wc -l)
                  children=\$((\${children}-1))
                  timestamp=\$(date +%s000)
                  for file in *; do
                    if [ -d \${file} ];
                    then
                      childLocations="\${childLocations}    <child location=\\"\${file}\\"/>\n"
                    fi
                  done

                  contentXML="<?xml version=\\"1.0\\" encoding=\\"UTF-8\\"?>\n"
                  contentXML="\${contentXML}<?compositeMetadataRepository version=\\"1.0.0\\"?>\n"
                  contentXML="\${contentXML}<repository name=\\"GEF ${RELEASE_TYPE_LABEL}\\" "
                  contentXML="\${contentXML}type=\\"org.eclipse.equinox.internal.p2.metadata."
                  contentXML="\${contentXML}repository.CompositeMetadataRepository\\" version=\\"1.0.0\\">\n"
                  contentXML="\${contentXML}  <properties size=\\"1\\">\n"
                  contentXML="\${contentXML}    <property name=\\"p2.timestamp\\" value=\\"\${timestamp}\\"/>\n"
                  contentXML="\${contentXML}  </properties>\n"
                  contentXML="\${contentXML}  <children size=\\"\${children}\\">\n"
                  contentXML="\${contentXML}\${childLocations}"
                  contentXML="\${contentXML}  </children>\n"
                  contentXML="\${contentXML}</repository>\n"
                  echo -e "\${contentXML}" > compositeContent.xml
                  cat compositeContent.xml

                  artifactXML="<?xml version=\\"1.0\\" encoding=\\"UTF-8\\"?>\n"
                  artifactXML="\${artifactXML}<?compositeArtifactRepository version=\\"1.0.0\\"?>\n"
                  artifactXML="\${artifactXML}<repository name=\\"GEF ${RELEASE_TYPE_LABEL}\\" "
                  artifactXML="\${artifactXML}type=\\"org.eclipse.equinox.internal.p2.artifact."
                  artifactXML="\${artifactXML}repository.CompositeArtifactRepository\\" version=\\"1.0.0\\">\n"
                  artifactXML="\${artifactXML}  <properties size=\\"1\\">\n"
                  artifactXML="\${artifactXML}    <property name=\\"p2.timestamp\\" value=\\"\${timestamp}\\"/>\n"
                  artifactXML="\${artifactXML}  </properties>\n"
                  artifactXML="\${artifactXML}  <children size=\\"\${children}\\">\n"
                  artifactXML="\${artifactXML}\${childLocations}"
                  artifactXML="\${artifactXML}  </children>\n"
                  artifactXML="\${artifactXML}</repository>\n"
                  echo -e "\${artifactXML}" > compositeArtifacts.xml
                  cat compositeArtifacts.xml

                  # Ensure p2.index exists
                  if [ ! -e "p2.index" ];
                  then
                    echo "Creating p2.index file."
                    p2Index="version = 1\n"
                    p2Index="\${p2Index}metadata.repository.factory.order=compositeContent.xml,\\!\n"
                    p2Index="\${p2Index}artifact.repository.factory.order=compositeArtifacts.xml,\\!\n"
                    echo -e "\${p2Index}" > p2.index
                    cat p2.index
                  fi
                '''
              fi
            fi
          """
        }
      }
    }
  }

  post {
    always {
      mail(
        to: "prediger@itemis.de",
        subject: "(GEF) Promotion Pipeline Finished",
        body: "Just testing for now!"
      )
    }
  }
}

def lookUpBuildNumber(String promotedBuild) {
  def promotedBuildNumber = sh(
    script: "curl -fSs ${JENKINS_URL}job/gef-master/${promotedBuild}/buildNumber",
    returnStdout: true
  );
  println "Found build number ${promotedBuildNumber} for build ${promotedBuild}";
  return promotedBuildNumber;
}

def getReleaseTypeLabel(String releaseType) {
  def label;
  switch("${releaseType}") {
    case "I":
      label = "Integration";
      break;
    case "S":
      label = "Milestones";
      break;
    case "R":
      label = "Releases";
      break;
    default:
      error("Invalid Release Type: ${releaseType}");
      break;
  }
  return label;
}

def getRemoteUpdateSite(String releaseType, String storageRoot) {
  def remoteSite;
  switch("${releaseType}") {
    case "I":
      remoteSite = "${storageRoot}/updates/integration";
      break;
    case "S":
      remoteSite = "${storageRoot}/updates/milestones";
      break;
    case "R":
      remoteSite = "${storageRoot}/updates/releases";
      break;
    default:
      error("Invalid Release Type: ${releaseType}");
      break;
  }
  return remoteSite;
}
