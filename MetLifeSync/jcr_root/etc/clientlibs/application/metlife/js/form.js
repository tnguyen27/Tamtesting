// Log Code
var logTest = '';

if (typeof SFDC === "undefined") {
    var SFDC = {};
    SFDC.form = [];
}

SFDC.form.forEach(function (element) {
    var parent = $("." + element.type);
    var submitText = parent.find('.form-submit').text();
    var processingText = parent.find('.form-submit').attr("data-proctext");

    (function ($) {

        var isValid = true;
        var POST_URL = '';
        var ERROR_URL = '';
        var PARENT_URL = '';
        var subjects = {};
        //var requestType = $("#request_type").val();
        var post_url = $('#post_url').val();
        var actualmarketvalue = $('#actual_market').val();
        var switchMarketvalue = $('#switch_market').val();
        return {
            init: function () {
                //console.log("init");

                var o = this;
                $(document).ready(function () {
                    var domain = document.domain;
                    parent.find('#Domain').attr("value", window.location.protocol + "//" + domain);

                    // Bind initial form events...
                    parent.find('.generic-form').bind('submit', function (e) {
                        e.preventDefault();
                        o.submitForm();
                        return false;
                    })
                        .find('.disclaimer')
                        .show()
                        .each(function () {
                            // Essentially, we need to wrap all previous elements in a div.clearfix
                            // to make sure disclaimers display correctly.
                            $(this).prevAll()
                                .reverse()  // prevAll will return elements in order from disclaimer or reverse DOM order
                                .wrapAll('<div class="clearfix"></div>');
                        });

                    // Initialize form and Bind Subjects
                    o._initForm()._bindSubjects();

                    // Submit form
                    parent.find('.form-submit').bind('click', function (e) {
                        e.preventDefault();
                        if (!parent.find(".form-submit").hasClass("disabled")) {
                            parent.find('.form-submit').addClass("disabled").html(processingText);
                            parent.find(".generic-form").submit();
                        }
                    });
                    POST_URL = $('#post_url').val();
                });
            },
            _initForm: function () {
                //console.log("initForm");

                // Iterate through all of the form fields identified in our fields Object...
                var fields = element.fields;
                var i = 0, len = element.fields.length;

                while (i < len) {
                    var field = fields[i];
                    var $field = parent.find('#' + field.id);

                    // Log Code
                    logTest += '' + field.type + ' - ' + field.id + '\n';

                    // Loosely, we'll grab the appropriate function and pass ID and inputs to it
                    if (field.type !== 'undefined' && field.type.length > 0 && (field.type !== 'text' && field.type !== 'textArea') && field.type !== 'dob' && field.type !== 'phNo' && field.type !== 'TermsandconditionsField' && field.type !== 'hidden') {
                        this[field.type](field.id, field.options, field.fieldDefaultValue);
                    }

                    // Do we have a tooltip to add or change for this field?
                    //if (field.tooltip.length > 0) {
                    //    $field.attr('title', field.tooltip);
                    //}

                    // Hide hidden fields
                    if (field.hidden) {
                        this.hideElement($field);
                    }

                    // set height of forms
                    if ($(".contact-rep-with-image").length > 0) {
                        contactRepWithImageSize();
                    }

                    // Add required class
                    if (field.validator != "") {
                        if ($field.hasClass("input-group")) {
                            parent.find('#' + field.id + " :input").addClass("required");
                        }
                        else {
                            $field.addClass("required")
                        }
                    }

                    // Add observers
                    if (field.observes) {
                        var fieldId = field.id;
                        if (field.type == "dob") {
                            fieldId = field.id + 'd';
                        }
                        if (field.type == "phNo") {
                            fieldId = field.id + "ac";
                        }
                        this._bindEvents(fieldId, field.observes);
                    }
                    i++;
                }

                //console.log(logTest);

                return this;
            },

            /***
             * Binds observers' events to actions of subject.
             * @param {String} id Observer's ID
             * @param {Array} subject Array of subject's we're observing.
             */
            _bindEvents: function (id, subject) {
                //console.log("bind events");

                var $el = parent.find('#' + id);
                var i = 0, len = subject.length;
                while (i < len) {
                    var sub = subject[i];
                    this._addEvent($el, sub);
                    i++;
                }
            },

            /***
             * Adds appropriate classes and behavior to el so that it observes element with ID of subField.
             * Mainly used to avoid a closure created while iterating through subjects in _bindEvents( ).
             * @param {jQueryObject} $el el to become observer
             * @param {Object} subject Subject name and values el will observe
             */
            _addEvent: function ($el, sub) {
                //console.log("add event");

                var o = this;
                $el.addClass('observe-' + sub.field)
                    .setTrigger({name: sub.field, values: sub.values.join(',')})
                    .bind('observe.' + sub.field, function (e) {
                        $this = $(this);
                        var reqValue = e.val;
                        if (reqValue != null)
                            reqValue = e.val.replace(/\'/g, "&apos;");
                        o._setRequiredVals($this, {key: e.field, value: reqValue})
                            .validateElement($this);
                    });

                // Enqueue subject's ID; we'll need to make sure it's loaded in the DOM
                if (!(sub.field in subjects)) {
                    subjects[sub.field] = true;
                }
            },

            /***
             * Sets $el's trigger val for this particular key, indicating whether this particular display criteria has been met or not.
             * @param {jQueryObject} $el Element whose triggers we're updating.
             * @param {Object} val Key/value pair that we are setting.
             */
            _setRequiredVals: function ($el, val) {
                //console.log("set required vals");

                if ($el.data('trigger')[val.key].values.indexOf(val.value) > -1) {
                    $el.data('trigger')[val.key].valid = true;
                } else {
                    $el.data('trigger')[val.key].valid = false;
                }
                return this;
            },

            /***
             * Iterates through $el's "trigger" values, determining if all display criteria are met and $el should be shown/hidden.
             * @param {jQueryObject} $el Element we're evaluating.
             */
            validateElement: function ($el) {
                //console.log("validate element");

                var isValid = true;
                var trigger = $el.data('trigger');

                for (var req in trigger) {
                    isValid = isValid & trigger[req].valid;
                }

                if (isValid) {
                    this.showElement($el);
                } else {
                    this.hideElement($el);
                }
            },

            /***
             * Shows $el, also enabling it for validation on the back end.
             * @param {jQueryObject} $el Field we want to show/enable.
             */
            showElement: function ($el) {
                //console.log("show element");

                $fld = parent.find('#' + $el.attr('id'));
                $fld.closest('.form-focus, .form-hidden').show();
                //$fld.parents('.form-user-grp').show();

            },

            /***
             * Hides $el, also disabling it from validation on the back end.
             * @param {jQueryObject} $el Field we want to hide/disable.
             */
            hideElement: function ($el) {
                //console.log("hide element");

                $fld = parent.find('#' + $el.attr('id'));
                $fld.closest('.form-focus, .form-hidden').hide();
                //$fld.parents('.form-user-grp').hide();
            },

            /***
             * Bind triggers to change Events of our subjects array. Note that we must
             * defer this until after all subjects are written into the page, i.e. after processing form's init( ).
             */
            _bindSubjects: function () {
                //console.log("bind subjects");

                for (var s in subjects) {
                    var $subject = parent.find('#' + s);
                    if (!$subject.hasClass('observed')) {
                        this._addSubjectEvent($subject, s);
                    }
                }
                return this;
            },

            /***
             * Bind event to our subject's change event.
             * @param {jQueryObject} $subject Object we'll observe.
             * @param {String} id ID of subject; passed along in appropriate events.
             */
            _addSubjectEvent: function ($subject, id) {
                //console.log("add subject events");

                // Radio buttons and checkBoxes will actually be children of $subject
                if ($subject.attr("type") == "checkBox") {
                    $subject.change(function () {
                        var groupID = $subject.attr('id');
                        var numberChecked = 0;

                        parent.find('#' + groupID + ":checked").each(function () {
                            numberChecked += 1;
                            parent.find('.observe-' + id).trigger({
                                type: 'observe.' + id,
                                field: id,
                                val: $(this).val()
                            });
                        });

                        if (numberChecked == 0) {
                            parent.find('.observe-' + id).trigger({
                                type: 'observe.' + id,
                                field: id,
                                val: "none"
                            });
                        }
                    });
                }
                else if ($subject.hasClass('button_group')) {
                    $subject.change(function () {
                        var groupID = $subject.attr('id');

                        parent.find('#' + groupID + ":checked").each(function () {
                            parent.find('.observe-' + id).trigger({
                                type: 'observe.' + id,
                                field: id,
                                val: $(this).val()
                            });
                        });
                    });
                }
                else if ($subject.get(0).tagName.toLowerCase() == 'div') {
                    $subject = $subject.find('input');
                    $subject.bind('change', function () {
                        $el = $(this);
                        parent.find('.observe-' + id).trigger({
                            type: 'observe.' + id,
                            field: id,
                            val: $el.val()
                        });
                    });

                    // Pre-select radio buttons as per the UAE form.
                    var $first = $subject.filter(':first');
                    if ($first.is('input')) {
                        $first.click().trigger('change');
                    }
                }
                else if ($subject.get(0).tagName.toLowerCase() == 'select') {
                    $subject.change(function () {
                        parent.find('.observe-' + id).trigger({
                            type: 'observe.' + id,
                            field: id,
                            val: $subject.val()
                        });
                    });
                    var $subject = $subject.filter(':first');
                    $subject.trigger('change');
                }
            },

            /***
             *  Validate Date
             */
            validateDate: function (dateString) {
                //console.log("validate date");
                // Parse the date parts to integers
                var parts = dateString.split("-");
                var day, month, year;


                // First check for the pattern
                if (!/^\d{1,2}\/\d{1,2}\/\d{4}$/.test(dateString))
                    return false;
                var parts = dateString.split("/");
                var day = parseInt(parts[0], 10);
                var month = parseInt(parts[1], 10);
                var year = parseInt(parts[2], 10);



                // Check year
                if (year < 1900 || year > 2999)
                    return false;

                // Check month
                if (month == 0 || month > 12)
                    return false;

                // Check day
                var monthLength = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];

                // Adjust for leap years
                if (year % 400 == 0 || (year % 100 != 0 && year % 4 == 0))
                    monthLength[1] = 29;

                if (day <= 0 || day > monthLength[month - 1])
                    return false;

                // Check for current date
                var inputDate = new Date(month + "/" + day + "/" + year);
                var todayDate = new Date();

                if (inputDate.setHours(0, 0, 0, 0) > todayDate.setHours(0, 0, 0, 0)) {
                    return false;
                } else {
                    return true;
                }
            },

            /***
             * Hide Error
             */
            hideError: function (id) {
                //console.log("hide error: " + id);

                var element = parent.find('#' + id);
                element.siblings(".errorSpan").css("display", "none");

                // Go through groups and add only to inside
                // Ignore button groups, check box groups, and terms and conditions... which do not have inputs
                if (element.hasClass("input-group")) {
                    parent.find('#' + id + " :input").removeClass('error');
                }
                else {
                    if (!(element.hasClass("button_group")) && !(element.hasClass("checkboxGroup")) && !(element.hasClass("termsCondition"))) {
                        element.removeClass('error');
                    }
                }
                isValid = true;
            },

            /***
             * Show Error
             */
            showError: function (id, type) {
                //console.log("show error: " + id);

                var element = parent.find('#' + id);
                element.siblings(".errorSpan").css("display", "block");

                // Go through groups and add only to inside
                // Ignore button groups, check box groups, and terms and conditions... which do not have inputs
                if (element.hasClass("input-group")) {
                    parent.find('#' + id + " :input").addClass('error');
                }
                else {
                    if (!(element.hasClass("button_group")) && !(element.hasClass("checkboxGroup")) && !(element.hasClass("termsCondition"))) {
                        element.addClass('error');
                    }
                }
                isValid = false;
            },

            /***
             * Post the form to the server.
             *   - Success: Redirect to the thank-you page.
             *   - Error/failure : Display error messages, etc.
             */
            submitForm: function () {
                //console.log("submit form");

                // Post the form, handling any error messages that come back, etc.

                var switchformID = $('#switch_form_fieldID').val();

                if ($('#fld_' + switchformID).is(":visible")) {
                    var selectedValue = $('#' + switchformID).val();  // dropdown selected value
                    var switchValues = $('#switch_form_fieldValues').val();
                    var result = switchValues.match(selectedValue);
                    if (result) {

                        if ($('#oid').attr("name") == "oid") {
                            $('#oid').attr("name", "orgid")
                        }
                        else {
                            $('#oid').attr("name", "oid")
                        }
                        $('#post_url').attr("value", $('#switch_post_url').val());
                        $('#' + actualmarketvalue).attr("name", switchMarketvalue);
                        $('#' + actualmarketvalue).attr("id", switchMarketvalue);
                    }
                    else {
                        if ($('#oid').attr("name") == "oid") {
                            $('#oid').attr("name", "orgid")
                        }
                        else {
                            $('#oid').attr("name", "oid")
                        }
                        $('#post_url').attr("value", post_url);
                        $('#' + switchMarketvalue).attr("name", actualmarketvalue);
                        $('#' + switchMarketvalue).attr("id", actualmarketvalue);

                    }

                }

                //if($(this).find("input").is('input:checkbox'))
                //{
                //    var selected =[];
                //    $('input:checked').each(function() {
                //	    selected.push($('#' +$(this).attr('id')).val());
                //    });
                //    $(this).find("input[type=hidden]").val(selected);
                //}
                //if($(this).find("input").is('input:radio'))
                //{
                //    var selected =[];
                //    $('input:checked').each(function () {
                //	    selected.push($('#' +$(this).attr('id')).val());
                //    });
                //    $(this).find("input[type=hidden]").val(selected);
                //}


                var fields = element.fields;
                var i = 0, len = element.fields.length;

                while (i < len) {
                    var field = fields[i];
                    if (parent.find('#' + field.id).val() != null)
                        parent.find('#' + field.id).val(parent.find('#' + field.id).val().replace(/\'/g, "'"));
                    i++;
                }

                /*
                 * Response to be in the form of
                 * {"result":true|false, [{"fieldName":true},{"fieldName2":false}, ... {"fieldNameN" : true|false ]}
                 */

                i = 0;
                var bool = true;
                while (i < len) {
                    var f = fields[i];
                    if (parent.find('#' + f.id).is(":visible")) {
                        var Phregex = new RegExp(/^\d{3}\-\d+$/);
                        //var Phregex = new RegExp(/^\d{3}\-\d{3}\-\d{4}$/);
                        if (f.type == "phNo") {
                            var number = "";
                            var areaCode = parent.find('#' + f.id + 'ac').val();
                            var numericVal = parent.find('#' + f.id + 'num').val();

                            number = areaCode + '-' + numericVal;
                            parent.find('#' + f.id).val(number);
                            parent.find('#' + f.id + "Input").val(number);
                        }
                        if (f.type == "dob") {
                            var date = parseInt(parent.find('#' + f.id + 'd').val());
                            var month = parseInt(parent.find('#' + f.id + 'm').val());
                            var year = parseInt(parent.find('#' + f.id + 'y').val());
                            var dateofBirth = "";

                            date = ('0' + date).slice(-2);
                            month = ('0' + month).slice(-2);

                           dateofBirth = date + '/' + month + '/' + year;

                            parent.find('#' + f.id).val(dateofBirth);
                            parent.find('#' + f.id + "Input").val(dateofBirth);
                        }
                        var fieldValue = parent.find('#' + f.id).val();
                        switch (f.validator) {
                            case false:
                                this.hideError(f.id);
                                break;
                            case "required":
                                if (f.type == "text" || f.type == "textArea") {
                                    if (fieldValue != "") {
                                        this.hideError(f.id);
                                    } else {
                                        this.showError(f.id, f.type);
                                    }
                                }
                                else if (f.type == "select") {
                                    if (parent.find('#' + f.id).get(0).selectedIndex == 0 && parent.find('#' + f.id).val() == null) {
                                        this.showError(f.id, f.type);
                                    } else {
                                        this.hideError(f.id);
                                    }
                                }
                                else if (f.type == "radio") {
                                    if (parent.find('#' + f.id).find('input[type=radio]:checked').length > 0) {
                                        this.hideError(f.id);
                                    } else {
                                        this.showError(f.id, f.type);
                                    }
                                }
                                else if (f.type == "checkBox") {
                                    if (parent.find('#' + f.id).find('input[type=checkBox]:checked').length > 0) {
                                        this.hideError(f.id);
                                    } else {
                                        this.showError(f.id, f.type);
                                    }
                                }
                                else if (f.type == "dob") {
                                    if (!this.validateDate(fieldValue)) {
                                        parent.find('#' + f.id + 'd').find('input[type="text"]').attr("val", "");
                                        parent.find('#' + f.id + 'm').find('input[type="text"]').attr("val", "");
                                        parent.find('#' + f.id + 'y').find('input[type="text"]').attr("val", "");
                                        this.showError(f.id, f.type);
                                    } else {
                                        this.hideError(f.id);
                                    }
                                }
                                else if (f.type == "phNo") {
                                    if (Phregex.test(fieldValue)) {
                                        this.hideError(f.id);
                                    } else {
                                        parent.find('#' + f.id + 'ac').find('input[type="text"]').attr("val", "");
                                        parent.find('#' + f.id + 'num').find('input[type="text"]').attr("val", "");
                                        this.showError(f.id, f.type);
                                    }
                                }
                                break;
                            case "numeric":
                                if (fieldValue != "") {
                                    if (f.type == "phNo") {
                                        if (Phregex.test(fieldValue)) {
                                            this.hideError(f.id);
                                        } else {
                                            parent.find('#' + f.id + 'ac').find('input[type="text"]').attr("val", "");
                                            parent.find('#' + f.id + 'num').find('input[type="text"]').attr("val", "");
                                            this.showError(f.id, f.type);
                                        }
                                    }
                                    else if (f.type == "dob") {
                                        if (!this.validateDate(fieldValue)) {
                                            parent.find('#' + f.id + 'd').find('input[type="text"]').attr("val", "");
                                            parent.find('#' + f.id + 'm').find('input[type="text"]').attr("val", "");
                                            parent.find('#' + f.id + 'y').find('input[type="text"]').attr("val", "");

                                            this.showError(f.id, f.type);
                                        } else {
                                            this.hideError(f.id);
                                        }
                                    }
                                    else {
                                        if (fieldValue.match(/^\d+$/)) {
                                            this.hideError(f.id);
                                        } else {
                                            this.showError(f.id, f.type);
                                        }
                                    }
                                }
                                break;
                            case "numeric_Req":
                                if (f.type == "phNo") {
                                    if (Phregex.test(fieldValue)) {
                                        this.hideError(f.id);
                                    } else {
                                        parent.find('#' + f.id + 'ac').find('input[type="text"]').attr("val", " ");
                                        parent.find('#' + f.id + 'num').find('input[type="text"]').attr("val", " ");
                                        this.showError(f.id, f.type);
                                    }
                                }
                                else if (f.type == "dob") {
                                    if (!this.validateDate(fieldValue)) {
                                        parent.find('#' + f.id + 'd').find('input[type="text"]').attr("val", " ");
                                        parent.find('#' + f.id + 'm').find('input[type="text"]').attr("val", " ");
                                        parent.find('#' + f.id + 'y').find('input[type="text"]').attr("val", " ");
                                        this.showError(f.id, f.type);
                                    } else {
                                        this.hideError(f.id);
                                    }
                                }
                                else {
                                    if (fieldValue.match(/^\d+$/)) {
                                        this.hideError(f.id);
                                    } else {
                                        this.showError(f.id, f.type);
                                    }
                                }
                                break;
                            case "email":
                                if (fieldValue != "") {
                                    var regex = new RegExp(/^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$/);
                                    if (regex.test(fieldValue)) {
                                        this.hideError(f.id);
                                    } else {
                                        this.showError(f.id, f.type);
                                    }
                                }
                                break;
                            case "email_Req":
                                var regex = new RegExp(/^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$/);
                                if (regex.test(fieldValue)) {
                                    this.hideError(f.id);
                                } else {
                                    this.showError(f.id, f.type);
                                }
                                break;
                            case "regex":
                                if (fieldValue != "") {
                                    var regexPattern = new RegExp(parent.find('#' + f.id).attr('pattern'));
                                    if (regexPattern.test(fieldValue) || fieldValue == "") {
                                        this.hideError(f.id);
                                    } else {
                                        this.showError(f.id, f.type);
                                    }
                                }
                                break;
                            case "regex_Req":
                                var regexPattern = new RegExp(parent.find('#' + f.id).attr('pattern'));
                                if (regexPattern.test(fieldValue)) {
                                    this.hideError(f.id);
                                } else {
                                    this.showError(f.id, f.type);
                                }
                                break;
                            case "TermsandconditionsFieldRequired":
                                if (parent.find('#' + f.id).find('input[type=checkBox]:checked').length > 0) {
                                    this.hideError(f.id);
                                } else {
                                    this.showError(f.id, f.type);
                                }
                        }
                    }
                    i++;
                    if (!isValid) {
                        bool = false;
                    }
                }

                if (bool) {
                    var formElement = parent.find(".generic-form");
                    var jsonData = {};
                    var formData;

                    if (parent.parent().hasClass("quote-tool-form")) {
                        console.log("quote tool ajax");

                        formData = formElement.serializeArray();
                        $.each(formData, function () {
                            if (jsonData[this.name] !== undefined) {
                                if (!jsonData[this.name].push) {
                                    jsonData[this.name] = [jsonData[this.name]];
                                }
                                jsonData[this.name].push(this.value || '');
                            } else {
                                jsonData[this.name] = this.value || '';
                            }
                        });

                        $.ajax({
                            url: '/global-assets/proxy/QuoteProxy.aspx',
                            dataType: 'json',
                            data: {json: JSON.stringify(jsonData)},
                            contentType: "application/json; charset=utf-8",
                            async: true,
                            type: 'POST',
                            success: function (data, status, xhr) {
                                console.log("ajax: success");
                                console.log(data);
                                switch (data.status.toLowerCase()) {
                                    case "success":
                                        console.log("response: success");
                                        jsonData.premium = data.solution.premium;
                                        break;
                                    case "failure":
                                        console.log("response: failure");
                                        break;
                                    case "error":
                                        console.log("response: error");
                                        break;
                                    default:
                                        console.log("response: default");
                                }
                            },
                            error: function (xhr, status, error) {
                                console.log("ajax: error");
                            },
                            complete: function(xhr, status) {
                                if(!jsonData.hasOwnProperty("premium")) {
                                    jsonData.premium = "error";
                                }
                                sessionStorage.setItem("quoteTool", JSON.stringify(jsonData));
                                if ($(".results-card .quote-box").length > 0) {
                                    getQuoteResults();
                                    $(".edit-form-quote-results .form-close").trigger("click");
                                    parent.find('.form-submit').removeClass("disabled").html(submitText);
                                } else {
                                    var domain = document.location.origin;
                                    var resultsPage = element.resultsPageURL;
                                    window.location.href = domain + resultsPage;
                                }
                            }
                        });
                    } else {
                        console.log("contact form ajax");

                        if (sessionStorage.getItem("quoteTool") != null) {
                            var quoteData = JSON.parse(sessionStorage.getItem("quoteTool"));

                            var quoteString = "";
                            $.each(quoteData, function(name, key) {
                                quoteString += name + ":" + key + "#";
                            });
                            quoteString = quoteString.replace(/#$/, "");

                            var quoteInfo = formElement.find("input[name='QuoteInfo']");
                            if(quoteInfo.length > 0) {
                                quoteInfo.val(quoteString);
                            } else {
                                $("<input type='hidden' name='QuoteInfo'/>").val(quoteString).prependTo(formElement);
                            }
                        }

                        var url;
                        var data;

                        console.log("direct sfdc");
                        url = '/bin/forms/formService.json';
                        data = formElement.serialize();

                        console.log(data);

                        $.ajax({
                            url: url,
                            dataType: 'json',
                            data: data,
                            contentType: 'application/json',
                            async: true,
                            type: 'POST',
                            success: function (data, status, xhr) {
                                console.log("ajax: success");
                                console.log(data);
                                switch (data.response.toLowerCase()) {
                                    case "success":
                                        console.log("response: success");
                                        formMessage(parent, "thanks");
                                        break;
                                    case "fail":
                                        console.log("response: fail");
                                        formMessage(parent, "error");
                                        break;
                                    case "error":
                                        console.log("response: error");
                                        formMessage(parent, "error");
                                        break;
                                    default:
                                        console.log("response: default");
                                }
                            },
                            error: function (xhr, status, error) {
                                console.log("ajax: error");
                                console.log(status, error);
                                formMessage(parent, "error");
                            }
                        });
                    }
                } else {
                    parent.find('.form-submit').removeClass("disabled").html(submitText);
                }
            },

            /***
             * Generates a radio group at [id], using key/value pairs in opts to generate radio buttons.
             * @param {String} id Name of radio group we want to create. Also used to determine fieldset into which we'll insert radios.
             * @param {Array} opts Array of key/value Objects representing radio labels and values. Example format: [{"key":"value"}, {"key2":"value2"}, ... {"keyN":"valueN"}]
             */
            radio: function (id, opts, defaultValue) {

                var h = '';
                var mod, columns;
                var i = 0, len = opts.length;

                // determine button grouping
                if (len <= 2) {
                    mod = 2;
                    columns = "two-columns";
                } else {
                    mod = 3;
                    columns = "three-columns";
                    if (!parent.parent().hasClass("quote-tool-form")) {
                        parent.find('#' + id).closest(".form-hidden, .form-focus").css("width", "100%");
                    }
                }

                while (i < len) {
                    var opt = opts[i];
                    for (var key in opt) {
                        // Log Code
                        logTest += '--' + id + ' @ ' + key + ' with value: ' + opt[key] + '\n';

                        // button grouping start
                        if (i % mod == 0) {
                            h += "<div>";
                        }

                        // button
                        if (!parent.parent().hasClass("quote-tool-form")) {
                            h += '<div class="radio_button ' + columns + '" id="' + id + '">';
                        } else {
                            h += '<div class="radio_button ' + '" id="' + id + '">';
                        }

                        if (opt[key] == defaultValue) {

                            h += '<input type="radio" id="' + id + '" name ="' + id + '" value="' + opt[key] + '" checked >';
                        } else {
                            h += '<input type="radio" id="' + id + '" name ="' + id + '" value="' + opt[key] + '">';
                        }
                        h += '<label for="insert_id" id="">' + key + '</label>';
                        h += '</div>';


                        // button grouping end
                        if ((i + 1) % mod == 0) {
                            h += "</div>";
                        }
                    }
                    i++;
                }

                // fills in missing spacing
                if (i % mod > 0) {
                    if (mod == 3) {
                        for (var k = 0; k < mod - (i % mod); k++) {
                            if (!parent.parent().hasClass("quote-tool-form")) {
                                h += '<div class="radio_button ' + columns + '"></div>';
                            } else {
                                h += '<div class="radio_button ' + '"></div>';
                            }
                        }
                        h += "</div>";
                    } else {
                        h += "</div>";
                    }
                }

                parent.find('#' + id).append(h);
            },

            /***
             * Generates a set of checkBoxes group at [id], using key/value pairs in opts to generate checkBoxes.
             * @param {String} id ID used to determine fieldset into which we'll insert checkBoxes.
             * @param {Array} opts Array of key/value Objects representing checkBox labels/names and values. Example format: [{"key":"value"}, {"key2":"value2"}, ... {"keyN":"valueN"}]
             */
            checkBox: function (id, opts, defaultValue) {
                var h = '';
                var mod, columns;
                var i = 0, len = opts.length;

                // determine button grouping
                if (len <= 2) {
                    mod = 2;
                    columns = "two-columns";
                } else {
                    mod = 3;
                    columns = "three-columns";
                    parent.find('#' + id).closest(".form-hidden, .form-focus").css("width", "100%");
                }

                while (i < len) {
                    var opt = opts[i];
                    for (var key in opt) {
                        // Log Code
                        logTest += '--' + id + ' @ ' + key + ' with value: ' + opt[key] + '\n';

                        var check_id = id + "" + i;

                        // button grouping start
                        if (i % mod == 0) {
                            h += "<div>";
                        }

                        // button
                        h += '<div class="checkBox ' + columns + '">';
                        h += '<label>';
                        h += '<input class="user-checkbox" type="checkBox" id="' + check_id + '" name ="' + id + '" value="' + opt[key] + '">';
                        h += '<span>' + key + '</span>';
                        h += '</label>';
                        h += '</div>';

                        // button grouping end
                        if ((i + 1) % mod == 0) {
                            h += "</div>";
                        }
                    }
                    i++;
                }

                // fills in missing spacing
                if (i % mod > 0) {
                    if (mod == 3) {
                        for (var k = 0; k < mod - (i % mod); k++) {
                            h += '<div class="checkBox ' + columns + '"></div>';
                        }
                        h += "</div>";
                    } else {
                        h += "</div>";
                    }
                }

                parent.find('#' + id).prepend(h);

                modCheckboxObserves(id);
            },

            /***
             * Generates options used for populating the select box given at ID.
             * @param {String} id ID of select whose options we will populate with generated HTML.
             * @param {Array} opts Array of Objects representing option key/value pairs. Can optionally contain named optgroups if "name" is not empty string. Example: [{name:'Group 1', options : {"key":"value"},{"key2":"value2"}]},{name : 'Group 2', options : [{"key":"value"}, {"key2":"value2"}]}]);
             */
            select: function (id, opts, defaultValue) {
                var h = '';
                var i = 0, len = opts.length;
                while (i < len) {
                    var opt = opts[i];
                    var isLabel = opt.label !== '';
                    var isGroup = opt.name !== '';

                    if (isLabel) {
                        h += '<option selected="selected" disabled="disabled" value="">' + opt.label + '</option>';
                    } else if (isGroup) {
                        h += '<optgroup label="' + opt.name + '">';
                    }

                    var childOpts = opt.options;
                    var j = 0, cLength = childOpts.length;
                    while (j < cLength) {
                        var cOpt = childOpts[j];
                        for (var key in cOpt) {
                            // Log Code
                            logTest += '--' + id + ' @ ' + key + ' with value: ' + cOpt[key] + '\n';

                            h += '<option value="' + cOpt[key] + '">' + key + '</option>'
                        }
                        j++;
                    }
                    i++;
                }
                parent.find('select#' + id).append(h);
            }
        }
    }(jQuery)).init();

    /* Mod check box observes? ***************/
    function modCheckboxObserves(id) {
        var fields = element.fields;
        var i = 0, len = element.fields.length;

        // Check every field to see if there is an observe in them
        for (var v = 0; v < len; v++) {
            // If field has an observe
            if (fields[v].observes.length > 0) {
                // Look at each observe
                for (var w = 0; w < fields[v].observes.length; w++) {
                    // If the observe comes from a checkbox group
                    var idOfObject = fields[v].observes[w].field;
                    if (idOfObject == id) {
                        // Get array of values of the checkbox group
                        var checkboxGroupValues = [];

                        parent.find("#" + idOfObject + " > div > label > input").each(function () {
                            checkboxGroupValues.push(this.value);
                        });
                        fields[v].observes[w].field = fields[v].observes[w].field + "" + jQuery.inArray(fields[v].observes[w].values[0], checkboxGroupValues);
                    }
                }
            }
        }
    }

    // Expands Form
    parent.find(".form-user-ctrl, .form-control, .formTextarea").on("focus", function () {
        parent.removeClass('form-off')
    });

    // Closes Form
    parent.find(".contact-close").on('click', function (evt) {
        evt.preventDefault();
        formReset(parent, element.fields);
        parent.find('.form-submit').removeClass("disabled").html(submitText);
    });
});

/***** Validations **************************************************/
// Contact Form Validatons
if ($(".generic-form").length > 0) {
    // Validation for Select Fields
    $('select[data-required=true]').on({
        change: function (evt) {
            $(this).trigger('blur');
        },
        blur: function (evt) {
            var $this = $(this);
            var val = $this.val();

            if (val != null) {
                if (val.length == 0) {
                    if ($this.hasClass("required")) {
                        $this.addClass('error');
                        $this.siblings(".errorSpan").show();
                    }
                } else {
                    $this.removeClass('error');
                    $this.siblings(".errorSpan").hide();
                }
            }
        }
    });

    // Validation for Text Fields
    $('input[type=text][data-required=true]').on({
        focus: function (evt) {
            $(this).trigger('keyup');
        },
        keyup: function (evt) {
            $(this).removeClass('error');
            $(this).siblings().removeClass('error');
            $(this).siblings(".errorSpan").hide();
            $(this).parent().siblings(".errorSpan").hide();
        }
    });

    // Validation for Radio Fields
    $(".generic-form").on("change", "input[type=radio]", function (evt) {
        var parent = $(this).closest(".form-user-grp");
        parent.find(".radio_button").removeClass('error');
        parent.find(".errorSpan").hide();
    });

    // Validation for Checkbox Fields
    $(".generic-form").on("click", "input[type=checkBox]", function (evt) {
        var parent = $(this).closest(".form-user-grp");
        parent.find(".user-checkbox").removeClass('error');
        parent.find(".errorSpan").hide();
    });
}
/***** Validations **************************************************/


/***** Contact Us and Privacy Forms *********************************/
// Sets the resize for label height
if ($(".contact-privacy").length > 0) {
    contactAboutFromLayout();
}

// Initialization for contact form text areas
function contactAboutFromLayout() {
    // text areas
    $(".generic-form .formTextarea").closest(".form-hidden, .form-focus").css("width", "100%");

    // terms and conditions
    $(".generic-form .termsCondition").closest(".form-hidden, .form-focus").css("width", "100%");
}
/***** Contact Us and Privacy Forms *********************************/


/***** Contact Rep with Image ***************************************/
// Sets the resize for form with contact image
if ($(".contact-rep-with-image").length > 0) {
    $(window).on("resize", function () {
        contactRepWithImageSize();
    });
}

// Resize form image
function contactRepWithImageSize() {
    var parent = $(".contact-rep-with-image");
    var form = parent.find(".contact-lead-form");
    var image = parent.find(".image");

    if (image.is(":visible") && form.hasClass("form-off")) {
        image.height(form.outerHeight());
    }
}
/***** Contact Rep with Image ***************************************/


/***** Quote Form ***************************************************/
// CTA Header Quote Tool
if ($(".cta_header_quote").length > 0) {
    $(".cta_header_quote").find(".select_wrapper").on("change", function () {
        quoteFormReset();
    });
}

// Initializes the quote results display and edit your quote
if ($(".results-card .quote-box").length > 0) {
    // Get Quote Results
    getQuoteResults();

    // Open Edit Quote Form
    $(".quote-edit .form-open").on("click", function () {
        preFillQuoteForm();
        $(".contact-form-quote-results").addClass("contact-form-quote-results--hidden");
        $(".edit-form-quote-results").addClass("edit-form-quote-results--block");
        $(".results-form").addClass("results-form--dark-blue");
        $(".quote-box").addClass("quote-box--inactive");
    });

    // Close Edit Quote Form
    $(".edit-form-quote-results .form-close").on("click", function () {
        $(".results-form").removeClass("results-form--dark-blue");
        $(".contact-form-quote-results").removeClass("contact-form-quote-results--hidden");
        $(".edit-form-quote-results").removeClass("edit-form-quote-results--block");
        $(".quote-box").removeClass("quote-box--inactive");
    });
}

// Resets quote forms
function quoteFormReset() {
    $(".cta_header_quote").find(".generic-form").each(function () {
        $(this).find("input, select, textarea").removeClass('error');
        $(this)[0].reset();
    });
}

// Prefills the values on the quote form
function preFillQuoteForm() {
    if (sessionStorage.getItem("quoteTool") != null) {
        var jsonData = JSON.parse(sessionStorage.getItem("quoteTool"));
        var quoteForm = $(".results-card .quote-tool-form .generic-form");

        $.each(jsonData, function (jsonKey, jsonValue) {
            var input = quoteForm.find("[name='" + jsonKey + "']");
            if (input.attr("type") == "radio") {
                // radio
                quoteForm.find("[name='" + jsonKey + "'][value='" + jsonValue + "']").prop("checked", true);
            } else if (input.attr("type") == "checkBox") {
                // checkbox
                if ($.isArray(jsonValue)) {
                    $.each(jsonValue, function (arrayIndex, arrayValue) {
                        quoteForm.find("[name='" + jsonKey + "'][value='" + arrayValue + "']").prop("checked", true);
                    });
                } else {
                    quoteForm.find("[name='" + jsonKey + "'][value='" + jsonValue + "']").prop("checked", true);
                }
            } else {
                if (input.siblings().hasClass("BirthDate")) {
                    // birthday
                    input.siblings().find(".birthDateDay").val(jsonValue.slice(0, 2));
                    input.siblings().find(".birthDateMonth").val(jsonValue.slice(3, 5));
                    input.siblings().find(".birthDateYear").val(jsonValue.slice(6, 10));
                }
                if (input.siblings().hasClass("phoneNumber")) {
                    // phone number
                    input.siblings().find(".phoneArea").val(jsonValue.substr(0, jsonValue.indexOf('-')));
                    input.siblings().find(".phoneLocal").val(jsonValue.substr(jsonValue.indexOf('-') + 1));

                }
                // all other text, textarea, select
                input.val(jsonValue);
            }
        });
    }
}

// Get Initial Quote Results
function getQuoteResults() {
    if (sessionStorage.getItem("quoteTool") != null) {
        var quote_box = $(".results-card .quote-box");
        var decimal = quote_box.attr("data-cents");
        var thousand = quote_box.attr("data-thousands");
        var jsonData = JSON.parse(sessionStorage.getItem("quoteTool"));

        // Set premium amount
        var coverage_price = quote_box.find(".coverage-price");
        coverage_price.find(".value").html(formatQuoteNumber(decimal, thousand, 2, jsonData.premium));

        // Set other text
        var coverage_terms = quote_box.find(".coverage-terms");
        coverage_terms.find("div").each(function () {
            var field = $(this).attr("data-field");
            if (jsonData.hasOwnProperty(field)) {
                $(this).find(".value").html(formatQuoteNumber(decimal, thousand, 0, jsonData[field]));
            }
        });

        // Store currency and frequency amounts
        jsonData.currency = coverage_price.find(".currency").text();
        jsonData.frequency = coverage_price.find(".frequency").text();
        sessionStorage.setItem("quoteTool", JSON.stringify(jsonData));
    }
}

// Format Quote Results Numbers
function formatQuoteNumber(decimal, thousand, fixed, number) {
    if (!isNaN(number)) {
        number = parseFloat(number).toFixed(fixed);
        number = number.toString().replace(".", decimal).replace(/\B(?=(\d{3})+(?!\d))/g, thousand);
    }
    return number;
}
/***** Quote Form ***************************************************/


/***** Form Functions ***********************************************/
// Resets contact forms
function formReset(parent, fields) {
    parent.addClass('form-off');
    parent.children().removeAttr("style");
    parent.find("input, select, textarea").removeClass('error');
    parent.find(".errorSpan").hide();
    parent.find('.generic-form')[0].reset();

    if (parent.hasClass("contact-image")) {
        contactRepWithImageSize();
    }

    // Hide hidden fields
    for (var i = 0; i < fields.length; i++) {
        var field = fields[i];
        if (field.hidden) {
            parent.find('#' + field.id).closest('.form-focus, .form-hidden').hide();
        }
    }
}

// Displays thank you/error message for contact forms
function formMessage(parent, status) {
    var message;
    if (status == "thanks") {
        message = parent.find(".contact-thanks");
    } else {
        message = parent.find(".contact-error");
    }
    message.siblings(":visible").fadeOut('slow', function () {
        message.css("display", "table-cell");
        setTimeout(function () {
            if (parent.parent().hasClass("contactSliderOuterCon")) {
                $('.contactSliderOuterCon').fadeOut(800, function () {
                    parent.find(".contact-close").trigger("click");
                });
            }
        }, 5000)
    });
}
/***** Form Functions ***********************************************/


/* jQuery plugin for adding multi-dimensional show/hide triggers to elements' $().data stores ***************/
(function ($) {
    $.fn.setTrigger = function (trigger) {
        return this.each(function () {
            $this = $(this);

            if ($this.data('trigger') === null || typeof $this.data('trigger') === 'undefined') {
                $this.data('trigger', {});
            }

            $this.data('trigger')[trigger.name] = {
                valid: false,
                values: trigger.values
            }
        });
    }
}(jQuery));

/* Mask Input Handling *************/
function maskInput(input, textbox, location, delimiter) {

    //Get the delimiter positons
    var locs = location.split(',');

    //Iterate until all the delimiters are placed in the textbox
    for (var delimCount = 0; delimCount <= locs.length; delimCount++) {
        for (var inputCharCount = 0; inputCharCount <= input.length; inputCharCount++) {

            //Check for the actual position of the delimiter
            if (inputCharCount == locs[delimCount]) {

                //Confirm thaft the delimiter is not already present in that position
                if (input.substring(inputCharCount, inputCharCount + 1) != delimiter) {
                    if (event.keyCode != 8 && event.keyCode != 46) {
                        input = input.substring(0, inputCharCount) + delimiter + input.substring(inputCharCount, input.length);
                    }
                }
            }
        }
    }
    textbox.value = input;
}

/* A few convenience plugins for jQuery *************/
(function ($) {
    // Reverse a jQuery array of elements
    $.fn.reverse = [].reverse;
}(jQuery));