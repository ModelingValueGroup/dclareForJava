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

import org.modelingvalue.collections.Collection;
import org.modelingvalue.collections.List;
import org.modelingvalue.jdclare.*;
import org.modelingvalue.jdclare.swing.DTreeNode.*;
import org.modelingvalue.jdclare.swing.Tree.*;
import org.modelingvalue.jdclare.swing.draw2d.*;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.util.*;

import static org.modelingvalue.jdclare.DClare.*;
import static org.modelingvalue.jdclare.PropertyQualifier.*;

@SuppressWarnings("rawtypes")
@Native(DTreeNodeNative.class)
public interface DTreeNode<T> extends DVisible, DStruct2<DTreeNode, T> {
    Icon DEFAULT_ICON = new BoxIcon();

    @Property(key = 0)
    DTreeNode parent();

    @Property(key = 1)
    T object();

    @SuppressWarnings("InfiniteRecursion")
    @Property
    default boolean wasExpanded() {
        return wasExpanded() || expanded();
    }

    @Property(containment)
    default List<DTreeNode> childNodes() {
        return wasExpanded() ? children(object()).toList() : List.of();
    }

    @Property
    String name();

    @Property(constant)
    default Icon icon() {
        return DEFAULT_ICON;
    }

    @Property
    boolean expanded();

    @Property
    default boolean leaf() {
        return children(object()).isEmpty();
    }

    Collection<DTreeNode> children(T object);

    @Property({containment, optional})
    PopupMenu popupMenu();

    class DTreeNodeNative<T> extends VisibleNative<DTreeNode<T>> implements TreeNode {

        public DTreeNodeNative(DTreeNode<T> visible) {
            super(visible);
        }

        @Override
        public void init(DObject parent) {
            super.init(parent);
            if (parent instanceof Tree) {
                set(DTreeNode::expanded, true);
            }
        }

        public void childNodes(List<DTreeNode> pre, List<DTreeNode> post) {
            ancestor(TreeNative.class).model().nodeStructureChanged(this);
        }

        public void leaf(boolean pre, boolean post) {
            ancestor(TreeNative.class).model().nodeStructureChanged(this);
        }

        public void name(String pre, String post) {
            ancestor(TreeNative.class).swing.repaint();
        }

        public void expanded(boolean pre, boolean post) {
            ancestor(TreeNative.class).swing.setSelectionPath(path());
        }

        @Override
        public TreeNode getChildAt(int childIndex) {
            return dNative(visible.childNodes().get(childIndex));
        }

        @Override
        public int getIndex(TreeNode child) {
            return visible.childNodes().firstIndexOf(((DTreeNodeNative) child).visible);
        }

        @Override
        public int getChildCount() {
            return visible.childNodes().size();
        }

        @Override
        public TreeNode getParent() {
            DTreeNode parent = visible.parent();
            return parent != null ? dNative(parent) : null;
        }

        @Override
        public boolean getAllowsChildren() {
            return true;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Enumeration children() {
            Iterator<DTreeNode> it = visible.childNodes().iterator();
            return new Enumeration() {
                @Override
                public boolean hasMoreElements() {
                    return it.hasNext();
                }

                @Override
                public Object nextElement() {
                    return dNative(it.next());
                }
            };
        }

        @Override
        public boolean isLeaf() {
            return visible.leaf();
        }

        @Override
        public String toString() {
            return visible.name();
        }

        public TreePath path() {
            DTreeNode parent = visible.parent();
            return parent != null ? ((DTreeNodeNative) dNative(parent)).path().pathByAddingChild(this) : new TreePath(this);
        }

    }

    class DTreeCellRenderer extends DefaultTreeCellRenderer {

        private static final long serialVersionUID = -6152427263542859472L;

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            DTreeNodeNative<?> nNode = ((DTreeNodeNative) value);
            setIcon(nNode.visible.icon());
            setText(nNode.visible.name());
            return this;
        }
    }

    class BoxIcon implements Icon {

        private static final Color NICE_BLUE = new Color(100, 100, 250);

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            D2D.drawRect(g, x, y, 10, 8, NICE_BLUE, Color.BLACK, null);
        }

        @Override
        public int getIconWidth() {
            return 12;
        }

        @Override
        public int getIconHeight() {
            return 9;
        }

    }
}
