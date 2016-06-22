# == Class: roles::rebuilt_local_env
#
# This role is intended to rebuild the local environment from scratch.

class roles::rebuilt_local_env ( ) {

  # Base environment
  include profiles::local_aptsource
  include profiles::java-dev
  include profiles::tools::basic
  include profiles::aem::developer

  # Local environment
  include profiles::local_project

  Class['profiles::local_aptsource'] -> Class['profiles::java-dev']
  Class['profiles::local_aptsource'] -> Class['profiles::tools::basic']
  Class['profiles::java-dev'] -> Class['profiles::aem::developer']
  Class['profiles::aem::developer'] -> Class['profiles::local_project']

}
