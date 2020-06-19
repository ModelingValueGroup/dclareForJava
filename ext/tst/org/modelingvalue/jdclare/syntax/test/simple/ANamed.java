//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
// (C) Copyright 2018-2019 Modeling Value Group B.V. (http://modelingvalue.org)                                        ~
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

import static org.modelingvalue.jdclare.DClare.dclare;
import static org.modelingvalue.jdclare.PropertyQualifier.hidden;
import static org.modelingvalue.jdclare.PropertyQualifier.unchecked;
import static org.modelingvalue.jdclare.PropertyQualifier.validation;

import org.modelingvalue.jdclare.DNamed;
import org.modelingvalue.jdclare.DProblem;
import org.modelingvalue.jdclare.DSeverity;
import org.modelingvalue.jdclare.Default;
import org.modelingvalue.jdclare.Property;

public interface ANamed extends DNamed {

    String NO_NAME = "<no name>";

    @Override
    @Default
    @Property(unchecked)
    default String name() {
        return NO_NAME;
    }

    @SuppressWarnings("unused")
    @Property({validation, hidden})
    default DProblem noName() {
        //noinspection StringEquality
        return name() == NO_NAME ? dclare(DProblem.class, this, "NO_NAME", DSeverity.fatal, "No Name") : null;
    }

}
