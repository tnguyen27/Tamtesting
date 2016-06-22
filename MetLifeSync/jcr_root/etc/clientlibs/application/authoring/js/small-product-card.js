Metlife.Components.SmallProductCard = {

    validate: function(value) {

        var msg = "Both lines must be less than or equal to 30 characters";

        var line1Cmp = CQ.Ext.getCmp('firstLineText');
        var line2Cmp = CQ.Ext.getCmp('secondLineText');
        var line1Text = line1Cmp.getValue().replace(/ /g,'');
        var line2Text = line2Cmp.getValue().replace(/ /g,'');

        if (line1Text.length + line2Text.length > 30) {
            line1Cmp.markInvalid(msg);
            line2Cmp.markInvalid(msg);
			return msg;
        }

		line1Cmp.clearInvalid();
        line2Cmp.clearInvalid();

        return true;
    }


};