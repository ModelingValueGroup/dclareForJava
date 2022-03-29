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

import static org.modelingvalue.jdclare.DClare.*;
import static org.modelingvalue.jdclare.PropertyQualifier.*;

import java.awt.event.*;

import javax.swing.*;

import org.modelingvalue.collections.*;
import org.modelingvalue.jdclare.*;
import org.modelingvalue.jdclare.swing.DMenu.*;
import org.modelingvalue.jdclare.swing.DMenubar.*;

@Native(DMenubarNative.class)
public interface DMenubar extends DComponent, DStruct1<Frame> {
    @Property(key = 0)
    Frame frame();

    @Property(containment)
    List<DMenu> menus();

    class DMenubarNative extends DComponentNative<DMenubar, JMenuBar> implements ActionListener {
        public DMenubarNative(DMenubar visible) {
            super(visible);
        }

        @Override
        public void init(DObject parent) {
            swing = new JMenuBar();
            super.init(parent);
        }

        @Override
        public void actionPerformed(ActionEvent e) {

        }

        @SuppressWarnings("unused")
        public void menus(List<DMenu> pre, List<DMenu> post) {
            swing.removeAll();
            post.forEachOrdered(i -> swing.add(((DMenuNative) dNative(i)).swing));
        }
    }
}
