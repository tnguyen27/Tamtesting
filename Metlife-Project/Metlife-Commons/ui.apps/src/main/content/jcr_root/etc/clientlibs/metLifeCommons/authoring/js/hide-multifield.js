MetlifeCommons.Components.HideMultifield = {
    selectionChanged: function (selection, value, isChecked) {

        var panel = selection.findParentByType('panel'),
    	    oneItemsFieldset = panel.find('itemId', 'files')[0];

        if (isChecked){
            oneItemsFieldset.show();
        }else{
            oneItemsFieldset.hide();
        }
	},
    loadContent: function(field, record, path) {
       var value = field.getValue(),
       		panel = field.findParentByType('panel'),
            oneItemsFieldset = panel.find('itemId', 'files')[0];

       if (!value.length){
            oneItemsFieldset.hide();
       }else{
            oneItemsFieldset.show();
       }
    }
};