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

package org.modelingvalue.jdclare.syntax.meta;

import org.modelingvalue.collections.*;
import org.modelingvalue.jdclare.*;
import org.modelingvalue.jdclare.syntax.regex.*;

import static org.modelingvalue.jdclare.DClare.*;
import static org.modelingvalue.jdclare.PropertyQualifier.*;

public interface TokenType extends DObject, DStruct3<GrammarClass<?>, String, String> {

    @Property(key = 0)
    GrammarClass<?> grammar();

    @Property(key = 1)
    String regex();

    @Property(key = 2)
    String literal();

    @Property(constant)
    boolean skipped();

    @Property
    Set<TerminalClass<?>> terminals();

    @Property(constant)
    default DPattern pattern() {
        return dclare(DPattern.class, regex());
    }

    default int compare(TokenType other) {
        int t = literal() != null ? -1 : 1;
        int o = other.literal() != null ? -1 : 1;
        if (t != o) {
            return Integer.compare(t, o);
        } else {
            int d = Integer.compare(other.regex().length(), regex().length());
            if (d != 0) {
                return d;
            } else {
                return regex().compareTo(other.regex());
            }
        }
    }

    @Property(constant)
    default String string() {
        String literal = literal();
        return literal != null ? ("'" + literal + "'") : ("<" + regex() + ">");
    }

}
