package com.myworld.listener;

import lombok.extern.slf4j.Slf4j;
import org.openmuc.openiec61850.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
public class EventListener  implements ClientEventListenerBox {
    private ServerModel serverModel;
    private ClientAssociation association;
    private Consumer<Collection<List<String>>> callback;

    @Override
    public void setServerModel(ServerModel serverModel) {
        this.serverModel = serverModel;
    }

    @Override
    public ServerModel getServerModel() {
        return this.serverModel;
    }

    @Override
    public void setAssociation(ClientAssociation association) {
        this.association = association;
    }

    @Override
    public ClientAssociation getAssociation() {
        return this.association;
    }

    @Override
    public void setCallBack(Consumer<Collection<List<String>>> callback) {
        this.callback = callback;
    }

    @Override
    public void newReport(Report report) {
        int index = 0;
        List<FcModelNode> values = report.getValues();
        Collection<List<String>> collect = new ArrayList<>();

        for (FcModelNode reportedDataSetMember : values) {
            Collection<ModelNode> children = reportedDataSetMember.getChildren();
            if (children==null) return;
            int i=0;
            List<String> list = new ArrayList<>();
            for (ModelNode childNode : children) {
                if (i==0){
                    String[] res = childNode.toString().split("\n");
//                    String prefix = extractBeforeSecondDot(res[0]);
//                    String desc = prefix + ".dU";
//                    BdaUnicodeString modelNode = (BdaUnicodeString) serverModel.findModelNode(desc, Fc.DC);
//                    String name = " ";
//                    name += new String(modelNode.getValue());
//                    if (name!=null){
//                        str+=name + "  ";
//                    }
                    for (String val : res) {
                        list.add(val);
                    }
                    i++;
                    continue;
                }
                list.add(childNode.toString());

            }

            List<BdaReasonForInclusion> reasonCodes = report.getReasonCodes();
            if ( reasonCodes!= null) {
                list.add("reason: " + reasonCodes.get(index).toString());//reason
            }
            collect.add(list);
//            appendFile("./ret.txt",str + "\n");
            index++;
        }
        this.callback.accept(collect);
    }


    @Override
    public void associationClosed(IOException e) {
        log.debug("Received connection closed signal. Reason: ");
        if (!e.getMessage().isEmpty()) {
            log.debug(e.getMessage());
        } else {
            log.debug("unknown");
        }
    }
//    static void appendFile(String filePath, String textToAppend) {
//        try (FileWriter writer = new FileWriter(filePath, true)) {
//            writer.write(textToAppend + "\n");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
