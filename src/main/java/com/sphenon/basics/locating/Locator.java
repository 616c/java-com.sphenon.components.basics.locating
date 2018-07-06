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
import com.sphenon.basics.debug.*;
import com.sphenon.basics.context.classes.*;
import com.sphenon.basics.message.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.configuration.*;
import com.sphenon.basics.expression.*;
import com.sphenon.basics.encoding.*;

import com.sphenon.basics.locating.locators.*;
import com.sphenon.basics.locating.returncodes.*;
import com.sphenon.basics.locating.tplinst.*;
import com.sphenon.basics.locating.factories.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.regex.*;

import java.io.File;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.io.IOException;

public class Locator implements Cloneable, Dumpable, ContextAware {
    static final public Class _class = Locator.class;

    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }

    static protected long runtimestep_level;
    static public    long adjustRuntimeStepLevel(long new_level) { long old_level = runtimestep_level; runtimestep_level = new_level; return old_level; }
    static public    long getRuntimeStepLevel() { return runtimestep_level; }

    static protected Configuration config;

    static {
        notification_level = NotificationLocationContext.getLevel(RootContext.getInitialisationContext(), "com.sphenon.basics.locating.Locator");
        runtimestep_level = RuntimeStepLocationContext.getLevel(_class);
        config = Configuration.create(RootContext.getInitialisationContext(), _class);
    };

    protected String  text_locator_value;
    protected Locator sub_locator;
    protected Object  base_object;

    protected Locator (CallContext call_context, String text_locator_value, Locator sub_locator, String locator_class_parameter_string) {
        this.text_locator_value             = text_locator_value;
        this.sub_locator                    = sub_locator;
        this.locator_class_parameter_string = locator_class_parameter_string;
    }

    protected Locator (CallContext call_context, String text_locator_value, Locator sub_locator, String locator_class_id, String locator_class_parameter_string) {
        this.text_locator_value             = text_locator_value;
        this.sub_locator                    = sub_locator;
        this.locator_class_id               = locator_class_id;
        this.locator_class_parameter_string = locator_class_parameter_string;
    }

    static protected RegularExpression split_re = new RegularExpression("/,|\\(|\\)");

    static protected RegularExpression any_re   = new RegularExpression("^(?:(?:(?:ctn|oorl):)?(?://([A-Za-z0-9_]*)(?::([^/]+))?))?(/)?(.*)$");

    /**
       Convenience method, creates a locator and retrieves the target
    */
    static public Object resolve(CallContext context, String text_locator, Object base_object, String default_type) throws InvalidLocator {
        return createLocator(context, text_locator, default_type).retrieveTarget(context, base_object);
    }

    static public Object resolve(CallContext context, String text_locator, Object base_object) throws InvalidLocator {
        return resolve(context, text_locator, base_object, null);
    }

    static public Object resolve(CallContext context, String text_locator) throws InvalidLocator {
        return resolve(context, text_locator, null, null);
    }

    static public Object tryResolve(CallContext context, String text_locator, Object base_object, String default_type) {
        try {
            return createLocator(context, text_locator, default_type).retrieveTarget(context, base_object);
        } catch (LocatorTargetNotFound ltnf){
            return null;
        } catch (InvalidLocator il) {
            il.printStackTrace();
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, il, "Cannot resolve locator, it is not valid");
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
    }

    static public Object tryResolve(CallContext context, String text_locator, Object base_object) {
        return tryResolve(context, text_locator, base_object, null);
    }

    static public Object tryResolve(CallContext context, String text_locator) {
        return tryResolve(context, text_locator, null, null);
    }

    static public Locator createLocator (CallContext context, String text_locator) throws InvalidLocator {
        return doCreateLocator(context, text_locator, false, null);
    }

    static public Locator createLocator (CallContext context, String text_locator, String default_type) throws InvalidLocator {
        return doCreateLocator(context, text_locator, false, default_type);
    }

    static public Locator createGenericLocator (CallContext context, String text_locator) throws InvalidLocator {
        return doCreateLocator(context, text_locator, true, null);
    }

    static protected Map<String,FactorySpecificLocator> factory_specific_locator_cache;

    static protected Locator doCreateLocator (CallContext context, String text_locator, boolean is_generic, String default_type) throws InvalidLocator {

        Locator sub_locator = null;

        {
            Matcher m = split_re.getMatcher(context, text_locator);
            int nesting = 0;
            int tll = text_locator.length();
            int lastend = 0;
            while (m.find()) {
                String sep = m.group(0);
                if (nesting == 0 && sep.equals("/,")) {
                    String sub_text_locator = text_locator.substring(m.end(0));
                    text_locator            = text_locator.substring(0, m.start(0));

                    sub_locator = createLocator(context, sub_text_locator);
                    break;
                } else if (sep.equals("(")) {
                    nesting++;
                } else if (sep.equals(")")) {
                    nesting--;
                    if (nesting < 0) {
                        InvalidLocator.createAndThrow(context, "Locator '%(locator)' contains inbalanced parenthesis, closing without opening at position '%(position)'", "locator", text_locator, "position", m.start());
                    }
                }
            }
            if (nesting != 0) {
                InvalidLocator.createAndThrow(context, "Locator '%(locator)' contains inbalanced parenthesis, too few closing ones", "locator", text_locator);
            }
        }

        String[] match;
        if (text_locator.startsWith("file:")) {
            return new LocatorFile(context, text_locator.substring(5), sub_locator, null);
        }

        if (default_type == null) { default_type = "File"; }

        if ((match = any_re.tryGetMatches(context, text_locator)) != null) {

            String lc = (match[0] != null ? match[0] : default_type);

            // handling of the intermediate '/'
            // "does it belong to locator-text or not?"
            if (    (match[2] != null && match[2].length() != 0)
                 && (match[0] == null || match[0].length() == 0)
                 && (match[1] == null || match[1].length() == 0)
               ) {
                if (match[3] == null) {
                    match[3] = match[2];
                } else {
                    match[3] = match[2] + match[3];
                }
            }

            if (is_generic) {
                return new Locator_Generic(context, match[3], sub_locator, lc, match[1]);

            } else {

                FactorySpecificLocator factory_specific_locator = factory_specific_locator_cache == null ? null : factory_specific_locator_cache.get(lc);
                if (factory_specific_locator == null) {
                    String locator_class_name = "com.sphenon.basics.locating.locators.Locator" + lc;
                    Class  locator_class = null;

                    try {
                        locator_class = com.sphenon.basics.cache.ClassCache.getClassForName(context, locator_class_name);
                    } catch (ClassNotFoundException cnfe) {
                        CustomaryContext.create((Context)context).throwConfigurationError(context, "Locator class '%(class)' for text locator '%(text_locator)' does not exist (class loader '%(loader)')", "text_locator", text_locator, "class", locator_class_name, "loader", Thread.currentThread().getContextClassLoader());
                        throw (ExceptionConfigurationError) null; // compiler insists
                    }

                    Constructor<Locator> constructor = null;
                    try {
                        constructor = locator_class.getConstructor(CallContext.class, String.class, Locator.class, String.class);
                    } catch (NoSuchMethodException nsme) {
                        CustomaryContext.create((Context)context).throwConfigurationError(context, "Locator class '%(class)' for text locator '%(text_locator)' exists, but does not provide an approriate constructor (CallContext, String, Locator, String)", "text_locator", text_locator, "class", locator_class);
                        throw (ExceptionConfigurationError) null; // compiler insists
                    }
                
                    factory_specific_locator = new FactorySpecificLocator_Generic(context, constructor, locator_class_name);

                    if (factory_specific_locator_cache == null) {
                        factory_specific_locator_cache = new HashMap<String,FactorySpecificLocator>();
                    }
                    factory_specific_locator_cache.put(lc, factory_specific_locator);
                }

                return factory_specific_locator.create(context, match[3], sub_locator, match[1]);
            }
        }

        CustomaryContext.create((Context)context).throwPreConditionViolation(context, "Text locator '%(text_locator)' has invalid format", "text_locator", text_locator);
        throw (ExceptionPreConditionViolation) null; // compiler insists
    }

    static protected RegularExpression locator_class_re = new RegularExpression(".*\\.locators\\.Locator([A-Za-z0-9_]+)$");

    protected String locator_class_id;

    public String getLocatorClassId (CallContext context) {
        if (this.locator_class_id == null) {
            String[] match = locator_class_re.tryGetMatches(context, this.getClass().getName());
            if (match != null) {
                this.locator_class_id = match[0];
            } else {
                this.locator_class_id = this.getClass().getName();
            }
        }
        return this.locator_class_id;
    }

    public String getLocatorClassParametrised (CallContext context) {
        return this.getLocatorClassId(context) + (this.locator_class_parameter_string == null ? "" : (":" + this.locator_class_parameter_string));
    }
        
    public String getTextLocatorValue (CallContext context) {
        return this.text_locator_value;
    }

    protected void setTextLocatorValue (CallContext context, String tlv) {
        this.text_locator_value = tlv;
        this.locator_steps = null;
    }

    public String getResolvedTextLocatorValue (CallContext context) throws InvalidLocator {
        CustomaryContext.create((Context)context).throwLimitation(context, "Locator '%(locatorclass)' does not provide a resolved text locator value", "locatorclass", this.getClass().getName());
        throw (ExceptionLimitation) null; // compiler insists
    }

    /**
       This method merely returns the local value of this Locator, currently
       without any base objects. It is currently used only in places where a
       unique identifier was needed or where it should be printed for
       debugging purposes.

       Maybe the function should be better renamed to "getLocalTextLocator",
       since there is no need for an "absolut" method since there IS NO
       absolute method (you're always relative!).
     */
    public String getPartialTextLocator (CallContext context, boolean root) {
        return (root ? "ctn:" : "") + "//" + this.getLocatorClassId(context) + "/" + this.text_locator_value + (this.sub_locator != null ? ("/," + this.sub_locator.getPartialTextLocator(context, false)) : "");
    }

    public String getPartialTextLocator (CallContext context) {
        return this.getPartialTextLocator(context, true);
    }

    /**
       @deprecated please use "getPartialTextLocator" instead
    */
    public String getTextLocator (CallContext context) {
        return this.getPartialTextLocator(context, true);
    }

    public Locator getSubLocator (CallContext context) {
        return this.sub_locator;
    }

    protected String concatenate(CallContext context, String s1, String s2) {
        int len1 = s1.length();
        int len2 = s2.length();
        return ((len1 != 0 && s1.charAt(len1-1) != '/' && len2 != 0 && s2.charAt(0) != '/') ? "/" : "") + s2;
    }

    public String tryGetTextLocatorValue (
        CallContext context,
        Locator relative_to,
        String result_locator_class) {
        return this.tryGetTextLocator_Wrapper(context, relative_to, result_locator_class);
    }

    public String tryGetTextLocator (
        CallContext context,
        Locator relative_to) {
        return this.tryGetTextLocator_Wrapper(context, relative_to, null);
    }

    static Locator relative_to_default;

    /**
       Tries to calculate a TextLocator-String which is relative to the
       location determined by the relative_to argument and whose class is
       given by the result_locator_class. If there is a result_locator_class
       given, the method returns only the text locator value (i.e., the part
       without the leading "ctn://<class/" prefix). If the result_locator_class
       argument is null, the result is a complete text locator (i.e. including
       the "ctn://class/" prefix).

       @param relative_to Determines the location which the resulting text
                          locator is relative to (note: there is no such thing
                          as an 'absolute' locator, there is always an
                          explicit or implicit reference system, be it
                          "TheInternet"; there is, nevertheless, a default
                          value if this argument is null, that default
                          is "ctn://Space/current_process/locator_factory",
                          which means the text lcoator is usable within
                          the current running process as an argument to
                          the factory {@link Factory_Locator})
       @param result_locator_class The class of the resulting locator, or null
       @returns The value part of a text locator string or the full text
                locator string, depending on the parameters (see explanation above)
     */
    protected String doGetTextLocator_Wrapper (
        CallContext context,
        Locator relative_to, // could become a Location; Locator for convenience
        String result_locator_class
    ) {

        if (relative_to == null) {
            if (relative_to_default == null) {
                relative_to_default = Factory_Locator.tryConstruct(context, "ctn://Space/current_process/locator_factory");
            }
            relative_to = relative_to_default;
        }

        return this.doGetTextLocator(context, relative_to, result_locator_class);
    }

    
    protected String tryGetTextLocator_Wrapper (
        CallContext context,
        Locator relative_to,
        String result_locator_class ) {

        if (relative_to == null) {
            if (relative_to_default == null) {
                relative_to_default = Factory_Locator.tryConstruct(context, "ctn://Space/current_process/locator_factory");
            }
            relative_to = relative_to_default;
        }
        return this.tryGetTextLocator(context, relative_to, result_locator_class);
    }

    protected String tryGetTextLocator (
        CallContext context,
        Locator relative_to,
        String result_locator_class ) {
      try {
        return doGetTextLocator(context, relative_to, result_locator_class);
      } catch (Exception e) {
        return null;
      }
    }

    protected String doGetTextLocator (
        CallContext context,
        Locator relative_to,
        String result_locator_class) {
        if (result_locator_class != null) {
            if (this.getLocatorClassId(context).equals(relative_to.getLocatorClassId(context)) && "Path".equals(result_locator_class)) {
                if (this.getLocatorClassId(context).equals("Path")) {
                    CustomaryContext.create((Context)context).throwLimitation(context, "Currently, the locator resolver does not allow to calculate relative pathes between two path locators");
                    throw (ExceptionLimitation) null; // compiler insists
                }
                try {
                    String rtv = relative_to.getResolvedTextLocatorValue(context);
                    String v = this.getResolvedTextLocatorValue(context);
                    if (v.length() >= rtv.length() && v.substring(0, rtv.length()).equals(rtv)) {
                        String rel = v.substring(rtv.length());
                        if (rtv.length() > 0 && rel.length() > 0 && rel.charAt(0) == '/') {
                            rel = rel.substring(1);
                        }
                        String result_locator = rel;
                        if (this.sub_locator != null && this.sub_locator instanceof LocatorPath) {
                            result_locator += concatenate(context, result_locator, this.sub_locator.getTextLocatorValue(context));
                        }
                        return result_locator;
                    } else {
                        CustomaryContext.create((Context)context).throwConfigurationError(context, "Calculation of relative path failed: resolved text locator value '%(parent)' is not prefix of other one '%(child)'", "parent", rtv, "child", v);
                        throw (ExceptionConfigurationError) null; // compiler insists
                    }
                } catch (InvalidLocator il) {
                    CustomaryContext.create((Context)context).throwConfigurationError(context, il, "Calculation of relative path failed");
                    throw (ExceptionConfigurationError) null; // compiler insists
                }
            }
        }
        
        return null;
    }

    // more convenient
    public String tryGetTextLocatorValue (
        CallContext context,
        String relative_to,
        String result_locator_class) {
        try {
            return tryGetTextLocatorValue(context, Locator.createLocator(context, relative_to), result_locator_class);
        } catch (InvalidLocator il) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, il, "Cannot get text locator value from '%(locator)', relative to '%(relativeto)'", "locator", this.text_locator_value, "relativeto", relative_to);
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
    }

    // and even more convenient
    static public String tryGetTextLocatorValue (
        CallContext context,
        String locator,
        String relative_to,
        String result_locator_class) {
        try {
            return Locator.createLocator(context, locator).tryGetTextLocatorValue(context, Locator.createLocator(context, relative_to), result_locator_class);
        } catch (InvalidLocator il) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, il, "Cannot get text locator value from '%(locator)', relative to '%(relativeto)'", "locator", locator, "relativeto", relative_to);
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
    }

    protected String concatenateTextLocators (CallContext context, String t1, String t2) {
        return (t1 == null ? "" : t1) + (t1 != null && t1.length() != 0 && t2 != null && t2.length() != 0 ? "/" : "") + (t2 == null ? "" : t2);
    }

    public void appendLocator (CallContext context, Locator sub_locator) {
        if (sub_locator instanceof LocatorPath && sub_locator.getTextLocatorValue(context).length() == 0) {
            return;
        }
        if (this.sub_locator == null) {
            if (this.getLocatorClassId(context).equals(sub_locator.getLocatorClassId(context)) || sub_locator.getLocatorClassId(context).equals("Path")) {
                String t1 = this.getTextLocatorValue(context);
                String t2 = sub_locator.getTextLocatorValue(context);
                String t3 = concatenateTextLocators (context, t1, t2);
                this.setTextLocatorValue(context, t3);
                if (sub_locator.sub_locator != null) {
                    this.sub_locator = sub_locator.sub_locator;
                }
            } else {
                this.sub_locator = sub_locator;
            }
        } else {
            this.sub_locator = (Locator) this.sub_locator.clone();
            this.sub_locator.appendLocator(context, sub_locator);
        }
    }

    public Locator clone() {
        Locator cloned_locator = null;
        try {
            cloned_locator = (Locator) super.clone();
        } catch (java.lang.CloneNotSupportedException cnse) {
        }
        if (cloned_locator.sub_locator != null) {
            cloned_locator.sub_locator = (Locator) cloned_locator.sub_locator.clone();
        }
        cloned_locator.cached_target = null;
        return cloned_locator;
    }

    public String toString() {
        return this.toString(RootContext.getFallbackCallContext());
    }

    public String toString(CallContext context) {
        return super.toString() + " [ " + this.getTextLocator(context) + " ] ";
    }

    public Vector<String> getPathParts(CallContext context) throws InvalidLocator {
        Vector<String> pp = new Vector<String>();
        pp.add("@" + this.getLocatorClassId(context) + (this.locator_class_parameter_string == null ? "" : (":" + this.locator_class_parameter_string)));
        
        LocatorStep[] ls = this.getLocatorSteps(context);
        for (LocatorStep step : ls) {
            StringBuilder sb = new StringBuilder();
            if (step.getIsDefaultAttribute(context) == false) {
                sb.append(step.getAttribute(context));
                sb.append("=");
            }
            if (step.getValue(context) != null) {
                sb.append(step.getValue(context));
            } else {
                sb.append("(");
                sb.append(step.getLocator(context).getPartialTextLocator(context));
                sb.append(")");
            }
            pp.add(sb.toString());
        }

        if (this.sub_locator != null) {
            for (String part : this.sub_locator.getPathParts(context)) {
                pp.add(part);
            }
        }

        return pp;
    }

    public Object getBaseObject(CallContext context) {
        return this.base_object;
    }

    public void setBaseObject(CallContext context, Object base_object) {
        this.base_object = base_object;
    }

    public String getTargetVariableName(CallContext context) {
        return null;
    }

    public Object retrieveTarget(CallContext context, Object base_object) throws InvalidLocator {
        this.setBaseObject(context, base_object);
        return this.retrieveTarget(context);
    }

    protected boolean isTargetCacheable(CallContext context) throws InvalidLocator {
        return true;
    }

    static protected class CacheEntry {
        public int count;
        public Map<String,String[]> javacodes;
    }
    static protected Map<String,CacheEntry> locator_cache;
    static public LocatorJavaCache ljc = null;

    static protected boolean create_locator_cache;
    static protected Boolean load_locator_cache;

    protected boolean doLoadLocatorCache(CallContext context) {
        if (load_locator_cache == null) {
            load_locator_cache = LocatingPackageInitialiser.getConfiguration(context).get(context, "LoadLocatorCache", false);
        }
        return load_locator_cache;
    }

    protected String[] getLocatorJavaCode(CallContext context) throws InvalidLocator {
        return null;
    }

    protected Object cached_target;

    public Object retrieveTarget(CallContext call_context) throws InvalidLocator {
        Context context = Context.create(call_context, this.getSourceLocationContext(call_context));

        RuntimeStep runtime_step = null;
        if ((runtimestep_level & RuntimeStepLevel.OBSERVATION_CHECKPOINT) != 0) { runtime_step = RuntimeStep.create((Context) context, RuntimeStepLevel.OBSERVATION_CHECKPOINT, _class, "Retrieving target of locator '%(locator)'", "locator", this.getTextLocator(context)); }
        try {

            if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { CustomaryContext.create((Context)context).sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Locator '%(classid)'/'%(textlocator)', retrieving target...", "classid", this.getLocatorClassId(context), "textlocator", this.text_locator_value); }

            Object result = cached_target;

            if (result != null) {
                if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { CustomaryContext.create((Context)context).sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Using cached target..."); }
            } else {

                if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { CustomaryContext.create((Context)context).sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Not cached, retrieving local target..."); }

                String tlv = this.getTextLocatorValue(context);

                Object o = null;
                LocatorJavaCache.Result ljcr = null;
                if (doLoadLocatorCache(context) && ljc != null) {
                    try {
                        ljcr = ljc.retrieve(context, tlv, this.lookupBaseObject(context, false));
                        if (ljcr != null) {
                            o = ljcr.result;
                        }
                    } catch (Throwable t) {
                        CustomaryContext.create(Context.create(context)).throwConfigurationError(context, t, "Evaluation of translated Locator '%(locator)' failed", "locator", tlv);
                        throw (ExceptionConfigurationError) null; // compiler insists
                    }
                }
                if (ljcr == null) {
                    o = this.retrieveLocalTarget(context, create_locator_cache);

                    if (create_locator_cache) {
                        CacheEntry ce = null;
                        if (locator_cache == null) {
                            locator_cache = new HashMap<String,CacheEntry>();
                        } else {
                            ce = locator_cache.get(tlv);
                        }
                        if (ce == null) {
                            ce = new CacheEntry();
                            ce.javacodes = new HashMap<String,String[]>(4);
                            locator_cache.put(tlv, ce);
                        }
                        String[] javacode = getLocatorJavaCode(context);
                        if (javacode != null) {
                            if (ce.javacodes.get(javacode[2]) == null) {
                                ce.javacodes.put(javacode[2], javacode);
                            }
                        }
                        ce.count++;
                    }
                }

                if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { CustomaryContext.create((Context)context).sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Locator '%(textlocator)', retrieved local target '%(local)'", "textlocator", this.text_locator_value, "local", o); }

                if (this.sub_locator != null) {

                    if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { CustomaryContext.create((Context)context).sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Resolving sub locator with local target as base..."); }

                    this.sub_locator.setBaseObject(context, o);

                    o = this.sub_locator.retrieveTarget(context);

                    if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { CustomaryContext.create((Context)context).sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Locator '%(textlocator)', retrieved target from sub locator '%(target)'", "textlocator", this.text_locator_value, "target", o); }
                }

                result = o;

                if (isTargetCacheable(context)) {
                    if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { CustomaryContext.create((Context)context).sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Caching target..."); }
                    cached_target = o;
                }
            }

            if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { CustomaryContext.create((Context)context).sendTrace(context, Notifier.DIAGNOSTICS, "Locator '%(textlocator)', returning final target '%(target)'", "textlocator", this.text_locator_value, "target", result); }

            if (runtime_step != null) { runtime_step.setCompleted(context, "Locator target successfully retrieved"); runtime_step = null; }

            return result;
        } catch (InvalidLocator il) {
            if (runtime_step != null) { runtime_step.setFailed(context, il, "Locator target retrieval failed"); runtime_step = null; }
            throw il;
        } catch (Error e) {
            if (runtime_step != null) { runtime_step.setFailed(context, e, "Locator target retrieval failed"); runtime_step = null; }
            throw e;
        } catch (RuntimeException re) {
            if (runtime_step != null) { runtime_step.setFailed(context, re, "Locator target retrieval failed"); runtime_step = null; }
            throw re;
        }
    }

    protected Object retrieveLocalTarget(CallContext context) throws InvalidLocator {
        return null;
    }

    protected Object retrieveLocalTarget(CallContext context, boolean create_java_code) throws InvalidLocator {
        return retrieveLocalTarget(context);
    }

    // --- parsing ------------------------------------------------------------------

    public class LocatorStep {
        public LocatorStep (CallContext call_context, String attribute, String value, Class target_type, boolean is_default_attribute) {
            this.attribute = attribute;
            this.value = value;
            this.locator = null;
            this.target_type = target_type;
            this.is_default_attribute = is_default_attribute;
        }

        public LocatorStep (CallContext call_context, String attribute, Locator locator, Class target_type, boolean is_default_attribute) {
            this.attribute = attribute;
            this.value = null;
            this.locator = locator;
            this.target_type = target_type;
            this.is_default_attribute = is_default_attribute;
        }

        protected String value;
        public String getValue (CallContext context) {
            return this.value;
        }

        protected Locator locator;
        public Locator getLocator (CallContext context) {
            return this.locator;
        }

        protected String attribute;
        public String getAttribute (CallContext context) {
            return this.attribute;
        }

        protected Class target_type;
        public Class getTargetType (CallContext context) {
            return this.target_type;
        }

        protected boolean is_default_attribute;
        public boolean getIsDefaultAttribute (CallContext context) {
            return this.is_default_attribute;
        }
    }

    static protected class LocatorParserTransition {
        public LocatorParserTransition (CallContext context, String specification) {
            String[] an = specification.split(":", -1);
            this.key_include_regexp = an[0];
            this.key_exclude_regexp = an[1];
            for (String atp : an[2].split("\\|")) {
                if (atp.equals("String")) {
                    this.allow_string_type = true;
                }
                if (atp.matches("Object.*")) {
                    this.allow_object_type = true;
                    String[] atplt = atp.split("-",2);
                    if (atplt != null && atplt.length == 2 && atplt[1] != null) {
                        this.sub_locator_default_type = atplt[1];
                    }
                }
            }
            this.next_state = Integer.parseInt(an[3]);
            if (an.length > 4) { this.value_include_regexp = new RegularExpression(context, an[4]); }
            if (an.length > 5) { this.value_exclude_regexp = new RegularExpression(context, an[5]); }
        }

        public void checkValue(CallContext context, String value, String step, String locator) throws InvalidLocator {
            if (    (    this.value_include_regexp != null
                      && this.value_include_regexp.matches(context, value) == false)
                 || (    this.value_exclude_regexp != null
                      && this.value_exclude_regexp.matches(context, value) == true)
               ) {
                InvalidLocator.createAndThrow(context, "While parsing locator '%(locator)', step '%(step)' contains an invalid value '%(value)' (does not match '%(include)'/'%(exclude)')", "locator", locator, "step", step, "value", value, "include", this.value_include_regexp, "exclude", this.value_exclude_regexp);
                throw (InvalidLocator) null; // compiler insists
            }
        }

        protected String key_include_regexp;
        public String getKeyIncludeRegExp (CallContext context) {
            return this.key_include_regexp;
        }

        protected String key_exclude_regexp;
        public String getKeyExcludeRegExp (CallContext context) {
            return this.key_exclude_regexp;
        }

        protected RegularExpression value_include_regexp;
        protected RegularExpression value_exclude_regexp;

        protected int next_state;
        public int getNextState (CallContext context) {
            return this.next_state;
        }

        protected boolean allow_object_type;
        public boolean allowObjectType (CallContext context) {
            return this.allow_object_type;
        }

        protected String sub_locator_default_type;
        public String getSubLocatorDefaultType (CallContext context) {
            return this.sub_locator_default_type;
        }

        protected boolean allow_string_type;
        public boolean allowStringType (CallContext context) {
            return this.allow_string_type;
        }
    }

    static protected class LPTRule {
        RegularExpression       inre;
        RegularExpression       exre;
        LocatorParserTransition lpt;
    }

    // static does not work here, does not find constructur then
    // compiler-bug???
    /* static */ protected class LocatorParserState {
        public LocatorParserState (CallContext context, String default_attribute, String transitions, boolean is_final, boolean can_terminate, Class target_type) {
            this.default_attribute = default_attribute;
            this.is_final          = is_final;
            this.can_terminate     = can_terminate;
            this.expected          = "";
            this.target_type       = target_type;
            if (transitions != null && transitions.length() != 0) {
                for (String transition : transitions.split(",")) {
                    LocatorParserTransition lpt = new LocatorParserTransition(context, transition);
                    String lptkin = lpt.getKeyIncludeRegExp(context);
                    String lptkex = lpt.getKeyExcludeRegExp(context);
                    this.expected += (expected.length() == 0 ? "" : ",") + lptkin + (lptkex != null & lptkex.length() != 0 ? (":" + lptkex) : "");
                    if (lptkin.matches("^[A-Za-z0-9_]+$") && (lptkex == null || lptkex.length() == 0)) {
                        if (this.next_states == null) {
                            this.next_states = new Hashtable<String,LocatorParserTransition>();
                        }
                        this.next_states.put(lptkin, lpt);
                    } else {
                        if (this.next_state_rules == null) {
                            this.next_state_rules = new Vector<LPTRule>();
                        }
                        LPTRule lptr = new LPTRule();
                        lptr.lpt = lpt;
                        lptr.inre = new RegularExpression(context, lptkin);
                        lptr.exre = new RegularExpression(context, lptkex);
                        this.next_state_rules.add(lptr);
                    }
                }
            }
        }

        protected String default_attribute;
        public String getDefaultAttribute (CallContext context) {
            return this.default_attribute;
        }

        protected Hashtable<String,LocatorParserTransition> next_states;
        protected Vector<LPTRule>                           next_state_rules;
        public LocatorParserTransition getNextState(CallContext context, String attribute) {
            if (attribute == null) { attribute = ""; }
            LocatorParserTransition lpt = (next_states == null ? null : next_states.get(attribute));
            if (lpt != null) { return lpt; }
            if (next_state_rules != null) {
                for (LPTRule next_state_rule : next_state_rules) {
                    if (    next_state_rule.inre.matches(context, attribute) == true
                         && next_state_rule.exre.matches(context, attribute) == false
                        ) {
                        return next_state_rule.lpt;
                    }
                }
            }
            return null;
        }

        protected String expected;
        public String getExpected(CallContext context) {
            return this.expected;
        }

        protected boolean is_final;
        public boolean isFinal(CallContext context) {
            return this.is_final;
        }

        protected boolean can_terminate;
        public boolean canTerminate(CallContext context) {
            return this.can_terminate;
        }

        protected Class target_type;
        public Class getTargetType(CallContext context) {
            return this.target_type;
        }
    }

    protected LocatorParserState[] getParserStates(CallContext context) {
        return new LocatorParserState[0];
    }

    protected boolean canGetParserStates(CallContext context) {
        return true;
    }

    static protected RegularExpression locator_sep = new RegularExpression("([^\\(\\)/]*)([\\(\\)/]?)");
    static protected RegularExpression key_re = new RegularExpression("(?:([A-Za-z0-9_]+)=)?(.*)");

    protected LocatorStep[] locator_steps;

    public LocatorStep[] getLocatorSteps(CallContext context) throws InvalidLocator {
        if (this.locator_steps != null) {
            return this.locator_steps;
        }

        LocatorParserState[] states = getParserStates(context);
        LocatorParserState   state = states[0];

        String tlv = this.getTextLocatorValue(context);
        Matcher m = locator_sep.getMatcher(context, tlv);
        int tlvlen = tlv.length();
        Vector<String> text_locator_steps = new Vector<String>();
        StringBuffer sb = new StringBuffer();
        int nesting = 0;
        boolean at_end;
        boolean at_sep = false;
        while (m.find()) {
            String text = m.group(1);
            String sep  = m.group(2);
            if (sep == null || sep.length() == 0 || sep.equals("/")) {
                if (nesting == 0) {
                    sb.append(text);
                    at_sep = true;
                } else {
                    sb.append(text);
                    sb.append(sep);
                }
            } else if (sep.equals("(")) {
                sb.append(text);
                sb.append(sep);
                nesting++;
            } else if (sep.equals(")")) {
                sb.append(text);
                sb.append(sep);
                nesting--;
                if (nesting < 0) {
                    InvalidLocator.createAndThrow(context, "Locator '%(locator)' contains inbalanced parenthesis, closing without opening after '%(segment)'", "locator", tlv, "segment", sb.toString() + text + sep);
                }
            }
            if ((at_end = (m.end() == tlvlen)) || at_sep) {
                text_locator_steps.add(sb.toString());
                sb.setLength(0);
                if (at_end) { break; }
                at_sep = false;
            }
        }
        if (nesting != 0) {
            InvalidLocator.createAndThrow(context, "Locator '%(locator)' contains inbalanced parenthesis, too few closing ones", "locator", tlv);
        }
        
        this.locator_steps = new LocatorStep[text_locator_steps.size()];
        int step = 0;

        for (String text_locator_step : text_locator_steps) {
            if (state.isFinal(context)) {
                InvalidLocator.createAndThrow(context, "While parsing locator '%(locator)', step '%(step)' is not valid here (locator is already complete)", "locator", tlv, "step", text_locator_step);
                throw (InvalidLocator) null; // compiler insists
            }

            String[] av = key_re.tryGetMatches(context, text_locator_step);
            String attribute = null;
            String value      = null;
            boolean is_default_attribute = false;
            if (av[0] != null && av[0].length() != 0) {
                attribute  = av[0];
                value      = av[1];
            } else {
                if (state.getDefaultAttribute(context) == null) {
                    InvalidLocator.createAndThrow(context, "While parsing locator '%(locator)', step '%(step)' is not valid here (there is no default attribute defined)", "locator", tlv, "step", text_locator_step);
                    throw (InvalidLocator) null; // compiler insists
                }
                attribute  = state.getDefaultAttribute(context);
                value      = av[1];
                is_default_attribute = true;
            }

            LocatorParserTransition lpt = state.getNextState(context, attribute);
            if (lpt == null) {
                InvalidLocator.createAndThrow(context, "While parsing locator '%(locator)', attribute '%(attribute)' is not valid here (expected '%(expected)')", "locator", tlv, "attribute", attribute, "expected", state.getExpected(context));
                throw (InvalidLocator) null; // compiler insists
            }

            lpt.checkValue(context, value, text_locator_step, tlv);

            state = states[lpt.getNextState(context)];

            int vl = value.length();
            boolean openpar  = value.length() >= 1 && value.charAt(0) == '(';
            boolean closepar = value.length() >= 1 && value.charAt(vl-1) == ')';

            if (openpar && ! closepar) {
                InvalidLocator.createAndThrow(context, "Nested locator '%(nested)' in '%(locator)' starts with a '(', but does not end with a ')'", "locator", tlv, "nested", value);
                throw (InvalidLocator) null; // compiler insists
            }
            if (closepar && ! openpar) {
                InvalidLocator.createAndThrow(context, "Nested locator '%(nested)' in '%(locator)' ends with a ')', but does not start with a '('", "locator", tlv, "nested", value);
                throw (InvalidLocator) null; // compiler insists
            }

            boolean is_object = (openpar && closepar);

            if (is_object && lpt.allowObjectType(context) == false) {
                InvalidLocator.createAndThrow(context, "A nested locator '%(nested)' in '%(locator)' refers to an object, but only strings are allowed here instead of a nested locator", "locator", tlv, "nested", value);
                throw (InvalidLocator) null; // compiler insists
            }

            if (is_object == false && lpt.allowStringType(context) == false) {
                InvalidLocator.createAndThrow(context, "Value '%(unnested)' in '%(locator)' is a string, but only nested locators are allowed here, refering to an object", "locator", tlv, "nested", value);
                throw (InvalidLocator) null; // compiler insists
            }

            if (is_object) {
                value = value.substring(1,vl-1);
            }

            this.locator_steps[step++] = is_object ?
                                 new LocatorStep(context, attribute, Locator.createLocator(context, value, lpt.getSubLocatorDefaultType(context)), state.getTargetType(context), is_default_attribute)
                               : new LocatorStep(context, attribute, Encoding.recode(context, value, Encoding.URI, Encoding.UTF8), state.getTargetType(context), is_default_attribute);
        }

        if (state.canTerminate(context) == false) {
            InvalidLocator.createAndThrow(context, "While parsing locator '%(locator)', locator is not complete (expected '%(expected)')", "locator", tlv, "expected", state.getExpected(context));
            throw (InvalidLocator) null; // compiler insists
        }

        return this.locator_steps;
    }

    public List<String> getLocatorParts(CallContext context, String attribute, int minimum, int maximum) {
        LocatorStep[] steps;
        try {
            steps = getLocatorSteps(context);
        } catch (InvalidLocator il) {
            CustomaryContext.create((Context)context).throwConfigurationError(context, il, "Cannot retrieve locator parts");
            throw (ExceptionConfigurationError) null; // compiler insists
        }
        List<String> parts = new ArrayList<String>();
        boolean in_part = false;
        boolean part_completed = false;
        for (LocatorStep step : steps) {
            if (step.getAttribute(context).equals(attribute)) {
                if (part_completed) {
                    CustomaryContext.create((Context)context).throwConfigurationError(context, "Locator '%(locator)' contains parts for attribute '%(attribute)' more than once", "locator", this.getTextLocatorValue(context), "attribute", attribute);
                    throw (ExceptionConfigurationError) null; // compiler insists
                }
                parts.add(step.getValue(context));
                in_part = true;
            } else {
                if (in_part) { part_completed = true; }
            }
        }
        if (parts.size() < minimum || parts.size() > maximum) {
            CustomaryContext.create((Context)context).throwConfigurationError(context, "Number '%(size)' of parts for attribute '%(attribute)' in locator '%(locator)' is not within range [%(minimum),%(maximum)]", "locator", this.getTextLocatorValue(context), "attribute", attribute, "size", parts.size(), "minimum", minimum, "maximum", maximum);
            throw (ExceptionConfigurationError) null; // compiler insists
        }
        return parts;
    }

    /* ------------------------------------------------------- */

    protected class LocatorClassParameter {
        public LocatorClassParameter (CallContext context, String name, String value_regexp, String default_value) {
            this.name          = name;
            this.value_regexp  = value_regexp;
            this.default_value = default_value;
        }

        protected String name;
        public String getName (CallContext context) {
            return this.name;
        }

        protected String value_regexp;
        public String getValueRegExp(CallContext context) {
            return this.value_regexp;
        }

        protected String default_value;
        public String getDefaultValue(CallContext context) {
            return this.default_value;
        }
    }

    protected LocatorClassParameter[] getLocatorClassParameters(CallContext context) {
        return new LocatorClassParameter[0];
    }

    protected String locator_class_parameter_string;
    protected HashMap<String,String> actual_locator_class_parameters;

    public String getLocatorClassParameter(CallContext context, String cp_name) throws InvalidLocator {
        if (this.actual_locator_class_parameters == null) {
            this.actual_locator_class_parameters = new HashMap<String,String>();
            String[] parts = locator_class_parameter_string == null ? new String[0] : locator_class_parameter_string.split(":");
            LocatorClassParameter[] formal_lcp = getLocatorClassParameters(context);
            int mf = formal_lcp.length;
            int ma = parts.length;
            int max = ma > mf ? ma : mf;
            for (int i=0; i<max; i++) {
                if (i < ma) {
                    String[] nv = parts[i].split("=", 2);
                    String name  = ((nv.length < 2 || nv[1] == null) ? null  : nv[0]);
                    String value = ((nv.length < 2 || nv[1] == null) ? nv[0] : nv[1]);
                    if (i < mf) {
                        String formal_name     = formal_lcp[i].getName(context);
                        String formal_value_re = formal_lcp[i].getValueRegExp(context);
                        if (name != null && name.length() != 0) {
                            if (name.equals(formal_name) == false) {
                                InvalidLocator.createAndThrow(context, "Locator class parameter '%(actual)' invalid, expected '%(expected)'", "actual", name, "expected", formal_name);
                            }
                        }
                        if (value.matches(formal_value_re) == false) {
                            InvalidLocator.createAndThrow(context, "Locator class parameter value '%(value)' does not match '%(regexp)'", "value", value, "regexp", formal_value_re);
                        }
                        actual_locator_class_parameters.put(formal_name, value);
                    } else {
                        if (name == null || name.length() == 0) {
                            InvalidLocator.createAndThrow(context, "No name for locator class parameter value '%(actual)'", "actual", value);
                        }
                        actual_locator_class_parameters.put(name, value);
                    }
                } else {
                    String formal_name   = formal_lcp[i].getName(context);
                    String default_value = formal_lcp[i].getDefaultValue(context);
                    
                    actual_locator_class_parameters.put(formal_name, default_value);
                }
            }
        }
        return actual_locator_class_parameters.get(cp_name);
    }

    /* ------------------------------------------------------- */

    protected Vector<LocatorBaseAcceptor> getBaseAcceptors(CallContext context) {
        return new Vector<LocatorBaseAcceptor>();
    }

    public Object tryAcceptBaseObject(CallContext context, Object base_object_candidate) {
        if (base_object_candidate == null) {
            return null;
        }
        Object base_object_result;
        for (LocatorBaseAcceptor base_acceptor : getBaseAcceptors(context)) {
            if (    (base_acceptor.getClass(context).isAssignableFrom(base_object_candidate.getClass()))
                 && ((base_object_result = (base_acceptor.tryAccept(context, base_object_candidate))) != null)
               ) {
                return base_object_result;
            }
        }
        if (base_object_candidate instanceof Locator) {
            try {
                Object target = ((Locator)base_object_candidate).retrieveTarget(context);
                if ((base_object_result = tryAcceptBaseObject(context, target)) != null) {
                    return base_object_result;
                }
            } catch (InvalidLocator il) {
                CustomaryContext.create((Context)context).throwPreConditionViolation(context, il, "Locator in LocatingContext is invalid");
                throw (ExceptionPreConditionViolation) null; // compiler insists
            }
        }
        if (base_object_candidate instanceof Location) {
            for (Locator locator : ((Location)base_object_candidate).getLocators(context)) {
                try {
                    Object target = locator.retrieveTarget(context);
                    if ((base_object_result = tryAcceptBaseObject(context, target)) != null) {
                        return base_object_result;
                    }
                } catch (InvalidLocator il) {
                    CustomaryContext.create((Context)context).throwPreConditionViolation(context, il, "Locator in LocatingContext is invalid");
                    throw (ExceptionPreConditionViolation) null; // compiler insists
                }
            }

        }
        return null;
    }

    protected Object lookupBaseObject(CallContext context, boolean required) throws InvalidLocator {
        Object base_object_result;
        if ((base_object_result = tryAcceptBaseObject(context, this.base_object)) != null) { return base_object_result; }

        LocationContext loc = this.getSourceLocationContext(context);
        if (loc != null) {
            LocatingContext llc = LocatingContext.get((Context) loc);
            if ((base_object_result = llc.lookupObject(context, this)) != null) { return base_object_result; }
        }

        if (required) {
            String expected = null;
            for (LocatorBaseAcceptor base_acceptor : getBaseAcceptors(context)) {
                expected = (expected == null ? "" : (expected + ", ")) + base_acceptor.getClass(context).getName().replace(".*\\.", "");
            }
            LocatorTargetNotFound.createAndThrow(context, "Locator '%(locator)' requires a base node, but none or no appropriate was given or found in context, expected one of '%(expected)'", "locator", this.getTextLocatorValue(context), "expected", expected);
            throw (InvalidLocator) null; // compiler insists
        }

        return null;
    }

    protected DataSource_LocationContext_ source_location_context_ds;

    protected LocationContext getSourceLocationContext (CallContext context) {
        return this.source_location_context_ds == null ? null : this.source_location_context_ds.get(context);
    }

    /**
       Embeds the locator into the given LocationContext parameter (on demand
       via a DataSource). If it is already embedded, the operation leaves it
       unchanged.
     */
    public void setSourceLocationContext (CallContext context, DataSource_LocationContext_ source_location_context_ds) {
        // why that?
        // a locator will need such a context only if it is unresolved and
        // relative, therefore this method just "offers" a location context
        // but: if a locator does already contain a location context within
        // which it needs to be evaluated and is passed as a location into
        // some context, this may alter the semantics incorrectly.
        // Example: a projected artefact is located at a base location plus
        // some relative path plus an additional relative path
        if (this.source_location_context_ds == null) {
            this.source_location_context_ds = source_location_context_ds;
        }
    }

    public DataSource_LocationContext_ getTargetLocationContext (CallContext context) {
        return new DataSource_LocationContext_() {
                public LocationContext getObject(CallContext context) {
                    return get(context);
                }
                public LocationContext get(CallContext context) {
                    LocationContext source_location_context = Locator.this.getSourceLocationContext(context);
                    Context location_context = (source_location_context == null ? ((Context) RootContext.createLocationContext()) : Context.create(null, source_location_context));
                    Object o;
                    try {
                        o = Locator.this.retrieveTarget(context);
                    } catch (InvalidLocator il) {
                        CustomaryContext.create((Context)context).throwConfigurationError(context, il, "Invalid locator while constructing locating context");
                        throw (ExceptionConfigurationError) null; // compiler insists
                    }
                    if (o != null) {
                        LocatingContext lc = LocatingContext.create(location_context);
                        lc.pushBaseObject(context, o);
                    }
                    return location_context;
                }
            };
    }

    public void dump(CallContext context, DumpNode dump_node) {
        DumpNode dn = dump_node.openDump(context, "Locator");
        dn.dump(context, "Class           ", this.getLocatorClassId(context) + " (" + this.getClass().getName().replaceFirst(".*\\.", "") + ")");
        dn.dump(context, "Value           ", this.text_locator_value);

        try {
            this.getLocatorClassParameter(context, "dummy"); // call to prepare
        } catch (InvalidLocator il) {
            dn.dump(context, "DUMP ERROR      ", il);
        }
        if (actual_locator_class_parameters != null && actual_locator_class_parameters.size() != 0) {
            DumpNode dns1 = dn.openDump(context, "Class Parameters");
            for (String key : actual_locator_class_parameters.keySet()) {
                dns1.dump(context, key, actual_locator_class_parameters.get(key));
            }
            dns1.close(context);
        }

        if (this.canGetParserStates(context)) {
            DumpNode dns2 = dn.openDump(context, "Steps           ");
            try {
                LocatorStep[] steps = getLocatorSteps(context);
                this.getLocatorClassParameter(context, "dummy"); // call to prepare
                for (LocatorStep step : steps) {
                    dns2.dump(context, step.getAttribute(context), "<" + step.getTargetType(context) + "> " + step.getValue(context));
                    if (step.getLocator(context) != null) {
                        dns2.dump(context, "Nested Locator", step.getLocator(context));
                    }
                }
            } catch (InvalidLocator il) {
                dn.dump(context, "DUMP ERROR      ", il);
            }
            dns2.close(context);
        }

        if (this.sub_locator != null) {
            dn.dump(context, "Sub Locator     ", this.sub_locator);
        }

        LocationContext loc = this.getSourceLocationContext(context);
        if (loc != null) {
            LocatingContext llc = LocatingContext.get((Context) loc);
            dn.dump(context, "Locating Context", llc);
        }

        dn.close(context);
    }

    public boolean equals(Object object) {
        if (object == null) { return false; }
        if ((object instanceof Locator) == false) { return false; }
        Locator other = (Locator) object;
        if (    (    this.text_locator_value == other.text_locator_value
                  || this.text_locator_value != null && this.text_locator_value.equals(other.text_locator_value)
                )
             && (    this.sub_locator == other.sub_locator
                  || this.sub_locator != null && this.sub_locator.equals(other.sub_locator)
                )
           ) { return true; }
        return false;
    }

    static public void dumpLocators(CallContext context) {
        if (create_locator_cache) {
            for (String locator : locator_cache.keySet()) {
                CacheEntry ce = locator_cache.get(locator);
                System.err.printf("%8s %s\n", ce.count, locator);
            }
        }
    } 

    static public void saveCacheOnExit(CallContext context) {
        java.lang.Runtime.getRuntime().addShutdownHook(new Thread() { public void run() { saveCache(RootContext.getDestructionContext()); } });
        create_locator_cache = true;
    }

    static public void saveCache(CallContext context) {
        if (create_locator_cache) {
            String filename = LocatingPackageInitialiser.getConfiguration(context).get(context, "LocatorJavaCacheFile", (String) null);
            try {
                if (filename != null) {
                    File f = new File(filename);
                    f.setWritable(true);
                    FileOutputStream fos = new FileOutputStream(f);
                    OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
                    BufferedWriter bw = new BufferedWriter(osw);
                    PrintWriter pw = new PrintWriter(bw);
                    
                    pw.print("package com.sphenon.basics.locating;\n");
                    pw.print("\n");
                    pw.print("import com.sphenon.basics.context.*;\n");
                    pw.print("import com.sphenon.basics.context.classes.*;\n");
                    pw.print("import com.sphenon.basics.debug.*;\n");
                    pw.print("import com.sphenon.basics.message.*;\n");
                    pw.print("import com.sphenon.basics.notification.*;\n");
                    pw.print("import com.sphenon.basics.exception.*;\n");
                    pw.print("import com.sphenon.basics.customary.*;\n");
                    pw.print("import com.sphenon.basics.configuration.*;\n");
                    pw.print("import com.sphenon.basics.expression.*;;\n");
                    pw.print("import com.sphenon.basics.data.*;;\n");
                    pw.print("\n");
                    pw.print("import java.util.Hashtable;\n");
                    pw.print("import java.util.Vector;\n");
                    pw.print("import java.util.Map;\n");
                    pw.print("import java.util.HashMap;\n");
                    pw.print("import java.util.Set;\n");
                    pw.print("import java.util.HashSet;\n");
                    pw.print("\n");
                    pw.print("public class LocatorJavaCacheImpl implements LocatorJavaCache {\n");
                    pw.print("    public Result retrieve(CallContext context, String locator, Object base_object) {\n");
                    pw.print("        Result result = null;\n");
                    pw.print("        switch (locator.hashCode()) {\n");

                    Set<Integer> hash_codes = new HashSet<Integer>();
                    for (String locator : locator_cache.keySet()) {
                        CacheEntry ce = locator_cache.get(locator);
                        Integer hc = locator.hashCode();
                        if (hash_codes.contains(hc)) {
                            System.err.print("*** WARNING! *** hash code duplicate!\n");
                        }
                        hash_codes.add(hc);
                        int size;
                        if (ce.javacodes != null && (size = ce.javacodes.size()) > 0) {
                            pw.printf("            // %8d %s\n", ce.count, locator.replace("\n", " "));
                            pw.print("            case " + hc + ": {\n");
                            boolean typecheck = (size > 1 || ce.javacodes.keySet().iterator().next().equals("java.lang.Object") == false);
                            boolean first = true;
                            for (String classkey : ce.javacodes.keySet()) {
                                String[] javacode = ce.javacodes.get(classkey);
                                if (first) {
                                    first = false;
                                    if (javacode.length > 3 && javacode[3] != null) {
                                        pw.print("                " + javacode[3].replaceAll("\n","\n                ") + "\n");
                                    }
                                }
                                if (typecheck) {
                                    pw.print("                if (" + (javacode.length > 4 && javacode[4] != null ? javacode[4] : "base_object") + " instanceof " + classkey + ") {\n");
                                } else {
                                    pw.print("                {\n");
                                }
                                if (javacode[0] != null) {
                                    pw.print("                    " + javacode[0].replaceAll("\n","\n                    ") + "\n");
                                }
                                pw.print("                    result = new Result(" + (javacode[1] == null ? "null" : javacode[1]) + ");\n");
                                pw.print("                    break;\n");
                                pw.print("                }\n");
                            }
                            if (typecheck) {
                                pw.print("                break;\n");
                            }
                            pw.print("            }\n");
                        }
                    }

                    pw.print("        }\n");
                    pw.print("        return result;\n");
                    pw.print("    }\n");
                    pw.print("}\n");
                    pw.print("\n");
                    
                    pw.close();
                    bw.close();
                    osw.close();
                    fos.close();
                }
            } catch (FileNotFoundException fnfe) {
                CustomaryContext.create((Context)context).throwPreConditionViolation(context, fnfe, "Cannot write to file '%(filename)'", "filename", filename);
                throw (ExceptionPreConditionViolation) null; // compiler insists
            } catch (UnsupportedEncodingException uee) {
                CustomaryContext.create((Context)context).throwPreConditionViolation(context, uee, "Cannot write to file '%(filename)'", "filename", filename);
                throw (ExceptionPreConditionViolation) null; // compiler insists
            } catch (IOException ioe) {
                CustomaryContext.create((Context)context).throwPreConditionViolation(context, ioe, "Cannot write to file '%(filename)'", "filename", filename);
                throw (ExceptionPreConditionViolation) null; // compiler insists
            }
        }
    }
}
