package org.modelingvalue.jdclare.swing.examples.sync;

import static org.modelingvalue.jdclare.DClare.*;

import java.awt.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

import org.modelingvalue.collections.Collection;
import org.modelingvalue.collections.List;
import org.modelingvalue.collections.Set;
import org.modelingvalue.dclare.*;
import org.modelingvalue.dclare.sync.*;
import org.modelingvalue.jdclare.*;
import org.modelingvalue.jdclare.meta.*;
import org.modelingvalue.jdclare.swing.draw2d.*;
import org.modelingvalue.jdclare.swing.examples.sync.D2Universe.*;

class SyncSerializationHelper implements SerializationHelper<DClass<DObject>, DObject, Setable<DObject, Object>> {
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public Predicate<Mutable> mutableFilter() {
        return m -> {
            boolean include = m instanceof DShape
                    || m instanceof DCanvas
                    || m instanceof D2Frame;
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
            boolean include = s instanceof Observed
                    && !s.toString().equals("D_PARENT_CONTAINING")
                    && !s.toString().equals("mode")
                    && !s.toString().equals("dObjectRules")
                    && !s.toString().startsWith("drag")
                    && !s.toString().startsWith("~");
            //                    if (!include) {
            //                        System.err.printf("----- setable rejected: %-50s %s\n", s, s.getClass().getSimpleName());
            //                    }
            return include;
        };
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public String serializeClass(DClass<DObject> clazz) {
        System.err.println("serializeClass " + clazz);
        return null;
    }

    @Override
    public String serializeSetable(Setable<DObject, Object> setable) {
        return setable.toString();
    }

    @Override
    public String serializeMutable(DObject mutable) {
        return Util.encodeWithLength(mutable.getClass().getInterfaces()[0].getName(), mutable.getKey(0).toString());
    }

    @Override
    public Object serializeValue(Setable<DObject, Object> setable, Object value) {
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

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public DClass<DObject> deserializeClass(String s) {
        //System.err.println("deserializeClass " + s);
        return null;
    }

    @Override
    public Setable<DObject, Object> deserializeSetable(DClass<DObject> clazz, String s) {
        //System.err.println("deserializeSetable: " + s);
        //clazz.dSetables().forEach(stbl -> System.err.println("  === " + stbl));
        return (Setable<DObject, Object>) clazz.dSetables().filter(stbl -> stbl.toString().equals(s)).findAny().orElseThrow();
    }

    @Override
    public DObject deserializeMutable(String s) {
        //System.err.println("deserializeMutable: " + s);
        try {
            String[] parts = Util.decodeFromLength(s, 2);
            //noinspection unchecked
            Class<? extends DObject> clazz = (Class<? extends DObject>) Class.forName(parts[0]);
            UUID                     uuid  = UUID.fromString(parts[1]);
            return dclareUU(clazz, uuid);
        } catch (ClassNotFoundException e) {
            throw new Error("can not deserializeMutable from " + s, e);
        }
    }

    @Override
    public Object deserializeValue(Setable<DObject, Object> setable, Object s) {
        DStruct  id   = (DStruct) setable.id();
        Method   o    = (Method) handler(id).key[0];
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
