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

package org.modelingvalue.jdclare.swing.examples.sync;

import static org.modelingvalue.jdclare.DClare.*;
import static org.modelingvalue.jdclare.PropertyQualifier.*;

import java.awt.*;
import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;
import java.util.function.*;

import org.modelingvalue.collections.Collection;
import org.modelingvalue.collections.List;
import org.modelingvalue.collections.Map;
import org.modelingvalue.collections.Set;
import org.modelingvalue.dclare.*;
import org.modelingvalue.dclare.sync.*;
import org.modelingvalue.jdclare.*;
import org.modelingvalue.jdclare.meta.*;
import org.modelingvalue.jdclare.swing.Frame;
import org.modelingvalue.jdclare.swing.Panel;
import org.modelingvalue.jdclare.swing.*;
import org.modelingvalue.jdclare.swing.draw2d.*;

public interface D2Universe extends GuiUniverse {
    @Override
    default void init() {
        GuiUniverse.super.init();
        set(this, GuiUniverse::frames, Set.of(dclare(D2Frame.class, this)));
    }

    interface D2Frame extends Frame, DStruct1<D2Universe> {
        @Property(key = 0)
        D2Universe wb();

        @Override
        @Property(constant)
        default DComponent contentPane() {
            return dclare(D2Panel.class, wb());
        }

        @Override
        @Property(constant)
        default DMenubar menubar() {
            return dclare(D2Menubar.class, this);
        }

        @Override
        @Default
        @Property
        default DPoint location() {
            return dclare(DPoint.class, 50.0, 50.0);
        }

        @Override
        @Default
        @Property
        default DDimension preferredSize() {
            return dclare(DDimension.class, 1300.0, 900.0);
        }
    }

    interface D2Menubar extends DMenubar {

        @Override
        default List<DMenu> menus() {
            return List.of(dclare(DMenu.class, this, "File"));
        }
    }

    interface D2Panel extends Panel, DStruct1<D2Universe> {
        @Property(key = 0)
        D2Universe d2();

        @Override
        @Property(constant)
        default Map<DComponent, Object> content() {
            return Map.<DComponent, Object> of()//
                    .put(dclare(D2MainWindow.class, d2()), BorderLayout.CENTER);
        }

        @Override
        default LayoutManager layoutManager() {
            return new BorderLayout();
        }
    }

    static void main(String[] args) {
        DClare<D2Universe> dclare = start(D2Universe.class, false);
        DeltaAdaptor<DClass<DObject>, DObject, Setable<DObject, Object>> a = new DeltaAdaptor<>("sync", dclare.universeTransaction(), new SerializationHelper<>() {
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            @Override
            public Predicate<Mutable> mutableFilter() {
                return m -> {
                    boolean b = m instanceof DShape || m instanceof DCanvas;
                    //                    if (!b && !m.toString().matches("^(DClock|D2Universe|InputDeviceData).*")) {
                    //                        System.err.printf("----- mutable rejected: %-50s\n", m);
                    //                        if (m.toString().equals("shapes")) {
                    //                            b = false;
                    //                        }
                    //                    }
                    return b;
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
                    System.err.println("SEND value DOBJECT: " + value);
                    return serializeMutable((DObject) value);
                } else if (value instanceof DStruct) {
                    System.err.println("SEND value DSTRUCT: " + value);
                    return serializeDStruct((DStruct) value);
                } else if (value instanceof Color) {
                    System.err.println("SEND value COLOR  : " + value);
                    return ((Color) value).getRGB();
                } else {
                    System.err.println("SEND value        : " + value);
                    return value;
                }
            }

            private Object serializeDStruct(DStruct value) {
                return Collection.range(0, value.getKeySize()).map(value::getKey).toList();
            }

            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            @Override
            public DClass<DObject> deserializeClass(String s) {
                System.err.println("deserializeClass " + s);
                return null;
            }

            @Override
            public Setable<DObject, Object> deserializeSetable(DClass<DObject> clazz, String s) {
                System.err.println("deserializeSetable: " + s);
                //clazz.dSetables().forEach(stbl -> System.err.println("  === " + stbl));
                return (Setable<DObject, Object>) clazz.dSetables().filter(stbl -> stbl.toString().equals(s)).findAny().orElseThrow();
            }

            @Override
            public DObject deserializeMutable(String s) {
                System.err.println("deserializeMutable: " + s);
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
                    System.err.println("RECV value boolean   : " + s + "  " + setable.id() + "   " + type.getName() + "  =>  " + s);
                    return s;
                } else if (type.equals(DPoint.class)) {
                    DPoint pt = s == null ? null : dclare(DPoint.class, (Double) ((List) s).get(0), (Double) ((List) s).get(1));
                    System.err.println("RECV value DPoint    : " + s + "  " + setable.id() + "   " + type.getName() + "  =>  " + pt);
                    return pt;
                } else if (type.equals(DDimension.class)) {
                    DDimension pt = s == null ? null : dclare(DDimension.class, (Double) ((List) s).get(0), (Double) ((List) s).get(1));
                    System.err.println("RECV value DDimension: " + s + "  " + setable.id() + "   " + type.getName() + "  =>  " + pt);
                    return pt;
                } else if (type.equals(Color.class)) {
                    Color color = s == null ? null : new Color(((Number) s).intValue());
                    System.err.println("RECV value Color     : " + s + "  " + setable.id() + "   " + type.getName() + "  =>  " + color);
                    return color;
                } else if (s instanceof List && type.equals(List.class)) {
                    List l = ((List) s).map(ss -> deserializeMutable((String) ss)).toList();
                    System.err.println("RECV value MutList   : " + s + "  " + setable.id() + "   " + type.getName() + "  =>  " + l);
                    return l;
                } else if (s instanceof List && type.equals(Set.class)) {
                    Set l = ((List) s).map(ss -> deserializeMutable((String) ss)).toSet();
                    System.err.println("RECV value MutSet    : " + s + "  " + setable.id() + "   " + type.getName() + "  =>  " + l);
                    return l;
                } else {
                    System.err.println("\n==================================================================================================");
                    System.err.println("RECV value Other     : " + s + "  " + setable.id() + "   " + type.getName() + "  =>  " + s);
                    System.err.println("==================================================================================================\n");
                    return s;
                }
            }
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        });

        Thread thread = new Thread(() -> {
            try {
                int portNumber = 55055;
                switch (System.getProperty("ROLE")) {
                case "server":
                    System.err.println("SERVER");
                    Thread.sleep(1000);
                    try (
                            ServerSocket sock = new ServerSocket(portNumber);
                            Socket clientSocket = sock.accept();
                            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
                    ) {
                        while (true) {
                            String json = a.get();
                            out.write(json);
                            out.write('\n');
                            out.flush();
                        }
                    }
                case "client":
                    System.err.println("CLIENT");
                    Thread.sleep(1000);
                    while (true) {
                        try (
                                Socket sock = new Socket("localhost", portNumber);
                                PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
                                BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                        ) {
                            in.lines().forEach(l -> a.accept(l));
                        } catch (ConnectException e) {
                            Thread.sleep(100);
                        }
                    }
                default:
                    throw new Error("define role with -DROLE=(server|client)");
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }, "networker");
        thread.setDaemon(true);
        thread.start();

        dclare.waitForEnd();
    }
}
