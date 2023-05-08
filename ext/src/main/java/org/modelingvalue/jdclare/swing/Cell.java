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

package org.modelingvalue.jdclare.swing;

import static org.modelingvalue.jdclare.DClare.*;
import static org.modelingvalue.jdclare.PropertyQualifier.*;

import javax.swing.table.*;

import org.modelingvalue.jdclare.*;
import org.modelingvalue.jdclare.swing.Cell.*;
import org.modelingvalue.jdclare.swing.Table.*;

@Native(CellNative.class)
public interface Cell<R, C, V> extends DStruct3<Table<R, C, V>, Row<R, C, V>, Column<R, C, V>>, DVisible {
    @Property(key = 0)
    Table<R, C, V> table();

    @Property(key = 1)
    Row<R, C, V> row();

    @Property(key = 2)
    Column<R, C, V> column();

    @Property(optional)
    default V value() {
        return table().value(row().object(), column().object());
    }

    default void setValue(V value) {
        table().setValue(row().object(), column().object(), value);
    }

    class CellNative<R, C, V> extends VisibleNative<Cell<R, C, V>> {
        public CellNative(Cell<R, C, V> visible) {
            super(visible);
        }

        @SuppressWarnings("unused")
        public void value(V pre, V post) {
            TableNative<R, C, V> table  = dNative(visible.table());
            AbstractTableModel   m      = (AbstractTableModel) table.swing.getModel();
            int                  row    = visible.row().nr();
            int                  column = visible.column().nr();
            m.fireTableCellUpdated(row, column);
        }
    }
}
