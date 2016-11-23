//Behavior for button to call
Metlife.Components.ProductCardTile = {
    selectionChanged: function (selection, value, isChecked) {
        var panel = selection.findParentByType('panel'),
            callButtonLabel = panel.getComponent('productActionSecondButtonLabel'),
            callButtonLink = panel.getComponent('productActionSecondButtonLink'),
            callButtonTarget = panel.getComponent('targetSecond'),
            asLivePersonButton = panel.getComponent('callLivePerson');
        if (isChecked) {
            callButtonLabel.hide();
            callButtonLink.hide();
            callButtonTarget.hide();
            asLivePersonButton.show();
        } else {
            asLivePersonButton.hide();
            callButtonLabel.show();
            callButtonLink.show();
            callButtonTarget.show();
        }
    },
    loadContent: function(field,record,path) {
        if (field.getValue().length != 0) {
            field.findParentByType('panel').getComponent("productActionSecondButtonLabel").hide();
            field.findParentByType('panel').getComponent("productActionSecondButtonLink").hide();
            field.findParentByType('panel').getComponent("targetSecond").hide();
            field.findParentByType('panel').getComponent("callLivePerson").show();
        } else {
            field.findParentByType('panel').getComponent("callLivePerson").hide();
            field.findParentByType('panel').getComponent("productActionSecondButtonLabel").show();
            field.findParentByType('panel').getComponent("productActionSecondButtonLink").show();
            field.findParentByType('panel').getComponent("targetSecond").show();
        }
    }
};

//Behavior for button to chat
Metlife.Components.ProductCardTileChatButton = {
    selectionChanged: function (selection, value, isChecked) {
        var panel = selection.findParentByType('panel'),
            callButtonLabel = panel.getComponent('productActionThirdButtonLabel'),
            callButtonLink = panel.getComponent('productActionThirdButtonLink'),
            callButtonTarget = panel.getComponent('targetThird'),
            asLivePersonButton = panel.getComponent('chatLivePerson');
        if (isChecked) {
            callButtonLabel.hide();
            callButtonLink.hide();
            callButtonTarget.hide();
            asLivePersonButton.show();
        } else {
            asLivePersonButton.hide();
            callButtonLabel.show();
            callButtonLink.show();
            callButtonTarget.show();
        }
    },
    loadContent: function(field,record,path) {
        if (field.getValue().length != 0) {
            field.findParentByType('panel').getComponent("productActionThirdButtonLabel").hide();
            field.findParentByType('panel').getComponent("productActionThirdButtonLink").hide();
            field.findParentByType('panel').getComponent("targetThird").hide();
            field.findParentByType('panel').getComponent("chatLivePerson").show();
        } else {
            field.findParentByType('panel').getComponent("chatLivePerson").hide();
            field.findParentByType('panel').getComponent("productActionThirdButtonLabel").show();
            field.findParentByType('panel').getComponent("productActionThirdButtonLink").show();
            field.findParentByType('panel').getComponent("targetThird").show();
        }
    }
};
