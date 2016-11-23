MetlifeCommons.Components.HideFieldTexts = {
    selectionChanged: function (selection, value, isChanged) {
		var panel = selection.findParentByType('panel'),
		    oneItemsFieldset = panel.find('item-id', '1-item')[0],
            twoItemsFieldset = panel.find('item-id', '2-items')[0],
            threeItemsFieldset = panel.find('item-id', '3-items')[0];
        if (value === 'option_a') {
            twoItemsFieldset.hide();
            oneItemsFieldset.show();
            threeItemsFieldset.show();
        }else if (value === 'option_b'){
            oneItemsFieldset.hide();
            twoItemsFieldset.show();
            threeItemsFieldset.hide();
        }
    },
    loadContent: function(field, record, path) {
		var value = field.getValue(),
			panel = field.findParentByType('panel'),
            oneItemsFieldset = panel.find('item-id', '1-item')[0],
            twoItemsFieldset = panel.find('item-id', '2-items')[0],
            threeItemsFieldset = panel.find('item-id', '3-items')[0];

        if (value === 'option_a') {
            twoItemsFieldset.hide();
            oneItemsFieldset.show();
            threeItemsFieldset.show();
        }else if (value === 'option_b'){
            oneItemsFieldset.hide();
            twoItemsFieldset.show();
            threeItemsFieldset.hide();
        }
    }
};