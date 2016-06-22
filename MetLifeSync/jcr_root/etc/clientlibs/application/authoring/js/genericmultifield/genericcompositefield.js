/**
 * @class CQ.form.GenericCompositeField
 * @extends CQ.form.CompositeField
 *
 * The <code>CQ.form.GenericCompositeField</code> class allows multiple input
 * fields to represent a single entry for editing multi value properties
 * (in GenericMultiField).
 *
 * @constructor
 * Creates a new Generic CompositeField
 * @param {Object} config The config object
 */
CQ.form.GenericCompositeField = CQ.Ext.extend(CQ.form.CompositeField, {

    /**
     * 
     */
    constructor: function(config) {
        this.config = config || { };
        this.componentItems = new Array();
        
        var defaults = {
            "border": false,
            "layout": "form",
            "layoutConfig": {
                "columns" : "1"
            }           
        };        
        this.config = CQ.Util.applyDefaults(config, defaults);
        CQ.form.GenericCompositeField.superclass.constructor.call(this, config);
    },

    /**
     * 
     */
     
    initComponent: function() {                
        
        CQ.form.GenericCompositeField.superclass.initComponent.call(this,arguments);
        
        for (var i = 0; i < this.config.length; i++) {  
            var name = this.config[i].name.replace("./", "");
            this.componentItems[name] = this.add(this.config[i]);   
            
            this.componentItems[name].width="85%";
            if( CQ.Ext.isSafari ) {
                this.componentItems[name].width="85%";
            }

            if( this.componentItems[name].isXType("trigger") && !this.componentItems[name].style ) {
                this.componentItems[name].anchor = "83%";                
                if( CQ.Ext.isSafari ) {
                    this.componentItems[name].anchor = "85%";
                }
            }
            if( this.componentItems[name].isXType("selection") ) {
                this.componentItems[name].width = "70%";
                this.componentItems[name].style = "width:70%";
            }
            
        }
        
        //this.callParent();
        
    },
    
    
    
    /**
     * 
     */
    processRecord: function(record, path) {
        for (var c in this.componentItems) {
            if (typeof(this.componentItems[c]) === 'function') {
                continue;
            }
            if (this.componentItems[c].processInit) {
                this.componentItems[c].processInit(path, record);
            }
            if (!this.componentItems[c].initialConfig.ignoreData) {
                this.componentItems[c].processRecord(record, path);
            }                   
        }
    },

    /**
     * 
     */
    setValue: function(value) {             
        for( var i = 0; i < this.config.length; i++ ) {
            this.componentItems[i].setValue(value.items.get(i).getValue());
        }
    },

    /**
     * 
     */
    getValue: function() {
        var returnArray = new Array();
        for (var i = 0; i < this.componentItems.length; i++) {
            returnArray[i] = this.componentItems[i].getValue();
        }
        return returnArray;
    }

});

// register xtype
CQ.Ext.reg("genericcompositefield", CQ.form.GenericCompositeField);