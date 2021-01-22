# -*- mode: ruby -*-
# vi: set ft=ruby :

# Changhua County, # https://cloud.google.com/compute/docs/regions-zones?hl=zh-tw
# HongKong ==> asia-east2-a, Singapore ==> asia-southeast1-a, Changhua County ==> asia-east1-c
zone=ENV.fetch('ZONE', "asia-east1-a")
#machine_type="n1-standard-4 # https://cloud.google.com/compute/docs/machine-types
machine_type=ENV.fetch('MACHINETYPE', "n1-standard-8")
vncsecret=ENV.fetch('VNNCSECRET', "passw0rd")

Vagrant.configure("2") do |config|
  config.vm.box = "google/gce"

  config.vm.provider :google do |google, override|
    google.google_project_id = "genuine-airfoil-302302"
    #google.google_json_key_location = "C:/Users/John/.vagrant.d/genuine-airfoil-302302-5b54b4fa2820.json"
    google.google_json_key_location = "service_account_key.json"
    # 1063501002719-compute@developer.gserviceaccount.com

    #google.image_family = 'ubuntu-1604-lts'
    google.zone = "#{zone}"
    #google.tags = ['vagrantbox', 'dev']

    override.ssh.username = "john"
    override.ssh.private_key_path = "C:/Users/John/.vagrant.d/insecure_private_key"
    #override.ssh.private_key_path = "~/.ssh/google_compute_engine"
    google.zone_config "#{zone}" do |zone1f|
        zone1f.name = "testing-#{zone}"
        zone1f.image_family = "ubuntu-1604-lts"
        zone1f.machine_type = "#{machine_type}"
        zone1f.zone = "#{zone}"
        zone1f.metadata = {'custom' => 'metadata', 'testing' => 'foobarbaz'}
        #zone1f.scopes = ['bigquery', 'monitoring', 'https://www.googleapis.com/auth/compute']
        zone1f.tags = ['http', 'https', 'bbbudp', 'vnc']
    end
  end

  $PROVISION_DEBIAN = <<SCRIPT
    uname=$(uname -a)
    ver=$(cat /etc/debian_version)
    echo "== BEGIN: vagrant provisioning on '${uname}'"
    echo "== DEBIAN VERSION: ${ver}"
    echo "== UPDATING Debian repositories and packages"
    /usr/bin/apt-get update -y -qq > /dev/null 2>&1
    /usr/bin/apt-get upgrade -y -qq > /dev/null 2>&1
    extip=$(curl -s http://metadata/computeMetadata/v1/instance/network-interfaces/0/access-configs/0/external-ip -H "Metadata-Flavor: Google")
    echo "== EXTERNAL IP: ${extip}"
    echo "== APPENDING /etc/motd"
    d=$(date +%r)
    echo "# ${d}" >> /etc/motd
    echo "== cat /etc/motd"
    cat /etc/motd
SCRIPT

  $no_run_script = <<-SCRIPT
    #cat > ~/.vnc/xstartup <<EOF 
    #EOF
    #1  vncserver
    #2  vi ~/.vnc/xstartup
    #   Add 4 lines     gnome-panel &     gnome-settings-daemon &     metacity &     nautilus & 
    #3  vncserver -kill :1
    #4  vncserver :1
    # bbb-test.sh -h lingo.xxxedu.tw -n 100
    echo "*** Nginx configuation done."
SCRIPT

  $vnc_up_script = <<-SCRIPT
    echo "Current user: [`whoami`], Run vncserver and prompt password..."
    echo -e "#{vncsecret}\n#{vncsecret}" | vnc4server
    echo "    Change xsstartup & kill & run again..."
    cat << EOT >> ~/.vnc/xstartup

gnome-panel &
gnome-settings-daemon &
metacity &
nautilus &
EOT
    vncserver -kill :1
    vncserver :1
    chmod +x /home/john/bbb-test.sh
    echo "*** VNC-server configuation done & running..."
SCRIPT

  config.vm.provision "file", source: "bbb-test.sh", destination: "$HOME/"  

  config.vm.define "bbb22" do|loadtest2|
    loadtest2.vm.provision :shell, inline: <<-SHELL
      echo "Current user: [`whoami`], deal with GCP..."
      apt-get update -q && apt-get -y upgrade
      apt-get install -y ubuntu-desktop gnome-panel gnome-settings-daemon metacity nautilus gnome-terminal
      apt-get install -y sudo vnc4server moreutils 

      sudo echo "Asia/Taipei" | sudo tee /etc/timezone
      sudo dpkg-reconfigure --frontend noninteractive tzdata
      sudo timedatectl set-timezone Asia/Taipei

      chmod +x /home/john/bbb-test.sh
      # bbb-test.sh -h lingo.xxxedu.tw -n 100
      # ./bbb-test.sh -h synchronize.tnnua.edu.tw -n 2 | ts '[%Y-%m-%d %H:%M:%S]'
    SHELL
    loadtest2.vm.provision :shell, privileged: false, inline: $vnc_up_script
    loadtest2.vm.provision :shell, privileged: true, inline: $PROVISION_DEBIAN
  end

end
