# == Class: local_aptsource
#
# This profile makes the node to use the local apt server
class local_aptsource (
  $key,
  $key_source,
  $location,
  $release     = 'wheezy-pkgs',
  $repos       = 'main',
  $include_src = false,
) {

  include apt

  apt::key { 'local-apt-key':
    key        => $key,
    key_source => $key_source,
  }

  apt::source { 'local-aptrepo':
    location   => $location,
    release    => $release,
    repos      => $repos,
    include_src => $include_src,
  }

  Apt::Key['local-apt-key'] -> Apt::Source['local-aptrepo']
}
