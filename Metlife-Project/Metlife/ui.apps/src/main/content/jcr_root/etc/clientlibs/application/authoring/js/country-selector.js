Metlife.Global.Countries = {

    defaultCountryProvider : function(path, record, _this){
         var americas = CQ.Ext.getCmp('AmericasCountries');
         var asia = CQ.Ext.getCmp('AsiaCountries');
         var europe = CQ.Ext.getCmp('EuropeCountries');
         var middleEastAndAfrica = CQ.Ext.getCmp('MEACountries');
         var countriesList = {
           country: []
         };
         if(americas != null && typeof americas != 'undefined'){
            var americasCountries = JSON.parse(JSON.stringify(americas.getValue()));
            for ( i=0; i<americasCountries.length; i++){
                 var value = JSON.parse(americasCountries[i]).flag;
                 countriesList.country.push({
                   "text" : value,
                   "value"  : value
                 });
             }
         }
         if(asia != null && typeof asia != 'undefined'){
             var asiaCountries = JSON.parse(JSON.stringify(asia.getValue()));
             for ( i=0; i<asiaCountries.length; i++){
                  var value = JSON.parse(asiaCountries[i]).flag;
                  countriesList.country.push({
                    "text" : value,
                    "value"  : value
                  });
              }
         }
         if(europe != null && typeof europe != 'undefined'){
             var europeCountries = JSON.parse(JSON.stringify(europe.getValue()));
             for ( i=0; i<europeCountries.length; i++){
                  var value = JSON.parse(europeCountries[i]).flag;
                  countriesList.country.push({
                    "text" : value,
                    "value"  : value
                  });
              }
          }
         if(middleEastAndAfrica != null && typeof middleEastAndAfrica != 'undefined'){
             var middleEastAndAfricaCountries = JSON.parse(JSON.stringify(middleEastAndAfrica.getValue()));
             for ( i=0; i<middleEastAndAfricaCountries.length; i++){
                  var value = JSON.parse(middleEastAndAfricaCountries[i]).flag;
                  countriesList.country.push({
                    "text" : value,
                    "value"  : value
                  });
              }
          }
         var allCountries = cleanup(countriesList.country, 'value');
         return allCountries;

    }
    
}