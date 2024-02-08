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

package org.modelingvalue.jdclare.test;

import org.modelingvalue.collections.*;
import org.modelingvalue.jdclare.*;

import static org.modelingvalue.jdclare.DClare.*;
import static org.modelingvalue.jdclare.PropertyQualifier.*;

public interface Reparents extends DUniverse {

    @Property(containment)
    Set<Node> tree();

    @Override
    default void init() {
        DUniverse.super.init();
        set(this, Reparents::tree, Collection.range(0, 100).map(nr -> dclare(Node.class, nr)).asSet());
    }

    interface Node extends DObject, DStruct1<Integer> {

        @Property(key = 0)
        int nr();

        @Property(containment)
        Set<Node> children();

        @Rule
        default void makeTree() {
            if (dParent() instanceof Reparents) {
                if (nr() < 100 && nr() % 10 == 0) {
                    int g = nr() / 10 + 1;
                    Node parent = dclare(Node.class, g * 100);
                    Set<Node> children = Collection.range(nr(), nr() + 10).map(nr -> dclare(Node.class, nr)).asSet();
                    reparent(parent, children);
                }
                if (nr() == 100) {
                    Node parent = dclare(Node.class, 10000);
                    Set<Node> children = Collection.range(1, 11).map(nr -> dclare(Node.class, nr * 100)).asSet();
                    reparent(parent, children);
                }
            }
        }

        default void reparent(Node parent, Set<Node> children) {
            set((Reparents) dParent(), Reparents::tree, Set::add, parent);
            set(parent, Node::children, Set::addAll, children);
        }

    }

}
