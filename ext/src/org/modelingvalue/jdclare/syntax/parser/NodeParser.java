//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
// (C) Copyright 2018-2020 Modeling Value Group B.V. (http://modelingvalue.org)                                        ~
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

package org.modelingvalue.jdclare.syntax.parser;

import org.modelingvalue.collections.*;
import org.modelingvalue.jdclare.*;
import org.modelingvalue.jdclare.syntax.Grammar.*;
import org.modelingvalue.jdclare.syntax.*;
import org.modelingvalue.jdclare.syntax.meta.*;

import static org.modelingvalue.jdclare.DClare.*;
import static org.modelingvalue.jdclare.PropertyQualifier.*;

public interface NodeParser extends DObject {

    default boolean match(NodeParser synt) {
        return synt.dClass().equals(dClass());
    }

    @Property(constant)
    Text<?, ?> text();

    @Property(constant)
    NodeType type();

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Property(optional)
    default Node abstractNode() {
        Node            result   = null;
        Set<DProblem>[] problems = new Set[]{Set.of()};
        if (resolvedToRoot()) {
            NodeType type = type();
            if (type instanceof NodeClass) {
                result = resolvedToRoot() ? ((NodeClass) type).create(this, problems) : null;
            }
        }
        set(this, NodeParser::createProblems, problems[0]);
        return result;
    }

    @Property(validation)
    Set<DProblem> createProblems();

    @Property
    boolean matched();

    @Property(constant)
    default boolean root() {
        return text().rootNodeClass().equals(type());
    }

    String value();

    default Set<NodeParser> parsers() {
        return sequences().add(this);
    }

    @Property
    default Set<NodeParser> sequences() {
        return matched() ? type().possibleParents().map(s -> (NodeParser) dclare(SequenceParser.class, this, s)).flatMap(NodeParser::parsers).toSet() : Set.of();
    }

    NodeParser nextMatch(NodeType pattern, SequenceParser upper);

    NodeParser previousMatch(NodeType pattern, SequenceParser upper);

    @Property
    Set<SequenceParser> firstOppos();

    default NodeParser firstUpper(NodeType pattern, SequenceParser owner) {
        for (SequenceParser first : firstOppos()) {
            if (!owner.hasUpper(first)) {
                NodeParser upper = first.firstUpper(pattern, owner);
                if (upper != null) {
                    return upper;
                }
            }
        }
        return pattern.match(type()) ? this : null;
    }

    @Property
    Set<SequenceParser> lastOppos();

    default NodeParser lastUpper(NodeType pattern, SequenceParser owner) {
        for (SequenceParser last : lastOppos()) {
            if (!owner.hasUpper(last)) {
                NodeParser upper = last.lastUpper(pattern, owner);
                if (upper != null) {
                    return upper;
                }
            }
        }
        return pattern.match(type()) ? this : null;
    }

    default boolean hasUpper(SequenceParser upper) {
        return equals(upper) || uppers().anyMatch(u -> u.hasUpper(upper));
    }

    @Property
    Set<SequenceParser> uppers();

    @Property(validation)
    default DProblem ambiguous() {
        Set<SequenceParser> uppers = uppersToRoot();
        return uppers.size() > 1 ? dclare(DProblem.class, firstTerminal().token(), "AMBIGUOUS", DSeverity.error, "Node '" + this + "' is contained in " + uppers.toString().substring(3)) : null;
    }

    @Property
    default Set<SequenceParser> uppersToRoot() {
        return matchedUppers().filter(NodeParser::resolvedToRoot).toSet();
    }

    @Property
    default boolean resolvedToRoot() {
        return root() || !uppersToRoot().isEmpty();
    }

    @Property
    default boolean resolved() {
        return root() || !matchedUppers().isEmpty();
    }

    @Property
    default Set<SequenceParser> matchedUppers() {
        return uppers().filter(SequenceParser::matched).toSet();
    }

    TerminalParser firstTerminal();

    TerminalParser lastTerminal();

}
