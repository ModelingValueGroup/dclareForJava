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

package org.modelingvalue.jdclare.swing.examples.newton;

import static org.modelingvalue.jdclare.DClare.dclare;
import static org.modelingvalue.jdclare.DClare.dclareUU;
import static org.modelingvalue.jdclare.DClare.set;
import static org.modelingvalue.jdclare.PropertyQualifier.constant;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

import org.modelingvalue.collections.List;
import org.modelingvalue.jdclare.DStruct1;
import org.modelingvalue.jdclare.Property;
import org.modelingvalue.jdclare.swing.DComponent;
import org.modelingvalue.jdclare.swing.DToolbar;
import org.modelingvalue.jdclare.swing.DToolbarItem;
import org.modelingvalue.jdclare.swing.InputDeviceData;
import org.modelingvalue.jdclare.swing.SplitPane;
import org.modelingvalue.jdclare.swing.draw2d.ClickMode;
import org.modelingvalue.jdclare.swing.draw2d.DCanvas;
import org.modelingvalue.jdclare.swing.draw2d.DDimension;
import org.modelingvalue.jdclare.swing.draw2d.DImage;
import org.modelingvalue.jdclare.swing.draw2d.DShape;
import org.modelingvalue.jdclare.swing.draw2d.SelectionMode;

public interface BilliardPane extends SplitPane, DStruct1<BilliardUniverse> {

    @Property(key = 0)
    BilliardUniverse universe();

    default DToolbarItem item(String text, String imageLink, Consumer<ActionEvent> action) {
        return dclareUU(DToolbarItem.class, //
                set(DToolbarItem::minimumSize, dclare(DDimension.class, 50.0, 50.0)), //
                set(DToolbarItem::text, text), //
                set(DToolbarItem::action, action), //
                set(DToolbarItem::imageLink, dclare(DImage.class, imageLink))//
        );
    }

    default void appendShape(DShape s) {
        set(canvas(), DCanvas::shapes, List::append, s);
    }

    default Table canvas() {
        return (Table) leftComponent();
    }

    default void prependShape(DShape s) {
        set(canvas(), DCanvas::shapes, List::prepend, s);
    }

    @Override
    @Property(constant)
    default double resizeWeight() {
        return 1;
    }

    @Override
    default boolean vertical() {
        return false;
    }

    @Override
    default boolean disableDivider() {
        return true;
    }

    @Override
    @Property(constant)
    default DComponent leftComponent() {
        return dclareUU(Table.class, set(DCanvas::color, new Color(100, 200, 200)), set(DCanvas::mode, selectionMode()));
    }

    default ClickMode ballMode() {
        return dclareUU(ClickMode.class, set(ClickMode::action, c -> {
            InputDeviceData di = c.deviceInput();
            appendShape(dclareUU(Ball.class, set(Ball::position, di.mousePosition())));
            set(c, DCanvas::mode, selectionMode());
        }));
    }

    default SelectionMode selectionMode() {
        return dclareUU(SelectionMode.class, DCanvas.SELECTION_MODE);
    }

    @Override
    @Property(constant)
    default DComponent rightComponent() {
        return dclareUU(DToolbar.class, //
                set(DToolbar::preferredSize, dclare(DDimension.class, 40.0, 100.0)), //
                set(DToolbar::minimumSize, dclare(DDimension.class, 50.0, 100.0)), //
                set(DToolbar::items, List.of(//
                        item("Select", "selection.png", (x) -> set(canvas(), DCanvas::mode, selectionMode())), //
                        item("Ball", "circle.png", (x) -> set(canvas(), DCanvas::mode, ballMode())) //
                )));
    }

}
