# == Class: profiles::java-dev
#
# This profile install the java related tools
class profiles::java-dev ( ) {
  package { "oracle-java7-jdk":
    ensure => "installed"
  }
  package { "maven":
    ensure           => "installed",
    install_options  => ['--no-install-recommends'],
  }
  Package['oracle-java7-jdk'] ->   Package['maven']
}
