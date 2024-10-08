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
import com.sphenon.basics.locating.*;
import com.sphenon.basics.locating.returncodes.*;

import java.lang.reflect.*;

import java.util.Vector;

public class LocatorEnvironment extends Locator {
    static protected Configuration config;
    static { config = Configuration.create(RootContext.getInitialisationContext(), "com.sphenon.basics.locating.locators.LocatorEnvironment"); };

    public LocatorEnvironment (CallContext context, String text_locator_value, Locator sub_locator, String locator_class_parameter_string) {
        super(context, text_locator_value, sub_locator, locator_class_parameter_string);
    }

    /* Parser States -------------------------------------------------------------------- */

    static protected LocatorParserState[] locator_parser_state;
        
    protected LocatorParserState[] getParserStates(CallContext context) {
        if (locator_parser_state == null) {
            locator_parser_state = new LocatorParserState[] {
                new LocatorParserState(context, "name", "name::String:1", false, true, null),
                new LocatorParserState(context, null  , null            , true , true, Object.class)
            };
        }
        return locator_parser_state;
    }

    /* Base Acceptors ------------------------------------------------------------------- */

    static protected Vector<LocatorBaseAcceptor> locator_base_acceptors;

    static protected Vector<LocatorBaseAcceptor> initBaseAcceptors(CallContext context) {
        if (locator_base_acceptors == null) {
            locator_base_acceptors = new Vector<LocatorBaseAcceptor>();
        }
        return locator_base_acceptors;
    }

    protected Vector<LocatorBaseAcceptor> getBaseAcceptors(CallContext context) {
        return initBaseAcceptors(context);
    }

    static public void addBaseAcceptor(CallContext context, LocatorBaseAcceptor base_acceptor) {
        initBaseAcceptors(context).add(base_acceptor);
    }
    
    /* ---------------------------------------------------------------------------------- */

    protected Object retrieveLocalTarget(CallContext context) throws InvalidLocator {
        LocatingContext lc = LocatingContext.get((Context) context);
        
        LocatorStep[] steps = getLocatorSteps(context);

        String name = steps[0].getValue(context);
        Object value = System.getenv(name);

        if (value == null) {
            InvalidLocator.createAndThrow(context, "While resolving environment locator '%(locator)', no object named '%(name)' was found within environment", "locator", this.getTextLocatorValue(context), "name", name);
            throw (InvalidLocator) null; // compiler insists
        }

        return value;
    }
}
