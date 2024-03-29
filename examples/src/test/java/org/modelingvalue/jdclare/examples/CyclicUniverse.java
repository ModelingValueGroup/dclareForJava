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

package org.modelingvalue.jdclare.examples;

import org.modelingvalue.jdclare.*;

import static org.modelingvalue.jdclare.DClare.*;
import static org.modelingvalue.jdclare.PropertyQualifier.*;

public interface CyclicUniverse extends DUniverse {

    static void main(String[] args) {
        runAndRead(CyclicUniverse.class);
    }

    interface A extends DStruct0, DObject {
        @Property
        default int x() {
            return dAncestor(CyclicUniverse.class).c().y();
        }

        @Property
        default int y() {
            return x();
        }
    }

    interface B extends DStruct0, DObject {
        @Property
        default int x() {
            return dAncestor(CyclicUniverse.class).a().y();
        }

        @Property
        default int y() {
            return x();
        }
    }

    interface C extends DStruct0, DObject {
        @Property
        default int x() {
            return dAncestor(CyclicUniverse.class).b().y();
        }

        @Property
        default int y() {
            return x();
        }
    }

    @Property({containment, constant})
    default A a() {
        return dclare(A.class);
    }

    @Property({containment, constant})
    default B b() {
        return dclare(B.class);
    }

    @Property({containment, constant})
    default C c() {
        return dclare(C.class);
    }

    @Override
    default IOString output() {
        return IOString.of("a.y=" + a().y() + " b.y=" + b().y() + " c.y=" + c().y() + System.lineSeparator() + "> ");
    }

    @Rule
    default void readInput() {
        String input = input().string().replaceAll("\\s+", "");
        if (input.equals("stop")) {
            set(this, DUniverse::stop, true);
        } else if (input.startsWith("a=")) {
            try {
                set(a(), A::x, Integer.parseInt(input.substring(2)));
            } catch (NumberFormatException nfe) {
                set(this, DUniverse::error, IOString.ofln("Only integers after 'a=' allowed"));
            }
        } else if (input.startsWith("b=")) {
            try {
                set(b(), B::x, Integer.parseInt(input.substring(2)));
            } catch (NumberFormatException nfe) {
                set(this, DUniverse::error, IOString.ofln("Only integers after 'b=' allowed"));
            }
        } else if (input.startsWith("c=")) {
            try {
                set(c(), C::x, Integer.parseInt(input.substring(2)));
            } catch (NumberFormatException nfe) {
                set(this, DUniverse::error, IOString.ofln("Only integers after 'c=' allowed"));
            }
        } else if (!input.isEmpty()) {
            set(this, DUniverse::error, IOString.ofln("Only 'stop', 'a=<int>' or 'b=<int>' or 'c=<int>'"));
        }
        set(this, DUniverse::input, IOString.of(""));
    }

}
