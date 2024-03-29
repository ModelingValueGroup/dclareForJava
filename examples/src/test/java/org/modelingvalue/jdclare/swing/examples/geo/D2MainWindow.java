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

package org.modelingvalue.jdclare.swing.examples.geo;

import static org.modelingvalue.jdclare.DClare.*;
import static org.modelingvalue.jdclare.PropertyQualifier.*;

import java.awt.*;
import java.awt.event.*;
import java.util.function.*;

import org.modelingvalue.collections.List;
import org.modelingvalue.collections.*;
import org.modelingvalue.jdclare.*;
import org.modelingvalue.jdclare.swing.ScrollPane;
import org.modelingvalue.jdclare.swing.*;
import org.modelingvalue.jdclare.swing.draw2d.*;

public interface D2MainWindow extends SplitPane, DStruct1<D2Universe> {

    @Property(key = 0)
    D2Universe d2();

    @Override
    @Property(constant)
    default DComponent leftComponent() {
        return dclareUU(CirlcesAndSquaresEditor.class);
    }

    @Override
    @Property(constant)
    default DComponent rightComponent() {
        return dclareUU(TrianglesEditor.class);
    }

    @Property(containment)
    default ExampleMapping1 mapping1() {
        return dclare(ExampleMapping1.class, ((DiagramEditor) leftComponent()).canvas(), ((DiagramEditor) rightComponent()).canvas());
    }

    @Override
    default boolean vertical() {
        return false;
    }

    interface DiagramEditor extends SplitPane {

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

        default DCanvas canvas() {
            return (DCanvas) ((ScrollPane) leftComponent()).viewportView();
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
    }

    interface CirlcesAndSquaresEditor extends DiagramEditor {

        @Override
        @Property(constant)
        default DComponent leftComponent() {
            return dclareUU(ScrollPane.class, (c) -> {
                DCanvas canvas = makeCanvas();
                set(c, ScrollPane::viewportView, canvas);
            });
        }

        default DCanvas makeCanvas() {
            return dclareUU(DCanvas.class, set(DCanvas::color, new Color(200, 255, 200)));
        }

        @Property(constant)
        default ClickMode rectangleMode() {
            return dclareUU(ClickMode.class, e -> set(e, ClickMode::action, z -> {
                InputDeviceData di = z.deviceInput();
                DRectangle r = dclareUU(DRectangle.class, set(DShape::position, di.mousePosition()));
                set(dclare(MappingData.class, ((DUUObject) r).uuid()).triangle(), DShape::position, di.mousePosition());
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
                prependShape(dclareUU(DLine.class, //
                        rule(DShape::position, l -> one.centre()), //
                        rule(DLine::endPoint, l -> two.centre()), //
                        rule("delete", l -> {
                            if ((pre(one::dParent) != null && one.dParent() == null) || //
                            (pre(two::dParent) != null && two.dParent() == null)) {
                                clear(l);
                            }
                        }) //
                ));
                set(c, DCanvas::mode, selectionMode());
            }));
        }

        @Property(constant)
        default SelectionMode selectionMode() {
            return dclareUU(SelectionMode.class, DCanvas.SELECTION_MODE);
        }

        @Override
        @Property(constant)
        default DComponent rightComponent() {
            return dclareUU(DToolbar.class, (c) -> {
                set(c, DToolbar::preferredSize, dclare(DDimension.class, 40.0, 100.0));
                set(c, DToolbar::minimumSize, dclare(DDimension.class, 50.0, 100.0));
                set(c, DToolbar::items, List.of(//
                        item("Select", "selection.png", (x) -> set(canvas(), DCanvas::mode, selectionMode())), //
                        item("Rectangle", "rectangle.png", (x) -> set(canvas(), DCanvas::mode, rectangleMode())), //
                        item("Circle", "circle.png", (x) -> set(canvas(), DCanvas::mode, circleMode())), //
                        item("Line", "line.png", (x) -> set(canvas(), DCanvas::mode, lineMode())) //
                ));
            });
        }

    }

    interface TrianglesEditor extends DiagramEditor {

        @Override
        @Property(constant)
        default DComponent leftComponent() {
            return dclareUU(ScrollPane.class, (c) -> {
                DCanvas canvas = makeCanvas();
                set(c, ScrollPane::viewportView, canvas);
            });
        }

        default DCanvas makeCanvas() {
            return dclareUU(DCanvas.class, set(DCanvas::color, new Color(217, 233, 255)));
        }

        @Override
        @Property(constant)
        default DComponent rightComponent() {
            return dclareUU(DToolbar.class, (c) -> {
                set(c, DToolbar::preferredSize, dclare(DDimension.class, 40.0, 100.0));
                set(c, DToolbar::minimumSize, dclare(DDimension.class, 50.0, 100.0));
                set(c, DToolbar::items, List.of(//
                        item("Triangle", "triangle.png", (x) -> {
                            DTriangle t = dclareUU(DTriangle.class, set(DShape::position, startPosition()));
                            set(dclare(MappingData.class, ((DUUObject) t).uuid()).rectangle(), DShape::position, startPosition());
                            appendShape(t);
                        }) //                        
                ));
            });
        }

        @Property(constant)
        default DPoint startPosition() {
            return dclare(DPoint.class, 100.0, 100.0);
        }
    }

    interface ExampleMapping1 extends DObject, DStruct2<DCanvas, DCanvas> {

        @Property(key = 0)
        DCanvas rectanglesAndCircles();

        @Property(key = 1)
        DCanvas triangles();

        @Property(containment)
        Set<MappingData> mappings();

        @Rule
        default void mapFromTriangles() {
            set(this, ExampleMapping1::mappings, triangles().shapes().map(r -> dclare(MappingData.class, ((DUUObject) r).uuid())).asSet());
        }

        @Rule
        default void mapFromRectangles() {
            set(this, ExampleMapping1::mappings, rectanglesAndCircles().shapes().filter(DRectangle.class).map(r -> dclare(MappingData.class, ((DUUObject) r).uuid())).asSet());
        }

        @Rule
        default void mapToTriangles() {
            set(triangles(), DCanvas::shapes, mappings().map(MappingData::triangle).asList());
        }

        @Rule
        default void mapToRectangles() {
            List<DShape> circlesAndLines = rectanglesAndCircles().shapes().filter(s -> !(s instanceof DRectangle)).asList();
            set(rectanglesAndCircles(), DCanvas::shapes, circlesAndLines.addAll(mappings().map(MappingData::rectangle)));
        }
    }

    interface MappingData extends DObject, DUUObject {

        @Property(constant)
        default DRectangle rectangle() {
            return dclareUU(DRectangle.class, uuid());
        }

        @Property(constant)
        default DTriangle triangle() {
            return dclareUU(DTriangle.class, uuid(), //
                    rule(DTriangle::position, t -> {
                        DFilled f = rectangle();
                        if (f.dragging()) {
                            DPoint delta = f.position().minus(pre(f, DShape::position));
                            DPoint p = pre(t, DShape::position);
                            return p.plus(delta);
                        }
                        return t.position();
                    }), //
                    rule(DTriangle::highlighted, t -> rectangle().selected()), //
                    rule(DTriangle::radius, t -> {
                        DCanvas c = dAncestor(ExampleMapping1.class).rectanglesAndCircles();
                        long nr = c.shapes().filter(DLine.class).filter(l -> l.position().equals(rectangle().centre()) || l.endPoint().equals(rectangle().centre())).count();
                        return (int) (50 + nr * 10);
                    }));
        }
    }

}
