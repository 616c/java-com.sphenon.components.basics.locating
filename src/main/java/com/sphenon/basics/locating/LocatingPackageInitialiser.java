package com.sphenon.basics.locating;

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
import com.sphenon.basics.context.classes.*;
import com.sphenon.basics.configuration.*;
import com.sphenon.basics.message.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.expression.*;
import com.sphenon.basics.metadata.*;
import com.sphenon.basics.variatives.*;

public class LocatingPackageInitialiser {
    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }
    static { notification_level = NotificationLocationContext.getLevel(RootContext.getInitialisationContext(), "com.sphenon.basics.locating.LocatingPackageInitialiser"); };

    static protected boolean initialised = false;

    static {
        initialise(RootContext.getRootContext());
    }

    static public synchronized void initialise (CallContext context) {
        
        if (initialised == false) {
            initialised = true;

            ExpressionEvaluatorRegistry.registerDynamicStringEvaluator(context, new com.sphenon.basics.locating.DynamicStringProcessor_Locating(context));
            ExpressionEvaluatorRegistry.registerExpressionEvaluator(context, new com.sphenon.basics.locating.ExpressionEvaluator_Locating(context));

            if (getConfiguration(context).get(context, "SaveLocatorCacheOnExit", false)) {
                Locator.saveCacheOnExit(context);
            }
        }
    }

    static protected Configuration config;
    static public Configuration getConfiguration (CallContext context) {
        if (config == null) {
            config = Configuration.create(context, "com.sphenon.basics.locating");
        }
        return config;
    }
}
