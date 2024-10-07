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
import com.sphenon.basics.expression.*;
import com.sphenon.basics.validation.returncodes.*;

import com.sphenon.basics.locating.*;
import com.sphenon.basics.locating.classes.*;
import com.sphenon.basics.locating.returncodes.*;

public class Factory_LocationVolatile_ByTextLocatorTemplate  {

    public Factory_LocationVolatile_ByTextLocatorTemplate (CallContext context) {
    }

    public Location createLocation(CallContext context) throws ValidationFailure {
        return this.tryCreateLocation(context);
    }

    public Location tryCreateLocation(CallContext context) {
        DataSource_Locator_DynamicString locator_source = new DataSource_Locator_DynamicString(context);
        locator_source.setTextLocatorTemplate(context, this.text_locator_template);
        locator_source.setDefaultType(context, this.default_type);
        locator_source.setBaseObject(context, this.base_object);
        locator_source.setScope(context, this.scope);
        return new Class_LocationVolatile(context, locator_source);
    }

    public Location mustCreateLocation(CallContext context) {
        return this.tryCreateLocation(context);
    }

    protected String text_locator_template;

    public String getTextLocatorTemplate (CallContext context) {
        return this.text_locator_template;
    }

    public void setTextLocatorTemplate (CallContext context, String text_locator_template) {
        this.text_locator_template = text_locator_template;
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

    protected Scope scope;

    public Scope getScope (CallContext context) {
        return this.scope;
    }

    public Scope defaultScope (CallContext context) {
        return null;
    }

    public void setScope (CallContext context, Scope scope) {
        this.scope = scope;
    }
}
