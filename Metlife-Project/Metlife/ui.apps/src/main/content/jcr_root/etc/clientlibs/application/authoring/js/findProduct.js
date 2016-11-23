//Behavior when is checked or unchecked checkbox to find products.
Metlife.Components.findProduct = {
    selectionChanged: function (selection, value, isChecked) {
        var panel = selection.findParentByType('panel'),
            products = panel.getComponent('products'),
            simpleProducts = panel.getComponent('simpleProducts');
        if (isChecked) {
            products.hide();
            simpleProducts.show();
        } else {
            simpleProducts.hide();
            products.show();
        }
    },
    loadContent: function(field,record,path) {
        var panel = field.findParentByType('panel'),
            products = panel.getComponent("products"),
            simpleProducts = panel.getComponent("simpleProducts");
        if (field.getValue().length != 0) {
            products.hide();
            simpleProducts.show();
        } else {
            simpleProducts.hide();
            products.show();
        }
    }
};
