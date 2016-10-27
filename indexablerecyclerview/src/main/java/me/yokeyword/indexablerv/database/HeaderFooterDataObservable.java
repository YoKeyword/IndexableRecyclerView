/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.yokeyword.indexablerv.database;

import android.database.Observable;

/**
 * A specialization of {@link Observable} for {@link DataObserver}
 * that provides methods for sending notifications to a list of
 * {@link DataObserver} objects.
 */
public class HeaderFooterDataObservable extends Observable<HeaderFooterDataObserver> {

    /**
     * Invokes {@link DataObserver#onChanged} on each observer.
     * Called when the contents of the data set have changed.  The recipient
     * will obtain the new contents the next time it queries the data set.
     */
    public void notifyChanged() {
        synchronized (mObservers) {
            // since onChanged() is implemented by the app, it could do anything, including
            // removing itself from {@link mObservers} - and that could cause problems if
            // an iterator is used on the ArrayList {@link mObservers}.
            // to avoid such problems, just march thru the list in the reverse order.
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onChanged();
            }
        }
    }

    public void notifyAdd(boolean header, Object preData, Object data) {
        synchronized (mObservers) {
            // since onChanged() is implemented by the app, it could do anything, including
            // removing itself from {@link mObservers} - and that could cause problems if
            // an iterator is used on the ArrayList {@link mObservers}.
            // to avoid such problems, just march thru the list in the reverse order.
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onAdd(header, preData, data);
            }
        }
    }

    public void notifyRemove(boolean header, Object object) {
        synchronized (mObservers) {
            // since onChanged() is implemented by the app, it could do anything, including
            // removing itself from {@link mObservers} - and that could cause problems if
            // an iterator is used on the ArrayList {@link mObservers}.
            // to avoid such problems, just march thru the list in the reverse order.
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onRemove(header, object);
            }
        }
    }
//
//    public void notifyAddAll(Object preData, Object data) {
//        synchronized (mObservers) {
//            // since onChanged() is implemented by the app, it could do anything, including
//            // removing itself from {@link mObservers} - and that could cause problems if
//            // an iterator is used on the ArrayList {@link mObservers}.
//            // to avoid such problems, just march thru the list in the reverse order.
//            for (int i = mObservers.size() - 1; i >= 0; i--) {
//                mObservers.get(i).onAddAll(itemType, position, datas);
//            }
//        }
//    }
//
//    public void notifyRemoveAll(Collection datas) {
//        synchronized (mObservers) {
//            // since onChanged() is implemented by the app, it could do anything, including
//            // removing itself from {@link mObservers} - and that could cause problems if
//            // an iterator is used on the ArrayList {@link mObservers}.
//            // to avoid such problems, just march thru the list in the reverse order.
//            for (int i = mObservers.size() - 1; i >= 0; i--) {
//                mObservers.get(i).onRemoveAll(itemType, datas);
//            }
//        }
//    }
}
