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

import org.modelingvalue.collections.*;
import org.modelingvalue.jdclare.*;

import static org.modelingvalue.jdclare.DClare.*;
import static org.modelingvalue.jdclare.PropertyQualifier.*;

public interface Orchestra extends DUniverse {

    @Property(containment)
    default Set<Instrument> instruments() {
        return Set.of(//
                dclare(Piano.class, "p", set(Piano::size, 12), rule(Piano::big, p -> p.size() > 24)), //
                dclare(Grand.class, "g"), //
                dclare(Mega.class, "m"));
    }

    interface Grand extends Piano {
        @Override
        @Property(constant)
        default int size() {
            return 36;
        }

        @Override
        default boolean big() {
            return size() > 24;
        }

    }

    interface Mega extends Piano {

        @Rule
        default void sizeRule() {
            set(this, Piano::size, 60);
        }

        @Rule
        default void bigRule() {
            set(this, Piano::big, size() > 24);
        }

    }

    @Override
    default void init() {
        DUniverse.super.init();
        dClare().addDiffHandler("pianoNative", callNativesOfClass(PianoKey.class));
    }
}
