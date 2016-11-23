Metlife.Components.RelatedContent = {
    selectionChanged: function (selection, value, isChanged) {
		var panel = selection.findParentByType('panel'),
		    oneItemsFieldset = panel.find('item-id', '1-item')[0],
            twoItemsFieldset = panel.find('item-id', '2-items')[0],
            threeItemsFieldset = panel.find('item-id', '3-items')[0];
        if (value === '2-items') {
            twoItemsFieldset.show();
            oneItemsFieldset.hide();
            threeItemsFieldset.hide();
        } else if (value === '3-items') {
            oneItemsFieldset.hide();
            twoItemsFieldset.hide();
            threeItemsFieldset.show();
        }else if (value === '1-item'){
            oneItemsFieldset.show();
            twoItemsFieldset.hide();
            threeItemsFieldset.hide();
        }
    },
    loadContent: function(field, record, path) {
		var value = field.getValue(),
			panel = field.findParentByType('panel'),
            oneItemsFieldset = panel.find('item-id', '1-item')[0],
            twoItemsFieldset = panel.find('item-id', '2-items')[0],
            threeItemsFieldset = panel.find('item-id', '3-items')[0];

        if (value === '2-items') {
            oneItemsFieldset.hide();
            twoItemsFieldset.show();
            threeItemsFieldset.hide();
        } else if (value === '3-items'){
            oneItemsFieldset.hide();
            twoItemsFieldset.hide();
            threeItemsFieldset.show();
        }else if (value === '1-item'){
            oneItemsFieldset.show();
            twoItemsFieldset.hide();
            threeItemsFieldset.hide();
        }
    }

};