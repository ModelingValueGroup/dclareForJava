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

package org.modelingvalue.jdclare.workbench;

import org.modelingvalue.collections.*;
import org.modelingvalue.jdclare.*;
import org.modelingvalue.jdclare.swing.*;

import static org.modelingvalue.jdclare.DClare.*;
import static org.modelingvalue.jdclare.PropertyQualifier.*;
import static org.modelingvalue.jdclare.workbench.ProblemsTableColumn.*;

public interface ProblemsTable extends Table<DProblem, ProblemsTableColumn, Object>, DStruct1<WBUniverse> {

    @Property(key = 0)
    WBUniverse wb();

    @Override
    default List<DProblem> rowObjects() {
        return wb().dAllProblems().toList();
    }

    @Override
    @Property(constant)
    default List<ProblemsTableColumn> columnObjects() {
        return List.of(context, id, severity, message);
    }

    @Override
    default int preferredWidth(ProblemsTableColumn column) {
        return column.width();
    }

    @Override
    default Object value(DProblem p, ProblemsTableColumn ct) {
        return ct.function().apply(p);
    }

    @Rule
    default void setSelected() {
        Row<DProblem, ProblemsTableColumn, Object> selected = selectedRow();
        set(wb(), WBUniverse::selectedProblem, selected != null ? selected.object() : null);
    }

}
