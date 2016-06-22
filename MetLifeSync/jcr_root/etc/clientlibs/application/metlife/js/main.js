/***** Helper Functions Begin *********************************************************/
// Case Insensitive ":contains"
$.expr[":"].contains = $.expr.createPseudo(function (arg) {
    return function (elem) {
        return $(elem).text().toUpperCase().indexOf(arg.toUpperCase()) >= 0;
    };
});
/***** Helper Functions End *********************************************************/

var allMonths;
if (typeof replaceTextForAll != 'undefined') {
    allMonths = replaceTextForAll;
}

var maxHeight = 0;


/* Document Ready Functions Begin ***********************************************************/
$(document).ready(function () {


    function resizeHeroCarousel() {
        if ($(window).width() > 1900)
            $('#myCarouselBanner .carousel-inner .item img').width($(window).width());
    }

    $.lazyLoadXT.autoLoadTime = 5000;
    resizeHeroCarousel();
    $(window).resize(function () {
        resizeHeroCarousel();
        $(window).lazyLoadXT({
            checkDuplicates: false
        });
    });

    if ($(".page_wrap").length > 0) {
        var stickyOffset = $('.pageWrap').offset().top + 20;
    } else {
        var stickyOffset = 20;
    }
    $(window).scroll(function () {
        var scroll_pos = $(window).scrollTop();

        if (scroll_pos >= stickyOffset) {
            $('.global_header').addClass('fixedHeader');
            $('.global_header').removeClass('notFixedHeader');
            $('.pageWrap,.megaMenu').addClass('addMargin');
            $(".loginOpen").css("top", "50px");
        }
        else {
            $('.global_header').removeClass('fixedHeader');
            $('.global_header').addClass('notFixedHeader');
            $('.pageWrap,.megaMenu').removeClass('addMargin');
            $(".loginOpen").css('top', '70px');
            //  $(".ss-gac-table").css('top', '54px');
            if ($(window).width() < 751) {
                $(".loginOpen").css("top", "50px");
                // $(".ss-gac-table").css('top', '103px');
            }
        }
    });


    if (!$(".visible-xs").is(":visible")) {
        $('.sub_navigation_menu_nav li a')
            .each(function () {
                maxHeight = Math.max(maxHeight, $(this).height());
            })
            .height(maxHeight);
        $('.sub_navigation_menu_nav li a p').each(function () {
            maxHeight = Math.max(maxHeight, $(this).height());
        }).height(maxHeight);
        var newHeight = $('.sub_navigation_menu_nav li a').height();
        $(".sub_navigation_menu").css("height", newHeight);
        $(".sub_navigation_menu_nav").css("height", newHeight);

    }
    else {
        $(".sub_navigation_menu").css("height", "100%");
        $(".sub_navigation_menu_nav").css("height", "100%");
        $('.sub_navigation_menu_nav li a ').css("height", "100%");
        $('.sub_navigation_menu_nav li a p').css("height", "100%");
    }

    if ($(".hidden-xs").is(":visible") == false) {
        $(".fax-container").find('.contact-lead-form').insertAfter($(".results_list_container"));

    }
    else {
        $(".fax-container").find('.contact-lead-form').insertAfter($(".fax-results-container > .maps-contact-form-container > button"));
    }

    // Bind the In View Handling (items sliding into view)
    $('.in_view').bind('inview', function (event, visible) {
        if (visible == true) {
            $(this).addClass('on');
        }
    });


    matchCampaignHeights();

    // Format the CTA Boxes
    formatCTABoxes();

    //Initialize Autocomplete for all GoogleMaps Inputs
    gmapsAutoCompleteInit();

    //For FAX page fill in input from URL
    parseUserAddress();

    // Initialize Blog Category Mobile Design
    blogCategoryMobileDesign();

    // Initialize CTA Header in FAX
    ctaHeaderFAXinit();

    //Load breakpoint images on resize
    loadBannerImagesOnResize();

    //Set Elements to same width
    setElementsWidthToLargest($(".matching-element-width"));

    //Campaign Page Header Resize
    campaignHeaderScroll();

    matchMegaMenuLabelHeights();

    contactUsFooterDivider();


    $(".subcat-productcard-bottom p a").click(function (e) {
        if ($(".hidden-xs").is(":visible")) {
            e.preventDefault();
        } else {
            return true;
        }

    });

    subcategoryProudctTilesLayout();
});
/* Document Ready Functions End *************************************************************/



/* Window Load Functions Begin ************************************************************/
$(window).load(function () {
    if (navigator.appVersion.indexOf("MSIE 9") !== -1) {
        //fix for IE9 placeholder.js
        window.setTimeout(function () {
            Placeholders.enable();
        }, 100);
    }


    // Reset Page position to top
    $(window).scrollTop(0);

    loadPage();

    loadResize();

    setCarouselMargin();

    productComparisonChart();

    micrositeCarouselSetup();

    micrositeComparisonChart();

    productCardSetImage();

    subcategoryProudctTilesHeight();

    matchLargeProductModuleHeights();

    matchMediumProductModuleHeights();

    removeDividerOnCampaignPageD();

    faoHeaderOptional();

    setDisabledField();
});
/* Window Load Functions End ************************************************************/



/* Window Resize Functions Begin ************************************************************/
$(window).resize(function (e) {

    micrositeComparisonChart();

    if (!$(".visible-xs").is(":visible")) {
        $('.sub_navigation_menu_nav li a')
            .each(function () {
                maxHeight = Math.max(maxHeight, $(this).height());
            })
            .height(maxHeight);
        $('.sub_navigation_menu_nav li a p').each(function () {
            maxHeight = Math.max(maxHeight, $(this).height());
        }).height(maxHeight);
        var newHeight = $('.sub_navigation_menu_nav li a').height();
        $(".sub_navigation_menu").css("height", newHeight);
        $(".sub_navigation_menu_nav").css("height", newHeight);

    }
    else {
        $(".sub_navigation_menu").css("height", "100%");
        $(".sub_navigation_menu_nav").css("height", "100%");
        $('.sub_navigation_menu_nav li a ').css("height", "100%");
        $('.sub_navigation_menu_nav li a p').css("height", "100%");
    }


    if ($(".hidden-xs").is(":visible") == false) {
        var focusedElement = document.activeElement;
        $(".fax-container").find('.contact-lead-form').insertAfter($(".results_list_container"));
        focusedElement.focus();
    }
    else {
        var focusedElement = document.activeElement;
        $(".fax-container").find('.contact-lead-form').insertAfter($(".fax-results-container > .maps-contact-form-container > button"));
        focusedElement.focus();
    }

    /*Enable the results page title and the results description to appear and reappear as the window is resized while the edit your quote menu is open.*/
    if ($(window).width() >= 750 && $(".results_page_title").css('display') == 'none' && $(".results_page_description").css('display') == 'none') {
        $(".results_page_title").show();
        $(".results_page_description").show();
    }
    else if ($(window).width() < 750 && ($(".edit_quote_mlt").css('display') == 'block' || $(".edit_quote_sit").css('display') == 'block')) {
        $(".results_page_title").hide();
        $(".results_page_description").hide();
    }

    /*if (($(window).width() >= 752 && $(window).width() <= 1008)) {
     if ($('.search_close_not_mobile').hasClass('hidden')) {
     $('.search_close_not_mobile').css("left", "275px");
     } else {
     $('.search_close_not_mobile').css("left", "275px");
     $('.searchBox').css("width", "168px");
     }
     }

     if (($(window).width() >= 1009)) {
     if ($('.search_close_not_mobile').hasClass('hidden')) {
     $('.search_close_not_mobile').css("left", "285px");
     } else {
     $('.search_close_not_mobile').css("left", "370px");
     $('.searchBox').css("width", "175px");
     }
     }*/

    if ($(window).width() <= 751) {
        if ($(".loginOpen").css("display") !== "none") {
            $(".loginOpen").addClass("login_mobile");
            /*$('body').css("height", " 615px");*/
        }
        $(".loginOpen").css("top", "50px");

        if ($(".contactSliderOuterCon").css("display") !== 'none') {
            $(".header .contact-close").click();
        }
        if ($('.megaMenu').css("display") === "inline-block") {
            $('body').css("height", "auto");
        }
        if (!$('.search_close_not_mobile').hasClass('hidden')) {
            //$('.searchBox').stop().animate({ 'width': '0px' }, 300);
            //$('.search_close').stop().animate({ 'left': '285px' }, 300);
            setTimeout(function () {
                $(".search_open").removeClass('hidden');
                $('.searchBox').addClass('hidden');
                $('.search_close_not_mobile').addClass('hidden');
            }, 300);
            // $('.suggestionsbox table,.suggestionsbox table tbody').css('visibility', 'hidden');
        }
    }

    if ($(window).width() > 751) {

        if ($(".global_header").hasClass("fixedHeader")) {
            $(".loginOpen").css("top", "50px");
        } else {
            $(".loginOpen").css("top", "70px");
        }
        $('body').css("height", "auto");
        $('.search_open').removeClass('searchOpenMobile');
        $('.search_close_mobile').addClass('hidden');
        $('.search_openTwoCon').addClass('hidden');
        $('.search_openTwoCon').stop().animate({'top': '-50px'}, 200);
        if ($('.megaMenu').css("display") === "inline-block") {
            $('body').css("height", "927px");
        }
    }

    if ($(".dob_label").is(':visible')) {
        $(".gawli_error_state").insertAfter(".select_state");
    }
    else {
        $(".gawli_error_state").insertAfter(".dob_input");
    }

    if ($(".hidden-xs").is(":visible")) {
        $(".mobile_expand_close").click();

        $(".error_zip_code").insertAfter(".mobile_expand");
    }else{

        $(".error_zip_code").insertAfter(".mobile_expand_open");
    }


    matchCampaignHeights();

    blogCategoryMobileDesign();

    productComparisonChart();


    loadResize();

    //see fao page, this handles when the user clicks into the text box on desktop and then resizes to mobile - fields should be shown
    if ($(".mobile_expand_close").is(":visible") && !$(".hidden-xs").is(":visible")) {

        $(".find_an_x_cta_search .mobile_expand_close").show();
        $(".find_an_x_cta_search .mobile_expand").slideDown();
        $(".error_zip_code").insertAfter(".input_field_container");
    }

    matchMegaMenuLabelHeights();

    contactUsFooterDivider();

    subcategoryProudctTilesHeight();

    matchLargeProductModuleHeights();

    matchMediumProductModuleHeights();


    setCarouselMargin();

});
/* Window Resize Functions End **************************************************************/


/***** Name Space Begin **************************************************************************/
var metlifeRedesign = {
    openSearch: function () {
        if ($(window).width() < 751) {
            if ($('.search_open').hasClass('searchOpenMobile')) {
                $('.search_openTwoCon').stop().animate({'top': '-50px'}, 300);
                $('.search_open').removeClass('searchOpenMobile');
                $(".ss-gac-table").css("visibility", "hidden");
                $(".ss-gac-m").css("visibility", "hidden");
            } else {
                if ($(".mega_menu_open").hasClass("hidden")) {
                    $('.mega_menu_open,.mega_menu_close').toggleClass('hidden');
                    $('.global_header').removeClass('menu-open');
                    $('.megaMenu').stop().animate({width: 'hide'});
                    $('body').css("height", "auto");
                }
                if ($(".login_open").hasClass("login_mobile")) {
                    $('.login_open').removeClass("login_mobile");
                    $('.loginOpen').stop().animate({right: '-800'}, 100);
                    $(".loginUsername").removeClass('login_error').val("");
                    $('body').css("height", "auto");
                    $("#formLogin").siblings(".formFail").find(".errorSpanLogin").css('display', 'none');
                    $("#formLogin").find(".login_submit_error").removeClass('login_submit_error');
                }
                $('.search_openTwoCon').removeClass("hidden");
                $('.search_openTwoCon').stop().animate({'top': '50px'});
                $('.search_open').addClass('searchOpenMobile');
                $('.search_close_mobile').removeClass('hidden');
                $('.login_open,.globalContact').show();
            }
        }
        if ($(window).width() >= 751 && $(window).width() <= 1008) {
            $(".search_open").addClass('hidden');
            $('.searchBox').removeClass('hidden');
            $('.search_close_not_mobile').removeClass('hidden');
        }
        if ($(window).width() >= 1009) {
            $(".search_open").addClass('hidden');
            $('.searchBox').removeClass('hidden');
            $('.search_close_not_mobile').removeClass('hidden');
        }
    },
    closeContacts: function () {
        $('.contactSliderOuterCon').animate({right: '-320'}, 200, function () {
            $(this).hide();
            $('.feedbackLink').removeClass('hidden');
        });
    },
    login: function () {
        $('.loginOpen').removeClass("hidden");
        if ($(window).width() < 751) {
            if ($('.search_open').hasClass('searchOpenMobile')) {
                $('.search_openTwoCon').stop().animate({'top': '-50px'}, 300);
                $('.search_open').removeClass('searchOpenMobile');
            }
            if ($(".mega_menu_open").hasClass("hidden")) {
                $('.mega_menu_open,.mega_menu_close').toggleClass('hidden');
                $('.global_header').removeClass('menu-open');
                $('.megaMenu').stop().animate({width: 'hide'});
                $('body').css("height", "auto");
            }
            if ($(".login_open").hasClass("login_mobile")) {

                $('.login_open').removeClass("login_mobile");
                $('.loginOpen').stop().animate({right: '-800'}, 100);
                $(".loginUsername").removeClass('login_error').val("");
                $("#formLogin").siblings(".formFail").find(".errorSpanLogin").css('display', 'none');
                $("#formLogin").find(".login_submit_error").removeClass('login_submit_error');
                $('body').css("height", "auto");
                $("html, body").scrollTop(scroll);
                $('.login_open').css("color", "#fff");

            } else {
                scroll = $(window).scrollTop();
                $('.login_open').addClass("login_mobile");
                $(".loginOpen").css("top", "50px");
                $('.loginOpen').stop().animate({right: '0'}, 100);
                $('body').css("height", "615px");
            }

            $('.loginOpen').show();
        }
        else {
            $('.loginOpen').css("right", "0");
            $('.loginOpen').toggle();
            $('body').css("height", "auto");
            if ($(".loginOpen").css("display") == 'none') {
                $(".loginUsername").removeClass('login_error').val("");
                $("#formLogin").siblings(".formFail").find(".errorSpanLogin").css('display', 'none');
                $("#formLogin").find(".login_submit_error").removeClass('login_submit_error');
            }

        }
    },
    closeMegaMenu: function () {
        $('.mega_menu_open,.mega_menu_close').toggleClass('hidden');
        $(".login_open").removeClass("hidden-sm hidden-lg");
        $('.globalContact').show();
        $('.global_header').removeClass('menu-open');
        if ($(window).width() >= 751) {
            $('.megaMenu').hide();
            $('.megaMenu').css("left", "0");
        }
        else {
            $('.megaMenu').stop().animate({left: '-800'}, 300);
            $('.megaMenu').css("left", "-800");
            $('body').css("height", "auto");

            if ($(".login_open").hasClass("login_mobile")) {
                $('.login_open').removeClass("login_mobile");
                $('.loginOpen').stop().animate({right: '-800'}, 100);
                $(".loginUsername").removeClass('login_error').val("");
                $("#formLogin").siblings(".formFail").find(".errorSpanLogin").css('display', 'none');
                $("#formLogin").find(".login_submit_error").removeClass('login_submit_error');
            }
        }
        $('.megaMenu').hide();
        if ($(window).width() > 751) {
            //$('.searchBox').animate({ 'width': '0px' }, 300);
            //$('.search_close_not_mobile').stop().animate({ 'left': '272px' }, 300);
            setTimeout(function () {
                $(".search_open").removeClass('hidden');
                $('.searchBox').addClass('hidden');
                $('.search_close_not_mobile').addClass('hidden');
            }, 300);
        } else {
            if ($('.search_open').hasClass('searchOpenMobile')) {
                $('.search_openTwoCon').stop().animate({'top': '-50px'}, 300);
                $('.search_open').removeClass('searchOpenMobile');
            }
        }
        if ($(window).width() >= 1009) {
        }
    },
    openMegaMenu: function () {
        $('.mega_menu_open,.mega_menu_close').toggleClass('hidden');
        $(".login_open").addClass("hidden-sm hidden-lg");
        $('.globalContact').hide();
        $('.global_header').addClass('menu-open');
        $('.loginOpen').hide();
        if ($(".contactSliderOuterCon").css("display") !== 'none') {
            $(".header .contact-close").click();
        }
        if ($(window).width() >= 751) {
            $('.megaMenu').css("left", "0");
            $('.megaMenu').show();
            $('.megaMenu').css('display', 'inline-block');
            $('body').css("height", "auto");
        }
        else {
            if ($('.search_open').hasClass('searchOpenMobile')) {
                $('.search_openTwoCon').stop().animate({'top': '-50px'}, 300);
                $('.search_open').removeClass('searchOpenMobile');
            }
            $('body').css("height", "927px");
            $('.megaMenu').css("left", "-800");
            $('.megaMenu').stop().animate({left: '0'}, 300);
            $('.megaMenu').show();
            $('.megaMenu').css('display', 'inline-block');
        }
    },
    lifeStageBounce: function (selectedStage) {
        var lifeStageSelected = selectedStage;
        //var lifeStageCurrent  = $(".life_stage_current");


        $(lifeStageSelected).stop().animate({
            top: 15
        }, 10, function () {
            $(lifeStageSelected).stop().animate({
                top: 0
            });
        });
    }
};
/***** Name Space End ****************************************************************************/


/***** Header Begin ******************************************************************************/
var scroll;

function setCarouselMargin(){

    if($(".carousel-fade").length !=0){

        if($(".hidden-xs").is(":visible")){
            $(".carousel-fade").css("margin-top","-"+ $(".home_page_sub_navigation").height()+ "px");
        }else{
            $(".carousel-fade").css("margin-top","0");
        }

    }

}

// Opens mega menu
$('.mega_menu_open').on('click', function (e) {
    scroll = $(window).scrollTop();
    e.preventDefault();
    if ($(".visible-xs").is(":visible")) {
        $("html, body").scrollTop(0);
    }
    metlifeRedesign.openMegaMenu();
    if ($(window).width() > 751) {
        metlifeRedesign.openSearch();
    }
});

// Closes mega menu
$('.mega_menu_close').on('click', function (e) {
    e.preventDefault();
    metlifeRedesign.closeMegaMenu();
    $("html, body").scrollTop(scroll);
});

// Opens search
$('.search_open').click(function (e) {
    e.preventDefault();
    metlifeRedesign.openSearch();
});



// Opens contacts
$('.globalContact').on('click', function (evt) {
    evt.preventDefault();
    $('.contactSliderOuterCon').show();
    $('.contactSliderOuterCon').stop().animate({right: '0'}, 200);
    $('.feedbackLink').addClass('hidden');
});

// Closes contacts
$('.header .contact-close').click(function (e) {
    e.preventDefault();
    metlifeRedesign.closeContacts();
});

// Telephone call behavior
$(".contactSliderOuterCon .contact-details a").first().on("click", function (evt) {
    // Only activate tel link in mobile
    if ($(".hidden-xs").is(":visible")) {
        evt.preventDefault();
    }
});

// Opens/Closes Header windows (search, contacts, and login)
$(document).click(function (event) {
    if (/*!$(event.target).parents().hasClass('suggestionsbox') &&*/ !$(event.target).hasClass('search_open') && !$(event.target).hasClass('searchBox') && !$(event.target).hasClass('label') && !$(event.target).hasClass('icon') && !$(event.target).hasClass('search_openTwo') && !$(event.target).hasClass('open_search_image') && !$(event.target).is('img')) {
        if ($(window).width() >= 751) {
            setTimeout(function () {
                $(".search_open").removeClass('hidden');
                $('.searchBox').addClass('hidden');
                $('.search_close_not_mobile').addClass('hidden');
            }, 300);
        } else {
            $('.search_open').removeClass('searchOpenMobile');
            $('.search_close_mobile').addClass('hidden');
            $('.search_openTwoCon').stop().animate({'top': '-50px'}, 300);
        }
        // $(".ss-gac-table").css("visibility", "hidden");
        //$(".ss-gac-m").css("visibility", "hidden");
    }
    if (!$(event.target).hasClass('loginUsername') && !$(event.target).hasClass('label') && !$(event.target).hasClass('loginOpen') && !$(event.target).hasClass('loginForm') && !$(event.target).hasClass('loginOptions') && !$(event.target).hasClass('loginformSubmit') && !$(event.target).hasClass('loginRegister') && !$(event.target).hasClass('forgotUsername') && !$(event.target).hasClass('login_open') && !$(event.target).hasClass('icon') && !$(event.target).hasClass('lazy') && !$(event.target).is('img')) {
        if ($(window).width() < 751) {
            if ($(".login_open").hasClass("login_mobile")) {
                $('body').css("height", "auto");
                $('.login_open').removeClass("login_mobile");
                $('.loginOpen').stop().animate({right: '-800'}, 100);
                $(".loginUsername").removeClass('login_error').val("");
                $("#formLogin").siblings(".formFail").find(".errorSpanLogin").css('display', 'none');
                $("#formLogin").find(".login_submit_error").removeClass('login_submit_error');
            }
        }
        else {
            $('.loginOpen').css("right", "0");
            $('.loginOpen').hide();
            $('body').css("height", "auto");
            $(".loginUsername").removeClass('login_error').val("");
            $("#formLogin").siblings(".formFail").find(".errorSpanLogin").css('display', 'none');
            $("#formLogin").find(".login_submit_error").removeClass('login_submit_error');
        }
    }
});

$("#headerLoginSubmit").click(function (e) {
    e.preventDefault();
    var formStatus = true;
    $("#formLogin").find('[data-required=true]').each(function () {
        var $this = $(this);
        var placeholder = $this.attr('placeholder');
        if ($this.val() == placeholder) {
            $this.val("");
        }

        var val = $this.val();

        if (val.length == 0) {
            $this.addClass('login_error');
            $this.parent("#formLogin").siblings(".formFail").find(".errorSpanLogin").css('display', 'block');
            $this.parent("#formLogin").find(".loginformSubmit").addClass('login_submit_error');
            $this.parent("#formLogin").find(".loginMoreoptions").addClass('login_submit_error');
            $this.val(placeholder);
            formStatus = false;
        }
        else {
            $this.parent("#formLogin").siblings(".formFail").find(".errorSpanLogin").css('display', 'none');
            $this.siblings(".login_error").parent("#formLogin").siblings(".formFail").find(".errorSpanLogin").css('display', 'block');
            $this.parent("#formLogin").find(".login_submit_error").removeClass('login_submit_error');
        }
    });
});

$("#formLogin").find('[data-required=true]').on({
    focus: function () {
        $this = $(this);
        $this.removeClass('login_error');
        var placeholder = $(this).attr('placeholder');
        if ($this.val() == placeholder) {
            $this.val("");

        }
    },
    blur: function () {
        var $this = $(this);
        var placeholder = $this.attr('placeholder');
        if ($this.val() == placeholder) {
            $this.val("");
        }
        var val = $this.val();
        if (val.length == 0) {
            $this.addClass('login_error');
            $this.parent("#formLogin").siblings(".formFail").find(".errorSpanLogin").css('display', 'block');
            $this.parent("#formLogin").find(".loginformSubmit").addClass('login_submit_error');
            $this.parent("#formLogin").find(".loginMoreoptions").addClass('login_submit_error');
            $this.val(placeholder);
        }
        else {
            $this.removeClass('login_error');
            $this.parent("#formLogin").siblings(".formFail").find(".errorSpanLogin").css('display', 'none');
            $this.parent("#formLogin").find(".login_submit_error").removeClass('login_submit_error');
        }
    }
});
/***** Header End ********************************************************************/


/***** Scroll For More Begin *********************************************************/
// Navigates to Life Stages
$(".scroll-for-more").click(function (e) {
    e.preventDefault();
    var height = $(".global_header").height();
    $('html, body').animate({scrollTop: $(".divider-snoopy").offset().top - height - 30}, 500);
});
/***** Scroll For More End *********************************************************/


/***** Life Stages on Home Page Begin ************************************************/
var activeItem = 0;

function loadPage() {
    //Show selected item
    $('.carousel-with-tabs .carousel-item').eq(activeItem).addClass('selected');
    $('.carousel-with-tabs .carousel-item .arrow-down').eq(activeItem).show();
    $('.carousel-with-tabs .carousel-tab').eq(activeItem).fadeIn();

    // Style tab based on clicked block
    $('.carousel-with-tabs .carousel-item').click(function () {
        activeItem = $(this).attr('data-target');
        $('.carousel-tabs > .carousel-tab').hide();
        $('.carousel-with-tabs .carousel-item').removeClass('selected');
        $('.carousel-with-tabs .carousel-item .arrow-down').hide();
        $(this).addClass('selected');
        $(this).find('.arrow-down').show();
        $('.carousel-tabs > .carousel-tab').eq($(this).attr('data-target')).fadeIn();
        $(this).stop().animate({
            top: 15
        }, 15, function () {
            $(this).stop().animate({
                top: 0
            });
        });
    });

    // Style first tab after a carousel slide
    $('#carouselWithTabs').bind('slid.bs.carousel', function (e) {
        $('.carousel-tabs > .carousel-tab').hide();
        $('.carousel-with-tabs .carousel-item').removeClass('selected');
        $('.carousel-with-tabs .carousel-item img.arrow-down').hide();
        activeItem = $('.item.active > .carousel-item').attr('data-target');
        $('.carousel-tabs > .carousel-tab').eq(activeItem).show();
        $('.carousel-with-tabs .carousel-item').eq(activeItem).addClass('selected');
        $('.carousel-with-tabs .carousel-item').eq(activeItem).find('.arrow-down').show();
    });
}

function loadResize() {
    // Determine how many blocks to show based on screen width
    // Passed from user input from CMS
    var splitter = 3;


    var sWidth = $(window).width();
    var slides = $(".carousel-with-tabs .carousel-inner [class*='col-']");
    switch (true) {
        case (sWidth > 1024):
            splitter = slides.length;
            break;
        case (sWidth > 767):
            break;
        default:
            splitter = 1;
    }

    $('.carousel-with-tabs .item [class*=col-]').removeClass('active').unwrap();
    for (var i = 0; i < slides.length; i += splitter) {
        slides.slice(i, i + splitter).wrapAll("<div class='item'></div>");
    }

    // Activate selected slide

    var n = Math.floor(activeItem / splitter);
    $('.carousel-with-tabs .item').eq(n).addClass('active');
}
/***** Life Stages on Home Page End ************************************************/


/***** Navigation Begin ************************************************************/
// Sub Nav w/ Go Back: Opens & Closes Menu
$(".sub_nav_goback_header").click(function () {
    $(".sub_nav_goback_menu").slideToggle();
});

// Sub Nav w/ Go Back: Selects active class
$(".sub_nav_goback .sub_nav_goback_links a").each(function () {
    if ($(this).attr("href") == window.location.pathname) {
        $(this).addClass("active");
    }
});

//Home Page Sub Nav
$(".home_page_sub_navigation_primary > li > a").on("click", function (evt) {
    if (!$(".hidden-lg").is(":visible")) {
        return;
    } else if ($(this).parent().hasClass("active")) {
        return;
    } else {
        evt.preventDefault();
        $(".home_page_sub_navigation_primary li nav").removeClass("visible");
        $(".home_page_sub_navigation_primary li ").removeClass("active");

        $(".home_page_sub_navigation").addClass("blue_background_tablet");
        $(".home_page_sub_navigation_primary").addClass("blue_background_tablet");
        $(this).siblings(".home_page_sub_navigation_secondary").addClass("visible");
        $(this).parent().addClass("active");
    }
});

//Home Page Sub Nav
$(document).on("click", function (evt) {
    var container = $(".home_page_sub_navigation_primary");
    if (!container.is(evt.target) && container.has(evt.target).length === 0) {
        $(".home_page_sub_navigation_primary").removeClass("blue_background_tablet");
        $(".home_page_sub_navigation_primary li nav").removeClass("visible");
        $(".home_page_sub_navigation_primary li ").removeClass("active");
    }

    var container = $(".home_page_sub_navigation");
    if (!container.is(evt.target) && container.has(evt.target).length === 0) {
        $(".home_page_sub_navigation").removeClass("blue_background_tablet");

    }
});

//Home Page Sub Nav
$(".home_page_sub_navigation_primary li").hover(function () {
        if (!$(".hidden-lg").is(":visible")) {
            $(".home_page_sub_navigation").addClass("blue_background");
            $(".home_page_sub_navigation_primary").addClass("blue_background");
        }
    },
    function () {
        if (!$(".hidden-lg").is(":visible")) {
            $(".home_page_sub_navigation").removeClass("blue_background");
            $(".home_page_sub_navigation_primary").removeClass("blue_background");
        }
    });
/***** Navigation End ************************************************************/


/***** Product Card Module Begin ***************************************************/
// Expands the Product Card
$('.expand-open, .expand-close').click(function () {
    var expand = $(this).siblings('.expand-content');
    expand.slideToggle(function () {
        if (expand.attr("style") == "display: block;") {
            expand.addClass("visible-xs visible-sm visible-lg");
        } else {
            expand.removeClass("visible-xs visible-sm visible-lg");
        }
        expand.removeAttr("style");
    });

    expand.siblings('.expand-open').toggle();
    expand.siblings('.expand-close').toggle();
});

// Navigates to the FAQ Section on Page
$(".product-card .read-more").click(function (e) {
    e.preventDefault();
    var height = $(".global_header").height();
    $('html, body').animate({scrollTop: $(".faq_background").offset().top - height}, 500);
});
/***** Product Card Module End ************************************************************/


/***** Components Begin ********************************************************************/
// Expand All Accordions
$(".accordion .expandAll").click(function () {
    var parent = $(this).closest(".accordion");
    parent.find(".collapseAll").show();
    parent.find(".expandAll").hide();
    parent.find(".content").each(function () {
        if (!$(this).is(':visible')) {
            $(this).slideToggle('slow').scrollTop(0);
            $(this).parent().find('.expand').toggleClass('collapse', 'expand');
        }
    });
});

// Collapse All Accordions
$(".accordion .collapseAll").click(function () {
    var parent = $(this).closest(".accordion");
    parent.find(".collapseAll").hide();
    parent.find(".expandAll").show();
    parent.find(".content").each(function () {
        if ($(this).is(':visible')) {
            $(this).slideToggle('slow').scrollTop(0);
            $(this).parent().find('.expand').toggleClass('collapse', 'expand');
        }
    });
});

// Expand/Collapse Each Accordion
$(".accordion .title").click(function () {
    var parent = $(this).closest(".accordion");

    if ($(this).siblings('.content').is(':visible')) {

    } else {
        closeAll(parent);
    }
    $(this).siblings().slideToggle();
    $(this).find('.expand').toggleClass('collapse', 'expand');
    if ($(".collapse").length === 0) {
        parent.find(".collapseAll").hide();
        parent.find(".expandAll").show();
    }
});

function closeAll(parent) {
    $(parent).children('div').children('.content').each(function () {
        if ($(this).is(':visible')) {
            $(this).slideToggle();
            $(this).parent().find('.expand').toggleClass('collapse', 'expand');
        }
    });
}

// Expandable Accordion switch object
$('.accordion-switch-dropdown > select').change(function () {
    var selectedOption = $(this).val();
    $('.accordion-switch-objects > .accordion-switch-object').addClass('hidden');
    var conCatSelectedOption = '.accordion-switch-objects > .accordion-switch-object.' + selectedOption;
    $(conCatSelectedOption).removeClass('hidden');
});

$('.accordionHeader').on("click", function () {
    $(this).addClass('acc-open');
    var acc_con = $(this).next('.accordionContent');
    if (acc_con.is(':visible')) {
        $(this).removeClass('acc-open');
        acc_con.slideUp(500);
    }
    else {
        $(this).addClass('acc-open');
        acc_con.slideDown(500);
    }
});

$(".contacts .contacts_region_container .contacts_region").click(function (evt) {

    if ($(".hidden-xs").is(":visible")) {
        evt.preventDefault();
    } else {
        var parent = $(this).closest(".contacts_region_container");
        $(this).siblings().slideToggle('slow');
        $(this).find('.expand').toggleClass('collapse', 'expand');
        if ($(".collapse").length === 0) {
            parent.find(".collapseAll").hide();
            parent.find(".expandAll").show();
        }
    }

});

// Format CTA boxes depending on number
function formatCTABoxes() {
    $(".cta-boxes-layout").each(function () {
        var layout = $(this);
        var number = layout.children().length;
        if (number <= 4) {
            layout.addClass("large");
        } else {
            layout.addClass("small");
        }
        layout.show();
    });
}

$(".full_menu_Label").on('click', function () {
    if ($(window).width() < 768) {
        if ($(this).hasClass('expanded')) {
            $(this).siblings('ul').slideUp();
            $(this).removeClass('expanded');
        }
        else {
            $('.subCategories, .subCategories_bottom').slideUp();
            $('.full_menu_Label').removeClass('expanded');
            $(this).addClass('expanded');
            $(this).siblings('ul').slideDown();
        }
    }
});

// modal images
$('.enlarge').click(function () {
    if ($(this).attr('src')) {
        $('#imgEnlarge').attr('src', $(this).attr('src'));
        if ($(this).attr('alt')) {
            $('#imgCaption').text($(this).attr('alt'));
        }
        $('#imgModal').modal('show');
    }
});

// Set Element Widths equal Begin
function setElementsWidthToLargest(elements) {
    var maxWidth = 0;
    elements.each(function (index) {
        maxWidth = $(this).innerWidth() > maxWidth ? $(this).innerWidth() : maxWidth;
    });

    elements.each(function (index) {
        $(this).css("width", maxWidth);
    });
}
/***** Components End **********************************************************************/


/***** Quote Results Page Begin **************************************************************/
$(".edit_quote").click(function () {
    if ($(window).width() < 768) {
        $(".results_page_title").hide();
        $(".results_page_description").hide();
    }
    $(".edit_quote_mlt").show();
    $(".quote_right_mlt").hide();
    $(".quote_results_thank_you").hide();
    $(".quote_left_mlt").addClass("click_on_edit_quote");
    $(".related_links_quote_results").hide();
});

$(".quote_close_click").click(function () {
    if ($(window).width() < 768) {
        $(".results_page_title").show();
        $(".results_page_description").show();
    }
    $(".quote_right_mlt").show();
    $(".edit_quote_mlt").hide();
    $(".quote_left_mlt").removeClass("click_on_edit_quote");
    $(".related_links_quote_results").show();
});

$(".submit_glt").click(function () {
    $(".glt_related_links").show();
    $(".quote_results_thank_you").show();
    $(".mlt_results_card .row").css("border-bottom", "none");
    $(".quote_right_mlt").hide();
    $(".edit_quote_mlt").hide();

});

$(".first_name, .last_name, .phone_number, .email_address").click(function () {
    $(".htr_address").removeClass('hidden');
    $(".htr_city").removeClass('hidden');
    $(".htr_select_state").removeClass('hidden');
    $(".htr_zip_code").removeClass('hidden');
    $(".disclaimer_apply").removeClass('hidden-lg');
    $(".disclaimer_apply").addClass('visible-lg');

    if ($(".disclaimer_apply").hasClass('hidden')) {
        $(".disclaimer_apply").removeClass('hidden');
    } else {
        $(".disclaimer_apply").removeClass('hidden');
    }
});
/***** Quote Results Page End **************************************************************/


/***** Rate Tables Begin ****************************************************************/
if ($(".rate_table").length > 0) {
    var tableColumns = 3;

    // Format Rate Tables
    formatRateTable();

    // Set Rate Table Sizes
    resizeRateTable();

    // Swipe to Next/Previous Set of Columns
    $('.rate_table .content_body').on({
        swipeleft: function () {
            var parent = $(this).closest(".rate_table");
            if (!parent.find(".controls ol li").last().hasClass("active") && $('.controls').is(':visible')) {
                var number = parent.find("td.last").nextAll().length;
                if (number >= tableColumns) {
                    parent.find('.window').animate({right: '+=100%'}, "slow");
                    number = tableColumns;
                }
                else {
                    parent.find('.window').animate({right: '+=' + 100 / tableColumns * number + '%'}, "slow");
                }

                var indicator = parent.find("ol li.active");
                indicator.removeClass("active");
                indicator.next().addClass("active");

                var first = parent.find("td.first");
                var last = parent.find("td.last");
                first.removeClass("first");
                first.nextAll().eq(number - 1).addClass("first");
                last.removeClass("last");
                last.nextAll().eq(number - 1).addClass("last");
            }
        },
        swiperight: function () {
            var parent = $(this).closest(".rate_table");
            if (!parent.find(".controls ol li").first().hasClass("active") && $('.controls').is(':visible')) {
                var number = parent.find("td.first").prevAll().length;
                if (number >= tableColumns) {
                    parent.find('.window').animate({right: '-=100%'}, "slow");
                    number = tableColumns;
                }
                else {
                    parent.find('.window').animate({right: '-=' + 100 / tableColumns * number + '%'}, "slow");
                }

                var indicator = parent.find("ol li.active");
                indicator.removeClass("active");
                indicator.prev().addClass("active");

                var first = parent.find("td.first");
                var last = parent.find("td.last");
                first.removeClass("first");
                first.prevAll().eq(number - 1).addClass("first");
                last.removeClass("last");
                last.prevAll().eq(number - 1).addClass("last");
            }
        }
    });

    // Scrolling for Rate Table
    $('.rate_table .content_body').on("scroll", function () {
        var parent = $(this).closest(".rate_table");
        parent.find(".content_top").scrollLeft($(this).scrollLeft());
        parent.find(".content_left").scrollTop($(this).scrollTop());
    });

    // Open & Close Monthly Rates Dropdown
    $(".monthly_rates .expand_button").click(function () {
        $(this).siblings(".unexpanded").slideToggle(function () {
            resizeRateTable();
        });
        resizeRateTable();
        $(this).find(".expand_button_open").toggleClass("hidden");
        $(this).find(".expand_button_close").toggleClass("hidden");
    });

    // Resize Rate table
    $(window).on("resize", function () {
        resizeRateTable();
    });
}

// Initial Formatting of Rate Table
function formatRateTable() {
    $(".rate_table").each(function () {
        var parent = $(this);

        if (parent.parent().hasClass("two-column-table")) {
            // removes optional components
            parent.find(".content_corner, .content_top, .content_left").remove();
            parent.find(".controls").remove();

            // appends the body content
            parent.find(".content_body table").append(parent.find(".content_temp table tbody"));
        } else {
            // appends corner content
            var cornerContent = parent.find(".content_temp tr:first-child td").eq(0).text();
            parent.find(".content_corner table").append("<tr><td>" + cornerContent + "</td></tr>");

            // appends top content
            var topContent;
            var topLocation = parent.find(".content_top table");
            topLocation.append("<tr></tr>");
            for (var i = 1; i < parent.find(".content_temp tr:first-child td").length; i++) {
                topContent = parent.find(".content_temp tr:first-child td").eq(i).text();
                topLocation.find("tr").append("<td>" + topContent + "</td>");
            }
            var columns = parent.find(".content_top table td").length;
            if (columns == 1) {
                parent.find(".content_top table td").eq(0).addClass("first last");
            } else if (columns <= tableColumns) {
                parent.find(".content_top table td").eq(0).addClass("first");
                parent.find(".content_top table td").eq(columns - 1).addClass("last");
            } else {
                parent.find(".content_top table td").eq(0).addClass("first");
                parent.find(".content_top table td").eq(tableColumns - 1).addClass("last");
            }

            // appends left content
            var leftContent;
            var leftLocation = parent.find(".content_left table");
            for (var i = 1; i < parent.find(".content_temp tr").length; i++) {
                leftContent = parent.find(".content_temp tr").eq(i).children("td").eq(0).text();
                leftLocation.append("<tr><td>" + leftContent + "</td></tr>");
            }

            // appends the body content
            var bodyContent;
            var bodyLocation = parent.find(".content_body table");
            for (var i = 1; i < parent.find(".content_temp tr").length; i++) {
                bodyLocation.append("<tr></tr>");
                for (var j = 1; j < parent.find(".content_temp tr:first-child td").length; j++) {
                    bodyContent = parent.find(".content_temp tr").eq(i).children("td").eq(j).text();
                    bodyLocation.find("tr").eq(i - 1).append("<td>" + bodyContent + "</td>");
                }
            }

            // sets buttons (for mobile)
            var buttons = Math.ceil(columns / tableColumns);
            if (buttons <= 1) {
                parent.find(".controls").hide();
            } else {
                var carousel = parent.find(".carousel-indicators");
                carousel.append("<li class='active'></li>");
                for (i = 0; i < buttons - 1; i++) {
                    carousel.append("<li></li>");
                }
            }
        }

        // removes temporary content
        parent.find(".content_temp").remove();
    });
}

// Resize Rate Table
function resizeRateTable() {
    $(".rate_table").each(function () {
        var parent = $(this);
        if (!parent.parent().hasClass("two-column-table")) {
            var columns = parent.find(".content_top tr td").length;
            var rows = parent.find(".content_left tr").length;
            var height = parseInt(parent.find(".content_left").css("max-height"));

            // Set widths for all elements
            if (!$(".hidden-xs").is(":visible") && !parent.hasClass("table-mobile")) {
                var visible;
                if (columns >= tableColumns) {
                    visible = tableColumns;
                } else {
                    visible = columns;
                }
                parent.find(".content_left, .content_body").removeAttr("style");
                parent.find(".content_corner, .content_left").css("width", 1 / (visible + 1) * 100 + "%");
                parent.find(".content_top, .content_body").css("width", visible / (visible + 1) * 100 + "%");
                parent.find(".window").css("width", columns / visible * 100 + "%");
                parent.find("td").css("width", 1 / columns * 100 + "%");

                parent.removeClass("table-nonmobile");
                parent.addClass("table-mobile");
            }

            // Set column width for tablet/ desktop
            if ($(".hidden-xs").is(":visible") && !parent.hasClass("table-nonmobile")) {
                var width;
                var max_columns;
                if (parent.parent().hasClass("comparison-table")) {
                    max_columns = 4;
                } else {
                    max_columns = 10;
                }

                if ((columns + 1) > max_columns) {
                    width = 100 / max_columns;

                    parent.find(".content_left, .content_body").removeAttr("style");
                    parent.find(".content_corner, .content_left").css("width", width + "%");
                    parent.find(".content_top, .content_body").css("width", width * (max_columns - 1) + "%");
                    parent.find(".window").css("width", columns / (max_columns - 1) * 100 + "%");
                    parent.find("td").css("width", 1 / columns * 100 + "%");
                    parent.find(".content_left").css("max-height", height + "px");
                    parent.find(".content_body").css("max-height", height + 17 + "px");
                } else {
                    width = 100 / (columns + 1);

                    parent.find(".content_left, .content_body").removeAttr("style");
                    parent.find(".content_corner, .content_left").css("width", width + "%");
                    parent.find(".content_top, .content_body").css("width", width * (columns) + "%");
                    parent.find(".window").css("width", 100 + "%");
                    parent.find("td").css("width", 1 / columns * 100 + "%");
                }

                parent.removeClass("table-mobile");
                parent.addClass("table-nonmobile");
            }

            // Vertical height
            var content_left = parent.find(".content_left");
            if (content_left.height() >= content_left.find(".content_container").height()) {
                content_left.css("padding-bottom", "0px")
            }

            // Fix widths for scrollbar
            parent.find(".content_top").width(parent.find(".content_body .content_container").width() - 1);
        }
    });
}

/***** Rates Tables End ****************************************************************/


/***** Carousel Swiping Begin ***************************************************************/
$("#productCompCarousel, #myCarouselBanner, #myCarouselTablet").on({
    swipeleft: function () {
        $(this).carousel('next');
    },
    swiperight: function () {
        $(this).carousel('prev');
    }
});

$("#myCarousel").on({
    swipeleft: function () {
        $(this).carousel('next');
        $("#myCarouselBottom").carousel('next');
    },
    swiperight: function () {
        $(this).carousel('prev');
        $("#myCarouselBottom").carousel('prev');
    }
});
/***** Carousel Swiping End ***************************************************************/


/***** Overlay Components Begin ***********************************************************/
$('[data-quoteToolLink="healthClass"]').click(function () {
    $(".health_guidelines_overlay").show();
    $("html, body").animate({scrollTop: 0}, "slow");
});

$(".close_health_overlay").click(function () {
    $(".health_guidelines_overlay").hide();
});

$(".health_guidelines_nav a").click(function () {
    var element = $(this).attr("href");
    $(this).closest(".content_table").find("td.content, th.content").addClass("hidden-xs");
    $(this).closest(".content_table").find(element).removeClass("hidden-xs");
});

$(".vision_dental_overlay_close").click(function () {
    $(".dental_overlay").addClass("hidden");
    $(".vision_overlay").addClass("hidden");
});

$(".vision_overlay_link").click(function () {
    $(".vision_overlay").removeClass("hidden");
    $("html, body").animate({scrollTop: 0}, "slow");
});

$(".dentist_overlay_link").click(function () {
    $(".dental_overlay").removeClass("hidden");
    $("html, body").animate({scrollTop: 0}, "slow");
});

// Opens View All Rates Overlay
$(".view-rates-open").on("click", function () {
    $(".view-all-rates").show();
    $("html, body").animate({scrollTop: 0}, 0);
    resizeRateTable();
});

// Closes View All Rates Overlay
$(".view-all-rates .view-close").on("click", function () {
    $(".view-all-rates").hide();
});

// Change  View All Rates Overlay Active Table
$(".view-all-rates .view-nav li").on("click", function () {
    var element = $(this);
    var index = element.index();

    // change active nav
    $(this).siblings().removeClass("active");
    $(this).addClass("active");

    // change active table
    var parent = $(this).closest(".view-all-rates");
    parent.find(".view-content").children().removeClass("active");
    parent.find(".view-content").children().eq(index).addClass("active");
    resizeRateTable();
});
/***** Overlay Components End ***********************************************************/


/***** Radio Selector Begin ***********************************************************/
// Sets the radio button image
/*function setRadioImage() {
 //change this to the new image folder
 if ($(".online_cta_check").is(':checked')) {
 $(".radio_online_button").attr("src", "/global-assets/static/images/form/form_radio_selected.png");
 $(".radio_rep_button").attr("src", "/global-assets/static/images/form/form_radio_unselected.png");
 $(".results_radio_online").attr("src", "/global-assets/static/images/form/form_radio_selected.png");
 $(".results_radio_rep").attr("src", "/global-assets/static/images/form/form_radio_unselected.png");
 $(".radio_qt_online").attr("src", "/global-assets/static/images/form/form_radio_selected.png");
 $(".radio_qt_rep").attr("src", "/global-assets/static/images/form/form_radio_unselected.png");
 } else {
 $(".radio_rep_button").attr("src", "/global-assets/static/images/form/form_radio_selected.png");
 $(".radio_online_button").attr("src", "/global-assets/static/images/form/form_radio_unselected.png");
 $(".results_radio_rep").attr("src", "/global-assets/static/images/form/form_radio_selected.png");
 $(".results_radio_online").attr("src", "/global-assets/static/images/form/form_radio_unselected.png");
 $(".radio_qt_online").attr("src", "/global-assets/static/images/form/form_radio_unselected.png");
 $(".radio_qt_rep").attr("src", "/global-assets/static/images/form/form_radio_selected.png");
 }
 }

 // Radio button click for MLT
 $(".radio_online_button").click(function () {
 $(".online_cta_check").attr("checked", true);
 $(".rep_cta_check").removeAttr("checked");
 $(".online_cta_check").prop("checked", true);
 $(".rep_cta_check").prop("checked");
 QuoteToolAPI.quoteToolType = 'MLT';
 setRadioImage();
 });

 $(".radio_text_online").click(function () {
 $(".online_cta_check").attr("checked", true);
 $(".rep_cta_check").removeAttr("checked");
 $(".online_cta_check").prop("checked", true);
 $(".rep_cta_check").prop("checked");
 QuoteToolAPI.quoteToolType = 'MLT';
 setRadioImage();
 });

 $(".radio_text_advisor").click(function () {
 $(".online_cta_check").removeAttr("checked");
 $(".rep_cta_check").attr("checked", true);
 $(".online_cta_check").prop("checked");
 $(".rep_cta_check").prop("checked", true);
 QuoteToolAPI.quoteToolType = 'GLT';
 setRadioImage();
 });

 // Radio button click for GLT
 $(".radio_rep_button").click(function () {
 $(".online_cta_check").removeAttr("checked");
 $(".rep_cta_check").attr("checked", true);
 $(".online_cta_check").prop("checked");
 $(".rep_cta_check").prop("checked", true);
 QuoteToolAPI.quoteToolType = 'GLT';
 setRadioImage();
 });*/
/***** Radio Selector End ***********************************************************/


/***** Find an X Begin **************************************************************/
var geocoder;

$(".geo_location_image").on('click touchstart', function () {
    if ($(window).width() < 1025) {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(handle_geolocation_home_query);
        }
    }
});

$(".find_an_x_input").on('focus', function () {
    $(this).attr("placeholder", "");
});

$(".find_an_x_input").on('blur', function () {
    var location;
    if ($(this).hasClass("find_x_location3")) {
        location = $(".find_x_location3").val();
        if (location === "") {
            $(this).addClass("find_an_x_input_error");
            $(this).attr("placeholder", "Zip or City, State");
            $("#select_vision_provider").addClass("find_an_x_input_error");
            $("#select_vision_provider").addClass("errorArrow");
        } else {
            $(this).removeClass("find_an_x_input_error");
            $("#select_vision_provider").removeClass("find_an_x_input_error");
            $("#select_vision_provider").removeClass("errorArrow");
        }
    }
    if ($(this).hasClass("find_x_location2")) {

        location = $(".find_x_location2").val();
        if (location === "") {
            $(this).addClass("find_an_x_input_error");
            $(this).attr("placeholder", "Zip or City, State");
            $("#select_dentist_network").addClass("find_an_x_input_error");
            $("#select_dentist_network").addClass("errorArrow");
        } else {
            $(this).removeClass("find_an_x_input_error");
            $("#select_dentist_network").removeClass("find_an_x_input_error");
            $("#select_dentist_network").removeClass("errorArrow");
        }
    }
    if ($(this).hasClass("find_x_location1")) {
        location = $(".find_x_location1").val();
        if (location === "") {
            $(this).addClass("find_an_x_input_error");
            $(this).attr("placeholder", "Zip or City, State");
        } else {
            $(this).removeClass("find_an_x_input_error");
        }
    }
});

$('.find_an_x_input').keyup(function (e) {
    // console.log("Handler for .keyup() called.", e.keyCode, $(this).attr('class'));
    if (e.keyCode == 13) {
        if ($(this).hasClass('find_x_location1')) {
            $("#ctaHeaderFAX1").click();
        }
        else if ($(this).hasClass('find_x_location2')) {
            $("#ctaHeaderFAX2").click();
        }
        else if ($(this).hasClass('find_x_location3')) {
            $("#ctaHeaderFAX3").click();
        }
    }
});

if ($(".cta_header_find_x").length > 0) {
    document.querySelector(".cta_header_find_x").addEventListener("invalid", function (event) {
        event.preventDefault();
    }, true);
}

$('#ctaHeaderFAX1').click(function () {
    $(".find_x_location1").attr("placeholder", "");
    var location = $(".find_x_location1").val();
    var isNumber = /^\d+$/.test(location);
    if (!isNumber || (isNumber && (location.length == 5))) {
        if (location === "") {
            $(".find_x_location1").attr("placeholder", "Zip or City, State");
            $(".find_x_location1").addClass("find_an_x_input_error");
        } else {
            var x = window.location.href;
            //var y = x.substring(0, x.lastIndexOf("/"));
            sessionStorage.setItem("faoZipCode", $(".find_x_location1").val());
            var y = $(this).attr('data-href') ;
            window.location.href = y;

        }
    }
    else {
        $('.find_office_wrap .errorSpan.error_zip_code').removeClass('hidden');
    }
});

$('#ctaHeaderFAX2').click(function () {
    $(".find_x_location2").attr("placeholder", "");
    var location = $(".find_x_location2").val();
    var isNumber = /^\d+$/.test(location);
     var selectedOption = $('#select_dentist_network').val().trim();
    if (!isNumber || (isNumber && (location.length == 5))) {
        if (location === "") {
            $(".find_x_location2").attr("placeholder", "Zip or City, State");
            $(".find_x_location2").addClass("find_an_x_input_error");
        } else if (selectedOption === "") {
            $("#select_dentist_network").addClass("find_an_x_input_error");
        } else {
            var str = "https://metlocator.metlife.com/metlocator/execute/Search?searchType=findDentistMetLife&networkID=2&zip=";
            var val2 = "&qsType=" + $('#select_dentist_network').val();
            str += location + val2;
            if (!($('#select_dentist_network').val() == 'TRICARE'))
                window.location.href = str;
            else
                window.location.href = "http://www.metlife.com/tricare";
        }
    }
    else {
        $('.find_dentist_wrap .errorSpan.error_zip_code').removeClass('hidden');
    }
});

$('#ctaHeaderFAX3').click(function () {
    $(".find_x_location3").attr("placeholder", "");
    var location = $(".find_x_location3").val();
    var isNumber = /^\d+$/.test(location);
    var selectedOption = $('#select_vision_provider').val().trim();
    if (!isNumber || (isNumber && (location.length == 5))) {
        if (location === "") {
            $(".find_x_location3").attr("placeholder", "Zip or City, State");
            $(".find_x_location3").addClass("find_an_x_input_error");
        } else if (selectedOption === "") {
            $("#select_vision_provider").addClass("find_an_x_input_error");
        } else {
            var str = "https://mymetlifevision.com/find-provider-location.html?zip=";
            var val2 = "&net=" + $('#select_vision_provider').val();
            str += location + val2;
            if (!($('#select_vision_provider').val() == 'SafeGuard'))
                window.location.href = str;
            else
                window.location.href = "https://www.metlife.com/individual/insurance/dental-insurance/vision-providers/vision-facility-reference-guides.html";
        }
    }
    else {
        $('.find_vision_wrap .errorSpan.error_zip_code').removeClass('hidden');
    }
});

$("#select_dentist_network").on('change', function () {
    var selectedOption = $(this).val();
    if (selectedOption === "") {
        $(this).addClass("find_an_x_input_error");
        $(this).addClass("errorArrow");
    } else {
        $(this).removeClass("find_an_x_input_error");
        $(this).removeClass("errorArrow");
    }
});

$("#select_vision_provider").on('change', function () {
    var selectedOption = $(this).val();
    if (selectedOption === "") {
        $(this).addClass("find_an_x_input_error");
        $(this).addClass("errorArrow");
    } else {
        $(this).removeClass("find_an_x_input_error");
        $(this).removeClass("errorArrow");
    }
});

$(".find_an_x_cta_search .mobile_expand_open").on('click', function (event) {
    event.preventDefault();
    if ($(".mobile_expand_close").not(":visible") && !$(".hidden-xs").is(":visible") ) {
        $(".find_an_x_cta_search .mobile_expand_close").show();
        $(".find_an_x_cta_search .mobile_expand").slideDown();
        $(".error_zip_code").insertAfter(".input_field_container");
    }
});

$(".find_an_x_cta_search .mobile_expand_close").on('click', function (event) {
    event.preventDefault();
    if ($(".mobile_expand_close").is(":visible")  && !$(".hidden-xs").is(":visible")) {
        $(".find_an_x_cta_search .mobile_expand_close").hide();
        $(".find_an_x_cta_search .mobile_expand").slideUp();
        $(".error_zip_code").insertAfter(".mobile_expand");
    }
});

// Initialize FAX
function ctaHeaderFAXinit() {
    //style first button as selected
    $('.btn-group-justified .btn-group .btn').eq(0).addClass('btn_fax_selected');
    $('.btn-group-justified img').eq(0).addClass('btn_select_arrow_selected');
    $('.find_an_x_wrap').eq(0).removeClass('hidden');

    //style button based on click
    $(".btn-group").click(function () {
        var index = $('.btn-group-justified .btn-group').index($(this));
        $('.find_an_x_wrap').addClass('hidden');
        $('.find_an_x_wrap').eq(index).removeClass('hidden');
        $('.btn-group-justified .btn-group .btn').removeClass('btn_fax_selected');
        $('.btn-group-justified img').removeClass('btn_select_arrow_selected');
        $(this).find(".btn").addClass('btn_fax_selected');
        $(this).find("img").addClass('btn_select_arrow_selected');
    });

}

// Geolocation
function handle_geolocation_home_query(position) {
    geocoder = new google.maps.Geocoder();
    var latlng = new google.maps.LatLng(position.coords.latitude, position.coords.longitude);
    var geocoder = geocoder = new google.maps.Geocoder();
    geocoder.geocode({'latLng': latlng}, function (results, status) {
        if (status == google.maps.GeocoderStatus.OK) {
            if (results[1]) {
                var arrAddress = results[1].address_components;
                var itemLocality = "";
                $.each(arrAddress, function (i, address_component) {
                    if (address_component.types[0] == "locality") {
                        itemLocality = address_component.long_name;
                    }
                    if (address_component.types[0] == "administrative_area_level_1") {
                        itemLocality += ', ' + address_component.long_name;
                    }
                    $('.find_an_x_input.find_x_location1').val(itemLocality);
                });
            }
        }
    });

}

// Gmaps Auto-complete Begin
function gmapsAutoCompleteInit() {
    $('.gmaps-auto-complete').each(function () {
        new google.maps.places.Autocomplete($(this)[0]);
    });
}

//Begin Pass user address to FAX page
function getURLParameter(sParam) {
    var sPageURL = window.location.search.substring(1);
    var sURLVariables = sPageURL.split('&');
    //console.log(sPageURL);
    //console.log(sURLVariables);
    for (var i = 0; i < sURLVariables.length; i++) {
        var sParameterName = sURLVariables[i].split('=');
        if (sParameterName[0] == sParam) {
            return sParameterName[1];
        }
    }
}

//Begin Pass user address to FAX page
function parseUserAddress() {
    var paramValue = getURLParameter("find_an_x_location");
    if (paramValue) {
        $(".cta_search").val(decodeURIComponent(paramValue).replace(/\+/g, " "));
        $(".directions_button").click();
    }
}
/***** Find an X  End********************************************************************/


/***** Begin Country Selector ***********************************************************/
$('.country_selector_container').click(function () {
    if ($(".hidden-xs").is(":visible")) {
        var top = $(".country_selector_container").height();
        $(".country_list_container").css("top", top + "px");
    }
    $('.country_list_container').slideToggle(500).scrollTop(0);
    return false;
});

$(window).resize(function () {
    var top = $(".country_selector_container").height();
    $(".country_list_container").css("top", top + "px");
    if (!$(".hidden-xs").is(":visible")) {
        $(".country_list_container").css("top", "");
    }
});


$('.country_group').click(function () {
    var selectedCountry = $('.country_selector_container .selected');
    selectedCountry.find('.country_flag').attr('src', $(this).find('.country_flag').attr('src'));
    selectedCountry.find('.country_name').text($(this).find('div.country_name').text());
    if ($(this).attr('data-redirect') !== "" && $(this).attr('data-redirect')) {
        window.location.href = $(this).attr('data-redirect');
    } else {
        alert("Missing URL for " + $(this).find('div.country_name').text());
    }
});

// Close country selector when not accessing
$(document).on("click tap", function (e) {
    var container = $(".country_list_container");
    if (!container.is(e.target)
        && container.has(e.target).length === 0) {
        container.slideUp(500);
    }
});
/***** End Country Selector *************************************************************/


/***** Glossary Begin **********************************************/
if ($(".glossary").length > 0) {
    // Initialize the Glossary Selector
    glossarySelectorInitialize();

    // Swipe for Glossary Letter Selector
    $(".glossary-selector").on({
        swipeleft: function () {
            var glossary = $(".glossary-selector .container");
            var width = parseFloat(glossary[0].style.width);
            var position = width - (100 + parseFloat(glossary[0].style.right));

            if (position > 100) {
                glossary.animate({right: '+=100%'}, "slow");
            } else {
                glossary.animate({right: '+=' + position + '%'}, "slow");
            }
        },
        swiperight: function () {
            var glossary = $(".glossary-selector .container");
            var position = parseFloat(glossary[0].style.right);

            if (position > 100) {
                glossary.animate({right: '-=100%'}, "slow");
            } else {
                glossary.animate({right: '-=' + position + '%'}, "slow");
            }
        }
    });

    // Navigate to Selected Letter
    $(".glossary-selector a").on("click", function (evt) {
        evt.preventDefault();
        if ($(this).hasClass("active")) {
            var height = $(".global_header").height() + $(".glossary-selector .selector").height();
            var location = $(".glossary-group h2:contains(" + $(this).attr("data-link") + ")");
            $('html,body').animate({scrollTop: location.offset().top - height}, 'slow');
        }
    });

    // Scroll and Resize for Glossary Selector
    $(window).on({
        scroll: function () {
            glossarySelectorPosition();
        },
        resize: function () {
            glossarySelectorSize()
        }
    });
}

// Initializes the Glossary Selector
function glossarySelectorInitialize() {
    var glossary_letters = $(".glossary-group").find("h2").text();
    var selector = $(".glossary-selector");
    var selectorContainer = $(".glossary-selector .container");

    glossary_letters = glossary_letters.toLowerCase().split("").sort();
    selectorContainer.find("a").each(function () {
        var link = $(this);
        var link_letters = link.attr("data-letters").toLowerCase().split("").sort();
        link_letters.forEach(function (val) {
            if (glossary_letters.indexOf(val) > -1) {
                link.addClass("active");
                if (link.attr("data-link") == undefined) {
                    link.attr("data-link", val);
                }
            }
        });
    });
    glossarySelectorSize();

    selectorContainer.css("right", "0%");
    $(".page_title").addClass("glossary-top");

    selector.show();
    selector.addClass("glossary-height");
}

// Resizes the Glossary Selector on
function glossarySelectorSize() {
    var parent = $(".glossary-selector");
    var container, link, length;

    // Set widths for mobile
    if (!$(".hidden-xs").is(":visible") && !parent.hasClass("glossary-mobile")) {
        container = parent.find(".container");
        link = parent.find("a");
        length = link.length;

        // set container width
        if (length <= 3) {
            container.css("width", "100%");
        } else {
            if (link.parent().hasClass("letter-group")) {
                container.css("width", length * 30 + "%");
            } else {
                container.css("width", length * 15 + "%");
            }
        }

        // set letter width
        link.each(function () {
            $(this).css("width", 100 / length + "%");
        });

        parent.removeClass("glossary-nonmobile");
        parent.addClass("glossary-mobile");
    }

    // Set widths for tablet/ desktop
    if ($(".hidden-xs").is(":visible") && !parent.hasClass("glossary-nonmobile")) {
        link = parent.find("a");
        length = link.length;

        // set container width
        parent.find(".container").css("width", "");

        // set letter width
        if (length <= 5) {
            link.each(function () {
                $(this).css("width", "20%");
            });
        } else {
            link.each(function () {
                $(this).css("width", 100 / length + "%");
            });
        }

        parent.removeClass("glossary-mobile");
        parent.addClass("glossary-nonmobile");
    }
}

// Sets the Glossary Selector Position
function glossarySelectorPosition() {
    if ($(window).scrollTop() > $(".glossary-selector").offset().top - $(".global_header").height()) {
        $('.glossary-selector .selector').addClass("fixed");
    } else {
        $('.glossary-selector .selector').removeClass("fixed");
    }
}
/***** Glossary End **********************************************/


/***** product comparison chart carousel ******8******************/
function productComparisonChart() {

    // Init first block
    $('.product_chart .carousel-item').removeClass('selected');
    $('.product_chart .carousel-item:first-child').addClass('selected');
    $('.product_chart .carousel-tab:first-child').show();


    // Determine how many blocks to show based on screen width
    var splitter = 0;
    var slides = $(".product_chart .carousel-inner [class*='col-']");
    switch (false) {
        case ($(".hidden-xs").is(":visible")):
            splitter = 1;
            break;
        default:
            splitter = 5;
    }
    $('.product_chart .item [class*=col-]').removeClass('active').unwrap();
    for (var i = 0; i < slides.length; i += splitter) {
        slides.slice(i, i + splitter).wrapAll("<div class='item'></div>");
    }

    $('.product_chart .item:first-child').addClass('active');


    // Style first tab after a carousel slide
    $('#productComparisonChartCarousel').bind('slid.bs.carousel', function (e) {
        var nextIndex = $('.item.active > .carousel-item').attr('data-target');
        $('.product_chart .carousel-item').eq(nextIndex).addClass('selected');
    });

}
/***** product comparison chart carousel change *********************/


/***** Blog Post Begins ***********************************************************/
// Hide/show popular blog posts
$(".showPopular").click(function () {
    $(".blog-sidebar-header span").removeClass("selected-category");
    $(this).addClass("selected-category");
    $(".popular-content").show();
    $(".recent-content").hide();
});

//Hide/show recent blog posts
$(".showRecent").click(function () {
    $(".blog-sidebar-header span").removeClass("selected-category");
    $(this).addClass("selected-category");
    $(".recent-content").show();
    $(".popular-content").hide();
});

if ($(".bread-crumb span:last").text().length > 100) {
    $(".bread-crumb span:last").text($(".bread-crumb span:last").text().substring(0, 97) + "...");
}

$('video').click(function () {
    if (this.paused) {
        this.play();
    }
    else {
        this.pause();
    }
});

$('video').hover(function toggleControls() {
    if (this.hasAttribute("controls")) {
        this.removeAttribute("controls")
    } else {
        this.setAttribute("controls", "controls")
    }
});

$('.enlarge').click(function () {
    if ($(this).attr('src')) {
        $('#imgEnlarge').attr('src', $(this).attr('src'));
        $('#imgCaption').text($(this).attr('alt'));
    }
});

function blogCategoryMobileDesign() {
    if ($(".hidden-xs").is(":visible") == false) {
        $(".blog-sidebar").insertAfter(".mightAlsoLike");
    }
    else {
        if ($(".body").length != 0) {
            $(".blog-sidebar").insertAfter(".body");
        }
        if ($(".blog-article-list").length != 0) {
            $(".blog-sidebar").insertAfter(".blog-article-list");
        }
    }
}

/*
 // Conversion of video seconds to format
 function secondsToHms(d) {
 d = Number(d);
 var h = Math.floor(d / 3600);
 var m = Math.floor(d % 3600 / 60);
 var s = Math.floor(d % 3600 % 60);
 return ((h > 0 ? h + ":" + (m < 10 ? "0" : "") : "") + m + ":" + (s < 10 ? "0" : "") + s);
 }
 */
/***** Blog Post End ***********************************************************/


function faoHeaderOptional() {
    var sel = $('.cta_header_find_x');
    if (sel.length == 0) {
        var current = $(".bread-crumb").children();
        $(".cta_header_quote").addClass("noFao");
    }
}


function removeDividerOnCampaignPageD() {
    if (!$('.campaign-card-addendum').text().trim().length) {
        $('.campaign-card-addendum').remove();
    }
}

function matchLargeProductModuleHeights() {
    if ($(".hidden-xs").is(":visible")) {
        if ($(".large_product_module_wrapper").length != 0) {
            $(".large_product_module_wrapper").each(function (index) {
                var largeProductTop = $(this).find(".large-product-top-section");
                var largeProductBottom = $(this).find(".large-product-bottom-section");
                var lgProductTopHeight = 0;
                var lgProductBottomHeight = 0;

                largeProductTop.css('min-height', '0px');
                largeProductTop.each(function () {

                    lgProductTopHeight = $(this).outerHeight() > lgProductTopHeight ? $(this).outerHeight() : lgProductTopHeight;

                });
                largeProductTop.css('min-height', lgProductTopHeight + 'px');


                largeProductBottom.css('min-height', '0px');
                largeProductBottom.each(function () {


                    lgProductBottomHeight = $(this).outerHeight() > lgProductBottomHeight ? $(this).outerHeight() : lgProductBottomHeight;

                });
                largeProductBottom.css('min-height', lgProductBottomHeight + 'px');

            });
        }
    } else {

        if ($(".large_product_module_wrapper").length != 0) {
            $(".large_product_module_wrapper").each(function () {
                var largeProductTop = $(this).find(".large-product-top-section");
                var largeProductBottom = $(this).find(".large-product-bottom-section");

                largeProductTop.css('min-height', 'auto');
                largeProductBottom.css('min-height', 'auto');
            });
        }
    }

};

function matchMediumProductModuleHeights() {

    if ($(".hidden-xs").is(":visible")) {
        if ($(".medium_product_module_wrapper").length != 0) {
            $(".medium_product_module_wrapper").each(function () {


                var mediumProductTop = $(this).find(".medium-product-card-top");
                var mediumProductBottom = $(this).find(".medium-product-card-bottom");

                var medTopHeight = 0;
                var medBottomHeight = 0;


                mediumProductTop.css('min-height', '0px');
                mediumProductTop.each(function () {


                    medTopHeight = $(this).outerHeight() > medTopHeight ? $(this).outerHeight() : medTopHeight;

                });
                mediumProductTop.css('min-height', medTopHeight + 'px');

                mediumProductBottom.css('min-height', '0px');
                mediumProductBottom.each(function () {


                    medBottomHeight = $(this).outerHeight() > medBottomHeight ? $(this).outerHeight() : medBottomHeight;

                });
                mediumProductBottom.css('min-height', medBottomHeight + 'px');

            });
        }
    } else {

        if ($(".medium_product_module_wrapper").length != 0) {
            $(".medium_product_module_wrapper").each(function () {
                var mediumProductTop = $(this).find(".medium-product-card-top");
                var mediumProductBottom = $(this).find(".medium-product-card-bottom");

                mediumProductTop.css('min-height', 'auto');
                mediumProductBottom.css('min-height', 'auto');

            });
        }
    }

};


/***** Cookie Banner Begins ***********************************************************/
var domain = getDomain(document.URL);
var gaReferrer = false;
//var hasAcceptanceCookie = false;

if ($(".cookieShell").length > 0) {
    if (createCookie === undefined) {
        var createCookie = false;
    }
    //if (cookieName === undefined) {
    //    var cookieName = "MLALUKCookiesAccepted";
    //}
    $("a").click(function () {
        if ($(this).attr("class") != "privacyPolicy" && createCookie == true) {
            checkExistance(cookieName);
            if (hasAcceptanceCookie == false) {
                setCookie(cookieName, "yes", cookieExpiry, "/", domain, "");
            }
        }
    });

    //if the cookie acceptance checkox is unchecked, drop the cookie right away
    if (createCookie == false) {
        checkExistance(cookieName);
        if (hasAcceptanceCookie == false) {
            setCookie(cookieName, "yes", cookieExpiry, "/", domain, "");
        }
    }

    // Will not do anything unless checkbox for creating cookies is selected
    if (createCookie == true || Allowimmediatesiteanalytics == true) {
        checkExistance(cookieName);

        if (hasAcceptanceCookie == false) {
            enterCookie();
        }

        deleteCookies();
    }


    var cookieHeight = $(".cookieShell").height();

    $(".global_header").css("top", cookieHeight + 1);
    $(".campaign-header").css("top", cookieHeight + 1);

    if (($(window).width() <= 767)) {
        if ($(".campaign-header").length > 0) {
            var campheight = $(".global_header").height() + cookieHeight;
            $(".megaMenu").css("top", campheight + 51);
        } else {
            var headerHeight = $(".campaign-header").height() + cookieHeight;
            $(".megaMenu").css("top", headerHeight + 51);
        }
    } else {
        if ($(".campaign-header").length > 0) {
            var campheight = $(".global_header").height() + cookieHeight;
            $(".megaMenu").css("top", campheight + 1);
        } else {
            var headerHeight = $(".campaign-header").height() + cookieHeight;
            $(".megaMenu").css("top", headerHeight + 1);
        }
    }
}

$(window).resize(function () {
    var cookieHeight = $(".cookieShell").height();
    if ($('.cookie-message-container').css("display") !== 'none' && $(".cookieShell").length > 0) {
        $(".global_header").css("top", cookieHeight + 1);
        $(".campaign-header").css("top", cookieHeight + 1);
        if (($(window).width() <= 767)) {
            if ($(".campaign-header").length > 0) {
                var campheight = $(".global_header").height() + cookieHeight;
                $(".megaMenu").css("top", campheight + 51);
            } else {
                var headerHeight = $(".campaign-header").height() + cookieHeight;
                $(".megaMenu").css("top", headerHeight + 51);
            }
        } else {
            if ($(".campaign-header").length > 0) {
                var campheight = $(".global_header").height() + cookieHeight;
                $(".megaMenu").css("top", campheight + 1);
            } else {
                var headerHeight = $(".campaign-header").height() + cookieHeight;
                $(".megaMenu").css("top", headerHeight + 1);
            }
        }
    } else {
        $(".global_header").css("top", "0px");
        $(".campaign-header").css("top", "0px");
        $(".megaMenu").css("top", "0px");
        if (($(window).width() <= 767)) {
            $(".megaMenu").css("top", "50px");
        }
    }
});

$(".cookie_submit").click(function () {
    $(".global_header").css("top", "0px");
    $(".campaign-header").css("top", "0px");
    if (($(window).width() >= 767)) {
        $(".megaMenu").css("top", "0px");
    } else {
        $(".megaMenu").css("top", "50px");
    }
});

function checkExistance() {
    if ($.cookie(cookieName) != undefined) {
        hasAcceptanceCookie = true;
    }
    else {
        hasAcceptanceCookie = false;
    }
}

function enterCookie() {
    if (Allowimmediatesiteanalytics == false) {
        showCookieBannerMessage();
    }
    else {
        setCookie(cookieName, "yes", cookieExpiry, "/", domain, "");
        showCookieBannerMessage();
    }
}

function getDomain(url) {
    return url.match(/:\/\/(.[^/]+)/)[1];
}

function showCookieBannerMessage() {
    $('.cookie-message-container').removeClass("hidden");
}

function deleteCookies() {
    // Remove cookies set by this application [Google Analytics, WebTrends, alicoRerral]
    if (cookieDelete) {
        var path = "/";
        var domain = getDomain(document.URL);
        var deleteCookie = cookieNamesDelete.split(';');

        for (var i = 0; i < deleteCookie.length; i++) {
            $.removeCookie(deleteCookie[i], {path: path});
        }
    }
}

function setCookie(name, value, expires, path, domain, secure) {
    if (expires == 0) {
        $.cookie(name, value, {
            path: path,
            domain: domain,
            secure: secure
        });
    }
    else {
        $.cookie(name, value, {
            expires: expires,
            path: path,
            domain: domain,
            secure: secure
        });
    }
}

function setAcceptCookieDesktop(hasAppCookie) {
    if (hasAppCookie) {
        createReferral();
    }

    checkExistance(cookieName);
    if (hasAcceptanceCookie == false) {
        setCookie(cookieName, "yes", cookieExpiry, "/", domain, "");
    }
    $('.cookie-message-container').slideUp();
}

function createReferral() {
    // set cookie when the page has referral
    var referrerEmpty = '';
    var referrer = document.referrer;
    var bMatch = false;
    //var ignoredUrls =
    //    ["http://uae.alico.com",
    //        "https://uae.alico.com",
    //        "http://www.uae.alico.com",
    //        "https://www.aue.alico.com",
    //        "http://stage.uae.alico.com",
    //        "https://stage.uae.alico.com",
    //        "http://secure.uae.alico.com",
    //        "https://secure.uae.alico.com"];
    var ignoredUrlPattern = /^http(s)?:\/\/(((stage|stage2|stage|teststage|metlifestage|www)\.)?([a-z]{0,3}\.)?alico.com|(www\.)?interamericana.cl)/;
    if (referrer.match(ignoredUrlPattern)) bMatch = true;
    // if the referrer is not from our domain, then this is the first time.

    if (Allowimmediatesiteanalytics == false) {
        if (!bMatch && hasAcceptanceCookie) {
            setCookie("alicoReferral", document.referrer, 0, "/", domain, "");
        }
        if (referrer == "" && hasAcceptanceCookie) {
            setCookie("alicoReferral", referrerEmpty, 0, "/", domain, "");
        }
    }
    else {
        if (!bMatch) {
            setCookie("alicoReferral", document.referrer, 0, "/", domain, "");
        }
        if (referrer == "") {
            setCookie("alicoReferral", referrerEmpty, 0, "/", domain, "");
        }
    }
}

/***** Cookie Banner End ***********************************************************/


/***** Privacy Categories**************************************************************/
/*$('a[href*=#]:not([href=#])').click(function () {
 if (location.pathname.replace(/^\//, '') == this.pathname.replace(/^\//, '') && location.hostname == this.hostname) {
 var target = $(this.hash);
 target = target.length ? target : $('[name=' + this.hash.slice(1) + ']');
 if (target.length) {
 $('html,body').animate({
 scrollTop: target.offset(50).top
 }, 300);
 return false;
 }
 }
 });*/

$(".toggle").click(function () {
    // hides children divs if shown, shows if hidden
    $(this).children("div").toggle();
});
/**************End**********************************************************************/


/****** Microsite Begin ************************************************************/
// Open Menu
$(".microsite_menu_open").on("click", function (e) {
    $('.microsite_menu_open,.microsite_menu_close').toggleClass('hidden');
    if ($(".visible-xs").is(":visible")) {

        $('.sub_navigation_menu').css("left", "-800");
        $('.sub_navigation_menu').stop().animate({left: '0'}, 300);
        $('.sub_navigation_menu').addClass('micro-site-mobile');

    } else {
        $('body').css("height", "auto");
    }
});

//Close Menu
$(".microsite_menu_close").on("click", function (e) {
    $('.microsite_menu_open,.microsite_menu_close').toggleClass('hidden');
    if ($(".visible-xs").is(":visible")) {
        $('.megaMenu').stop().animate({left: '-800'}, 300);
        $('.megaMenu').css("left", "-800");
        $('body').css("height", "auto");
        $('.sub_navigation_menu').removeClass('micro-site-mobile');
    } else {

    }
});

// Microsite compariion chart
function micrositeCarouselSetup() {
    $(".microsite-product-chart .carousel .carousel-inner .carousel-item").each(function () {
        var columnNum = $(this).children(".column-wrapper").length;
        switch (columnNum) {
            case (1):

                if ($(".visible-xs").is(":visible")) {
                    if ($(this).parent().next().length != 0) {

                        $(this).find(".microsite-column-category").addClass("mcc2");
                        $(this).parent().next().find(".carousel-item").find(".microsite-column-category").clone().appendTo($(this)).addClass("visible-xs mcc2");
                        $(this).parent().next().find(".carousel-item").find(".column-wrapper").first().clone().appendTo($(this)).addClass("visible-xs col2");


                    } else {
                        $(this).find(".microsite-column-category").addClass("mcc2");
                        $(".carousel-item").first().find(".microsite-column-category").first().clone().appendTo($(this)).addClass("visible-xs mcc2");
                        $(".carousel-item").first().find(".column-wrapper").first().clone().appendTo($(this)).addClass("visible-xs col2");

                    }
                } else {
                    if ($(this).is(".carousel-item:last-child")) {
                        $(this).find(".microsite-column-category").addClass("mcc2");
                        $(this).parent().find(".carousel-item").first().find(".microsite-column-category").first().clone().appendTo($(this)).addClass("visible-xs mcc2");
                        $(this).parent().find(".carousel-item").first().find(".column-wrapper").first().clone().appendTo($(this)).addClass("visible-xs col2");

                    } else {
                        $(this).find(".microsite-column-category").addClass("mcc2");
                        $(this).next(".carousel-item").find(".microsite-column-category").clone().appendTo($(this)).addClass("visible-xs mcc2");
                        $(this).next(".carousel-item").find(".column-wrapper").first().clone().appendTo($(this)).addClass("visible-xs col2");
                    }

                }

                break;
            case (2):
                $(this).children(".column-wrapper").css("width", "50%");
                break;
            case (3):
                $(this).clone().addClass('visible-xs').insertAfter($(this)).find('.column-wrapper').first().remove();
                $(this).find('.column-wrapper:last-child').addClass("hidden-xs");
                break;
            case (4):
                $(this).clone().addClass('visible-xs').insertAfter($(this)).find('.column-wrapper:nth-child(-n+3)').remove();
                $(this).find('.column-wrapper:nth-child(n+4)').addClass("hidden-xs");
                break;
            case (5):
                $(this).clone().addClass('visible-xs').insertAfter($(this)).find('.column-wrapper:not(:nth-last-child(-n+2))').remove();
                ;
                $(this).clone().addClass('visible-xs').insertAfter($(this)).find('.column-wrapper:nth-child(-n+3), .column-wrapper:last-child').remove();
                $(this).find('.column-wrapper:nth-child(n+4)').addClass("hidden-xs");
                break;
            default:
        }


    });
};

function matchHeights() {
    if (!$(".visible-xs").is(":visible")) {
        $(".microsite-product-chart").each(function () {
            var elements = $(this).find(".microsite-column-category:nth-child(1)");

            var height = 0;

            elements.css('min-height', '0px');
            elements.each(function () {
                height = $(this).height() > height ? $(this).height() : height;

            });
            elements.css('min-height', height + 'px');

        });
    } else {
        $(".microsite-product-chart .carousel .carousel-inner .item").each(function () {
            if ($(this).css("display") == "none") {
                return true;
            } else {
                var elements = $(this).find(".microsite-column-category");

                var height = 0;

                elements.css('min-height', '0px');
                elements.each(function () {

                    height = $(this).height() > height ? $(this).height() : height;


                });
                elements.css('min-height', height + 'px');
                $(".left-col-mcc").css('min-height', height + 'px');
                $(".microsite-product-chart .carousel .carousel-inner .carousel-item ").each(function () {
                    $(this).find(".microsite-column-category:nth-child(3)").css("margin-top", 0 - height);


                });


            }

        });
    }
};

function micrositeComparisonChart() {

    matchHeights();

    if (!$(".visible-xs").is(":visible")) {
        $(".microsite-product-chart .carousel .carousel-inner .carousel-item").each(function () {
            var columnNum = $(this).children(".column-wrapper").not('.visible-xs').length;
            $(this).css("width", 20 * columnNum + "%");
            $(this).children(".column-wrapper").css("width", "calc( 100% / " + columnNum);

        });

    } else {
        $(".microsite-product-chart .carousel .carousel-inner .carousel-item").css("width", "100%");
        $(".microsite-product-chart .carousel .carousel-inner .carousel-item .column-wrapper").css("width", "50%");

    }


    $(".microsite-product-chart .carousel .carousel-inner .carousel-item .column-wrapper").not(".visible-xs").each(function (index) {

        if (!$(".visible-xs").is(":visible")) {
            if (index % 2 == 0) {
                $(this).css("background-color", "#f2f2f2");
            } else {
                $(this).css("background-color", "#ffffff");
            }


        }

    });
    $(".microsite-product-chart .carousel .carousel-inner .carousel-item .column-wrapper").each(function (index) {


        if ($(".visible-xs").is(":visible")) {
            if (index % 2 == 0) {
                $(this).css("background-color", "#f2f2f2");
            } else {
                $(this).css("background-color", "#ffffff");
            }


        }

    });

    var splitter = 0;

    // Init first block
    $('.microsite-product-chart .carousel-item').removeClass('selected');
    $('.microsite-product-chart .carousel-item:first-child').addClass('selected');

    // Determine how many blocks to show based on screen width

    var slides = $(".microsite-product-chart .carousel-inner [class*='col-']");
    switch (false) {
        case ($(".hidden-xs").is(":visible")):
            splitter = 1;

            break;
        default:
            splitter = 5;
    }


    $('.microsite-product-chart .item [class*=col-]').removeClass('active').unwrap();
    for (var i = 0; i < slides.length; i += splitter) {
        slides.slice(i, i + splitter).wrapAll("<div class='item'></div>");
    }


    $('.microsite-product-chart .item:first-child').addClass('active');

    // Style first tab after a carousel slide
    $('#micrositeComparisonChartCarousel').bind('slid.bs.carousel', function (e) {
        matchHeights();
        var nextIndex = $('.item.active > .carousel-item').attr('data-target');
        $('.microsite-product-chart .carousel-item').eq(nextIndex).addClass('selected');
    });


};
/****** Microsite End ***************************************************************/


/**** Campaign Begin ****************************************/
$(".quotes_call_component").on("click", function () {
    $(".call_now_comp_form").toggle();
});

// Campaign Page Header Scroll
function campaignHeaderScroll() {

    if ($(".campaign-header").length !== 0) {
        var stickyOffset = $('.campaign-header').height() + 20;

        $(window).scroll(function () {
            var scrollPos = $(window).scrollTop();
            var campaignHeaderHeight = $('.campaign-header').height();
            //  console.log("vertical scroll campaign header height is: " + campaignHeaderHeight);
            $('.campaign-header-popup').css("top", campaignHeaderHeight);

            if (scrollPos >= stickyOffset) {
                $(".campaign-header").addClass("campaign-header-on-scroll");
            } else {
                $(".campaign-header").removeClass("campaign-header-on-scroll");
            }
        });

        $(window).resize(function () {
            var campaignHeaderHeight = $('.campaign-header').height();
            $('.campaign-header-popup').css("top", campaignHeaderHeight);
            // console .log("resize scroll campaign header height is: " + campaignHeaderHeight);
        });

        $(window).on("resize scroll", function () {
            $(".pageWrap").height($(".campaign-header").height());
            $(".campaign-header").on('webkitTransitionEnd otransitionend oTransitionEnd msTransitionEnd transitionend', function (e) {
                $(".pageWrap").height($(".campaign-header").height());
            });
        });

        $(".campaign-header-ctc").click(function () {
            $(".campaign-header-popup-wrapper").toggle();
        });
    }
};

function matchCampaignHeights() {

    if ($(".campaign-cell").length != 0) {
        $(".campaign-cell").each(function () {
            var elements = $(this).find(".col");
            // console.log(elements.length);
            var height = 0;

            elements.css('min-height', '0px');
            elements.find(".contact-campaign").css('min-height', '0px');
            elements.each(function () {

                height = $(this).height() > height ? $(this).height() : height;

            });


            if ($(".hidden-xs").is(":visible")) {
                elements.css('min-height', height + 'px');
                elements.find(".contact-campaign").css('min-height', height + 'px');
            } else {
                elements.css('min-height', "auto");
                elements.find(".contact-campaign").css('min-height', "auto");
            }
        });
    }


};
/**** Campaign End ****************************************/


/**** Home Hero Carousel  Begin ***************************/
//Load Breakpoint Images on Resize
function loadBannerImagesOnResize() {
    $.lazyLoadXT.autoLoadTime = 5000;
    var resizeTimeout;
    $(window).resize(function () {
        resizeTimeout = setTimeout(function () {
            $(window).lazyLoadXT({
                checkDuplicates: false
            });
            clearTimeout(resizeTimeout);
        }, 500);
    });
}
/**** Home Hero Carousel  End *****************************/



function matchMegaMenuLabelHeights() {

    if ($(".fullMenuInner").length != 0) {
        $(".fullMenuInner").each(function () {
            var elements = $(this).find(".full_menu_Label");
            // console.log(elements.length);
            var height = 0;

            elements.css('min-height', '0px');
            elements.each(function () {

                height = $(this).height() > height ? $(this).height() : height;

            });
            elements.css('min-height', height + 20 + 'px');


        });
    }


};


function contactUsFooterDivider() {

    if ($(".contact-footer-wrapper").length != 0) {

        $(".contact-footer-category").each(function (index) {
            var height = $(this).height();
            var listElementNum = index;
            //console.log(listElementNum);
            if (listElementNum % 2 == 1) {
                if (height > $(this).prev().height()) {
                    $(this).addClass("border-left");
                    $(this).prev().removeClass("border-right");
                } else {
                    $(this).prev().addClass("border-right");
                    $(this).removeClass("border-left");
                }
            }

        });
    }


};

function productCardSetImage() {

    if ($(".hidden-xs").is(":visible")) {
        if ($(".title-with-image").length > 0) {
            $(".title-with-image").each(function () {
                $(this).removeAttr("data-backgroundMobile-src");
                $(this).css('background-image', 'url(' + $(this).attr("data-backgroundDesktop-src") + ')');
                $(this).css('background-size', 'cover');
            });
        }

    } else if ($(".hidden-xs").is(":visible") == false) {
        if ($(".title-with-image").length > 0) {
            $(".title-with-image").each(function () {
                $(this).removeAttr("data-backgroundDesktop-src");
                $(this).css('background-image', 'url(' + $(this).attr("data-backgroundMobile-src") + ')');
                $(this).css('background-size', 'cover');
            });
        }

    } else {
        if ($(".title-with-image").length > 0) {
            $(".title-with-image").each(function () {
                $(this).removeAttr("data-backgroundMobile-src");
                $(this).css('background-image', 'url(' + $(this).attr("data-backgroundDesktop-src") + ')');
                $(this).css('background-size', 'cover');
            });
        }
    }

}

$(document).ready(function() {
    if ($('.tooltip').length > 0 || $('.tooltip-pos-left').length > 0) {
        applyTooltips();
    }
});

function applyToolTipster() {
    $('.tooltip').not('.tooltipstered').tooltipster({
        position: 'right',
        trigger: 'click',
        minWidth: 50,
        maxWidth: 300
    });
    $('.tooltip-pos-left').not('.tooltipstered').tooltipster({
        position: 'right',
        trigger: 'click',
        minWidth: 50,
        maxWidth: 300
    });
}

$(window).resize(function () {
    if ($('.tooltip').length > 0 || $('.tooltip-pos-left').length > 0) {
        applyTooltips();
    }
    $('.tooltip').each(function() {
        $(this).tooltipster('hide');
    });
    $('.tooltipster-pos-left').each(function() {
        $(this).tooltipster('hide');
    })
});

function applyTooltips() {
    applyToolTipster();
    if ($(window).width() < 768) {
        $('.tooltip').tooltipster('option', 'position', 'bottom-right');
        $('.tooltip').tooltipster('option', 'offsetX', '7');
        $('.tooltip-pos-left').tooltipster('option', 'position', 'bottom-left');
        $('.tooltip-pos-left').tooltipster('option', 'offsetX', '-7');
    } else {
        $('.tooltip').tooltipster('option', 'position', 'right');
        $('.tooltip').tooltipster('option', 'offsetX', '0');
        $('.tooltip-pos-left').tooltipster('option', 'position', 'left');
        $('.tooltip-pos-left').tooltipster('option', 'offsetX', '0');
    }
    $('.tooltip').each(function () {
        //var referenceFrame = $(this).closest('.tooltip-reference-frame');
        //var tooltipAttach = $(this).closest('.tooltip-attach');
        //var leftSideOfWindow = tooltipAttach.offset().left < ($(window).width() / 2);
        //reference frame for use when we have two column forms
        //var leftSideOfReference = (tooltipAttach.offset().left - referenceFrame.offset().left) < (($(window).width() - referenceFrame.offset().left) / 2);
        //if on left side of window
        //move tooltip to left of box
        var roomForToolTip = (($(window).width()) - $(this).offset().left) > 330;
        if (!roomForToolTip) {
            $(this).tooltipster('option', 'position', 'bottom-right');
            $(this).tooltipster('option', 'offsetX', '7');
        }
        $(this).closest('.form-user-grp').css('margin-right', '30px');
    });
    $('.tooltip-pos-left').each(function () {
        /*var roomForToolTip = (($(window).width()) - $(this).offset().left) < 330;
         if (!roomForToolTip) {
         $(this).tooltipster('option', 'position', 'bottom-left');
         $(this).tooltipster('option', 'offsetX', '7');
         }*/
        $(this).closest('.form-user-grp').css('margin-left', '30px');
    })
}



/*******Sub Category Insurance Quote Page*******/

//Hide and show the form and product description corresponding to the dropdown selected
$("#insurance-type-quote-page").change(function () {
    console.log("entered");
    //If the user did not arrive at the site from a product page, then we have the disabled set to false, therefore\
    //we need to check for whether the first option of the dropdown is showing, and if it is, we disable it.
    if($("#insurance-type-quote-page option:first-child").attr("disabled", false)) {
        $("#insurance-type-quote-page option:first-child").attr("disabled", true);
    }
    var formToShow = $("#insurance-type-quote-page").val();
    showInsuranceProductDescription(formToShow);
    $(".quote-tool-form").show();
    $(".quote-tool-form form").hide();

    //All forms for this page will have a hidden class on them by default...therefore we have remove the hidden
    //class on the form that corresponds to the product selected on the dropdown menu
    if($("#" + formToShow).hasClass("hidden")) {
        $("#" + formToShow).removeClass("hidden");
    }
    $("#"+formToShow).show();
    $(".generic-form").trigger("reset");
});

function setDisabledField(){
    if($(".cta_header_quote").length != 0){
        var firstForm = $(".cta_header_quote").find(".quote-tool-form").find("div").first();
        var firstField =  firstForm.find("form").find(".form-focus").eq(0).clone();

        $(".age_wrapper").append(firstField);

        $('.age_wrapper .form-focus .form-user-grp  input, .age_wrapper .form-focus .form-user-grp select').each(function () {
            $(this).prop('disabled', 'disabled');
        });
    }
}

function showInsuranceProductDescription(selectedOption) {
    $(".get_a_quote_tool_text_wrap").hide();
    //All product descriptions have the hidden class called on them by default...therefore we have remove the hidden
    //class the first time the product is selected in the dropdown menu.
    if($("." + selectedOption).hasClass("hidden")) {
        $("." + selectedOption).removeClass("hidden");
    }
    $("." + selectedOption).show();
}

function setProductDropdownSelect(product) {
    //var sampleProduct = "Term Life Insurance";
    $("#insurance-type-quote-page option").each(function() {this.selected = (this.text == product);});

}

function setProductDetailsDisplay(product) {
    //var sampleProduct = "Term Life Insurance";

    var productToDisplay = $(".get_a_quote_tool_summary_header").filter(function() { return $(this).html() == product; }).text();
    if(productToDisplay == product) {

        //$.filter(function)...if callback returns truthy, then the element is included in the filtered set
        $(".get_a_quote_tool_summary_header")
            .filter(function() { return $(this).html() == product; })
            .parent().removeClass("hidden");
    }

}

function setProductForm(product) {
    //var sampleProduct = "Term Life Insurance";
    console.log(product);
    var valOfFormUnhidden = $("#insurance-type-quote-page option").filter(function() { return $(this).html() == product}).val();
    console.log(valOfFormUnhidden);
    if($(".generic-form").is("#" + valOfFormUnhidden)) {
        console.log("entered if statement");
        $("#" + valOfFormUnhidden).removeClass("hidden");
    }
}

//Event handler for the results page when a user clicks the "GET MY QUOTE" button on product card
$(".product-card .action .btn-green").click(function() {
    localStorage.setItem("nameOfProduct", $(".page_title > h1").text());
});

$(document).ready(function() {
    if($('.get_a_quote_tool_summary_header').length > 0) {
        if(localStorage.getItem("nameOfProduct") === null) {
            $("#insurance-type-quote-page option:first-child").attr("disabled", false);
        } else {
            var product = localStorage.getItem("nameOfProduct");
            setProductDropdownSelect(product);
            setProductDetailsDisplay(product);
            setProductForm(product);
            localStorage.removeItem("nameOfProduct");
        }

    }
});

/****End of the Sub Category Insurance Page****/

$(".policy_link").on("click", function(evt){

    console.log("working");
    console.log($(this).attr("href"));
    if($(this).attr("href").length !=0){
        var location =$(this).attr("href");
        $("html, body").animate({
            scrollTop: $(location).offset().top - 80
        }, 500);
        return false;
    }

});

$(".search_location_image, .location_search_image").on('click',function () {

    if(navigator.geolocation){
        navigator.geolocation.getCurrentPosition(handle_geolocation_home_query());

    }

});

function handle_geolocation_home_query(position){

    geocoder = new google.maps.Geocoder();
    var latlng = new google.maps.LatLng(position.coords.latitude, position.coords.longitude);

    var geocoder = geocoder = new google.maps.Geocoder();
    geocoder.geocode({ 'latLng': latlng }, function (results, status) {
        if (status == google.maps.GeocoderStatus.OK) {
            if (results[1]) {
                var arrAddress = results[1].address_components;
                var itemLocality ="";
                $.each(arrAddress, function (i, address_component) {
                    if (address_component.types[0] == "locality"){
                        itemLocality = address_component.long_name;
                    }
                    if (address_component.types[0] == "administrative_area_level_1"){
                        itemLocality += ', '+address_component.long_name;
                    }
                    $('.input_field_container .cta_search').val(itemLocality);
                    $('.find_an_x_input').val(itemLocality);

                });
            }
        }
    });

}

function adjustBtnWidth() {
    //For each visible tab, stack buttons vertically if at least one button is too wide (larger than container minus padding/margins)
    $('.carousel-with-tabs .carousel-tab:visible').each(function () {
        //For a visible tab, get the max width of its buttons then set it to all buttons
        var btnWidth = $(this).find(".btn").map(function () {
                return $(this).width();
            }).get(),
            maxBtnWidth = Math.max.apply(null, btnWidth);
        $(this).find('.btn').width(maxBtnWidth);
        //If maxBtnWidth is larger than the buttons container (minus padding/margin), stack buttons vertically.
        if($('.carousel-with-tabs .carousel-tab:visible .v-divider-arrow .row [class*="col-"] .btn').length > 0)
            var btnMargin = $('.carousel-with-tabs .carousel-tab:visible .v-divider-arrow .row [class*="col-"] .btn').css('padding-left').replace('px', '');
        if($('.carousel-with-tabs .carousel-tab:visible .v-divider-arrow .row [class*="col-"]')!== undefined)
            var maxBtnContainerWidth = $('.carousel-with-tabs .carousel-tab:visible .v-divider-arrow .row [class*="col-"]').width() - 4 * btnMargin; //multiply by 4 because we have 2 buttons and each has left and right padding
        if ( $(this).find(".btn").length <= 2 || maxBtnContainerWidth <= maxBtnWidth ) {
            $('.carousel-with-tabs .carousel-tab:visible .v-divider-arrow .row [class*="col-"]').addClass('full-width');
        }
    });
}

function resetBootstrapItems() {

    // Init first block
    $('.carousel-with-tabs .carousel-item').removeClass('selected');
    $('.carousel-with-tabs .carousel-item .arrow-down').hide();
    $('.carousel-with-tabs .carousel-tab').hide();
    $('.carousel-with-tabs .carousel-item:first-child').addClass('selected');
    $('.carousel-with-tabs .carousel-item:first-child .arrow-down').show();
    $('.carousel-with-tabs .carousel-tab').first().show();

    //How many blocks to show per carousel slide? PASS USER INPUT FROM CMS
    var splitter = 3;

    // Determine how many blocks to show based on screen width
    var sWidth = $(window).width();
    var slides = $(".carousel-with-tabs .carousel-inner [class*='col-']");
    switch (true) {
        case (sWidth > 1024):
            splitter = slides.length;
            break;
        case (sWidth > 767):
            break;
        default:
            splitter = 1;
    }
    $('.carousel-with-tabs .carousel-inner [class*="col-"]').css('width', (100 / splitter) + '%');
    $('.carousel-with-tabs .item [class*=col-]').removeClass('active').unwrap();
    for (var i = 0; i < slides.length; i += splitter) {
        slides.slice(i, i + splitter).wrapAll("<div class='item'></div>");
    }

    // Activate first slide
    $('.carousel-with-tabs .item:first-child').addClass('active');
    adjustBtnWidth();

    // Style tab based on clicked block
    $('.carousel-with-tabs .carousel-item').click(function () {
        $('.carousel-tabs > .carousel-tab').hide();
        $('.carousel-with-tabs .carousel-item').removeClass('selected');
        $('.carousel-with-tabs .carousel-item .arrow-down').hide();
        $(this).addClass('selected');
        $(this).find('.arrow-down').show();
        $('.carousel-tabs > .carousel-tab').eq($(this).attr('data-target')).show();
        adjustBtnWidth();
    });

    // Style first tab after a carousel slide
    $('#carouselWithTabs').bind('slid.bs.carousel', function (e) {
        $('.carousel-tabs > .carousel-tab').hide();
        $('.carousel-with-tabs .carousel-item').removeClass('selected');
        $('.carousel-with-tabs .carousel-item img.arrow-down').hide();
        var nextIndex = $('.item.active > .carousel-item').attr('data-target');
        $('.carousel-tabs > .carousel-tab').eq(nextIndex).show();
        $('.carousel-with-tabs .carousel-item').eq(nextIndex).addClass('selected');
        $('.carousel-with-tabs .carousel-item').eq(nextIndex).find('.arrow-down').show();
    });
}

$(window).load(function () {
    resetBootstrapItems();
});
$(window).resize(function () {
    resetBootstrapItems();
});











/*AEM Specific Funcitons*/
function breadCrumbAEM(){
    var sel = $('.bread-crumb script[type="text/javascript"]');
    if(sel.length != 0){
        var current = $(".bread-crumb").children();
        $(".bread-crumb span:nth-last-child(2)").addClass("aem");
    }
}

function productCardAEM(){
    $(".subcat-row").each(function () {


        var sel = $('script[type="text/javascript"]' , $(this));

        if(sel.length != 0){
            var numProdCards = $(this).find(".subcategory-product-card").length;
            var numImageCards = $(this).find(".subcategory-image-product-card").length;
            var  current =  numProdCards + numImageCards;

            // var cardToChange = $(':last-child()', $(this));
            // console.log(cardToChange);
            if(current == 3){
                if ($(window).width() >= 751) {
                    $(this).children(2).last().prev().css("margin-right", "0px");
                    // console.log("$(window).width() >= 767");
                }

                if ($(window).width() <= 750){
                    $(this).children(2).last().prev().css("margin-right", "10px")
                    //console.log($(window).width());
                }
            }
        }
    });
}

function largeProductCardAEM(){
    $(".large_product_module_wrapper").each(function () {


        var sel = $('script[type="text/javascript"]' , $(this));

        if(sel.length != 0){
            if ($(window).width() >= 751) {
                $(this).children(2).last().prev().css("margin-right", "0px");
            }
        }
    });
}

function quoteToolImageLink(){
    $('.form-focus').each(function(){
        $('[data-quoteToolLink]').parent().parent().parent().find(".form-control").addClass("smallerForm");
    });
};

$("#opinion_lab_link").click(function(){
    $("#oo_tab").trigger("click");
});

$(document).ready(function(){
    breadCrumbAEM();
    productCardAEM();
    largeProductCardAEM();
    quoteToolImageLink();
});

$(window).resize(function(){
    breadCrumbAEM();
    productCardAEM();
    largeProductCardAEM();
    quoteToolImageLink();
});

$(window).load(function(){
    breadCrumbAEM();
    productCardAEM();
    largeProductCardAEM();
    quoteToolImageLink();
});

$(document).ready(function(){
    if($(".cta_wrapper").length != 0 && $(".cta_wrapper").parent().parent().parent().find(".find_an_x_wrap:first").find(".cta_wrapper").siblings(".dental_vision_wrap").length == 0){
        $(".cta_wrapper").parent().parent().parent().find(".find_an_x_wrap:first").find(".cta_wrapper").css("width", "calc(100% - 100px)");
    }

});

$(window).resize(function(){
    if ($(window).width() < 751 && $(".cta_wrapper").length != 0 && $(".cta_wrapper").parent().parent().parent().find(".find_an_x_wrap:first").find(".cta_wrapper").siblings(".dental_vision_wrap").length == 0) {
        $(".cta_wrapper").parent().parent().parent().find(".find_an_x_wrap:first").find(".cta_wrapper").css("width", "calc(100% - 100px)");
    }
});


function subcategoryProudctTilesHeight() {

    if ($(".hidden-xs").is(":visible")) {
        if ($(".subcat-row").length != 0) {
            $(".subcat-row").each(function () {
                $(".subcategory-image-product-card").each(function () {
                    // console.log($(this));
                    if ($(this).find(".subcat-image-top").css("float") == "right" && $(this).hasClass("subcategory-promo-skinny-card")){
                        var valHeight = $(this).find(".subcat-image-text").outerHeight();
                        $(this).find(".subcat-image-top").height(valHeight);
                    }
                    if ($(this).find(".subcat-image-top").css("float") == "left" && $(this).hasClass("subcategory-promo-skinny-card-left")){
                        var valHeight = $(this).find(".subcat-image-text").outerHeight();
                        $(this).find(".subcat-image-top").height(valHeight);
                    }
                });
                if($('.subcategory-promo-large-card').hasClass('subcategory-promo-large-card-right')) {
                    $(".subcategory-promo-large-card-right").find(".subcat-image-top").css('height' , '300');
                    $(".subcategory-promo-large-card-right").find(".subcat-image-text").css('height' , '300');
                }
            });
        }
    } else {
        if ($(".subcat-row").length != 0) {
            $(".subcat-row").each(function () {
                $(".subcategory-image-product-card").each(function () {
                    if ($(this).find(".subcat-image-top").css("float") == "right") {
                        $(this).find(".subcat-image-top").css("height", "auto");
                    }
                });
            });
        }
    }


};

function subcategoryProudctTilesLayout(){

    if ($(".subcat-row").length != 0) {
        $(".subcat-row").each(function () {
            var numImageCards = $(this).find(".subcategory-image-product-card").length;
            if (numImageCards > 1) {
                // console.log($(".subcategory-image-product-card").length);
                $(this).find(".subcategory-image-product-card").addClass("single-promo")
                $(this).find(".subcategory-image-product-card").css("height", "272");
            }
            if ($(".subcategory-product-card").length != 0) {
                var numProductCards = $(this).find(".subcategory-product-card").length;
                if( numProductCards == 1){
                    if ($(".subcategory-product-card").length != 0){
                        $(this).find(".subcategory-image-product-card").addClass("double-promo");
                    }

                }else if(numProductCards == 2){
                    if ($(".subcategory-product-card").length != 0){
                        $(this).find(".subcategory-image-product-card").addClass("single-promo");
                    }
                }
            }
        });
    }
};

function getActionLink(el){
    return $(el).parent().data('actionLink').trim();
}

$(".login_open").click(function (e) {
    if(!$(".login_open").hasClass("linkOnly")){
        e.preventDefault();
        metlifeRedesign.login();
    }
});

$(".contact-us__select").on("change", function(){
    var whichresults = $(".contact-us__select").val();
    $(".contact-us__results__wrapper").addClass('hidden');
    if( $(".contact-us__results__wrapper").hasClass(whichresults)){
        $("." + whichresults).removeClass("hidden");
    }
});