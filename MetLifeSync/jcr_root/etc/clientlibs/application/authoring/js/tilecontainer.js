
Metlife.Components.TileContainer = {
    selectionChanged: function (selection, value, isChanged) {
		var panel = selection.findParentByType('panel'),
            twoItemsFieldset = panel.find('item-id', '2-items')[0],
            threeItemsFieldset = panel.find('item-id', '3-items')[0];
        if (value === '2-items') {
            twoItemsFieldset.show();
            threeItemsFieldset.hide();
        } else if (value === '3-items') {
            twoItemsFieldset.hide();
            threeItemsFieldset.show();
        }else if (value === '1-item'){
            twoItemsFieldset.hide();
            threeItemsFieldset.hide();
        }
    },
    loadContent: function(field, record, path) {
		var value = field.getValue(),
			panel = field.findParentByType('panel'),
            twoItemsFieldset = panel.find('item-id', '2-items')[0],
            threeItemsFieldset = panel.find('item-id', '3-items')[0];

        if (value === '2-items') {
            twoItemsFieldset.show();
            threeItemsFieldset.hide();
        } else if (value === '3-items'){
            twoItemsFieldset.hide();
            threeItemsFieldset.show();
        }else if (value === '1-item'){
            twoItemsFieldset.hide();
            threeItemsFieldset.hide();
        }
    }

};
