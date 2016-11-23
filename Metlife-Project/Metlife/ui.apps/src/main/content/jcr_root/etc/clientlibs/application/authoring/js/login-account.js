Metlife.Components.LoginAccount = {
    selectionChanged: function (selection, value, isChecked) {

         var tabPanel = selection.findParentByType('tabpanel'),
            panel = selection.findParentByType('panel'),
		    individuals = panel.getComponent('individuals'),
            businesses = panel.getComponent('businesses'),
         	brokers = panel.getComponent('brokers'),
         	manualTab = tabPanel.getComponent('manualTab');

        if (isChecked){
            tabPanel.unhideTabStripItem(manualTab);
            individuals.show();
            businesses.show();
            brokers.show();
        } else {
            tabPanel.hideTabStripItem(manualTab);
            individuals.hide();
            businesses.hide();
            brokers.hide();
        }
    },
    loadContent: function(field, record, path) {
		var value = field.getValue(),
		    tabPanel = field.findParentByType('tabpanel'),
			panel = field.findParentByType('panel'),
            individuals = panel.getComponent('individuals'),
            businesses = panel.getComponent('businesses'),
            brokers = panel.getComponent('brokers');
            manualTab = tabPanel.getComponent('manualTab');

        if (!value.length){
              tabPanel.hideTabStripItem(manualTab);
              individuals.hide();
              businesses.hide();
              brokers.hide();
        } else {
            individuals.show();
            businesses.show();
            brokers.show();
        }
    }
};