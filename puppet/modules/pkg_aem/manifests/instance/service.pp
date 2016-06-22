# Install al the required files
define pkg_aem::instance::service  (
  $aem_url              = 'http://localhost:4502',
  $aem_user             = 'admin',
  $aem_password         = 'admin',
  $aem_backup_directory = '/home/cq/aem-author-backup',
  $install_dir          = $title,
  $configuration_path   = "$title/cqtools.conf",
  $aem_os_user          = 'cq',
  $aem_os_group         = 'cq',
  $aem_backup_delay     = 1,
  ) {

  # Install the start file
  file { "/etc/init.d/cqauthor":
    ensure  => file,
    owner   => 'root',
    group   => 'root',
    mode    => '0755',
    content => template('pkg_aem/aem_service.erb'),
  }

  # Install the service file
  file { "$install_dir/crx-quickstart/bin/start":
    ensure  => file,
    owner   => "$aem_os_user",
    group   => "$aem_os_group",
    mode    => '0755',
    content => template('pkg_aem/start.erb'),
  }

  service { "cqauthor":
    enable => true,
    ensure => "running",
  }

  File["/etc/init.d/cqauthor"] -> Service["cqauthor"]
  File["$install_dir/crx-quickstart/bin/start"] -> Service["cqauthor"]

}

