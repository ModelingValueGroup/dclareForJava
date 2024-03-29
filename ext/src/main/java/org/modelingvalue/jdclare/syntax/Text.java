//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
// (C) Copyright 2018-2023 Modeling Value Group B.V. (http://modelingvalue.org)                                        ~
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

package org.modelingvalue.jdclare.syntax;

import org.modelingvalue.collections.*;
import org.modelingvalue.jdclare.*;
import org.modelingvalue.jdclare.syntax.Grammar.*;
import org.modelingvalue.jdclare.syntax.meta.*;
import org.modelingvalue.jdclare.syntax.parser.*;

import static org.modelingvalue.jdclare.DClare.*;
import static org.modelingvalue.jdclare.PropertyQualifier.*;

public interface Text<S extends Grammar, R extends Node> extends DObject {

    @Property
    String string();

    @Property(constant)
    Class<S> syntaxClass();

    @Property(constant)
    Class<R> rootClass();

    @Property(constant)
    default NodeClass<R> rootNodeClass() {
        return (NodeClass<R>) DClare.dClass(rootClass());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Property(constant)
    default GrammarClass<S> grammar() {
        return (GrammarClass) DClare.dClass(syntaxClass());
    }

    @SuppressWarnings("unchecked")
    @Property({constant, containment})
    default Tokenizer<S> tokenizer() {
        return dclare(Tokenizer.class, this);
    }

    @SuppressWarnings("unchecked")
    @Property({constant, containment})
    default Parser<S, R> parser() {
        return dclare(Parser.class, this);
    }

    @SuppressWarnings("unchecked")
    @Property(optional)
    default R root() {
        Set<NodeParser> roots = parser().roots();
        //noinspection OptionalGetWithoutIsPresent
        return roots.size() == 1 ? (R) roots.findAny().get().abstractNode() : null;
    }

    @Property(validation)
    default DProblem rootNotReloved() {
        return root() == null ? dclare(DProblem.class, this, "NO ROOT", DSeverity.error, "No <" + rootNodeClass().name() + "> found") : null;
    }

}
