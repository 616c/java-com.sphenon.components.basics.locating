package com.sphenon.basics.locating.operations;

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
import com.sphenon.basics.monitoring.*;
import com.sphenon.basics.operations.*;
import com.sphenon.basics.operations.classes.*;
import com.sphenon.basics.data.*;

import com.sphenon.basics.locating.*;
import com.sphenon.basics.locating.returncodes.*;

public class Operation_RetrieveLocatorTarget implements Operation {
    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }
    static { notification_level = NotificationLocationContext.getLevel(RootContext.getInitialisationContext(), "com.sphenon.basics.locating.operations.Operation_RetrieveLocatorTarget"); };

    public Operation_RetrieveLocatorTarget (CallContext context) {
    }

    protected String text_locator;

    public String getTextLocator (CallContext context) {
        return this.text_locator;
    }

    public void setTextLocator (CallContext context, String text_locator) {
        this.text_locator = text_locator;
    }

    public Execution execute (CallContext context) {
        return execute(context, null);
    }

    protected Object target;

    public Execution execute (CallContext context, DataSink<Execution> execution_sink) {
        Execution execution = null;

        try {
            this.target = null;

            Locator locator = Locator.createLocator(context, this.getTextLocator(context));

            target = locator.retrieveTarget(context);

            if ((this.notification_level & Notifier.CHECKPOINT) != 0) {
                CustomaryContext cc = CustomaryContext.create((Context)context);
                cc.sendTrace(context, Notifier.CHECKPOINT, "Via locator '%(locator)', retrieved target '%(target)'", "locator", this.getTextLocator(context), "target", target);
            }

            execution = Class_Execution.createExecutionSuccess(context);
        } catch (InvalidLocator il) {
            execution = Class_Execution.createExecutionFailure(context, il);
        }

        if (execution_sink != null) { execution_sink.set(context, execution); }
        return execution;
    }

    public Object getTarget (CallContext context) {
        return this.target;
    }
}
