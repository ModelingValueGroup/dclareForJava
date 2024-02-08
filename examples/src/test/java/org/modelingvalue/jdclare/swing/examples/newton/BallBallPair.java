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

package org.modelingvalue.jdclare.swing.examples.newton;

import static java.lang.Math.*;
import static org.modelingvalue.jdclare.DClare.*;

import org.modelingvalue.jdclare.DStruct2;
import org.modelingvalue.jdclare.Default;
import org.modelingvalue.jdclare.Property;
import org.modelingvalue.jdclare.Rule;
import org.modelingvalue.jdclare.swing.draw2d.DPoint;

public interface BallBallPair extends DStruct2<Ball, Ball>, CollisionPair {

    @Property(key = 0)
    Ball a();

    @Property(key = 1)
    Ball b();

    @Override
    @Property
    default double preCollisionTime() {
        DPoint va = pre(a(), Ball::velocity);
        DPoint vb = pre(b(), Ball::velocity);
        DPoint pa = pre(a(), Ball::position);
        DPoint pb = pre(b(), Ball::position);
        return collisionTime(va, vb, pa, pb);
    }

    @Override
    default double postCollisionTime() {
        DPoint va = a().solVelocity();
        DPoint vb = b().solVelocity();
        DPoint pa = a().solPosition();
        DPoint pb = b().solPosition();
        return collisionTime(va, vb, pa, pb);
    }

    private double collisionTime(DPoint va, DPoint vb, DPoint pa, DPoint pb) {
        if (!va.equals(DPoint.NULL) || !vb.equals(DPoint.NULL)) {
            Table table = a().table();
            DPoint dv = va.minus(vb);
            DPoint dp = pa.minus(pb);
            double a = dv.dot(dv);
            double b = 2 * dp.dot(dv);
            double c = dp.dot(dp);
            double d = pow(b, 2.0) - 4 * a * (c - 4 * table.ballRadiusPow());
            if (d >= 0) {
                double sqrt = sqrt(d);
                double t1 = (-b + sqrt) / (2 * a);
                double t2 = (-b - sqrt) / (2 * a);
                return Math.min(t1, t2);
            }
        }
        return Double.MAX_VALUE;
    }

    @Override
    default DPoint velocity(Ball ball) {
        return ball.equals(a()) ? aVelocity() : ball.equals(b()) ? bVelocity() : ball.solVelocity();
    }

    @Default
    @Property
    default DPoint aVelocity() {
        return DPoint.NULL;
    }

    @Default
    @Property
    default DPoint bVelocity() {
        return DPoint.NULL;
    }

    @Rule
    default void velocity() {
        Table table = a().table();
        DPoint va = a().solVelocity();
        DPoint vb = b().solVelocity();
        if (equals(table.collision())) {
            DPoint na = b().solPosition().minus(a().solPosition()).normal();
            DPoint nb = na.mult(-1.0);
            DPoint vna = na.mult(va.dot(na));
            DPoint vnb = nb.mult(vb.dot(nb));
            DPoint vta = va.minus(vna);
            DPoint vtb = vb.minus(vnb);
            double f = 1.0 - table.ballsBouncingResistance();
            set(this, BallBallPair::aVelocity, vta.plus(vnb).mult(f));
            set(this, BallBallPair::bVelocity, vtb.plus(vna).mult(f));
        } else {
            set(this, BallBallPair::aVelocity, va);
            set(this, BallBallPair::bVelocity, vb);
        }
    }

    @Override
    default double distance() {
        DPoint pa = a().solPosition();
        DPoint pb = b().solPosition();
        int radius = a().radius();
        return Math.abs(Math.abs(pb.minus(pa).length()) - radius - radius);
    }

}
