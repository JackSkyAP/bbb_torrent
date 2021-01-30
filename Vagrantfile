# -*- mode: ruby -*-
# vi: set ft=ruby :

# Changhua County, # https://cloud.google.com/compute/docs/regions-zones?hl=zh-tw
# HongKong ==> asia-east2-a, Singapore ==> asia-southeast1-a, Changhua County ==> asia-east1-c
zone=ENV.fetch('ZONE', "asia-east1-a")
#machine_type="n1-standard-4 # https://cloud.google.com/compute/docs/machine-types
machine_type=ENV.fetch('MACHINETYPE', "n1-standard-4")
GCPPROJECTID=ENV.fetch('GCP_PROJECTID', 'GOOGLE_PROJECT_ID')
GCPJSONPATHNAME=ENV.fetch('GCP_CREDENTIAL', 'GOOGLE_APPLICATION_CREDENTIALS')

vm_username=ENV.fetch('SSH_USERNAME', 'skyap')
vm_private_key=ENV.fetch('SSH_PRIVATE_KEY', "~/.vagrant.d/insecure_private_key")
vncsecret=ENV.fetch('VNNCSECRET', "passw0rd")

dry_run=ENV.fetch('DRYRUN', false)

puts "GCPPROJECTID: #{GCPPROJECTID}"
puts "JSONKEYPATH: #{GCPJSONPATHNAME}"
puts "dry_run: #{dry_run.inspect}"
puts "vm_username: #{vm_username}"
puts "private_key: #{vm_private_key}"

Vagrant.configure("2") do |config|
  config.vm.box = "google/gce"
  #config.ssh.insert_key=false

  config.vm.provider :google do |google, override|

    google.google_project_id = "#{GCPPROJECTID}"
    #google.google_json_key_location = "./radiant-cycle-301903-d88acd716fd4.json"
    google.google_json_key_location = ENV.fetch('GCP_CREDENTIAL', "./radiant-cycle-301903-d88acd716fd4.json")

    google.name = "testing-#{zone}"
    google.image_family = 'ubuntu-1604-lts'
    google.machine_type = "#{machine_type}"
    google.zone = "#{zone}"
    #google.metadata = {'custom' => 'metadata', 'username' => "#{vm_username", 'privage_key' => "#{vm_private_key}" }
    google.metadata = {'custom' => 'metadata', 'username' => "#{vm_username}", 'privage_key' => "#{vm_private_key}" }
    #google.metadata = {'custom' => 'metadata', 'testing' => 'foobarbaz'}
    google.tags = ['http-server', 'https-server', 'vnc']

    override.ssh.username = "#{vm_username}"
    override.ssh.private_key_path = "#{vm_private_key}"
  end

  $PROVISION_DEBIAN = <<SCRIPT
    echo "Current user: [`whoami`], Provision_Debian..."
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
    chmod +x "/home/#{vm_username}/bbb-test.sh"
    echo "*** VNC-server configuation done & running..."
SCRIPT

  config.vm.provision "file", source: "bbb-test.sh", destination: "$HOME/"  

  config.vm.define "bbb22" do|loadtest2|
    loadtest2.vm.provision :shell, inline: <<-SHELL
      echo "Current user: [`whoami`], deal with GCP..."
      apt-get update -q && apt-get -y upgrade
      #echo "Asia/Taipei" > /etc/timezone
      sudo ln -snf /usr/share/zoneinfo/Asia/Taipei /etc/localtime
      dpkg-reconfigure --frontend noninteractive tzdata
    SHELL
    if !dry_run    
      loadtest2.vm.provision :shell, inline: <<-SHELL
        echo "Current user: [`whoami`], deal with Desktop for VNC..."
        apt-get install -y ubuntu-desktop gnome-panel gnome-settings-daemon metacity nautilus gnome-terminal
        apt-get install -y sudo vnc4server moreutils 

        chmod +x "/home/#{vm_username}/bbb-test.sh"
        # bbb-test.sh -h lingo.xxxedu.tw -n 100
        # ./bbb-test.sh -h synchronize.tnnua.edu.tw -n 2 | ts '[%Y-%m-%d %H:%M:%S]'
      SHELL
      loadtest2.vm.provision :shell, privileged: false, inline: $vnc_up_script
    end
    loadtest2.vm.provision :shell, privileged: true, inline: $PROVISION_DEBIAN
  end

end
