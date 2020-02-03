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

package org.modelingvalue.jdclare.swing.draw2d;

import org.modelingvalue.jdclare.*;
import org.modelingvalue.jdclare.swing.DComponent.*;
import org.modelingvalue.jdclare.swing.*;
import org.modelingvalue.jdclare.swing.draw2d.DFilled.*;

import java.awt.*;
import java.awt.event.*;

import static org.modelingvalue.jdclare.DClare.*;
import static org.modelingvalue.jdclare.PropertyQualifier.*;

@Native(FilledNative.class)
public interface DFilled extends DShape {

    @Property
    default Color color() {
        return selected() ? Color.yellow : Color.white;
    }

    @Property(optional)
    String text();

    @Override
    default DPoint position() {
        return dragging() ? dragStartPosition().plus(canvas().deviceInput().mousePosition().minus(dragStartMousePosition())) : position();
    }

    @Property(optional)
    DPoint dragStartMousePosition();

    @Property(optional)
    DPoint dragStartPosition();

    @Property()
    default boolean dragging() {
        DCanvas         canvas = canvas();
        InputDeviceData di     = canvas.deviceInput();
        if (!pre(di, InputDeviceData::isLeftMouseDown) && di.isLeftMouseDown() && pre(this::dParent) != null && hit(di.mousePosition())) {
            set(this, DFilled::dragStartPosition, position());
            set(this, DFilled::dragStartMousePosition, di.mousePosition());
            return true;
        } else if (dragging() && (!di.isLeftMouseDown() || di.pressedKeys().contains(KeyEvent.VK_ESCAPE))) {
            if (di.pressedKeys().contains(KeyEvent.VK_ESCAPE)) {
                set(this, DShape::position, dragStartPosition());
            }
            set(this, DFilled::dragStartPosition, null);
            set(this, DFilled::dragStartMousePosition, null);
            return false;
        } else {
            return dragging();
        }
    }

    abstract class FilledNative<S extends DFilled> extends ShapeNative<S> {

        public FilledNative(S visible) {
            super(visible);
        }

        public void color(Color pre, Color post) {
            ancestor(DComponentNative.class).swing().repaint();
        }

        public void text(String pre, String post) {
            ancestor(DComponentNative.class).swing().repaint();
        }

    }

}
