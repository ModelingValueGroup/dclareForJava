//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
// (C) Copyright 2018-2019 Modeling Value Group B.V. (http://modelingvalue.org)                                        ~
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

import static org.modelingvalue.jdclare.PropertyQualifier.*;

import java.awt.*;

import javax.swing.*;

import org.modelingvalue.collections.*;
import org.modelingvalue.jdclare.*;
import org.modelingvalue.jdclare.swing.Panel.*;

@SuppressWarnings("unused")
@Native(PanelNative.class)
public interface Panel extends DComponent {
    @Property
    Map<DComponent, Object> content();

    @Property(constant)
    LayoutManager layoutManager();

    @Property(containment)
    default Set<DComponent> components() {
        return content().toKeys().toSet();
    }

    class PanelNative extends DComponentNative<Panel, JPanel> {
        public PanelNative(Panel visible) {
            super(visible);
        }

        @Override
        public void init(DObject parent) {
            swing = new JPanel(visible.layoutManager());
            super.init(parent);
        }

        @Override
        public void exit(DObject parent) {
            super.exit(parent);
        }

        public void content(Map<DComponent, Object> pre, Map<DComponent, Object> post) {
            pre.removeAllKey(post).forEachOrdered(ct -> swing.remove(swing(ct.getKey())));
            post.removeAllKey(pre).forEachOrdered(ct -> swing.add(swing(ct.getKey()), ct.getValue()));
            swing.invalidate();
            swing.repaint();
        }
    }
}
