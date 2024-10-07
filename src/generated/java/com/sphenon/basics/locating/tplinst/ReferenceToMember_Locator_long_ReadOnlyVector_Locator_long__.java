// instantiated with jti.pl from ReferenceToMember
// please do not modify this file directly
package com.sphenon.basics.locating.tplinst;

import com.sphenon.basics.locating.*;

import com.sphenon.basics.context.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.reference.*;
import com.sphenon.basics.many.*;

public interface ReferenceToMember_Locator_long_ReadOnlyVector_Locator_long__
  extends Reference_Locator_
    , ReferenceToMember<Locator,ReadOnlyVector<Locator>>
{
    public ReadOnlyVector_Locator_long_ getContainer(CallContext context);
    public long     getIndex    (CallContext context);
}
