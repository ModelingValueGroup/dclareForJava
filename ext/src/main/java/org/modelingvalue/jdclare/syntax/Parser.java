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
import org.modelingvalue.jdclare.syntax.Grammar.*;
import org.modelingvalue.jdclare.syntax.parser.*;

import static org.modelingvalue.jdclare.DClare.*;
import static org.modelingvalue.jdclare.PropertyQualifier.*;

public interface Parser<S extends Grammar, R extends Node> extends DStruct1<Text<S, R>>, DObject {

    @Property(key = 0)
    Text<S, R> text();

    @Property
    default Set<NodeParser> roots() {
        return lines().flatMap(LineParser::roots).asSet();
    }

    @Property(containment)
    default Set<LineParser> lines() {
        return text().tokenizer().lines().map(l -> dclare(LineParser.class, this, l)).asSet();
    }

    interface LineParser extends DStruct2<Parser<?, ?>, Line>, DObject {
        @Property(key = 0)
        Parser<?, ?> parser();

        @Property(key = 1)
        Line line();

        @Property(containment)
        default Set<TokenParser> tokens() {
            return line().tokens().map(t -> dclare(TokenParser.class, this, t)).asSet();
        }

        @Property
        default Set<NodeParser> roots() {
            return tokens().flatMap(TokenParser::roots).asSet();
        }

        interface TokenParser extends DStruct2<LineParser, Token>, DObject {

            @Property(key = 0)
            LineParser lineParser();

            @Property(key = 1)
            Token token();

            @Property(containment)
            default Set<NodeParser> parsers() {
                return token().terminals().flatMap(NodeParser::parsers).asSet();
            }

            @Property
            default Set<NodeParser> roots() {
                return parsers().filter(p -> p.root() && p.matched()).asSet();
            }

        }

    }

}
