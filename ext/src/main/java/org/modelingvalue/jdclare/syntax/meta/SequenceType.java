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

import static org.modelingvalue.jdclare.PropertyQualifier.*;

public interface SequenceType extends NodeType {

    @Property(constant)
    List<SequenceElement> sequenceElements();

    @Property(constant)
    default SequenceElement startElement() {
        List<SequenceElement> singletons = sequenceElements().filter(e -> !e.many() && e.mandatory()).asList();
        return singletons.filter(e -> e.nodeType() instanceof TerminalClass && ((TerminalClass<?>) e.nodeType()).token().literal() != null).findFirst().orElse(singletons.first());
    }

    @Property(constant)
    default NodeType startClass() {
        SequenceElement startElement = startElement();
        return startElement != null ? startElement.nodeType() : null;
    }

    @Override
    @Property(constant)
    default Set<TerminalClass<?>> firstTerminals(Set<NodeType> done) {
        NodeType nodeType = sequenceElements().first().nodeType();
        return !done.contains(nodeType) ? nodeType.firstTerminals(done) : Set.of();
    }

    @Override
    @Property(constant)
    default Set<TerminalClass<?>> lastTerminals(Set<NodeType> done) {
        NodeType nodeType = sequenceElements().last().nodeType();
        return !done.contains(nodeType) ? nodeType.lastTerminals(done) : Set.of();
    }

}
