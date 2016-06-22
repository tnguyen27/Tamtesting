# == Class: Rolecontrol
#
# This assigns the role to the machine.
#
# === Parameters for the manifest
#
# [*par_role*]
#   Main role for this node, possible values:
#   - base_dev_env
#
# === Vagrant File Facter example
#
# puppet.facter = {
#   "par_role" => "base_dev_env",
# }


class rolecontrol (
  # Role
  $role,
  # Main Parameters
  $aemuser,
  $aempassword,
  $aemurl,
  $aempath,
) {

  case $rolecontrol::role {
    'base_dev_env':       { include roles::base_dev_env }
    'local_env':          { include roles::local_env }
    'rebuilt_local_env':  { include roles::rebuilt_local_env }
    default:              { warning('Unknown -role- selected') } # do nothing
  }
}


class {'rolecontrol':
  # The role
  role => $par_role,
  
  #################################################
  # Parameters $rolecontrol::parname              #
  #################################################
  # AEM ----------------------
  aemuser      => $par_aemuser,
  aempassword  => $par_aempassword,
  aemurl       => $par_aemurl,
  aempath      => $par_aempath,
}
