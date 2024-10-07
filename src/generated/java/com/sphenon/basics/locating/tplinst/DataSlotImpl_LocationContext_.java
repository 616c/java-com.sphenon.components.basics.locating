// instantiated with jti.pl from DataSlotImpl
// please do not modify this file directly
package com.sphenon.basics.locating.tplinst;

import com.sphenon.basics.locating.*;

import com.sphenon.basics.context.*;

public class DataSlotImpl_LocationContext_
    implements DataSlot_LocationContext_ {

    LocationContext data;

    public DataSlotImpl_LocationContext_ (CallContext context) {
    }

    public DataSlotImpl_LocationContext_ (CallContext context, LocationContext data) {
        this.data = data;
    }

    public void set(CallContext context, LocationContext data) {
        this.data = data;
    }

    public void setObject(CallContext context, Object data) {
        set(context, (LocationContext)data);
    }

    public LocationContext get(CallContext context) {
        return this.data;
    }

    public Object getObject(CallContext context) {
        return get(context);
    }
}
