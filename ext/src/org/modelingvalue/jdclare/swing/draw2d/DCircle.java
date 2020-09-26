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

import java.awt.*;

import org.modelingvalue.jdclare.*;
import org.modelingvalue.jdclare.swing.DComponent.*;
import org.modelingvalue.jdclare.swing.draw2d.DCircle.*;

@Native(CircleNative.class)
public interface DCircle extends DFilled {
    @Default
    @Property
    default int radius() {
        return 50;
    }

    @Override
    default boolean hit(DPoint pt) {
        return pt.minus(position()).length() < radius();
    }

    @Override
    default DPoint centre() {
        return position();
    }

    class CircleNative extends FilledNative<DCircle> {
        public CircleNative(DCircle visible) {
            super(visible);
        }

        @Override
        public void paint(Graphics2D g) {
            DPoint pt = visible.position();
            int rad = visible.radius();
            Color fc = visible.color();
            Color lc = visible.lineColor();
            String str = visible.text();
            D2D.drawOval(g, (int) pt.x() - rad, (int) pt.y() - rad, rad * 2, rad * 2, fc, lc, str);
        }

        @SuppressWarnings("unused")
        public void radius(int pre, int post) {
            ancestor(DComponentNative.class).swing().repaint();
        }
    }
}
