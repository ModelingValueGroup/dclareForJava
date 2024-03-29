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

import static org.modelingvalue.jdclare.DClare.D_OBJECT_CLASS;
import static org.modelingvalue.jdclare.DClare.OPPOSITE;
import static org.modelingvalue.jdclare.DClare.SCOPE;
import static org.modelingvalue.jdclare.DClare.ann;
import static org.modelingvalue.jdclare.DClare.checkQualifiers;
import static org.modelingvalue.jdclare.DClare.dProperty;
import static org.modelingvalue.jdclare.DClare.dclare;
import static org.modelingvalue.jdclare.DClare.getConstraints;
import static org.modelingvalue.jdclare.DClare.overridden;
import static org.modelingvalue.jdclare.DClare.qual;
import static org.modelingvalue.jdclare.DClare.rawClass;
import static org.modelingvalue.jdclare.PropertyQualifier.constant;
import static org.modelingvalue.jdclare.PropertyQualifier.containment;
import static org.modelingvalue.jdclare.PropertyQualifier.hidden;
import static org.modelingvalue.jdclare.PropertyQualifier.mandatory;
import static org.modelingvalue.jdclare.PropertyQualifier.optional;
import static org.modelingvalue.jdclare.PropertyQualifier.softMandatory;
import static org.modelingvalue.jdclare.PropertyQualifier.validation;
import static org.modelingvalue.jdclare.PropertyQualifier.visible;

import java.lang.reflect.Method;
import java.util.function.Function;

import org.modelingvalue.collections.ContainingCollection;
import org.modelingvalue.collections.Set;
import org.modelingvalue.collections.util.StringUtil;
import org.modelingvalue.dclare.State;
import org.modelingvalue.jdclare.DClare;
import org.modelingvalue.jdclare.DNative.ChangeHandler;
import org.modelingvalue.jdclare.DObject;
import org.modelingvalue.jdclare.DStruct;
import org.modelingvalue.jdclare.DStruct1;
import org.modelingvalue.jdclare.Native;
import org.modelingvalue.jdclare.Property;

public interface DMethodProperty<O extends DStruct, T> extends DProperty<O, T>, DStruct1<Method> {

    @Property(key = 0)
    Method method();

    @Override
    @Property(constant)
    default String name() {
        return method().getName();
    }

    @Override
    @Property(constant)
    default boolean containment() {
        Method method = method();
        return qual(method, containment);
    }

    @SuppressWarnings("rawtypes")
    @Override
    @Property(constant)
    default Class type() {
        Method method = method();
        return DClare.rawClass(method.getGenericReturnType());
    }

    @SuppressWarnings("rawtypes")
    @Override
    @Property(constant)
    default Class elementClass() {
        Method method = method();
        return DClare.elementClass(method.getGenericReturnType());
    }

    @SuppressWarnings("rawtypes")
    @Override
    @Property(constant)
    default Class objectClass() {
        Method method = method();
        return method.getDeclaringClass();
    }

    @Override
    @Property(constant)
    default boolean validation() {
        Method method = method();
        return qual(method, validation);
    }

    @Override
    @Property(constant)
    default boolean visible() {
        Method method = method();
        if (qual(method, visible)) {
            return true;
        } else if (qual(method, hidden)) {
            return false;
        } else {
            return keyNr() < 0 && !validation();
        }
    }

    @Override
    @Property(constant)
    default DProperty<?, ?> opposite() {
        Method method = method();
        State constraints = getConstraints(method);
        Method opposite = constraints != null ? overridden(null, method, (o, m) -> {
            Method oppos = constraints.get(m, OPPOSITE);
            return oppos != null ? oppos : o;
        }) : null;
        if (opposite != null) {
            return dProperty(opposite);
        } else {
            return implicitOpposite();
        }
    }

    @SuppressWarnings("unchecked")
    @Property({constant, containment, optional})
    default DOppositeProperty<?, ?> implicitOpposite() {
        Method method = method();
        State constraints = getConstraints(method);
        Method opposite = constraints != null ? overridden(null, method, (o, m) -> {
            Method oppos = constraints.get(m, OPPOSITE);
            return oppos != null ? oppos : o;
        }) : null;
        if (opposite == null && !D_OBJECT_CLASS.equals(method) && !containment() && !constant() && //
                DObject.class.isAssignableFrom(objectClass()) && DObject.class.isAssignableFrom(elementClass())) {
            return dclare(DOppositeProperty.class, this);
        } else {
            return null;
        }
    }

    @Override
    @Property(constant)
    default boolean mandatory() {
        Method method = method();
        checkQualifiers(method);
        if (qual(method, mandatory) || qual(method, softMandatory)) {
            return true;
        } else if (qual(method, optional)) {
            return false;
        } else {
            return !many() && !validation();
        }
    }

    @Override
    @Property(constant)
    default boolean softMandatory() {
        return qual(method(), softMandatory);
    }

    @Override
    @Property(constant)
    default boolean many() {
        Method method = method();
        return ContainingCollection.class.isAssignableFrom(rawClass(method.getGenericReturnType()));
    }

    @Override
    @Property(constant)
    default boolean constant() {
        Method method = method();
        return DClare.key(method) >= 0 || qual(method, constant) || !DObject.class.isAssignableFrom(method.getDeclaringClass());
    }

    @Override
    @Property(constant)
    default boolean derived() {
        return deriver() != null;
    }

    @Override
    @Property(constant)
    default boolean key() {
        Method method = method();
        return DClare.key(method) >= 0;
    }

    @Override
    @Property(constant)
    default int keyNr() {
        Method method = method();
        return DClare.key(method);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    @Property(constant)
    default DProperty<O, Set<?>> scopeProperty() {
        Method method = method();
        State constraints = getConstraints(method);
        Method scopeProperty = constraints != null ? overridden(null, method, (s, m) -> {
            Method scope = constraints.get(m, SCOPE);
            return scope != null ? scope : s;
        }) : null;
        if (scopeProperty != null) {
            DProperty prop = dProperty(scopeProperty);
            if (prop != null) {
                return prop;
            } else {
                throw new Error("Scope property " + StringUtil.toString(scopeProperty) + " not found");
            }
        }
        return null;
    }

    @Override
    default DProperty<O, T> actualize(DStructClass<?> dClass) {
        if (key()) {
            return this;
        } else {
            Method method = method();
            Method actual = DClare.actualize(dClass.jClass(), method);
            return actual != method ? dProperty(actual) : this;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    @Property(constant)
    default Function<O, T> deriver() {
        Method method = method();
        return DClare.deriver(method);
    }

    @SuppressWarnings("unchecked")
    @Override
    @Property(constant)
    default T defaultValue() {
        Method method = method();
        return (T) DClare.defaultValue(method, mandatory() && !constant());
    }

    @SuppressWarnings("rawtypes")
    @Override
    @Property(constant)
    default ChangeHandler<DObject, T> nativeChangeHandler() {
        Method method = method();
        Class pType = method.getReturnType();
        Native ann = ann(method.getDeclaringClass(), Native.class);
        if (ann != null) {
            try {
                return ChangeHandler.of(ann.value().getMethod(method.getName(), pType, pType));
            } catch (NoSuchMethodException e) {
                // ignored: no changeHandler method defined
            } catch (SecurityException e) {
                throw new Error(e);
            }
        }
        return null;
    }

}
