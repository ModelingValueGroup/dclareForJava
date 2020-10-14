package org.modelingvalue.jdclare.swing.examples.sync;

import java.util.*;
import java.util.concurrent.*;

import org.modelingvalue.dclare.sync.*;

public class SyncConnectionHandler {
    private final SupplierAndConsumer<String> sac;
    private final List<SocketSyncConnection>  connectionList = new ArrayList<>();
    private final AsyncConnectorDaemon        asyncConnector = new AsyncConnectorDaemon();

    public SyncConnectionHandler(SupplierAndConsumer<String> sac) {
        this.sac = sac;
    }

    public List<SocketSyncConnection> getConnections() {
        return Collections.unmodifiableList(connectionList);
    }

    public void connect(String host, int port) {
        SocketSyncConnection newConnection = new SocketSyncConnection(host, port, sac) {
            @Override
            public void close() {
                super.close();
                connectionList.remove(this);
            }
        };
        try {
            connectionList.add(newConnection);
            asyncConnector.queue.put(newConnection);
        } catch (InterruptedException e) {
            throw new Error("connect to " + newConnection.getName() + " failed", e);
        }
    }

    public void disconnect(SocketSyncConnection conn) {
        conn.close();
    }

    public void disconnect() {
        getConnections().forEach(this::disconnect);
    }

    private static class AsyncConnectorDaemon extends WorkDaemon<SocketSyncConnection> {
        private final BlockingQueue<SocketSyncConnection> queue = new ArrayBlockingQueue<>(10);

        public AsyncConnectorDaemon() {
            super("AsyncConnectorDaemon");
            start();
        }

        @Override
        protected SocketSyncConnection waitForWork() throws InterruptedException {
            return queue.take();
        }

        @Override
        protected void execute(SocketSyncConnection connection) {
            connection.connect();
        }
    }
}
