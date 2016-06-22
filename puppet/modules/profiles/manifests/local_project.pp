# == Class: profiles::local_project
#
# This profile installs the utility scripts
# and configures your local project.
class profiles::local_project ( ) {

  # Install all the development scripts
  include pkg_scripts
  # Local project setup
  include pkg_local_project
  Class[pkg_scripts] -> Class[pkg_local_project]
}
