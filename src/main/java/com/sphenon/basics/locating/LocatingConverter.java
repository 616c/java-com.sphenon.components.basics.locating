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

abstract public class LocatingConverter {

    abstract public Object convert(CallContext context, Object object);

    public LocatingConverter (CallContext context, Class source_class, Class target_class) {
        this.source_class = source_class;
        this.target_class = target_class;
    }

    protected Class source_class;

    public Class getSourceClass (CallContext context) {
        return this.source_class;
    }

    protected Class target_class;

    public Class getTargetClass (CallContext context) {
        return this.target_class;
    }
}
