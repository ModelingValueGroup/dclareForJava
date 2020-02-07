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

import org.junit.*;
import org.modelingvalue.collections.Set;
import org.modelingvalue.collections.util.*;
import org.modelingvalue.dclare.*;
import org.modelingvalue.dclare.ex.*;
import org.modelingvalue.jdclare.*;
import org.modelingvalue.jdclare.test.BirdUniverse.*;

import java.util.*;
import java.util.function.*;

import static org.junit.Assert.*;
import static org.modelingvalue.jdclare.DClare.*;

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
            Assert.fail();
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
            Assert.fail();
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
            Assert.fail();
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
            Assert.fail();
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
            Assert.fail();
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
            Assert.fail();
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
            Assert.fail();
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
            Assert.fail();
        } catch (Throwable t) {
            Throwable cause = getCause(t);
            assertThrowable(cause, TooManyObservedException.class, "Too many observed (10002) by 0.Pigeon::addChildren1", x -> ((TooManyObservedException) x).getSimpleMessage());
        }
    }

    @Test
    public void tooManyObservedException2() {
        try {
            DClare<BirdUniverse> dclare = of(BirdUniverse.class);
            start(dclare);
            addBird(dclare, Pigeon.class, Pair.of("0", "yellow"));
            stop(dclare);
            Assert.fail();
        } catch (Throwable t) {
            Throwable cause = getCause(t);
            assertThrowable(cause, TooManyObservedException.class, "Too many observed (4802) by 0.Pigeon::addChildren2", x -> ((TooManyObservedException) x).getSimpleMessage());
        }
    }

    @Test
    public void noOrphans() {
        DClare<BirdUniverse> dclare = of(BirdUniverse.class);
        start(dclare);
        addBird(dclare, Pigeon.class, Pair.of("0", "green"));
        State result = stop(dclare);
        Set<Bird> birds = result.getObjects(Bird.class).toSet();
        assertEquals("Unexpected Birds: " + birds, 1, birds.size());
    }

    @Test
    public void tooManyObserversException1() {
        try {
            DClare<BirdUniverse> dclare = of(BirdUniverse.class);
            start(dclare);
            addBird(dclare, HouseSparrow.class, Pair.of("1", "yellow"));
            stop(dclare);
            Assert.fail();
        } catch (Throwable t) {
            Throwable cause = getCause(t);
            assertThrowable(cause, TooManyObserversException.class, "Too many observers (2803) of 1.color", x -> ((TooManyObserversException) x).getSimpleMessage());
        }
    }

    @Test
    public void tooManyObserversException2() {
        try {
            DClare<BirdUniverse> dclare = of(BirdUniverse.class);
            start(dclare);
            addBird(dclare, Sparrow.class, Pair.of("0", "black"));
            stop(dclare);
            Assert.fail();
        } catch (Throwable t) {
            Throwable cause = getCause(t);
            assertThrowable(cause, TooManyObserversException.class, "Too many observers (2002) of 0.color", x -> ((TooManyObserversException) x).getSimpleMessage());
        }
    }

    @Test
    public void missingMandatory1() {
        try {
            DClare<BirdUniverse> dclare = of(BirdUniverse.class);
            start(dclare);
            addBird(dclare, HummingBird.class, Pair.of("0", "green"));
            stop(dclare);
            Assert.fail();
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
            Assert.fail();
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
            Assert.fail();
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
            Assert.fail();
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
            Assert.fail();
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
            Assert.fail();
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
            Assert.fail();
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
            Assert.fail();
        } catch (Throwable t) {
            Throwable cause = getCause(t);
            assertThrowable(cause, NotMergeableException.class, java.util.regex.Pattern.quote("0.color= null -> [green,yellow]"));
        }
    }

    @Test
    public void constantNotSetAndNotDerivedError() {
        try {
            DClare<BirdUniverse> dclare = of(BirdUniverse.class);
            start(dclare);
            addBird(dclare, Pheasant.class, Pair.of("0", "red"));
            stop(dclare);
            Assert.fail();
        } catch (Throwable t) {
            Throwable cause = getCause(t);
            assertThrowable(cause, Error.class, "Constant headColor is not set and not derived");
        }
    }

    @Test
    public void constantIsDerivedError() {
        try {
            DClare<BirdUniverse> dclare = of(BirdUniverse.class);
            start(dclare);
            addBird(dclare, Pheasant.class, Pair.of("0", "black"));
            stop(dclare);
            Assert.fail();
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
            Assert.fail();
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
        assertTrue(cause.getMessage() + " != " + regex, cause.getMessage().matches(regex));
    }

    private void assertThrowable(Throwable cause, Class<? extends Throwable> throwable, String message, Function<Throwable, String> f) {
        if (PRINT_STACK_TRACE || !throwable.equals(cause.getClass())) {
            cause.printStackTrace();
        }
        assertEquals(throwable, cause.getClass());
        assertEquals(message, f.apply(cause));
    }

}
