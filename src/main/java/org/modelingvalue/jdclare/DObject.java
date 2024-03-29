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

import static org.modelingvalue.jdclare.PropertyQualifier.*;

import java.io.PrintStream;
import java.io.PrintWriter;

import org.modelingvalue.collections.Collection;
import org.modelingvalue.collections.Entry;
import org.modelingvalue.collections.List;
import org.modelingvalue.collections.Map;
import org.modelingvalue.collections.Set;
import org.modelingvalue.collections.util.NonLockingPrintWriter;
import org.modelingvalue.collections.util.Pair;
import org.modelingvalue.collections.util.StringUtil;
import org.modelingvalue.dclare.*;
import org.modelingvalue.jdclare.meta.DClass;
import org.modelingvalue.jdclare.meta.DProperty;
import org.modelingvalue.jdclare.meta.DRule;
import org.modelingvalue.jdclare.meta.DStructClass;

@SuppressWarnings("unused")
@Extend(DClass.class)
public interface DObject extends DStruct, Mutable {

    @Override
    default State run(State state, MutableTransaction parent) {
        return Mutable.super.run(state, parent);
    }

    @Override
    default DObject dParent() {
        return (DObject) Mutable.super.dParent();
    }

    default DProperty<?, ?> dContainmentProperty() {
        return (DProperty<?, ?>) Mutable.super.dContaining().id();
    }

    @SuppressWarnings("unchecked")
    @Override
    default Collection<DObject> dChildren() {
        return (Collection<DObject>) Mutable.super.dChildren();
    }

    @SuppressWarnings("unchecked")
    @Override
    default Collection<DObject> dChildren(State state) {
        return (Collection<DObject>) Mutable.super.dChildren(state);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    default Collection<? extends Observer<?>> dAllObservers() {
        Collection<? extends Observer<?>> classObservers = Mutable.super.dAllObservers();
        Collection<? extends Observer<?>> objectObservers = (Collection) dObjectRules().map(DRule::observer);
        return Collection.concat(classObservers, objectObservers);
    }

    @Override
    default Setable<Mutable, ?> dContaining() {
        return Mutable.super.dContaining(); // do not remove this! it seems unneccesarry but it is not; this has to do with how Proxy handles calls.
    }

    @Override
    default <C> C dAncestor(Class<C> cls) {
        return Mutable.super.dAncestor(cls);
    }

    @Override
    default <T> T dParent(Class<T> cls) {
        return Mutable.super.dParent(cls);
    }

    @Override
    default void dActivate() {
        Mutable.super.dActivate(); // do not remove this! it seems unneccesarry but it is not; this has to do with how Proxy handles calls.
    }

    @Override
    default void dDeactivate(LeafTransaction tx) {
        Mutable.super.dDeactivate(tx); // do not remove this! it seems unneccesarry but it is not; this has to do with how Proxy handles calls.
    }

    @Override
    default boolean dHasAncestor(Mutable ancestor) {
        return Mutable.super.dHasAncestor(ancestor); // do not remove this! it seems unneccesarry but it is not; this has to do with how Proxy handles calls.
    }

    @Override
    default void dHandleRemoved(Mutable parent) {
        Mutable.super.dHandleRemoved(parent); // do not remove this! it seems unneccesarry but it is not; this has to do with how Proxy handles calls.
    }

    @Override
    default boolean dCheckConsistency() {
        return Mutable.super.dCheckConsistency(); // do not remove this! it seems unneccesarry but it is not; this has to do with how Proxy handles calls.
    }

    @Override
    default ConstantState dMemoization(AbstractDerivationTransaction tx) {
        return Mutable.super.dMemoization(tx); // do not remove this! it seems unneccesarry but it is not; this has to do with how Proxy handles calls.
    }

    @Override
    default Pair<Mutable, Setable<Mutable, ?>> dParentContaining() {
        return Mutable.super.dParentContaining(); // do not remove this! it seems unneccesarry but it is not; this has to do with how Proxy handles calls.
    }

    @Override
    default void dChangedParentContaining(Pair<Mutable, Setable<Mutable, ?>> pre, Pair<Mutable, Setable<Mutable, ?>> post) {
        Mutable.super.dChangedParentContaining(pre, post); // do not remove this! it seems unneccesarry but it is not; this has to do with how Proxy handles calls.
    }

    @Override
    default boolean dIsOrphan(State state) {
        return Mutable.super.dIsOrphan(state); // do not remove this! it seems unneccesarry but it is not; this has to do with how Proxy handles calls.
    }

    @Override
    default MutableTransaction openTransaction(MutableTransaction parent) {
        return Mutable.super.openTransaction(parent);
    }

    @Override
    default void closeTransaction(Transaction tx) {
        Mutable.super.closeTransaction(tx);
    }

    @Override
    default Mutable dResolve(Mutable self) {
        return this;
    }

    @Override
    default MutableTransaction newTransaction(UniverseTransaction universeTransaction) {
        return Mutable.super.newTransaction(universeTransaction);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Property({constant, hidden})
    default DClass<DObject> dClass() {
        DStructClass<DStruct> dStructClass = dStructClass();
        return (DClass) dStructClass;
    }

    @Property(hidden)
    Map<Object, Set<DProblem>> dProblemsMap();

    @SuppressWarnings("unchecked")
    @Property(hidden)
    default Set<DProblem> dProblems() {
        return Collection.concat(dClass().allValidations().flatMap(p -> (Collection<DProblem>) p.getCollection(this)), //
                dProblemsMap().flatMap(Entry::getValue)).asSet();
    }

    @Property(hidden)
    default Set<DProblem> dAllProblems() {
        return dProblems().addAll(dChildren().flatMap(DObject::dAllProblems));
    }

    @SuppressWarnings("rawtypes")
    @Property({containment, hidden})
    Set<DRule> dObjectRules();

    default String dString() {
        return dString("");
    }

    default String dString(String prefix) {
        var sb = new StringBuilder();
        dDump(NonLockingPrintWriter.of(sb::append), prefix);
        return sb.toString();
    }

    default void dDump(PrintStream stream) {
        dDump(stream, "");
    }

    default void dDump(PrintWriter writer) {
        dDump(writer, "");
    }

    default void dDump(PrintStream stream, String prefix) {
        dDump(new PrintWriter(stream, true), prefix);
    }

    @SuppressWarnings("unchecked")
    default void dDump(PrintWriter writer, String prefix) {
        DClass<DObject> cls = dClass();
        if (cls != null) {
            writer.println(prefix + this + " (" + cls.name() + ") {");
            for (DProperty<DObject, ?> p : cls.allNonContainments().sorted()) {
                if (p.visible()) {
                    writer.println(prefix + "  " + p.name() + " = " + StringUtil.toString(p.get(this)));
                }
            }
            for (DProperty<DObject, ?> p : cls.allContainments().sorted()) {
                if (p.visible()) {
                    Collection<DObject> coll = (Collection<DObject>) p.getCollection(this);
                    if (!coll.isEmpty()) {
                        writer.println(prefix + "  " + p.name() + " = {");
                        for (DObject child : (coll instanceof List ? coll : coll.sorted())) {
                            child.dDump(writer, prefix + "    ");
                        }
                        writer.println(prefix + "  }");
                    } else {
                        writer.println(prefix + "  " + p.name() + " = {}");
                    }
                }
            }
        } else {
            writer.println(prefix + this + " (???) {");
        }
        writer.println(prefix + "}");
    }

}
