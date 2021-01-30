def CIEMAIL="rd@click-ap.com"
properties([
    parameters([
    string(name: 'REMOTE_IP', trim: true, defaultValue: '35.201.203.126', description: "遠端的IP" ),
    choice(name: 'LOCAL_IP', choices: ['192.168.50', '192.168.1'], description: '公司的IP')
    ])
])
DRYRUN=false
def DRYRUN=Boolean.valueOf(DRYRUN)
def CIHOST= "${LOCAL_IP}.xxx"
echo """
JOB_NAME(BASE): ${JOB_NAME}(${JOB_BASE_NAME})
JOB_URL:        ${JOB_URL}

"""

//def REMOTE_IP
node ('master') {

    stage('OpenVNC') {
        // def CIHOSTNAME=$(echo ${BUILD_URL} | awk -F[/:] '{print $4}')
        def CIHOSTNAME=InetAddress.localHost.canonicalHostName
        // echo "CIHOSTNAME: ${CIHOSTNAME}"
        def CIHOSTADDRESS=InetAddress.localHost.hostAddress 
        //echo "CIHOSTADDRESS: ${CIHOSTADDRESS}"
        echo "REMOTE_IP:      ${REMOTE_IP}"
    
        if (!DRYRUN) {
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
            echo "   正要啟用 websockify ...(啟用後這個Job會一直監聽，直到你把它停止。)"
            sh "websockify --web=/usr/share/novnc/ --cert=/etc/pki/tls/certs/novnc.pem ${CIHOSTADDRESS}:${freePort} ${REMOTE_IP}:5901"
            // abort...
        }
    }
}

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