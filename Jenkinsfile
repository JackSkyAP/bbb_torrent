def CIEMAIL="rd@click-ap.com"
properties([
    parameters([
    string(name: 'VNNCSECRET', trim: true, defaultValue: 'passw0rd', description: '''
VNC 進入密碼
The VNC login password?
vncsecret'''),
    string(name: 'PROJECTID', trim: true, defaultValue: 'radiant-cycle-301903', description: "GCP 專案ID" ),
    string(name: 'ZONE', trim: true, defaultValue: 'asia-east1-c', description: '''選擇開機的區域:
HongKong => asia-east2-a
Singapore => asia-southeast1-a
Changhua County ==> asia-east1-a/b/c
# 其它區域: https://cloud.google.com/compute/docs/regions-zones?hl=zh-tw'''),
    choice(name: 'MACHINETYPE', choices: ['n1-standard-4', 'n1-standard-8','n1-standard-1'], description: '''機器的等級:
GCP machine-type: https://cloud.google.com/compute/docs/machine-types'''),
    booleanParam(name: 'DRYRUN', defaultValue: false, description: 'DRYRUN')
    ])
])

def DRYRUN=Boolean.valueOf(DRYRUN)
def GCP_PROJECT_ID=PROJECTID

echo """
JOB_NAME(BASE): ${JOB_NAME}(${JOB_BASE_NAME})
JOB_URL:        ${JOB_URL}

SECRET:         ${VNNCSECRET}
GCP-PROJECT:    ${GCP_PROJECT_ID}
ZONE:           ${ZONE}
MACHINETYPE:    ${MACHINETYPE}
DRYRUN:         ${DRYRUN}
"""

//echo "PWD: ${PWD}"
def REMOTE_IP
def REMOTE_PORT='5901'
node ('master') {
    stage('Checkout') {
        checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/JackSkyAP/bbb_torrent.git']]])
    }
    stage('Vagrant up') {
        withEnv([
            "DRYRUN=${DRYRUN}",
            "SSH_USERNAME=skyap",
            "SSH_PRIVATE_KEY=~/.vagrant.d/insecure_private_key",
            "GCP_PROJECTID=${GCP_PROJECT_ID}"]) {
            withCredentials([file(credentialsId: GCP_PROJECT_ID, variable: 'FILE')]) {
                try{
                    if (!fileExists('google_json_key.json')) {
                        sh "cp ${FILE} ./google_json_key.json"
                    }
                    sh "GCP_CREDENTIAL=${FILE} vagrant up --provider=google --provision"
                }
                catch (e) {
                    echo 'Vagrant up failed: ' + e.toString()
                    //sh "GCP_CREDENTIAL=${FILE} vagrant up --provision"
                }
            }
            //export REMOTE_IP=$(vagrant ssh-config | grep -P '(^|\s)HostName' |  awk '{print $2}')"
            def HostNameStr = sh(
                script: "vagrant ssh-config | grep -P 'HostName' ", 
                returnStdout: true).trim ()
            HostName = HostNameStr.split(/\s/);
            REMOTE_IP=HostName[1].trim()
            echo "REMOTE_IP:      ${REMOTE_IP}"
        }
    }
    
    stage('OpenVNC') {
        // def CIHOSTNAME=$(echo https://www.moodle.tw | awk -F[/:] '{print $4}')
        def CIHOSTNAME=InetAddress.localHost.canonicalHostName
        def CIHOSTADDRESS=InetAddress.localHost.hostAddress 
        echo """CIHOSTNAME: ${CIHOSTNAME}
CIHOSTADDRESS: ${CIHOSTADDRESS}
REMOTE_IP:      ${REMOTE_IP}"""
        if (DRYRUN) {
            echo "Just Dry-Run..."
        }
        else {
            int freePort = findFreePort();
            def btn_html="http://${CIHOSTADDRESS}:${freePort}"
            def btn_caption="Please open: ${REMOTE_IP}:5901 by vnc-viewer ${CIHOSTADDRESS}:${freePort}"
            echo "Please open vnc-viewer by: http://${CIHOSTADDRESS}:${freePort} 請點選連結開啟VNC桌面。"
            //echo "<a href=\"${btn_html}\" target=\"_blank\"><button>${btn_caption}</button></a>"
            echo "   進入Ubuntu後右鍵開啟 terminal, type: ./bbb-test.sh -h bbb.moodle.edu.tw -n 2 | ts '[%Y-%m-%d %H:%M:%S]' "
            echo "   使用完畢後, 請記得刪除GCP以免造成不必要費用!!"
            mail to: "${CIEMAIL}", subject: "遠端桌面 is ready...",
              body: """遠端${REMOTE_IP}桌面己開啟, 請由 http://${CIHOSTADDRESS}:${freePort} 進入; 使用完畢後, 請記得刪除GCP以免造成不必要費用!! 
                Please go to ${BUILD_URL}console and verify the build. 
                NODE_NAME: ${NODE_NAME}.
                JOB_NAME: ${JOB_NAME},
                JOB_BASE_NAME: ${JOB_BASE_NAME}, 
                WORKSPACE: ${WORKSPACE}.
                JOB_URL: ${JOB_URL}.
            """
            sh "websockify --web=/usr/share/novnc/ --cert=/etc/pki/tls/certs/novnc.pem ${CIHOSTADDRESS}:${freePort} ${REMOTE_IP}:${REMOTE_PORT}"
        }
    }
}

/*
第1次執行(build)，會用zone開新機，新安裝VNC，且會比較久；開好後會看到noVNC連結。
第2次執行前，確認是己關機，再build選zone開同台，操作同第1次(但IP會不一樣)。
（Build 完成後會停在OpenVNC；VNC沒用後，build 要手動停止）
＊若是在開機狀態，只是停止build，可以用 OpenVNC 再次打開桌面。
----------------
After enter VNC, open terminal:
./bbb-test.sh -h synchronize.tnnua.edu.tw -n 2 | ts '[%Y-%m-%d %H:%M:%S]'
----------------
withCredentials([file(credentialsId: 'radiant-cycle-301903', variable: 'FILE')]) {
*/

@NonCPS
def findFreePort() {
    ServerSocket socket= null;
    try
    { socket= new ServerSocket(0); return socket.getLocalPort(); }
    catch (IOException e) { 
    } finally {
    	if (socket != null) {
    		try
    		{ socket.close(); }
    		catch (IOException e) {
    		}
    	}
    }
    return -1;
}