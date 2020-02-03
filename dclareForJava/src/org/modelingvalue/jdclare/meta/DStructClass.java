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

package org.modelingvalue.jdclare.meta;

import org.modelingvalue.collections.*;
import org.modelingvalue.collections.Collection;
import org.modelingvalue.collections.List;
import org.modelingvalue.collections.Set;
import org.modelingvalue.jdclare.*;

import java.lang.reflect.*;
import java.util.*;

import static org.modelingvalue.jdclare.PropertyQualifier.*;

public interface DStructClass<T extends DStruct> extends DClassContainer, DStruct1<Class<?>> {

    @SuppressWarnings("rawtypes")
    @Constraints
    private void metaRelations() {
        DClare.<DStructClass, DStructClass, Object, Object> OPPOSITE(DStructClass::allSupers, DStructClass::allSubs);
    }

    @SuppressWarnings({"rawtypes"})
    @Property(constant)
    default Set<DStructClass> allSupers() {
        return Collection.concat(this, supers().flatMap(DStructClass::allSupers)).toSet();
    }

    @SuppressWarnings("rawtypes")
    @Property(constant)
    default List<DStructClass> sortedSupers() {
        return allSupers().sorted((a, b) -> a.equals(b) ? 0 : b.isSubOf(a) ? 1 : a.isSubOf(b) ? -1 : 0).toList();
    }

    @SuppressWarnings("rawtypes")
    @Property
    Set<DStructClass> allSubs();

    @SuppressWarnings("rawtypes")
    default boolean isSubOf(DStructClass sup) {
        return allSupers().contains(sup);
    }

    @SuppressWarnings("rawtypes")
    default boolean isSuperOf(DStructClass sub) {
        return allSubs().contains(sub);
    }

    @Property(key = 0)
    Class<T> jClass();

    @Override
    @Property(constant)
    default String name() {
        return jClass().getSimpleName();
    }

    @SuppressWarnings("rawtypes")
    @Property(constant)
    default Set<DStructClass> supers() {
        return DClare.dSupers(jClass());
    }

    @Override
    @Property(constant)
    default Set<DStructClass<?>> classes() {
        return DClare.dInnerClasses(jClass());
    }

    @Property(constant)
    default boolean isAbstract() {
        return jClass().getAnnotation(Abstract.class) != null;
    }

    @SuppressWarnings("unchecked")
    @Property(constant)
    default QualifiedSet<String, DProperty<T, ?>> allProperties() {
        QualifiedSet<String, DProperty<T, ?>> result = QualifiedSet.of(DNamed::name);
        for (DStructClass<T> cls : sortedSupers()) {
            if (cls != null) {
                result = result.addAll(cls.properties().map(p -> p.actualize(this)));
            }
        }
        return result;
    }

    @Property(constant)
    default List<DProperty<T, ?>> keys() {
        return allProperties().filter(DProperty::key).sorted(Comparator.comparingInt(DProperty::keyNr)).toList();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Property({constant, containment})
    default Set<DProperty<T, ?>> properties() {
        Set<DProperty<T, ?>> properties = Set.of();
        for (Method method : jClass().getDeclaredMethods()) {
            DProperty p = DClare.dProperty(method);
            if (p != null) {
                properties = properties.add(p);
            }
        }
        return properties;
    }

}
