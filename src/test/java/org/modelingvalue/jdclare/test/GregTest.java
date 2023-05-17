package org.modelingvalue.jdclare.test;

import static org.modelingvalue.jdclare.DClare.dStruct;
import static org.modelingvalue.jdclare.DClare.set;
import static org.modelingvalue.jdclare.PropertyQualifier.containment;
import static org.modelingvalue.jdclare.PropertyQualifier.optional;

import org.modelingvalue.jdclare.*;

public interface GregTest extends DUniverse {

    public static void main(String[] args) {
        DClare.runAndStop(GregTest.class);
    }

    @Property(containment)
    default MyCount count() {
        return dStruct(MyCount.class, this);
    }

    interface MyCount extends DStruct1<GregTest>, DObject {
        @Property(key = 0)
        GregTest universe();

        @Property()
        @Default
        default long val() {
            return 0;
        }

        @Property({optional, containment})
        default MySetter setter() {
            if (val() > 10) {
                return null;
            }
            return dStruct(MySetter.class, this, this.val());
        }

    }

    interface MySetter extends DStruct2<MyCount, Long>, DObject {
        @Property(key = 0)
        MyCount count();

        @Property(key = 1)
        long value();

        @Rule
        default void rule() {
            set(count(), MyCount::val, count().val() + 1);
        }
    }
}
