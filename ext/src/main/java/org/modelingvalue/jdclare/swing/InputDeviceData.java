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

import java.awt.event.*;

import org.modelingvalue.collections.*;
import org.modelingvalue.jdclare.*;
import org.modelingvalue.jdclare.swing.draw2d.*;

@SuppressWarnings("unused")
public interface InputDeviceData extends DObject, DStruct1<DComponent> {
    @Default
    @Property
    default DPoint mousePosition() {
        return DPoint.NULL;
    }

    @Property
    default boolean isAltDown() {
        return pressedKeys().contains(KeyEvent.VK_ALT);
    }

    @Property
    default boolean isCtrlDown() {
        return pressedKeys().contains(KeyEvent.VK_CONTROL);
    }

    @Property
    boolean isLeftMouseDown();

    @Property
    Set<Integer> pressedKeys();
}
