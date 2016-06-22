#!/usr/bin/env sh

# Downloaded from: https://github.com/hashicorp/puppet-bootstrap/blob/master/debian.sh
# This bootstraps Puppet on Debian
set -e

export DEBIAN_FRONTEND=noninteractive

# Do the initial apt-get update
echo "Initial apt-get update..."
apt-get update >/dev/null

# Older versions of Debian don't have lsb_release by default, so 
# install that if we have to.
which lsb_release || apt-get install -y lsb-release

# Load up the release information
DISTRIB_CODENAME=$(lsb_release -c -s)

REPO_DEB_URL="http://apt.puppetlabs.com/puppetlabs-release-${DISTRIB_CODENAME}.deb"

#--------------------------------------------------------------------
# NO TUNABLES BELOW THIS POINT
#--------------------------------------------------------------------
#if [ "$EUID" -ne "0" ]; then
if [ "$(id -u)" -ne "0" ]; then
  echo "This script must be run as root." >&2
  exit 1
fi

# Install wget if we have to (some older Debian versions)
echo "Installing wget..."
apt-get install -y wget >/dev/null

# Install rsync if we have to (to transfer the puppet files to the guest)
echo "Installing rsync..."
apt-get install -y rsync >/dev/null

# Install the PuppetLabs repo
echo "Configuring PuppetLabs repo..."
repo_deb_path=$(mktemp)
wget --output-document="${repo_deb_path}" "${REPO_DEB_URL}" 2>/dev/null
dpkg -i "${repo_deb_path}" >/dev/null
apt-get update >/dev/null

# Install Puppet
echo "Installing Puppet..."
DEBIAN_FRONTEND=noninteractive apt-get -y -o Dpkg::Options::="--force-confdef" -o Dpkg::Options::="--force-confold" install puppet >/dev/null
# Disable the templatedir in puppet.conf, https://tickets.puppetlabs.com/browse/PUP-2566
sed -i 's/templatedir=/# templatedir=/' /etc/puppet/puppet.conf

echo "Puppet installed!"
