Metlife.Global.Countries = {

    defaultCountryProvider : function(path, record, _this){
         var tabpanel = _this.findParentByType("tabpanel").getComponent('countriesAndLanguagesTab');
            console.log(tabpanel);
         var americas = tabpanel.getComponent(1).getComponent(1);
         var asia = tabpanel.getComponent(2).getComponent(1);
         var europe = tabpanel.getComponent(3).getComponent(1);
         var middleEastAndAfrica = tabpanel.getComponent(4).getComponent(1);

         var countriesList = {
           country: []
         };
         var americasCountries = JSON.parse(JSON.stringify(americas.getValue()));
         var asiaCountries = JSON.parse(JSON.stringify(asia.getValue()));
         var europeCountries = JSON.parse(JSON.stringify(europe.getValue()));
         var middleEastAndAfricaCountries = JSON.parse(JSON.stringify(middleEastAndAfrica.getValue()));


         for ( i=0; i<americasCountries.length; i++){
             var value = JSON.parse(americasCountries[i]).flag;
             countriesList.country.push({
               "text" : value,
               "value"  : value
             });
         }
         for ( i=0; i<asiaCountries.length; i++){
             var value = JSON.parse(asiaCountries[i]).flag;
             countriesList.country.push({
               "text" : value,
               "value"  : value
             });
         }
         for ( i=0; i<europeCountries.length; i++){
             var value = JSON.parse(europeCountries[i]).flag;
             countriesList.country.push({
               "text" : value,
               "value"  : value
             });
         }
         for ( i=0; i<middleEastAndAfricaCountries.length; i++){
             var value = JSON.parse(middleEastAndAfricaCountries[i]).flag;
             countriesList.country.push({
               "text" : value,
               "value"  : value
             });
         }

         var allCountries = cleanup(countriesList.country, 'value');
         return allCountries;

    }

}