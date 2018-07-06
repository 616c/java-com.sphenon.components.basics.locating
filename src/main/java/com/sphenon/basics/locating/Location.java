package com.sphenon.basics.locating;

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

import com.sphenon.basics.context.*;
import com.sphenon.basics.message.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.configuration.*;

import com.sphenon.basics.locating.tplinst.*;
import com.sphenon.basics.locating.returncodes.*;

/**
   A location is a logical sub entity of a logical space.

   In general, objects may be located at locations. Location may serve as
   references for objects, e.g. for retrieval. Locations can be simple or
   complex, small or large. Locations may hold none, just one, or many
   objects. Locations may have a substructure themselfes and may contain
   further locations.

   Locations are located by Locators. The same location may be located by
   different locators (e.g. a certain folder may be located by a file system
   path and at the same time by a URL via internet).

   Locations attach the located objects to the spatial context where the
   location resides. Therefore, locations may provide certain contextual
   information and characteristics. E.g., an object located at a certain
   location may imply storage of that object in a database, or, another e.g.,
   a location may imply a certain human language as the default for
   documents.

   @see Locator
   @see Space
 */

import java.util.List;

public interface Location extends Cloneable {

    /**
       Retrieves all locators of this location

       @returns the locators
     */
    public List<Locator> getLocators (CallContext context);

    /**
       Chooses, from the locators of this location, a preferred one and
       returns its text locator representation, relative to the  given
       locator (see {@link Locator#tryGetTextLocator} for details).

       (Currently, preferences are not evaluated but simply the first locator
       is returned).

       @returns the text locator of the preferred locator
     */
    public String tryGetTextLocator (CallContext context, Locator relative_to, Object preferences);

    /**
       Retrieves a locator of the requested class which locates this location instance.
       Retrieving such a locator may involve execution of a more or less complicated
       resolution scheme.

       @param relative_to the resulting locator is relative to the location specified by this parameter
       @param locator_class the resulting locator is an instance of this class
     */
    public String tryGetTextLocatorValue (CallContext context, Locator relative_to, String locator_class);

    /**
       Retrieves a locator of the requested class which locates this location instance.
       Retrieving such a locator may involve execution of a more or less complicated
       resolution scheme.

       @param relative_to the resulting locator is relative to the location specified by this parameter
       @param locator_class the resulting locator is an instance of this class
     */
    public String tryGetTextLocatorValue (CallContext context, Location relative_to, String locator_class);

    /**
       Retrieves a locator of the requested class which locates this location instance.
       Retrieving such a locator may involve execution of a more or less complicated
       resolution scheme.

       @param relative_to the resulting locator is relative to the location specified by this parameter
       @param locator_class the resulting locator is an instance of this class
     */
    public String tryGetTextLocatorValue (CallContext context, String relative_to, String locator_class);

    /**
       Chooses, from the locators of this location, a preferred one and retrieves it's target.

       @param base_object passed to the selected Locator
       @return Target of the selected Locator
    */
    public Object retrieveTarget(CallContext context, Object base_object) throws InvalidLocator;

    /**
       Appends the sub_locator to each locator that belongs to this location.
       In effect, this location is "moved" by the amount given by the sub_locator.

       @param sub_locator a relative locator, typically a PathLocator, that defines the amount to move the location
       @returns For convenience, returns this Location instance itself
     */
    public Location appendLocator (CallContext context, Locator sub_locator);

    /**
       Embeds all locators of this location into the given LocationContext
       parameter (on demand via a DataSource).
       If a locator is already embedded, the operation leaves it unchanged.
    */
    public void setSourceLocationContext (CallContext context, DataSource_LocationContext_ location_context);

    /**
       Retrieves a LocationContext containing the LocatingContexts of the
       Locators plus all Locator targets (on demand via a DataSource)
     */
    public DataSource_LocationContext_ getTargetLocationContext (CallContext context);

    /**
       Retrieves a unique string identifying this location, typically this is a list of the contained locators.
     */
    public String getUniqueIdentifier(CallContext context);

    /**
       Clones the locator, and clears a cached target in the clone.
     */
    public Location clone();
}
