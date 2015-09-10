package com.umad.wat.base;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.WeakHashMap;

public class ActivityConnector<AttachedObject> {
    private WeakHashMap<AttachedObject, Object> weakHashMap = new WeakHashMap<>();
    private WeakReference<AttachedObject> attachedObjectRef;

    public final void attach(@NonNull AttachedObject object) {
        final WeakReference<AttachedObject> weakReference = new WeakReference<>(object);
        weakHashMap.put(object, new Object());
        attachedObjectRef = weakReference;
    }

    public final void detach(@NonNull AttachedObject object) {
        if (weakHashMap.remove(object) == null) {
            return;
        }

        Iterator<AttachedObject> it = weakHashMap.keySet().iterator();
        if (it.hasNext()) {
            attachedObjectRef = new WeakReference<>(it.next());
        }
        else {
            attachedObjectRef = null;
        }
    }

    @Nullable
    protected AttachedObject getAttachedObject() {
        if (attachedObjectRef == null)
            return null;
        return attachedObjectRef.get();
    }
}
