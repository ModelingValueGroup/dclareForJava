//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
// (C) Copyright 2018-2023 Modeling Value Group B.V. (http://modelingvalue.org)                                        ~
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

package org.modelingvalue.jdclare.test;

import static org.modelingvalue.jdclare.DClare.*;
import static org.modelingvalue.jdclare.PropertyQualifier.*;

import org.modelingvalue.collections.Set;
import org.modelingvalue.jdclare.*;

@SuppressWarnings("unused")
public interface BirdUniverse extends DUniverse {

    @Property(containment)
    Set<Bird> birds();

    interface Bird extends DStruct2<DObject, String>, DNamed {

        default Bird firstBird() {
            DObject p = dParent();
            return p instanceof Bird ? ((Bird) p).firstBird() : this;
        }

        @Property(key = 0)
        DObject parent();

        @Override
        @Property(key = 1)
        String name();

        @Property
        String color();

        @Property(optional)
        String wingColor();

        @Property(optional)
        String childrenName();

        @Property(containment)
        Set<Wing> wings();

        @Property(containment)
        Set<Bird> children();

        @Property
        Set<Bird> orphans();

        String notAProperty();

    }

    interface Wing extends DStruct2<Bird, String>, DNamed {
        @Property(key = 0)
        Bird bird();

        @Override
        @Property(key = 1)
        String name();

        @Property
        default int span() {
            return 1;
        }
    }

    interface Feather extends DStruct2<Bird, Integer>, DObject {
        @Property(key = 0)
        Bird bird();

        @Property(key = 1)
        Integer id();

        @Property
        default String color() {
            return bird().color();
        }
    }

    interface Condor extends Bird {

        @Rule
        default void setWingColor() {
            if ("white".equals(color())) {
                set(this, Bird::wingColor, "notwhite");
                set(this, Bird::wingColor, "nowhite");
            }
        }

        @Rule
        default void setWingColor1() {
            if ("red".equals(color())) {
                set(this, Bird::wingColor, "notred");
            }
        }

        @Rule
        default void setWingColor2() {
            if ("red".equals(color())) {
                set(this, Bird::wingColor, "nored");
            }
        }

        @Rule
        default void addLeftWing() {
            if ("green".equals(color())) {
                Wing wing = dclare(Wing.class, this, "Left");
                set(this, Bird::wings, Set::add, wing);
            }
        }

        @Rule
        default void removeLeftWing() {
            if ("green".equals(color())) {
                Wing wing = dclare(Wing.class, this, "Left");
                set(this, Bird::wings, Set::remove, wing);
            }
        }

        @Rule
        default void addAndRemoveLeftWing() {
            if ("blue".equals(color())) {
                Wing wing = dclare(Wing.class, this, "Left");
                set(this, Bird::wings, Set::remove, wing);
                set(this, Bird::wings, Set::add, wing);
            }
        }

        @Rule
        default void increaseLeftWingSpan() {
            if ("black".equals(color())) {
                Wing wing = dclare(Wing.class, this, "Left");
                int span = wing.span();
                set(wing, Wing::span, span + 1);
            }
        }

        @Rule
        default void multiply() {
            if ("yellow".equals(color()) && children().isEmpty() && name().length() < 7) {
                for (int i = 0; i < 7; i++) {
                    Bird child = dclare(Condor.class, this, name() + i);
                    set(this, Bird::children, Set::add, child);
                    set(child, Bird::color, "yellow");
                }
            }
        }

        @Rule
        default void addGenerations() {
            if ("brown".equals(color()) && name().length() < 50) {
                for (int i = 0; i < 2; i++) {
                    Bird child = dclare(Condor.class, dUniverse(), name() + i);
                    set(this, Bird::children, Set::add, child);
                    set(child, Bird::color, "brown");
                }
            }
        }

    }

    interface Pigeon extends Bird {

        @Rule
        default void addChildren1() {
            if ("grey".equals(color())) {
                for (int i = 0; i < 10000; i++) {
                    Bird child = dclare(Pigeon.class, this, name() + i);
                    set(this, Bird::children, Set::add, child);
                    set(child, Bird::color, "notgrey");
                }
            }
        }

        @Rule
        default void setGreyChildrenName() {
            if ("grey".equals(color())) {
                set(this, Bird::childrenName, children().reduce("", (n, b) -> n + b.name(), (a, b) -> a + b));
            }
        }

        @Rule
        default void addChildren2() {
            if ("yellow".equals(color())) {
                for (int i = 0; i < 400; i++) {
                    Bird child = dclare(Bird.class, this, name() + i);
                    set(child, Bird::color, "notyellow");
                    set(this, Bird::children, Set::add, child);
                    for (int j = 0; j < 10; j++) {
                        Bird grandChild = dclare(Bird.class, child, name() + j + "gc"); //
                        set(grandChild, Bird::color, "notyellow");
                        set(child, Bird::children, Set::add, grandChild);
                    }
                }
            }
        }

        @Rule
        default void setYellowChildrenName() {
            if ("yellow".equals(color())) {
                set(this, Bird::childrenName, children().flatMap(Bird::children).reduce("", (n, b) -> n + b.name(), (a, b) -> a + b));
            }
        }

        @Rule
        default void addOrphans() {
            if ("green".equals(color())) {
                for (int i = 0; i < 100; i++) {
                    Bird orphan = dclare(Pigeon.class, this, name() + i);
                    set(this, Bird::orphans, Set::add, orphan);
                    set(orphan, Bird::color, "green");
                }
                set(this, Bird::color, "notgreen");
            }
        }
    }

    interface HouseSparrow extends Bird {
        @Rule
        default void multiply() {
            if ("yellow".equals(color()) && children().isEmpty() && name().length() < 4) {
                for (int i = 0; i < 200; i++) {
                    Bird child = dclare(HouseSparrow.class, this, name() + i);
                    set(this, Bird::children, Set::add, child);
                }
            }
        }

        @Override
        default String color() {
            return dclare(HouseSparrow.class, dUniverse(), "1").color();
        }

    }

    interface Sparrow extends Bird {

        @Rule
        default void addChildren() {
            if ("black".equals(color())) {
                for (int i = 0; i < 2000; i++) {
                    Sparrow child = dclare(Sparrow.class, this, name() + i);
                    set(child, Bird::color, "noblack");
                    set(this, Bird::children, Set::add, child);
                }
            }
        }

        @Rule
        default void setWingColor1() {
            Bird parent = dclare(Sparrow.class, dclare(BirdUniverse.class), "0");
            if (parent.color().equals("black")) {
                set(parent, Bird::color, "notblack");
            }
        }

        @Rule
        default void setWingColor2() {
            Bird parent = dclare(Sparrow.class, dclare(BirdUniverse.class), "0");
            if (parent.color().equals("black")) {
                set(parent, Bird::color, "notblack");
            }
        }

        @Rule
        default void setWingColor3() {
            Bird parent = dclare(Sparrow.class, dclare(BirdUniverse.class), "0");
            if (parent.color().equals("black")) {
                set(parent, Bird::color, "notblack");
            }
        }

        @Rule
        default void setWingColor4() {
            Bird parent = dclare(Sparrow.class, dclare(BirdUniverse.class), "0");
            if (parent.color().equals("black")) {
                set(parent, Bird::color, "notblack");
            }
        }

        @Rule
        default void setWingColor5() {
            Bird parent = dclare(Sparrow.class, dclare(BirdUniverse.class), "0");
            if (parent.color().equals("black")) {
                set(parent, Bird::color, "notblack");
            }
        }

    }

    interface HummingBird extends Bird {
        @Rule
        default void missingColor() {
            if ("green".equals(color())) {
                Bird child = dclare(HummingBird.class, this, this.name() + "+");
                set(this, Bird::children, Set::add, child);
            }
        }

        @Rule
        default void nullPointerInMandatoryProperty() {
            if ("blue".equals(color())) {
                Bird child = dclare(HummingBird.class, this, this.name() + "+");
                set(this, Bird::children, Set::add, child);
            }
        }

        @SuppressWarnings("null")
        @Rule
        default void nullPointerInOptionalProperty() {
            if ("yellow".equals(color())) {
                Bird child = dclare(HummingBird.class, this, this.name() + "+");
                set(child, Bird::wingColor, null);
                String wingColor = child.wingColor();
                if (wingColor == null) {
                    //noinspection ConstantConditions
                    System.err.println(wingColor.length());
                }
            }
        }

        @Rule
        default void divisionByZero() {
            if ("red".equals(color())) {
                Wing wing = dclare(Wing.class, this, "Left");
                int span = wing.span();
                //noinspection divzero
                set(wing, Wing::span, span / 0);
            }
        }
    }

    interface Pheasant extends Bird {

        @Default
        @Property(constant)
        default String feetColor() {
            return "orange";
        }

        @Property(constant)
        String tailColor();

        @Property(constant)
        String headColor();

        @Property(constant)
        default String legColor() {
            return "";
        }

        @Property(constant)
        default String leftLegColor() {
            return "";
        }

        @Property(constant)
        default String rightLegColor() {
            return "";
        }

        @Rule
        default void setColor() {
            if ("yellow".equals(color())) {
                set(this, Bird::color, "notyellow");
            }
        }

        @Rule
        default void setTailColor1() {
            if ("blue".equals(color())) {
                set(this, Pheasant::tailColor, "notblue");
            }
        }

        @Rule
        default void setTailColor2() {
            set(this, Pheasant::tailColor, color());
        }

        @Rule
        default void setHeadColor1() {
            if ("red".equals(color())) {
                feetColor(); // init default value
                set(this, Pheasant::feetColor, "black");
            }
        }

        @Rule
        default void setLegColor() {
            if ("black".equals(color())) {
                set(this, Pheasant::legColor, "black");
            }
        }

        @Rule
        default void setLegColorCircular1() {
            if ("white".equals(color())) {
                set(this, Pheasant::rightLegColor, leftLegColor());
            }
        }

        @Rule
        default void setLegColorCircular2() {
            if ("white".equals(color())) {
                set(this, Pheasant::leftLegColor, rightLegColor());
            }
        }

    }

    @Override
    default void init() {
        DUniverse.super.init();
    }

    static void main(String[] args) {
        DClare.run(BirdUniverse.class);
    }

}
