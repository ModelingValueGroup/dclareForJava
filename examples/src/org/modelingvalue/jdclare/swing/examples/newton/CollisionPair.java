package org.modelingvalue.jdclare.swing.examples.newton;

import org.modelingvalue.jdclare.*;
import org.modelingvalue.jdclare.swing.draw2d.*;

public interface CollisionPair extends DObject {

    @Property
    double preCollisionTime();

    double postCollisionTime();

    DPoint velocity(Ball ball);

    double distance();

}
