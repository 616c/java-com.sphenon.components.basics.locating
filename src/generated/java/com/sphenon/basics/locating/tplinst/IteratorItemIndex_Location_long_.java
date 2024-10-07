// instantiated with jti.pl from IteratorItemIndex
// please do not modify this file directly
package com.sphenon.basics.locating.tplinst;

import com.sphenon.basics.locating.*;

import com.sphenon.basics.context.*;
import com.sphenon.basics.exception.*;

import com.sphenon.basics.many.*;
import com.sphenon.basics.many.returncodes.*;

public interface IteratorItemIndex_Location_long_
  extends Iterator_Location_
    , IteratorItemIndex<Location>
{
    // returns current index
    public long getCurrentIndex (CallContext context) throws DoesNotExist;

    // like "getCurrentIndex", but returns null instead of throwing exception
    public long tryGetCurrentIndex (CallContext context);
}
