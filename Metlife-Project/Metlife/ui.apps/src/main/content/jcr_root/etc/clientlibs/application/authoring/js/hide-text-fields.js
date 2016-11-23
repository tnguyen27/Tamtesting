Metlife.Components.HideTextFields = {
    selectionChanged: function (selection, value, isChanged) {
		var panel = selection.findParentByType('panel'),
		    oneItemsFieldset = panel.find('item-id', '1-item')[0],
            twoItemsFieldset = panel.find('item-id', '2-items')[0],
            threeItemsFieldset = panel.find('item-id', '3-items')[0];
            fourItemsFieldset = panel.find('item-id', '4-item')[0];
            fiveItemsFieldset = panel.find('item-id', '5-item')[0];
            sixItemsFieldset = panel.find('item-id', '6-item')[0];
        if (value === 'linkText') {
            twoItemsFieldset.hide();
            oneItemsFieldset.show();
            threeItemsFieldset.hide();
            fourItemsFieldset.show();
            fiveItemsFieldset.show();
            sixItemsFieldset.hide();
        } else if (value === 'numberText') {
            oneItemsFieldset.hide();
            twoItemsFieldset.show();
            threeItemsFieldset.hide();
            fourItemsFieldset.hide();
            fiveItemsFieldset.hide();
            sixItemsFieldset.show();
        }else if (value === 'textOnly'){
            oneItemsFieldset.hide();
            twoItemsFieldset.hide();
            threeItemsFieldset.show();
            fourItemsFieldset.hide();
            fiveItemsFieldset.hide();
            sixItemsFieldset.hide();
        }
    },
    loadContent: function(field, record, path) {
		var value = field.getValue(),
			panel = field.findParentByType('panel'),
            oneItemsFieldset = panel.find('item-id', '1-item')[0],
            twoItemsFieldset = panel.find('item-id', '2-items')[0],
            threeItemsFieldset = panel.find('item-id', '3-items')[0];
            fourItemsFieldset = panel.find('item-id', '4-item')[0];
            fiveItemsFieldset = panel.find('item-id', '5-item')[0];
            sixItemsFieldset = panel.find('item-id', '6-item')[0];
        if (value === '') {
            oneItemsFieldset.hide();
            twoItemsFieldset.hide();
            threeItemsFieldset.hide();
            fourItemsFieldset.hide();
            fiveItemsFieldset.hide();
            sixItemsFieldset.hide();
        } else if (value === 'linkText') {
            oneItemsFieldset.show();
            twoItemsFieldset.hide();
            threeItemsFieldset.hide();
            fourItemsFieldset.show();
            fiveItemsFieldset.show();
            sixItemsFieldset.hide();
        } else if (value === 'numberText'){
            oneItemsFieldset.hide();
            twoItemsFieldset.show();
            threeItemsFieldset.hide();
            fourItemsFieldset.hide();
            fiveItemsFieldset.hide();
            sixItemsFieldset.show();
        }else if (value === 'textOnly'){
            oneItemsFieldset.hide();
            twoItemsFieldset.hide();
            threeItemsFieldset.show();
            fourItemsFieldset.hide();
            fiveItemsFieldset.hide();
            fiveItemsFieldset.hide();
            sixItemsFieldset.hide();
            sixItemsFieldset.hide();
        }
    }

};