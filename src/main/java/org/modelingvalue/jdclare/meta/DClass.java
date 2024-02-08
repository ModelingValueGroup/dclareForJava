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

package org.modelingvalue.jdclare.meta;

import static org.modelingvalue.jdclare.PropertyQualifier.constant;
import static org.modelingvalue.jdclare.PropertyQualifier.containment;

import java.lang.reflect.Method;

import org.modelingvalue.collections.Collection;
import org.modelingvalue.collections.ContainingCollection;
import org.modelingvalue.collections.Entry;
import org.modelingvalue.collections.Map;
import org.modelingvalue.collections.Set;
import org.modelingvalue.dclare.Mutable;
import org.modelingvalue.dclare.MutableClass;
import org.modelingvalue.dclare.Observer;
import org.modelingvalue.dclare.Setable;
import org.modelingvalue.jdclare.DClare;
import org.modelingvalue.jdclare.DObject;
import org.modelingvalue.jdclare.Property;

public interface DClass<T extends DObject> extends DStructClass<T>, MutableClass {

    @SuppressWarnings({"rawtypes", "unchecked", "RedundantSuppression"})
    @Property(constant)
    default Set<DRule> allRules() {
        return allSupers().filter(DClass.class).<DRule> flatMap(DClass::rules).asSet();
    }

    @Property(constant)
    default Set<DProperty<T, ?>> allContainments() {
        return allProperties().filter(DProperty::containment).asSet();
    }

    @Property(constant)
    default Set<DProperty<T, ?>> allConstants() {
        return allProperties().filter(DProperty::constant).asSet();
    }

    @Property(constant)
    default Set<DProperty<T, ?>> allValidations() {
        return allProperties().filter(DProperty::validation).asSet();
    }

    @Property(constant)
    default Set<DProperty<T, ?>> allNonContainments() {
        return allProperties().filter(p -> !p.containment()).asSet();
    }

    @Property(constant)
    default Map<DProperty<T, ?>, DProperty<T, Set<?>>> scopedProperties() {
        return allProperties().map(p -> {
            DProperty<T, Set<?>> scope = p.scopeProperty();
            return scope != null ? Entry.<DProperty<T, ?>, DProperty<T, Set<?>>> of(p, scope) : null;
        }).notNull().asMap(e -> e);
    }

    @Property(constant)
    default Set<DProperty<T, ?>> mandatoryProperties() {
        return allProperties().filter(p -> !p.constant() && p.mandatory() && //
                (p.defaultValue() == null || p.defaultValue() instanceof ContainingCollection)).asSet();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Property({constant, containment})
    default Set<DRule<T>> rules() {
        Set<DRule<T>> rules = Set.of();
        for (Method method : jClass().getDeclaredMethods()) {
            DMethodRule r = DClare.RULE.get(method);
            if (r != null) {
                rules = rules.add(r);
            }
        }
        return rules;
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes", "RedundantSuppression"})
    default Collection<? extends Observer<?>> dObservers() {
        //noinspection RedundantCast
        return (Collection) allRules().map(DRule::observer);
    }

    @SuppressWarnings("rawtypes")
    @Override
    default Collection<Observer> dDerivers(Setable setable) {
        return MutableClass.super.dDerivers(setable);
    }

    @SuppressWarnings("unchecked")
    @Override
    default Collection<? extends Setable<? extends Mutable, ?>> dSetables() {
        return allProperties().map(DClare::getable).filter(Setable.class).map(s -> (Setable<? extends Mutable, ?>) s);
    }

    @Override
    default boolean isInternable() {
        return true;
    }

}
