//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
// (C) Copyright 2018-2023 Modeling Value Group B.V. (http://modelingvalue.org)                                        ~
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

import static org.modelingvalue.jdclare.DClare.*;
import static org.modelingvalue.jdclare.PropertyQualifier.*;

import java.awt.*;

import org.modelingvalue.collections.List;
import org.modelingvalue.collections.*;
import org.modelingvalue.jdclare.*;
import org.modelingvalue.jdclare.swing.Frame;
import org.modelingvalue.jdclare.swing.Panel;
import org.modelingvalue.jdclare.swing.*;
import org.modelingvalue.jdclare.swing.draw2d.*;

public interface D2Universe extends GuiUniverse {
    @Override
    default void init() {
        GuiUniverse.super.init();
        set(this, GuiUniverse::frames, Set.of(dclare(D2Frame.class, this)));
    }

    interface D2Frame extends Frame, DStruct1<D2Universe> {
        @Property(key = 0)
        D2Universe wb();

        @Override
        @Property(constant)
        default DComponent contentPane() {
            return dclare(D2Panel.class, wb());
        }

        @Override
        @Property(constant)
        default DMenubar menubar() {
            return dclare(D2Menubar.class, this);
        }

        @Override
        @Default
        @Property
        default DPoint location() {
            return dclare(DPoint.class, 50.0, 50.0);
        }

        @Override
        @Default
        @Property
        default DDimension preferredSize() {
            return dclare(DDimension.class, 1300.0, 900.0);
        }
    }

    interface D2Menubar extends DMenubar {

        @Override
        default List<DMenu> menus() {
            return List.of(dclare(DMenu.class, this, "File"));
        }
    }

    interface D2Panel extends Panel, DStruct1<D2Universe> {
        @Property(key = 0)
        D2Universe d2();

        @Override
        @Property(constant)
        default Map<DComponent, Object> content() {
            return Map.<DComponent, Object> of()//
                    .put(dclare(D2MainWindow.class, d2()), BorderLayout.CENTER);
        }

        @Override
        default LayoutManager layoutManager() {
            return new BorderLayout();
        }
    }
}
