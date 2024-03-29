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

package org.modelingvalue.jdclare;

import static org.modelingvalue.dclare.CoreSetableModifier.containment;
import static org.modelingvalue.dclare.CoreSetableModifier.mandatory;
import static org.modelingvalue.jdclare.PropertyQualifier.constant;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.*;
import java.time.Clock;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.modelingvalue.collections.*;
import org.modelingvalue.collections.util.*;
import org.modelingvalue.collections.util.ContextThread.ContextPool;
import org.modelingvalue.dclare.*;
import org.modelingvalue.jdclare.DNative.ChangeHandler;
import org.modelingvalue.jdclare.meta.*;

@SuppressWarnings({"unchecked", "rawtypes", "unused"})
public final class DClare<U extends DUniverse> extends UniverseTransaction {
    private static final int                                                      ANIMATE_DELAY_TIME = Integer.getInteger("ANIMATE_DELAY_TIME", 1);
    //
    private static final ContextPool                                              THE_POOL           = ContextThread.createPool();
    //
    private static final String                                                   DEFAULT            = "DEFAULT";
    private static final String                                                   CONSTRAINTS        = "CONSTRAINTS";
    //
    private static final Set<Class<?>>                                            DSTRUCTS           = Set.of(DStruct0.class, DStruct1.class, DStruct2.class, DStruct3.class, DStruct4.class, DStruct5.class, DStruct6.class, DStruct7.class, DStruct8.class, DStruct9.class, DStruct10.class);
    //
    private static final Method                                                   HASH_CODE          = method(Object::hashCode);
    private static final Method                                                   EQUALS             = method(Object::equals);
    private static final Method                                                   TO_STRING          = method(Object::toString);
    //
    public static final Method                                                    D_OBJECT_RULES     = method(DObject::dObjectRules);
    public static final Method                                                    D_OBJECT_CLASS     = DClare.<DObject, DClass> method(DObject::dClass);
    //
    private static final Method                                                   PACKAGES           = method(DPackageContainer::packages);
    private static final Method                                                   CLASSES            = method(DClassContainer::classes);
    //
    private static final Method                                                   GET_KEY            = method(DStruct::getKey);
    private static final Method                                                   GET_KEY_SIZE       = method(DStruct::getKeySize);
    private static final Method                                                   LOOKUP             = method(DStruct::lookup);
    //
    private static final Method                                                   PASSED_SECONDS     = method(DClock::passSeconds);

    public static final Observed<DUniverse, Integer>                              ROOT_RUN_NR        = Observed.of("dRootRunNr", 0);
    public static final Context<State>                                            CLASS_INIT_STATE   = Context.of();
    public static final Setable<Method, Method>                                   OPPOSITE           = Setable.of("dOpposite", null);
    public static final Setable<Method, Method>                                   SCOPE              = Setable.of("dScope", null);
    private static final Constant<Class<? extends DStruct>, State>                CLASS_INIT         = Constant.of("DStructClassInit", c -> {
                                                                                                         if (c.isInterface()) {
                                                                                                             return dClare().constraints(c);
                                                                                                         } else {
                                                                                                             throw new Error("Non Interface DObject Class '" + c + "'");
                                                                                                         }
                                                                                                     });
    private static final Constant<Class<? extends DStruct>, DStructClass>         CLASS              = Constant.<Class<? extends DStruct>, DStructClass> of("DStructClass", (Class<? extends DStruct> c) -> dclare(extend(c, DStructClass.class), c), (tx, c, o, d) -> {
                                                                                                         Class declaringClass = c.getDeclaringClass();
                                                                                                         if (declaringClass == null) {
                                                                                                             Package pack = c.getPackage();
                                                                                                             DClassContainer constainer = DClare.PACKAGE.get(pack != null ? pack.getName() : "<default>");
                                                                                                             DClare.<DClassContainer, Set<DStructClass>> setable(CLASSES).set(constainer, Set::add, d);
                                                                                                         } else {
                                                                                                             dClass(declaringClass);
                                                                                                         }
                                                                                                     });
    private static final Constant<Method, DProperty>                              PROPERTY           = Constant.<Method, DProperty> of("dProperty", (Method m) -> {
                                                                                                         if (m.getReturnType() != Void.TYPE && !m.isSynthetic() && m.getParameterCount() == 0 &&                                                                                               //
                                                                                                                 (ann(m, Property.class) != null || extend(m, DMethodProperty.class) != DMethodProperty.class)) {
                                                                                                             return dclare(extend(m, DMethodProperty.class), m);
                                                                                                         } else {
                                                                                                             return null;
                                                                                                         }
                                                                                                     }, (tx, m, o, p) -> {
                                                                                                         if (p != null) {
                                                                                                             dClass((Class) m.getDeclaringClass());
                                                                                                         }
                                                                                                     });
    private static final Constant<Pair<Class, LambdaReflection>, Method>          METHOD             = Constant.of("dMethod", p -> {
                                                                                                         Method method = p.b().implMethod();
                                                                                                         if (method.getDeclaringClass() != p.a()) {
                                                                                                             try {
                                                                                                                 return p.a().getMethod(method.getName(), method.getParameterTypes());
                                                                                                             } catch (NoSuchMethodException | SecurityException e) {
                                                                                                                 throw new Error(e);
                                                                                                             }
                                                                                                         } else {
                                                                                                             return method;
                                                                                                         }
                                                                                                     });
    private static final Constant<DProperty, Getable>                             GETABLE            = Constant.of("dGetable", p -> {
                                                                                                         Object def = p.key() ? null : p.defaultValue();
                                                                                                         SetableModifier[] mods = {                                                                                                                                                            //
                                                                                                                 mandatory.iff(p.mandatory()),                                                                                                                                                 //
                                                                                                                 containment.iff(p.containment())};
                                                                                                         Function der = p.derived() ? p.deriver() : null;
                                                                                                         DProperty oppos = p.opposite();
                                                                                                         DProperty scope = p.scopeProperty();
                                                                                                         Supplier<Setable<?, ?>> os = oppos != null ? () -> DClare.setable(oppos) : null;
                                                                                                         Supplier<Setable<DObject, Set<?>>> ss = scope != null ? () -> DClare.setable(scope) : null;
                                                                                                         return p.key() ? new KeyGetable(p, p.keyNr(), null) : p.constant() ? Constant.of(p, def, os, ss, der, mods) : Observed.of(p, def, os, ss, mods);
                                                                                                     });
    public static final Constant<Method, DMethodRule>                             RULE               = Constant.of("dRule", (Method m) -> {
                                                                                                         if (m.getParameterCount() == 0 && !m.isSynthetic() && (m.isDefault() || Modifier.isPrivate(m.getModifiers())) &&                                                                      //
                                                                                                                 (ann(m, Property.class) != null || ann(m, Rule.class) != null ||                                                                                                              //
                                                                                                                         extend(m, DMethodProperty.class) != DMethodProperty.class || extend(m, DMethodRule.class) != DMethodRule.class) &&                                                    //
                                                                                                                 !m.isAnnotationPresent(Default.class) && !qual(m, constant)) {
                                                                                                             return dclare(extend(m, DMethodRule.class), m);
                                                                                                         } else {
                                                                                                             return null;
                                                                                                         }
                                                                                                     });
    private static final Constant<String, DPackage>                               PACKAGE            = Constant.<String, DPackage> of("dPackage", (String n) -> {
                                                                                                         int i = n.lastIndexOf('.');
                                                                                                         if (i > 0) {
                                                                                                             DPackage pp = DClare.PACKAGE.get(n.substring(0, i));
                                                                                                             return dclare(DPackage.class, pp, n.substring(i + 1));
                                                                                                         } else {
                                                                                                             return dclare(DPackage.class, dUniverse(), n);
                                                                                                         }
                                                                                                     }, (tx, n, o, p) -> DClare.<DPackageContainer, Set<DPackage>> setable(PACKAGES).set(p.parent(), Set::add, p));
    private static final Constant<Class<? extends DStruct>, Lookup>               NATIVE_LOOKUP      = Constant.of("nLookup", c -> dStruct((Class<? extends DStruct>) c).lookup());
    public static final Constant<Method, Handle>                                  HANDLE             = Constant.of("nHandle", Handle::new);
    private static final Constant<Class<? extends DStruct>, Constructor<DNative>> NATIVE_CONSTRUCTOR = Constant.of("dNativeConstructor", c -> {
                                                                                                         Native ann = ann(c, Native.class);
                                                                                                         Constructor<DNative> constr = null;
                                                                                                         if (ann != null) {
                                                                                                             c = cls(c, Native.class);
                                                                                                             try {
                                                                                                                 constr = (Constructor<DNative>) ann.value().getConstructor(c);
                                                                                                             } catch (NoSuchMethodException | SecurityException e) {
                                                                                                                 throw new Error(e);
                                                                                                             }
                                                                                                         }
                                                                                                         return constr;
                                                                                                     });
    private static final Constant<DObject, DNative>                               NATIVE             = Constant.of("dNative", o -> {
                                                                                                         DNative dNative = null;
                                                                                                         Constructor<DNative> nativeConstructor = NATIVE_CONSTRUCTOR.get(jClass(o));
                                                                                                         if (nativeConstructor != null) {
                                                                                                             try {
                                                                                                                 dNative = nativeConstructor.newInstance(o);
                                                                                                             } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                                                                                                                 throw new Error(e);
                                                                                                             }
                                                                                                         }
                                                                                                         return dNative;
                                                                                                     });

    public static <U extends DUniverse> DClare<U> of(Class<U> universeClass) {
        return new DClare<>(universeClass, true, Clock.systemDefaultZone(), new DclareConfig().withMaxInInQueue(100).withDevMode(true));
    }

    public static <U extends DUniverse> DClare<U> of(Class<U> universeClass, DclareConfig config) {
        return new DClare<>(universeClass, true, Clock.systemDefaultZone(), config);
    }

    public static <U extends DUniverse> DClare<U> of(Class<U> universeClass, Clock clock) {
        return new DClare<>(universeClass, true, clock, new DclareConfig().withMaxInInQueue(100).withDevMode(true));
    }

    public static <U extends DUniverse> DClare<U> of(Class<U> universeClass, Clock clock, DclareConfig config) {
        return new DClare<>(universeClass, true, clock, config);
    }

    public static <U extends DUniverse> DClare<U> of(Class<U> universeClass, boolean checkFatals) {
        return new DClare<>(universeClass, checkFatals, Clock.systemDefaultZone(), new DclareConfig().withMaxInInQueue(100).withDevMode(true));
    }

    public static <U extends DUniverse> DClare<U> of(Class<U> universeClass, boolean checkFatals, DclareConfig config) {
        return new DClare<>(universeClass, checkFatals, Clock.systemDefaultZone(), config);
    }

    @SafeVarargs
    public static <U extends DUniverse> State run(Class<U> universeClass, Consumer<U>... steps) {
        return start(universeClass, true, steps).waitForEnd();
    }

    @SafeVarargs
    public static <U extends DUniverse> State runAndStop(Class<U> universeClass, Consumer<U>... steps) {
        DClare<U> root = start(universeClass, true, steps);
        root.stop();
        return root.waitForEnd();
    }

    @SafeVarargs
    public static <U extends DUniverse> State runAndRead(Class<U> universeClass, Consumer<U>... steps) {
        DClare<U> root = start(universeClass, true, steps);
        root.readInput();
        return root.waitForEnd();
    }

    @SafeVarargs
    public static <U extends DUniverse> DClare<U> start(Class<U> universeClass, boolean checkFatals, Consumer<U>... steps) {
        DClare<U> root = of(universeClass, checkFatals);
        root.start();
        U universe = root.universe();
        for (int i = 0; i < steps.length; i++) {
            Consumer<U> action = steps[i];
            root.put("$step" + i, () -> action.accept(universe));
        }
        return root;
    }

    public static void main(String[] args) {
        if (args.length == 1) {
            try {
                runAndStop((Class<? extends DUniverse>) Class.forName(args[0]));
            } catch (ClassNotFoundException e) {
                System.err.println("Class name '" + args[0] + "' of the universe no found.");
            }
        } else {
            System.err.println("Please provide one argument with the class name of the universe.");
        }
    }

    public static DClare<?> dClare() {
        return dClare(ActionTransaction.getCurrent());
    }

    public static DClare<?> dClare(Transaction tx) {
        return (DClare) tx.universeTransaction();
    }

    public static DUniverse dUniverse() {
        return dClare().universe();
    }

    @SafeVarargs
    public static <S extends DObject> S dclareUU(Class<S> jClass, Consumer<S>... inits) {
        return dclareUU(jClass, UUID.randomUUID(), inits);
    }

    @SafeVarargs
    public static <S extends DObject> S dclareUU(Class<S> jClass, UUID uuid, Consumer<S>... inits) {
        if (!DUUObject.class.isAssignableFrom(jClass)) {
            DSTRUCTS.filter(s -> s.isAssignableFrom(jClass)).forEachOrdered(s -> {
                throw new Error("No universally unique object of " + jClass.getSimpleName() + " allowed.");
            });
            return init(dStruct(jClass, DUUObject.class, uuid), inits);
        } else {
            return init(dStruct(jClass, uuid), inits);
        }
    }

    @SafeVarargs
    public static <S extends DStruct0> S dclare(Class<S> jClass, Consumer<S>... inits) {
        return init(dStruct(jClass), inits);
    }

    @SafeVarargs
    public static <S extends DStruct1<T0>, T0> S dclare(Class<S> jClass, T0 v0, Consumer<S>... inits) {
        return init(dStruct(jClass, v0), inits);
    }

    @SafeVarargs
    public static <S extends DStruct2<T0, T1>, T0, T1> S dclare(Class<S> jClass, T0 v0, T1 v1, Consumer<S>... inits) {
        return init(dStruct(jClass, v0, v1), inits);
    }

    @SafeVarargs
    public static <S extends DStruct3<T0, T1, T2>, T0, T1, T2> S dclare(Class<S> jClass, T0 v0, T1 v1, T2 v2, Consumer<S>... inits) {
        return init(dStruct(jClass, v0, v1, v2), inits);
    }

    @SafeVarargs
    public static <S extends DStruct4<T0, T1, T2, T3>, T0, T1, T2, T3> S dclare(Class<S> jClass, T0 v0, T1 v1, T2 v2, T3 v3, Consumer<S>... inits) {
        return init(dStruct(jClass, v0, v1, v2, v3), inits);
    }

    @SafeVarargs
    public static <S extends DStruct5<T0, T1, T2, T3, T4>, T0, T1, T2, T3, T4> S dclare(Class<S> jClass, T0 v0, T1 v1, T2 v2, T3 v3, T4 v4, Consumer<S>... inits) {
        return init(dStruct(jClass, v0, v1, v2, v3, v4), inits);
    }

    @SafeVarargs
    public static <S extends DStruct6<T0, T1, T2, T3, T4, T5>, T0, T1, T2, T3, T4, T5> S dclare(Class<S> jClass, T0 v0, T1 v1, T2 v2, T3 v3, T4 v4, T5 v5, Consumer<S>... inits) {
        return init(dStruct(jClass, v0, v1, v2, v3, v4, v5), inits);
    }

    @SafeVarargs
    public static <S extends DStruct7<T0, T1, T2, T3, T4, T5, T6>, T0, T1, T2, T3, T4, T5, T6> S dclare(Class<S> jClass, T0 v0, T1 v1, T2 v2, T3 v3, T4 v4, T5 v5, T6 v6, Consumer<S>... inits) {
        return init(dStruct(jClass, v0, v1, v2, v3, v4, v5, v6), inits);
    }

    @SafeVarargs
    public static <S extends DStruct8<T0, T1, T2, T3, T4, T5, T6, T7>, T0, T1, T2, T3, T4, T5, T6, T7> S dclare(Class<S> jClass, T0 v0, T1 v1, T2 v2, T3 v3, T4 v4, T5 v5, T6 v6, T7 v7, Consumer<S>... inits) {
        return init(dStruct(jClass, v0, v1, v2, v3, v4, v5, v6, v7), inits);
    }

    @SafeVarargs
    public static <S extends DStruct9<T0, T1, T2, T3, T4, T5, T6, T7, T8>, T0, T1, T2, T3, T4, T5, T6, T7, T8> S dclare(Class<S> jClass, T0 v0, T1 v1, T2 v2, T3 v3, T4 v4, T5 v5, T6 v6, T7 v7, T8 v8, Consumer<S>... inits) {
        return init(dStruct(jClass, v0, v1, v2, v3, v4, v5, v6, v7, v8), inits);
    }

    @SafeVarargs
    public static <S extends DStruct10<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9>, T0, T1, T2, T3, T4, T5, T6, T7, T8, T9> S dclare(Class<S> jClass, T0 v0, T1 v1, T2 v2, T3 v3, T4 v4, T5 v5, T6 v6, T7 v7, T8 v8, T9 v9, Consumer<S>... inits) {
        return init(dStruct(jClass, v0, v1, v2, v3, v4, v5, v6, v7, v8, v9), inits);
    }

    @SuppressWarnings("unused")
    public interface RuleConstructor<T extends DStruct> {
        void makeRule(T t);
    }

    public static <T extends DObject, V> Consumer<T> rule(SerializableFunction<T, V> prop, Function<T, V> val) {
        return o -> rule(o, prop, val);
    }

    public static <T extends DObject> Consumer<T> rule(String name, Consumer<T> rule) {
        return o -> rule(o, name, rule);
    }

    public static <T extends DObject, V> Consumer<T> set(SerializableFunction<T, V> prop, V val) {
        return o -> set(o, prop, val);
    }

    private static <S extends DStruct> S init(S o, Consumer<S>[] inits) {
        for (Consumer<S> init : inits) {
            init.accept(o);
        }
        return o;
    }

    public static <X extends DObject, Y extends DObject, A, B> void OPPOSITE(SerializableFunction<X, A> from, SerializableFunction<Y, B> to) {
        State state = CLASS_INIT_STATE.get();
        Method fromMethod = method(from);
        Method toMethod = method(to);
        state = state.set(fromMethod, OPPOSITE, toMethod);
        state = state.set(toMethod, OPPOSITE, fromMethod);
        CLASS_INIT_STATE.setOnThread(state);
    }

    public static <O extends DObject, A, B> void SCOPE(SerializableFunction<O, A> property, SerializableFunction<O, Collection<B>> scope) {
        CLASS_INIT_STATE.setOnThread(CLASS_INIT_STATE.get().set(method(property), SCOPE, method(scope)));
    }

    public static <O extends DObject, V> void rule(O dObject, SerializableFunction<O, V> property, Function<O, V> value) {
        Setable<DObject, Set<DRule>> ors = setable(D_OBJECT_RULES);
        DProperty dProperty = dProperty(dObject, property);
        ors.set(dObject, Set::add, dclare(DObjectRule.class, dObject, dProperty.name(), //
                set(DObjectRule::consumer, id((Consumer<O>) o -> dProperty.set(o, value.apply(o)), dObject, dProperty)), //
                set(DObjectRule::initPriority, Priority.one)));
    }

    public static <O extends DObject, V> void rule(O dObject, String name, Consumer<O> rule) {
        Setable<DObject, Set<DRule>> ors = setable(D_OBJECT_RULES);
        ors.set(dObject, Set::add, dclare(DObjectRule.class, dObject, name, //
                set(DObjectRule::consumer, id(rule, dObject, name)), //
                set(DObjectRule::initPriority, Priority.one)));
    }

    public static <O extends DObject, V> void set(O dObject, SerializableFunction<O, V> property, V value) {
        setable(method(dObject, property)).set(dObject, value);
    }

    public static <O extends DObject, V, E> void set(O dObject, SerializableFunction<O, V> property, BiFunction<V, E, V> function, E element) {
        DClare.<O, V> setable(method(dObject, property)).set(dObject, function, element);
    }

    public static <O extends DStruct, V> V get(O dObject, SerializableFunction<O, V> property) {
        return (V) getable(method(dObject, property)).get(dObject);
    }

    public static <O extends DStruct, V> V pre(O dObject, SerializableFunction<O, V> property) {
        ROOT_RUN_NR.get(dUniverse());
        return (V) getable(method(dObject, property)).pre(dObject);
    }

    public static <T> T pre(Supplier<T> supplier) {
        return dClare().preState().get(supplier);
    }

    public static <O extends DObject, D extends DObject, V> Set<D> opposite(O dObject, Class<D> cls, SerializableFunction<D, V> property) {
        return (Set) getable(dProperty(method(cls, property)).opposite()).getCollection(dObject).asSet();
    }

    public static <O extends DStruct, V> Collection<V> getCollection(O dObject, SerializableFunction<O, V> property) {
        return getable(method(dObject, property)).getCollection(dObject);
    }

    public static <O extends DStruct, V> DProperty<O, V> dProperty(O dObject, SerializableFunction<O, V> property) {
        return dProperty(method(dObject, property));
    }

    public static <O extends DStruct, V> DProperty<O, V> dProperty(Method method) {
        return PROPERTY.get(method);
    }

    public static <V> Method method(SerializableRunnable runnable) {
        return runnable.implMethod();
    }

    public static <V> Method method(SerializableConsumer<V> consumer) {
        return consumer.implMethod();
    }

    public static <V, W> Method method(SerializableBiConsumer<V, W> consumer) {
        return consumer.implMethod();
    }

    public static <U, V, W> Method method(SerializableTriConsumer<U, V, W> consumer) {
        return consumer.implMethod();
    }

    public static <O, V> Method method(SerializableFunction<O, V> function) {
        return function.implMethod();
    }

    public static <V, W, R> Method method(SerializableBiFunction<V, W, R> function) {
        return function.implMethod();
    }

    public static <U, V, W, R> Method method(SerializableTriFunction<U, V, W, R> function) {
        return function.implMethod();
    }

    private static <O extends DStruct, V> Method method(O dObject, SerializableFunction<O, V> property) {
        return method(jClass(dObject), property);
    }

    private static <O extends DStruct, V> Method method(Class<O> cls, SerializableFunction<O, V> property) {
        return METHOD.get(Pair.of(cls, property));
    }

    public static <O extends DStruct, V> int getNrOfObservers(O dObject, DProperty<O, V> property) {
        Getable<O, V> getable = getable(property.actualize(dObject.dStructClass()));
        return getable instanceof Observed ? ((Observed) getable).getNrOfObservers(dObject) : 0;
    }

    public static <O extends DStruct, V> V get(O dObject, DProperty<O, V> property) {
        Getable<O, V> getable = getable(property.actualize(dObject.dStructClass()));
        return getable.get(dObject);
    }

    public static <O extends DStruct, V> Collection<?> getCollection(O dObject, DProperty<O, V> property) {
        Getable<O, V> getable = getable(property.actualize(dObject.dStructClass()));
        return getable.getCollection(dObject);
    }

    public static <O extends DStruct, V> V set(O dObject, DProperty<O, V> property, V value) {
        Setable<O, V> setable = (Setable) getable(property.actualize(dObject.dStructClass()));
        return setable.set(dObject, value);
    }

    public static <O extends DStruct, V, E> void set(O dObject, DProperty<O, V> property, BiFunction<V, E, V> function, E element) {
        Setable<O, V> setable = (Setable) getable(property.actualize(dObject.dStructClass()));
        setable.set(dObject, function, element);
    }

    public static <T extends DObject, N extends DNative<T>> N dNative(T dObject) {
        return (N) NATIVE.get(dObject);
    }

    private State constraints(Class<? extends DStruct> cls) {
        for (Method method : cls.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Constraints.class)) {
                return CLASS_INIT_STATE.get(emptyState(), () -> {
                    run(dStruct(cls, CONSTRAINTS), method);
                    return CLASS_INIT_STATE.get();
                });
            }
        }
        return emptyState();
    }

    public static void clear(DObject dObject) {
        unparent(dObject);
        ActionTransaction.getCurrent().clear(dObject);
    }

    public static void unparent(DObject dObject) {
        DObject oldParent = dObject.dParent();
        DProperty cProperty = dObject.dContainmentProperty();
        if (oldParent != null && cProperty != null) {
            cProperty.set(oldParent, DClare::remove, dObject);
        }
    }

    private static <D> D getDefault(Class<D> type, Method method, boolean mandatory) {
        if (Set.class.equals(type)) {
            return (D) Set.of();
        } else if (List.class.equals(type)) {
            return (D) List.of();
        } else if (QualifiedSet.class.equals(type)) {
            return (D) QualifiedSet.of(DNamed::name);
        } else if (Map.class.equals(type)) {
            return (D) Map.of();
        } else if (float.class.equals(type)) {
            return (D) Float.valueOf(0.0f);
        } else if (double.class.equals(type)) {
            return (D) Double.valueOf(0.0d);
        } else if (long.class.equals(type)) {
            return (D) Long.valueOf(0L);
        } else if (int.class.equals(type)) {
            return (D) Integer.valueOf(0);
        } else if (short.class.equals(type)) {
            return (D) Short.valueOf((short) 0);
        } else if (byte.class.equals(type)) {
            return (D) Byte.valueOf((byte) 0);
        } else if (char.class.equals(type)) {
            return (D) Character.valueOf('\u0000');
        } else if (boolean.class.equals(type)) {
            return (D) Boolean.FALSE;
        } else if (mandatory) {
            Class<?> prim = primitiveClass(type);
            if (prim != null) {
                return (D) getDefault(prim, method, false);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public static <T extends DStruct> Class<T> jClass(T dObject) {
        return (Class<T>) dObject.getClass().getInterfaces()[0];
    }

    public static Class<?> interf(Object object) {
        return object.getClass().getInterfaces()[0];
    }

    public static <T extends DStruct> T dStruct(Class<T> jClass, Object... key) {
        return (T) Proxy.newProxyInstance(jClass.getClassLoader(), new Class<?>[]{jClass}, new DStructHandler(key, jClass));
    }

    public static <T extends DStruct, U extends DStruct> T dStruct(Class<T> jClass1, Class<U> jClass2, Object... key) {
        return (T) Proxy.newProxyInstance(jClass1.getClassLoader(), new Class<?>[]{jClass1, jClass2}, new DStructHandler(key, jClass1));
    }

    public static <O, V> Getable<O, V> getable(DProperty property) {
        return GETABLE.get(property);
    }

    private static <O, V> Getable<O, V> getable(Method method) {
        DProperty<DStruct, Object> property = dProperty(method);
        if (property == null) {
            throw new Error("Method " + method.getDeclaringClass().getSimpleName() + "::" + method.getName() + " is not a Property");
        }
        return getable(property);
    }

    private static <O, V> Setable<O, V> setable(Method method) {
        return (Setable<O, V>) getable(method);
    }

    public static <O, V> Setable<O, V> setable(DProperty property) {
        return (Setable<O, V>) getable(property);
    }

    public static <F> F id(F f, Object... key) {
        Class<?>[] interfaces = f.getClass().getInterfaces();
        return (F) Proxy.newProxyInstance(interfaces[0].getClassLoader(), interfaces, new IdHandler<>(interfaces[0], f, key));
    }

    private static final class IdHandler<F> implements InvocationHandler {
        private Object[]  key;
        private F         f;
        private final int hashCode;

        private IdHandler(Class<?> intf, F f, Object[] key) {
            this.key = key;
            this.f = f;
            this.hashCode = Arrays.hashCode(key) + intf.hashCode();
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.equals(TO_STRING)) {
                return "ID:" + interf(proxy).getSimpleName() + StringUtil.toString(key);
            } else if (method.equals(HASH_CODE)) {
                return hashCode;
            } else if (method.equals(EQUALS)) {
                if (proxy == args[0]) {
                    return true;
                } else if (args[0] == null) {
                    return false;
                } else if (args[0].getClass() != proxy.getClass()) {
                    return false;
                }
                IdHandler<F> other = (IdHandler<F>) Proxy.getInvocationHandler(args[0]);
                if (other.key == key) {
                    return true;
                } else if (!Arrays.equals(key, other.key)) {
                    return false;
                } else if (Age.age(key) > Age.age(other.key)) {
                    other.key = key;
                    f = other.f;
                    return true;
                } else {
                    key = other.key;
                    f = other.f;
                    return true;
                }
            } else {
                return method.invoke(f, args);
            }
        }
    }

    public static final Comparator<DStruct> COMPARATOR = (a, b) -> {
        if (a instanceof DNamed && b instanceof DNamed) {
            return ((DNamed) a).name().compareTo(((DNamed) b).name());
        }
        DStructClass aCls = a.dStructClass();
        DStructClass bCls = b.dStructClass();
        if (aCls.keys().equals(bCls.keys())) {
            DStructHandler x = handler(a);
            DStructHandler y = handler(b);
            for (int i = 0; i < x.key.length && i < y.key.length; i++) {
                int c = compare(x.key[i], y.key[i]);
                if (c != 0) {
                    return c;
                }
            }
            return 0;
        } else {
            return compare(aCls, bCls);
        }
    };

    private static int compare(Object a, Object b) {
        if (a instanceof Comparable) {
            return ((Comparable) a).compareTo(b);
        } else {
            return (a == null ? "" : StringUtil.toString(a)).compareTo(b == null ? "" : StringUtil.toString(b));
        }
    }

    public static String toString(DStruct o) {
        return jClass(o).getSimpleName() + StringUtil.toString(handler(o).key);
    }

    public static DStructHandler handler(DStruct o) {
        return (DStructHandler) Proxy.getInvocationHandler(o);
    }

    public static final class DStructHandler implements InvocationHandler {

        public Object[]   key;
        private final int hashCode;

        private DStructHandler(Object[] key, Class<? extends DStruct> intf) {
            this.key = key;
            this.hashCode = Arrays.hashCode(key) + intf.hashCode();
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.equals(LOOKUP)) {
                return MethodHandles.privateLookupIn(jClass((DStruct) proxy), MethodHandles.lookup());
            } else if (method.equals(TO_STRING)) {
                String clarification;
                try {
                    String asString = ((DStruct) proxy).asString();
                    if (asString != null) {
                        return asString;
                    }
                    clarification = "returned null]";
                } catch (VirtualMachineError e) {
                    // these are so fundamental, we do not want to swallow them
                    throw e;
                } catch (Throwable t) {
                    // the rest is ignored and we fall back on
                    clarification = "throwed " + t.getMessage();
                }
                return DClare.toString((DStruct) proxy) + " [asString() " + clarification + "]";
            } else if (method.equals(HASH_CODE)) {
                return hashCode;
            } else if (method.equals(GET_KEY)) {
                return key[(int) args[0]];
            } else if (method.equals(GET_KEY_SIZE)) {
                return key.length;
            } else if (method.equals(EQUALS)) {
                if (proxy == args[0]) {
                    return true;
                } else if (args[0] == null) {
                    return false;
                } else if (args[0].getClass() != proxy.getClass()) {
                    return false;
                }
                DStructHandler other = DClare.handler((DStruct) args[0]);
                if (other.key == key) {
                    return true;
                } else if (!Arrays.equals(key, other.key)) {
                    return false;
                } else if (Age.age(key) > Age.age(other.key)) {
                    other.key = key;
                    return true;
                } else {
                    key = other.key;
                    return true;
                }
            } else if (method.getParameterCount() == 0) {
                DProperty<DStruct, Object> dProperty = dProperty(method);
                if (dProperty != null) {
                    return getable(dProperty).get(proxy);
                }
            }
            Handle nFunction = HANDLE.get(method);
            if (nFunction.handle != null) {
                return nFunction.invoke(proxy, args);
            } else {
                throw new UnsupportedOperationException(method.toString());
            }
        }
    }

    public static State getConstraints(Method method) {
        Set<Class> supers = supers(Set.of(method.getDeclaringClass(), elementClass(method.getGenericReturnType())));
        return dClare().emptyState().merge(null, supers.map(CLASS_INIT::get).toArray(State[]::new), supers.size());
    }

    @SuppressWarnings("unlikely-arg-type")
    public static <O, E> Object remove(O o, E e) {
        return o instanceof ContainingCollection ? ((ContainingCollection) o).remove(e) : e.equals(o) ? null : o;
    }

    public static Class elementClass(Type type) {
        Class element = rawClass(type);
        if (type instanceof ParameterizedType && ContainingCollection.class.isAssignableFrom(element)) {
            Type[] ata = ((ParameterizedType) type).getActualTypeArguments();
            if (ata.length == 1) {
                element = rawClass(ata[0]);
            } else if (QualifiedSet.class.isAssignableFrom(element)) {
                element = rawClass(ata[1]);
            }
        }
        return element;
    }

    public static Class<?> rawClass(Type type) {
        return type instanceof Class ? (Class) type : type instanceof ParameterizedType ? rawClass(((ParameterizedType) type).getRawType()) : Object.class;
    }

    public final static class Handle {
        private final Method      method;
        public final MethodHandle handle;

        private Handle(Method method) {
            this.method = method;
            Class jClass = method.getDeclaringClass();
            boolean struct = DStruct.class.isAssignableFrom(jClass);
            if (struct && (method.getModifiers() & Modifier.ABSTRACT) == 0) {
                try {
                    this.handle = NATIVE_LOOKUP.get(jClass).unreflectSpecial(method, jClass);
                } catch (IllegalAccessException e) {
                    throw new Error(e);
                }
            } else {
                this.handle = null;
            }
        }

        private Object invoke(Object object, Object... args) throws Throwable {
            if (handle == null) {
                return method.invoke(object, args);
            } else {
                MethodHandle binded = handle.bindTo(object);
                return args == null || args.length == 0 ? binded.invoke() : binded.invokeWithArguments(args);
            }
        }

        @Override
        public String toString() {
            return StringUtil.toString(method);
        }

    }

    public static <T extends Annotation> T ann(Class<?> cls, Class<T> annotation) {
        return supers(null, cls, (a, c) -> c.isAnnotationPresent(annotation) ? c.getAnnotation(annotation) : a);
    }

    @SuppressWarnings("SameParameterValue")
    private static <T extends Annotation> Class cls(Class<?> cls, Class<T> annotation) {
        return supers(null, cls, (a, c) -> c.isAnnotationPresent(annotation) ? c : a);
    }

    public static <T extends Annotation> T ann(Method method, Class<T> annotation) {
        return overridden(null, method, (a, m) -> m.isAnnotationPresent(annotation) ? m.getAnnotation(annotation) : a);
    }

    public static boolean qual(Method method, PropertyQualifier q) {
        return overridden(false, method, (f, m) -> {
            Property p = m.getAnnotation(Property.class);
            if (p != null) {
                for (PropertyQualifier mq : p.value()) {
                    if (mq == q) {
                        return true;
                    }
                }
            }
            return f;
        });
    }

    public static void checkQualifiers(Method method) {
        if (qual(method, PropertyQualifier.mandatory) && qual(method, PropertyQualifier.optional)) {
            throw new Error("Illegal qualifier combination: mandatory and optional in: " + method);
        }
        if (qual(method, PropertyQualifier.mandatory) && qual(method, PropertyQualifier.softMandatory)) {
            throw new Error("Illegal qualifier combination: mandatory and softMandatory in: " + method);
        }
        if (qual(method, PropertyQualifier.optional) && qual(method, PropertyQualifier.softMandatory)) {
            throw new Error("Illegal qualifier combination: optional and softMandatory in: " + method);
        }
    }

    public static int key(Method method) {
        return overridden(-1, method, (k, m) -> {
            Property p = m.getAnnotation(Property.class);
            return p != null && p.key() >= 0 ? p.key() : k;
        });
    }

    @SuppressWarnings("SameParameterValue")
    private static <T extends Annotation> Method method(Method method, Class<T> annotation) {
        return overridden(null, method, (a, m) -> m.isAnnotationPresent(annotation) ? m : a);
    }

    public static <T> Class<? extends T> extend(Method method, Class<T> target) {
        //noinspection DuplicatedCode
        return overridden(target, method, (e, m) -> {
            for (Annotation ann : m.getAnnotations()) {
                Extend ext = ann.annotationType().getAnnotation(Extend.class);
                if (ext != null) {
                    for (Class val : ext.value()) {
                        if (e.isAssignableFrom(val)) {
                            e = val;
                        }
                    }
                }
            }
            Extend ext = m.getAnnotation(Extend.class);
            if (ext != null) {
                for (Class val : ext.value()) {
                    if (e.isAssignableFrom(val)) {
                        e = val;
                    }
                }
            }
            return e;
        });
    }

    @SuppressWarnings("SameParameterValue")
    private static <T> Class<? extends T> extend(Class cls, Class<T> target) {
        //noinspection DuplicatedCode
        return supers(target, cls, (e, c) -> {
            for (Annotation ann : c.getAnnotations()) {
                Extend ext = ann.annotationType().getAnnotation(Extend.class);
                if (ext != null) {
                    for (Class val : ext.value()) {
                        if (e.isAssignableFrom(val)) {
                            e = val;
                        }
                    }
                }
            }
            Extend ext = c.getAnnotation(Extend.class);
            if (ext != null) {
                for (Class val : ext.value()) {
                    if (e.isAssignableFrom(val)) {
                        e = val;
                    }
                }
            }
            return e;
        });
    }

    public static <T> T overridden(T init, Method method, BiFunction<T, Method, T> function) {
        for (Class sup : method.getDeclaringClass().getInterfaces()) {
            if (DStruct.class.isAssignableFrom(sup)) {
                try {
                    Method overridden = sup.getMethod(method.getName(), method.getParameterTypes());
                    init = overridden(init, overridden, function);
                } catch (NoSuchMethodException e) {
                    // ignored: no overridden method defined
                } catch (SecurityException e) {
                    throw new Error(e);
                }
            }
        }
        return function.apply(init, method);
    }

    public static <T> T supers(T init, Class<?> cls, BiFunction<T, Class<?>, T> function) {
        for (Class sup : cls.getInterfaces()) {
            if (DStruct.class.isAssignableFrom(cls)) {
                init = supers(init, sup, function);
            }
        }
        return function.apply(init, cls);
    }

    public static Function deriver(Method method) {
        Method defMethod = DClare.method(method, Default.class);
        boolean concrete = (method.getModifiers() & Modifier.ABSTRACT) == 0;
        return defMethod != method && concrete ? o -> DClare.run(o, method) : null;
    }

    public static Object defaultValue(Method method, boolean mandatory) {
        Method defMethod = DClare.method(method, Default.class);
        return defMethod != null ? run(dStruct((Class<DStruct>) defMethod.getDeclaringClass(), DEFAULT), defMethod) : getDefault(method.getReturnType(), method, mandatory);
    }

    private static Set<Class> supers(Set<Class> subs) {
        subs = subs.filter(DStruct.class::isAssignableFrom).asSet();
        return !subs.isEmpty() ? subs.addAll(supers(subs.flatMap(s -> Collection.of(s.getInterfaces())).asSet())) : subs;
    }

    public static Set<DStructClass> dSupers(Class cls) {
        Set<Class> supers = Set.of(cls.getInterfaces());
        Class s = cls.getSuperclass();
        return (s != null ? supers.add(s) : supers).map((Function<Class, DStructClass>) DClare::dClass).notNull().asSet();
    }

    public static Set<DStructClass<?>> dInnerClasses(Class cls) {
        Set<DStructClass<?>> inners = Set.of();
        for (Class inner : cls.getDeclaredClasses()) {
            DStructClass dClass = dClass(inner);
            if (dClass != null) {
                inners = inners.add(dClass);
            }
        }
        return inners;
    }

    private static Class<?> primitiveClass(Class<?> type) {
        if (Float.class.equals(type)) {
            return float.class;
        } else if (Double.class.equals(type)) {
            return double.class;
        } else if (Long.class.equals(type)) {
            return long.class;
        } else if (Integer.class.equals(type)) {
            return int.class;
        } else if (Short.class.equals(type)) {
            return short.class;
        } else if (Byte.class.equals(type)) {
            return byte.class;
        } else if (Character.class.equals(type)) {
            return char.class;
        } else if (Boolean.class.equals(type)) {
            return boolean.class;
        } else {
            return null;
        }
    }

    public static <T extends DStruct> DStructClass<T> dClass(Class<T> cls) {
        return DStruct.class.isAssignableFrom(cls) ? CLASS.get(cls) : null;
    }

    public static Method actualize(Class<?> intf, Method method) {
        if (intf == method.getDeclaringClass() || Modifier.isPrivate(method.getModifiers())) {
            return method;
        } else {
            try {
                return intf.getMethod(method.getName(), method.getParameterTypes());
            } catch (NoSuchMethodException | SecurityException e) {
                throw new Error(e);
            }
        }
    }

    public static Object run(Object self, Method method, Object... args) {
        Handle dHandle = HANDLE.get(method);
        try {
            return dHandle.invoke(self, args);
        } catch (Throwable e) {
            while (e instanceof InvocationTargetException) {
                e = e.getCause();
            }
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else if (e instanceof Error) {
                throw (Error) e;
            } else {
                throw new Error(e);
            }
        }
    }

    public static StateDeltaHandler callNativesOfClass(Class<? extends DObject> filterClass) {
        return new StateDeltaHandler() {

            private final Concurrent<Map<Pair<DNative, ChangeHandler>, Pair<Object, Object>>> deferred = Concurrent.of(Map.of());
            private final Concurrent<Map<Pair<DNative, ChangeHandler>, Pair<Object, Object>>> queue    = Concurrent.of(Map.of());

            @Override
            public void handleDelta(State pre, State post, boolean last, DefaultMap<Object, Set<Setable>> setted) {
                pre.diff(post, //
                        filterClass::isInstance, p -> true).forEachOrdered(e0 -> {
                            DObject dObject = (DObject) e0.getKey();
                            DNative no = NATIVE.get(dObject);
                            Pair<Pair, Pair> diff = (Pair) e0.getValue().get(Mutable.D_PARENT_CONTAINING);
                            if (diff != null) {
                                if (diff.a() == null) {
                                    no.init((DObject) diff.b().a());
                                    dObject.dClass().allProperties().forEachOrdered(p -> {
                                        ChangeHandler nch = p.nativeChangeHandler();
                                        if (nch != null) {
                                            change(no, nch, Pair.of(p.defaultValue(), p.get(dObject)));
                                        }
                                    });
                                } else if (diff.b() == null) {
                                    no.exit((DObject) diff.a().a());
                                }
                            } else if (dObject.dParentContaining() != null) {
                                e0.getValue().forEachOrdered(e1 -> {
                                    if (e1.getKey().id() instanceof DProperty && e1.getKey().getClass() == Observed.class) {
                                        DProperty<DStruct, Object> p = (DProperty) e1.getKey().id();
                                        ChangeHandler nch = p.nativeChangeHandler();
                                        if (nch != null) {
                                            change(no, nch, e1.getValue());
                                        }
                                    }
                                });
                            }
                        });
                run(queue);
                if (last) {
                    run(deferred);
                }
            }

            private void change(DNative no, ChangeHandler nch, Pair<Object, Object> val) {
                Pair<DNative, ChangeHandler> key = Pair.of(no, nch);
                if (nch.deferred()) {
                    deferred.change(m -> {
                        Pair<Object, Object> old = m.get(key);
                        return m.put(key, old != null ? Pair.of(old.a(), val.b()) : val);
                    });
                } else {
                    queue.change(m -> m.put(key, val));
                }
            }

            private void run(Concurrent<Map<Pair<DNative, ChangeHandler>, Pair<Object, Object>>> todo) {
                for (Entry<Pair<DNative, ChangeHandler>, Pair<Object, Object>> e : todo.result()) {
                    e.getKey().b().handle(e.getKey().a(), e.getValue().a(), e.getValue().b());
                }
                todo.init(Map.of());
            }

        };
    }

    public static int runNr() {
        return ROOT_RUN_NR.get(dUniverse());
    }

    // Instance part

    private final Clock                 clock;
    private Setable<DUniverse, Boolean> stopSetable;
    private Thread                      inputReader;
    private Timer                       timer;

    private final Action<Universe>      stop        = Action.of("$stop", o -> stopSetable.set(universe(), true));
    private final Action<Universe>      setTime     = Action.of("$setTime", o -> setTime());
    private final Action<Universe>      animate     = Action.of("$animate", o -> animate());
    private final Action<Universe>      printOutput = Action.of("$printOutput", o -> printOutput());
    private final Action<Universe>      restart     = Action.of("$restart", o -> restart());
    private final Action<Universe>      checkFatals;

    private DClare(Class<? extends DUniverse> universeClass, boolean checkFatals, Clock clock, DclareConfig config) {
        super(dStruct(universeClass), THE_POOL, config);
        this.checkFatals = checkFatals ? Action.of("$checkFatals", o -> checkFatals()) : null;
        this.clock = clock;
    }

    public Clock getClock() {
        return clock;
    }

    private void restart() {
        // do nothing
    }

    private void setTime() {
        ROOT_RUN_NR.set(universe(), Integer::sum, 1);
        set(dUniverse().clock(), DClock::time, clock.instant());
    }

    private void animate() {
        if (inQueue.isEmpty()) {
            DClock clock = dUniverse().clock();
            if (dProperty(PASSED_SECONDS).getNrOfObservers(clock) > 1) {
                if (timer == null) {
                    timer = new Timer(true);
                }
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        DClare.super.put(restart);
                    }
                }, ANIMATE_DELAY_TIME);
            }
        }
    }

    private void checkFatals() {
        Set<DProblem> fatals = universe().dAllProblems().filter(p -> p.severity() == DSeverity.fatal).asSet();
        if (!fatals.isEmpty()) {
            throw new Error("Fatal problems: " + fatals.toString().substring(3));
        }
    }

    private void printOutput() {
        IOString out = universe().output();
        int runNr = ROOT_RUN_NR.get(universe());
        if (out.nr() == runNr && !out.string().isEmpty()) {
            System.out.print(out.string());
            System.out.flush();
        }
        IOString err = universe().error();
        if (err.nr() == runNr && !err.string().isEmpty()) {
            System.err.print(err.string());
            System.err.flush();
        }
    }

    private synchronized void readInput() {
        if (inputReader == null) {
            inputReader = new Thread(() -> {
                @SuppressWarnings("resource")
                Scanner scanner = new Scanner(System.in);
                scanner.useDelimiter("\r\n|[\n\r\u2028\u2029\u0085]");
                while (true) {
                    String input = scanner.next();
                    put("$input", () -> set(universe(), DUniverse::input, dclare(IOString.class, runNr(), input)));
                }
            });
            inputReader.setDaemon(true);
            inputReader.start();
        }
    }

    public State run() {
        start();
        stop();
        return waitForEnd();
    }

    @Override
    protected void init() {
    }

    @Override
    public final U universe() {
        return (U) mutable();
    }

    @SuppressWarnings("UnusedReturnValue")
    private static KeyGetable cyclicKey(Method method, @SuppressWarnings("SameParameterValue") int nr) {
        DProperty property = dclare(extend(method, DMethodProperty.class), method);
        PROPERTY.force(method, property);
        KeyGetable getable = new KeyGetable(property, nr, null);
        GETABLE.force(property, getable);
        return getable;
    }

    @SuppressWarnings("UnusedReturnValue")
    private static Constant cyclicConstant(Method method, SetableModifier... modifiers) {
        DProperty property = dclare(extend(method, DMethodProperty.class), method);
        PROPERTY.force(method, property);
        Constant setable = Constant.of(property, defaultValue(method, false), deriver(method), modifiers);
        GETABLE.force(property, setable);
        return setable;
    }

    private static Observed cyclicObserved(Method method, SetableModifier... modifiers) {
        DProperty property = dclare(extend(method, DMethodProperty.class), method);
        PROPERTY.force(method, property);
        Observed setable = Observed.of(property, defaultValue(method, false), modifiers);
        GETABLE.force(property, setable);
        return setable;
    }

    public void start() {
        Action<Universe> bootstrap = Action.of("$bootstrap", o -> {
            cyclicKey(DClare.<DMethodProperty, Method> method(DMethodProperty::method), 0);
            cyclicKey(DClare.<DStructClass, Class> method(DStructClass::jClass), 0);
            cyclicConstant(DClare.<DMethodProperty, Class> method(DMethodProperty::objectClass));
            cyclicConstant(DClare.<DMethodProperty, Class> method(DMethodProperty::elementClass));
            cyclicConstant(DClare.<DMethodProperty, Boolean> method(DMethodProperty::key));
            cyclicConstant(DClare.<DMethodProperty, Integer> method(DMethodProperty::keyNr));
            cyclicConstant(DClare.<DMethodProperty, Object> method(DMethodProperty::validation));
            cyclicConstant(DClare.<DMethodProperty, Object> method(DMethodProperty::many));
            cyclicConstant(DClare.<DMethodProperty, Object> method(DMethodProperty::mandatory));
            cyclicConstant(DClare.<DMethodProperty, Object> method(DMethodProperty::defaultValue));
            cyclicConstant(DClare.<DMethodProperty, DProperty> method(DMethodProperty::implicitOpposite), containment);
            cyclicConstant(DClare.<DMethodProperty, Boolean> method(DMethodProperty::containment));
            cyclicConstant(DClare.<DMethodProperty, DProperty> method(DMethodProperty::opposite));
            cyclicConstant(DClare.<DMethodProperty, Boolean> method(DMethodProperty::softMandatory));
            cyclicConstant(DClare.<DMethodProperty, DProperty> method(DMethodProperty::scopeProperty));
            cyclicConstant(DClare.<DMethodProperty, Boolean> method(DMethodProperty::constant));
            cyclicConstant(DClare.<DMethodProperty, Boolean> method(DMethodProperty::derived));
            cyclicConstant(DClare.<DMethodProperty, Function> method(DMethodProperty::deriver));
            cyclicObserved(DClare.<DPackageContainer, Set> method(DPackageContainer::packages), containment);
            cyclicObserved(DClare.<DClassContainer, Set> method(DClassContainer::classes), containment);
            dClass(DMethodProperty.class);
            dClass(DStructClass.class);
            dClass(DClass.class);
            dClass(DPackageContainer.class);
            dClass(DClassContainer.class);
            dClass(DStruct.class);
            dClass(DObject.class);
            dClass(DNamed.class);
            stopSetable = cyclicObserved(DClare.method(DUniverse::stop));
            addPreAction(setTime);
            if (checkFatals != null) {
                addPostAction(checkFatals);
            }
            addPostAction(printOutput);
            addPostAction(animate);
        });
        addTimeTravelingAction(bootstrap);
        put(bootstrap);
        super.init();
    }

    private static class KeyGetable extends Getable<DStruct, Object> {

        private final int keyNr;

        protected KeyGetable(Object id, int keyNr, QuadConsumer<LeafTransaction, DStruct, Object, Object> changed) {
            super(id, null);
            this.keyNr = keyNr;
            if (changed != null) {
                throw new Error("No Containment nor Deriver allowed for key: " + this);
            }
        }

        @Override
        public Object get(DStruct object) {
            return handler(object).key[keyNr];
        }

    }

    @Override
    public boolean isStopped(State state) {
        return stopSetable != null && state.get(universe(), stopSetable);
    }

    @Override
    protected void end(State state) {
        if (timer != null) {
            timer.cancel();
        }
        super.end(state);
    }

    @Override
    public void stop() {
        if (timer != null) {
            timer.cancel();
        }
        put(stop);
    }

}
