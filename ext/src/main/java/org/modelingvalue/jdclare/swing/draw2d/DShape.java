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

package org.modelingvalue.jdclare.swing.draw2d;

import static org.modelingvalue.jdclare.DClare.*;

import java.awt.*;
import java.awt.event.*;

import org.modelingvalue.jdclare.*;
import org.modelingvalue.jdclare.swing.DComponent.*;
import org.modelingvalue.jdclare.swing.*;
import org.modelingvalue.jdclare.swing.draw2d.DShape.*;

@Native(ShapeNative.class)
public interface DShape extends DVisible {
    default DCanvas canvas() {
        return dAncestor(DCanvas.class);
    }

    @Property
    DPoint position();

    @Default
    @Property
    default boolean highlighted() {
        return false;
    }

    @Property
    default Color lineColor() {
        return highlighted() ? Color.red : Color.black;
    }

    boolean hit(DPoint pt);

    @Property
    default boolean selected() {
        if (DClare.dclareUU(SelectionMode.class, DCanvas.SELECTION_MODE).equals(canvas().mode())) {
            InputDeviceData di = canvas().deviceInput();
            if (di.isLeftMouseDown() && !pre(di, InputDeviceData::isLeftMouseDown)) {
                return hit(di.mousePosition()) ? !pre(this, DShape::selected) : di.isCtrlDown() && selected();
            }
        }
        return selected();
    }

    @Rule
    default void delete() {
        if (canvas() != null && selected()) {
            InputDeviceData di = canvas().deviceInput();
            if (di.pressedKeys().contains(KeyEvent.VK_DELETE)) {
                DClare.unparent(this);
            }
        }
    }

    @Default
    @Property
    default DPoint centre() {
        return DPoint.NULL;
    }

    @SuppressWarnings("unused")
    abstract class ShapeNative<S extends DShape> extends VisibleNative<S> {
        public ShapeNative(S visible) {
            super(visible);
        }

        public abstract void paint(Graphics2D g);

        public void position(DPoint pre, DPoint post) {
            ancestor(DComponentNative.class).swing().repaint();
        }

        public void lineColor(Color pre, Color post) {
            ancestor(DComponentNative.class).swing().repaint();
        }
    }
}
