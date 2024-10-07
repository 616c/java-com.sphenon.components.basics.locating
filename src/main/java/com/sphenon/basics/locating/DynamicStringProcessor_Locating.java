package com.sphenon.basics.locating;

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
import com.sphenon.basics.notification.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.data.*;
import com.sphenon.basics.operations.*;

import com.sphenon.basics.expression.*;
import com.sphenon.basics.expression.classes.*;
import com.sphenon.basics.expression.returncodes.*;

import com.sphenon.basics.locating.returncodes.*;

public class DynamicStringProcessor_Locating implements ExpressionEvaluator {

    public DynamicStringProcessor_Locating (CallContext context) {
        this.result_attribute = new Class_ActivityAttribute(context, "Result", "Object", "-", "*");
        this.activity_interface = new Class_ActivityInterface(context);
        this.activity_interface.addAttribute(context, this.result_attribute);
    }

    protected Class_ActivityInterface activity_interface;
    protected ActivityAttribute result_attribute;

    public String[] getIds(CallContext context) {
        return new String[] { "l", "locator", "oorl" };
    }

    public Object evaluate(CallContext context, String string, Scope scope, DataSink<Execution> execution_sink) {
        Object result;
        try {
            Scope.Result lbo = scope == null ? null : scope.tryGetWithNull(context, "locator_base_object");
            if (lbo != null) {
                result = (Locator.resolve(context, string, lbo.value));
            } else {
                result = (Locator.resolve(context, string));
            }
        } catch (InvalidLocator il) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, il, "Dynamic string '%(string)' does not represent a valid locator", "string", string);
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
        return ContextAware.ToString.convert(context, result);
    }

    public ActivityClass parse(CallContext context, ExpressionSource expression_source) throws EvaluationFailure {
        return new ActivityClass_ExpressionEvaluator(context, this, expression_source, this.activity_interface, this.result_attribute);
    }
}
