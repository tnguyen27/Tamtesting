
Metlife = {
    Components: {},
    Forms : {},
    Global: {}
};


$(document).ready(function(){
    //Tile container
    $('.tile-container').each(function(){
       $(this).find('.xk-component:last').addClass('xk-tile-container-last-tile');
    });
    //Subcategory Tile Container
    $('.subcategory-tile-container').each(function(){
        $(this).find('.xk-component:last').addClass('xk-tile-container-last-tile');
        $(this).after('<div style="clear:both;"></div>')
    });


    //life stages resizing to order properly the tabs
    $(window).trigger('resize');
    $('.life-stages-container').trigger('resize');
    window.setTimeout(function() {
        $(window).trigger('resize');
        $('.life-stages-container').trigger('resize');

    }, 1000);

    
});


// method used to remove repeated items: used for country_selector component
function cleanup(arr, prop) {
    var new_arr = [];
    var lookup  = {};

    for (var i in arr) {
        lookup[arr[i][prop]] = arr[i];
    }

    for (i in lookup) {
        new_arr.push(lookup[i]);
    }

    return new_arr;
}
