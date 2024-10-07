// instantiated with jti.pl from Vector
// please do not modify this file directly
package com.sphenon.basics.locating.tplinst;

import com.sphenon.basics.locating.*;

import com.sphenon.basics.context.*;
import com.sphenon.basics.exception.*;

import com.sphenon.basics.many.*;
import com.sphenon.basics.many.returncodes.*;

import com.sphenon.ui.annotations.*;

@UIId("")
@UIName("")
@UIClassifier("Vector_Locator_")
@UIParts("js:instance.getIterable(context)")
public interface Vector_Locator_long_
  extends ReadOnlyVector_Locator_long_,
          WriteVector_Locator_long_
          , GenericVector<Locator>
          , GenericIterable<Locator>
{
    public Locator                                    get             (CallContext context, long index) throws DoesNotExist;
    public Locator                                    tryGet          (CallContext context, long index);
    public boolean                                     canGet          (CallContext context, long index);

    public ReferenceToMember_Locator_long_ReadOnlyVector_Locator_long__  getReference    (CallContext context, long index) throws DoesNotExist;
    public ReferenceToMember_Locator_long_ReadOnlyVector_Locator_long__  tryGetReference (CallContext context, long index);

    public Locator                                    set             (CallContext context, long index, Locator item);
    public void                                        add             (CallContext context, long index, Locator item) throws AlreadyExists;
    public void                                        prepend         (CallContext context, Locator item);
    public void                                        append          (CallContext context, Locator item);
    public void                                        insertBefore    (CallContext context, long index, Locator item) throws DoesNotExist;
    public void                                        insertBehind    (CallContext context, long index, Locator item) throws DoesNotExist;
    public Locator                                    replace         (CallContext context, long index, Locator item) throws DoesNotExist;
    public Locator                                    unset           (CallContext context, long index);
    public Locator                                    remove          (CallContext context, long index) throws DoesNotExist;

    public IteratorItemIndex_Locator_long_       getNavigator    (CallContext context);

    public long                                        getSize         (CallContext context);

    // for sake of Iterable's
    public java.util.Iterator<Locator>              getIterator_Locator_ (CallContext context);
    public java.util.Iterator                          getIterator (CallContext context);
    public VectorIterable_Locator_long_          getIterable_Locator_ (CallContext context);
    public Iterable<Locator> getIterable (CallContext context);
}
