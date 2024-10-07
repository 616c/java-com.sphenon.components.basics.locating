package com.sphenon.basics.locating.classes;

/****************************************************************************
  Copyright 2001-2024 Sphenon GmbH

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

import com.sphenon.basics.locating.*;
import com.sphenon.basics.locating.tplinst.*;
import com.sphenon.basics.locating.returncodes.*;

import java.util.List;
import java.util.Vector;

public class LocationProxy implements Location {

    public List<Locator> getLocators (CallContext context) {
        return new Vector<Locator>();
    }

    public LocationProxy (CallContext context) {
    }

    public String tryGetTextLocator (CallContext context, Locator relative_to, Object preferences) {
        return null;
    }

    public String tryGetTextLocatorValue (CallContext context, Locator relative_to, String locator_class) {
        return null;
    }

    public String tryGetTextLocatorValue (CallContext context, Location relative_to, String locator_class) {
        return null;
    }

    public String tryGetTextLocatorValue (CallContext context, String relative_to, String locator_class) {
        return null;
    }

    public Location appendLocator (CallContext context, Locator sub_locator) {
        return this;
    }

    public Object retrieveTarget(CallContext context, Object base_object) throws InvalidLocator 
    {
	return null;
    }
	
    public String getUniqueIdentifier(CallContext context) {
        return null;
    }

    public Class_Location clone() {
        Class_Location cloned_location = null;
        try {
            cloned_location = (Class_Location) super.clone();
        } catch (CloneNotSupportedException cnse) {
        }
        return cloned_location;
    }

    public String toString() {
        return super.toString() + " [ ] ";
    }

    DataSource_LocationContext_ location_context;

    public void setSourceLocationContext (CallContext context, DataSource_LocationContext_ location_context) {
        this.location_context = location_context;
    }

    public DataSource_LocationContext_ getTargetLocationContext (CallContext context) {
        return this.location_context;
    }
}
