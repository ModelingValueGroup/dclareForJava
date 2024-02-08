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

package org.modelingvalue.jdclare.test;

import static org.junit.jupiter.api.Assertions.*;
import static org.modelingvalue.jdclare.DClare.dNative;
import static org.modelingvalue.jdclare.DClare.of;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.modelingvalue.collections.Collection;
import org.modelingvalue.collections.Set;
import org.modelingvalue.collections.util.Pair;
import org.modelingvalue.dclare.Mutable;
import org.modelingvalue.dclare.Priority;
import org.modelingvalue.dclare.State;
import org.modelingvalue.dclare.TransactionClass;
import org.modelingvalue.dclare.ex.OutOfScopeException;
import org.modelingvalue.jdclare.DClare;
import org.modelingvalue.jdclare.DNamed;
import org.modelingvalue.jdclare.DObject;
import org.modelingvalue.jdclare.DStruct;
import org.modelingvalue.jdclare.DUniverse;
import org.modelingvalue.jdclare.test.PrioUniverse.Prio;

public class JDclareTests {
    private static final int     MANY_TIMES  = 100;
    private static final boolean DUMP        = Boolean.getBoolean("DUMP");
    private static final Clock   FIXED_CLOCK = Clock.fixed(Instant.EPOCH, ZoneId.systemDefault());

    @Test
    public void manyUniverse() {
        for (int i = 0; i < MANY_TIMES; i++) {
            DClare<DUniverse> dClare = of(DUniverse.class);
            State result = dClare.run();
            result.run(() -> check(result));
        }
    }

    @Test
    public void universe() {
        DClare<DUniverse> dClare = of(DUniverse.class);
        State result = dClare.run();
        if (DUMP) {
            System.err.println("***************************** Begin DUniverse ***********************************");
            System.err.println(result.asString());
            System.err.println("***************************** End DUniverse *************************************");
        }
        result.run(() -> {
            if (DUMP) {
                System.err.println("******************************Begin Universe*******************************");
                result.getObjects(DUniverse.class).findAny().ifPresent(u -> u.dDump(System.err));
                System.err.println("******************************End Universe*********************************");
            }
            check(result);
        });
    }

    @Test
    public void manyOrchestra() {
        State prev = null;
        for (int i = 0; i < MANY_TIMES; i++) {
            DClare<Orchestra> dClare = of(Orchestra.class, FIXED_CLOCK);
            State next = dClare.run();
            next.run(() -> {
                check(next);
                checkOrchestra(next);
            });
            if (prev != null && !prev.equals(next)) {
                String diff = prev.diffString(next, o -> true, s -> Mutable.D_CHANGE_ID != s && DClare.ROOT_RUN_NR != s);
                assertEquals("", diff, "Diff: ");
            }
            prev = next;
        }
    }

    @Test
    public void orchestra() {
        DClare<Orchestra> dClare = of(Orchestra.class);
        State result = dClare.run();
        result.run(() -> {
            check(result);
            checkOrchestra(result);
            if (DUMP) {
                System.err.println("******************************Begin Orchestra*******************************");
                result.getObjects(Piano.class).forEachOrdered(u -> u.dDump(System.err));
                System.err.println("******************************End Orchestra*********************************");
            }
        });
    }

    private void checkOrchestra(State result) {
        Set<Piano> pianos = result.getObjects(Piano.class).asSet();
        assertEquals(3, pianos.size(), "Unexpected Pianos: " + pianos);
        pianos.forEachOrdered(p -> {
            assertTrue(p.size() > 0, "No Keys");
            assertEquals(p.size(), p.keys().size());
            assertEquals(p.big(), p.size() > 24);
            assertEquals(p.size() * 5 / 12, p.keys().filter(PianoKey::black).size());
            p.keys().forEachOrdered(k -> {
                KeyNative n = dNative(k);
                assertNotNull(n, "No KeyNative");
                assertEquals(1, n.inits(), "Init not 1:");
                assertEquals(k.black(), n.black(), "Black Native not equal:");
            });

        });
    }

    private void check(State result) {
        assertEquals(Set.of(), result.getObjects(DObject.class).filter(o -> !(o instanceof DUniverse) && o.dParent() == null).asSet(), "No Parent:");
        assertEquals(Set.of(), result.getObjects(DStruct.class).filter(o -> o.dStructClass() == null).asSet(), "No dStructType:");
        assertEquals(Set.of(), result.getObjects(DObject.class).filter(o -> o.dClass() == null).asSet(), "No dClass:");
        assertEquals(Set.of(), result.getObjects(DNamed.class).filter(o -> o.name() == null).asSet(), "No name:");
        assertEquals(Set.of(), result.getObjects(DUniverse.class).flatMap(DObject::dAllProblems).asSet(), "Problems:");
        Set<Pair<DObject, Set<TransactionClass>>> scheduled = result.getObjects(DObject.class).map(o -> Pair.of(o, //
                Collection.of(Priority.ALL).flatMap(prio -> Collection.concat(result.actions(prio).get(o), result.children(prio).get(o))).asSet())).filter(p -> !p.b().isEmpty()).asSet();
        // System.err.println(scheduled);
        assertEquals(Set.of(), scheduled, "Scheduled:");
    }

    @Test
    public void testReparents() {
        DClare<Reparents> dClare = of(Reparents.class);
        State result = dClare.run();
        result.run(() -> check(result));
        if (DUMP) {
            result.run(() -> {
                Optional<Reparents> r = result.getObjects(Reparents.class).findAny();
                r.ifPresent(u -> System.err.println(u.tree()));
                System.err.println("******************************Begin Reparents************************************");
                r.ifPresent(u -> u.tree().forEachOrdered(v -> v.dDump(System.err)));
                System.err.println("******************************End Reparents**************************************");
            });
        }
    }

    @Test
    public void testPriorities() {
        DClare<PrioUniverse> dClare = of(PrioUniverse.class);
        State result = dClare.run();
        result.run(() -> {
            check(result);
            checkPriorities(result);
        });
        if (DUMP) {
            result.run(() -> {
                System.err.println("******************************Begin Priorities************************************");
                for (Prio prio : result.getObjects(Prio.class)) {
                    prio.dDump(System.err);
                }
                System.err.println("******************************End Priorities**************************************");
            });
        }
    }

    @Test
    public void manyPriorities() {
        State prev = null;
        for (int i = 0; i < MANY_TIMES; i++) {
            DClare<PrioUniverse> dClare = of(PrioUniverse.class, FIXED_CLOCK);
            State next = dClare.run();
            next.run(() -> {
                check(next);
                checkPriorities(next);
            });
            if (prev != null && !prev.equals(next)) {
                String diff = prev.diffString(next, o -> true, s -> Mutable.D_CHANGE_ID != s);
                assertEquals("", diff, "Diff: ");
            }
            prev = next;
        }
    }

    private void checkPriorities(State result) {
        Set<Prio> prios = result.getObjects(Prio.class).asSet();
        assertEquals(3, prios.size(), "Unexpected Priorities: " + prios);
        prios.forEachOrdered(p -> {
            String target = p.dClass().name();
            target = target.substring(target.length() - 1);
            assertEquals(target, p.x());
            assertEquals(target, p.y());
        });
    }

    @Test
    public void testScrum() {
        DClare<Scrum> dClare = of(Scrum.class);
        dClare.start();
        dClare.put("company", () -> dClare.universe().initTest(dClare));
        dClare.stop();
        State result = dClare.waitForEnd();
        result.run(() -> {
            assertFalse(result.getObjects(Team.class).flatMap(Team::problems).isEmpty(), "Wim is has no Problems!");
            if (DUMP) {
                result.getObjects(Team.class).forEachOrdered(v -> v.dDump(System.err));
            }
        });
    }

    @Test
    public void testScopeProblem() {
        try {
            DClare<Scrum> dClare = of(Scrum.class);
            dClare.start();
            dClare.put("company", () -> dClare.universe().initScopeProblem(dClare));
            dClare.stop();
            dClare.waitForEnd();
            fail();
        } catch (Throwable t) {
            Throwable cause = getCause(t);
            assertThrowable(cause, OutOfScopeException.class, java.util.regex.Pattern.quote("The value 'Set[Pieter Puk]' of 'developers' of object 'DClare' is out of scope 'Set[]'"));
        }
    }

    private Throwable getCause(Throwable t) {
        while (t.getCause() != null) {
            t = t.getCause();
        }
        return t;
    }

    private void assertThrowable(Throwable cause, Class<? extends Throwable> throwable, String regex) {
        assertEquals(throwable, cause.getClass());
        assertTrue(cause.getMessage().matches(regex), cause.getMessage() + " != " + regex);
    }

}
