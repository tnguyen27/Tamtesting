MetlifeMicrosites.Components.tableComponent = {

    hideOrShowTab: function(panel,tab,value){
        if (value == true){
            panel.unhideTabStripItem(tab);
        } else {
            panel.hideTabStripItem(tab);
        }
    },

    hideOrShowColumns: function( selection,value, checked){

        var tabpanel = selection.findParentByType("tabpanel");
        var coltab2 = tabpanel.getComponent("colu25");
        var coltab3 = tabpanel.getComponent("colu35");
        var coltab4 = tabpanel.getComponent("colu45");
        var coltab5 = tabpanel.getComponent("colu55");
        var coltab6 = tabpanel.getComponent("colu65");
        var coltab7 = tabpanel.getComponent("colu75");
        var coltab8 = tabpanel.getComponent("colu85");
        var coltab9 = tabpanel.getComponent("colu95");
        var coltab10 = tabpanel.getComponent("colu105");
        var coltab11 = tabpanel.getComponent("colu115");
        var coltab12 = tabpanel.getComponent("colu125");

        var colArray = new Array(coltab2, coltab3, coltab4, coltab5, coltab6, coltab7,
            coltab8, coltab9, coltab10, coltab11, coltab12);

        if (value !== null) {

            var intValue = parseInt(value);

            for (index = 0; index < colArray.length ; index++) {

                var selecCol = colArray[index];

                if (index < (intValue - 1)){

                    MetlifeMicrosites.Components.tableComponent.hideOrShowTab(tabpanel, selecCol, true);

                } else {

                    MetlifeMicrosites.Components.tableComponent.hideOrShowTab(tabpanel, selecCol, false);

                }
            }//for
        }//if
    }
}