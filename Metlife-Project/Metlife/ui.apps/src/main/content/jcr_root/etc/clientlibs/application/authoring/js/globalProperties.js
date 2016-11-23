//Behavior for the global properties.
Metlife.Components.loginType = {
    selectionChanged: function (selection, value, isChecked) {
        var panel = selection.findParentByType('panel'),
            loginPagePathfield = panel.getComponent('loginPagePathfield'),
            loginTarget = panel.getComponent('loginTarget');
            loginConfigurationPathfield = panel.getComponent('loginConfigurationPathfield');
        if (selection.getValue() == "form") {
            loginConfigurationPathfield.show();
            loginPagePathfield.hide();
            loginTarget.hide();
        } else {
            loginConfigurationPathfield.hide();
            loginPagePathfield.show();
            loginTarget.show();
        }
		loginTarget.doLayout(true,true);
    },
    loadcontent: function(field,record,path) {
        var panel = field.findParentByType('panel'),
                    loginPagePathfield = panel.getComponent('loginPagePathfield'),
                    loginTarget = panel.getComponent('loginTarget');
                    loginConfigurationPathfield = panel.getComponent('loginConfigurationPathfield');
                if (field.getValue() == "form") {
                    loginConfigurationPathfield.show();
                    loginPagePathfield.hide();
                    loginTarget.hide();
                } else {
                    loginConfigurationPathfield.hide();
                    loginPagePathfield.show();
                    loginTarget.show();
                }
    }
}