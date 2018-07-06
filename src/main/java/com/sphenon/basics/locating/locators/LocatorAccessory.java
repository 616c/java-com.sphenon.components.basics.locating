package com.sphenon.basics.locating.locators;

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
import com.sphenon.basics.validation.returncodes.*;
import com.sphenon.basics.metadata.*;
import com.sphenon.basics.metadata.tplinst.*;
import com.sphenon.basics.metadata.returncodes.*;
import com.sphenon.basics.accessory.*;
import com.sphenon.basics.locating.*;
import com.sphenon.basics.locating.factories.*;
import com.sphenon.basics.locating.returncodes.*;

import java.util.Vector;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

public class LocatorAccessory extends Locator {

    public LocatorAccessory (CallContext context, String text_locator_value, Locator sub_locator, String locator_class_parameter_string) {
        super(context, text_locator_value, sub_locator, locator_class_parameter_string);
    }


    /* Parser States -------------------------------------------------------------------- */

    static protected LocatorParserState[] locator_parser_state;
        
    protected LocatorParserState[] getParserStates(CallContext context) {
        if (locator_parser_state == null) {
            locator_parser_state = new LocatorParserState[] {
                new LocatorParserState(context, "class", "class::String:1", false, true, null),
                new LocatorParserState(context, null   , null             , true , true, Object.class)
            };
        }
        return locator_parser_state;
    }

    /* Base Acceptors ------------------------------------------------------------------- */

    static protected Vector<LocatorBaseAcceptor> locator_base_acceptors;

    static protected Vector<LocatorBaseAcceptor> initBaseAcceptors(CallContext context) {
        if (locator_base_acceptors == null) {
            locator_base_acceptors = new Vector<LocatorBaseAcceptor>();
            locator_base_acceptors.add(new LocatorBaseAcceptor(context, CoreObject.class));
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

    public String getTargetVariableName(CallContext context) {
        return "accessory";
    }

    protected Object retrieveLocalTarget(CallContext context) throws InvalidLocator {
        Object base = lookupBaseObject(context, true);

        if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { CustomaryContext.create((Context)context).sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Retrieving local target of Accessory Locator '%(textlocator)', base object '%(base)'...", "textlocator", this.text_locator_value, "base", base); }

        LocatorStep[] steps = getLocatorSteps(context);

        String accessory_class = steps[0].getValue(context);

        CoreObject ume = (CoreObject) base;

        try {
            Type accessory_type = TypeManager.get(context, accessory_class);
            Set_Object_ so = ume.getAccessorys(context).tryGet(context, accessory_type);

            if (so == null || so.getSize(context) == 0) {
                if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { CustomaryContext.create((Context)context).sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Retrieved no or empty set"); }
                return null;
            }
            if (so.getSize(context) == 1) {
                Object result = so.getNavigator(context).tryGetCurrent(context);
                if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { CustomaryContext.create((Context)context).sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Retrieved one accessory: '%(result)'", "result", result); }
                return result;
            }
            if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { CustomaryContext.create((Context)context).sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Retrieved #%(count) accessories", "count", so.getSize(context)); }
            return so;

        } catch (NoSuchClass nsc) {
            CustomaryContext.create((Context)context).throwConfigurationError(context, nsc, "No such accessory class '%(accessory_class)' (referenced in locator '%(locator)')", "accessory_class", accessory_class, "locator", this.text_locator_value);
            throw (ExceptionConfigurationError) null; // compiler insists
        }
    }
}
