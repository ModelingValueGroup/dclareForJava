//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
// (C) Copyright 2018-2022 Modeling Value Group B.V. (http://modelingvalue.org)                                        ~
//                                                                                                                     ~
// Licensed under the GNU Lesser General Public License v3.0 (the 'License'). You may not use this file except in      ~
// compliance with the License. You may obtain a copy of the License at: https://choosealicense.com/licenses/lgpl-3.0  ~
// Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on ~
// an 'AS IS' BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the  ~
// specific language governing permissions and limitations under the License.                                          ~
//                                                                                                                     ~
// Maintainers:                                                                                                        ~
//     Wim Bast, Tom Brus, Ronald Krijgsheld                                                                           ~
// Contributors:                                                                                                       ~
//     Arjan Kok, Carel Bast                                                                                           ~
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

package org.modelingvalue.jdclare.swing.examples.sync;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.modelingvalue.dclare.sync.SocketSyncConnection;
import org.modelingvalue.dclare.sync.SyncConnectionHandler;

import com.intellij.uiDesigner.core.*;

public class ConnectionDialog {
    private JButton                     clientConnectButton;
    private JTextField                  clientHostField;
    private JSpinner                    clientPortSpinner;
    private JTable                      clientConnectionTable;
    private JLabel                      clientInfoLabel;
    //
    private JPanel                      root;
    private JLabel                      clientHostLabel;
    private JLabel                      clientPortLabel;
    private JButton                     disconnectButton;
    //
    private final SyncConnectionHandler connectionHandler;
    private final ConnectionModel       connModel = new ConnectionModel();
    private SocketSyncConnection        selectedConnection;

    @SuppressWarnings("serial")
    public static class ConnectionModel extends AbstractTableModel {
        private final String[]                   columnNames = {"endpoint", "#rcv-bytes", "#send-bytes", "#rcv-∂", "#send-∂",};
        private final List<SocketSyncConnection> connections = new ArrayList<>();

        @Override
        public int getRowCount() {
            return connections.size();
        }

        @Override
        public String getColumnName(int col) {
            return columnNames[col];
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public Object getValueAt(int row, int col) {
            SocketSyncConnection conn = connections.get(row);
            switch (col) {
            case 0:
                return conn.getName();
            case 1:
                return conn.getNumInBytes();
            case 2:
                return conn.getNumOutBytes();
            case 3:
                return conn.getNumInPackages();
            case 4:
                return conn.getNumOutPackages();
            default:
                return "??";
            }
        }

        @Override
        public Class<?> getColumnClass(int col) {
            return col == 0 ? String.class : Integer.class;
        }

        public SocketSyncConnection getConnectionAt(int row) {
            return connections.get(row);
        }

        public void updateRows(List<SocketSyncConnection> l) {
            if (l.equals(connections)) {
                fireTableRowsUpdated(0, l.size() - 1);
            } else {
                connections.clear();
                connections.addAll(l);
                fireTableDataChanged();
            }
        }
    }

    @SuppressWarnings("serial")
    DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
                                                @Override
                                                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                                                    super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                                                    setHorizontalAlignment(1 <= column ? JLabel.RIGHT : JLabel.LEFT);
                                                    setBackground(Color.lightGray);
                                                    setFont(clientConnectionTable.getFont());
                                                    return this;
                                                }
                                            };

    @SuppressWarnings("serial")
    DefaultTableCellRenderer cellRenderer   = new DefaultTableCellRenderer() {
                                                @Override
                                                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                                                    if (value instanceof Integer) {
                                                        value = String.format("%,d", value);
                                                    }
                                                    super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                                                    setHorizontalAlignment(1 <= column ? JLabel.RIGHT : JLabel.LEFT);
                                                    if (row == -1) {
                                                        super.setBackground(Color.lightGray);
                                                    }
                                                    setFont(clientConnectionTable.getFont());
                                                    return this;
                                                }
                                            };

    public ConnectionDialog(SyncConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;

        JFrame frame = new JFrame("Connection");
        frame.setContentPane(root);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setAlwaysOnTop(true);
        frame.setLocationRelativeTo(null);
        frame.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                for (Frame frame : Frame.getFrames()) {
                    frame.toFront();
                    frame.repaint();
                }
            }
        });
        clientConnectButton.addActionListener(e -> connectClient());
        disconnectButton.addActionListener(e -> disconnectClient());
        clientHostField.setText("meet.modelingvalue.nl");
        clientPortSpinner.setValue(55055);
        clientPortSpinner.addChangeListener(e -> update());

        clientConnectionTable.setModel(connModel);
        clientConnectionTable.getTableHeader().setDefaultRenderer(headerRenderer);
        clientConnectionTable.setDefaultRenderer(String.class, cellRenderer);
        clientConnectionTable.setDefaultRenderer(Number.class, cellRenderer);
        clientConnectionTable.getSelectionModel().addListSelectionListener(e -> {
            int[] sel = clientConnectionTable.getSelectionModel().getSelectedIndices();
            if (sel.length == 0) {
                selectedConnection = null;
            } else {
                selectedConnection = connModel.getConnectionAt(sel[0]);
            }
            update();
        });

        update();
        frame.pack();
        frame.setVisible(true);

        new Timer(200, e -> update()).start();
    }

    private void connectClient() {
        String host = clientHostField.getText();
        Integer port = (Integer) clientPortSpinner.getValue();
        connectionHandler.connect(host, port);
        update();
    }

    private void disconnectClient() {
        connectionHandler.disconnect(selectedConnection);
        update();
    }

    private void update() {
        List<SocketSyncConnection> connections = connectionHandler.getConnections().toMutable();
        connModel.updateRows(connections);

        boolean busy = connections.stream().anyMatch(SocketSyncConnection::isConnecting);
        clientConnectButton.setEnabled(!busy);
        clientHostLabel.setEnabled(!busy);
        clientHostField.setEnabled(!busy);
        clientPortLabel.setEnabled(!busy);
        clientPortSpinner.setEnabled(!busy);

        SocketSyncConnection sel = this.selectedConnection;
        disconnectButton.setEnabled(!busy && sel != null);
        disconnectButton.setText(sel == null ? "Disconnect" : "Disconnect " + sel.getName());
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    {
        // GUI initializer generated by IntelliJ IDEA GUI Designer
        // >>> IMPORTANT!! <<<
        // DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        root = new JPanel();
        root.setLayout(new GridLayoutManager(4, 5, new Insets(0, 0, 0, 0), -1, -1));
        root.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        clientHostLabel = new JLabel();
        clientHostLabel.setText("host:");
        root.add(clientHostLabel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        clientHostField = new JTextField();
        root.add(clientHostField, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(200, -1), null, 0, false));
        clientPortLabel = new JLabel();
        clientPortLabel.setText("port:");
        root.add(clientPortLabel, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        clientPortSpinner = new JSpinner();
        root.add(clientPortSpinner, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, -1), new Dimension(100, -1), new Dimension(100, -1), 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        root.add(panel1, new GridConstraints(2, 0, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 100), null, null, 0, false));
        panel1.setBorder(BorderFactory.createTitledBorder(null, "connections", TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION, null, null));
        final JScrollPane scrollPane1 = new JScrollPane();
        Font scrollPane1Font = UIManager.getFont("TableHeader.font");
        if (scrollPane1Font != null) {
            scrollPane1.setFont(scrollPane1Font);
        }
        panel1.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        clientConnectionTable = new JTable();
        clientConnectionTable.setBackground(new Color(-2605));
        clientConnectionTable.setEnabled(true);
        clientConnectionTable.setFillsViewportHeight(false);
        Font clientConnectionTableFont = UIManager.getFont("TableHeader.font");
        if (clientConnectionTableFont != null) {
            clientConnectionTable.setFont(clientConnectionTableFont);
        }
        clientConnectionTable.setGridColor(new Color(-2763307));
        clientConnectionTable.setIntercellSpacing(new Dimension(2, 2));
        clientConnectionTable.setSelectionBackground(new Color(-13310218));
        clientConnectionTable.setSelectionForeground(new Color(-16777216));
        scrollPane1.setViewportView(clientConnectionTable);
        clientConnectButton = new JButton();
        clientConnectButton.setText("Connect");
        root.add(clientConnectButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        clientInfoLabel = new JLabel();
        Font clientInfoLabelFont = UIManager.getFont("IconButton.font");
        if (clientInfoLabelFont != null) {
            clientInfoLabel.setFont(clientInfoLabelFont);
        }
        clientInfoLabel.setText("client-info");
        root.add(clientInfoLabel, new GridConstraints(1, 0, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 20), null, null, 1, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        root.add(panel2, new GridConstraints(3, 0, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        disconnectButton = new JButton();
        disconnectButton.setText("Disconnect");
        panel2.add(disconnectButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel2.add(spacer1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return root;
    }

}
