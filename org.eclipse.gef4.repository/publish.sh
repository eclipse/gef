#!/bin/sh

jobName="gef4-master"

# Script may take 3 command line parameters:
# $1: Hudson build id: <id>
# $2: Build type: i(ntegration), m(ilestone), r(elease)
# $3: Whether to merge the site with an existing one: (y)es, (n)o
# $4: The release label used to label the (nested) update site, e.g. 3.10.0 or 3.10.1M4
# 
if [ $# -eq 4 ];
        then
                buildId=$1
                buildType=$2
                merge=$3
               	releaseLabel=$4
        else
                if [ $# -ne 0 ];
                then
                        exit 1
                fi
fi

if [ -z "$buildId" ];
then
        for i in $( find ~/.hudson/jobs/$jobName/builds -type l | sed 's!.*/!!' | sort)
        do
                echo -n "$i, "
        done
        echo "lastStable, lastSuccessful"
        echo -n "Please enter the id/label of the Hudson job you want to promote:"
        read buildId
fi
if [ -z "$buildId" ];
        then
                exit 0
fi

# Determine the local update site we want to publish to
if [ "$buildId" = "lastStable" -o "$buildId" = "lastSuccessful" ];
        then
                jobDir=$(readlink -f ~/.hudson/jobs/$jobName/$buildId)
        else
                jobDir=$(readlink -f ~/.hudson/jobs/$jobName/builds/$buildId)
fi
localUpdateSite=$jobDir/archive/update-site
echo "Using local update-site: $localUpdateSite"

# Reverse lookup the build id (in case lastSuccessful or lastStable was used)
for i in $(find ~/.hudson/jobs/$jobName/builds/ -type l)
do
        if [ "$(readlink -f $i)" =  "$jobDir" ];
                then
                        buildId=${i##*/}
        fi
done
echo "Reverse lookup yielded build id: $buildId"

# Select the build type
if [ -z "$buildType" ];
then
        echo -n "Please select which type of build you want to publish to [i(integration), m(ilestone), r(elease)]: "
        read buildType
fi
echo "Publishing as $buildType build"

# Determine remote update site we want to promote to
case $buildType in
        i|I) remoteSite=integration ;;
        m|M) remoteSite=milestones ;;
        r|R) remoteSite=releases ;;
        *) exit 0 ;;
esac
remoteUpdateSiteBase="tools/gef/gef4/updates/$remoteSite"
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
                        exit 0
                fi
        else
                merge=n
fi
echo "Merging with existing site: $merge"

# Determine releaseLabel
if [ -z "$releaseLabel" ];
        then
                echo -n "Please enter release label (e.g. 3.10.0, 3.10.1M2):"
                read releaseLabel
fi

# Prepare a temp directory
tmpDir="$jobName-publish-tmp"
rm -fr $tmpDir
mkdir -p $tmpDir/update-site
cd $tmpDir

# Download and prepare Eclipse SDK, which is needed to merge update site and postprocess repository 
echo "Downloading eclipse to $PWD"
cp /home/data/httpd/download.eclipse.org/eclipse/downloads/drops4/R-4.4.2-201502041700/eclipse-SDK-4.4.2-linux-gtk-x86_64.tar.gz .
tar -xvzf eclipse-SDK-4.4.2-linux-gtk-x86_64.tar.gz
cd eclipse
chmod 700 eclipse
cd ..
if [ ! -d "eclipse" ];
        then
                echo "Failed to download an Eclipse SDK, being needed for provisioning."
                exit
fi
# Prepare Eclipse SDK to provide WTP releng tools (used to postprocess repository, i.e set p2.mirrorsURL property)
echo "Installing WTP Releng tools"
./eclipse/eclipse -nosplash --launcher.suppressErrors -clean -debug -application org.eclipse.equinox.p2.director -repository http://download.eclipse.org/webtools/releng/repository/ -installIUs org.eclipse.wtp.releng.tools.feature.feature.group
# Clean up
echo "Cleaning up"
rm eclipse-SDK-4.4.2-linux-gtk-x86_64.tar.gz

updateSiteLabel=${releaseLabel}_${jobName}_#${buildId}
# Prepare composite local update site (transfer into composite if needed)
if [ "$merge" = y ];
    then
        # check if the remote site is a composite update site
        echo "Merging existing site into local one."
        if [ -e "$remoteUpdateSite/compositeArtifacts.xml" ];
    	    then
    	    cp -r $remoteUpdateSite/* update-site/
    	else
    		mkdir -p update-site/pre_${updateSiteLabel}
    		cp -r $remoteUpdateSite/* update-site/pre_${updateSiteLabel}/
    	fi
else 
   	echo "Skipping merge operation."    
fi
# move local update site below composite one
mkdir -p update-site/${updateSiteLabel}
cp $localUpdateSite/* update-site/${updateSiteLabel}/
    	
cd update-site
children=$(find . -maxdepth 1 -type d -print | wc -l)
children=$(($children-1))
    		 	
timestamp=$(date +%s000)

content="
<?xml version='1.0' encoding='UTF-8'?>
<?compositeMetadataRepository version='1.0.0'?>
<repository name='GEF4 ${remoteSite}' type='org.eclipse.equinox.internal.p2.metadata.repository.CompositeMetadataRepository' version='1.0.0'>
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
<repository name='GEF4 ${remoteSite}' type='org.eclipse.equinox.internal.p2.artifact.repository.CompositeArtifactRepository' version='1.0.0'>
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
./eclipse/eclipse -nosplash --launcher.suppressErrors -clean -debug -application org.eclipse.wtp.releng.tools.addRepoProperties -vmargs -DartifactRepoDirectory=$PWD/update-site/${updateSiteLabel} -Dp2MirrorsURL="http://www.eclipse.org/downloads/download.php?format=xml&file=/$remoteUpdateSiteBase/${updateSiteLabel}"

# Create p2.index file
if [ ! -e "update-site/p2.index" ];
    then
        echo "Creating p2.index file"
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

# Clean up
echo "Cleaning up"
rm -fr eclipse
rm -fr update-site
