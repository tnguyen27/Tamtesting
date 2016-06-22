# == Class: profiles::local_aptsource
#
# This profile makes the node to use the local apt server
class profiles::local_aptsource ( ) {

  contain ::local_aptsource
}
