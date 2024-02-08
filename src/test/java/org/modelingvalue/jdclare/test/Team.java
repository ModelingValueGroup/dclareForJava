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

package org.modelingvalue.jdclare.test;

import org.modelingvalue.collections.*;
import org.modelingvalue.jdclare.*;

import static org.modelingvalue.jdclare.DClare.*;
import static org.modelingvalue.jdclare.PropertyQualifier.*;

public interface Team extends DUUObject, DNamed {

    @Constraints
    private void constraints() {
        SCOPE(Team::developers, Team::companyEmployees);
        SCOPE(Team::productOwner, Team::companyEmployees);
        SCOPE(Team::scrumMaster, Team::companyEmployees);
    }

    @Property(optional)
    Company company();

    @Property({validation, hidden})
    default Set<DProblem> problems() {
        return teamMembers().distinct().size() != teamMembers().size() ? Set.of(dclare(DProblem.class, this, "ONE_ROLE", DSeverity.error, "Person can only have one Role")) : Set.of();
    }

    @Property
    Set<Person> developers();

    @Property(optional)
    Person productOwner();

    @Property(optional)
    Person scrumMaster();

    @Property(hidden)
    default Set<Person> companyEmployees() {
        return company() != null ? company().employees() : Set.of();
    }

    @Property
    default List<Person> teamMembers() {
        List<Person> result = developers().asList();
        Person       master = scrumMaster();
        if (master != null) {
            result = result.append(master);
        }
        Person owner = productOwner();
        if (owner != null) {
            result = result.append(owner);
        }
        return result;
    }

}
