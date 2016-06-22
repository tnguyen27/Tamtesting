# == Class: roles::base_dev_env
#
# This role all the software required
# for a generic AEM developer.

class roles::base_dev_env ( ) {

  include profiles::local_aptsource
  include profiles::java-dev
  include profiles::tools::basic
  include profiles::aem::developer

  Class['profiles::local_aptsource'] -> Class['profiles::java-dev']
  Class['profiles::local_aptsource'] -> Class['profiles::tools::basic']
  Class['profiles::java-dev'] -> Class['profiles::aem::developer']
}
