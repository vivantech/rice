/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kcb;

import org.kuali.rice.kcb.dao.BusinessObjectDao;
import org.kuali.rice.kcb.service.EmailService;
import org.kuali.rice.kcb.service.KENIntegrationService;
import org.kuali.rice.kcb.service.MessageDelivererRegistryService;
import org.kuali.rice.kcb.service.MessageDeliveryService;
import org.kuali.rice.kcb.service.MessageService;
import org.kuali.rice.kcb.service.MessagingService;
import org.kuali.rice.kcb.service.RecipientPreferenceService;
import org.quartz.JobDetail;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Service locator interface for the KCB module.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface KCBServiceLocator {
    public BusinessObjectDao getBusinessObjectDao();
    public JobDetail getMessageProcessingJobDetail();
    public PlatformTransactionManager getTransactionManager();
    public MessageDeliveryService getMessageDeliveryService();
    public MessageService getMessageService();
    public MessagingService getMessagingService();
    public MessageDelivererRegistryService getMessageDelivererRegistryService();
    public EmailService getEmailService();
    public RecipientPreferenceService getRecipientPreferenceService();
    public KENIntegrationService getKenIntegrationService();
}