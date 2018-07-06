// instantiated with jti.pl from Vector

/****************************************************************************
  Copyright 2001-2018 Sphenon GmbH

  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  License for the specific language governing permissions and limitations
  under the License.
*****************************************************************************/
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
@UIClassifier("Vector_Location_")
@UIParts("js:instance.getIterable(context)")
public interface Vector_Location_long_
  extends ReadOnlyVector_Location_long_,
          WriteVector_Location_long_
          , GenericVector<Location>
          , GenericIterable<Location>
{
    public Location                                    get             (CallContext context, long index) throws DoesNotExist;
    public Location                                    tryGet          (CallContext context, long index);
    public boolean                                     canGet          (CallContext context, long index);

    public ReferenceToMember_Location_long_ReadOnlyVector_Location_long__  getReference    (CallContext context, long index) throws DoesNotExist;
    public ReferenceToMember_Location_long_ReadOnlyVector_Location_long__  tryGetReference (CallContext context, long index);

    public Location                                    set             (CallContext context, long index, Location item);
    public void                                        add             (CallContext context, long index, Location item) throws AlreadyExists;
    public void                                        prepend         (CallContext context, Location item);
    public void                                        append          (CallContext context, Location item);
    public void                                        insertBefore    (CallContext context, long index, Location item) throws DoesNotExist;
    public void                                        insertBehind    (CallContext context, long index, Location item) throws DoesNotExist;
    public Location                                    replace         (CallContext context, long index, Location item) throws DoesNotExist;
    public Location                                    unset           (CallContext context, long index);
    public Location                                    remove          (CallContext context, long index) throws DoesNotExist;

    public IteratorItemIndex_Location_long_       getNavigator    (CallContext context);

    public long                                        getSize         (CallContext context);

    // for sake of Iterable's
    public java.util.Iterator<Location>              getIterator_Location_ (CallContext context);
    public java.util.Iterator                          getIterator (CallContext context);
    public VectorIterable_Location_long_          getIterable_Location_ (CallContext context);
    public Iterable<Location> getIterable (CallContext context);
}
