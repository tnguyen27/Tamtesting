Metlife.Components.Login = {
    selectionChanged: function (selection, value, isChecked) {

        var panel = selection.findParentByType('panel', true),
            modalConfigurationFieldSet = panel.findByType('fieldset', true)[2],
            loginPageFieldset = panel.findByType('fieldset', true)[1],
        	modalConfigLength = modalConfigurationFieldSet.length;

        if (isChecked){
            modalConfigurationFieldSet.hide();
            loginPageFieldset.show();
            for(var i = 0; i < modalConfigLength; i ++){
			    modalConfigurationFieldSet[i].allowBlank = true;
        	}
        }else{
            modalConfigurationFieldSet.show();
            loginPageFieldset.hide();
            for(var i = 0; i < modalConfigLength; i ++){
			    modalConfigurationFieldSet[i].allowBlank = false;
        	}
        }

	},
    loadContent: function(field, record, path) {
        var value = field.getValue(),
            panel = field.findParentByType('panel', true),
            modalConfigurationFieldSet = panel.findByType('fieldset', true)[2],
            loginPageFieldset = panel.findByType('fieldset', true)[1];
        console.log(value);
        if (value.length){
            console.log("no value");
            modalConfigurationFieldSet.hide();
            loginPageFieldset.show();
        }else{
            modalConfigurationFieldSet.show();
            loginPageFieldset.hide();
        }
    }
};