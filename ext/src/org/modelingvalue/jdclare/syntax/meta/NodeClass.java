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

package org.modelingvalue.jdclare.syntax.meta;

import static org.modelingvalue.jdclare.PropertyQualifier.*;

import org.modelingvalue.collections.Collection;
import org.modelingvalue.collections.Set;
import org.modelingvalue.jdclare.DClare;
import org.modelingvalue.jdclare.DProblem;
import org.modelingvalue.jdclare.Property;
import org.modelingvalue.jdclare.meta.DStructClass;
import org.modelingvalue.jdclare.syntax.Grammar.Node;
import org.modelingvalue.jdclare.syntax.parser.NodeParser;

public interface NodeClass<T extends Node> extends DStructClass<T>, NodeType {

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    @Property(constant)
    default GrammarClass<?> grammar() {
        return (GrammarClass) DClare.dClass((Class) jClass().getDeclaringClass());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Property(constant)
    default Set<NodeType> options() {
        Class cls = jClass();
        return Collection.of(cls.getDeclaringClass().getClasses()).filter(cls::isAssignableFrom).map(c -> (NodeType) DClare.dClass(c)).//
                filter(c -> c instanceof TerminalClass || c instanceof SequenceType).toSet();
    }

    @Override
    default boolean match(NodeType synt) {
        return options().contains(synt);
    }

    @Override
    default Set<TerminalClass<?>> firstTerminals(Set<NodeType> done) {
        return options().filter(t -> !done.contains(t)).flatMap(t -> t.firstTerminals(done.add(t))).toSet();
    }

    @Override
    default Set<TerminalClass<?>> lastTerminals(Set<NodeType> done) {
        return options().filter(t -> !done.contains(t)).flatMap(t -> t.lastTerminals(done.add(t))).toSet();
    }

    T create(NodeParser nodeParser, Set<DProblem>[] problems);

    @SuppressWarnings("unchecked")
    @Property(constant)
    default Set<SyntaxProperty<Node, Object>> syntaxProperties() {
        //REVIEW: this gives an error in IntelliJ but compiles ok with javac => report to Jetbrains
        return properties().filter(SyntaxProperty.class).toSet();
    }

}
