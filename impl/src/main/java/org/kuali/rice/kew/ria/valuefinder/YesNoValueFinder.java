/*
 * Copyright 2010 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kew.ria.valuefinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.kuali.rice.core.util.KeyValue;
import org.kuali.rice.core.util.ContreteKeyValue;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;

/**
 * Generic value finder for Yes/No values.
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class YesNoValueFinder extends KeyValuesBase {
	private static final List<KeyValue> ACTIVE_LABELS;
	static {
		final List<KeyValue> activeLabels = new ArrayList<KeyValue>();
		activeLabels.add(new ContreteKeyValue("Y", "Yes"));
		activeLabels.add(new ContreteKeyValue("N", "No"));
		ACTIVE_LABELS = Collections.unmodifiableList(activeLabels);
	}
	
	@Override
	public List<KeyValue> getKeyValues() {
		return ACTIVE_LABELS;
	}
}
