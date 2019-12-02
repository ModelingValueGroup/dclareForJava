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

package org.modelingvalue.jdclare;

import org.modelingvalue.jdclare.meta.*;

import java.io.*;
import java.lang.invoke.MethodHandles.*;

import static org.modelingvalue.jdclare.PropertyQualifier.constant;
import static org.modelingvalue.jdclare.PropertyQualifier.*;

@Extend(DStructClass.class)
public interface DStruct extends Comparable<DStruct>, Serializable {

    int getKeySize();

    Object getKey(int i);

    @Property({constant, hidden})
    default DStructClass<DStruct> dStructClass() {
        return DClare.dClass(DClare.jClass(this));
    }

    @Override
    default int compareTo(DStruct other) {
        return DClare.COMPARATOR.compare(this, other);
    }

    default String asString() {
        return DClare.toString(this);
    }

    Lookup lookup();
}
