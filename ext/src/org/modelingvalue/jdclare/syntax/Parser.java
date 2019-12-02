//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
// (C) Copyright 2018 Modeling Value Group B.V. (http://modelingvalue.org)                                             ~
//                                                                                                                     ~
// Licensed under the GNU Lesser General Public License v3.0 (the "License"). You may not use this file except in      ~
// compliance with the License. You may obtain a copy of the License at: https://choosealicense.com/licenses/lgpl-3.0  ~
// Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on ~
// an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the  ~
// specific language governing permissions and limitations under the License.                                          ~
//                                                                                                                     ~
// Contributors:                                                                                                       ~
//     Wim Bast, Carel Bast, Tom Brus, Arjan Kok, Ronald Krijgsheld                                                    ~
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

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
        return lines().flatMap(LineParser::roots).toSet();
    }

    @Property(containment)
    default Set<LineParser> lines() {
        return text().tokenizer().lines().map(l -> dclare(LineParser.class, this, l)).toSet();
    }

    interface LineParser extends DStruct2<Parser<?, ?>, Line>, DObject {
        @Property(key = 0)
        Parser<?, ?> parser();

        @Property(key = 1)
        Line line();

        @Property(containment)
        default Set<TokenParser> tokens() {
            return line().tokens().map(t -> dclare(TokenParser.class, this, t)).toSet();
        }

        @Property
        default Set<NodeParser> roots() {
            return tokens().flatMap(TokenParser::roots).toSet();
        }

        interface TokenParser extends DStruct2<LineParser, Token>, DObject {

            @Property(key = 0)
            LineParser lineParser();

            @Property(key = 1)
            Token token();

            @Property(containment)
            default Set<NodeParser> parsers() {
                return token().terminals().flatMap(NodeParser::parsers).toSet();
            }

            @Property
            default Set<NodeParser> roots() {
                return parsers().filter(p -> p.root() && p.matched()).toSet();
            }

        }

    }

}
