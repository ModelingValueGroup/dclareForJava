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

package org.modelingvalue.jdclare.meta;

import org.modelingvalue.collections.*;
import org.modelingvalue.jdclare.DNative.*;
import org.modelingvalue.jdclare.*;

import java.util.function.*;

import static org.modelingvalue.jdclare.PropertyQualifier.*;

public interface DOppositeProperty<O extends DObject, E extends DObject> extends DProperty<O, Set<E>>, DStruct1<DProperty<E, ?>> {

    @Override
    @Property(key = 0)
    DProperty<E, ?> opposite();

    @Override
    @Property(constant)
    default String name() {
        return "~" + opposite().name();
    }

    @Override
    @Property(constant)
    default boolean containment() {
        return false;
    }

    @SuppressWarnings("rawtypes")
    @Override
    @Property(constant)
    default Class elementClass() {
        return opposite().objectClass();
    }

    @SuppressWarnings("rawtypes")
    @Override
    @Property(constant)
    default Class type() {
        return Set.class;
    }

    @SuppressWarnings("rawtypes")
    @Override
    @Property(constant)
    default Class objectClass() {
        return opposite().elementClass();
    }

    @Override
    @Property(constant)
    default boolean validation() {
        return false;
    }

    @Override
    @Property(constant)
    default boolean visible() {
        return false;
    }

    @Override
    @Property(constant)
    default boolean mandatory() {
        return false;
    }

    @Override
    @Property(constant)
    default boolean many() {
        return true;
    }

    @Override
    @Property(constant)
    default boolean constant() {
        return false;
    }

    @Override
    @Property(constant)
    default boolean derived() {
        return false;
    }

    @Override
    @Property(constant)
    default boolean key() {
        return false;
    }

    @Override
    @Property(constant)
    default int keyNr() {
        return -1;
    }

    @Override
    @Property(constant)
    default DProperty<O, Set<?>> scopeProperty() {
        return null;
    }

    @Override
    default DProperty<O, Set<E>> actualize(DStructClass<?> dClass) {
        return this;
    }

    @Override
    @Property(constant)
    default Function<O, Set<E>> deriver() {
        return null;
    }

    @Override
    @Property(constant)
    default Set<E> defaultValue() {
        return Set.of();
    }

    @Override
    @Property(constant)
    default ChangeHandler<DObject, Set<E>> nativeChangeHandler() {
        return null;
    }

}
