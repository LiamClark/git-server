# -*- mode: ruby -*-
# vi: set ft=ruby :

VAGRANTFILE_API_VERSION = "2"

Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|
	config.vm.box		= "precise32"
	config.vm.box_url	= "http://files.vagrantup.com/precise32.box"
	
	config.vm.provider :virtualbox do |vb|
		vb.customize ["modifyvm", :id, "--memory", 512, "--cpus", 1]
	end

	config.vm.synced_folder "~/.ssh", "/keys"
	config.vm.synced_folder "puppet/files", "/vagrant/files"

    config.vm.synced_folder "/etc/git-server/mirrors", "/home/git/mirrors",
    	mount_options: ["dmode=777", "fmode=666"]
    config.vm.synced_folder "/etc/git-server/repositories", "/home/git/repositories",
    	mount_options: ["dmode=777", "fmode=666"]

	config.vm.provision "puppet" do |puppet|
		puppet.options = "--verbose --debug"
		puppet.manifests_path = "puppet/manifests"
	end
    	
end
