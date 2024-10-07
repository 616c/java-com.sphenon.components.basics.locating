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
import com.sphenon.basics.context.classes.*;
import com.sphenon.basics.debug.*;
import com.sphenon.basics.message.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.configuration.*;
import com.sphenon.basics.data.*;

import com.sphenon.basics.locating.*;
import com.sphenon.basics.locating.tplinst.*;
import com.sphenon.basics.locating.returncodes.*;

import java.util.List;
import java.util.Vector;

public class Class_LocationVolatile implements Location, Dumpable, ContextAware {

    public List<Locator> getLocators (CallContext context) {
        Vector<Locator> locators = new Vector<Locator>();
        locators.add(this.getLocator(context));
        return locators;
    }

    protected DataSource<Locator> locator_source;
    protected Locator last_retrieved_locator;
    protected Locator my_cloned_locator;

    public DataSource<Locator> getLocatorSource (CallContext context) {
        return this.locator_source;
    }

    public void setLocatorSource (CallContext context, DataSource<Locator> locator_source) {
        this.locator_source = locator_source;
    }

    protected Vector<Locator> appended_locators;
    protected DataSource_LocationContext_ source_location_context;

    public Locator getLocator (CallContext context) {
        Locator my_locator = this.locator_source.get(context);

        if (this.appended_locators != null || this.source_location_context != null) {

            if (this.my_cloned_locator != null && this.last_retrieved_locator == my_locator) {
                return this.my_cloned_locator;
            }

            this.last_retrieved_locator = my_locator;
            this.my_cloned_locator = my_locator.clone();

            if (this.appended_locators != null) {
                for (Locator appended_locator : this.appended_locators) {
                    this.my_cloned_locator.appendLocator(context, appended_locator);
                }
            }

            if (this.source_location_context != null) {
                this.my_cloned_locator.setSourceLocationContext(context, this.source_location_context);
            }

            return this.my_cloned_locator;
        }

        return my_locator;
    }

    public Class_LocationVolatile (CallContext context, DataSource<Locator> locator_source) {
        this.locator_source = locator_source;
    }

    public String tryGetTextLocator (CallContext context, Locator relative_to, Object preferences) {
        return this.getLocator(context).tryGetTextLocator(context, relative_to);
    }

    public String tryGetTextLocatorValue (CallContext context, Locator relative_to, String locator_class) {
        return this.getLocator(context).tryGetTextLocatorValue (context, relative_to, locator_class);
    }

    public String tryGetTextLocatorValue (CallContext context, Location relative_to, String locator_class) {
        if (relative_to instanceof Class_Location) {
            return this.getLocator(context).tryGetTextLocatorValue (context, ((Class_Location) relative_to).locator, locator_class);
        }
        return null;
    }

    public String tryGetTextLocatorValue (CallContext context, String relative_to, String locator_class) {
        return this.getLocator(context).tryGetTextLocatorValue (context, relative_to, locator_class);
    }

    public Object retrieveTarget(CallContext context, Object base_object) throws InvalidLocator {
        return this.getLocator(context).retrieveTarget(context, base_object);
    }

    public Location appendLocator (CallContext context, Locator sub_locator) {
        if (this.appended_locators == null) {
            this.appended_locators = new Vector<Locator>();
        }
        this.appended_locators.add(sub_locator);

        this.last_retrieved_locator = null;
        this.my_cloned_locator = null;

        return this;
    }

    public Class_LocationVolatile clone() {
        Class_LocationVolatile cloned_location = null;
        try {
            cloned_location = (Class_LocationVolatile) super.clone();
        } catch (CloneNotSupportedException cnse) {
        }
        // das geht nicht so ohne weiteres, weil nicht alle DataSources clonable sind
        // (wäre auch umtändlich mitunter)
        // siehe oben "getLocator"
        // cloned_location.locator_source = (Locator) cloned_location.locator_source.clone();
        return cloned_location;
    }

    public String toString() {
        return this.toString(RootContext.getFallbackCallContext());
    }

    public String toString(CallContext context) {
        return super.toString() + " [ " + this.getLocator(context).toString(context) + " ] ";
    }

    public String getUniqueIdentifier(CallContext context) {
        return this.getLocator(context).getPartialTextLocator(context);
    }

    public void setSourceLocationContext (CallContext context, DataSource_LocationContext_ location_context) {
        this.source_location_context = location_context;

        this.last_retrieved_locator = null;
        this.my_cloned_locator = null;
    }

    public DataSource_LocationContext_ getTargetLocationContext (CallContext context) {
        // hier wird's richtig lustig wenn mehrere Locators da sind:
        // dann werden nämlich mehrere parent-location-contexts
        // nötig, siehe MultipleLocationParents.iss
        return this.getLocator(context).getTargetLocationContext(context);
    }

    public void dump(CallContext context, DumpNode dump_node) {
        DumpNode dn = dump_node.openDump(context, "Location");
        dn.dump(context, "Locator", this.getLocator(context));
        dn.close(context);
    }

    public boolean equals(Object object) {
        if (object == null) { return false; }
        if ((object instanceof Class_Location) == false) { return false; }
        Class_Location other = (Class_Location) object;
        Locator my_locator = this.getLocator(RootContext.getFallbackCallContext());
        if (my_locator == other.locator) { return true; }
        if (my_locator != null && my_locator.equals(other.locator)) { return true; }
        return false;
    }
}
