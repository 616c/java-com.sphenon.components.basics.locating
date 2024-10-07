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
import com.sphenon.basics.debug.*;
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

public class Class_Location implements Location, Dumpable, ContextAware {

    public List<Locator> getLocators (CallContext context) {
        Vector<Locator> locators = new Vector<Locator>();
        locators.add(this.locator);
        return locators;
    }

    protected Locator locator;

    public Locator getLocator (CallContext context) {
        return this.locator;
    }

    public void setLocator (CallContext context, Locator locator) {
        this.locator = locator;
    }

    public Class_Location (CallContext context, Locator locator) {
        this.locator = locator;
    }

    public String tryGetTextLocator (CallContext context, Locator relative_to, Object preferences) {
        return this.locator.tryGetTextLocator(context, relative_to);
    }

    public String tryGetTextLocatorValue (CallContext context, Locator relative_to, String locator_class) {
        return this.locator.tryGetTextLocatorValue (context, relative_to, locator_class);
    }

    public String tryGetTextLocatorValue (CallContext context, Location relative_to, String locator_class) {
        if (relative_to instanceof Class_Location) {
            return this.locator.tryGetTextLocatorValue (context, ((Class_Location) relative_to).locator, locator_class);
        }
        return null;
    }

    public String tryGetTextLocatorValue (CallContext context, String relative_to, String locator_class) {
        return this.locator.tryGetTextLocatorValue (context, relative_to, locator_class);
    }

    public Object retrieveTarget(CallContext context, Object base_object) throws InvalidLocator {
        return this.locator.retrieveTarget(context, base_object);
    }

    public Location appendLocator (CallContext context, Locator sub_locator) {
        this.locator.appendLocator(context, sub_locator);
        return this;
    }

    public Class_Location clone() {
        Class_Location cloned_location = null;
        try {
            cloned_location = (Class_Location) super.clone();
        } catch (CloneNotSupportedException cnse) {
        }
        cloned_location.locator = (Locator) cloned_location.locator.clone();
        return cloned_location;
    }

    public String toString() {
        return super.toString() + " [ " + this.locator.toString() + " ] ";
    }

    public String toString(CallContext context) {
        return super.toString() + " [ " + this.locator.toString(context) + " ] ";
    }

    public String getUniqueIdentifier(CallContext context) {
        return this.locator.getPartialTextLocator(context);
    }

    public void setSourceLocationContext (CallContext context, DataSource_LocationContext_ location_context) {
        this.locator.setSourceLocationContext(context, location_context);
    }

    public DataSource_LocationContext_ getTargetLocationContext (CallContext context) {
        // hier wird's richtig lustig wenn mehrere Locators da sind:
        // dann werden nämlich mehrere parent-location-contexts
        // nötig, siehe MultipleLocationParents.iss
        return this.locator.getTargetLocationContext(context);
    }

    public void dump(CallContext context, DumpNode dump_node) {
        DumpNode dn = dump_node.openDump(context, "Location");
        dn.dump(context, "Locator", this.locator);
        dn.close(context);
    }

    public boolean equals(Object object) {
        if (object == null) { return false; }
        if ((object instanceof Class_Location) == false) { return false; }
        Class_Location other = (Class_Location) object;
        if (this.locator == other.locator) { return true; }
        if (this.locator != null && this.locator.equals(other.locator)) { return true; }
        return false;
    }
}
