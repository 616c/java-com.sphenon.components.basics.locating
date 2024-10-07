// instantiated with jti.pl from WriteVector
// please do not modify this file directly
package com.sphenon.basics.locating.tplinst;

import com.sphenon.basics.locating.*;

import com.sphenon.basics.context.*;
import com.sphenon.basics.exception.*;

import com.sphenon.basics.many.returncodes.*;

public interface WriteVector_Location_long_
{
    public Location set          (CallContext context, long index, Location item);
    public void     add          (CallContext context, long index, Location item) throws AlreadyExists;
    public void     prepend      (CallContext context, Location item);
    public void     append       (CallContext context, Location item);
    public void     insertBefore (CallContext context, long index, Location item) throws DoesNotExist;
    public void     insertBehind (CallContext context, long index, Location item) throws DoesNotExist;
    public Location replace      (CallContext context, long index, Location item) throws DoesNotExist;
    public Location unset        (CallContext context, long index);
    public Location remove       (CallContext context, long index) throws DoesNotExist;
}

