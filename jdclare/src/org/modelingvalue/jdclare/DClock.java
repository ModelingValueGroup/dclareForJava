package org.modelingvalue.jdclare;

import java.time.*;

import static org.modelingvalue.jdclare.DClare.*;

public interface DClock extends DObject, DStruct0 {

    final double BILLION = 1000000000.0;

    @Property
    @Default
    default Instant time() {
        return dClare().getClock().instant();
    }

    @Property
    default Duration passTime() {
        return Duration.between(pre(this, DClock::time), time());
    }

    @Property
    default double passSeconds() {
        return passTime().toNanos() / BILLION;
    }

}
