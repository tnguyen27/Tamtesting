MetlifeMicrosites.Components.hidePathfield = {
    selectionChanged: function (selection, value, isChecked, prefixId) {

            var tabPanel = selection.findParentByType('tabpanel'),
                panel = selection.findParentByType('panel'),
                pdfLink = panel.getComponent(prefixId + 'pdf'),
                pathLink = panel.getComponent(prefixId + 'link');
            if (isChecked){
                pathLink.show();
                pdfLink.hide();
            }else{
                pathLink.hide();
                pdfLink.show();
            }
    	},
        loadContent: function(field, record, path, prefixId) {
            var value = field.getValue(),
                tabPanel = field.findParentByType('tabpanel'),
                panel = field.findParentByType('panel'),
                pdfLink = panel.getComponent(prefixId + 'pdf'),
                pathLink = panel.getComponent(prefixId + 'link');
            if (value.length == 1){
                pathLink.show();
                pdfLink.hide();
            }else{
                pathLink.hide();
                pdfLink.show();
            }
        }

};