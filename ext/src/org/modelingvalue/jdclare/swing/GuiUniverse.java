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

package org.modelingvalue.jdclare.swing;

import static org.modelingvalue.jdclare.DClare.*;
import static org.modelingvalue.jdclare.PropertyQualifier.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.modelingvalue.collections.*;
import org.modelingvalue.dclare.*;
import org.modelingvalue.jdclare.*;

public interface GuiUniverse extends DUniverse {
    @Property({containment, hidden})
    Set<Frame> frames();

    @Rule
    default void stopRule() {
        if (frames().isEmpty() && !pre(this, GuiUniverse::frames).isEmpty()) {
            dClare().stop();
        }
    }

    @Override
    default void init() {
        DUniverse.super.init();
        ImperativeTransaction itx = dClare().addImperative("swingNative", pre -> {
        }, callNativesOfClass(DVisible.class), SwingUtilities::invokeLater, true);
        SwingUtilities.invokeLater(() -> LeafTransaction.getContext().setOnThread(itx));
        KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        kfm.addKeyEventDispatcher(e -> {
            if (e.getID() == KeyEvent.KEY_PRESSED && e.isControlDown()) {
                if (e.getKeyCode() == KeyEvent.VK_Z) {
                    dClare().backward();
                    return true;
                } else if (e.getKeyCode() == KeyEvent.VK_Y) {
                    dClare().forward();
                    return true;
                }
            }
            return false;
        });
    }
}
