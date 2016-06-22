# Install al the required files
define pkg_aem::instance::packages  (
  $aem_url              = 'http://localhost:4502',
  $aem_user             = 'admin',
  $aem_password         = 'admin',
  $aem_backup_directory = '/home/cq/aem-author-backup',
  $install_dir          = $title,
  $configuration_path   = "$title/cqtools.conf",
  $aem_os_user          = 'cq',
  $aem_backup_delay     = 1,
  ) {

    # Initialize Nexus
  # class {'nexus':
  #   url => "http://192.168.33.10:8081/nexus",
  #   # username => "nexus",
  #   # password => "********"
  # }
  include 'nexus'

  # Download the license
  nexus::artifact {'license':
    gav => "com.brutils.misc:lic:1",
    packaging => 'tar.gz',
    repository => "public",
    output => "/tmp/lic.tar.gz",
    ensure  => 'present',
    timeout => 600,
    owner => 'vagrant',
    group => 'vagrant',
    mode => 0755
  }

  # Download cq author
  nexus::artifact {'author-quickstart':
    gav => "com.brutils.author:author:1",
    packaging => 'tar.xz',
    repository => "public",
    output => "/tmp/author-1.tar.xz",
    ensure  => 'present',
    timeout => 0,
    owner => 'vagrant',
    group => 'vagrant',
    mode => 0755
  }

  file { "/home/vagrant/aem-author":
    ensure => "directory",
    owner  => "vagrant",
    group  => "vagrant",
    mode   => 755,
  }

  # Extract CQ
  staging::extract { 'author-1.tar.xz':
    source  => '/tmp/author-1.tar.xz',
    target  => '/home/vagrant/aem-author',
    creates => '/home/vagrant/aem-author/crx-quickstart',
    user    => 'vagrant',
    group   => 'vagrant',
    onlyif => "test ! -d /home/vagrant/aem-author/crx-quickstart"
  }

  staging::extract { 'lic.tar.gz':
    source  => '/tmp/lic.tar.gz',
    target  => '/home/vagrant/aem-author',
    creates => '/home/vagrant/aem-author/license.properties',
    user    => 'vagrant',
    group   => 'vagrant',
    onlyif => "test ! -f /home/vagrant/aem-author/license.properties"
  }


  # Dependencies
  Nexus::Artifact['author-quickstart'] -> Staging::Extract ['author-1.tar.xz']
  File ["/home/vagrant/aem-author"]    -> Staging::Extract ['author-1.tar.xz']

  Nexus::Artifact['license'] -> Staging::Extract ['lic.tar.gz']

}

