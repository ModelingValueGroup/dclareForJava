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

package org.modelingvalue.jdclare.swing.draw2d;

import static org.modelingvalue.jdclare.DClare.*;
import static org.modelingvalue.jdclare.PropertyQualifier.*;

import java.awt.*;
import java.util.*;

import javax.swing.*;

import org.modelingvalue.collections.List;
import org.modelingvalue.collections.Set;
import org.modelingvalue.jdclare.*;
import org.modelingvalue.jdclare.swing.*;
import org.modelingvalue.jdclare.swing.draw2d.DCanvas.*;
import org.modelingvalue.jdclare.swing.draw2d.DShape.*;

@Native(DCanvasNative.class)
public interface DCanvas extends DComponent {
    UUID SELECTION_MODE = UUID.randomUUID();

    @Default
    @Property
    default Color color() {
        return Color.WHITE;
    }

    @Property({containment, optional})
    CanvasMode mode();

    @Property
    default Set<DShape> selected() {
        return shapes().filter(DShape::selected).asSet();
    }

    @Property(containment)
    List<DShape> shapes();

    @SuppressWarnings("unused")
    class DCanvasNative extends DComponentNative<DCanvas, JPanel> {
        public DCanvasNative(DCanvas visible) {
            super(visible);
        }

        @Override
        public void init(DObject parent) {
            swing = new JPanel() {
                private static final long serialVersionUID = 1L;

                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
                    super.paintComponent(g);
                    visible.shapes().forEachOrdered(s -> ((ShapeNative<?>) dNative(s)).paint(g2d));
                }
            };
            swing.setOpaque(true);
            swing.setPreferredSize(new Dimension(2000, 2000));
            swing.setDoubleBuffered(true);
            super.init(parent);
        }

        public void color(Color pre, Color post) {
            swing.setBackground(post);
        }

        public void shapes(List<DShape> pre, List<DShape> post) {
            swing.repaint();
        }
    }
}
