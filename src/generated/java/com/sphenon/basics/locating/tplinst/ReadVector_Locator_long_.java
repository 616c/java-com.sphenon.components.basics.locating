// instantiated with jti.pl from ReadVector
// please do not modify this file directly
package com.sphenon.basics.locating.tplinst;

import com.sphenon.basics.locating.*;

import com.sphenon.basics.context.*;
import com.sphenon.basics.exception.*;

import com.sphenon.basics.many.returncodes.*;

public interface ReadVector_Locator_long_
{
    public Locator                                    get             (CallContext context, long index) throws DoesNotExist;
    public Locator                                    tryGet          (CallContext context, long index);
    public boolean                                     canGet          (CallContext context, long index);

    public ReferenceToMember_Locator_long_ReadOnlyVector_Locator_long__  getReference    (CallContext context, long index) throws DoesNotExist;
    public ReferenceToMember_Locator_long_ReadOnlyVector_Locator_long__  tryGetReference (CallContext context, long index);
}

