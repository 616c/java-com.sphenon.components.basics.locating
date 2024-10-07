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

public class Factory_Location_ByTextLocator  {

    public Factory_Location_ByTextLocator (CallContext context) {
    }

    public Location createLocation(CallContext context) throws ValidationFailure {
        try {
            Locator locator = Locator.createLocator(context, this.text_locator, this.default_type);
            locator.setBaseObject(context, this.base_object);
            return new Class_Location(context, locator);
        } catch (InvalidLocator il) {
            ValidationFailure.createAndThrow(context, il, "Cannot create location");
            throw (ValidationFailure) null;
        }
    }

    public Location tryCreateLocation(CallContext context) {
        try {
            Locator locator = Locator.createLocator(context, this.text_locator, this.default_type);
            locator.setBaseObject(context, this.base_object);
            return new Class_Location(context, locator);
        } catch (InvalidLocator il) {
            return null;
        }
    }

    public Location mustCreateLocation(CallContext context) {
        try {
            Locator locator = Locator.createLocator(context, this.text_locator, this.default_type);
            locator.setBaseObject(context, this.base_object);
            return new Class_Location(context, locator);
        } catch (InvalidLocator il) {
            CustomaryContext.create((Context)context).throwAssertionProvedFalse(context, il, "Cannot create location");
            throw (ExceptionAssertionProvedFalse) null; // compiler insists
        }
    }

    protected String text_locator;

    public String getTextLocator (CallContext context) {
        return this.text_locator;
    }

    public void setTextLocator (CallContext context, String text_locator) {
        this.text_locator = text_locator;
    }

    protected String default_type;

    public String getDefaultType (CallContext context) {
        return this.default_type;
    }

    public String defaultDefaultType (CallContext context) {
        return null;
    }

    public void setDefaultType (CallContext context, String default_type) {
        this.default_type = default_type;
    }

    protected Object base_object;

    public Object getBaseObject (CallContext context) {
        return this.base_object;
    }

    public Object defaultBaseObject (CallContext context) {
        return null;
    }

    public void setBaseObject (CallContext context, Object base_object) {
        this.base_object = base_object;
    }
}
