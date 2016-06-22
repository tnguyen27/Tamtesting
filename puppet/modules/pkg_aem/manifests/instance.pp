define pkg_aem::instance  (
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

  pkg_cqtools::setup {"$configuration_path":
    aem_url              => $aem_url,
    aem_user             => $aem_user,
    aem_password         => $aem_password,
    aem_backup_directory => $aem_backup_directory,
    install_dir          => $install_dir,
    aem_os_user          => $aem_os_user,
    aem_backup_delay     => 1,
  }

  pkg_aem::instance::packages {"$configuration_path":
    aem_url              => $aem_url,
    aem_user             => $aem_user,
    aem_password         => $aem_password,
    aem_backup_directory => $aem_backup_directory,
    install_dir          => $install_dir,
    aem_os_user          => $aem_os_user,
    aem_backup_delay     => 1,
  }

  # CQ init file
  pkg_aem::instance::service {'/etc/init.d/cqauthor':
    aem_url              => $aem_url,
    aem_user             => $aem_user,
    aem_password         => $aem_password,
    aem_backup_directory => $aem_backup_directory,
    install_dir          => $install_dir,
    aem_os_user          => $aem_os_user,
    aem_os_group         => $aem_os_group,
    aem_backup_delay     => 1,
  }

  Pkg_cqtools::Setup ["$configuration_path"] ->  Pkg_aem::Instance::Service ['/etc/init.d/cqauthor']
  Pkg_aem::Instance::Packages["$configuration_path"] ->  Pkg_aem::Instance::Service ['/etc/init.d/cqauthor']
  
}
