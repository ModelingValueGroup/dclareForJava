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

import org.modelingvalue.collections.*;
import org.modelingvalue.jdclare.*;
import org.modelingvalue.jdclare.swing.Frame.*;
import org.modelingvalue.jdclare.swing.draw2d.*;

import javax.swing.*;
import java.awt.event.*;

import static org.modelingvalue.jdclare.PropertyQualifier.*;

@Native(FrameNative.class)
public interface Frame extends DContainer {

    @Property(containment)
    DComponent contentPane();

    @Property({containment, optional})
    DMenubar menubar();

    class FrameNative extends DContainerNative<Frame, JFrame> {

        private final WindowAdapter windowLsitener;

        public FrameNative(Frame visible) {
            super(visible);
            windowLsitener = new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    DClare.<GuiUniverse, Set<Frame>, Frame> set(((GuiUniverse) DClare.dUniverse()), GuiUniverse::frames, Set::remove, visible);
                }
            };
        }

        @Override
        public void init(DObject parent) {
            swing = new JFrame();
            swing.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            swing.addWindowListener(windowLsitener);
            super.init(parent);
        }

        @Override
        public void exit(DObject parent) {
            swing.removeWindowListener(windowLsitener);
            super.exit(parent);
        }

        @Override
        public void location(DPoint pre, DPoint post) {
            swing.setLocation((int) post.x(), (int) post.y());
        }

        @Override
        public void size(DDimension pre, DDimension post) {
        }

        public void contentPane(DComponent pre, DComponent post) {
            swing.setContentPane(post != null ? swing(post) : null);
            if (post != null) {
                swing.pack();
            }
        }

        public void menubar(DMenubar pre, DMenubar post) {
            JMenuBar menuBar = post != null ? (JMenuBar) swing(post) : null;
            swing.setJMenuBar(menuBar);
            if (post != null) {
                swing.pack();
            }
        }

        @Override
        @Deferred
        public void preferredSize(DDimension pre, DDimension post) {
            super.preferredSize(pre, post);
            if (post != null) {
                swing.pack();
            }
        }

    }
}
