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

package org.modelingvalue.jdclare.swing.examples.sync;

import org.modelingvalue.dclare.sync.DeltaAdaptor;
import org.modelingvalue.dclare.sync.SupplierAndConsumer;
import org.modelingvalue.dclare.sync.SyncConnectionHandler;
import org.modelingvalue.jdclare.DClare;

public class Main {
    public static void main(String[] args) {
        DClare<D2Universe> dclare = DClare.start(D2Universe.class, false);
        SupplierAndConsumer<String> a = new DeltaAdaptor<>("sync", dclare.universeTransaction(), new SyncSerializationHelper());
        @SuppressWarnings("unused")
        ConnectionDialog dia = new ConnectionDialog(new SyncConnectionHandler(a));
        dclare.waitForEnd();
    }
}
