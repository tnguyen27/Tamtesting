define pkg_cqtools::setup (
  $aem_url              = 'http://localhost:4502',
  $aem_user             = 'admin',
  $aem_password         = 'admin',
  $aem_backup_directory = '/home/cq/aem-author-backup',
  $install_dir          = '/home/cq/aem-author',
  $aem_os_user          = 'cq',
  $aem_backup_delay     = 1,
  $configuration_path   = $title,
  ) {

  # Install the package
  include pkg_cqtools::packages

  # Install the configuration
  # /etc/cqtools/cqtools.conf
  file { "$configuration_path":
    owner   => "root",
    group   => 'cq',
    mode    => '0660',
    content => template('pkg_cqtools/cqtools_conf.erb'),
  }

  # Verify the backup directory
  if defined(File["$aem_backup_directory"]) {
    fail("AEM backup directory File[$aem_backup_directory] duplicated.")
  }

  file { "$aem_backup_directory":
    ensure => "directory",
    owner   => "$aem_os_user",
    group   => 'cq',
  }

  # add the user to the cq group
  user { "$aem_os_user":
    ensure => present,
    groups => [cq],
  }

  Class['pkg_cqtools::packages'] -> File["$configuration_path"]
}
