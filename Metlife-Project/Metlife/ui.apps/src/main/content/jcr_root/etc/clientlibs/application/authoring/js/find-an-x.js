Metlife.Components.FindAnX = {
    selectionChanged: function (selection, value, isChanged) {

		var panel = selection.findParentByType('panel'),
            configurationTab = panel.findParentByType('panel').find('item-id', 'configuration-tab')[0],
            pathField = panel.find('item-id', 'path-field')[0];
        if (value === 'custom') {
            pathField.show();
            configurationTab.disable();
        } else if(value === 'default') {
            pathField.hide();
            configurationTab.enable();
        } else {
            pathField.hide();
            configurationTab.disable();
        }
    },
    loadContent: function(field, record, path) {

		var value = field.getValue(),
			panel = field.findParentByType('panel'),
            configurationTab = panel.findParentByType('panel').find('item-id', 'configuration-tab')[0],
            pathField = panel.find('item-id', 'path-field')[0];
        if (value === 'custom') {
            pathField.show();
            configurationTab.disable();
        } else if(value === 'default') {
            pathField.hide();
            configurationTab.enable();
        } else {
            pathField.hide();
            configurationTab.disable();
        }
    }

};