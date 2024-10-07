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
import com.sphenon.basics.data.*;
import com.sphenon.basics.expression.*;

import com.sphenon.basics.locating.*;
import com.sphenon.basics.locating.factories.*;
import com.sphenon.basics.locating.tplinst.*;
import com.sphenon.basics.locating.returncodes.*;

public class DataSource_Locator_DynamicString implements DataSource<Locator> {

    public DataSource_Locator_DynamicString(CallContext context) {
    }

    public Locator getObject(CallContext context) {
        String text_locator = new DynamicString(context, this.text_locator_template).get(context, this.scope);
        Factory_Locator factory = new Factory_Locator(context);
        factory.setTextLocator (context, text_locator);
        factory.setDefaultType (context, this.default_type);
        factory.setBaseObject (context, this.base_object);
        return factory.mustCreateLocator(context);
    }

    public Locator get(CallContext context) {
        return this.getObject(context);
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
