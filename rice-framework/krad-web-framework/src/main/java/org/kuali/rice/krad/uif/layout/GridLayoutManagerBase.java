/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.krad.uif.layout;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.parse.BeanTags;
import org.kuali.rice.krad.uif.CssConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.container.Container;
import org.kuali.rice.krad.uif.container.Group;

/**
 * Layout manager that organizes its components in a table based grid
 *
 * <p>
 * Items are laid out from left to right (with each item taking up one column)
 * until the configured number of columns is reached. If the item count is
 * greater than the number of columns, a new row will be created to render the
 * remaining items (and so on until all items are placed). Labels for the fields
 * can be pulled out (default) and rendered as a separate column. The manager
 * also supports the column span and row span options for the field items. If
 * not specified the default is 1.
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTags({@BeanTag(name = "gridLayout-bean", parent = "Uif-GridLayoutBase"),
        @BeanTag(name = "twoColumnGridLayout-bean", parent = "Uif-TwoColumnGridLayout"),
        @BeanTag(name = "fourColumnGridLayout-bean", parent = "Uif-FourColumnGridLayout"),
        @BeanTag(name = "sixColumnGridLayout-bean", parent = "Uif-SixColumnGridLayout")})
public class GridLayoutManagerBase extends LayoutManagerBase implements GridLayoutManager {
    private static final long serialVersionUID = 1890011900375071128L;

    private int numberOfColumns;

    private boolean suppressLineWrapping;
    private boolean applyAlternatingRowStyles;
    private boolean applyDefaultCellWidths;
    private boolean renderFirstRowHeader;
    private boolean renderAlternatingHeaderColumns;
    private boolean renderRowFirstCellHeader;

    private List<String> rowCssClasses;

    public GridLayoutManagerBase() {
        super();

        rowCssClasses = new ArrayList<String>();
    }

    /**
     * The following finalization is performed:
     *
     * <ul>
     * <li>If suppressLineWrapping is true, sets the number of columns to the
     * container's items list size</li>
     * <li>Adjust the cell attributes for the container items</li>
     * </ul>
     *
     * @see org.kuali.rice.krad.uif.layout.LayoutManagerBase#performFinalize(org.kuali.rice.krad.uif.view.View,
     *      java.lang.Object, org.kuali.rice.krad.uif.container.Container)
     */
    @Override
    public void performFinalize(Object model, Component component) {
        super.performFinalize(model, component);
        
        Container container = (Container) component;

        if (suppressLineWrapping) {
            numberOfColumns = container.getItems().size();
        }

        for (Component item : container.getItems()) {
            if (!(this instanceof TableLayoutManager)) {
                item.addWrapperCssClass("uif-gridLayoutCell");
            }
            setCellAttributes(item);
        }
    }

    /**
     * Moves the width, align, and valign settings of the component to the corresponding cell properties (if not
     * already configured)
     *
     * @param component instance to adjust settings for
     */
    protected void setCellAttributes(Component component) {
        if (StringUtils.isNotBlank(component.getWidth()) && StringUtils.isBlank(component.getCellWidth())) {
            component.setCellWidth(component.getWidth());
            component.setWidth("");
        }

        if (StringUtils.isNotBlank(component.getAlign()) && !StringUtils.contains(component.getWrapperStyle(),
                CssConstants.TEXT_ALIGN)) {
            if (component.getWrapperStyle() == null) {
                component.setWrapperStyle("");
            }

            component.setWrapperStyle(
                    component.getWrapperStyle() + CssConstants.TEXT_ALIGN + component.getAlign() + ";");
            component.setAlign("");
        }

        if (StringUtils.isNotBlank(component.getValign()) && !StringUtils.contains(component.getWrapperStyle(),
                CssConstants.VERTICAL_ALIGN)) {
            if (component.getWrapperStyle() == null) {
                component.setWrapperStyle("");
            }

            component.setWrapperStyle(
                    component.getWrapperStyle() + CssConstants.VERTICAL_ALIGN + component.getValign() + ";");
            component.setValign("");
        }
    }

    /**
     * @see LayoutManagerBase#getSupportedContainer()
     */
    @Override
    public Class<? extends Container> getSupportedContainer() {
        return Group.class;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.layout.GridLayoutManager#getNumberOfColumns()
     */
    @Override
    @BeanTagAttribute(name = "numberOfColumns")
    public int getNumberOfColumns() {
        return this.numberOfColumns;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.layout.GridLayoutManager#setNumberOfColumns(int)
     */
    @Override
    public void setNumberOfColumns(int numberOfColumns) {
        this.numberOfColumns = numberOfColumns;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.layout.GridLayoutManager#isSuppressLineWrapping()
     */
    @Override
    @BeanTagAttribute(name = "suppressLineWrapping")
    public boolean isSuppressLineWrapping() {
        return this.suppressLineWrapping;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.layout.GridLayoutManager#setSuppressLineWrapping(boolean)
     */
    @Override
    public void setSuppressLineWrapping(boolean suppressLineWrapping) {
        this.suppressLineWrapping = suppressLineWrapping;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.layout.GridLayoutManager#isApplyAlternatingRowStyles()
     */
    @Override
    @BeanTagAttribute(name = "applyAlternatingRowStyles")
    public boolean isApplyAlternatingRowStyles() {
        return this.applyAlternatingRowStyles;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.layout.GridLayoutManager#setApplyAlternatingRowStyles(boolean)
     */
    @Override
    public void setApplyAlternatingRowStyles(boolean applyAlternatingRowStyles) {
        this.applyAlternatingRowStyles = applyAlternatingRowStyles;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.layout.GridLayoutManager#isApplyDefaultCellWidths()
     */
    @Override
    @BeanTagAttribute(name = "applyDefaultCellWidths")
    public boolean isApplyDefaultCellWidths() {
        return this.applyDefaultCellWidths;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.layout.GridLayoutManager#setApplyDefaultCellWidths(boolean)
     */
    @Override
    public void setApplyDefaultCellWidths(boolean applyDefaultCellWidths) {
        this.applyDefaultCellWidths = applyDefaultCellWidths;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.layout.GridLayoutManager#isRenderRowFirstCellHeader()
     */
    @Override
    @BeanTagAttribute(name = "renderRowFirstCellHeader")
    public boolean isRenderRowFirstCellHeader() {
        return renderRowFirstCellHeader;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.layout.GridLayoutManager#setRenderRowFirstCellHeader(boolean)
     */
    @Override
    public void setRenderRowFirstCellHeader(boolean renderRowFirstCellHeader) {
        this.renderRowFirstCellHeader = renderRowFirstCellHeader;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.layout.GridLayoutManager#isRenderFirstRowHeader()
     */
    @Override
    @BeanTagAttribute(name = "renderFirstRowHeader")
    public boolean isRenderFirstRowHeader() {
        return renderFirstRowHeader;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.layout.GridLayoutManager#setRenderFirstRowHeader(boolean)
     */
    @Override
    public void setRenderFirstRowHeader(boolean renderFirstRowHeader) {
        this.renderFirstRowHeader = renderFirstRowHeader;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.layout.GridLayoutManager#isRenderAlternatingHeaderColumns()
     */
    @Override
    @BeanTagAttribute(name = "renderAlternatingHeaderColumns")
    public boolean isRenderAlternatingHeaderColumns() {
        return this.renderAlternatingHeaderColumns;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.layout.GridLayoutManager#setRenderAlternatingHeaderColumns(boolean)
     */
    @Override
    public void setRenderAlternatingHeaderColumns(boolean renderAlternatingHeaderColumns) {
        this.renderAlternatingHeaderColumns = renderAlternatingHeaderColumns;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.layout.GridLayoutManager#getRowCssClasses()
     */
    @Override
    @BeanTagAttribute(name = "rowCssClasses", type = BeanTagAttribute.AttributeType.LISTVALUE)
    public List<String> getRowCssClasses() {
        return rowCssClasses;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.layout.GridLayoutManager#setRowCssClasses(java.util.List)
     */
    @Override
    public void setRowCssClasses(List<String> rowCssClasses) {
        this.rowCssClasses = rowCssClasses;
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.DictionaryBeanBase#copyProperties(Object)
     */
    @Override
    protected <T> void copyProperties(T layoutManager) {
        super.copyProperties(layoutManager);

        GridLayoutManager gridLayoutManagerCopy = (GridLayoutManager) layoutManager;

        gridLayoutManagerCopy.setNumberOfColumns(this.numberOfColumns);
        gridLayoutManagerCopy.setSuppressLineWrapping(this.suppressLineWrapping);
        gridLayoutManagerCopy.setApplyAlternatingRowStyles(this.applyAlternatingRowStyles);
        gridLayoutManagerCopy.setApplyDefaultCellWidths(this.applyDefaultCellWidths);
        gridLayoutManagerCopy.setRenderFirstRowHeader(this.renderFirstRowHeader);
        gridLayoutManagerCopy.setRenderAlternatingHeaderColumns(this.renderAlternatingHeaderColumns);
        gridLayoutManagerCopy.setRenderRowFirstCellHeader(this.renderRowFirstCellHeader);

        if (rowCssClasses != null) {
            gridLayoutManagerCopy.setRowCssClasses(new ArrayList<String>(rowCssClasses));
        }
    }
}
