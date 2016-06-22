class pkg_scripts  ( ) {

  # Install the service file
  file { "/home/vagrant/install.sh":
    ensure  => file,
    owner   => 'vagrant',
    group   => 'vagrant',
    mode    => '0755',
    content => file('pkg_scripts/install.sh'),
  }

  file { "/home/vagrant/update.sh":
    ensure  => file,
    owner   => 'vagrant',
    group   => 'vagrant',
    mode    => '0755',
    content => file('pkg_scripts/update.sh'),
  }

  file { "/home/vagrant/build.sh":
    ensure  => file,
    owner   => 'vagrant',
    group   => 'vagrant',
    mode    => '0755',
    content => file('pkg_scripts/build.sh'),
  }

}
