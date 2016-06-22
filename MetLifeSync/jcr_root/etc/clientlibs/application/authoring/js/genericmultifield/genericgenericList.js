/**
 * @class CQ.form.GenericGenericList
 * @extends CQ.form.CompositeField
 *
 * @constructor
 * Creates a new Generic GenericList
 * @param {Object} config The config object
 */
CQ.form.GenericGenericList = CQ.Ext.extend(CQ.form.CompositeField, {

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
        CQ.form.GenericGenericList.superclass.constructor.call(this, config);
    },

    
    initComponent: function() {                

        CQ.form.GenericGenericList.superclass.initComponent.call(this,arguments);

         for (var i=0; i<this.config.length; i++){           
            var name = this.config[i].name.replace("./", "");
            this.componentItems[name] = this.add(this.config[i]);
        }
        
        //this.callParent();
    },
    
    processRecord: function(record, path){
        for (var c in this.componentItems){
            if (typeof(this.componentItems[c]) === 'function'){
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

    setValue: function(value) {             
            for (var c in value){                   
                this.componentItems[c].setValue(value[c]);
            }
        },

    getValue: function() {
        var returnArray = new Array();
        for (var i = 0; i<this.componentItems.length; i++){
            returnArray[i] = this.componentItems[i].getValue();
        }
        return returnArray;
    },
    
    setParName: function(value){
        //this.items.items[2].setValue(value);
    }
    

});

// register xtype
CQ.Ext.reg('genericgenericlist', CQ.form.GenericGenericList);

