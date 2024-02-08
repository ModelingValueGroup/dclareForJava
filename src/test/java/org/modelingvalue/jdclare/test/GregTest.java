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

import static org.modelingvalue.jdclare.DClare.dStruct;
import static org.modelingvalue.jdclare.DClare.set;
import static org.modelingvalue.jdclare.PropertyQualifier.containment;
import static org.modelingvalue.jdclare.PropertyQualifier.optional;

import org.modelingvalue.jdclare.*;

public interface GregTest extends DUniverse {

    public static void main(String[] args) {
        DClare.runAndStop(GregTest.class);
    }

    @Property(containment)
    default MyCount count() {
        return dStruct(MyCount.class, this);
    }

    interface MyCount extends DStruct1<GregTest>, DObject {
        @Property(key = 0)
        GregTest universe();

        @Property()
        @Default
        default long val() {
            return 0;
        }

        @Property({optional, containment})
        default MySetter setter() {
            if (val() > 10) {
                return null;
            }
            return dStruct(MySetter.class, this, this.val());
        }

    }

    interface MySetter extends DStruct2<MyCount, Long>, DObject {
        @Property(key = 0)
        MyCount count();

        @Property(key = 1)
        long value();

        @Rule
        default void rule() {
            set(count(), MyCount::val, count().val() + 1);
        }
    }
}
