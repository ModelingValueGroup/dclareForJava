//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
// (C) Copyright 2018-2021 Modeling Value Group B.V. (http://modelingvalue.org)                                        ~
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

import org.modelingvalue.collections.util.*;
import org.modelingvalue.jdclare.*;
import org.modelingvalue.jdclare.swing.DTextComponent.*;

import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*;

@Native(TextComponentNative.class)
public interface DTextComponent extends DComponent {

    @Property
    String string();

    @Default
    @Property
    default Color background() {
        return new Color(255, 255, 200);
    }

    @Default
    @Property
    default Color foreground() {
        return new Color(55, 55, 40);
    }

    @Default
    @Property
    default Pair<Integer, Integer> selection() {
        return Pair.of(0, 0);
    }

    class TextComponentNative<D extends DTextComponent, J extends JTextComponent> extends DComponentNative<D, J> implements CaretListener, DocumentListener {
        public TextComponentNative(D visible) {
            super(visible);
        }

        @Override
        public void init(DObject parent) {
            swing.setText(visible.string());
            super.init(parent);
            swing.getDocument().addDocumentListener(this);
            swing.addCaretListener(this);
        }

        @Override
        public void exit(DObject parent) {
            super.exit(parent);
            swing.removeCaretListener(this);
        }

        @Deferred
        public void string(String pre, String post) {
            if (!swing.getText().equals(post)) {
                int start = swing.getSelectionStart();
                int end = swing.getSelectionEnd();
                swing.setText(post);
                swing.setSelectionStart(start);
                swing.setSelectionEnd(end);
            }
        }

        @Deferred
        public void selection(Pair<Integer, Integer> pre, Pair<Integer, Integer> post) {
            swing.select(post.a(), post.b());
            swing.requestFocus();
        }

        public void background(Color pre, Color post) {
            swing.setBackground(post);
        }

        public void foreground(Color pre, Color post) {
            swing.setForeground(post);
        }

        @Override
        public void caretUpdate(CaretEvent e) {
            set(DTextComponent::selection, Pair.of(swing.getSelectionStart(), swing.getSelectionEnd()));
        }

        @Override
        public void changedUpdate(DocumentEvent arg0) {
            // Do Nothing
        }

        @Override
        public void insertUpdate(DocumentEvent arg0) {
            set(DTextComponent::string, swing.getText());
        }

        @Override
        public void removeUpdate(DocumentEvent arg0) {
            set(DTextComponent::string, swing.getText());
        }
    }
}
