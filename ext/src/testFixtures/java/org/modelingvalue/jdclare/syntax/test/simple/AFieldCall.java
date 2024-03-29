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

package org.modelingvalue.jdclare.syntax.test.simple;

import static org.modelingvalue.jdclare.DClare.SCOPE;
import static org.modelingvalue.jdclare.PropertyQualifier.containment;
import static org.modelingvalue.jdclare.PropertyQualifier.optional;
import static org.modelingvalue.jdclare.PropertyQualifier.softMandatory;

import org.modelingvalue.collections.Set;
import org.modelingvalue.jdclare.Constraints;
import org.modelingvalue.jdclare.Property;
import org.modelingvalue.jdclare.syntax.test.types.AType;

public interface AFieldCall extends AExpression {

    @Property({containment, optional})
    AExpression base();

    @Property(softMandatory)
    AField field();

    @Override
    default AType type() {
        return field().type();
    }

    @Constraints
    default void constraints() {
        SCOPE(AFieldCall::field, AFieldCall::scopeField);
    }

    @Property
    default Set<AField> scopeField() {
        AExpression base = base();
        if (base != null) {
            return base.type().fields();
        } else {
            return dAncestor(AClass.class).fields();
        }
    }

}
