package com.myworld.Client;

import org.openmuc.openiec61850.BdaBoolean;
import org.openmuc.openiec61850.ClientAssociation;
import org.openmuc.openiec61850.ServiceError;
import org.openmuc.openiec61850.Urcb;
import java.io.IOException;
import java.util.List;

public final class URCBStarter {
//    private static int IntgPd = 5000;
    static void startURcb(ClientAssociation association, Urcb urcb, String rcbName) throws ServiceError, IOException {
        association.getRcbValues(urcb);
        BdaBoolean resv = (BdaBoolean) urcb.getChild("Resv");
        association.reserveUrcb(urcb);
        association.getDataValues(resv);

        urcb.getRptId().setValue(rcbName);
        urcb.getTrgOps().setGeneralInterrogation(false);
        urcb.getTrgOps().setDataUpdate(false);
        urcb.getTrgOps().setDataChange(false);

        association.setRcbValues(urcb, true, true, true, true, true, true, true, true);
        association.getRcbValues(urcb);
        association.reserveUrcb(urcb);
        association.cancelUrcbReservation(urcb);
        association.enableReporting(urcb);
        association.startGi(urcb);

        urcb.getTrgOps().setGeneralInterrogation(true);
        urcb.getTrgOps().setDataChange(true);
        urcb.getIntgPd().setValue(5000);

        association.disableReporting(urcb);

        List<ServiceError> serviceErrors =
                association.setRcbValues(urcb,
                        false, false, false, false, true,
                        true, false, false);
        association.enableReporting(urcb);
        association.startGi(urcb);
    }
}
