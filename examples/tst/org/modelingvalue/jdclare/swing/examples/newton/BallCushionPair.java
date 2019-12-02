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

package org.modelingvalue.jdclare.swing.examples.newton;

import org.modelingvalue.jdclare.*;
import org.modelingvalue.jdclare.swing.draw2d.*;

import static org.modelingvalue.jdclare.DClare.*;

public interface BallCushionPair extends DStruct3<Ball, Boolean, Boolean>, CollisionPair {

    @Property(key = 0)
    Ball ball();

    @Property(key = 1)
    Boolean isY();

    @Property(key = 2)
    Boolean isMax();

    @Override
    @Property
    default double preCollisionTime() {
        return collisionTime(pre(ball(), Ball::velocity), pre(ball(), Ball::position));
    }

    @Override
    default double postCollisionTime() {
        return collisionTime(ball().solVelocity(), ball().solPosition());
    }

    private double collisionTime(DPoint v, DPoint p) {
        if (v.y() != 0.0 || v.x() != 0.0) {
            Table table = ball().table();
            DPoint min = table.cushionMinimum();
            DPoint max = table.cushionMaximum();
            if (isY()) {
                if (isMax() && v.y() > 0.0) {
                    return (max.y() - p.y()) / v.y();
                } else if (!isMax() && v.y() < 0.0) {
                    return (min.y() - p.y()) / v.y();
                }
            } else {
                if (isMax() && v.x() > 0.0) {
                    return (max.x() - p.x()) / v.x();
                } else if (!isMax() && v.x() < 0.0) {
                    return (min.x() - p.x()) / v.x();
                }
            }
        }
        return Double.MAX_VALUE;
    }

    @Override
    default DPoint velocity(Ball ball) {
        return ball.equals(ball()) ? velocity() : ball.solVelocity();
    }

    @Property
    default DPoint velocity() {
        Table table = ball().table();
        DPoint v = ball().solVelocity();
        if (equals(table.collision())) {
            double res = 1.0 - table.cushionBouncingResistance();
            if (isY()) {
                v = dclare(DPoint.class, v.x() * res, -v.y() * res);
            } else {
                v = dclare(DPoint.class, -v.x() * res, v.y() * res);
            }
        }
        return v;
    }

    @Override
    default double distance() {
        Ball ball = ball();
        DPoint p = ball.solPosition();
        Table table = ball.table();
        DPoint min = table.cushionMinimum();
        DPoint max = table.cushionMaximum();
        if (isY()) {
            if (isMax()) {
                return Math.abs(max.y() - p.y());
            } else {
                return Math.abs(p.y() - min.y());
            }
        } else {
            if (isMax()) {
                return Math.abs(max.x() - p.x());
            } else {
                return Math.abs(p.x() - min.x());
            }
        }
    }

}
