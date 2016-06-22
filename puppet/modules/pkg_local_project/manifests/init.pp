# == Class: pkg_local_project
#
# This class deploys your local project
# to the local AEM instance and configures
# the JCRSyncr to listen to the appropriate
# paths inside your vagrant instance.

class pkg_local_project  ( ) {

  exec { 'dev-wait-for-cq':
    command => 'cqctl status && cqadm wait-for-start',
    path    => '/usr/local/bin/:/bin:/usr/sbin:/usr/bin',
    timeout => '600',
  }

  exec { '/home/vagrant/install.sh':
    command => 'su -l vagrant -c /home/vagrant/install.sh',
    path    => '/usr/local/bin/:/bin:/usr/sbin:/usr/bin',
    timeout => '600',
  }

  # Remove _maven.repositories files and *lastUpdated
  # See https://jira.codehaus.org/browse/MNG-5185
  exec { 'remove-maven.repositories':
    command => 'find /home/vagrant/.m2/repository -type f -name "_maven.repositories" -exec rm -rf {} \;',
    path    => '/usr/local/bin/:/bin:/usr/sbin:/usr/bin',
  }

  exec { 'remove-maven.lastUpdated':
    command => 'find /home/vagrant/.m2/repository -type f -name "*.lastUpdated" -exec rm -rf {} \;',
    path    => '/usr/local/bin/:/bin:/usr/sbin:/usr/bin',
  }

  # Dependencies
  Exec['remove-maven.repositories'] ->  Exec['/home/vagrant/install.sh']
  Exec['remove-maven.lastUpdated'] ->  Exec['/home/vagrant/install.sh']
}
