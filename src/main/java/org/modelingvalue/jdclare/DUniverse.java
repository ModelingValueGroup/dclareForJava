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

package org.modelingvalue.jdclare;

import static org.modelingvalue.jdclare.DClare.dclare;
import static org.modelingvalue.jdclare.PropertyQualifier.constant;
import static org.modelingvalue.jdclare.PropertyQualifier.containment;
import static org.modelingvalue.jdclare.PropertyQualifier.hidden;

import org.modelingvalue.dclare.State;
import org.modelingvalue.dclare.Universe;
import org.modelingvalue.jdclare.meta.DPackageContainer;

public interface DUniverse extends DPackageContainer, DStruct0, Universe {

    @Default
    @Property(hidden)
    default IOString input() {
        return dclare(IOString.class, 0, "");
    }

    @Default
    @Property(hidden)
    default IOString output() {
        return dclare(IOString.class, 0, "");
    }

    @Default
    @Property(hidden)
    default IOString error() {
        return dclare(IOString.class, 0, "");
    }

    @Property({hidden, containment, constant})
    default DClock clock() {
        return dclare(DClock.class);
    }

    @Property(hidden)
    boolean stop();

    @Override
    default void init() {
        Universe.super.init(); // do not remove this! it seems unneccesarry but it is not; this has to do with how Proxy handles calls.
    }

    @Override
    default void exit() {
        Universe.super.exit(); // do not remove this! it seems unneccesarry but it is not; this has to do with how Proxy handles calls.
    }

    @Override
    default boolean isInternable() {
        return Universe.super.isInternable(); // do not remove this! it seems unneccesarry but it is not; this has to do with how Proxy handles calls.
    }

    @Override
    default boolean dIsOrphan(State state) {
        return Universe.super.dIsOrphan(state);
    }

}
