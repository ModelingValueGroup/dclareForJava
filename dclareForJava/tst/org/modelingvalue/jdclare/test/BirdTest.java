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

package org.modelingvalue.jdclare.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.modelingvalue.jdclare.DClare.dclare;
import static org.modelingvalue.jdclare.DClare.of;
import static org.modelingvalue.jdclare.DClare.set;

import java.util.HashSet;
import java.util.function.Function;

import org.junit.jupiter.api.Test;
import org.modelingvalue.collections.Set;
import org.modelingvalue.collections.util.NotMergeableException;
import org.modelingvalue.collections.util.Pair;
import org.modelingvalue.dclare.State;
import org.modelingvalue.dclare.ex.EmptyMandatoryException;
import org.modelingvalue.dclare.ex.NonDeterministicException;
import org.modelingvalue.dclare.ex.TooManyChangesException;
import org.modelingvalue.dclare.ex.TooManyObservedException;
import org.modelingvalue.dclare.ex.TooManyObserversException;
import org.modelingvalue.jdclare.DClare;
import org.modelingvalue.jdclare.test.BirdUniverse.Bird;
import org.modelingvalue.jdclare.test.BirdUniverse.Condor;
import org.modelingvalue.jdclare.test.BirdUniverse.HouseSparrow;
import org.modelingvalue.jdclare.test.BirdUniverse.HummingBird;
import org.modelingvalue.jdclare.test.BirdUniverse.Pheasant;
import org.modelingvalue.jdclare.test.BirdUniverse.Pigeon;
import org.modelingvalue.jdclare.test.BirdUniverse.Sparrow;

public class BirdTest {
    private static final boolean PRINT_STACK_TRACE = Boolean.getBoolean("PRINT_STACK_TRACE");

    private void addBird(DClare<BirdUniverse> dclare, Class<? extends Bird> clazz, Pair<String, String> props) {
        dclare.put(dclare.universe(), () -> {
            Bird bird = dclare(clazz, dclare.universe(), props.a());
            set(bird, Bird::color, props.b());
            set(dclare.universe(), BirdUniverse::birds, Set::add, bird);
        });
    }

    private void addBirds(DClare<BirdUniverse> dclare, Class<? extends Bird> clazz, java.util.Set<Pair<String, String>> props) {
        dclare.put(dclare.universe(), () -> {
            for (Pair<String, String> p : props) {
                Bird bird = dclare(clazz, dclare.universe(), p.a());
                set(bird, Bird::color, p.b());
                set(dclare.universe(), BirdUniverse::birds, Set::add, bird);
            }
        });
    }

    private State stop(DClare<BirdUniverse> dclare) {
        dclare.stop();
        return dclare.waitForEnd();
    }

    private void start(DClare<BirdUniverse> dclare) {
        dclare.start();
    }

    @Test
    public void tooManyChangesException1() {
        try {
            DClare<BirdUniverse> dclare = of(BirdUniverse.class);
            start(dclare);
            addBird(dclare, Condor.class, Pair.of("0", "red"));
            stop(dclare);
            fail();
        } catch (Throwable t) {
            Throwable cause = getCause(t);
            assertThrowable(cause, TooManyChangesException.class);
        }
    }

    @Test
    public void tooManyChangesException2() {
        try {
            DClare<BirdUniverse> dclare = of(BirdUniverse.class);
            start(dclare);
            addBird(dclare, Condor.class, Pair.of("0", "white"));
            stop(dclare);
            fail();
        } catch (Throwable t) {
            Throwable cause = getCause(t);
            assertThrowable(cause, TooManyChangesException.class);
        }
    }

    @Test
    public void tooManyChangesException3() {
        try {
            DClare<BirdUniverse> dclare = of(BirdUniverse.class);
            start(dclare);
            addBird(dclare, Condor.class, Pair.of("0", "green"));
            stop(dclare);
            fail();
        } catch (Throwable t) {
            Throwable cause = getCause(t);
            assertThrowable(cause, TooManyChangesException.class);
        }
    }

    @Test
    public void tooManyChangesException4() {
        try {
            DClare<BirdUniverse> dclare = of(BirdUniverse.class);
            start(dclare);
            addBird(dclare, Condor.class, Pair.of("0", "black"));
            stop(dclare);
            fail();
        } catch (Throwable t) {
            Throwable cause = getCause(t);
            assertThrowable(cause, TooManyChangesException.class);
        }
    }

    @Test
    public void tooManyChangesException5() {
        try {
            DClare<BirdUniverse> dclare = of(BirdUniverse.class);
            start(dclare);
            addBird(dclare, Condor.class, Pair.of("0", "blue"));
            stop(dclare);
            fail();
        } catch (Throwable t) {
            Throwable cause = getCause(t);
            assertThrowable(cause, TooManyChangesException.class);
        }
    }

    @Test
    public void tooManyChangesException6() {
        try {
            DClare<BirdUniverse> dclare = of(BirdUniverse.class);
            start(dclare);
            addBird(dclare, Condor.class, Pair.of("0", "brown"));
            stop(dclare);
            fail();
        } catch (Throwable t) {
            Throwable cause = getCause(t);
            assertThrowable(cause, TooManyChangesException.class);
        }
    }

    @Test
    public void tooManyChangesException7() {
        try {
            DClare<BirdUniverse> dclare = of(BirdUniverse.class);
            start(dclare);
            addBird(dclare, Condor.class, Pair.of("0", "yellow"));
            stop(dclare);
            fail();
        } catch (Throwable t) {
            Throwable cause = getCause(t);
            assertThrowable(cause, TooManyChangesException.class);
        }
    }

    @Test
    public void tooManyObservedException1() {
        try {
            DClare<BirdUniverse> dclare = of(BirdUniverse.class);
            start(dclare);
            addBird(dclare, Pigeon.class, Pair.of("0", "grey"));
            stop(dclare);
            fail();
        } catch (Throwable t) {
            Throwable cause = getCause(t);
            assertThrowable(cause, TooManyObservedException.class, "Too many observed (xxxx) by 0.Pigeon::addChildren1", x -> ((TooManyObservedException) x).getSimpleMessage().replaceFirst("\\d\\d+", "xxxx"));
        }
    }

    @Test
    public void tooManyObservedException2() {
        try {
            DClare<BirdUniverse> dclare = of(BirdUniverse.class);
            start(dclare);
            addBird(dclare, Pigeon.class, Pair.of("0", "yellow"));
            stop(dclare);
            fail();
        } catch (Throwable t) {
            Throwable cause = getCause(t);
            assertThrowable(cause, TooManyObservedException.class, "Too many observed (xxxx) by 0.Pigeon::addChildren2", x -> ((TooManyObservedException) x).getSimpleMessage().replaceFirst("\\d\\d+", "xxxx"));
        }
    }

    @Test
    public void noOrphans() {
        DClare<BirdUniverse> dclare = of(BirdUniverse.class);
        start(dclare);
        addBird(dclare, Pigeon.class, Pair.of("0", "green"));
        State result = stop(dclare);
        Set<Bird> birds = result.getObjects(Bird.class).toSet();
        assertEquals(1, birds.size(), "Unexpected Birds: " + birds);
    }

    @Test
    public void tooManyObserversException1() {
        try {
            DClare<BirdUniverse> dclare = of(BirdUniverse.class);
            start(dclare);
            addBird(dclare, HouseSparrow.class, Pair.of("1", "yellow"));
            stop(dclare);
            fail();
        } catch (Throwable t) {
            Throwable cause = getCause(t);
            assertThrowable(cause, TooManyObserversException.class, "Too many observers (xxxx) of 1.color", x -> ((TooManyObserversException) x).getSimpleMessage().replaceFirst("\\d\\d+", "xxxx"));
        }
    }

    @Test
    public void tooManyObserversException2() {
        try {
            DClare<BirdUniverse> dclare = of(BirdUniverse.class);
            start(dclare);
            addBird(dclare, Sparrow.class, Pair.of("0", "black"));
            stop(dclare);//.run(() -> dclare.universe().dDump(System.err));
            fail();
        } catch (Throwable t) {
            Throwable cause = getCause(t);
            assertThrowable(cause, TooManyObserversException.class, "Too many observers (xxxx) of 0.color", x -> ((TooManyObserversException) x).getSimpleMessage().replaceFirst("\\d\\d+", "xxxx"));
        }
    }

    @Test
    public void missingMandatory1() {
        try {
            DClare<BirdUniverse> dclare = of(BirdUniverse.class);
            start(dclare);
            addBird(dclare, HummingBird.class, Pair.of("0", "green"));
            stop(dclare);
            fail();
        } catch (Throwable t) {
            Throwable cause = getCause(t);
            assertThrowable(cause, EmptyMandatoryException.class, java.util.regex.Pattern.quote("Empty mandatory property 'color' of object '0+'"));
        }
    }

    @Test
    public void missingMandatory2() {
        try {
            DClare<BirdUniverse> dclare = of(BirdUniverse.class);
            start(dclare);
            addBird(dclare, HummingBird.class, Pair.of("0", "blue"));
            stop(dclare);
            fail();
        } catch (Throwable t) {
            Throwable cause = getCause(t);
            assertThrowable(cause, EmptyMandatoryException.class, java.util.regex.Pattern.quote("Empty mandatory property 'color' of object '0+'"));
        }
    }

    @Test
    public void nullPointerException2() {
        try {
            DClare<BirdUniverse> dclare = of(BirdUniverse.class);
            start(dclare);
            addBird(dclare, HummingBird.class, Pair.of("0", "yellow"));
            stop(dclare);
            fail();
        } catch (Throwable t) {
            Throwable cause = getCause(t);
            assertThrowable(cause, NullPointerException.class);
        }
    }

    @Test
    public void arithmeticException() {
        try {
            DClare<BirdUniverse> dclare = of(BirdUniverse.class);
            start(dclare);
            addBird(dclare, HummingBird.class, Pair.of("0", "red"));
            stop(dclare);
            fail();
        } catch (Throwable t) {
            Throwable cause = getCause(t);
            assertThrowable(cause, ArithmeticException.class);
        }
    }

    @Test
    public void nonDeterministicException1() {
        try {
            DClare<BirdUniverse> dclare = of(BirdUniverse.class);
            start(dclare);
            addBird(dclare, Pheasant.class, Pair.of("0", "blue"));
            stop(dclare);
            fail();
        } catch (Throwable t) {
            Throwable cause = getCause(t);
            assertThrowable(cause, NonDeterministicException.class);
        }
    }

    @Test
    public void nonDeterministicException2() {
        try {
            DClare<BirdUniverse> dclare = of(BirdUniverse.class);
            start(dclare);
            addBird(dclare, Pheasant.class, Pair.of("0", "yellow"));
            stop(dclare);
            fail();
        } catch (Throwable t) {
            Throwable cause = getCause(t);
            assertThrowable(cause, NonDeterministicException.class, java.util.regex.Pattern.quote("Constant is not consistent 0.tailColor=yellow!=notyellow"));
        }
    }

    @Test
    public void nonDeterministicException3() {
        try {
            DClare<BirdUniverse> dclare = of(BirdUniverse.class);
            start(dclare);
            java.util.Set<Pair<String, String>> props = new HashSet<>();
            props.add(Pair.of("0", "green"));
            props.add(Pair.of("1", "yellow"));
            addBirds(dclare, Pheasant.class, props);
            stop(dclare);
            fail();
        } catch (Throwable t) {
            Throwable cause = getCause(t);
            assertThrowable(cause, NonDeterministicException.class, java.util.regex.Pattern.quote("Constant is not consistent 1.tailColor=yellow!=notyellow"));
        }
    }

    @Test
    public void notMergeableException() {
        try {
            DClare<BirdUniverse> dclare = of(BirdUniverse.class);
            start(dclare);
            java.util.Set<Pair<String, String>> props = new HashSet<>();
            props.add(Pair.of("0", "green"));
            props.add(Pair.of("0", "yellow"));
            addBirds(dclare, Pheasant.class, props);
            stop(dclare);
            fail();
        } catch (Throwable t) {
            Throwable cause = getCause(t);
            assertThrowable(cause, NotMergeableException.class, java.util.regex.Pattern.quote("0.color= null -> [green,yellow]"));
        }
    }

    @Test
    public void nonDeterministicException0() {
        try {
            DClare<BirdUniverse> dclare = of(BirdUniverse.class);
            start(dclare);
            addBird(dclare, Pheasant.class, Pair.of("0", "red"));
            stop(dclare);
            fail();
        } catch (Throwable t) {
            Throwable cause = getCause(t);
            assertThrowable(cause, NonDeterministicException.class, java.util.regex.Pattern.quote("Constant is not consistent 0.feetColor=orange!=black"));
        }
    }

    @Test
    public void constantIsDerivedError() {
        try {
            DClare<BirdUniverse> dclare = of(BirdUniverse.class);
            start(dclare);
            addBird(dclare, Pheasant.class, Pair.of("0", "black"));
            stop(dclare);
            fail();
        } catch (Throwable t) {
            Throwable cause = getCause(t);
            assertThrowable(cause, Error.class, "Constant legColor is derived");
        }
    }

    @Test
    public void circularConstantError() {
        try {
            DClare<BirdUniverse> dclare = of(BirdUniverse.class);
            start(dclare);
            addBird(dclare, Pheasant.class, Pair.of("0", "white"));
            stop(dclare);
            fail();
        } catch (Throwable t) {
            Throwable cause = getCause(t);
            assertThrowable(cause, Error.class, "Constant (left|right)LegColor is derived");

        }
    }

    private Throwable getCause(Throwable t) {
        while (t.getCause() != null) {
            t = t.getCause();
        }
        return t;
    }

    private void assertThrowable(Throwable cause, Class<? extends Throwable> throwable) {
        if (PRINT_STACK_TRACE || !throwable.equals(cause.getClass())) {
            cause.printStackTrace();
        }
        assertEquals(throwable, cause.getClass());
    }

    private void assertThrowable(Throwable cause, Class<? extends Throwable> throwable, String regex) {
        if (PRINT_STACK_TRACE || !throwable.equals(cause.getClass())) {
            cause.printStackTrace();
        }
        assertEquals(throwable, cause.getClass());
        assertTrue(cause.getMessage().matches(regex), cause.getMessage() + " != " + regex);
    }

    private void assertThrowable(Throwable cause, Class<? extends Throwable> throwable, String message, Function<Throwable, String> f) {
        if (PRINT_STACK_TRACE || !throwable.equals(cause.getClass())) {
            cause.printStackTrace();
        }
        assertEquals(throwable, cause.getClass());
        assertEquals(message, f.apply(cause));
    }

}
