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

package org.modelingvalue.jdclare.swing;

import java.awt.event.*;
import java.util.function.*;

import javax.swing.*;

import org.modelingvalue.jdclare.*;
import org.modelingvalue.jdclare.swing.MenuItem.*;

@Native(MenuItemNative.class)
public interface MenuItem extends DStruct3<DComponent, String, Consumer<ActionEvent>>, DComponent, DNamed {
    @Property(key = 0)
    DComponent menu();

    @Override
    @Property(key = 1)
    String name();

    @Property(key = 2)
    Consumer<ActionEvent> action();

    class MenuItemNative extends DComponentNative<MenuItem, JMenuItem> implements ActionListener {
        public MenuItemNative(MenuItem visible) {
            super(visible);
        }

        @Override
        public void init(DObject parent) {
            swing = new JMenuItem(visible.name());
            swing.addActionListener(this);
        }

        @Override
        public void exit(DObject parent) {
            if (swing != null) {
                swing.removeActionListener(this);
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Consumer<ActionEvent> action = visible.action();
            if (action != null) {
                action.accept(e);
            }
        }
    }
}
