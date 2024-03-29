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

package org.modelingvalue.jdclare.meta;

import static org.modelingvalue.jdclare.DClare.qual;
import static org.modelingvalue.jdclare.PropertyQualifier.constant;
import static org.modelingvalue.jdclare.PropertyQualifier.validation;

import java.lang.reflect.Method;
import java.util.function.Consumer;

import org.modelingvalue.dclare.Priority;
import org.modelingvalue.jdclare.DClare;
import org.modelingvalue.jdclare.DObject;
import org.modelingvalue.jdclare.DStruct1;
import org.modelingvalue.jdclare.Property;

public interface DMethodRule<O extends DObject, T> extends DRule<O>, DStruct1<Method> {

    @Property(key = 0)
    Method method();

    @Override
    @Property(constant)
    default String name() {
        Method method = method();
        return method.getDeclaringClass().getSimpleName() + "::" + method.getName();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    @Property(constant)
    default Consumer<O> consumer() {
        Method method = method();
        if (method.getReturnType() == Void.TYPE) {
            return o -> DClare.run(o, method);
        } else {
            DProperty p = DClare.dProperty(method);
            return o -> p.set(o, DClare.run(o, method));
        }
    }

    @Override
    @Property(constant)
    default boolean validation() {
        Method method = method();
        return qual(method, validation);
    }

    @Override
    @Property(constant)
    default Priority initPriority() {
        return validation() ? Priority.OUTER : Priority.one;
    }

}
