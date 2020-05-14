#!/bin/sh
#*******************************************************************************
# Copyright (c) 2016, 2017 itemis AG and others.
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License 2.0 which is available at
# http://www.eclipse.org/legal/epl-2.0.
#
# SPDX-License-Identifier: EPL-2.0
#
# Contributors:
#     Alexander Nyßen (itemis AG) - initial API and implementation
#*******************************************************************************
# Script may take 6-7 command line parameters:
# $1: Hudson job name: <name>
# $2: Hudson build id: <id>
# $3: Build type: i(ntegration), s(table), r(elease)
# $4: Whether to promote to an update-site: (y)es, (n)o
# $5: Whether to merge the site with an existing one: (y)es, (n)o
# $6: Whether to generate drop files: (y)es, (n)o
# $7: The release label used to label the drop files and (nested) update site, e.g. 3.10.0 or 3.10.1
# $8: An optional release label suffix to be appended to drop files and (nested) update site name, e.g. M1, RC1 

if [ $# -eq 7 -o $# -eq 8  ];
then
	jobName=$1
	echo "jobName: $jobName"
	buildId=$2
	echo "buildId: $buildId"
	buildType=$3
	echo "buildType: $buildType"
	site=$4
	echo "site: $site"
	merge=$5
	echo "merge: $merge"
	dropFiles=$6
	echo "dropFiles: $dropFiles"
	releaseLabel=$7	
	echo "releaseLabel: $releaseLabel"
	if [ -n "$8" ];
	then
		releaseLabelSuffix=$8
		echo "releaseLabelSuffix: $releaseLabelSuffix"
	fi
else
	if [ $# -ne 0 ];
	then
		exit 1
	fi
fi

if [ -z "$jobName" ];
then
	echo -n "Please enter the name of the Hudson job you want to promote:"
	read jobName
fi

if [ -z "$buildId" ];
then
	for i in $( find ~/.hudson/jobs/$jobName/builds -type l | sed 's!.*/!!' | sort)
	do
		echo -n "$i, "
	done
	echo "lastStable, lastSuccessful"
	echo -n "Please enter the id of the $jobName build you want to promote:"
	read buildId
fi

if [ "$buildId" = "lastStable" -o "$buildId" = "lastSuccessful" ];
then
	# Reverse lookup the build id (in case lastSuccessful or lastStable was used)
	for i in $(find ~/.hudson/jobs/$jobName/builds/ -type l)
	do
		if [ "$(readlink -f $i)" = "$(readlink -f ~/.hudson/jobs/$jobName/$buildId)" ];
		then
			buildId=${i##*/}
		fi
	done
	echo "Reverse lookup (lastStable/lastSuccessful) yielded buildId: $buildId"
fi

# Determine the local update site we want to publish to
jobDir=$(readlink -f ~/.hudson/jobs/$jobName/builds/$buildId)
if [ ! -d $jobDir ];
then
	echo "The specified buildId does not refer to an existing build: $buildId"
	exit 1
fi
localUpdateSite=$jobDir/archive/update-site
echo "Publishing from local update site: $localUpdateSite"

# Select the build type
if [ -z "$buildType" ];
then
    echo -n "Please select which type of build you want to publish to [i(integration), s(table), r(elease)]: "
    read buildType
fi
echo "Publishing as $buildType build"

# check if we are going to promote to an update-site
if [ -z "$site" ];
then
	echo -n "Do you want to promote to an remote update site? [(y)es, (n)o]:"
	read site
fi
if [ "$site" != y -a "$site" != n ];
then
	echo "Parameter site has to be 'y'(es) or 'n'(o) but was: $site"
    exit 0
fi
echo "Promoting to remote update site: $site"

if [ "$site" = y ];
then
	# Determine remote update site we want to promote to
	case $buildType in
		i|I) remoteSite=integration;;
		s|S) remoteSite=milestones;;
		r|R) remoteSite=releases;;
		*) 
		echo "Parameter buildType has to be 'i'(ntegration), 's'(stable), or 'r'(elease), but was: $buildType"
		exit 1 ;;
	esac
	remoteUpdateSiteBase="tools/gef/updates/$remoteSite"
	remoteUpdateSite="/home/data/httpd/download.eclipse.org/$remoteUpdateSiteBase"
	echo "Publishing to remote update-site: $remoteUpdateSite"

	if [ -d "$remoteUpdateSite" ];
	then
		if [ -z "$merge" ];
		then
			echo -n "Do you want to merge with the existing update-site? [(y)es, (n)o]:"
			read merge
		fi
		if [ "$merge" != y -a "$merge" != n ];
		then
			echo "Parameter merge has to be 'y'(es) or 'n'(o) but was: $merge"
			exit 1
		fi
	else
		merge=n
	fi
	echo "Merging with existing site: $merge"
fi

# check if we are going to create drop files
if [ -z "$dropFiles" ];
then
	echo -n "Do you want to create drop files? [(y)es, (n)o]:"
	read dropFiles
fi
if [ "$dropFiles" != y -a "$dropFiles" != n ];
then
	echo "Parameter dropFiles has to be 'y'(es) or 'n'(o) but was: $dropFiles"
	exit 1
fi
echo "Generating drop files: $dropFiles"

# Determine releaseLabel
if [ -z "$releaseLabel" ];
then
    echo -n "Please enter release label (e.g. 3.10.0, 3.10.1M2):"
    read releaseLabel
fi

# Prepare a temp directory
tmpDir="$jobName-publish-tmp"
rm -fr $tmpDir
mkdir -p $tmpDir
cd $tmpDir

# Download and prepare Eclipse SDK, which is needed to merge update site and postprocess repository 
echo "Downloading eclipse to $PWD"
curl -L --output eclipse-SDK.tar.gz https://www.eclipse.org/downloads/download.php?file=/eclipse/downloads/drops4/R-4.9-201809060745/eclipse-SDK-4.9-linux-gtk-x86_64.tar.gz
tar -xvzf eclipse-SDK.tar.gz
cd eclipse
chmod 700 eclipse
cd ..
if [ ! -d "eclipse" ];
then
    echo "Failed to download an Eclipse SDK, being needed for provisioning."
    exit 1
fi
# Prepare Eclipse SDK to provide WTP releng tools (used to postprocess repository, i.e set p2.mirrorsURL property)
echo "Installing WTP Releng tools"
./eclipse/eclipse -nosplash --launcher.suppressErrors -clean -debug -application org.eclipse.equinox.p2.director -repository http://download.eclipse.org/webtools/releng/repository/ -installIUs org.eclipse.wtp.releng.tools.feature.feature.group
# Clean up
echo "Cleaning up"
rm eclipse-SDK.tar.gz

# Generate drop files
if [ "$dropFiles" = y ];
	then
	
	# Prepare local update site (for drop files)
	mkdir -p update-site
	cp -R $localUpdateSite/* update-site/
	echo "Copied $localUpdateSite to local directory update-site."
	
	qualifiedVersion=$(find update-site/features/ -maxdepth 1 | grep "org.eclipse.gef.common.sdk")
	qualifiedVersion=${qualifiedVersion%.jar}
    qualifiedVersion=${qualifiedVersion#*_}
    qualifier=${qualifiedVersion##*.}
    dropDir="${releaseLabel}/$(echo $buildType | tr '[:lower:]' '[:upper:]')$qualifier"
    localDropDir=drops/$dropDir
    echo "Creating drop files in local directory $localDropDir"
    mkdir -p $localDropDir
    
    cd update-site
    zip -r ../$localDropDir/GEF-Update-${releaseLabel}${releaseLabelSuffix}.zip features plugins artifacts.jar content.jar
    md5sum ../$localDropDir/GEF-Update-${releaseLabel}${releaseLabelSuffix}.zip > ../$localDropDir/GEF-Update-${releaseLabel}${releaseLabelSuffix}.zip.md5
    echo "Created GEF-Update-Site-${releaseLabel}${releaseLabelSuffix}.zip"  
    cd .. 

    # Cleanup local update site (for drop files generation)
	rm -fr update-site

	#generating build.cfg file to be referenced from downloads web page
    echo "hudson.job.name=$jobName" > $localDropDir/build.cfg
    echo "hudson.job.id=$buildId (${jobDir##*/})" >> $localDropDir/build.cfg
    echo "hudson.job.url=https://hudson.eclipse.org/gef/job/$jobName/$buildId" >> $localDropDir/build.cfg

    remoteDropDir=/home/data/httpd/download.eclipse.org/tools/gef/downloads/drops/$dropDir
    mkdir -p $remoteDropDir
    cp -R $localDropDir/* $remoteDropDir/
fi

if [ "$site" = y ];
then
	mkdir -p update-site
	updateSiteLabel=${releaseLabel}${releaseLabelSuffix}_${jobName}_${buildId}
	# Prepare composite local update site (transfer into composite if needed)
	if [ "$merge" = y ];
	then
		# check if the remote site is a composite update site
		echo "Merging existing site into local one."
		if [ -e "$remoteUpdateSite/compositeArtifacts.xml" ];
		then
			cp -R $remoteUpdateSite/* update-site/
		else
			mkdir -p update-site/pre_${updateSiteLabel}
			cp -R $remoteUpdateSite/* update-site/pre_${updateSiteLabel}/
		fi
	else 
		echo "Skipping merge operation."    
	fi
	# move local update site below composite one
	mkdir -p update-site/${updateSiteLabel}
	cp -R $localUpdateSite/* update-site/${updateSiteLabel}/
    	
	cd update-site
	children=$(find . -maxdepth 1 -type d -print | wc -l)
	children=$(($children-1))
	timestamp=$(date +%s000)

content="
<?xml version='1.0' encoding='UTF-8'?>
<?compositeMetadataRepository version='1.0.0'?>
<repository name='GEF ${remoteSite}' type='org.eclipse.equinox.internal.p2.metadata.repository.CompositeMetadataRepository' version='1.0.0'>
<properties size='1'>
<property name='p2.timestamp' value='${timestamp}'/>
</properties>
<children size='${children}'>
$(
for file in *; do
  if [ -d $file ]; then
printf "<child location='${file}'/>"
  fi
done
)
</children>
</repository>
"
	echo $content > compositeContent.xml

artifact="
<?xml version='1.0' encoding='UTF-8'?>
<?compositeArtifactRepository version='1.0.0'?>
<repository name='GEF ${remoteSite}' type='org.eclipse.equinox.internal.p2.artifact.repository.CompositeArtifactRepository' version='1.0.0'>
<properties size='1'>
<property name='p2.timestamp' value='${timestamp}'/>
</properties>
<children size='${children}'>
$(
for file in *; do
  if [ -d $file ]; then
printf "<child location='${file}'/>"
  fi
done
)
</children>
</repository>
"
	echo $artifact > compositeArtifacts.xml

	cd ..

	# Ensure p2.mirrorURLs property is used in update site
	echo "Updating p2.mirrorURLs property."
	./eclipse/eclipse -nosplash --launcher.suppressErrors -clean -debug -application org.eclipse.wtp.releng.tools.addRepoProperties -vmargs -DartifactRepoDirectory=$PWD/update-site/${updateSiteLabel} -Dp2MirrorsURL="http://www.eclipse.org/downloads/download.php?format=xml&file=/$remoteUpdateSiteBase/${updateSiteLabel}"

	# Create p2.index file
	if [ ! -e "update-site/p2.index" ];
	then
		echo "Creating p2.index file."
		echo "version = 1" > update-site/p2.index
		echo "metadata.repository.factory.order=compositeContent.xml,\!" >> update-site/p2.index
		echo "artifact.repository.factory.order=compositeArtifacts.xml,\!" >> update-site/p2.index
	fi

	# Backup then clean remote update site
	echo "Creating backup of remote update site."
	if [ -d "$remoteUpdateSite" ];
	then
		if [ -d BACKUP ];
		then
			rm -fr BACKUP
		fi
		mkdir BACKUP
		cp -R $remoteUpdateSite/* BACKUP/
		rm -fr $remoteUpdateSite
	fi

	echo "Publishing contents of local update-site directory to remote update site $remoteUpdateSite"
	mkdir -p $remoteUpdateSite
	cp -R update-site/* $remoteUpdateSite/
fi

# Clean up
echo "Cleaning up"
rm -fr eclipse
rm -fr update-site
