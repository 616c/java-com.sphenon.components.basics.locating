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

public class Factory_Locator  {

    public Factory_Locator (CallContext context) {
    }

    static public Locator construct(CallContext context, String text_locator) throws ValidationFailure {
        Factory_Locator factory = new Factory_Locator(context);
        factory.setTextLocator (context, text_locator);
        return factory.createLocator(context);
    }

    static public Locator tryConstruct(CallContext context, String text_locator) {
        Factory_Locator factory = new Factory_Locator(context);
        factory.setTextLocator (context, text_locator);
        return factory.tryCreateLocator(context);
    }

    static public Locator mustConstruct(CallContext context, String text_locator) {
        Factory_Locator factory = new Factory_Locator(context);
        factory.setTextLocator (context, text_locator);
        return factory.mustCreateLocator(context);
    }

    static public Locator construct(CallContext context, String text_locator, String default_type) throws ValidationFailure {
        Factory_Locator factory = new Factory_Locator(context);
        factory.setTextLocator (context, text_locator);
        factory.setDefaultType (context, default_type);
        return factory.createLocator(context);
    }

    static public Locator tryConstruct(CallContext context, String text_locator, String default_type) {
        Factory_Locator factory = new Factory_Locator(context);
        factory.setTextLocator (context, text_locator);
        factory.setDefaultType (context, default_type);
        return factory.tryCreateLocator(context);
    }

    static public Locator mustConstruct(CallContext context, String text_locator, String default_type) {
        Factory_Locator factory = new Factory_Locator(context);
        factory.setTextLocator (context, text_locator);
        factory.setDefaultType (context, default_type);
        return factory.mustCreateLocator(context);
    }

    static public Locator construct(CallContext context, String text_locator, boolean is_generic) throws ValidationFailure {
        Factory_Locator factory = new Factory_Locator(context);
        factory.setTextLocator (context, text_locator);
        factory.setIsGeneric (context, is_generic);
        return factory.createLocator(context);
    }

    static public Locator tryConstruct(CallContext context, String text_locator, boolean is_generic) {
        Factory_Locator factory = new Factory_Locator(context);
        factory.setTextLocator (context, text_locator);
        factory.setIsGeneric (context, is_generic);
        return factory.tryCreateLocator(context);
    }

    static public Locator mustConstruct(CallContext context, String text_locator, boolean is_generic) {
        Factory_Locator factory = new Factory_Locator(context);
        factory.setTextLocator (context, text_locator);
        factory.setIsGeneric (context, is_generic);
        return factory.mustCreateLocator(context);
    }

    static public Locator construct(CallContext context, String text_locator, Object base_object) throws ValidationFailure {
        Factory_Locator factory = new Factory_Locator(context);
        factory.setTextLocator (context, text_locator);
        factory.setBaseObject (context, base_object);
        return factory.createLocator(context);
    }

    static public Locator tryConstruct(CallContext context, String text_locator, Object base_object) {
        Factory_Locator factory = new Factory_Locator(context);
        factory.setTextLocator (context, text_locator);
        factory.setBaseObject (context, base_object);
        return factory.tryCreateLocator(context);
    }

    static public Locator mustConstruct(CallContext context, String text_locator, Object base_object) {
        Factory_Locator factory = new Factory_Locator(context);
        factory.setTextLocator (context, text_locator);
        factory.setBaseObject (context, base_object);
        return factory.mustCreateLocator(context);
    }

    public Locator createLocator(CallContext context) throws ValidationFailure {
        try {
            return doCreateLocator(context);
        } catch (InvalidLocator il) {
            ValidationFailure.createAndThrow(context, il, "Cannot create locator");
            throw (ValidationFailure) null;
        }
    }

    public Locator tryCreateLocator(CallContext context) {
        try {
            return doCreateLocator(context);
        } catch (InvalidLocator il) {
            return null;
        }
    }

    public Locator mustCreateLocator(CallContext context) {
        try {
            return doCreateLocator(context);
        } catch (InvalidLocator il) {
            CustomaryContext.create((Context)context).throwAssertionProvedFalse(context, il, "Cannot create locator");
            throw (ExceptionAssertionProvedFalse) null; // compiler insists
        }
    }

    protected Locator doCreateLocator(CallContext context) throws InvalidLocator {
        if (this.prototype_locator == null && this.text_locator == null) {
            InvalidLocator.createAndThrow(context, "Factory_Locator needs at least a PrototypeLocator or a TextLocator");
        }
        if (this.prototype_locator != null && this.text_locator != null) {
            InvalidLocator.createAndThrow(context, "Factory_Locator needs either a PrototypeLocator or a TextLocator, but not both");
        }
        Locator locator = prototype_locator != null ?
                            prototype_locator.clone()
                          : this.is_generic ?
                              Locator.createGenericLocator(context, this.text_locator)
                            : Locator.createLocator(context, this.text_locator, this.default_type);
        if (this.base_object != null) {
            locator.setBaseObject(context, this.base_object);
        }
        if (this.sub_locator != null) {
            locator.appendLocator(context, Locator.createLocator(context, this.sub_locator));
        }
        return locator;
    }

    protected String text_locator;

    public String getTextLocator (CallContext context) {
        return this.text_locator;
    }

    public String defaultTextLocator (CallContext context) {
        return null;
    }

    public void setTextLocator (CallContext context, String text_locator) {
        this.text_locator = text_locator;
    }

    protected Locator prototype_locator;

    public Locator getPrototypeLocator (CallContext context) {
        return this.prototype_locator;
    }

    public Locator defaultPrototypeLocator (CallContext context) {
        return null;
    }

    public void setPrototypeLocator (CallContext context, Locator prototype_locator) {
        this.prototype_locator = prototype_locator;
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

    protected boolean is_generic;

    public boolean getIsGeneric (CallContext context) {
        return this.is_generic;
    }

    public boolean defaultIsGeneric (CallContext context) {
        return false;
    }

    public void setIsGeneric (CallContext context, boolean is_generic) {
        this.is_generic = is_generic;
    }
}
