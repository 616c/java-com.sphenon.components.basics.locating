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
import com.sphenon.basics.context.classes.*;
import com.sphenon.basics.message.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.configuration.*;
import com.sphenon.basics.expression.*;
import com.sphenon.basics.data.*;
import com.sphenon.basics.locating.*;
import com.sphenon.basics.locating.returncodes.*;

import java.lang.reflect.*;

import java.util.Vector;
import java.util.Map;
import java.util.HashMap;

/**
   {@EntitySecurityClass User}

   @ignore ==============================================================================================
   @doclet {@Category Overview} {@Audience Development} {@Maturity Final} {@SecurityClass User}

   A locator for not practically locatable entities, like unreferenced, temporary in-memory items,
   in cases where a locator, who might be used for identification (e.g. in caches) is required.
   The hash value used here for such locators then shall be sufficiently unique, e.g. a UUID.
 */
public class LocatorEphemeral extends Locator {

    public LocatorEphemeral (CallContext context, String text_locator_value, Locator sub_locator, String locator_class_parameter_string) {
        super(context, text_locator_value, sub_locator, locator_class_parameter_string);
    }

    /* Parser States -------------------------------------------------------------------- */

    static protected LocatorParserState[] locator_parser_state;
        
    protected LocatorParserState[] getParserStates(CallContext context) {
        if (locator_parser_state == null) {
            locator_parser_state = new LocatorParserState[] {
                new LocatorParserState(context, "hash", "hash::String:0", false, true, Object.class)
            };
        }
        return locator_parser_state;
    }
}
