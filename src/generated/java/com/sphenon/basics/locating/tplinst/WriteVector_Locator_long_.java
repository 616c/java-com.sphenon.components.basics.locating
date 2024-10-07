// instantiated with jti.pl from WriteVector
// please do not modify this file directly
package com.sphenon.basics.locating.tplinst;

import com.sphenon.basics.locating.*;

import com.sphenon.basics.context.*;
import com.sphenon.basics.exception.*;

import com.sphenon.basics.many.returncodes.*;

public interface WriteVector_Locator_long_
{
    public Locator set          (CallContext context, long index, Locator item);
    public void     add          (CallContext context, long index, Locator item) throws AlreadyExists;
    public void     prepend      (CallContext context, Locator item);
    public void     append       (CallContext context, Locator item);
    public void     insertBefore (CallContext context, long index, Locator item) throws DoesNotExist;
    public void     insertBehind (CallContext context, long index, Locator item) throws DoesNotExist;
    public Locator replace      (CallContext context, long index, Locator item) throws DoesNotExist;
    public Locator unset        (CallContext context, long index);
    public Locator remove       (CallContext context, long index) throws DoesNotExist;
}

