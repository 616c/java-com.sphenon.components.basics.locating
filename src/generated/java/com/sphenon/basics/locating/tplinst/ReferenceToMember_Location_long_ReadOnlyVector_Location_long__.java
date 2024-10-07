// instantiated with jti.pl from ReferenceToMember
// please do not modify this file directly
package com.sphenon.basics.locating.tplinst;

import com.sphenon.basics.locating.*;

import com.sphenon.basics.context.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.reference.*;
import com.sphenon.basics.many.*;

public interface ReferenceToMember_Location_long_ReadOnlyVector_Location_long__
  extends Reference_Location_
    , ReferenceToMember<Location,ReadOnlyVector<Location>>
{
    public ReadOnlyVector_Location_long_ getContainer(CallContext context);
    public long     getIndex    (CallContext context);
}
