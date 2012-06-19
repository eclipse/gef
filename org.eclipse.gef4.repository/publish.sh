#!/bin/sh

jobName="gef4-nightly-tycho"

# Script may take 3 command line parameters:
# $1: Hudson build id: <id>
# $2: Build type: n(ightly), m(ilestone), r(elease)
# $3: Whether to merge the site with an existing one: (y)es, (n)o
# 
if [ $# -eq 3 ];
        then
                buildId=$1
                buildType=$2
                merge=$3
        else
                if [ $# -ne 0 ];
                then
                        exit 1
                fi
fi

if [ -z "$buildId" ];
then
        for i in $( find /shared/jobs/$jobName/builds -type l | sed 's!.*/!!' | sort)
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
                jobDir=$(readlink -f /shared/jobs/$jobName/$buildId)
        else
                jobDir=$(readlink -f /shared/jobs/$jobName/builds/$buildId)
fi
localUpdateSite=$jobDir/archive/update-site
echo "Using local update-site: $localUpdateSite"

# Reverse lookup the build id (in case lastSuccessful or lastStable was used)
for i in $(find /shared/jobs/$jobName/builds/ -type l)
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
        echo -n "Please select which type of build you want to publish to [n(ightly), i(integration), m(ilestone), r(elease)]: "
        read buildType
fi
echo "Publishing as $buildType build"

# Determine remote update site we want to promote to
case $buildType in
        n|N) remoteSite=nightly ;;
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

# Prepare a temp directory
tmpDir="$jobName-publish-tmp"
rm -fr $tmpDir
mkdir -p $tmpDir/update-site
cd $tmpDir

# Download and prepare Eclipse SDK, which is needed to merge update site and postprocess repository 
echo "Downloading eclipse to $PWD"
cp /home/data/httpd/download.eclipse.org/eclipse/downloads/drops/R-3.7.2-201202080800/eclipse-SDK-3.7.2-linux-gtk-x86_64.tar.gz .
tar -xvzf eclipse-SDK-3.7.2-linux-gtk-x86_64.tar.gz
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
rm eclipse-SDK-3.7.2-linux-gtk-x86_64.tar.gz

# Prepare local update site (merge if required)
if [ "$merge" = y ];
        then
        echo "Merging existing site into local one."
        ./eclipse/eclipse -nosplash --launcher.suppressErrors -clean -debug -application org.eclipse.equinox.p2.metadata.repository.mirrorApplication -source file:$remoteUpdateSite -destination file:update-site
        ./eclipse/eclipse -nosplash --launcher.suppressErrors -clean -debug -application org.eclipse.equinox.p2.metadata.repository.mirrorApplication -source file:$localUpdateSite -destination file:update-site
        ./eclipse/eclipse -nosplash --launcher.suppressErrors -clean -debug -application org.eclipse.equinox.p2.artifact.repository.mirrorApplication -source file:$remoteUpdateSite -destination file:update-site
        ./eclipse/eclipse -nosplash --launcher.suppressErrors -clean -debug -application org.eclipse.equinox.p2.artifact.repository.mirrorApplication -source file:$localUpdateSite -destination file:update-site
        echo "Merged $localUpdateSite and $remoteUpdateSite into local directory update-site."
else
        echo "Skipping merge operation."
        cp -R $localUpdateSite/* update-site/
        echo "Copied $localUpdateSite to local directory update-site."
fi

# Ensure p2.mirrorURLs property is used in update site
echo "Setting p2.mirrorsURL to http://www.eclipse.org/downloads/download.php?format=xml&file=/$remoteUpdateSiteBase"
./eclipse/eclipse -nosplash --launcher.suppressErrors -clean -debug -application org.eclipse.wtp.releng.tools.addRepoProperties -vmargs -DartifactRepoDirectory=$PWD/update-site -Dp2MirrorsURL="http://www.eclipse.org/downloads/download.php?format=xml&file=/$remoteUpdateSiteBase"

# Create p2.index file
if [ ! -e "update-site/p2.index" ];
    then
        echo "Creating p2.index file"
        echo "version = 1" > update-site/p2.index
        echo "metadata.repository.factory.order = content.xml,\!" >> update-site/p2.index
        echo "artifact.repository.factory.order = artifacts.xml,\!" >> update-site/p2.index
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
