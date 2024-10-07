package com.sphenon.basics.locating.factories;

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
import com.sphenon.basics.validation.returncodes.*;

import com.sphenon.basics.locating.*;
import com.sphenon.basics.locating.classes.*;
import com.sphenon.basics.locating.returncodes.*;

public class Factory_Location_ByPrototype  {

    public Factory_Location_ByPrototype (CallContext context) {
    }

    static public Location mustConstruct(CallContext context, Location prototype_location, String sub_locator) {
        try {
            return construct(context, prototype_location, sub_locator);
        } catch (ValidationFailure vf) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, vf, "Creation of location by prototype failed");
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
    }

    static public Location construct(CallContext context, Location prototype_location, String sub_locator) throws ValidationFailure {
        Factory_Location_ByPrototype factory = new Factory_Location_ByPrototype(context);
        factory.setPrototypeLocation (context, prototype_location);
        factory.setSubLocator (context, sub_locator);
        return factory.createLocation(context);
    }

    public Location createLocation(CallContext context) throws ValidationFailure {
        try {
            return this.doCreateLocation(context);
        } catch (InvalidLocator il) {
            ValidationFailure.createAndThrow(context, il, "Cannot create location");
            throw (ValidationFailure) null;
        }
    }

    public Location tryCreateLocation(CallContext context) {
        try {
            return this.doCreateLocation(context);
        } catch (InvalidLocator il) {
            return null;
        }
    }

    public Location mustCreateLocation(CallContext context) {
        try {
            return this.doCreateLocation(context);
        } catch (InvalidLocator il) {
            CustomaryContext.create((Context)context).throwAssertionProvedFalse(context, il, "Cannot create location");
            throw (ExceptionAssertionProvedFalse) null; // compiler insists
        }
    }

    protected Location doCreateLocation(CallContext context) throws InvalidLocator {
        Location location = prototype_location.clone();

        if (this.sub_locator != null) {
            location.appendLocator(context, Locator.createLocator(context, this.sub_locator));
        }

        return location;
    }

    protected Location prototype_location;

    public Location getPrototypeLocation (CallContext context) {
        return this.prototype_location;
    }

    public void setPrototypeLocation (CallContext context, Location prototype_location) {
        this.prototype_location = prototype_location;
    }

    protected String sub_locator;

    public String getSubLocator (CallContext context) {
        return this.sub_locator;
    }

    public String defaultSubLocator (CallContext context) {
        return null;
    }

    public void setSubLocator (CallContext context, String sub_locator) {
        this.sub_locator = sub_locator;
    }
}
