package com.sphenon.basics.locating.retrievers;

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
import com.sphenon.basics.retriever.returncodes.*;

import com.sphenon.basics.locating.*;
import com.sphenon.basics.locating.classes.*;
import com.sphenon.basics.locating.returncodes.*;

public class RetrieverByTextLocator {

    public Object retrieveObject (CallContext context) throws RetrievalFailure {
        try {
            return Locator.createLocator(context, this.text_locator, this.default_type).retrieveTarget(context, this.base);
        } catch (InvalidLocator il) {
            RetrievalFailure.createAndThrow(context, il, "Could not retrieve object, locator is invalid");
            throw (RetrievalFailure) null; // compiler insists
        }
    }

    protected String text_locator;

    public String getTextLocator (CallContext context) {
        return this.text_locator;
    }

    public void setTextLocator (CallContext context, String text_locator) {
        this.text_locator = text_locator;
    }

    protected Object base;

    public Object getBase (CallContext context) {
        return this.base;
    }

    public Object defaultBase (CallContext context) {
        return null;
    }

    public void setBase (CallContext context, Object base) {
        this.base = base;
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
}
