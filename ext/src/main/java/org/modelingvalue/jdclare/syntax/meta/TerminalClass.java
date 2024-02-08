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

import org.modelingvalue.collections.*;
import org.modelingvalue.jdclare.*;
import org.modelingvalue.jdclare.syntax.Grammar.*;
import org.modelingvalue.jdclare.syntax.*;

import java.util.regex.*;

import static org.modelingvalue.jdclare.DClare.*;
import static org.modelingvalue.jdclare.PropertyQualifier.*;

public interface TerminalClass<T extends Node> extends NodeClass<T> {

    @Constraints
    private void tConstraints() {
        DClare.<TerminalClass<?>, TokenType, TokenType, Set<TerminalClass<?>>> OPPOSITE(TerminalClass::token, TokenType::terminals);
    }

    @Property(constant)
    default TokenType token() {
        return dclare(TokenType.class, grammar(), regex(), literal(), set(TokenType::skipped, false));
    }

    private String regex() {
        String literal = literal();
        if (literal != null) {
            return Pattern.quote(literal);
        } else {
            Regex regex = ann(jClass(), Regex.class);
            return regex != null ? regex.value() : ".+";
        }
    }

    private String literal() {
        Literal literal = ann(jClass(), Literal.class);
        return literal != null ? literal.value() : null;
    }

    @Override
    default boolean match(NodeType synt) {
        return equals(synt);
    }

    @Override
    default Set<TerminalClass<?>> firstTerminals(Set<NodeType> done) {
        return Set.of(this);
    }

    @Override
    default Set<TerminalClass<?>> lastTerminals(Set<NodeType> done) {
        return Set.of(this);
    }

    @Property(constant)
    default SyntaxProperty<Node, Object> syntaxProperty() {
        Collection<SyntaxProperty<Node, Object>> properties = syntaxProperties();
        return properties.findAny().orElse(null);
    }

}
