//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
// (C) Copyright 2018-2022 Modeling Value Group B.V. (http://modelingvalue.org)                                        ~
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

package org.modelingvalue.jdclare.syntax.test;

import org.junit.jupiter.api.Test;
import org.modelingvalue.collections.List;
import org.modelingvalue.collections.Set;
import org.modelingvalue.dclare.Mutable;
import org.modelingvalue.dclare.State;
import org.modelingvalue.jdclare.DClare;
import org.modelingvalue.jdclare.DObject;
import org.modelingvalue.jdclare.DUniverse;
import org.modelingvalue.jdclare.syntax.Text;
import org.modelingvalue.jdclare.syntax.meta.GrammarClass;
import org.modelingvalue.jdclare.syntax.test.MySyntax.Unit;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.modelingvalue.jdclare.DClare.of;

public class SyntaxTests {

    private static final int     MANY_TIMES  = 32;
    private static final boolean DUMP        = Boolean.getBoolean("DUMP");
    private static final Clock   FIXED_CLOCK = Clock.fixed(Instant.EPOCH, ZoneId.systemDefault());

    //@Test
    public void manySyntax() {
        State prev = null;
        for (int i = 0; i < MANY_TIMES; i++) {
            // System.err.printf("# manySyntax %3d ", i);
            State next = doit();
            next.run(() -> test(next));
            if (prev != null) {
                String diff = prev.diffString(next, o -> true, s -> Mutable.D_CHANGE_ID != s);
                if (prev.equals(next)) {
                    //System.err.print("states equal" + ("".equals(diff) ? "" : " BUT DIFF != \"\""));
                } else {
                    //System.err.print("states differ");
                    assertEquals("", diff, "Diff: ");
                }
            }
            prev = next;
        }
    }

    private State doit() {
        DClare<TextUniverse> dClare = of(TextUniverse.class, FIXED_CLOCK);
        dClare.start();
        dClare.put("change0", () -> DClare.set(dClare.universe().text(), Text::string, STRING));
        dClare.put("change1", () -> DClare.set(dClare.universe().text(), Text::string, (s, e) -> s.replace("low", e), "lowest"));
        dClare.put("change2", () -> DClare.set(dClare.universe().text(), Text::string, (s, e) -> s.replace("aaa", e), "bbb"));
        dClare.stop();
        return dClare.waitForEnd();
    }

    private static final String STRING = List.of(//
            "package jdclare.test;", //
            "class Upper {", //
            "   Number low = 1000;", //
            "   String name = \"upper\";", //
            "   Lower aaa;", //
            "   Upper me = aaa.bbb;", //
            "}", //
            "class Lower {", //
            "   Number high = 10;", //
            "   String name = \"lower\";", //
            "   Upper bbb;", //
            "   Lower me = bbb.aaa;", //
            "}"//
    ).reduce("", (a, b) -> a.length() == 0 || b.length() == 0 ? a + b : a + "\n" + b);

    @Test
    public void oneSyntax() {
        State result = doit();
        result.run(() -> {
            if (DUMP) {
                System.err.println("******************************Begin State*************************************");
                System.err.println(result.asString());
                System.err.println("******************************End State***************************************");
                System.err.println("******************************Begin Syntax************************************");
                result.getObjects(GrammarClass.class).filter(s -> s.jClass() == MySyntax.class).forEachOrdered(u -> u.dDump(System.err));
                System.err.println("******************************End Syntax**************************************");
                System.err.println("******************************Begin Text**************************************");
                result.getObjects(Text.class).forEachOrdered(u -> u.dDump(System.err));
                System.err.println("******************************End Text****************************************");
                System.err.println("******************************Begin Root**************************************");
                result.getObjects(Unit.class).forEachOrdered(u -> u.dDump(System.err));
                System.err.println("******************************End Root****************************************");
                System.err.println("******************************Begin Problems**********************************");
                result.getObjects(DUniverse.class).forEachOrdered(u -> u.dAllProblems().forEachOrdered(System.err::println));
                System.err.println("******************************End Problems************************************");
            }
            test(result);
        });
    }

    private void test(State result) {
        assertEquals(Set.of(), result.getObjects(TextUniverse.class).filter(t -> t.text().root() == null).toSet(), "No Root");
        assertEquals(Set.of(), result.getObjects(TextUniverse.class).flatMap(DObject::dAllProblems).toSet(), "Problems");
    }

}
