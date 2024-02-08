//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//  (C) Copyright 2018-2024 Modeling Value Group B.V. (http://modelingvalue.org)                                         ~
//                                                                                                                       ~
//  Licensed under the GNU Lesser General Public License v3.0 (the 'License'). You may not use this file except in       ~
//  compliance with the License. You may obtain a copy of the License at: https://choosealicense.com/licenses/lgpl-3.0   ~
//  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on  ~
//  an 'AS IS' BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the   ~
//  specific language governing permissions and limitations under the License.                                           ~
//                                                                                                                       ~
//  Maintainers:                                                                                                         ~
//      Wim Bast, Tom Brus                                                                                               ~
//                                                                                                                       ~
//  Contributors:                                                                                                        ~
//      Ronald Krijgsheld ✝, Arjan Kok, Carel Bast                                                                       ~
// --------------------------------------------------------------------------------------------------------------------- ~
//  In Memory of Ronald Krijgsheld, 1972 - 2023                                                                          ~
//      Ronald was suddenly and unexpectedly taken from us. He was not only our long-term colleague and team member      ~
//      but also our friend. "He will live on in many of the lines of code you see below."                               ~
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

package org.modelingvalue.jdclare.swing;

import static org.modelingvalue.jdclare.DClare.*;

import java.awt.event.*;

import javax.swing.*;

import org.modelingvalue.collections.*;
import org.modelingvalue.jdclare.*;

@Native(DMenu.DMenuNative.class)
public interface DMenu extends DComponent, DStruct2<DComponent, String>, DNamed, MenuItem {
    @Override
    @Property(key = 0)
    DComponent menu();

    @Override
    @Property(key = 1)
    String name();

    @Property
    List<MenuItem> menuItems();

    class DMenuNative extends DComponentNative<DMenu, JMenu> implements ActionListener {
        public DMenuNative(DMenu visible) {
            super(visible);
        }

        @Override
        public void init(DObject parent) {
            swing = new JMenu(visible.name());
            super.init(parent);
        }

        @Override
        public void actionPerformed(ActionEvent arg0) {
        }

        @SuppressWarnings("unused")
        public void menuItems(List<MenuItem> pre, List<MenuItem> post) {
            swing.removeAll();
            post.forEachOrdered(i -> swing.add(((MenuItemNative) dNative(i)).swing));
        }
    }
}
