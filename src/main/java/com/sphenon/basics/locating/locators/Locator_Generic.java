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

import com.sphenon.basics.locating.*;
import com.sphenon.basics.locating.returncodes.*;

public class Locator_Generic extends Locator {

    public Locator_Generic (CallContext context, String text_locator_value, Locator sub_locator, String locator_class_id, String locator_class_parameter_string) {
        super(context, text_locator_value, sub_locator, locator_class_id, locator_class_parameter_string);
    }

    static protected LocatorParserState[] locator_parser_state;
        
    protected LocatorParserState[] getParserStates(CallContext context) {
        if (locator_parser_state == null) {
            locator_parser_state = new LocatorParserState[] {
                new LocatorParserState(context, "id", ".*::String:0", false, true, Object.class)
            };
        }
        return locator_parser_state;
    }

    protected Object retrieveLocalTarget(CallContext context) throws InvalidLocator {
        CustomaryContext.create((Context)context).throwAssertionProvedFalse(context, "Locator_Generic should not be resolved with this method");
        throw (ExceptionAssertionProvedFalse) null; // compiler insists        
    }
}
