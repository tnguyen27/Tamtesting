function expandGeneralContent() {
    $(".generic_content").find(".wrapper-general").find(".unexpanded").slideToggle();
    $(".generic_content").find(".expand_button_open").addClass("hidden");
    $(".generic_content").find(".expand_button_close").removeClass("hidden");
    if ($(".rate_table").length > 0) {
        resizeRateTable();
    }
}
$(window).load(function () {
    expandGeneralContent();
});