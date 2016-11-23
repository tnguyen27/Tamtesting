/**
 * @class CQ.form.GenericMultiField
 * @extends CQ.form.CompositeField
 * <p>The GenericMultiField allows to input question and answer pairs and to manage the ordering of the Q/A pairs.</p>
 * @constructor
 * Creates a new Generic MultiField.
 * @param {Object} config The config object
 */
CQ.form.GenericMultiField = CQ.Ext.extend(CQ.form.CompositeField, {

    /**
     * @cfg {Object} fieldConfig
     * The configuration options for the fields (optional).
     */
    fieldConfig: null,
    
    /**
     * @cfg {Object} numberOfItems 
     */
    numberOfItems: null,
    
    /**
     * @cfg {Object}
     * The name of the widget, NOT the name of the individual
     * fields that comprise this widget
     */
    genericMultiFieldName: null,
    
    /**
     * @cfg {Boolean} storeIndexPos
     * <code>TRUE</code> to store index position, mainly used with
     * second level links for left navigation component
     */
    storeIndexPos: false,
    
    /**
     * @cfg {Number} maxItems Maximum number of items that can be created (defaults to 25).     
     */
    maxItems: 50,
     
    /**
     * Creates a new <code>CQ.form.GenericMultiField</code>.
     * @constructor
     * @param {Object} config The config object
     */
    constructor: function(config) {
        var list = this;
        
        if (!config.genericMultiFieldName) {
            config.genericMultiFieldName = "./numberofitems";
        }
        
        if (!config.value) {
            config.value = 0;
        }
        
        if (!config.fieldConfig) {
            config.fieldConfig = {};
        }
        if (!config.fieldConfig.xtype) {
            config.fieldConfig.xtype = "genericcompositefield";
        }
        config.fieldConfig.name = config.name;
        config.fieldConfig.style = "width:95%;";
        var items = new Array();
        
        if (!config.addItemLabel) {
            config.addItemLabel = CQ.I18n.getMessage("Add Item");
        }
        
        this.numberOfItems = new CQ.Ext.form.Hidden({
            "name": config.genericMultiFieldName,
            "value": config.value
        });
        items.push(this.numberOfItems);
        
        if(config.readOnly) {            
            config.fieldConfig.readOnly = true;
        } else {
            items.push({
                xtype: "toolbar",
                cls: "cq-multifield-toolbar",
                items: [
                    "->",
                    {
                        xtype: "textbutton",
                        text: config.addItemLabel,
                        style: "padding-right:6px",
                        handler:function() {
                            list.addItem();
                        }
                    },
                    {
                        xtype: "button",
                        iconCls: "cq-multifield-add",
                        template: new CQ.Ext.Template('<span><button class="x-btn" type="{0}"></button></span>'),
                        handler: function() {
                            list.addItem();
                        }
                    }
                ]
            });
        }

        
        this.genericMultiFieldName = config.genericMultiFieldName;
        this.fieldConfig = config.fieldConfig;
        
        for (var i = 0; i < this.fieldConfig.length; i++) {
                items.push({
                    "xtype":"hidden",
                    "name":this.fieldConfig[i].name + CQ.Sling.DELETE_SUFFIX
                });
        }  
        items.push({ 
            "xtype":"hidden",
            "name":config.genericMultiFieldName + CQ.Sling.DELETE_SUFFIX
        });
        
        
        config = CQ.Util.applyDefaults(config, {
            "defaults":{
                "xtype":"genericmultifielditem",
                "fieldConfig":config.fieldConfig,
                "storeIndexPos": false,
                "maxItems": 25
            },
            "items":[
                {
                    "xtype":"panel",
                    "border":false,
                    "bodyStyle":"padding:4px",
                    "items":items
                }
            ]
        });

        if (config.storeIndexPos) this.storeIndexPos = config.storeIndexPos;
        if (config.maxItems) this.maxItems = config.maxItems;
        
        CQ.form.GenericMultiField.superclass.constructor.call(this,config);
        if (this.defaults.fieldConfig.regex) {            
            this.defaults.fieldConfig.regex = config.fieldConfig.regex;
        }
        this.addEvents(
            /**
             * @event change
             * Fires when the value is changed.
             * @param {CQ.form.GenericMultiField} this
             * @param {Mixed} newValue The new value
             * @param {Mixed} oldValue The original value
             */
            "change"
        );
        
        this.on("render",function() {
            this.parentDialog = this.findParentByType("dialog");
            if(this.parentDialog) {
                
                this.parentDialog.on("beforeSubmit", function(path, record) {
                    if( list.storeIndexPos ) {
                        list.resetCustomValue();
                    }
                }, this.parentDialog);
            }
        },this);

        this.findParentByType("panel").addListener("show", function() {
            var tmp = this.findByType("genericmultifielditem");
            var multifield = null;
            
            if (tmp.length > 0){
                multifield = tmp[0];
            }
            
            for (var j=0; j<tmp.length; j++){
                
                multifield = tmp[j]; 
                if (multifield != null){
                    multifield.doLayout();
                    var itemsTmp = multifield.findByType("pathfield");
                    for ( var i = 0; i < itemsTmp.length; i ++ ) {
                        var itemX = itemsTmp[i];
                        var triggerX = itemX.trigger;
                        if (triggerX != null){
                            if (triggerX.getWidth() > 15) {
                                itemX.setWidth('95%');
                            }
                        }
                    }
                }
            }
            
            /*
            if ( tmp.length > 0 ) multifield = tmp[0];
            if (multifield != null) {
                multifield.doLayout();
                var itemsTmp = multifield.findByType("multifielditem2");
                for ( var i = 0; i < itemsTmp.length; i ++ ) {
                    var item = itemsTmp[i];
                    if (item.field.isXType("trigger")) {
                        if (item.field2.getWidth() > 15) {
                            item.field.setWidth(item.field2.getWidth());
                        } else {
                            item.field.setWidth(134);
                        }
                        item.field.wrap.setWidth(item.field.getWidth()+"px");
                    }
                    if (item.field2.isXType("trigger")) {
                        if (item.field.getWidth() > 15) {
                            item.field2.setWidth(item.field.getWidth());
                        } else {
                            item.field2.setWidth(134);
                        }
                        item.field2.wrap.setWidth(item.field2.getWidth()+"px");
                    }
                }
            }
            */
            
            /**/
        });


    },

    /**
     * Validates a value according to the field's validation rules and marks the field as invalid
     * if the validation fails
     * @param {Mixed} value The value to validate
     * @return {Boolean} True if the value is valid, else false
     */
    validateValue : function(value) {
        var isValid = true;
        var currentObj = this;
        var dialogFields = CQ.Util.findFormFields(currentObj);
        for (var name in dialogFields) {
            for (var i = 0; i < dialogFields[name].length; i++) {
                // only validate values if "OK" button clicked if inside genericcompositefield
                if( dialogFields[name][i].xtype == "genericcompositefield" || dialogFields[name][i].isXType("genericcompositefield") ) {
                    var compositeField = dialogFields[name][i];
                    // FOR loop to loop through each field that comprises one genericcompositefield
                    for( var x = 0; x < compositeField.items.getCount(); x++ ) {

                        // if field (part of one genericcompositefield) is required and no value is
                        // input, mark invalid and do NOT pass validation (error message should be displayed)
                        if( !compositeField.items.get(x).allowBlank && compositeField.items.get(x).getValue().length < 1 ) {
                            compositeField.items.get(x).markInvalid(compositeField.items.get(x).blankText);
                            isValid = false;
                        }
                    }  // end FOR loop of genericcompositefields
                }  // end IF statement - only if genericcompositefield
            }  // end OUTER FOR loop to loop through all fields in dialog
        }  // end OUTER OUTER FOR loop
        return isValid;        
    },
    
    /**
     * Called from constructor, on "beforeSubmit" listener, when the author
     * clicks the "OK" button in the dialog. Setting some values to be saved
     * (usually hidden fields, to support leftnav component). Called and 
     * values set before the actual submit/dialog save.
     */
    resetCustomValue: function() {        
        var currentObj = this;
        var dialogFields = CQ.Util.findFormFields(currentObj);
        for (var name in dialogFields) {
            var indexNum = 0;
            for (var i = 0; i < dialogFields[name].length; i++) {
                // only adjust values before "OK" post if inside genericcompositefield
                if( dialogFields[name][i].xtype == "genericcompositefield" || dialogFields[name][i].isXType("genericcompositefield") ) {
                    var compositeField = dialogFields[name][i];
                    // for loop to loop through each field that comprises one genericcompositefield
                    for( var x = 0; x < compositeField.items.getCount(); x++ ) {
        
                        var originalValue = compositeField.items.get(x).getValue();
                        var linkTextValue;
                        if( compositeField.items.get(x).name == "./secondLevelLinkText" ) {
                            linkTextValue = originalValue;
                            linkTextValue = linkTextValue.replace(/ /g,"-").toLowerCase();
                        }
                        if( compositeField.items.get(x).name == "./secondLevelIndex" 
                             && (!originalValue || originalValue == null || originalValue == '') ) {
                            compositeField.items.get(x).setRawValue(indexNum);
                            compositeField.items.get(x).setValue(indexNum);
                        }
                        // store version of link text (without spaces) in hidden field so
                        // can match the link text item by looping through the array and
                        // matching this hidden field (which indicates which link text in the
                        // array it is). From this, can grab the index of the array and
                        // then can inherit from parent or above. kind of convoluted, but
                        // only way it can work based on the leftnav, leftnavitem, leftnavparsys components
                        if( compositeField.items.get(x).name == "./secondLevelHolderName" 
                             && (!originalValue || originalValue == null || originalValue == '') ) {
                            compositeField.items.get(x).setRawValue("node_holder_"+linkTextValue);
                            compositeField.items.get(x).setValue("node_holder_"+linkTextValue);
                        }
                    }  // end FOR loop of genericcompositefields
                    indexNum++;
                }  // end IF statement - only if genericcompositefield
            }  // end OUTER FOR loop to loop through all fields in dialog
        }  // end OUTER OUTER FOR loop
    },
    
    /**
     * Adds a new field to the widget.
     * @param value The value of the field
     */
    addItem: function(value) {        
        if( this.items.getCount() > this.maxItems ) {
            var msg = CQ.I18n.getMessage("You can only create a maximum of ") + this.maxItems + CQ.I18n.getMessage(" items.");
            CQ.Ext.Msg.show({
                "title": CQ.I18n.getMessage("Create New Item"),
                "msg": msg,
                "buttons": CQ.Ext.Msg.OK
            }); 
        } else {
            var item = this.insert(this.items.getCount() - 1, {});
            this.findParentByType("form").getForm().add(item.field);
            this.doLayout();
    
            if (value) {
                item.setValue(value);
            }
            if (item.field.isXType("trigger")) {
                item.field.wrap.setWidth("95%");
            }
                
            this.setNumberOfItems();
            return item;
        }
    },
    
    /**
     * Returns the data value.
     * @return {String[]} value The field value
     */
    getValue: function() {
        var value = new Array();
        this.items.each(function(item, index/*, length*/) {
            if (item instanceof CQ.form.GenericMultiField.Item) {
                value[index] = item.getValue();
                index++;
            }
        }, this);
        return value;
    },

    /**
     * Sets a data value into the field and validates it.
     * @param {Mixed} value The value to set
     */
    setValue: function(value) {
         
        this.fireEvent("change", this, value, this.getValue());   
        this.doLayout();
        if ((value != null) && (value != "")) {
            if (value instanceof Array || CQ.Ext.isArray(value)) {
              for (var i = 0; i < value.length; i++) {
                  this.addItem(value[i]);
              }
          } else {
            this.addItem(value);
          }
        }
    },
     
    /**
     * Returns the number of separate items
     * @return {int} value The field value
     */
    getNumberOfItems: function() {
        return this.items.getCount() - 1;
    },
      
    /**
     * Sets the number of separate items
     * @param {int} num
     */
    setNumberOfItems: function(num) {
        if (!num) {
            this.numberOfItems.setValue(this.getNumberOfItems()); 
        } else {
            this.numberOfItems.setValue(num);
        }
    },
    
    // This function allows the saved nodes to be retrieved and
    // displayed in the dialog when re-opening the dialog.
    // This retrieves the correct input fields but the actual
    // information inside the input fields (e.g. the actual name
    // of the contact) is retrieved via the other processRecord() in the item.
    processRecord: function(record, path) {   
        var oldItems = this.items;
        var values = new Array();
         oldItems.each(function(item/*, index, length*/) {
             if (item instanceof CQ.form.GenericMultiField.Item) {
                 this.remove(item, true);
                 this.findParentByType("form").getForm().remove(item);
             }
         }, this);                 
  
         var numItems = record.get(this.genericMultiFieldName);  

         if (!numItems) {
            numItems = 0;
         }
         
         if (numItems == 1) {
                var item = this.addItem();
                item.processRecord(record, path);
         } else {
            for (var i = 0; i < numItems; i++ ) {
                var newRecord = record.copy();
                for (c in newRecord.data) {                 
                    if (CQ.Ext.isArray(newRecord.data[c])) {
                        newRecord.data[c] = newRecord.data[c][i];           
                    }
                }
                var item = this.addItem();
                item.processRecord(newRecord, path);
            }
         }
     }

});

CQ.Ext.reg("genericmultifield", CQ.form.GenericMultiField);

/**
 * The <code>CQ.form.GenericMultiField.Item</code> class represents an item in a
 * <code>CQ.form.GenericMultiField</code>. This class is not intended for direct use.
 *
 * @private
 * @class CQ.form.GenericMultiField.Item
 * @extends CQ.Ext.Panel
 */
CQ.form.GenericMultiField.Item = CQ.Ext.extend(CQ.Ext.Panel, {

    
    /**
     * Creates a new <code>CQ.form.GenericMultiField.Item</code>.
     * @constructor
     * @param {Object} config The config object
     */
    constructor: function(config) {
        
        var item = this;        
        this.field = CQ.Util.build(config.fieldConfig, true);

        var items = new Array();
        items.push({
            "xtype":"panel",
            "border":false,
            "cellCls":"cq-multifield-itemct",
            "items":item.field
        });

        if(!config.fieldConfig.readOnly) {
           items.push({
                    "xtype": "panel",
                    "border": false,
                    "items": {
                        "xtype": "button",
                        "iconCls": "cq-multifield-up",
                        "template": new CQ.Ext.Template('<span><button class="x-btn" type="{0}"></button></span>'),
                        "handler": function(){
                            var parent = item.ownerCt;
                            var index = parent.items.indexOf(item);

                            if (index > 0) {
                                item.reorder(parent.items.itemAt(index - 1));
                            }
                        }
                    }
                });
                items.push({
                    "xtype": "panel",
                    "border": false,
                    "items": {
                        "xtype": "button",
                        "iconCls": "cq-multifield-down",
                        "template": new CQ.Ext.Template('<span><button class="x-btn" type="{0}"></button></span>'),
                        "handler": function(){
                            var parent = item.ownerCt;
                            var index = parent.items.indexOf(item);

                            if (index < parent.items.getCount() - 1) {
                                item.reorder(parent.items.itemAt(index + 1));
                            }
                        }
                    }
                });
                
                ////////////
                
                items.push({
                "xtype":"panel",
                "border":false,
                "items":{
                    "xtype":"button",
                    "iconCls": "cq-multifield-remove",
                    "template": new CQ.Ext.Template('<span><button class="x-btn" type="{0}"></button></span>'),
                    "handler":function() {
                        var parent = item.ownerCt;
                        var numItems = parent.items.getCount() - 1;

                        // need to actually remove all fields that comprise one
                        // entity/composite field; for example, if there is
                        // 1 browsefield, 2 textfields that = 1 image, need
                        // to loop through and remove the browsefield and 2
                        // textfields
                        for( var i = 0; i < item.field.items.getCount(); i++ ) {
                            item.ownerCt.remove(item.field.items.get(i));
                        }

                        item.ownerCt.remove(item);
                        parent.setNumberOfItems(parent.items.getCount()-1);   
                    }
                }
            });
              
        }

        config = CQ.Util.applyDefaults(config, {
            "layout":"table",
            "anchor":"100%",
            "border":false,
            "layoutConfig":{
                "columns":4
            },
            "defaults":{
                "bodyStyle":"padding:3px"
            },
            "items":items
        });
        
        CQ.form.GenericMultiField.Item.superclass.constructor.call(this, config);
        

        if (config.value) {
            this.field.setValue(config.value);
        }
    },
    
    /**
     * Reorders the item above the specified item.
     * @param item The item to reorder above
     */
    reorder: function(item) {
        // number of items (individual fields) that make up one entity/composite field;
        // for example, if there is
        // 1 browsefield, 2 textfields that = 1 image, need
        // to loop through and switch the browsefield and 2
        // textfields
        for( var i = 0; i < item.field.items.getCount(); i++) {
            var value = item.field.items.get(i).getValue();
            item.field.items.get(i).setValue(this.field.items.get(i).getValue());
            this.field.items.get(i).setValue(value);
        }
    },

    /**
     * Returns the data value.
     * @return {String} value The field value
     */
    getValue: function() {
        return this.field.getValue();
    },

    /**
     * Sets a data value into the field and validates it.
     * @param {String} value The value to set
     */
    setValue: function(value) {
        this.field.setValue(value);
    },
    
    // This function NEEDS to be used in conjunction with the processRecord()
    // function above. It retrieves the actual information from the input fields
    // (the saved text, etc. in CRX) and displays the information in the dialog
    // when re-opening the dialog
    processRecord : function(record, path) {
        this.field.processRecord(record, path);
    }
});

CQ.Ext.reg("genericmultifielditem", CQ.form.GenericMultiField.Item);