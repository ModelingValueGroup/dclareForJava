//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
// (C) Copyright 2018-2020 Modeling Value Group B.V. (http://modelingvalue.org)                                        ~
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

import org.modelingvalue.jdclare.DObject;
import org.modelingvalue.jdclare.DUniverse;
import org.modelingvalue.jdclare.Default;
import org.modelingvalue.jdclare.IOString;
import org.modelingvalue.jdclare.Property;
import org.modelingvalue.jdclare.Rule;

public interface RecursiveUniverse2 extends DUniverse {

    static void main(String[] args) {
        runAndRead(RecursiveUniverse2.class);
    }

    @Default
    @Property
    default int depth() {
        return 3;
    }

    @Property({containment, constant})
    default Element element() {
        return dclareUU(Element.class);
    }

    interface Element extends DObject {
        @Property
        default int nr() {
            DObject p = dParent();
            return p instanceof Element ? ((Element) p).nr() + 1 : 0;
        }

        @Property({containment, optional})
        default Element child() {
            Element child = child();
            return nr() < dclare(RecursiveUniverse2.class).depth() ? (child == null ? dclareUU(Element.class) : child) : null;
        }
    }

    @Override
    default IOString output() {
        return IOString.of(element().dString() + "> ");
    }

    @Rule
    default void readInput() {
        String input = input().string().replaceAll("\\s+", "");
        if (input.equals("stop")) {
            set(this, DUniverse::stop, true);
        } else if (!input.isEmpty()) {
            try {
                set(this, RecursiveUniverse2::depth, Integer.parseInt(input));
            } catch (NumberFormatException nfe) {
                set(this, DUniverse::error, IOString.ofln("Only integer or 'stop' allowed"));
            }
        }
        set(this, DUniverse::input, IOString.of(""));
    }

}
