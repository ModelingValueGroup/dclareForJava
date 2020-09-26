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

import javax.swing.*;

import org.modelingvalue.jdclare.*;
import org.modelingvalue.jdclare.swing.SplitPane.*;

@Native(SplitPaneNative.class)
public interface SplitPane extends DComponent {
    @Property({containment, optional})
    DComponent leftComponent();

    @Property({containment, optional})
    DComponent rightComponent();

    @Property(constant)
    boolean vertical();

    @Property(constant)
    default boolean disableDivider() {
        return false;
    }

    @Property(constant)
    default double resizeWeight() {
        return 0.5;
    }

    class SplitPaneNative extends DComponentNative<SplitPane, JSplitPane> {
        public SplitPaneNative(SplitPane visible) {
            super(visible);
        }

        @Override
        public void init(DObject parent) {
            swing = new JSplitPane(visible.vertical() ? JSplitPane.VERTICAL_SPLIT : JSplitPane.HORIZONTAL_SPLIT, true);
            swing.setResizeWeight(visible.resizeWeight());
            swing.setDividerSize(visible.disableDivider() ? 0 : 2);
            swing.setContinuousLayout(true);
            super.init(parent);
        }

        @SuppressWarnings("unused")
        public void leftComponent(DComponent pre, DComponent post) {
            DComponent vp = visible.leftComponent();
            swing.setLeftComponent(vp != null ? swing(vp) : null);
            swing.invalidate();
            swing.repaint();
        }

        @SuppressWarnings("unused")
        public void rightComponent(DComponent pre, DComponent post) {
            DComponent vp = visible.rightComponent();
            swing.setRightComponent(vp != null ? swing(vp) : null);
            swing.invalidate();
            swing.repaint();
        }
    }
}
