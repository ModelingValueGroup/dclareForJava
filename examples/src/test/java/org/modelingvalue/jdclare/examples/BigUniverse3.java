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

package org.modelingvalue.jdclare.examples;

import static org.modelingvalue.jdclare.DClare.*;
import static org.modelingvalue.jdclare.PropertyQualifier.*;

import org.modelingvalue.collections.List;
import org.modelingvalue.jdclare.DNamed;
import org.modelingvalue.jdclare.DUniverse;
import org.modelingvalue.jdclare.Default;
import org.modelingvalue.jdclare.IOString;
import org.modelingvalue.jdclare.Property;
import org.modelingvalue.jdclare.Rule;

@SuppressWarnings("unused")
public interface BigUniverse3 extends DUniverse {

    static void main(String[] args) {
        runAndRead(BigUniverse3.class);
    }

    @Default
    @Property
    default int requiered() {
        return 100;
    }

    @Property(containment)
    default List<Element> elements() {
        List<Element> elements = elements();
        return requiered() > 0 ? (elements.isEmpty() ? List.of(dclareUU(Element.class)) : elements) : List.of();
    }

    interface Element extends DNamed {

        default int nr() {
            return dclare(BigUniverse3.class).elements().firstIndexOf(this);
        }

        @Override
        default String name() {
            return "E" + nr();
        }

        @Rule
        default void next() {
            BigUniverse3 u = dclare(BigUniverse3.class);
            int requiered = u.requiered();
            int nr = nr();
            int size = u.elements().size();
            if (nr < requiered && size < requiered) {
                set(u, BigUniverse3::elements, List::append, dclareUU(Element.class));
            } else if (nr > requiered) {
                set(u, BigUniverse3::elements, List::remove, this);
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
                set(this, BigUniverse3::requiered, Integer.parseInt(input));
            } catch (NumberFormatException nfe) {
                set(this, DUniverse::error, IOString.ofln("Only integer or 'stop' allowed"));
            }
        }
        set(this, DUniverse::input, IOString.of(""));
    }

}
