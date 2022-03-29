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

package org.modelingvalue.jdclare;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public interface DNative<T extends DObject> {

    default void init(DObject parent) {
    }

    default void exit(DObject parent) {
    }

    class ChangeHandler<O extends DObject, V> {

        public static <D extends DObject, E> ChangeHandler<D, E> of(Method handler) {
            return new ChangeHandler<>(handler);
        }

        private final Method  handler;
        private final boolean deferred;

        private ChangeHandler(Method handler) {
            this.handler = handler;
            this.deferred = handler.isAnnotationPresent(Deferred.class);
        }

        public boolean deferred() {
            return deferred;
        }

        public void handle(DNative<O> nat, V pre, V post) {
            try {
                DClare.actualize(nat.getClass(), handler).invoke(nat, pre, post);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new Error(e);
            }
        }
    }
}
