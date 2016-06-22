class profiles::tools::basic ( ) {
  notify{"Profile tools::basic included.": }

  # Install basic tools in the system.
  $basictools = [ "screen", "sudo", "less", "telnet", "vim-tiny", "emacs", "multitail", "git", "nfs-kernel-server" ]
  package { $basictools:
    ensure => "installed"
  }

}
