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

package org.modelingvalue.jdclare.syntax.test;

import org.modelingvalue.collections.*;
import org.modelingvalue.jdclare.*;
import org.modelingvalue.jdclare.syntax.*;
import org.modelingvalue.jdclare.syntax.test.simple.*;
import org.modelingvalue.jdclare.syntax.test.types.*;

import static org.modelingvalue.jdclare.DClare.*;
import static org.modelingvalue.jdclare.PropertyQualifier.*;

@Skipped({"\\s+", "//[^\\r\\n]*\\r?\\n"})
public interface MySyntax extends Grammar, DStruct0 {

    @Sequence({PACKAGE.class, SEMICOLON.class})
    interface Unit extends ObjectNode {
        @Syntax(nr = 1, separator = DOT.class)
        @Property({containment, mandatory})
        List<PACKAGE_REF> cPackages();

        @Syntax(nr = 3)
        List<CClass> cClasses();

        @Rule
        default void addClasses() {
            set(cPackages().last().aPackage(), APackage::classes, //
                    (o, a) -> o.filter(c -> !fromEqualText(c)).asSet().addAll(a), cClasses());
        }
    }

    @Regex("[a-zA-Z]\\w*")
    interface PACKAGE_REF extends ObjectNode {
        @Syntax
        APackage aPackage();

        @Constraints
        private void sConstraints() {
            SCOPE(PACKAGE_REF::aPackage, PACKAGE_REF::packageScope);
        }

        @Property
        default Set<APackage> packageScope() {
            PACKAGE_REF pre  = dAncestor(Unit.class).cPackages().previous(this);
            APackage    pack = pre != null ? pre.aPackage() : null;
            return pack != null ? pack.aPackages() : ((APackageContainer) dUniverse()).aPackages();
        }
    }

    @Sequence({CLASS.class, BEGIN.class, END.class})
    interface CClass extends ObjectNode, AClass {
        @Override
        @Syntax(nr = 1, type = IDENTIFIER.class)
        String name();

        @Override
        @Syntax(nr = 3, type = CField.class)
        Set<AField> fields();
    }

    @Sequence(SEMICOLON.class)
    interface CField extends AField, ObjectNode {
        @Override
        @Syntax(nr = 0, type = CType.class)
        AType type();

        @Override
        @Syntax(nr = 1, type = IDENTIFIER.class)
        String name();

        @Override
        @Syntax(nr = 2, prefix = ASSIGN.class)
        CExpression expression();

        @Override
        default String asString() {
            return sParserNode() == null ? "<Unknown>" : AField.super.asString();
        }
    }

    interface CExpression extends AExpression, ObjectNode {
    }

    @Sequence
    interface CFieldCall extends AFieldCall, CExpression {
        @Override
        @Syntax(nr = 0, postfix = DOT.class)
        CExpression base();

        @Override
        @Syntax(nr = 1, type = IDENTIFIER.class)
        @Property
        AField field();
    }

    @Regex("\"[^\"]*\"")
    interface STRING extends AStringLiteral, CExpression {
        @Syntax
        @Default
        @Property(hidden)
        default String quotedString() {
            return "\"\"";
        }

        @Override
        default String stringValue() {
            String qs = quotedString();
            return qs.substring(1, qs.length() - 1);
        }
    }

    @Regex("-?\\d+")
    interface NUMBER extends ANumberLiteral, CExpression {
        @Override
        @Syntax
        int numberValue();
    }

    interface CType extends StructNode {
    }

    @Regex("[a-zA-Z]\\w*")
    interface CClassType extends CType {
    }

    @Literal("Number")
    interface L_NUMBER extends CType {
    }

    @Literal("String")
    interface L_STRING extends CType {
    }

    @Regex("[a-zA-Z]\\w*")
    interface IDENTIFIER extends StructNode {
    }

    @Literal("package")
    interface PACKAGE extends StructNode {
    }

    @Literal("class")
    interface CLASS extends StructNode {
    }

    @Literal("{")
    interface BEGIN extends StructNode {
    }

    @Literal("}")
    interface END extends StructNode {
    }

    @Literal("=")
    interface ASSIGN extends StructNode {
    }

    @Literal(";")
    interface SEMICOLON extends StructNode {
    }

    @Literal(",")
    interface COMMA extends StructNode {
    }

    @Literal(".")
    interface DOT extends StructNode {
    }

}
