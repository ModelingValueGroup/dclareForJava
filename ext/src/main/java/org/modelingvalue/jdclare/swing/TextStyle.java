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

import org.modelingvalue.jdclare.*;

import javax.swing.text.*;
import javax.swing.text.Highlighter.*;
import java.awt.*;

import static org.modelingvalue.jdclare.PropertyQualifier.*;

public interface TextStyle extends DStruct6<Color, Color, Boolean, Boolean, Boolean, HighlightPainter> {

    StyleContext STYLE_CONTEXT = StyleContext.getDefaultStyleContext();

    @Property(key = 0)
    Color foreground();

    @Property(key = 1)
    Color background();

    @Property(key = 2)
    boolean bold();

    @Property(key = 3)
    boolean underline();

    @Property(key = 4)
    boolean italic();

    @Property(key = 5)
    HighlightPainter highligth();

    @Property(constant)
    default AttributeSet attributeSet() {
        AttributeSet as = STYLE_CONTEXT.getEmptySet();
        Color fg = foreground();
        if (fg != null) {
            as = STYLE_CONTEXT.addAttribute(as, StyleConstants.Foreground, fg);
        }
        Color bg = background();
        if (bg != null) {
            as = STYLE_CONTEXT.addAttribute(as, StyleConstants.Background, bg);
        }
        if (bold()) {
            as = STYLE_CONTEXT.addAttribute(as, StyleConstants.Bold, true);
        }
        if (underline()) {
            as = STYLE_CONTEXT.addAttribute(as, StyleConstants.Underline, true);
        }
        if (italic()) {
            as = STYLE_CONTEXT.addAttribute(as, StyleConstants.Italic, true);
        }
        return as;
    }

}
