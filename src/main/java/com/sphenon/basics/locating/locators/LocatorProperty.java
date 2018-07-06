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

public class LocatorProperty extends Locator {
    static protected Configuration config;
    static { config = Configuration.create(RootContext.getInitialisationContext(), "com.sphenon.basics.locating.locators.LocatorProperty"); };

    public LocatorProperty (CallContext context, String text_locator_value, Locator sub_locator, String locator_class_parameter_string) {
        super(context, text_locator_value, sub_locator, locator_class_parameter_string);
    }

    /* Parser States -------------------------------------------------------------------- */

    static protected LocatorParserState[] locator_parser_state;
        
    protected LocatorParserState[] getParserStates(CallContext context) {
        if (locator_parser_state == null) {
            locator_parser_state = new LocatorParserState[] {
                new LocatorParserState(context, "property", "property::String:0", false, true, Object.class)
            };
        }
        return locator_parser_state;
    }

    /* Base Acceptors ------------------------------------------------------------------- */

    static protected Vector<LocatorBaseAcceptor> locator_base_acceptors;

    static protected Vector<LocatorBaseAcceptor> initBaseAcceptors(CallContext context) {
        if (locator_base_acceptors == null) {
            locator_base_acceptors = new Vector<LocatorBaseAcceptor>();
            locator_base_acceptors.add(new LocatorBaseAcceptor(context, Object.class));
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

    static final protected String init_code = "Object o = base_object;\nwhile (o instanceof DataSource) { o = ((DataSource) o).getObject(context); }\n";
    static final protected String ds_code = "while (o instanceof DataSource) { o = ((DataSource) o).getObject(context); }\n";

    protected Object retrieveLocalTarget(CallContext context, boolean create_java_code) throws InvalidLocator {
        Object current = lookupBaseObject(context, true);

        LocatorStep[] steps = getLocatorSteps(context);

        StringBuilder jcb = null;
        if (create_java_code) {
            this.javacode = new String[5];
            jcb = new StringBuilder();
            this.javacode[3] = init_code;
            this.javacode[4] = "o";
        }

        Integer[] lsia = new Integer[1];
        for (Integer lsi=0; lsi<steps.length; lsi++) {
            LocatorStep step = steps[lsi];
            if (current == null) {
                LocatorTargetNotFound.createAndThrow(context, "Intermediate node in property locator '%(locator)' before '%(value)' is null", "locator", this.getTextLocatorValue(context), "value", step.getValue(context));
                throw (InvalidLocator) null; // compiler insists
            }
            lsia[0] = lsi;
            current = getProperty(context, current, step, steps, lsia, jcb, lsi == 0 ? this.javacode : null);
            lsi = lsia[0];
        }

        if (create_java_code) {
            javacode[0] = jcb.toString();
            javacode[1] = "o";
        }

        return current;
    }

    static protected RegularExpression member_re = new RegularExpression("^[A-Za-z_][A-Za-z0-9_]*$");
    static protected RegularExpression index_re = new RegularExpression("^[0-9]+$");

    protected Object getProperty(CallContext context, Object object, LocatorStep step, LocatorStep[] steps, Integer[] lsia, StringBuilder jcb, String[] code) throws InvalidLocator {
        String value = step.getValue(context);

        while (object instanceof DataSource) {
            object = ((DataSource)object).getObject(context);
        }

        if (value == null || value.isEmpty()) {
            return object;
        }

        Class oclass = object.getClass();

        if (oclass.isAnonymousClass()) {
            if (    oclass.getSuperclass() == java.lang.Object.class
                 && oclass.getInterfaces().length == 1
               ) {
                oclass = oclass.getInterfaces()[0];
            } else
            if (    oclass.getSuperclass() != java.lang.Object.class
                 && oclass.getInterfaces().length == 0
               ) {
                oclass = oclass.getSuperclass();
            }
        }

        try {

            Map<String,Integer> cached_class_indices = getCachedClassIndices(context, oclass);
            Integer index = cached_class_indices.get(value);

            int start_index = index == null ? 0 : index;

            boolean is_member   = false;
            boolean is_index    = false;
            boolean is_member_1 = false;
            boolean is_index_1  = false;

            if (start_index == 0) {
                is_member   = member_re.matches(context, value);
                is_index    = index_re.matches(context, value);
            } else {
                cached_class_indices = null;
            }

            start_index =    start_index != 0 ?  start_index
                           : is_member        ?   1
                           : is_index         ?  41
                           :                     81;

            String value_1 = null;

            if (start_index >= 10 && start_index < 30) {
                if (lsia[0] < steps.length - 1) {
                    value_1 = steps[lsia[0]+1].getValue(context); lsia[0]++;
                    is_member_1 = member_re.matches(context, value_1);
                    is_index_1  = index_re.matches(context, value_1);
                }
            }

            switch (start_index) {
                case  0: // default, try them all

                case  1: try { return invoke(context, method(context, oclass, jcb, code, cached_class_indices, value,  1, "tryGet" + value, CallContext.class), object, jcb, context); } catch (NoSuchMethodException msme) { }
                case  2: try { return invoke(context, method(context, oclass, jcb, code, cached_class_indices, value,  2, "tryGet" + value), object, jcb); } catch (NoSuchMethodException msme) { }
                case  3: try { return invoke(context, method(context, oclass, jcb, code, cached_class_indices, value,  3, "get" + value, CallContext.class), object, jcb, context); } catch (NoSuchMethodException msme) { }
                case  4: try { return invoke(context, method(context, oclass, jcb, code, cached_class_indices, value,  4, "get" + value), object, jcb); } catch (NoSuchMethodException msme) { }
                case  5: try { return invoke(context, method(context, oclass, jcb, code, cached_class_indices, value,  5, "tryGet", CallContext.class, String.class), object, jcb, context, value); } catch (NoSuchMethodException msme) { }
                case  6: try { return invoke(context, method(context, oclass, jcb, code, cached_class_indices, value,  6, "tryGet", String.class), object, jcb, value); } catch (NoSuchMethodException msme) { }
                case  7: try { return invoke(context, method(context, oclass, jcb, code, cached_class_indices, value,  7, "get", CallContext.class, String.class), object, jcb, context, value); } catch (NoSuchMethodException msme) { }
                case  8: try { return invoke(context, method(context, oclass, jcb, code, cached_class_indices, value,  8, "get", String.class), object, jcb, value); } catch (NoSuchMethodException msme) { }
                case  9: if (java.util.Map.class.isAssignableFrom(oclass)) {
                             if (jcb != null) {
                                 if (code != null) { code[2] = "MAP"; } else { jcb.append(ds_code); }
                                 jcb.append("o = ((Map) o).get(\"" + value + "\");");
                             }
                             if (cached_class_indices != null) {
                                 cached_class_indices.put(value, 9);
                             }
                             return ((Map) object).get(value);
                         }

                         if (lsia[0] < steps.length - 1) {
                             value_1 = steps[lsia[0]+1].getValue(context); lsia[0]++;
                             is_member_1 = member_re.matches(context, value_1);
                             is_index_1  = index_re.matches(context, value_1);
                         }

                case 10: if (is_member_1) { try { return invoke(context, method(context, oclass, jcb, code, cached_class_indices, value, 10, "tryGet" + value, CallContext.class, String.class), object, jcb, context, value_1); } catch (NoSuchMethodException msme) { } }
                case 11: if (is_member_1) { try { return invoke(context, method(context, oclass, jcb, code, cached_class_indices, value, 11, "tryGet" + value, String.class), object, jcb, value_1); } catch (NoSuchMethodException msme) { } }
                case 12: if (is_member_1) { try { return invoke(context, method(context, oclass, jcb, code, cached_class_indices, value, 12, "get" + value, CallContext.class, String.class), object, jcb, context, value_1); } catch (NoSuchMethodException msme) { } }
                case 13: if (is_member_1) { try { return invoke(context, method(context, oclass, jcb, code, cached_class_indices, value, 13, "get" + value, String.class), object, jcb, value_1); } catch (NoSuchMethodException msme) { } }

                case 14: if (is_index_1) { try { return invoke(context, method(context, oclass, jcb, code, cached_class_indices, value, 14, "tryGet" + value, CallContext.class, Integer.class), object, jcb, context, Integer.parseInt(value_1)); } catch (NoSuchMethodException msme) { } }
                case 15: if (is_index_1) { try { return invoke(context, method(context, oclass, jcb, code, cached_class_indices, value, 15, "tryGet" + value, CallContext.class, int.class), object, jcb, context, Integer.parseInt(value_1)); } catch (NoSuchMethodException msme) { } }
                case 16: if (is_index_1) { try { return invoke(context, method(context, oclass, jcb, code, cached_class_indices, value, 16, "get" + value, CallContext.class, Integer.class), object, jcb, context, Integer.parseInt(value_1)); } catch (NoSuchMethodException msme) { } }
                case 17: if (is_index_1) { try { return invoke(context, method(context, oclass, jcb, code, cached_class_indices, value, 17, "get" + value, CallContext.class, int.class), object, jcb, context, Integer.parseInt(value_1)); } catch (NoSuchMethodException msme) { } }
                case 18: if (is_index_1) { try { return invoke(context, method(context, oclass, jcb, code, cached_class_indices, value, 18, "tryGet" + value, CallContext.class, Long.class), object, jcb, context, Long.parseLong(value_1)); } catch (NoSuchMethodException msme) { } }
                case 19: if (is_index_1) { try { return invoke(context, method(context, oclass, jcb, code, cached_class_indices, value, 19, "tryGet" + value, CallContext.class, long.class), object, jcb, context, Long.parseLong(value_1)); } catch (NoSuchMethodException msme) { } }
                case 20: if (is_index_1) { try { return invoke(context, method(context, oclass, jcb, code, cached_class_indices, value, 20, "get" + value, CallContext.class, Long.class), object, jcb, context, Long.parseLong(value_1)); } catch (NoSuchMethodException msme) { } }
                case 21: if (is_index_1) { try { return invoke(context, method(context, oclass, jcb, code, cached_class_indices, value, 21, "get" + value, CallContext.class, long.class), object, jcb, context, Long.parseLong(value_1)); } catch (NoSuchMethodException msme) { } }
                case 22: if (is_index_1) { try { return invoke(context, method(context, oclass, jcb, code, cached_class_indices, value, 22, "tryGet" + value, Integer.class), object, jcb, Integer.parseInt(value_1)); } catch (NoSuchMethodException msme) { } }
                case 23: if (is_index_1) { try { return invoke(context, method(context, oclass, jcb, code, cached_class_indices, value, 23, "tryGet" + value, int.class), object, jcb, Integer.parseInt(value_1)); } catch (NoSuchMethodException msme) { } }
                case 24: if (is_index_1) { try { return invoke(context, method(context, oclass, jcb, code, cached_class_indices, value, 24, "get" + value, Integer.class), object, jcb, Integer.parseInt(value_1)); } catch (NoSuchMethodException msme) { } }
                case 25: if (is_index_1) { try { return invoke(context, method(context, oclass, jcb, code, cached_class_indices, value, 25, "get" + value, int.class), object, jcb, Integer.parseInt(value_1)); } catch (NoSuchMethodException msme) { } }
                case 26: if (is_index_1) { try { return invoke(context, method(context, oclass, jcb, code, cached_class_indices, value, 26, "tryGet" + value, Long.class), object, jcb, Long.parseLong(value_1)); } catch (NoSuchMethodException msme) { } }
                case 27: if (is_index_1) { try { return invoke(context, method(context, oclass, jcb, code, cached_class_indices, value, 27, "tryGet" + value, long.class), object, jcb, Long.parseLong(value_1)); } catch (NoSuchMethodException msme) { } }
                case 28: if (is_index_1) { try { return invoke(context, method(context, oclass, jcb, code, cached_class_indices, value, 28, "get" + value, Long.class), object, jcb, Long.parseLong(value_1)); } catch (NoSuchMethodException msme) { } }
                case 29: if (is_index_1) { try { return invoke(context, method(context, oclass, jcb, code, cached_class_indices, value, 29, "get" + value, long.class), object, jcb, Long.parseLong(value_1)); } catch (NoSuchMethodException msme) { } }

                case 30: InvalidLocatorSyntax.createAndThrow(context, "While resolving property locator '%(locator)', no property named '%(value)' was found at class '%(class)'", "locator", this.getTextLocatorValue(context), "value", value, "class", oclass.getName());
                         throw (InvalidLocator) null; // compiler insists

                case 41: if (oclass.isArray()) {
                             if (jcb != null) {
                                 if (code != null) { code[2] = "ARRAY"; } else { jcb.append(ds_code); }
                                 jcb.append("o = java.lang.reflect.Array.get(o, " + value + ");");
                             }
                             if (cached_class_indices != null) {
                                 cached_class_indices.put(value, 41);
                             }
                             return java.lang.reflect.Array.get(object, Integer.parseInt(value));
                         }
                case 42: try { return invoke(context, method(context, oclass, jcb, code, cached_class_indices, value, 42, "tryGet", CallContext.class, Integer.class), object, jcb, context, Integer.parseInt(value)); } catch (NoSuchMethodException msme) { }
                case 43: try { return invoke(context, method(context, oclass, jcb, code, cached_class_indices, value, 43, "tryGet", CallContext.class, int.class), object, jcb, context, Integer.parseInt(value)); } catch (NoSuchMethodException msme) { }
                case 44: try { return invoke(context, method(context, oclass, jcb, code, cached_class_indices, value, 44, "get", CallContext.class, Integer.class), object, jcb, context, Integer.parseInt(value)); } catch (NoSuchMethodException msme) { }
                case 45: try { return invoke(context, method(context, oclass, jcb, code, cached_class_indices, value, 45, "get", CallContext.class, int.class), object, jcb, context, Integer.parseInt(value)); } catch (NoSuchMethodException msme) { }
                case 46: try { return invoke(context, method(context, oclass, jcb, code, cached_class_indices, value, 46, "tryGet", CallContext.class, Long.class), object, jcb, context, Long.parseLong(value)); } catch (NoSuchMethodException msme) { }
                case 47: try { return invoke(context, method(context, oclass, jcb, code, cached_class_indices, value, 47, "tryGet", CallContext.class, long.class), object, jcb, context, Long.parseLong(value)); } catch (NoSuchMethodException msme) { }
                case 48: try { return invoke(context, method(context, oclass, jcb, code, cached_class_indices, value, 48, "get", CallContext.class, Long.class), object, jcb, context, Long.parseLong(value)); } catch (NoSuchMethodException msme) { }
                case 49: try { return invoke(context, method(context, oclass, jcb, code, cached_class_indices, value, 49, "get", CallContext.class, long.class), object, jcb, context, Long.parseLong(value)); } catch (NoSuchMethodException msme) { }
                case 50: try { return invoke(context, method(context, oclass, jcb, code, cached_class_indices, value, 50, "tryGet", Integer.class), object, jcb, Integer.parseInt(value)); } catch (NoSuchMethodException msme) { }
                case 51: try { return invoke(context, method(context, oclass, jcb, code, cached_class_indices, value, 51, "tryGet", int.class), object, jcb, Integer.parseInt(value)); } catch (NoSuchMethodException msme) { }
                case 52: try { return invoke(context, method(context, oclass, jcb, code, cached_class_indices, value, 52, "get", Integer.class), object, jcb, Integer.parseInt(value)); } catch (NoSuchMethodException msme) { }
                case 53: try { return invoke(context, method(context, oclass, jcb, code, cached_class_indices, value, 53, "get", int.class), object, jcb, Integer.parseInt(value)); } catch (NoSuchMethodException msme) { }
                case 54: try { return invoke(context, method(context, oclass, jcb, code, cached_class_indices, value, 54, "tryGet", Long.class), object, jcb, Long.parseLong(value)); } catch (NoSuchMethodException msme) { }
                case 55: try { return invoke(context, method(context, oclass, jcb, code, cached_class_indices, value, 55, "tryGet", long.class), object, jcb, Long.parseLong(value)); } catch (NoSuchMethodException msme) { }
                case 56: try { return invoke(context, method(context, oclass, jcb, code, cached_class_indices, value, 56, "get", Long.class), object, jcb, Long.parseLong(value)); } catch (NoSuchMethodException msme) { }
                case 57: try { return invoke(context, method(context, oclass, jcb, code, cached_class_indices, value, 57, "get", long.class), object, jcb, Long.parseLong(value)); } catch (NoSuchMethodException msme) { }

                case 58: InvalidLocatorSyntax.createAndThrow(context, "While resolving property locator '%(locator)', no appropriate getter (int/long) for index '%(value)' was found at class '%(class)'", "locator", this.getTextLocatorValue(context), "value", value, "class", oclass.getName());
                         throw (InvalidLocator) null; // compiler insists

                case 81: 
                default: InvalidLocatorSyntax.createAndThrow(context, "While resolving property locator '%(locator)', the term '%(value)' neither denotes a property name (alphanumeric) nor an index (numeric) at class '%(class)'", "locator", this.getTextLocatorValue(context), "value", value, "class", oclass.getName());
                         throw (InvalidLocator) null; // compiler insists
            }
        } catch (IllegalAccessException iae) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, iae, "While resolving property locator '%(locator)', generic invocation of property '%(value)' failed at class '%(class)'", "locator", this.getTextLocatorValue(context), "value", value, "class", oclass.getName());
            throw (ExceptionPreConditionViolation) null; // compiler insists
        } catch (InvocationTargetException ite) {
            if (ite.getTargetException() instanceof java.lang.RuntimeException) {
                throw (java.lang.RuntimeException) ite.getTargetException();
            }
            if (ite.getTargetException() instanceof java.lang.Error) {
                throw (java.lang.Error) ite.getTargetException();
            }
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, ite.getTargetException(), "While resolving property locator '%(locator)', generic invocation of property '%(value)' failed at class '%(class)'", "locator", this.getTextLocatorValue(context), "value", value, "class", oclass.getName());
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
    }

    static protected Map<String,Map<String,Integer>> cached_indices = new HashMap<String,Map<String,Integer>>(100);

    protected Map<String,Integer> getCachedClassIndices(CallContext context, Class oclass) {
        String class_name = oclass.getName();
        Map<String,Integer> cached_class_indices = cached_indices.get(class_name);
        if (cached_class_indices == null) {
            cached_class_indices = new HashMap<String,Integer>(4);
            cached_indices.put(class_name, cached_class_indices);
        }
        return cached_class_indices;
    }

    protected Method method(CallContext context, Class oclass, StringBuilder jcb, String[] code, Map<String,Integer> cached_class_indices, String value, int index, String name, Class... arguments) throws NoSuchMethodException {
        Method method = oclass.getMethod(name, arguments);
        if (cached_class_indices != null) {
            cached_class_indices.put(value, index);
        }
        if (jcb != null) {
            Class dc = method.getDeclaringClass();
            Class mgc = findMoreGeneralClass(context, dc, name, arguments);
            if (mgc != null) {
                dc = mgc;
            }
            if (code != null) {
                code[2] = dc.getName();
            } else {
                jcb.append(ds_code);
            }
            jcb.append("o = ((" + dc.getName() + ") o)." + name);
        }
        return method;
    }

    protected Class findMoreGeneralClass(CallContext context, Class cls, String name, Class... arguments) {
        // in case of inheritance diamonds   A->B A->C b->D C->D
        // this algorithm will fail, but maybe it's not worth it 80/20
        Class mgc = null;
        Class superclass = cls.getSuperclass();
        if (superclass != null) {
            try {
                superclass.getMethod(name, arguments);

                Class emgc = findMoreGeneralClass(context, superclass, name, arguments);

                mgc = emgc != null ? emgc : superclass;
            } catch (NoSuchMethodException nsme) {
            }
        }
        for (Class ifc : cls.getInterfaces()) {
            try {
                ifc.getMethod(name, arguments);

                Class emgc = findMoreGeneralClass(context, ifc, name, arguments);

                Class mgc_test = emgc != null ? emgc : ifc;

                if (mgc != mgc_test) {
                    return null;
                }
            } catch (NoSuchMethodException nsme) {
            }
        }
        return mgc;
    }

    protected Object invoke(CallContext context, Method method, Object object, StringBuilder jcb, Object... arguments)
                           throws IllegalAccessException, InvocationTargetException {
        Object result = method.invoke(object, arguments);
        if (jcb != null) {
            jcb.append("(");
            boolean first = true;
            for (Object argument : arguments) {
                if (first) {
                    first = false;
                } else {
                    jcb.append(", ");
                }
                if (argument instanceof CallContext) {
                    jcb.append("context");
                } else if (argument instanceof String) {
                    jcb.append("\"" + ((String) argument).replaceAll("\"", "\\\\\"") + "\"");
                } else if (argument instanceof Integer) {
                    jcb.append(((Integer) argument).toString());
                } else if (argument instanceof Long) {
                    jcb.append(((Long) argument).toString() + "L");
                } else {
                    CustomaryContext.create((Context)context).throwPreConditionViolation(context, "While resolving property locator '%(locator)' and creating javacode for it, an unexpected argument type was found '%(type)'", "locator", this.getTextLocatorValue(context), "type", argument.getClass().getName());
                    throw (ExceptionPreConditionViolation) null; // compiler insists
                }
            }
            jcb.append(");\n");
        }
        return result;
    }

    protected String[] javacode;

    protected String[] getLocatorJavaCode(CallContext context) throws InvalidLocator {
        return javacode;
    }
}
