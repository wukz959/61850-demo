package com.myworld.listener;

import org.openmuc.openiec61850.ClientAssociation;
import org.openmuc.openiec61850.ClientEventListener;
import org.openmuc.openiec61850.ServerModel;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;


public interface ClientEventListenerBox extends ClientEventListener {
    void setServerModel(ServerModel serverModel);
    ServerModel getServerModel();
    void setAssociation(ClientAssociation association);
    ClientAssociation getAssociation();
    void setCallBack(Consumer<Collection<List<String>>> callback);
}
