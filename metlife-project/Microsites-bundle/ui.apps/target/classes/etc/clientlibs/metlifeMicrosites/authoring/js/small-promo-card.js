MetlifeMicrosites.Components.SmallPromoCard = {
    selectionChanged: function (selection, value, isChecked) {

        var tabPanel = selection.findParentByType('tabpanel'),
            panel = selection.findParentByType('panel'),
            manualTab = tabPanel.getComponent('manualTab');
        if (isChecked){
            tabPanel.unhideTabStripItem(manualTab);
        }else{
            tabPanel.hideTabStripItem(manualTab);
        }
	},
    loadContent: function(field, record, path) {
        var value = field.getValue(),
            tabPanel = field.findParentByType('tabpanel'),
            panel = field.findParentByType('panel'),
            manualTab = tabPanel.getComponent('manualTab');
        if (!value.length){
            tabPanel.hideTabStripItem(manualTab);
        }else{
            tabPanel.unhideTabStripItem(manualTab);
        }
    }
};