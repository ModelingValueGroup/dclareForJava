//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
// (C) Copyright 2018-2021 Modeling Value Group B.V. (http://modelingvalue.org)                                        ~
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

package org.modelingvalue.jdclare.workbench;

import org.modelingvalue.collections.List;
import org.modelingvalue.collections.Set;
import org.modelingvalue.jdclare.*;
import org.modelingvalue.jdclare.meta.*;
import org.modelingvalue.jdclare.swing.*;

import java.util.*;

import static org.modelingvalue.jdclare.PropertyQualifier.*;
import static org.modelingvalue.jdclare.workbench.PropertiesTableColumn.*;

public interface PropertiesTable extends Table<DProperty<DObject, ?>, PropertiesTableColumn, Object>, DStruct1<WBUniverse> {

    @Property(key = 0)
    WBUniverse wb();

    @Override
    default List<DProperty<DObject, ?>> rowObjects() {
        DObject object = object();
        return object == null ? List.of() : object.dClass().allNonContainments().//
                filter(DProperty::visible).sorted().toList();
    }

    @Override
    @Property(constant)
    default List<PropertiesTableColumn> columnObjects() {
        return List.of(property, value);
    }

    @Override
    default int preferredWidth(PropertiesTableColumn column) {
        return column.width();
    }

    @Override
    default Object value(DProperty<DObject, ?> p, PropertiesTableColumn ct) {
        DObject object = object();
        return object != null ? ct.function().apply(object, p) : "";
    }

    @Override
    default boolean isEditable(DProperty<DObject, ?> p, PropertiesTableColumn ct) {
        if (PropertiesTableColumn.value == ct && !p.many() && !p.derived() && !p.key() && !p.constant()) {
            DObject object = object();
            return object.getKeySize() == 1 && object.getKey(0) instanceof UUID;
        } else {
            return false;
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    default void setValue(DProperty<DObject, ?> p, PropertiesTableColumn ct, Object value) {
        ((DProperty) p).set(object(), value);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    default List scope(DProperty<DObject, ?> p, PropertiesTableColumn ct) {
        DProperty<DObject, Set<?>> scopeProperty = p.scopeProperty();
        return scopeProperty != null ? scopeProperty.get(object()).sorted().toList() : null;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    default Class type(DProperty<DObject, ?> p, PropertiesTableColumn ct) {
        return p.type();
    }

    @Property(optional)
    default DObject object() {
        return wb().selected();
    }

}
