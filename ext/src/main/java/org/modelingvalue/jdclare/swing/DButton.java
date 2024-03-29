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

import java.awt.*;
import java.awt.event.*;
import java.util.function.*;

import javax.swing.*;
import javax.swing.border.*;

import org.modelingvalue.jdclare.*;
import org.modelingvalue.jdclare.swing.DButton.*;
import org.modelingvalue.jdclare.swing.draw2d.*;

@Native(ButtonNative.class)
public interface DButton extends DComponent {
    @Property
    String text();

    @Property
    boolean selected();

    @Property(optional)
    DImage imageLink();

    @Property
    Consumer<ActionEvent> action();

    @SuppressWarnings("unused")
    class ButtonNative extends DComponentNative<DButton, JButton> {
        public ButtonNative(DButton visible) {
            super(visible);
        }

        @Override
        public void init(DObject parent) {
            swing = new JButton();
            swing.setBorder(new EmptyBorder(2, 2, 2, 2));
            swing.addActionListener(x -> {
                swing.setSelected(!swing.isSelected());
                Consumer<ActionEvent> action = visible.action();
                dClare().put(action, () -> action.accept(x));
                DClare.set(visible, DButton::selected, swing.isSelected());
            });
            updateImageAndText(visible.imageLink() != null ? visible.imageLink().image() : null, visible.text());
            super.init(parent);
        }

        private void updateImageAndText(Image i, String text) {
            if (i != null) {
                swing.setIcon(new ImageIcon(i));
                swing.setText(null);
                swing.setToolTipText(text);
            } else {
                swing.setIcon(null);
                swing.setText(text);
            }
        }

        public void imageLink(DImage pre, DImage post) {
            if (pre != post) {
                updateImageAndText(post.image(), visible.text());
            }
        }

        public void text(String pre, String post) {
            updateImageAndText(visible.imageLink() != null ? visible.imageLink().image() : null, post);
        }
    }
}
