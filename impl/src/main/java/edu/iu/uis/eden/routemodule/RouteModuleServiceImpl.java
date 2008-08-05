/*
 * Copyright 2005-2007 The Kuali Foundation.
 *
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.iu.uis.eden.routemodule;

import org.apache.log4j.Logger;
import org.kuali.rice.core.reflect.ObjectDefinition;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kew.actionrequests.ActionRequestValue;
import org.kuali.rice.kew.clientapp.RouteModuleRemote;
import org.kuali.rice.kew.engine.node.RouteNode;
import org.kuali.rice.kew.exception.ResourceUnavailableException;
import org.kuali.rice.kew.exception.WorkflowRuntimeException;
import org.kuali.rice.kew.util.KEWConstants;


/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RouteModuleServiceImpl implements RouteModuleService {

    private static final Logger LOG = Logger.getLogger(RouteModuleServiceImpl.class);

    public RouteModule findRouteModule(RouteNode node) throws ResourceUnavailableException {
        String routeMethodName = node.getRouteMethodName();
        LOG.debug("Finding route module for routeMethodName="+routeMethodName+" at route level "+node.getRouteNodeName());
        RouteModule routeModule = null;
        // default to FlexRM module if the routeMethodName is null
        if (routeMethodName == null || node.isFlexRM()) {
            routeModule = getFlexRMRouteModule(routeMethodName);
        } else {
            routeModule = getRouteModule(routeMethodName);
        }
        return routeModule;
    }

    public RouteModule findRouteModule(ActionRequestValue actionRequest) throws ResourceUnavailableException {
        if (actionRequest.getNodeInstance() == null) {
            return null;
        }
        return findRouteModule(actionRequest.getNodeInstance().getRouteNode());
    }

    private RouteModule getRouteModule(String routeMethodName) throws ResourceUnavailableException {
        if (routeMethodName == null) {
            return null;
        } else if ("".equals(routeMethodName.trim()) || KEWConstants.ROUTE_LEVEL_NO_ROUTE_MODULE.equals(routeMethodName)) {
                return null;
        }
        Object routeModule = GlobalResourceLoader.getObject(new ObjectDefinition(routeMethodName));//SpringServiceLocator.getExtensionService().getRouteModule(routeMethodName);
        if (routeModule instanceof RouteModule) {
            return (RouteModule)routeModule;
        } else if (routeModule instanceof RouteModuleRemote) {
            return new RouteModuleRemoteAdapter((RouteModuleRemote)routeModule);
        }
        throw new WorkflowRuntimeException("Could not locate the Route Module with the given name: " + routeMethodName);
    }

    private RouteModule getFlexRMRouteModule(String ruleTemplateName) {
        return new FlexRMAdapter();
    }


}
