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

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.Highlighter.*;

import org.modelingvalue.collections.List;
import org.modelingvalue.jdclare.*;
import org.modelingvalue.jdclare.swing.DTextPane.*;
import org.modelingvalue.jdclare.swing.PopupMenu.*;

@Native(TextPaneNative.class)
public interface DTextPane<E extends TextElement> extends DTextComponent {
    @Property(containment)
    List<E> elements();

    default E element(int position) {
        return elements().filter(e -> e.offset() <= position && position - e.offset() <= e.len()).findFirst().orElse(null);
    }

    @SuppressWarnings("unused")
    class TextPaneNative<E extends TextElement> extends TextComponentNative<DTextPane<E>, JTextPane> {
        private final KeyAdapter keyAdapter;

        public TextPaneNative(DTextPane<E> visible) {
            super(visible);
            keyAdapter = new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_SPACE) {
                        Caret caret = swing.getCaret();
                        Point point = caret.getMagicCaretPosition();
                        popup(point.x, point.y, caret.getDot(), false);
                        e.consume();
                    }
                }
            };
        }

        @Override
        public void init(DObject parent) {
            swing = new JTextPane();
            swing.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            swing.addKeyListener(keyAdapter);
            super.init(parent);
        }

        @Override
        public void exit(DObject parent) {
            super.exit(parent);
            swing.removeKeyListener(keyAdapter);
        }

        @Override
        protected void popup(int x, int y) {
            int position = swing.viewToModel2D(new Point(x, y));
            if (position >= 0) {
                popup(x, y, position, true);
            }
        }

        private void popup(int x, int y, int position, boolean select) {
            E el = visible.element(position);
            if (el != null) {
                PopupMenu popupMenu = el.popupMenu();
                if (popupMenu != null) {
                    if (select) {
                        swing.select(el.offset(), el.offset() + el.len());
                        swing.requestFocus();
                    }
                    ((PopupMenuNative) dNative(popupMenu)).swing.show(swing, x, y);
                }
            }
        }

        @Override
        @Deferred
        public void string(String pre, String post) {
            super.string(pre, post);
            elements(null, visible.elements());
        }

        @Deferred
        public void elements(List<E> pre, List<E> post) {
            Highlighter highlighter = swing.getHighlighter();
            highlighter.removeAllHighlights();
            StyledDocument styledDocument = swing.getStyledDocument();
            post.forEachOrdered(el -> {
                TextStyle style = el.style();
                styledDocument.setCharacterAttributes(el.offset(), el.len(), style.attributeSet(), true);
                HighlightPainter hl = style.highligth();
                if (hl != null) {
                    try {
                        highlighter.addHighlight(el.offset(), el.offset() + el.len(), hl);
                    } catch (BadLocationException e) {
                        throw new Error(e);
                    }
                }
            });
        }
    }
}
