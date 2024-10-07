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
import com.sphenon.basics.data.*;

import com.sphenon.basics.locating.*;
import com.sphenon.basics.locating.classes.*;

public class Factory_LocationVolatile  {

    public Factory_LocationVolatile (CallContext context) {
    }

    static public Location construct(CallContext context, DataSource<Locator> locator_source) {
        Factory_LocationVolatile factory = new Factory_LocationVolatile(context);
        factory.setLocatorSource(context, locator_source);
        return factory.createLocation(context);
    }

    public Location createLocation(CallContext context) {
        return new Class_LocationVolatile(context, this.locator_source);
    }

    protected DataSource<Locator> locator_source;

    public DataSource<Locator> getLocatorSource (CallContext context) {
        return this.locator_source;
    }

    public void setLocatorSource (CallContext context, DataSource<Locator> locator) {
        this.locator_source = locator_source;
    }

}
