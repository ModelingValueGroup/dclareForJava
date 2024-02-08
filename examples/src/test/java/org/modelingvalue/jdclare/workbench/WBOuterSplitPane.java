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

package org.modelingvalue.jdclare.workbench;

import static org.modelingvalue.jdclare.DClare.*;
import static org.modelingvalue.jdclare.PropertyQualifier.*;

import org.modelingvalue.collections.*;
import org.modelingvalue.jdclare.*;
import org.modelingvalue.jdclare.swing.*;
import org.modelingvalue.jdclare.swing.draw2d.*;

public interface WBOuterSplitPane extends SplitPane, DStruct1<WBUniverse> {

    @Property(key = 0)
    WBUniverse wb();

    @Override
    default DComponent leftComponent() {
        return dclare(InnerSplitPane1.class, wb());
    }

    @Override
    default DComponent rightComponent() {
        return dclare(InnerSplitPane2.class, wb());
    }

    @Override
    @Property(constant)
    default double resizeWeight() {
        return 0.9;
    }

    @Override
    default boolean vertical() {
        return true;
    }

    interface InnerSplitPane1 extends DStruct1<WBUniverse>, SplitPane {
        @Property(key = 0)
        WBUniverse wb();

        @Override
        default DComponent leftComponent() {
            return dclare(MyScrollPane1.class, wb());
        }

        @Override
        default DComponent rightComponent() {
            return dclare(MyTabbedPane2.class, wb());
        }

        @Override
        @Property(constant)
        default double resizeWeight() {
            return 0.1;
        }

        @Override
        default boolean vertical() {
            return false;
        }
    }

    interface InnerSplitPane2 extends DStruct1<WBUniverse>, SplitPane {
        @Property(key = 0)
        WBUniverse wb();

        @Override
        default DComponent leftComponent() {
            return dclare(MyScrollPane3.class, wb());
        }

        @Override
        default DComponent rightComponent() {
            return dclare(MyScrollPane4.class, wb());
        }

        @Override
        @Property(constant)
        default double resizeWeight() {
            return 0.1;
        }

        @Override
        default boolean vertical() {
            return false;
        }

        @Override
        @Default
        @Property
        default DDimension preferredSize() {
            return dclare(DDimension.class, 1600.0, 200.0);
        }
    }

    interface MyScrollPane1 extends DStruct1<WBUniverse>, ScrollPane {
        @Property(key = 0)
        WBUniverse wb();

        @Override
        @Property(constant)
        default DComponent viewportView() {
            return dclare(UniverseExplorer.class, wb());
        }
    }

    interface MyTabbedPane2 extends DStruct1<WBUniverse>, TabbedPane {
        @Property(key = 0)
        WBUniverse wb();

        @Override
        @Property(constant)
        default Map<String, DComponent> tabs() {
            return Map.<String, DComponent> of().//
                    put("TextExample", dclare(MyScrollPane2.class, wb())).//
                    put("DiagramExample", dclare(MyScrollPane5.class, wb()));
        }
    }

    interface MyScrollPane2 extends DStruct1<WBUniverse>, ScrollPane {
        @Property(key = 0)
        WBUniverse wb();

        @Override
        @Property(constant)
        default DComponent viewportView() {
            return dclare(TextEditorPane.class, wb());
        }
    }

    interface MyScrollPane5 extends DStruct1<WBUniverse>, ScrollPane {
        @Property(key = 0)
        WBUniverse wb();

        @Override
        @Property(constant)
        default DComponent viewportView() {
            return dclare(DiagramCanvas.class, wb());
        }
    }

    interface MyScrollPane3 extends DStruct1<WBUniverse>, ScrollPane {
        @Property(key = 0)
        WBUniverse wb();

        @Override
        @Property(constant)
        default DComponent viewportView() {
            return dclare(PropertiesTable.class, wb());
        }
    }

    interface MyScrollPane4 extends DStruct1<WBUniverse>, ScrollPane {
        @Property(key = 0)
        WBUniverse wb();

        @Override
        @Property(constant)
        default DComponent viewportView() {
            return dclare(ProblemsTable.class, wb());
        }
    }

}
