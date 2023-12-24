package com.github.kettoleon.hive4j.clients.ssh;

import com.jcraft.jsch.JSch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SshPortForwardingFactory {

    @Autowired
    private JSch jSch;
    public void startPortForwardingFromConfigIfNeeded(Map<String, Object> config){

        //TODO so ugly, use jackson or something
        if(config.containsKey("ssh")){
            Map<String, Object> ssh = (Map<String, Object>) config.get("ssh");
            SshPortForwarding sshpf = new SshPortForwarding(
                    jSch,
                    ssh.get("user") + "",
                    ssh.get("pass") + "",
                    ssh.get("host") + "",
                    Integer.parseInt(ssh.get("localPort") + ""),
                    ssh.get("remoteHost") + "",
                    Integer.parseInt(ssh.get("remotePort") + "")
            );
            new Thread(sshpf).start();

            while(!sshpf.isConnected()){
                try {
                    Thread.sleep(100l);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

        }

    }

}
