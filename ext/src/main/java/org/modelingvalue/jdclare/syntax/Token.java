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

package org.modelingvalue.jdclare.syntax;

import org.modelingvalue.collections.*;
import org.modelingvalue.jdclare.*;
import org.modelingvalue.jdclare.syntax.meta.*;
import org.modelingvalue.jdclare.syntax.parser.*;

import static org.modelingvalue.jdclare.DClare.*;
import static org.modelingvalue.jdclare.PropertyQualifier.*;

public interface Token extends DObject, DStruct2<Line, Long> {

    @Constraints
    private void constraints() {
        OPPOSITE(Token::previousToken, Token::nextToken);
    }

    @Property(key = 0)
    Line line();

    @Property(key = 1)
    long id();

    @Property(optional)
    TokenType type();

    @Property
    String value();

    @Property
    int startInLine();

    @Property
    default Set<TerminalParser> terminals() {
        TokenType type = type();
        return type != null ? type.terminals().map(s -> dclare(TerminalParser.class, s, this)).asSet() : Set.of();
    }

    @Property(constant)
    default Text<?, ?> text() {
        return line().text();
    }

    @Property(optional)
    Token nextToken();

    @Property(optional)
    Token previousToken();

    @Property(optional)
    default Token next() {
        Token next = nextToken();
        return next == null ? null : (next.type() == null || next.type().skipped()) ? next.next() : next;
    }

    @Property(optional)
    default Token previous() {
        Token prev = previousToken();
        return prev == null ? null : (prev.type() == null || prev.type().skipped()) ? prev.previous() : prev;
    }

    @Property(validation)
    default DProblem noType() {
        return type() == null ? dclare(DProblem.class, this, "TOKEN", DSeverity.error, "No token type defined for: " + value()) : null;
    }

    default int nr() {
        Token prev = previousToken();
        return prev != null && prev.line().equals(line()) ? prev.nr() + 1 : 0;
    }

    @Override
    default String asString() {
        return "T" + line().nr() + "." + nr();
    }

}
