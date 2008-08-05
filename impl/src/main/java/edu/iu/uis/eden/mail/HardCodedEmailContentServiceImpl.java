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
// Created on Jan 18, 2007

package edu.iu.uis.eden.mail;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.apache.log4j.Logger;
import org.kuali.rice.core.Core;
import org.kuali.rice.kew.actionitem.ActionItem;
import org.kuali.rice.kew.clientapp.IDocHandler;
import org.kuali.rice.kew.doctype.DocumentType;
import org.kuali.rice.kew.util.KEWConstants;

import edu.iu.uis.eden.feedback.web.FeedbackForm;
import edu.iu.uis.eden.plugin.attributes.CustomEmailAttribute;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.util.Utilities;

/**
 * EmailContentGenerator that reproduces the hardcoded messages generated by the
 * current ActionListEmailServiceImpl. This is for testing purposes during the transition
 * to the EmailContentService.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class HardCodedEmailContentServiceImpl extends BaseEmailContentServiceImpl {
    private static final Logger LOG = Logger.getLogger(HardCodedEmailContentServiceImpl.class);

    protected String defaultReminderSubject = "OneStart Action List Reminder";

    public void setDefaultReminderSubject(String defaultReminderSubject) {
        this.defaultReminderSubject = defaultReminderSubject;
    }

    // ---- EmailContentService interface implementation

    public EmailContent generateImmediateReminder(WorkflowUser user, ActionItem actionItem, DocumentType documentType) {
        String docHandlerUrl = actionItem.getRouteHeader().getDocumentType().getDocHandlerUrl();
        if (docHandlerUrl.indexOf("?") == -1) {
            docHandlerUrl += "?";
        } else {
            docHandlerUrl += "&";
        }
        docHandlerUrl += IDocHandler.ROUTEHEADER_ID_PARAMETER + "=" + actionItem.getRouteHeaderId();
        docHandlerUrl += "&" + IDocHandler.COMMAND_PARAMETER + "=" + IDocHandler.ACTIONLIST_COMMAND;
        StringBuffer emailBody = new StringBuffer();

        emailBody.append("Your OneStart Action List has an eDoc(electronic document) that needs your attention: \n\n");
        emailBody.append("Document ID:\t" + actionItem.getRouteHeaderId() + "\n");
        emailBody.append("Initiator:\t\t");
        try {
            emailBody.append(actionItem.getRouteHeader().getInitiatorUser().getDisplayName() + "\n");
        } catch (Exception e) {
            LOG.error("Error retrieving initiator for action item " + actionItem.getRouteHeaderId());
            emailBody.append("\n");
        }
        emailBody.append("Type:\t\t" + "Add/Modify " + actionItem.getRouteHeader().getDocumentType().getName() + "\n");
        emailBody.append("Title:\t\t" + actionItem.getDocTitle() + "\n");
        emailBody.append("\n\n");
        emailBody.append("To respond to this eDoc: \n");
        emailBody.append("\tGo to " + docHandlerUrl + "\n\n");
        emailBody.append("\tOr you may access the eDoc from your Action List: \n");
        emailBody.append("\tGo to " + getActionListUrl() + ", and then click on the numeric Document ID: " + actionItem.getRouteHeaderId() + " in the first column of the List. \n");
        emailBody.append("\n\n\n");
        emailBody.append("To change how these email notifications are sent(daily, weekly or none): \n");
        emailBody.append("\tGo to " + getPreferencesUrl() + "\n");
        emailBody.append("\n\n\n");
        
        emailBody.append(getHelpLink(documentType) + "\n\n\n");

        // for debugging purposes on the immediate reminder only
        if (!isProduction()) {
            try {
                emailBody.append("Action Item sent to "
                        + actionItem.getUser().getAuthenticationUserId()
                                .getAuthenticationId());
                if (actionItem.getDelegationType() != null) {
                    emailBody.append(" for delegation type "
                            + actionItem.getDelegationType());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        StringBuffer emailSubject = new StringBuffer();
        try {
            CustomEmailAttribute customEmailAttribute = getCustomEmailAttribute(user, actionItem);
            if (customEmailAttribute != null) {
                String customBody = customEmailAttribute.getCustomEmailBody();
                if (!Utilities.isEmpty(customBody)) {
                    emailBody.append(customBody);
                }
                String customEmailSubject = customEmailAttribute.getCustomEmailSubject();
                if (!Utilities.isEmpty(customEmailSubject)) {
                    emailSubject.append(customEmailSubject);
                }
            }
        } catch (Exception e) {
            LOG.error("Error when checking for custom email body and subject.", e);
        }
        
        EmailContent content = new EmailContent();
        content.setBody(emailBody.toString(), false);
        content.setSubject(getEmailSubject(emailSubject.toString()));
        
        return content;

    }

    public EmailContent generateDailyReminder(WorkflowUser user, Collection<ActionItem> actionItems) {
        StringBuffer sf = new StringBuffer();
        sf.append(getDailyWeeklyMessageBody(actionItems));
        sf.append("To change how these email notifications are sent (immediately, weekly or none): \n");
        sf.append("\tGo to " + getPreferencesUrl() + "\n");
        // sf.append("\tSend as soon as you get an eDoc\n\t" +
        // getPreferencesUrl() + "\n\n");
        // sf.append("\tSend weekly\n\t" + getPreferencesUrl() + "\n\n");
        // sf.append("\tDo not send\n\t" + getPreferencesUrl() + "\n");
        sf.append("\n\n\n");
        sf.append(getHelpLink() + "\n\n\n");
        
        EmailContent content = new EmailContent();
        content.setBody(sf.toString(), false);
        content.setSubject(getEmailSubject());
        
        return content;
    }

    public EmailContent generateWeeklyReminder(WorkflowUser user, Collection<ActionItem> actionItems) {
        StringBuffer sf = new StringBuffer();
        sf.append(getDailyWeeklyMessageBody(actionItems));
        sf
                .append("To change how these email notifications are sent (immediately, daily or none): \n");
        sf.append("\tGo to " + getPreferencesUrl() + "\n");
        // sf.append("\tSend as soon as you get an eDoc\n\t" +
        // getPreferencesUrl() + "\n\n");
        // sf.append("\tSend daily\n\t" + getPreferencesUrl() + "\n\n");
        // sf.append("\tDo not send\n\t" + getPreferencesUrl() + "\n");
        sf.append("\n\n\n");
        sf.append(getHelpLink() + "\n\n\n");

        EmailContent content = new EmailContent();
        content.setBody(sf.toString(), false);
        content.setSubject(getEmailSubject());

        return content;
    }

    /**
     * @see edu.iu.uis.eden.feedback.web.FeedbackAction
     * @param form feedback form from action
     * @return email content
     */
    public EmailContent generateFeedback(FeedbackForm form) {
        EmailContent content = new EmailContent();
        content.setBody(constructFeedbackEmailBody(form), false);
        content.setSubject(constructFeedbackSubject(form));
        return content;
    }

    // ---- helper methods

    public String getDailyWeeklyMessageBody(Collection actionItems) {
        StringBuffer sf = new StringBuffer();
        HashMap docTypes = getActionListItemsStat(actionItems);

        sf
                .append("Your OneStart Action List has "
                        + actionItems.size()
                        + " eDocs(electronic documents) that need your attention: \n\n");
        Iterator iter = docTypes.keySet().iterator();
        while (iter.hasNext()) {
            String docTypeName = (String) iter.next();
            sf.append("\t" + ((Integer) docTypes.get(docTypeName)).toString()
                    + "\t" + docTypeName + "\n");
        }
        sf.append("\n\n");
        sf.append("To respond to each of these eDocs: \n");
        sf
                .append("\tGo to "
                        + getActionListUrl()
                        + ", and then click on its numeric Document ID in the first column of the List.\n");
        sf.append("\n\n\n");
        return sf.toString();
    }

    private HashMap getActionListItemsStat(Collection actionItems) {
        HashMap docTypes = new LinkedHashMap();
        Iterator iter = actionItems.iterator();

        while (iter.hasNext()) {
            String docTypeName = ((ActionItem) iter.next()).getRouteHeader()
                    .getDocumentType().getName();
            if (docTypes.containsKey(docTypeName)) {
                docTypes.put(docTypeName, new Integer(((Integer) docTypes
                        .get(docTypeName)).intValue() + 1));
            } else {
                docTypes.put(docTypeName, new Integer(1));
            }
        }
        return docTypes;
    }

    private String getHelpLink() {
        return getHelpLink(null);
    }

    private String getHelpLink(DocumentType documentType) {
        return "For additional help, email " + "<mailto:" + getDocumentTypeEmailAddress(documentType) + ">";
    }

    private String getEmailSubject() {
        return defaultReminderSubject;
    }

    private String getEmailSubject(String customSubject) {
        return defaultReminderSubject + " " + customSubject;
    }

    /**
     * @see edu.iu.uis.eden.feedback.web.FeedbackAction
     * @param form feedback form from action
     * @return email content
     */
    private String constructFeedbackSubject(FeedbackForm form) {
        String subject = "Feedback from " + form.getNetworkId();
        if (form.getRouteHeaderId() != null) {
            subject += (" for document " + form.getRouteHeaderId());
        }
        return subject;
    }

    /**
     * @see edu.iu.uis.eden.feedback.web.FeedbackAction
     * @param form feedback form from action
     * @return email content
     */
    private String constructFeedbackEmailBody(FeedbackForm form) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("\n");
        buffer.append("Network ID: " + form.getNetworkId()).append("\n");
        buffer.append("Name: " + form.getUserName()).append("\n");
        buffer.append("Email: " + form.getUserEmail()).append("\n");
        buffer.append("Phone: " + form.getPhone()).append("\n");
        buffer.append("Time: " + form.getTimeDate()).append("\n");
        buffer.append("Environment: " + Core.getCurrentContextConfig().getEnvironment()).append("\n\n");
        
        buffer.append("Document type: " + form.getDocumentType()).append("\n");
        buffer.append("Document id: " + (form.getRouteHeaderId() != null ? form.getRouteHeaderId() : "")).append("\n\n");
        
        buffer.append("Category: " + form.getEdenCategory()).append("\n");
        buffer.append("Comments: \n" + form.getComments()).append("\n\n");
        
        buffer.append("Exception: \n" + form.getException());
        return buffer.toString();
    }

    private boolean isProduction() {
        return KEWConstants.PROD_DEPLOYMENT_CODE.equals(getDeploymentEnvironment());
    }
}