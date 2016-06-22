# == Class: m2_cache
#
# This class installs a initial m2 cache
# in /home/vagrant/.m2/repository
#
# === Dependencies
#
# This module depends on:
#
# - nexus module: To download artifacts from
#   the nexus repository.
# - staging module: To extract the artifact.

class m2_cache ( ) {

  # Download the m2 cache
  nexus::artifact {'m2-cache':
    gav => "com.brutils.misc:m2-cache:1",
    packaging => 'tar.gz',
    repository => "public",
    output => "/tmp/m2-cache.tar.gz",
    ensure  => 'present',
    timeout => 0,
    owner => 'vagrant',
    group => 'vagrant',
    mode => 0755
  }



  file { "/home/vagrant/.m2":
    ensure => "directory",
    owner  => "vagrant",
    group  => "vagrant",
    mode   => 755,
  }
  
  staging::extract { 'm2-cache.tar.gz':
    source  => '/tmp/m2-cache.tar.gz',
    target  => '/home/vagrant/.m2/',
    creates => '/home/vagrant/.m2/repository',
    user    => 'vagrant',
    group   => 'vagrant',
    onlyif => "test ! -f /home/vagrant/.m2/repository"
  }

  
  Nexus::Artifact['m2-cache'] -> Staging::Extract['m2-cache.tar.gz']
  File['/home/vagrant/.m2'] -> Staging::Extract['m2-cache.tar.gz']
}
