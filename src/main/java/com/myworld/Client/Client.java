package com.myworld.Client;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Collection;
import com.myworld.listener.ClientEventListenerBox;
import lombok.extern.slf4j.Slf4j;
import org.openmuc.openiec61850.*;
import static com.myworld.Client.URCBStarter.startURcb;

@Slf4j
public class Client {
    private InetAddress remoteAddress;
    private int remotePort = 102;
    private String authenticationParameter;
    private InetAddress localAdress;
    private int localPort = 0;
    private ClientEventListenerBox eventListener;

    private volatile ClientAssociation association;
    private ServerModel serverModel;
    static int index = 0;

    private Client(Builder builder) {
        // 设置所有必要和可选的成员变量
        this.remoteAddress = builder.remoteAddress;
        this.remotePort = builder.remotePort;
        this.authenticationParameter = builder.authenticationParameter;
        this.localAdress = builder.localAdress;
        this.localPort = builder.localPort;
        this.eventListener = builder.eventListener;
    }
    public static class Builder {
        private final InetAddress remoteAddress;
        private final InetAddress localAdress;
        private final ClientEventListenerBox eventListener;

        // optional parameter
        private int remotePort = 102;
        private String authenticationParameter;
        private int localPort = 0;

        // constructor
        public Builder(InetAddress remoteAddress, InetAddress localAdress, ClientEventListenerBox eventListener) {
            this.remoteAddress = remoteAddress;
            this.localAdress = localAdress;
            this.eventListener = eventListener;
        }

        public Builder remotePort(int remotePort) {
            this.remotePort = remotePort;
            return this;
        }
        public Builder authenticationParameter(String authenticationParameter) {
            this.authenticationParameter = authenticationParameter;
            return this;
        }
        public Builder localPort(int localPort) {
            this.localPort = localPort;
            return this;
        }

        public Client build() {
            return new Client(this);
        }
    }

    public void connected() {
        ClientSap clientSap = new ClientSap();

        try {
            log.debug("remote address: " + remoteAddress + " remote port: " + remotePort);
            log.debug("local address: " + localAdress + " local port: " + localPort);
            association = clientSap.associate(remoteAddress, remotePort,
                    authenticationParameter, localAdress, localPort, eventListener);
            eventListener.setAssociation(association);
        } catch (IOException e) {
            log.debug("Exception:  " + e);
            log.debug("Unable to connect to remote host.");
            return;
        }
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                association.close();
            }
        });
        log.debug("successfully connected");
        log.debug("retrieving model...");
        try {
            serverModel = association.retrieveModel();
            eventListener.setServerModel(serverModel);
        } catch (ServiceError e) {
            log.debug("Service error: " + e.getMessage());
            return;
        } catch (IOException e) {
            log.debug("Fatal error: " + e.getMessage());
            return;
        }
        log.debug("successfully read model");
//        association.getAllDataValues();

        log.debug("try to get urcb data...");
        Collection<Urcb> urcbs = serverModel.getUrcbs();
        urcbs.forEach(item -> {
            String ref = item.getReference().toString();
            Urcb urcb = serverModel.getUrcb(ref);
            try {
                startURcb(association, urcb,"rcb"+index++);
            } catch (ServiceError serviceError) {
                log.debug("Service error -  " + urcb.getReference().toString() + " :" + serviceError.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

//    static void appendFile(String filePath, String textToAppend) {
//        try (FileWriter writer = new FileWriter(filePath, true)) {
//            writer.write(textToAppend + "\n");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
