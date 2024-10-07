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

import java.io.File;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocatorFile extends Locator {

    public LocatorFile (CallContext context, String text_locator_value, Locator sub_locator, String locator_class_parameter_string) {
        super(context, text_locator_value, sub_locator, locator_class_parameter_string);
        this.text_locator_value = this.replaceEnvironmentVariable(context, text_locator_value);
    }

    /* Parser States -------------------------------------------------------------------- */

    static protected LocatorParserState[] locator_parser_state;
        
    protected LocatorParserState[] getParserStates(CallContext context) {
        if (locator_parser_state == null) {
            locator_parser_state = new LocatorParserState[] {
                new LocatorParserState(context, "name", "name::String:0", false, true, File.class)
            };
        }
        return locator_parser_state;
    }

    /* Base Acceptors ------------------------------------------------------------------- */

    static protected Vector<LocatorBaseAcceptor> locator_base_acceptors;

    static public class LocatorBaseAcceptor_File extends LocatorBaseAcceptor {
        public LocatorBaseAcceptor_File (CallContext context) {
            super(context, File.class);
        }
        public Object tryAccept(CallContext context, Object base_object_candidate) {
            return (((File) base_object_candidate).isDirectory()) ? base_object_candidate : null;
        }
    }

    static protected Vector<LocatorBaseAcceptor> initBaseAcceptors(CallContext context) {
        if (locator_base_acceptors == null) {
            locator_base_acceptors = new Vector<LocatorBaseAcceptor>();
            locator_base_acceptors.add(new LocatorBaseAcceptor_File(context));
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
        return "file";
    }

    public String doGetTextLocator (CallContext context, Locator relative_to, String result_locator_class) {

        if (    relative_to.getLocatorClassId(context).equals("Space")
             && (    (    result_locator_class != null
                       && "local_host/file_system".equals(relative_to.getTextLocatorValue(context))
                       && "File".equals(result_locator_class)
                     )
                  || (    result_locator_class == null
                       && "current_process/locator_factory".equals(relative_to.getTextLocatorValue(context))
                     )
                )
           ) {
            String result_locator = this.text_locator_value;
            try {
                Object base = lookupBaseObject(context, false);
                if (base != null) {
                    File file = (File) this.retrieveLocalTarget(context);
                    result_locator =  file.getAbsolutePath();
                }
            } catch (InvalidLocator il) {
                CustomaryContext.create((Context)context).throwConfigurationError(context, il, "While looking for base object in LocatingContext, an invalid locator was found");
                throw (ExceptionConfigurationError) null; // compiler insists
            }

            if (this.sub_locator != null && this.sub_locator instanceof LocatorPath) {
                result_locator += concatenate(context, result_locator, this.sub_locator.getTextLocatorValue(context));
            }
            if (result_locator_class == null)
                return "ctn://File/" + result_locator;
            else {
                return result_locator;
            }
        }

        return super.doGetTextLocator(context, relative_to, result_locator_class);
    }

    protected Object retrieveLocalTarget(CallContext context) throws InvalidLocator {
        Object base = lookupBaseObject(context, false);

        String tlv = this.getTextLocatorValue(context);
        if (base == null || (tlv.length() > 0 && tlv.charAt(0) == '/')) {
            return new File(this.getTextLocatorValue(context));
        } else {
            return new File((File) base, this.getTextLocatorValue(context));
        }
    }

    public String getResolvedTextLocatorValue (CallContext context) throws InvalidLocator {
        return ((File)(this.retrieveTarget(context))).getPath();
    }
    
    protected Pattern environmentVariablePattern = Pattern.compile("\\$\\{([A-Za-z0-9_]*)\\}");

    private String replaceEnvironmentVariable(CallContext context, String string) {
        Matcher m = this.environmentVariablePattern.matcher(string);
        StringBuffer sb = new StringBuffer();
      
        while (m.find()) {
            String g = m.group(1);
            String env_variable = System.getenv(g);
            m.appendReplacement(sb, "");
            if (env_variable == null) {
                CustomaryContext.create((Context)context).throwConfigurationError(context, "Could not resolve given environment variable: '%(variable)'", "variable", g);
                throw (ExceptionConfigurationError) null; // compiler insists
            }
            sb.append(env_variable);
        }
        m.appendTail(sb);

        return sb.toString();
    }

    /**
       Used by append method in Locator.java. Overrides default implementation
       there. In our case (File), we want to treat a leading slash in the
       second locator (t2) as a sign of restarting at root. E.g. if t1 is
       "/home/john" and t2 is "/tmp", the result would be "/home/john//tmp";
       this will be reduced to simply "/tmp", since t2 is obviously an
       absolute path.

       Possibly the "concatenate" method should also be overriden here and
       modified accordingly.
     */
    protected String concatenateTextLocators (CallContext context, String t1, String t2) {
        if (t2 != null && t2.length() != 0 && t2.charAt(0) == '/') return t2;
        return (t1 == null ? "" : t1) + (t1 != null && t1.length() != 0 && t2 != null && t2.length() != 0 ? "/" : "") + (t2 == null ? "" : t2);
    }
}
