# == Class: pkg_aem
#
# Full description of class pkg_aem here.
#
# === Parameters
#
# Document parameters here.
#
# [*sample_parameter*]
#   Explanation of what this parameter affects and what it defaults to.
#   e.g. "Specify one or more upstream ntp servers as an array."
#
# === Variables
#
# Here you should define a list of variables that this module would require.
#
# [*sample_variable*]
#   Explanation of how this variable affects the funtion of this class and if
#   it has a default. e.g. "The parameter enc_ntp_servers must be set by the
#   External Node Classifier as a comma separated list of hostnames." (Note,
#   global variables should be avoided in favor of class parameters as
#   of Puppet 2.6.)
#
# === Examples
#
#  class { pkg_aem:
#    servers => [ 'pool.ntp.org', 'ntp.local.company.com' ],
#  }
#
# === Authors
#
# Author Name <author@domain.com>
#
# === Copyright
#
# Copyright 2015 Your name here, unless otherwise noted.
#
class pkg_aem  (
  $aem_url              = 'http://localhost:4502',
  $aem_user             = 'admin',
  $aem_password         = 'admin',
  $aem_backup_directory = '/home/cq/aem-author-backup',
  $install_dir          = '/home/cq/aem-author',
  $aem_os_user          = 'cq',
  $aem_os_group         = 'cq',
  $aem_backup_delay     = 1,
  $configuration_path   = "/etc/cqtools/cqtools.conf",
  ) {

    pkg_aem::instance { "$install_dir":
      aem_url              => $aem_url,
      aem_user             => $aem_user,
      aem_password         => $aem_password,
      aem_backup_directory => $aem_backup_directory,
      install_dir          => $install_dir,
      aem_os_user          => $aem_os_user,
      aem_os_group         => $aem_os_group,
      aem_backup_delay     => $aem_backup_delay,
      configuration_path   => $configuration_path,
    }
}
