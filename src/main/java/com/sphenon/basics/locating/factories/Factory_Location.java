package com.sphenon.basics.locating.factories;

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

import com.sphenon.basics.locating.*;
import com.sphenon.basics.locating.classes.*;

public class Factory_Location  {

    public Factory_Location (CallContext context) {
    }

    static public Location construct(CallContext context, Locator locator) {
        Factory_Location factory = new Factory_Location(context);
        factory.setLocator(context, locator);
        return factory.createLocation(context);
    }

    public Location createLocation(CallContext context) {
        return new Class_Location(context, this.locator);
    }

    protected Locator locator;

    public Locator getLocator (CallContext context) {
        return this.locator;
    }

    public void setLocator (CallContext context, Locator locator) {
        this.locator = locator;
    }

}
