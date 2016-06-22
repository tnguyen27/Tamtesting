# -*- mode: ruby -*-
# vi: set ft=ruby :

# ------------------------------------------------
# Usage:
#  Normal use:
#  $ vagrant up 
#  Rebuild the base box:
#  $ ENV='rebuilt_base' vagrant up
#  Rebuild the base box and deploy your project 
#  $ ENV='rebuilt_base_and_project' vagrant up
# ------------------------------------------------

# Vagrantfile API/syntax version. Don't touch unless you know what you're doing!
VAGRANTFILE_API_VERSION = "2"

Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|

  
  if ENV['ENV'] == 'rebuilt_base' || ENV['ENV'] == 'rebuilt_base_and_project'
    config.vm.box = "chef/debian-7.8"
    # puppet-bootstrap
    config.vm.provision "shell", path: "scripts/debian.sh"
    if ENV['ENV'] == 'rebuilt_base'
      selected_role = 'base_dev_env'
    else
      selected_role = 'rebuilt_local_env'
    end
  else
    # Replace this with the url to the corresponding box in the local
    # network
    config.vm.box = 'bedrock-base'
    config.vm.box_url = "http://bedrock-vagrant:Dyewwooc8*29)DTCn@54.236.230.120/vagrant/bedrock-base/bedrock-base.json"
    selected_role = 'local_env'
  end

  # Run `vagrant box outdated` to check for updates.
  config.vm.box_check_update = false
  config.vm.graceful_halt_timeout = 600

  config.vm.network "forwarded_port", guest: 4502, host: 4502   # CQ
  config.vm.network "forwarded_port", guest: 30303, host: 30303 # CQ Degug

  # Create a private network, which allows host-only access to the machine
  # using a specific IP.
  # config.vm.network "private_network", ip: "192.168.33.10"

  # Use synced folders backed by NFS to improve performance.
  # Install nfs-kernel-server package in debian / ubuntu.
  # config.vm.network "private_network", type: "dhcp"
  # config.vm.synced_folder ".", "/vagrant", type: "nfs"

  # Share an additional folder to the guest VM.
  # config.vm.synced_folder "../data", "/vagrant_data"

  config.vm.provision "puppet" do |puppet|
    puppet.hiera_config_path = "puppet/hiera.yaml"
    puppet.module_path = "puppet/modules"
    puppet.manifests_path = "puppet/manifests"
    puppet.manifest_file  = 'rolecontrol.pp'

    # Custom facts to be exposed by Facter can be specified as well:
    puppet.facter = {
      ## Main role for this machine
      "par_role" => selected_role,
    }
    # ADDITIONAL OPTIONS
    # puppet.options = "--verbose --debug  --graph --graphdir /vagrant/graphs"
  end


  config.vm.provider "virtualbox" do |v|
    host = RbConfig::CONFIG['host_os']
    # Give VM 1/4 system memory & access to all cpu cores on the host
    if host =~ /darwin/
      cpus = `sysctl -n hw.ncpu`.to_i
      # sysctl returns Bytes and we need to convert to MB
      # mem = `sysctl -n hw.memsize`.to_i / 1024 / 1024 / 4
    elsif host =~ /linux/
      cpus = `nproc`.to_i
      # meminfo shows KB and we need to convert to MB
      # mem = `grep 'MemTotal' /proc/meminfo | sed -e 's/MemTotal://' -e 's/ kB//'`.to_i / 1024 / 4
    else
      cpus = 1
      # mem = 1024
    end
    # v.memory = mem
    v.memory = 3072
    v.cpus = cpus
  end
end
