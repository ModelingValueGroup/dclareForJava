//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
// (C) Copyright 2018-2022 Modeling Value Group B.V. (http://modelingvalue.org)                                        ~
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
import org.modelingvalue.jdclare.swing.draw2d.DLine.*;

import java.awt.*;

import static java.lang.Math.*;

@Native(LineNative.class)
public interface DLine extends DShape {

    @Default
    @Property
    default DPoint endPoint() {
        return DPoint.NULL;
    }

    @Override
    default boolean hit(DPoint pt) {
        DPoint start = position();
        DPoint end = endPoint();
        double lowx = min(start.x(), end.x());
        double highx = max(start.x(), end.x());

        double lowy = min(start.y(), end.y());
        double highy = max(start.y(), end.y());

        return pt.x() > lowx && pt.x() < highx && pt.y() > lowy && pt.y() < highy && //
                end.minus(start).hasEqualAngle(end.minus(pt));
    }

    @Override
    default DPoint centre() {
        DPoint pos = position();
        return pos.plus(endPoint().minus(pos).div(2.0));
    }

    class LineNative extends ShapeNative<DLine> {

        public LineNative(DLine visible) {
            super(visible);
        }

        @Override
        public void paint(Graphics2D g) {
            DPoint pt = visible.position();
            DPoint end = visible.endPoint();
            Color lc = visible.lineColor();
            D2D.drawLine(g, lc, (int) pt.x(), (int) pt.y(), (int) end.x(), (int) end.y());
        }

        @SuppressWarnings("unused")
        public void endPoint(DPoint pre, DPoint post) {
            ancestor(DComponentNative.class).swing().repaint();
        }

    }

}
