MetlifeCommons.Global.GlossaryParsys = {

    filterComponents : function(container,component,index, groupName, componentName){
         if(component.title === groupName){
         filteredItems = new Array();
         filteredKeys = new Array();
         items = component.items.items;
         keys = component.items.keys;
         for(i = 0; i<items.length ; i++) {
            if(items[i].inputValue == componentName) {
                filteredItems.push(items[i]);
                filteredKeys.push(keys[i]);
            }
         }
         component.items.items = filteredItems;
         } else {
            component.hide();
         }
    },
    disableCheckbox: function(container, groupName) {
        container.getEl().child('input[value="group:'+groupName+'"]').hide();
    }
}