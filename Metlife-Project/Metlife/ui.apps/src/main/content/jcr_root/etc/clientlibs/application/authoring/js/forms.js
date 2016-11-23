
Metlife.Forms.Observes = {
    selectionChanged: function (selection, value, isChecked) {
        var tabPanel = selection.findParentByType('tabpanel'),
            observesTab = tabPanel.getComponent('observesTab');
        if (isChecked){
            tabPanel.unhideTabStripItem(observesTab);
        }else{
            tabPanel.hideTabStripItem(observesTab);
        }
	},
    loadContent: function(field, record, path) {
        var value = field.getValue(),
            tabPanel = field.findParentByType('tabpanel'),
            panel = field.findParentByType('panel'),
            observesTab = tabPanel.getComponent('observesTab');
        if (!value.length){
            tabPanel.hideTabStripItem(observesTab);
        }
    },
    observesOptionsProvider : function(path, record){
        return JSON.parse(CQ.shared.HTTP.get(CQ.HTTP.getPath()+"/_jcr_content/container.observes.json").responseText);

    },
    beforeObserveValueLoad : function(field, record, path){
        var tabPanel = field.findParentByType('tabpanel'),
            observesTab = tabPanel.getComponent('observesTab'),
            observeField = observesTab.getComponent('observeField');
        field.setOptions(JSON.parse(CQ.shared.HTTP.get(observeField.getValue()+".textValueOptions.json").responseText));
        field.fireEvent('loadcontent', field, record, path);
        return false;
    },
    observeFieldChange: function (selection, value, isChecked) {
        var tabPanel = selection.findParentByType('tabpanel'),
            observesTab = tabPanel.getComponent('observesTab'),
            field = observesTab.getComponent('observeValue');
            try{
                field.setValue("");
                field.setOptions(JSON.parse(CQ.shared.HTTP.get(value+".textValueOptions.json").responseText));
                field.focus(true)
            }catch(e){
                console.log(e);
            }
            field.fireEvent('beforeloadcontent', field, record, path);

    }
};
