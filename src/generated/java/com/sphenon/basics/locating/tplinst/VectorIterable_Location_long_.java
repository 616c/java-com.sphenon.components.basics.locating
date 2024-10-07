// instantiated with jti.pl from VectorIterable
// please do not modify this file directly
package com.sphenon.basics.locating.tplinst;

import com.sphenon.basics.locating.*;

import com.sphenon.basics.context.*;
import com.sphenon.basics.exception.*;

import com.sphenon.basics.many.*;
import com.sphenon.basics.many.returncodes.*;

public class VectorIterable_Location_long_ implements Iterable<Location>
{
    protected java.util.Iterator<Location> iterator;

    public VectorIterable_Location_long_ (CallContext context, Vector_Location_long_ vector) {
        this.iterator = (vector == null ? (new java.util.Vector<Location>()).iterator() : vector.getIterator_Location_(context));
    }

    public java.util.Iterator<Location> iterator () {
        return this.iterator;
    }
}

