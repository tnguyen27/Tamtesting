#!/bin/bash
set -e
# cd into the source directory
cd /vagrant
# install the bundle using the osgi console
mvn clean install -Pdeploy-felix

