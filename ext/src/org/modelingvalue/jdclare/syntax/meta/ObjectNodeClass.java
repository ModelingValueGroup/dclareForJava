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

import org.modelingvalue.collections.*;
import org.modelingvalue.jdclare.*;
import org.modelingvalue.jdclare.meta.*;
import org.modelingvalue.jdclare.syntax.Grammar.*;
import org.modelingvalue.jdclare.syntax.parser.*;

import java.util.function.*;

import static org.modelingvalue.jdclare.DClare.*;
import static org.modelingvalue.jdclare.PropertyQualifier.*;

public interface ObjectNodeClass<T extends ObjectNode> extends DClass<T>, NodeClass<T> {

    @Override
    default T create(NodeParser nodeParser, Set<DProblem>[] problems) {
        return dclare(jClass(), nodeParser);
    }

    @SuppressWarnings("rawtypes")
    @Override
    @Property(constant)
    default Set<DRule<T>> rules() {
        Set<DRule<T>> rules = DClass.super.rules();
        if (this instanceof ObjectSequenceClass) {
            int i = 0;
            for (SequenceElement se : ((ObjectSequenceClass<T>) this).sequenceElements()) {
                SyntaxProperty property = se.property();
                if (property != null) {
                    rules = rules.add(syntaxRule(property, i));
                }
                i++;
            }
        } else if (this instanceof ObjectTerminalClass) {
            SyntaxProperty property = ((ObjectTerminalClass<T>) this).syntaxProperty();
            if (property != null) {
                rules = rules.add(syntaxRule(property, -1));
            }
        }
        return rules;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    default SyntaxPropertyRule<T> syntaxRule(SyntaxProperty property, int i) {
        return dclare(SyntaxPropertyRule.class, property, set(SyntaxPropertyRule::consumer, id((Consumer<T>) o -> {
            NodeParser    nodeParser    = o.sParserNode();
            ElementParser elementParser = nodeParser instanceof SequenceParser ? ((SequenceParser) nodeParser).sequenceElementParsers().get(i) : (ElementParser) nodeParser;
            Set<DProblem> problems      = property.transform(elementParser, v -> property.set(o, v), o);
            set(o, DObject::dProblemsMap, (m, p) -> m.put(property, p), problems);
        }, property)));
    }

}
