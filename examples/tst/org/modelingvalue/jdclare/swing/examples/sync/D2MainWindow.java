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

package org.modelingvalue.jdclare.swing.examples.sync;

import static org.modelingvalue.jdclare.DClare.*;
import static org.modelingvalue.jdclare.PropertyQualifier.*;

import java.awt.*;
import java.util.*;

import org.modelingvalue.collections.List;
import org.modelingvalue.jdclare.*;
import org.modelingvalue.jdclare.swing.ScrollPane;
import org.modelingvalue.jdclare.swing.*;
import org.modelingvalue.jdclare.swing.draw2d.*;

public interface D2MainWindow extends DiagramEditor, DStruct1<D2Universe> {
    Color canvasBackColor = new Color(new Random().nextInt()).brighter().brighter();

    @Property(key = 0)
    D2Universe d2();

    @Override
    @Property(constant)
    default DComponent leftComponent() {
        return dclareUU(ScrollPane.class, c -> {
            DCanvas canvas = makeCanvas();
            set(c, ScrollPane::viewportView, canvas);
        });
    }

    @Override
    @Property(constant)
    default DComponent rightComponent() {
        return dclareUU(DToolbar.class, c -> {
            set(c, DToolbar::preferredSize, dclare(DDimension.class, 40.0, 100.0));
            set(c, DToolbar::minimumSize, dclare(DDimension.class, 50.0, 100.0));
            set(c, DToolbar::items, List.of(//
                    item("Select", "selection.png", x -> set(canvas(), DCanvas::mode, selectionMode())),
                    item("Rectangle", "rectangle.png", x -> set(canvas(), DCanvas::mode, rectangleMode())),
                    item("Circle", "circle.png", x -> set(canvas(), DCanvas::mode, circleMode())),
                    item("Line", "line.png", x -> set(canvas(), DCanvas::mode, lineMode()))
            ));
        });
    }

    default DCanvas makeCanvas() {
        return dclareUU(DCanvas.class, UUID.fromString("00f30107-23d1-4316-bc59-3340d1c29b43"), set(DCanvas::color, canvasBackColor));
    }

    @Property(constant)
    default SelectionMode selectionMode() {
        return dclareUU(SelectionMode.class, DCanvas.SELECTION_MODE);
    }

    @Property(constant)
    default ClickMode rectangleMode() {
        return dclareUU(ClickMode.class, e -> set(e, ClickMode::action, z -> {
            InputDeviceData di = z.deviceInput();
            DRectangle      r  = dclareUU(DRectangle.class, set(DShape::position, di.mousePosition()));
            appendShape(r);
            set(z, DCanvas::mode, selectionMode());
        }));
    }

    @Property(constant)
    default ClickMode circleMode() {
        return dclareUU(ClickMode.class, e -> set(e, ClickMode::action, c -> {
            InputDeviceData di = c.deviceInput();
            appendShape(dclareUU(DCircle.class, set(DShape::position, di.mousePosition())));
            set(c, DCanvas::mode, selectionMode());
        }));
    }

    @Property(constant)
    default LineMode lineMode() {
        return dclareUU(LineMode.class, e -> set(e, LineMode::action, (c, sel) -> {
            DShape one = sel.get(0);
            DShape two = sel.get(1);
            prependShape(dclareUU(DLine.class,
                    rule(DShape::position, l -> one.centre()),
                    rule(DLine::endPoint, l -> two.centre()),
                    rule("delete", l -> {
                        if ((pre(one::dParent) != null && one.dParent() == null)
                                || (pre(two::dParent) != null && two.dParent() == null)) {
                            clear(l);
                        }
                    })
            ));
            set(c, DCanvas::mode, selectionMode());
        }));
    }
}
