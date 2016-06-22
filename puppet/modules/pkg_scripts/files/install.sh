#!/bin/bash
set -e
mkdir -p ~/.m2
cd /vagrant

if [ -f ~/.m2/settings.xml ]
then
    cp -f ~/.m2/settings.xml ~/.m2/settings.xml.back
fi

cp -f Resources/settings.xml ~/.m2/settings.xml
mvn -f CQFiles/CQPackage/PkgDeployment/pom.xml clean install -Pxcqb-cqpkg # Installs XCQB Package
mvn -f CQFiles/CQPackage/PkgDeployment/pom.xml install -Pxcqb-jcrsyncr-cqpkg # Installs the JCRSyncr
mvn -f CQFiles/CQPackage/PkgDeployment/pom.xml install -Pxcqb-xpress-cqpkg # Installs Xpress (OPTIONAL)
mvn clean install -Dmaven.test.skip=true -Dcheckstyle.skip=true -Dpmd.skip=true -Pdeploy-felix # Build the project
mvn install -PJCRSyncr-Setup # JCRSyncr automatic setup
mvn clean install -B -f Resources/Content/pom.xml -Dcq.server=http://localhost:4502 # Installs content package example (OPTIONAL)

