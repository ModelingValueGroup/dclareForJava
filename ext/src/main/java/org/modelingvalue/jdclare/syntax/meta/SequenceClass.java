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

import org.modelingvalue.collections.List;
import org.modelingvalue.collections.Set;
import org.modelingvalue.jdclare.*;
import org.modelingvalue.jdclare.syntax.Grammar.*;
import org.modelingvalue.jdclare.syntax.*;

import java.util.*;

import static org.modelingvalue.jdclare.DClare.*;

public interface SequenceClass<T extends Node> extends NodeClass<T>, SequenceType {

    @SuppressWarnings("resource")
    @Override
    default List<SequenceElement> sequenceElements() {
        Set<SequenceElement> elements = syntaxProperties().flatMap(SyntaxProperty::elements).asSet();
        int[] nr = new int[1];
        for (Class<? extends Node> cls : DClare.ann(jClass(), Sequence.class).value()) {
            while (elements.anyMatch(e -> e.nr() == nr[0] * SyntaxProperty.STEP_SIZE)) {
                nr[0]++;
            }
            elements = elements.add(dclare(SequenceElement.class, SyntaxProperty.STEP_SIZE * nr[0]++, (NodeClass<?>) DClare.dClass(cls), true, false, null));
        }
        return elements.sorted(Comparator.comparingInt(SequenceElement::nr)).asList();
    }

    @Override
    default boolean match(NodeType synt) {
        return equals(synt);
    }

    @Override
    default Set<TerminalClass<?>> firstTerminals(Set<NodeType> done) {
        return SequenceType.super.firstTerminals(done);
    }

    @Override
    default Set<TerminalClass<?>> lastTerminals(Set<NodeType> done) {
        return SequenceType.super.lastTerminals(done);
    }

}
