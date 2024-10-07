package com.sphenon.basics.locating;

/****************************************************************************
  Copyright 2001-2024 Sphenon GmbH

  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  License for the specific language governing permissions and limitations
  under the License.
*****************************************************************************/

import com.sphenon.basics.context.*;
import com.sphenon.basics.debug.*;
import com.sphenon.basics.message.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.customary.*;

import java.util.Hashtable;
import java.util.Vector;

public class LocatingContext extends SpecificContext implements Dumpable {

    static protected LocatingContext default_singleton;
    protected boolean is_default_singelton;

    static public LocatingContext getOrCreate(Context context) {
        LocatingContext locating_context = (LocatingContext) context.getSpecificContext(LocatingContext.class);
        if (locating_context == null) {
            locating_context = new LocatingContext(context, false);
            context.setSpecificContext(LocatingContext.class, locating_context);
        }
        return locating_context;
    }

    static public LocatingContext get(Context context) {
        LocatingContext locating_context = (LocatingContext) context.getSpecificContext(LocatingContext.class);
        if (locating_context != null) {
            return locating_context;
        }
        return default_singleton == null ? (default_singleton = new LocatingContext(context, true)) : default_singleton;
    }

    static public LocatingContext create(Context context) {
        LocatingContext locating_context = new LocatingContext(context, false);
        context.setSpecificContext(LocatingContext.class, locating_context);
        return locating_context;
    }

    protected LocatingContext (Context context, boolean is_default_singelton) {
        super(context);
        this.is_default_singelton = is_default_singelton;
        this.scope = null;
    }

    protected Hashtable scope;

    public void registerObject(CallContext context, String name, Object value) {
        if (is_default_singelton) {
            CustomaryContext.create((Context) context).throwPreConditionViolation(context, "Cannot modify default singelton LocatingContext");
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
        if (this.scope == null) {
            this.scope = new Hashtable();
        }
        this.scope.put(name, value);
    }

    public Object getObject(CallContext cc, String name) {
        if (is_default_singelton) { return null; }
        LocatingContext locating_context;
        Object value = (this.scope != null ? this.scope.get(name) : null);
        return (value != null ?
                     value
                  : (locating_context = (LocatingContext) this.getCallContext(LocatingContext.class)) != null ?
                       locating_context.getObject(cc, name)
                     : null
               );
    }

    protected Vector<Object> base_objects;

    public void pushBaseObject(CallContext context, Object object) {
        if (is_default_singelton) {
            CustomaryContext.create((Context) context).throwPreConditionViolation(context, "Cannot modify default singelton LocatingContext");
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
        if (this.base_objects == null) {
            this.base_objects = new Vector<Object>();
        }
        this.base_objects.add(object);
    }

    public Object lookupObject(CallContext context, Locator locator) {
        if (is_default_singelton) { return null; }
        Object base_object_result;
        if (base_objects != null) {
            for (int i=base_objects.size()-1; i >= 0; i--) {
                Object candidate = base_objects.elementAt(i);
                if ((base_object_result = locator.tryAcceptBaseObject(context, candidate)) != null) { return base_object_result; }
            }
        }
        LocatingContext locating_context;
        if ((locating_context = (LocatingContext) this.getLocationContext(LocatingContext.class)) != null) {
            if ((base_object_result = locating_context.lookupObject(context, locator)) != null) { return base_object_result; }
        }
        if ((locating_context = (LocatingContext) this.getCallContext(LocatingContext.class)) != null) {
            if ((base_object_result = locating_context.lookupObject(context, locator)) != null) { return base_object_result; }
        }

        return null;
    }

    public void dump(CallContext context, DumpNode dump_node) {
        DumpNode dn = dump_node.openDump(context, "LocatingContext");
        if (base_objects != null) {
            int i=1;
            DumpNode dns1 = dn.openDump(context, "Base Objects");
            for (Object candidate : base_objects) {
                dns1.dump(context, (new Integer(i++)).toString(), candidate);
            }
            dns1.close(context);
        }
    }
}
