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
import static org.modelingvalue.jdclare.PropertyQualifier.*;

import javax.swing.*;

import org.modelingvalue.collections.*;
import org.modelingvalue.jdclare.*;
import org.modelingvalue.jdclare.swing.TabbedPane.*;

@SuppressWarnings("unused")
@Native(TabbedPaneNative.class)
public interface TabbedPane extends DComponent {
    @Property
    Map<String, DComponent> tabs();

    @Property(containment)
    default Set<DComponent> containedTabs() {
        return tabs().toValues().asSet();
    }

    class TabbedPaneNative extends DComponentNative<TabbedPane, JTabbedPane> {
        public TabbedPaneNative(TabbedPane visible) {
            super(visible);
        }

        @Override
        public void init(DObject parent) {
            swing = new JTabbedPane();
            super.init(parent);
        }

        public void tabs(Map<String, DComponent> pre, Map<String, DComponent> post) {
            for (int i = 0; i < swing.getTabCount(); i++) {
                String title = swing.getTitleAt(i);
                if (post.get(title) == null) {
                    swing.remove(i--);
                } else {
                    post = post.removeKey(title);
                }
            }
            for (Entry<String, DComponent> e : post) {
                swing.addTab(e.getKey(), ((DComponentNative<?, ?>) dNative(e.getValue())).swing);
            }
        }
    }
}
