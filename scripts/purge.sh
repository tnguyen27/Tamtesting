#!/bin/bash
# set -x
# Reference: http://blog.codeboutique.com/post/creating-debian-squeeze-box-for-vagrant
# Remove installers 7.9
echo "Removing install files."
rm -f /tmp/author-1.tar.xz
rm -f /tmp/lic.tar.gz
rm -f /tmp/m2-cache.tar.gz
rm -f /etc/apt/sources.list.d/local-aptrepo.list
# ↑↑ reduced to 5.2

echo "Installing localepurge and zerofree"
DEBIAN_FRONTEND="noninteractive" apt-get install localepurge zerofree
echo "Removing locales"
localepurge
# Keep en, es, pt
#rm -rf /usr/share/locale/{af,am,ar,as,ast,az,bal,be,bg,bn,bn_IN,br,bs,byn,ca,cr,cs,csb,cy,da,de,de_AT,dz,el,en_AU,en_CA,eo,et,et_EE,eu,fa,fi,fo,fr,fur,ga,gez,gl,gu,haw,he,hi,hr,hu,hy,id,is,it,ja,ka,kk,km,kn,ko,kok,ku,ky,lg,lt,lv,mg,mi,mk,ml,mn,mr,ms,mt,nb,ne,nl,nn,no,nso,oc,or,pa,pl,ps,qu,ro,ru,rw,si,sk,sl,so,sq,sr,sr*latin,sv,sw,ta,te,th,ti,tig,tk,tl,tr,tt,ur,urd,ve,vi,wa,wal,wo,xh,zh,zh_HK,zh_CN,zh_TW,zu}

echo "Removing the localepurge package."
apt-get purge -y localepurge

# Remove bash history
echo "Remove bash history"
unset HISTFILE
rm -f /root/.bash_history
rm -f /home/vagrant/.bash_history

# Cleanup log files
echo "Cleaning log files"
find /var/log -type f | while read f; do echo -ne '' > $f; done;

echo "Removing /usr/share/doc"
rm -rf /usr/share/doc/*
rm -rf /usr/src/vboxguest*
rm -rf /usr/src/virtualbox-ose-guest*

# Remove _maven.repositories files and *lastUpdated
# See https://jira.codehaus.org/browse/MNG-5185
find /home/vagrant/.m2/repository -type f -name "_maven.repositories" -exec rm -rf {} \;
find /home/vagrant/.m2/repository -type f -name "*.lastUpdated" -exec rm -rf {} \;

echo "Removing linux-headers, debian-faq and installtion-report"
# rm -rf /usr/src/linux-headers* use apt-get remove instead
apt-get purge -y '^linux-headers-.*' debian-faq installation-report

echo "Remove apt cache and orphan packages."
# find /var/cache -type f -exec rm -rf {} \;
# Remove APT cache
apt-get autoremove -y
apt-get clean -y

# Fake init 1
echo "Shutting down services"
service exim4 stop
service puppet stop
service sudo stop
service sudo rsync stop
service cqauthor stop
killall dhclient
service network-manager stop
service rsyslog stop

echo "Mounting /dev/mapper/packer--debian--7-root ( / ) as read only."
mount -o remount,ro /dev/mapper/packer--debian--7-root
echo "Mounting /dev/sda1 ( /boot ) as read only."
mount -o remount,ro /dev/sda1

echo "Executing serofree on /dev/sda1"
zerofree /dev/sda1
echo "Executing zerofree on /dev/mapper/packer--debian--7-root"
zerofree /dev/mapper/packer--debian--7-root

echo "======================================================================"
echo "After the box is turned off, you can create your box with the command:"
echo 'vagrant package --base $(cat .vagrant/machines/default/virtualbox/id) --output bedrock-base_N.N.N.box'
echo "======================================================================"
echo "Shutting down the virtual machine ..."
shutdown -h now

# To add the vagrant box to your catalog:
#  vagrant box add bedrock-base-N.N bedrock-base-N.N.box
# You can confirm by listing added boxes:
# you@laptop:~/src/vagrant$ vagrant box list
# To remove it:
#  you@laptop:~/src/vagrant$ vagrant box remove MyDebianVagrantBox
# [vagrant] Deleting box 'MyDebianVagrantBox'..
# To start your box:
# vagrant init MyDebianVagrantBox

# http://blog.codeboutique.com/post/creating-debian-squeeze-box-for-vagrant
# https://gist.github.com/adrienbrault/3775253
# http://docs.vagrantup.com/v2/boxes/base.html
