//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
// (C) Copyright 2018-2020 Modeling Value Group B.V. (http://modelingvalue.org)                                        ~
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

import javax.swing.*;

import org.modelingvalue.collections.*;
import org.modelingvalue.jdclare.*;
import org.modelingvalue.jdclare.swing.MenuItem.*;
import org.modelingvalue.jdclare.swing.PopupMenu.*;

@SuppressWarnings("unused")
@Native(PopupMenuNative.class)
public interface PopupMenu extends DComponent {
    @Property(containment)
    List<MenuItem> items();

    class PopupMenuNative extends DComponentNative<PopupMenu, JPopupMenu> {
        public PopupMenuNative(PopupMenu visible) {
            super(visible);
        }

        @Override
        public void init(DObject parent) {
            swing = new JPopupMenu();
        }

        @Override
        public void exit(DObject parent) {
        }

        public void items(List<MenuItem> pre, List<MenuItem> post) {
            swing.removeAll();
            post.forEachOrdered(i -> swing.add(((MenuItemNative) dNative(i)).swing));
        }
    }
}
