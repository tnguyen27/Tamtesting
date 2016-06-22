# == Class: roles::local_env
#
# This role is applied to machines
# intended for local development.
# AEM and JDK 7 are should be present
# in the machine.

class roles::local_env ( ) {
  include profiles::local_project
}
