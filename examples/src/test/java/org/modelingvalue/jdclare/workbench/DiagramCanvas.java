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

package org.modelingvalue.jdclare.workbench;

import static org.modelingvalue.jdclare.DClare.dclare;
import static org.modelingvalue.jdclare.PropertyQualifier.constant;
import static org.modelingvalue.jdclare.PropertyQualifier.containment;

import java.awt.Color;

import org.modelingvalue.collections.List;
import org.modelingvalue.collections.QualifiedSet;
import org.modelingvalue.collections.Set;
import org.modelingvalue.jdclare.DObject;
import org.modelingvalue.jdclare.DStruct1;
import org.modelingvalue.jdclare.DStruct2;
import org.modelingvalue.jdclare.DStruct3;
import org.modelingvalue.jdclare.Property;
import org.modelingvalue.jdclare.meta.DStructClass;
import org.modelingvalue.jdclare.swing.draw2d.DCanvas;
import org.modelingvalue.jdclare.swing.draw2d.DDimension;
import org.modelingvalue.jdclare.swing.draw2d.DLine;
import org.modelingvalue.jdclare.swing.draw2d.DPoint;
import org.modelingvalue.jdclare.swing.draw2d.DRectangle;
import org.modelingvalue.jdclare.swing.draw2d.DShape;

public interface DiagramCanvas extends DStruct1<WBUniverse>, DCanvas {

    @Property(key = 0)
    WBUniverse wb();

    @Override
    @Property(constant)
    default Color color() {
        return new Color(200, 255, 200);
    }

    @Override
    @Property(constant)
    default DDimension preferredSize() {
        return dclare(DDimension.class, 20000.0, 2000.0);
    }

    @Override
    default List<DShape> shapes() {
        return List.<DShape> of().addAll(supers()).addAll(classes());
    }

    @Property
    default Set<SuperLine> supers() {
        return classes().flatMap(c -> c.supers().map(s -> dclare(SuperLine.class, this, c, s))).asSet();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Property
    default Set<ClassRectangle> classes() {
        DObject sel = wb().selected();
        if (sel instanceof DStructClass) {
            Set<DStructClass> all = ((DStructClass) sel).allSubs();
            all = all.addAll(all.flatMap(DStructClass::allSupers));
            return all.map(c -> dclare(ClassRectangle.class, this, c)).asSet();
        } else {
            return Set.of();
        }
    }

    @Property(containment)
    default QualifiedSet<Integer, Level> levels() {
        return classes().map(ClassRectangle::level).distinct().map(l -> dclare(Level.class, this, l)).asQualifiedSet(Level::level);
    }

    interface Level extends DObject, DStruct2<DiagramCanvas, Integer> {
        @Property(key = 0)
        DiagramCanvas diagram();

        @Property(key = 1)
        int level();

        @Property
        default Set<ClassRectangle> classes() {
            int l = level();
            return diagram().classes().filter(c -> c.level() == l).asSet();
        }

        @Property
        default List<ClassRectangle> sorted() {
            return classes().sorted((a, b) -> {
                int c = Integer.compare(a.upper(), b.upper());
                return c == 0 && !a.equals(b) ? a.compareTo(b) : c;
            }).asList();
        }
    }

    interface SuperLine extends DStruct3<DiagramCanvas, ClassRectangle, ClassRectangle>, DLine {

        @Property(key = 0)
        DiagramCanvas diagram();

        @Property(key = 1)
        ClassRectangle special();

        @Property(key = 2)
        ClassRectangle general();

        @Override
        default DPoint position() {
            return special().centre();
        }

        @Override
        default DPoint endPoint() {
            return general().centre();
        }

    }

    interface ClassRectangle extends DStruct2<DiagramCanvas, DStructClass<?>>, DRectangle {

        @Property(key = 0)
        DiagramCanvas diagram();

        @Property(key = 1)
        DStructClass<?> cls();

        @Override
        default String text() {
            return cls().name();
        }

        @Override
        default boolean selected() {
            return cls().equals(diagram().wb().selected());
        }

        @Override
        default DDimension size() {
            return dclare(DDimension.class, 120.0, 40.0);
        }

        @Property
        default Set<ClassRectangle> supers() {
            //noinspection unchecked
            return cls().supers().map(s -> dclare(ClassRectangle.class, diagram(), s)).asSet();
        }

        @Property
        default int level() {
            return supers().map(ClassRectangle::level).reduce(-1, Math::max) + 1;
        }

        @Property
        default int order() {
            Level l = diagram().levels().get((Integer) level());
            return l == null ? 0 : l.sorted().firstIndexOf(this);
        }

        @Property
        default int upper() {
            Set<ClassRectangle> supers = supers();
            return supers.isEmpty() ? 0 : supers.map(ClassRectangle::order).reduce(0, Integer::sum) / supers.size();
        }

        @Override
        default DPoint position() {
            return dragging() ? position() : dclare(DPoint.class, order() * 130 + 10.0, level() * 110 + 10.0);
        }

    }

}
