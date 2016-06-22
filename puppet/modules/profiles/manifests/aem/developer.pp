# == Class: profiles::aem::developer
#
# This profile installs an AEM 6 author instance
# in the developer machine.
class profiles::aem::developer ( ) {

  include ::nexus

  contain ::pkg_cqtools::packages

  contain ::pkg_aem

  class {'m2_cache':
  }
  
  notify {"profile-aem-dev-notify":
    message => "Profile included: profiles::aem::developer"
  }

  Class['nexus'] -> Class['pkg_aem']
  # pkg_cqtools::packages creates the cq group
  Class['pkg_cqtools::packages'] -> User["vagrant"]
  
}
