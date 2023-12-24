package com.github.kettoleon.hive4j.clients.ssh;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SshPortForwarding implements Runnable {

    private final JSch jsch;
    private final String user;
    private final String host;
    private final int lport;
    private final String rhost;
    private final int rport;
    private HardcodedUserInfo userInfo;
    private Session session;

    public SshPortForwarding(JSch jsch, String user, String password, String host, int lport, String rhost, int rport) {
        this.jsch = jsch;
        this.user = user;
        this.host = host;
        this.lport = lport;
        this.rhost = rhost;
        this.rport = rport;
        userInfo = new HardcodedUserInfo(password);
    }

    public void run() {
        while (true) {
            try {
                session = jsch.getSession(user, host, 22);
                session.setUserInfo(userInfo);
                session.setServerAliveInterval(10000);
            } catch (Exception e) {
                log.warn("Failed to create ssh session to {}@{}", user, host, e);
                return;
            }
            while (!session.isConnected()) {
                try {
                    session.connect();
                    int assignedPort = session.setPortForwardingL(lport, rhost, rport);
                    log.info("Port forwarding active! localhost:" + assignedPort + " -> " + rhost + ":" + rport);
                } catch (Exception e) {
                    log.warn("Failed to connect to {}@{}, retrying... ({})", user, host, e.getMessage());
                    try {
                        Thread.sleep(5000l);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
            while (session.isConnected()) {
                try {
                    Thread.sleep(1000l);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            log.info("Connection to {}@{} lost. Reconnecting...", user, host);
        }
    }

    public boolean isConnected(){
        return session != null && session.isConnected();
    }

    public static class HardcodedUserInfo implements UserInfo {

        private String password;

        public HardcodedUserInfo(String password) {
            this.password = password;
        }

        @Override
        public String getPassword() {
            return password;
        }

        @Override
        public String getPassphrase() {
            return null;
        }

        @Override
        public boolean promptPassword(String s) {
            log.info(s);
            return true;
        }

        @Override
        public boolean promptPassphrase(String s) {
            log.info(s);
            return true;
        }

        @Override
        public boolean promptYesNo(String s) {
            log.info(s);
            return true;
        }

        @Override
        public void showMessage(String s) {
            log.info(s);
        }
    }

}
