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
import org.modelingvalue.jdclare.meta.*;
import org.modelingvalue.jdclare.syntax.Grammar.*;
import org.modelingvalue.jdclare.syntax.parser.*;

import static org.modelingvalue.jdclare.DClare.*;

public interface StructNodeClass<T extends StructNode> extends NodeClass<T> {

    @Override
    default T create(NodeParser nodeParser, Set<DProblem>[] problems) {
        List<DProperty<T, ?>> keys = keys();
        Object[] key = new Object[keys.size()];
        for (DProperty<T, ?> kp : keys) {
            if (kp instanceof SyntaxProperty) {
                //noinspection unchecked
                SyntaxProperty<T, ?> sp = (SyntaxProperty<T, ?>) kp;
                ElementParser elementParser = nodeParser instanceof TerminalParser ? (TerminalParser) nodeParser : //
                        ((SequenceParser) nodeParser).sequenceElementParsers().get(sp.nr() * SyntaxProperty.STEP_SIZE);
                problems[0] = problems[0].addAll(sp.transform(elementParser, v -> key[sp.keyNr()] = v, null));
            }
        }
        return dStruct(jClass(), key);
    }

}
