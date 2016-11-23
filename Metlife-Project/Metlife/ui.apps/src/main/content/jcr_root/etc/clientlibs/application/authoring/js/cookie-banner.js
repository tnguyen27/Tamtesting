Metlife.Components.CookieBanner = {
    keyup: function (field, e) {

        var value = field.getValue();
        console.log(value);
        if(value < 365){
			field.setValue = 365;
		}
    }
};