//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
// (C) Copyright 2018-2022 Modeling Value Group B.V. (http://modelingvalue.org)                                        ~
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

package org.modelingvalue.jdclare.meta;

import static org.modelingvalue.jdclare.PropertyQualifier.constant;

import java.util.function.Consumer;

import org.modelingvalue.dclare.Priority;
import org.modelingvalue.dclare.NonInternableObserver;
import org.modelingvalue.dclare.Observer;
import org.modelingvalue.jdclare.DObject;
import org.modelingvalue.jdclare.DStruct2;
import org.modelingvalue.jdclare.Property;

public interface DObjectRule<O extends DObject> extends DRule<O>, DStruct2<O, String> {

    @Property(key = 0)
    O object();

    @Property(key = 1)
    String id();

    @Override
    @Property(constant)
    Consumer<O> consumer();

    @Override
    @Property(constant)
    default String name() {
        return object() + "::" + id();
    }

    @Override
    @Property(constant)
    Priority initPriority();

    @Override
    @Property(constant)
    default Observer<O> observer() {
        return NonInternableObserver.of(this, o -> consumer().accept(o), initPriority());
    }

}
