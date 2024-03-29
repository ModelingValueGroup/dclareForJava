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

import static org.modelingvalue.jdclare.DClare.dclare;
import static org.modelingvalue.jdclare.DClare.set;
import static org.modelingvalue.jdclare.PropertyQualifier.containment;

import org.modelingvalue.collections.Set;
import org.modelingvalue.jdclare.Abstract;
import org.modelingvalue.jdclare.DNamed;
import org.modelingvalue.jdclare.DStruct1;
import org.modelingvalue.jdclare.DUniverse;
import org.modelingvalue.jdclare.Property;
import org.modelingvalue.jdclare.Rule;

public interface PrioUniverse extends DUniverse {

    @Property(containment)
    default Set<DNamed> prios() {
        return Set.of(dclare(PrioX.class, "X"), dclare(PrioY.class, "Y"), dclare(PrioZ.class, "Z"));
    }

    @Abstract
    interface Prio extends DNamed, DStruct1<String> {

        @Override
        @Property(key = 0)
        String name();

        @Property
        default String x() {
            return y();
        }

        @Property
        default String y() {
            return x();
        }
    }

    interface PrioX extends Prio {

        @Rule
        default void setY() {
            set(this, Prio::y, "X");
        }

    }

    interface PrioY extends Prio {

        @Rule
        default void setX() {
            set(this, Prio::x, "Y");
        }

    }

    interface PrioZ extends Prio {

        @Rule
        default void setY() {
            set(this, Prio::y, "Z");
        }

    }

}
