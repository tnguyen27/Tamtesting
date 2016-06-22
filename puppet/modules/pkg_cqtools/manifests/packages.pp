class pkg_cqtools::packages (
  $ensure              = "installed",
  $install_options     = ['--no-install-recommends'],
  ) {
  # Install the package
  package { "cqtools":
    ensure           => "installed",
    install_options  => ['--no-install-recommends'],
  }

}
