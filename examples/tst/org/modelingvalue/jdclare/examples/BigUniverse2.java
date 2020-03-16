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

package org.modelingvalue.jdclare.examples;

import static org.modelingvalue.jdclare.DClare.*;
import static org.modelingvalue.jdclare.PropertyQualifier.*;

import org.modelingvalue.collections.Set;
import org.modelingvalue.jdclare.DNamed;
import org.modelingvalue.jdclare.DStruct1;
import org.modelingvalue.jdclare.DUniverse;
import org.modelingvalue.jdclare.Default;
import org.modelingvalue.jdclare.IOString;
import org.modelingvalue.jdclare.Property;
import org.modelingvalue.jdclare.Rule;

@SuppressWarnings("unused")
public interface BigUniverse2 extends DUniverse {

    static void main(String[] args) {
        runAndRead(BigUniverse2.class);
    }

    @Default
    @Property
    default int size() {
        return 100;
    }

    @Property(containment)
    default Set<Element> elements() {
        Set<Element> elements = elements();
        return size() > 0 ? (elements.isEmpty() ? Set.of(dclare(Element.class, 1)) : elements) : Set.of();
    }

    interface Element extends DNamed, DStruct1<Integer> {
        @Property(key = 0)
        int nr();

        @Override
        default String name() {
            return "E" + nr();
        }

        @Rule
        default void next() {
            BigUniverse2 u = dclare(BigUniverse2.class);
            int size = u.size();
            int nr = nr();
            if (nr < size) {
                set(u, BigUniverse2::elements, Set::add, dclare(Element.class, nr + 1));
            } else if (nr > size) {
                set(u, BigUniverse2::elements, Set::remove, this);
            }
        }
    }

    @Override
    default IOString output() {
        return IOString.of(elements() + System.lineSeparator() + "> ");
    }

    @Rule
    default void readInput() {
        String input = input().string().replaceAll("\\s+", "");
        if (input.equals("stop")) {
            set(this, DUniverse::stop, true);
        } else if (!input.isEmpty()) {
            try {
                set(this, BigUniverse2::size, Integer.parseInt(input));
            } catch (NumberFormatException nfe) {
                set(this, DUniverse::error, IOString.ofln("Only integer or 'stop' allowed"));
            }
        }
        set(this, DUniverse::input, IOString.of(""));
    }

}
