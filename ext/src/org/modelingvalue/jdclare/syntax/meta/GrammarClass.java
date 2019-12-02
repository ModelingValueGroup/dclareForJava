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

package org.modelingvalue.jdclare.syntax.meta;

import org.modelingvalue.collections.*;
import org.modelingvalue.jdclare.*;
import org.modelingvalue.jdclare.meta.*;
import org.modelingvalue.jdclare.syntax.*;
import org.modelingvalue.jdclare.syntax.Grammar.*;
import org.modelingvalue.jdclare.syntax.regex.*;

import static org.modelingvalue.jdclare.DClare.*;
import static org.modelingvalue.jdclare.PropertyQualifier.*;

public interface GrammarClass<T extends Grammar> extends DClass<T> {

    @Property(constant)
    default DPattern newLinePattern() {
        return dclare(DPattern.class, "\\r?\\n");
    }

    @Property(constant)
    default Set<TokenType> skipped() {
        Skipped skipped = ann(jClass(), Skipped.class);
        return skipped == null ? Set.of() : Collection.of(skipped.value()).map(r -> {
            return dclare(TokenType.class, GrammarClass.this, r, null, set(TokenType::skipped, true));
        }).toSet();
    }

    @SuppressWarnings("rawtypes")
    @Property(constant)
    default Set<NodeClass> syntaxNodes() {
        return classes().filter(NodeClass.class).toSet();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Property(constant)
    default Set<SequenceType> sequences() {
        Collection<SequenceClass<Node>> sequences = (Collection) syntaxNodes().filter(SequenceClass.class);
        return sequences.flatMap(s -> Collection.concat(s, s.syntaxProperties().flatMap(SyntaxProperty::anonymousSequenceTypes))).toSet();
    }

    @SuppressWarnings("rawtypes")
    @Property(constant)
    default Set<TerminalClass> terminals() {
        return syntaxNodes().filter(TerminalClass.class).toSet();
    }

    @Property({constant, containment})
    default List<TokenType> tokens() {
        return Collection.concat(terminals().map(TerminalClass::token), skipped()).sorted(TokenType::compare).toList();
    }

    @Property(constant)
    default List<DPattern> tokenPatterns() {
        return tokens().map(TokenType::pattern).toList();
    }

}
