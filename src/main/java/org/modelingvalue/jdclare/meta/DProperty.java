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

import static org.modelingvalue.jdclare.PropertyQualifier.optional;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.modelingvalue.collections.Collection;
import org.modelingvalue.collections.Set;
import org.modelingvalue.jdclare.Abstract;
import org.modelingvalue.jdclare.DClare;
import org.modelingvalue.jdclare.DNamed;
import org.modelingvalue.jdclare.DNative.ChangeHandler;
import org.modelingvalue.jdclare.DObject;
import org.modelingvalue.jdclare.DStruct;
import org.modelingvalue.jdclare.Default;
import org.modelingvalue.jdclare.Property;

@Abstract
public interface DProperty<O extends DStruct, V> extends DNamed {

    @Default
    @Property
    default boolean isAbstract() {
        return false;
    }

    @Property
    boolean key();

    @Property
    int keyNr();

    @Property
    boolean containment();

    @Property
    boolean constant();

    @Property
    boolean many();

    @Property
    boolean mandatory();

    @Property
    boolean derived();

    @Property(optional)
    Function<O, V> deriver();

    @Property
    boolean validation();

    @Property(optional)
    V defaultValue();

    @Property(optional)
    DProperty<?, ?> opposite();

    @Property(optional)
    DProperty<O, Set<?>> scopeProperty();

    @SuppressWarnings("rawtypes")
    @Property
    Class type();

    @SuppressWarnings("rawtypes")
    @Property
    Class elementClass();

    @SuppressWarnings("rawtypes")
    @Property
    Class objectClass();

    @Property(optional)
    ChangeHandler<DObject, V> nativeChangeHandler();

    @Default
    @Property
    default boolean visible() {
        return true;
    }

    @Default
    @Property
    default boolean softMandatory() {
        return false;
    }

    default V get(O object) {
        return DClare.get(object, this);
    }

    default int getNrOfObservers(O object) {
        return DClare.getNrOfObservers(object, this);
    }

    default Collection<?> getCollection(O object) {
        return DClare.getCollection(object, this);
    }

    default void set(O object, V value) {
        DClare.set(object, this, value);
    }

    default <E> void set(O object, BiFunction<V, E, V> function, E element) {
        DClare.set(object, this, function, element);
    }

    @SuppressWarnings("unchecked")
    default DProperty<O, V> actualize(DStructClass<?> dClass) {
        return key() ? this : (DProperty<O, V>) dClass.allProperties().get(name());
    }

}
