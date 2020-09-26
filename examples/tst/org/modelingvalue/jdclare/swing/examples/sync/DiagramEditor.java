package org.modelingvalue.jdclare.swing.examples.sync;

import static org.modelingvalue.jdclare.DClare.*;
import static org.modelingvalue.jdclare.PropertyQualifier.*;

import java.awt.event.*;
import java.util.function.*;

import org.modelingvalue.collections.*;
import org.modelingvalue.jdclare.*;
import org.modelingvalue.jdclare.swing.*;
import org.modelingvalue.jdclare.swing.draw2d.*;

public interface DiagramEditor extends SplitPane {
    default DToolbarItem item(String text, String imageLink, Consumer<ActionEvent> action) {
        return dclareUU(DToolbarItem.class, //
                set(DToolbarItem::minimumSize, dclare(DDimension.class, 50.0, 50.0)), //
                set(DToolbarItem::text, text), //
                set(DToolbarItem::action, action), //
                set(DToolbarItem::imageLink, dclare(DImage.class, imageLink))//
        );
    }

    default DCanvas canvas() {
        return (DCanvas) ((ScrollPane) leftComponent()).viewportView();
    }

    default void appendShape(DShape s) {
        set(canvas(), DCanvas::shapes, List::append, s);
    }

    default void prependShape(DShape s) {
        set(canvas(), DCanvas::shapes, List::prepend, s);
    }

    @Override
    @Property(constant)
    default double resizeWeight() {
        return 1;
    }

    @Override
    default boolean vertical() {
        return false;
    }

    @Override
    default boolean disableDivider() {
        return true;
    }
}
