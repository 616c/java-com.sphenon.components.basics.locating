// instantiated with jti.pl from ReadVector
// please do not modify this file directly
package com.sphenon.basics.locating.tplinst;

import com.sphenon.basics.locating.*;

import com.sphenon.basics.context.*;
import com.sphenon.basics.exception.*;

import com.sphenon.basics.many.returncodes.*;

public interface ReadVector_Location_long_
{
    public Location                                    get             (CallContext context, long index) throws DoesNotExist;
    public Location                                    tryGet          (CallContext context, long index);
    public boolean                                     canGet          (CallContext context, long index);

    public ReferenceToMember_Location_long_ReadOnlyVector_Location_long__  getReference    (CallContext context, long index) throws DoesNotExist;
    public ReferenceToMember_Location_long_ReadOnlyVector_Location_long__  tryGetReference (CallContext context, long index);
}

