// instantiated with jti.pl from VectorIterable
// please do not modify this file directly
package com.sphenon.basics.locating.tplinst;

import com.sphenon.basics.locating.*;

import com.sphenon.basics.context.*;
import com.sphenon.basics.exception.*;

import com.sphenon.basics.many.*;
import com.sphenon.basics.many.returncodes.*;

public class VectorIterable_Locator_long_ implements Iterable<Locator>
{
    protected java.util.Iterator<Locator> iterator;

    public VectorIterable_Locator_long_ (CallContext context, Vector_Locator_long_ vector) {
        this.iterator = (vector == null ? (new java.util.Vector<Locator>()).iterator() : vector.getIterator_Locator_(context));
    }

    public java.util.Iterator<Locator> iterator () {
        return this.iterator;
    }
}

