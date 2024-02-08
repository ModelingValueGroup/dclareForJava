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
                filter(c -> c instanceof TerminalClass || c instanceof SequenceType).asSet();
    }

    @Override
    default boolean match(NodeType synt) {
        return options().contains(synt);
    }

    @Override
    default Set<TerminalClass<?>> firstTerminals(Set<NodeType> done) {
        return options().filter(t -> !done.contains(t)).flatMap(t -> t.firstTerminals(done.add(t))).asSet();
    }

    @Override
    default Set<TerminalClass<?>> lastTerminals(Set<NodeType> done) {
        return options().filter(t -> !done.contains(t)).flatMap(t -> t.lastTerminals(done.add(t))).asSet();
    }

    T create(NodeParser nodeParser, Set<DProblem>[] problems);

    @SuppressWarnings("unchecked")
    @Property(constant)
    default Set<SyntaxProperty<Node, Object>> syntaxProperties() {
        //REVIEW: this gives an error in IntelliJ but compiles ok with javac => report to Jetbrains
        return properties().filter(SyntaxProperty.class).asSet();
    }

}
