"# bbb_torrent" 

第1次執行(build)，會用zone開新機，新安裝VNC，且會比較久；開好後會看到noVNC連結。
第2次執行前，確認是己關機，再build選zone開同台，操作同第1次(但IP會不一樣)。
（Build 完成後會停在OpenVNC；VNC沒用後，build 要手動停止）
＊若是在開機狀態，只是停止build，可以用 OpenVNC 再次打開桌面。
----------------
After enter VNC, open terminal:
./bbb-test.sh -h synchronize.tnnua.edu.tw -n 2 | ts '[%Y-%m-%d %H:%M:%S]'
----------------
withCredentials([file(credentialsId: 'radiant-cycle-301903', variable: 'FILE')]) {