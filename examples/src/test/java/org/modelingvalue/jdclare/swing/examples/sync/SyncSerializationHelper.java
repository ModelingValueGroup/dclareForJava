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

package org.modelingvalue.jdclare.swing.examples.sync;

import static org.modelingvalue.jdclare.DClare.*;

import java.awt.Color;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.function.Predicate;

import org.modelingvalue.collections.Collection;
import org.modelingvalue.collections.List;
import org.modelingvalue.collections.Set;
import org.modelingvalue.dclare.Mutable;
import org.modelingvalue.dclare.Observed;
import org.modelingvalue.dclare.Setable;
import org.modelingvalue.dclare.sync.SerializationHelper;
import org.modelingvalue.dclare.sync.Util;
import org.modelingvalue.jdclare.DObject;
import org.modelingvalue.jdclare.DStruct;
import org.modelingvalue.jdclare.meta.DClass;
import org.modelingvalue.jdclare.swing.draw2d.DCanvas;
import org.modelingvalue.jdclare.swing.draw2d.DDimension;
import org.modelingvalue.jdclare.swing.draw2d.DPoint;
import org.modelingvalue.jdclare.swing.draw2d.DShape;
import org.modelingvalue.jdclare.swing.examples.sync.D2Universe.D2Frame;

class SyncSerializationHelper implements SerializationHelper<DClass<DObject>, DObject, Setable<DObject, Object>> {
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public Predicate<Mutable> mutableFilter() {
        return m -> {
            boolean include = m instanceof DShape || m instanceof DCanvas || m instanceof D2Frame;
            //                    if (!b && !m.toString().matches("^(DClock|D2Universe|InputDeviceData).*")) {
            //                        System.err.printf("----- mutable rejected: %-50s\n", m);
            //                        if (m.toString().equals("shapes")) {
            //                            b = false;
            //                        }
            //                    }
            return include;
        };
    }

    @Override
    public Predicate<Setable<DObject, ?>> setableFilter() {
        return s -> {
            boolean include = s instanceof Observed && !s.toString().equals("D_PARENT_CONTAINING") && !s.toString().equals("mode") && !s.toString().equals("dObjectRules") && !s.toString().startsWith("drag") && !s.toString().startsWith("~");
            //                    if (!include) {
            //                        System.err.printf("----- setable rejected: %-50s %s\n", s, s.getClass().getSimpleName());
            //                    }
            return include;
        };
    }

    @Override
    public DClass<DObject> getMutableClass(DObject s) {
        return s.dClass();
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String serializeSetable(Setable<DObject, Object> setable) {
        return setable.toString();
    }

    @Override
    public String serializeMutable(DObject mutable) {
        return Util.encodeWithLength(mutable.getClass().getInterfaces()[0].getName(), mutable.getKey(0).toString());
    }

    @Override
    public Object serializeValue(DObject dObject, Setable<DObject, Object> setable, Object value) {
        if (value instanceof DObject) {
            //System.err.println("SEND value DOBJECT: " + value);
            return serializeMutable((DObject) value);
        } else if (value instanceof DStruct) {
            //System.err.println("SEND value DSTRUCT: " + value);
            return Collection.range(0, ((DStruct) value).getKeySize()).map(((DStruct) value)::getKey).toList();
        } else if (value instanceof Color) {
            //System.err.println("SEND value COLOR  : " + value);
            return ((Color) value).getRGB();
        } else {
            //System.err.println("SEND value        : " + value);
            return value;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @SuppressWarnings("unchecked")
    @Override
    public Setable<DObject, Object> deserializeSetable(DClass<DObject> clazz, String s) {
        //System.err.println("deserializeSetable: " + s);
        //clazz.dSetables().forEach(stbl -> System.err.println("  === " + stbl));
        return (Setable<DObject, Object>) clazz.dSetables().filter(stbl -> stbl.toString().equals(s)).findAny().orElseThrow();
    }

    @SuppressWarnings("unchecked")
    @Override
    public DObject deserializeMutable(String s) {
        //System.err.println("deserializeMutable: " + s);
        try {
            String[] parts = Util.decodeFromLength(s, 2);
            //noinspection unchecked
            Class<? extends DObject> clazz = (Class<? extends DObject>) Class.forName(parts[0]);
            UUID uuid = UUID.fromString(parts[1]);
            return dclareUU(clazz, uuid);
        } catch (ClassNotFoundException e) {
            throw new Error("can not deserializeMutable from " + s, e);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Object deserializeValue(DObject dObject, Setable<DObject, Object> setable, Object s) {
        DStruct id = (DStruct) setable.id();
        Method o = (Method) handler(id).key[0];
        Class<?> type = o.getReturnType();
        if (type.equals(boolean.class)) {
            //System.err.println("RECV value boolean   : " + s + "  " + setable.id() + "   " + type.getName() + "  =>  " + s);
            return s;
        } else if (type.equals(DPoint.class)) {
            DPoint pt = s == null ? null : dclare(DPoint.class, (Double) ((org.modelingvalue.collections.List) s).get(0), (Double) ((org.modelingvalue.collections.List) s).get(1));
            //System.err.println("RECV value DPoint    : " + s + "  " + setable.id() + "   " + type.getName() + "  =>  " + pt);
            return pt;
        } else if (type.equals(DDimension.class)) {
            DDimension pt = s == null ? null : dclare(DDimension.class, (Double) ((org.modelingvalue.collections.List) s).get(0), (Double) ((org.modelingvalue.collections.List) s).get(1));
            //System.err.println("RECV value DDimension: " + s + "  " + setable.id() + "   " + type.getName() + "  =>  " + pt);
            return pt;
        } else if (type.equals(Color.class)) {
            Color color = s == null ? null : new Color(((Number) s).intValue());
            //System.err.println("RECV value Color     : " + s + "  " + setable.id() + "   " + type.getName() + "  =>  " + color);
            return color;
        } else if (s instanceof org.modelingvalue.collections.List && type.equals(org.modelingvalue.collections.List.class)) {
            org.modelingvalue.collections.List l = ((org.modelingvalue.collections.List) s).map(ss -> deserializeMutable((String) ss)).toList();
            //System.err.println("RECV value MutList   : " + s + "  " + setable.id() + "   " + type.getName() + "  =>  " + l);
            return l;
        } else if (s instanceof org.modelingvalue.collections.List && type.equals(Set.class)) {
            Set l = ((List) s).map(ss -> deserializeMutable((String) ss)).toSet();
            //System.err.println("RECV value MutSet    : " + s + "  " + setable.id() + "   " + type.getName() + "  =>  " + l);
            return l;
        } else {
            System.err.println("\n==================================================================================================");
            System.err.println("RECV value Other     : " + s + "  " + setable.id() + "   " + type.getName() + "  =>  " + s);
            System.err.println("==================================================================================================\n");
            return s;
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
