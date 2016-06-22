Metlife.Components.CategorySubnav = {
    selectionChanged: function (selection, value, isChecked) {

        var tabPanel = selection.findParentByType('tabpanel'),
            panel = selection.findParentByType('panel'),
            categories = panel.getComponent('categories'),
            manualTab = tabPanel.getComponent('manualTab');
        if (isChecked){
            tabPanel.unhideTabStripItem(manualTab);
            categories.hide();
        }else{
            tabPanel.hideTabStripItem(manualTab);
            categories.show();
        }
	},
    loadContent: function(field, record, path) {
        var value = field.getValue(),
            tabPanel = field.findParentByType('tabpanel'),
            panel = field.findParentByType('panel'),
            categories = panel.getComponent('categories'),
            manualTab = tabPanel.getComponent('manualTab');
        if (!value.length){
            tabPanel.hideTabStripItem(manualTab);
        }else{
            categories.hide();
        }
    }
};