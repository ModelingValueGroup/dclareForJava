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

package org.modelingvalue.jdclare.swing.examples.newton;

import static org.modelingvalue.jdclare.DClare.*;
import static org.modelingvalue.jdclare.PropertyQualifier.*;

import java.util.Comparator;

import org.modelingvalue.collections.List;
import org.modelingvalue.collections.Set;
import org.modelingvalue.jdclare.DClock;
import org.modelingvalue.jdclare.Property;
import org.modelingvalue.jdclare.Rule;
import org.modelingvalue.jdclare.swing.draw2d.DCanvas;
import org.modelingvalue.jdclare.swing.draw2d.DPoint;

public interface Table extends DCanvas {

    @Property(constant)
    default double gravity() {
        return 9.80665;
    }

    @Property(constant)
    default double rollingResistance() {
        return 0.8;
    }

    @Property(constant)
    default double friction() {
        return gravity() * rollingResistance();
    }

    @Property(constant)
    default double cushionBouncingResistance() {
        return 0.2;
    }

    @Property(constant)
    default double ballsBouncingResistance() {
        return 0.2;
    }

    @Property(constant)
    default double ballRadius() {
        return 20.0;
    }

    @Property(constant)
    default double ballRadiusPow() {
        return Math.pow(ballRadius(), 2.0);
    }

    @Property(constant)
    default DPoint cushionMinimum() {
        return dclare(DPoint.class, ballRadius(), ballRadius());
    }

    @Property(containment)
    default Set<CollisionPair> collisionPairs() {
        return balls().flatMap(Ball::collisionPairs).toSet();
    }

    @Property()
    default List<Ball> balls() {
        return shapes().filter(Ball.class).toList();
    }

    @Property
    default DPoint cushionMaximum() {
        return size().toPoint().minus(cushionMinimum());
    }

    @Property
    default boolean moving() {
        return balls().anyMatch(Ball::moving);
    }

    @Property
    default double velocityDelta() {
        return friction() * passSeconds();
    }

    @Property
    default double positionDelta() {
        return 0.5 * velocityDelta() * passSeconds();
    }

    @Property
    default double passSeconds() {
        return moving() ? dUniverse().clock().passSeconds() : 0.01;
    }

    @Property(optional)
    default CollisionPair firstCollision() {
        if (moving()) {
            double timeWindow = dUniverse().clock().passSeconds() * 2;
            return collisionPairs().filter(c -> c.preCollisionTime() > 0.0 && c.preCollisionTime() <= timeWindow).min(Comparator.comparingDouble(CollisionPair::preCollisionTime)).orElse(null);
        } else {
            return null;
        }
    }

    @Property(optional)
    default CollisionPair collision() {
        CollisionPair firstCollision = firstCollision();
        return firstCollision != null && firstCollision.distance() <= 0.5 ? firstCollision : null;
    }

    @Rule
    default void setCollisionTime() {
        CollisionPair firstCollision = firstCollision();
        if (firstCollision != null && collision() == null) {
            double passNanos = (passSeconds() + firstCollision.postCollisionTime()) * DClock.BILLION;
            DClock clock = dUniverse().clock();
            set(clock, DClock::time, pre(clock, DClock::time).plusNanos((long) passNanos));
        }
    }
}
