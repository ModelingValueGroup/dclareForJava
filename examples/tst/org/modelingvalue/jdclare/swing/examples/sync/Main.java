package org.modelingvalue.jdclare.swing.examples.sync;

import org.modelingvalue.dclare.sync.*;
import org.modelingvalue.jdclare.*;

public class Main {
    public static void main(String[] args) {
        DClare<D2Universe>          dclare = DClare.start(D2Universe.class, false);
        SupplierAndConsumer<String> a      = new DeltaAdaptor<>("sync", dclare.universeTransaction(), new SyncSerializationHelper());
        @SuppressWarnings("unused")
        ConnectionDialog dia = new ConnectionDialog(new SyncConnectionHandler(a));
        dclare.waitForEnd();
    }
}
