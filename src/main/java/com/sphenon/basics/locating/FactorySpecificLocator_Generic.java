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
import com.sphenon.basics.exception.*;
import com.sphenon.basics.customary.*;

import java.lang.reflect.*;

public class FactorySpecificLocator_Generic implements FactorySpecificLocator {

    protected Constructor<Locator> constructor;
    protected String locator_class_name;

    public FactorySpecificLocator_Generic(CallContext context, Constructor<Locator> constructor, String locator_class_name) {
        this.constructor = constructor;
        this.locator_class_name = locator_class_name;
    }

    public Locator create(CallContext context, String text_locator_value, Locator sub_locator, String locator_class_parameter_string) {
        try {
            return constructor.newInstance(context, text_locator_value, sub_locator, locator_class_parameter_string);
        } catch (InstantiationException ie) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, ie, "Class '%(class)' cannot be instantiated", "class", locator_class_name);
            throw (ExceptionPreConditionViolation) null; // compiler insists
        } catch (IllegalAccessException iae) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, iae, "Class '%(class)' cannot be instantiated", "class", locator_class_name);
            throw (ExceptionPreConditionViolation) null; // compiler insists
        } catch (InvocationTargetException ite) {
            if (ite.getTargetException() instanceof java.lang.RuntimeException) {
                throw (java.lang.RuntimeException) ite.getTargetException();
            }
            if (ite.getTargetException() instanceof java.lang.Error) {
                throw (java.lang.Error) ite.getTargetException();
            }
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, ite.getTargetException(), "Class '%(class)' cannot be instantiated", "class", locator_class_name);
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
    }
}
