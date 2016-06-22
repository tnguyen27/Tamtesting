#!/bin/bash
set -e
cd /vagrant

mvn -f CQFiles/CQPackage/PkgDeployment/pom.xml clean install -Pxcqb-cqpkg
mvn -f CQFiles/CQPackage/PkgDeployment/pom.xml install -Pxcqb-jcrsyncr-cqpkg
mvn -f CQFiles/CQPackage/PkgDeployment/pom.xml install -Pxcqb-xpress-cqpkg
