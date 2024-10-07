package com.sphenon.basics.locating.locators;

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
import com.sphenon.basics.system.*;

import com.sphenon.basics.locating.*;
import com.sphenon.basics.locating.returncodes.*;

import static com.sphenon.basics.system.StringUtilities.isEmpty;

import java.util.List;

public class LocatorSSH extends Locator {

    public LocatorSSH (CallContext context, String text_locator_value, Locator sub_locator, String locator_class_parameter_string) {
        super(context, text_locator_value, sub_locator, locator_class_parameter_string);
    }

    static protected LocatorParserState[] locator_parser_state;
        
    protected LocatorParserState[] getParserStates(CallContext context) {
        if (locator_parser_state == null) {
            locator_parser_state = new LocatorParserState[] {
                new LocatorParserState(context, "host"    , "host::String:1", false, true, null),
                new LocatorParserState(context, "port"    , "port::String:2:[0-9]*,user::String:3", false, true, null),
                new LocatorParserState(context, "user"    , "user::String:3", false, true, null),
                new LocatorParserState(context, null      , null            , true , true, Object.class)
            };
        }
        return locator_parser_state;
    }

    protected Object retrieveLocalTarget(CallContext context) throws InvalidLocator {
        CustomaryContext.create((Context)context).throwAssertionProvedFalse(context, this.getClass().getName().replaceFirst(".*\\.","") + " should not be resolved with this method");
        throw (ExceptionAssertionProvedFalse) null; // compiler insists        
    }

    public String getHost(CallContext context) {
        List<String> parts = getLocatorParts(context, "host", 1, 1);
        return parts.get(0);
    }

    public String getPort(CallContext context) {
        List<String> parts = getLocatorParts(context, "port", 0, 1);
        return parts.size() == 0 ? "22" : parts.get(0);
    }

    public String getUser(CallContext context) {
        List<String> parts = getLocatorParts(context, "user", 0, 1);
        return parts.size() == 0 ? null : parts.get(0);
    }
}
