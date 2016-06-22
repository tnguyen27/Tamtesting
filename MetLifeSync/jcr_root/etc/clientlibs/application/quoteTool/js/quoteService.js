//Quote tool Home Page Javascript
var quoteDomain;
var quotelanguage;
var quoteProduct;
var quoteSubmit;
var quoteUrl;
var quoteToolForm;
// CTA Header Quote Tool
if ($(".cta_header_quote").length > 0) {
	$(".cta_header_quote").find(".select_wrapper").on("change", function () {
		quoteFormReset();
	});
}
// Resets quote forms
function quoteFormReset() {
	$(".cta_header_quote").find(".generic-form").each(function () {
		$(this).find("input, select, textarea").removeClass('error');
		$(this)[0].reset();
	});
}
// Initializes the quote results display and edit your quote
if ($(".results-card .quote-box").length > 0) {
	// Get Quote Results
	// Open Edit Quote Form
	$(".quote-edit .form-open").on("click", function () {
		if(sessionStorage.getItem("product") !== null){
			$("#insurance-type").val($("[data-product='"+ sessionStorage.getItem("product") + "']").val());
		}
		$("#insurance-type").change();
		$(".contact-form-quote-results").addClass("contact-form-quote-results--hidden");
		$(".edit-form-quote-results").addClass("edit-form-quote-results--block");
		$(".results-form").addClass("results-form--dark-blue");
		$(".quote-box").addClass("quote-box--inactive");
		QuoteToolServiceAPI.preFillQuoteForm();
	});

	// Close Edit Quote Form
	$(".edit-form-quote-results .form-close").on("click", function () {
		$(".results-form").removeClass("results-form--dark-blue");
		$(".contact-form-quote-results").removeClass("contact-form-quote-results--hidden");
		$(".edit-form-quote-results").removeClass("edit-form-quote-results--block");
		$(".quote-box").removeClass("quote-box--inactive");
	});
}

$(".submit-quote").click(function(e){
	e.preventDefault();
	if($(".submit-quote").parent().parent().parent().parent().hasClass('quote-tool-form')){
		quoteUrl ="";
		//quoteUrl = 'channels.metlife.com:8080/MetQuoteService/GetAQuote?json={"domain":"' + quoteDomain + '","language":"'+ quotelanguage+'","product":"'+ quoteProduct +'","country":"default"';

		quoteUrl = 'http://us1700104:8080/MetQuoteService/GetAQuote?json={"domain":"' + quoteDomain + '","language":"'+ quotelanguage+'","product":"'+ quoteProduct +'","country":"default"';
		QuoteToolServiceAPI.loopThroughInputs();
		quoteUrl +=  '}';
		if(QuoteToolServiceAPI.validateFields()){
			QuoteToolServiceAPI.serviceCall();
		}
	}
});

$(".dobMonth").on("change", function () {
	QuoteToolServiceAPI.populateDaysDropDown("#");
});
$(".dobyear").on("change", function () {
	QuoteToolServiceAPI.populateDaysDropDown("#");
});

$("#insurance-type").on("change", function(){
	var formToShow = $("#insurance-type").val();
	$(".quote-tool-form").show();
	$(".quote-tool-form form").hide();
	$("[data-show-form='"+quoteToolForm+ "']").hide();
	quoteSubmit = $("#insurance-type").val();
	$("."+formToShow + " form").show();
	quoteSubmit = $("#insurance-type").val();
	if($("[data-quoteDescription='"+ quoteSubmit +"']").length > 0){
		$("[data-quoteDescription]").addClass("hidden");
		$("[data-quoteDescription='"+ quoteSubmit +"']").removeClass("hidden");
	}
	quoteToolForm = $(this).find(':selected').val();
	quoteDomain = $("[data-quoteTool='"+ quoteToolForm +"']").attr("data-domain");
	quotelanguage = $("[data-quoteTool='"+ quoteToolForm +"']").attr("data-lan");
	quoteProduct = $(this).find(':selected').attr('data-product');
	if(quoteProduct ==="us_gawli"){
		$(".cta_header_quote div + .btn-green").hide();
		$("#submitBtn").hide();
		if($('#' +quoteToolForm + 'state')[0].selectedIndex === 0){
			$("[data-quoteTool='"+quoteToolForm+"']").find(".form-focus, .form-button").addClass("hidden");
			$("[data-show-form='"+quoteToolForm+ "']").show().css("margin-top", "5px");
			$('#' +quoteToolForm + 'state').parent().parent().removeClass('hidden');
			$('#' +quoteToolForm + 'state').show();
			$('#' +quoteToolForm + 'state').css("width", "75%");
		}else{
			$('#' +quoteToolForm + 'state').css("width", "100%");
			$("[data-show-form='"+quoteToolForm+ "']").hide();
		}
		QuoteToolServiceAPI.quoteToolType = 'GAWLI';
		var d = new Date();
		d = d.getFullYear();
		var r = QuoteToolServiceAPI.gawliAgeCriteria[$('#' +quoteToolForm + 'state').val()];
		if(r != 'undefined')
		{
			if(typeof (r) == "object")
			{
				QuoteToolServiceAPI.populateYearDropDown(d-r[1],18,".dobYear");
			}
			else
			{
				QuoteToolServiceAPI.populateYearDropDown(d-80,18,".dobYear");
			}
		}
	}else if(quoteProduct ==="us_term"){
		$(".cta_header_quote div + .btn-green").hide();
		$("#submitBtn").hide();
		if($('#' +quoteToolForm + 'state')[0].selectedIndex === 0){
			$("[data-quoteTool='"+quoteToolForm+"']").find(".form-focus, .form-button").addClass("hidden");
			$("[data-show-form='"+quoteToolForm+ "']").show().css("margin-top", "5px");
			$('#' +quoteToolForm + 'state').parent().parent().removeClass('hidden');
			$('#' +quoteToolForm + 'state').show();
			$('#' +quoteToolForm + 'state').css("width", "75%");
		}else{
			$('#' +quoteToolForm + 'state').css("width", "100%");
			$("[data-show-form='"+quoteToolForm+ "']").hide();
		}
		var d = new Date(), r = QuoteToolServiceAPI.sitStates.indexOf(QuoteToolServiceAPI.selectedState);
		d = d.getFullYear();
		QuoteToolServiceAPI.quoteToolType = 'SIT';
		QuoteToolServiceAPI.populateYearDropDown(d-75,18,".dobYear");
	}else if($("#insurance-type")[0].selectedIndex !== 0){
		$(".cta_header_quote div + .btn-green").hide();
		$("#submitBtn").show();
	}else{
		$(".cta_header_quote div + .btn-green").show();
	}
});


$(".btn-green").on("click", function(){
	if(quoteToolForm !== undefined){
		if($(this).data("show-form") === quoteToolForm){
			if($('#' +quoteToolForm + 'state')[0].selectedIndex === 0){
				$('#' +quoteToolForm + 'state').addClass("error").parent().find('.errorSpan').show().css("display" , "block");
			}else{
				$("[data-quoteTool='"+quoteToolForm+"']").find(".form-focus, .form-button").removeClass("hidden");
				$(this).hide();
				$('#' +quoteToolForm + 'state').css("width", "100%");
				$('#' +quoteToolForm + 'state').removeClass("error").parent().find(".errorSpan").hide();
				if ($(".floatContentLeft").index()%2 !== 0) {
					$(".floatContentLeft").prev().css("width","101%");
				}

			}
		}
	}

});



$(".stateSelect").on("change", function(){
	if(quoteProduct ==="us_gawli"){
		QuoteToolServiceAPI.quoteToolType = 'GAWLI';
		var d = new Date();
		d = d.getFullYear();
		var r = QuoteToolServiceAPI.gawliAgeCriteria[$('#' +quoteToolForm + 'state').val()];
		if(r != 'undefined')
		{
			if(typeof (r) == "object")
			{
				QuoteToolServiceAPI.populateYearDropDown(d-r[1],18,".dobYear");
			}
			else
			{
				QuoteToolServiceAPI.populateYearDropDown(d-80,18,".dobYear");
			}
		}
	}else if(quoteProduct ==="us_term"){
		var d = new Date();
		d = d.getFullYear();
		QuoteToolServiceAPI.quoteToolType = 'SIT';
		QuoteToolServiceAPI.populateYearDropDown(d-75,18,".dobYear");
	}
});

$(document).ready(function(){
	QuoteToolServiceAPI.loadEventListeners();
});

String.prototype.toTitleCase = function() {
	var i, j, str, lowers, uppers;
	str = this.replace(/([^\W_]+[^\s-]*) */g, function(txt) {
		return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();
	});

	// Certain minor words should be left lowercase unless
	// they are the first or last words in the string
	lowers = ['A', 'An', 'The', 'And', 'But', 'Or', 'For', 'Nor', 'As', 'At',
		'By', 'For', 'From', 'In', 'Into', 'Near', 'Of', 'On', 'Onto', 'To', 'With'];
	for (i = 0, j = lowers.length; i < j; i++)
		str = str.replace(new RegExp('\\s' + lowers[i] + '\\s', 'g'),
			function(txt) {
				return txt.toLowerCase();
			});

	// Certain words such as initialisms or acronyms should be left uppercase
	uppers = ['Id', 'Tv'];
	for (i = 0, j = uppers.length; i < j; i++)
		str = str.replace(new RegExp('\\b' + uppers[i] + '\\b', 'g'),
			uppers[i].toUpperCase());

	return str;
}

var isWhole_re = /^\s*\d+\s*$/;
function isWhole (s) {
	return String(s).search (isWhole_re) != -1
}

var isNonblank_re    = /\S/;
function isNonblank (s) {
	return String (s).search (isNonblank_re) != -1
}

var QuoteToolServiceAPI = {
	quoteUrl: null,
	selectedInsurance: null,
	selectedState: null,
	quoteToolType: null,
	gawliStates: "AL,AK,AZ,AR,CA,CO,CT,DE,DC,FL,GA,HI,ID,IL,IN,IA,KS,KY,LA,ME,MD,MI,MN,MS,MO,MT,NE,NV,NH,NJ,NM,NY,NC,ND,OH,OK,OR,PA,RI,SC,SD,TN,TX,UT,VT,VA,WV,WI,WY",
	gawliOnlineAvailableStates: "AL,AK,AZ,AR,CA,CO,CT,DE,DC,FL,GA,HI,ID,IL,IN,IA,KS,KY,LA,ME,MD,MI,MN,MS,MO,NE,NV,NH,NJ,NM,NC,ND,OH,OK,OR,PA,RI,SC,SD,TN,TX,UT,VT,VA,WV,WI,WY",
	sitStates: "AL,AK,AZ,AR,CO,CT,DE,DC,FL,GA,HI,ID,IL,IN,IA,KS,KY,LA,ME,MD,MA,MI,MN,MS,MO,MT,NE,NV,NH,NJ,NM,NC,ND,OH,OK,OR,PA,RI,SC,SD,TN,TX,UT,VT,VA,WV,WI,WY",
	sitOnlineAvailableStates: "AL,AK,AZ,AR,CO,CT,DE,DC,FL,GA,HI,ID,IL,IN,IA,KS,KY,LA,ME,MD,MA,MI,MN,MS,MO,NE,NV,NH,NJ,NM,NC,ND,OH,OK,OR,PA,RI,SC,SD,TN,TX,UT,VT,VA,WV,WI,WY",
	mltApprovedStates: "AL,AK,AZ,AR,CA,CO,CT,DC,DE,FL,GA,HI,IA,ID,IL,IN,KS,KY,LA,MA,MD,MI,MN,MO,MS,MT,NC,ND,NE,NH,NJ,NM,NV,NY,OH,OK,OR,PA,RI,SC,SD,TN,TX,UT,VA,VT,WA,WI,WV,WY",
	sitCompatibleAge: null,
	quoteOption: 'online',
	quoteToolStarted: false,
	gawliAgeError: false,
	coverage: {
		"GAWLIAmounts": ["$2,500", "$5,000", "$7,500", "$10,000", "$15,000", "$20,000", "$30,000", "$40,000", "$50,000"],
		"GAWLIValues": ["2500", "5000", "7500", "10000", "15000", "20000", "30000", "40000", "50000"],
		/*	"GLTAmounts" : ["$750,000","$1,000,000","$1,500,000","$2,000,000","$2,500,000","$3,000,000","$3,500,000","$4,000,000","$4,500,000","$5,000,000"],
		 "GLTValues" : ["750000","1000000","1500000","2000000","2500000","3000000","3500000","4000000","4500000","5000000"],
		 "MLTAmounts" : ["$100,000","$150,000","$200,000","$250,000","$300,000","$400,000","$500,000"],
		 "MLTValues" : ["100000","150000","200000","250000","300000","400000","500000"],
		 "SITAmounts" : ["$10,000","$20,000","$30,000","$50,000"],
		 "SITValues" : ["10000","20000","30000","50000"],*/
		"TERMAmounts": ["$10,000", "$20,000", "$30,000", "$50,000", "$100,000", "$150,000", "$200,000", "$250,000", "$300,000", "$400,000", "$500,000", "$750,000", "$1,000,000", "$1,500,000", "$2,000,000", "$2,500,000", "$3,000,000", "$3,500,000", "$4,000,000", "$4,500,000", "$5,000,000"],
		"TERMValues": ["10000", "20000", "30000", "50000", "100000", "150000", "200000", "250000", "300000", "400000", "500000", "750000", "1000000", "1500000", "2000000", "2500000", "3000000", "3500000", "4000000", "4500000", "5000000"],
		"MLTGLTAmounts": ["$100,000", "$150,000", "$200,000", "$250,000", "$300,000", "$400,000", "$500,000", "$750,000", "$1,000,000", "$1,500,000", "$2,000,000", "$2,500,000", "$3,000,000", "$3,500,000", "$4,000,000", "$4,500,000", "$5,000,000"],
		"MLTGLTValues": ["100000", "150000", "200000", "250000", "300000", "400000", "500000", "750000", "1000000", "1500000", "2000000", "2500000", "3000000", "3500000", "4000000", "4500000", "5000000"]
	},
	gawliAgeCriteria: {
		"AR": [45, 70],
		"MN": [45, 65],
		"MO": [45, 75],
		"NE": [45, 75],
		"NJ": [45, 75],
		"PA": [56, 70]
	},
	termLength: {
		51: [10, 15, 20, 30],
		66: [10, 15, 20],
		71: [10, 15],
		76: [10]
	},
	termLength_NY: {
		66: [10, 15, 20],
		76: [10]
	},
	termLength_WA: {
		51: [10, 15, 20, 30],
		66: [10, 15, 20],
		71: [10, 15]
	},
	populateYearDropDown: function(year,min,element) {
		var yearOptions = $(element);
		var yr = new Date();

		yr = yr.getFullYear() - min;
		$(element).children().remove();
		$(element).append("<option value='' selected disabled>YYYY</option>");
		for (i = yr; i >= year; i--) {
			var optionValue = i;
			yearOptions.append($('<option>', {
				value: optionValue,
				text: optionValue.toString()
			}));
		}
	},
	isLeapYear : function(a) {
		a = parseInt(a);
		if (a % 4 == 0) {
			if (a % 100 != 0) {
				return true
			} else {
				if (a % 400 == 0) {
					return true
				} else {
					return false
				}
			}
		}
		return false;
	},
	populateDaysDropDown: function (id) {
		var numDayDropDown = $(".dobDay").length;
		var numMonthDropDown = $(".dobMonth").length;
		var numYearDropDown = $(".dobYear").length;
		if (($(".dobMonth").val() == "09") || ($(".dobMonth").val() == "04") ||
			($(".dobMonth").val() == "06") || ($(".dobMonth").val() == "11")) {
			$(".dobDay option:eq(31)").remove();

		} else if ($(".dobMonth").val() == "02") {

			if ((QuoteToolServiceAPI.isLeapYear($(".dobYear").val()) == false) || $(".dobYear").val() == "") {
				$(".dobDay option:eq(31)").remove();
				$(".dobDay option:eq(30)").remove();
				$(".dobDay option:eq(29)").remove();
			} else {
				if (($(".dobDay option[value='29']").length > (numDayDropDown - numDayDropDown)) == false) {
					$(".dobDay").append('<option value="29">29</option>');
				}
				$(".dobDay option:eq(31)").remove();
				$(".dobDay option:eq(30)").remove();
			}

		} else {
			if ((($(".dobDay option[value='29']").length - numDayDropDown) > 0) == false) {

				$(".dobDay").append('<option value="29">29</option>');
			}
			if ((($(".dobDay option[value='30']").length - numDayDropDown) > 0) == false) {

				$(".dobDay").append('<option value="30">30</option>');
			}
			if ((($(".dobDay option[value='31']").length - numDayDropDown) > 0) == false) {

				$(".dobDay").append('<option value="31">31</option>');
			}
		}
	},
	validateFields: function() {
		var areErrorFieldsPresent = false;

		$("[data-quoteTool='"+ quoteToolForm +"']").each(function(){
			if(!$("[data-quoteTool='"+ quoteToolForm +"']").find(".form-focus").find(".errorSpan").is(":visible")){
				areErrorFieldsPresent =  true;
			}
		});
		return areErrorFieldsPresent;
	},
	formatQuotePremium : function(premium){
		//if(premium != Math.round(premium)){
		var dec = parseFloat(Math.round(premium*100)/100).toFixed(2);
		return dec;
	},
	numberWithCommas: function(x){
		var parts = x.toString().split(".");
		parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
		return parts.join(".");
	},
	serviceCall: function() {
		//console.log(quoteUrl);
		$.ajax({
			url: quoteUrl,
			contentType: "application/json; charset=utf-8",
			async: true,
			dataType:'json',
			type: 'POST',
			success: function(response) {
				//	console.log('success ',response);
				var numObjects = Object.keys(response.solution).length;
				window.sessionStorage.clear();
				QuoteToolServiceAPI.setSessionStorage();

				if(response.solution.premium !== undefined &&  response.solution.premium !== null){
					var prem = QuoteToolServiceAPI.numberWithCommas(QuoteToolServiceAPI.formatQuotePremium(response.solution.premium));
					sessionStorage.setItem("premium", prem);
				}

				if(response.solution.age !== undefined && response.solution.age !== null){
					sessionStorage.setItem("age",response.solution.age);
				}
				if(response.solution.gender !== undefined && response.solution.gender !== null){
					sessionStorage.setItem("gender",response.solution.gender);
				}
				if(response.solution.coverage !== undefined && response.solution.coverage !== null){
					var cov = QuoteToolServiceAPI.numberWithCommas(QuoteToolServiceAPI.formatQuotePremium(response.solution.coverage));
					sessionStorage.setItem("coverage",cov);
				}
				if(response.solution.term !== undefined && response.solution.term !== null){
					sessionStorage.setItem("term",response.solution.term);
				}
				if(response.solution.coverageType !== undefined && response.solution.coverage_type !== null){
					sessionStorage.setItem("coverageType",response.solution.coverageType);
				}
				if(response.solution.state !== undefined && response.solution.state !== null){
					sessionStorage.setItem("state",response.solution.state);
				}
				if(response.solution.income !== undefined && response.solution.income !== null){
					sessionStorage.setItem("income",response.solution.income);
				}
				if($('#' +quoteToolForm + 'dobMonth').length > 0 && $('#' +quoteToolForm + 'dobDay').length > 0  && $('#' +quoteToolForm + 'dobYear').length > 0 ){
					sessionStorage.setItem('dobMonth', $('#' +quoteToolForm + 'dobMonth').val());
					sessionStorage.setItem('dobDay', $('#' +quoteToolForm + 'dobDay').val());
					sessionStorage.setItem('dobYear', $('#' +quoteToolForm + 'dobYear').val());
				}
				for(var i = 1; i <=numObjects; i++){
					var optionalSelect = response.solution.hasOwnProperty('optionalSelect' + i);
					if(optionalSelect){
						sessionStorage.setItem('optionalSelect' + i, response.solution['optionalSelect' + i]);
					}
					var optionalRadio = response.solution.hasOwnProperty('optionalRadio' + i);
					if(optionalRadio){
						sessionStorage.setItem('optionalRadio' + i, response.solution['optionalRadio' + i]);
					}
				}
				sessionStorage.setItem("product" , quoteProduct);
				QuoteToolServiceAPI.redirectToQuoteResultsPage(quoteUrl);
			},
			error: function(e) {
				console.log('error ',e);
			},
			timeout:30000
		});
	},
	loadResults: function(){
		if($(".results-card").length > 0){
			if(sessionStorage.getItem("premium") !== null){
				$(".coverage-price .value").text(sessionStorage.getItem("premium"));
			}
			//console.log(sessionStorage.getItem("coverage") !== null)
			if(sessionStorage.getItem("coverage") !== null){
				$("[data-field='coverage'] .value").text(sessionStorage.getItem("coverage"));
			}

			if(sessionStorage.getItem("coverageType") !== null){
				var cov = sessionStorage.getItem("coverageType").toTitleCase();
				$("[data-field='coverage']").html('<span class="value"> ' + cov + ' </span>');
			}
			if(sessionStorage.getItem("coverageType") === null && sessionStorage.getItem("coverage") === null){
				$("[data-field='coverage']").remove();
			}

			if(sessionStorage.getItem("term") !== null){
				$("[data-field='term'] .value").text(sessionStorage.getItem("term"));
			}else{
				$("[data-field='term']").html('');
			}
		}else{
			$("#insurance-type").val($("#insurance-type option:first").val());
			sessionStorage.clear();
		}
	},
	toTitleCase: function(str){
		return str.replace(/\w\S*/g, function(txt){return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();});
	},
	calculateAge: function() {
		var l = 0;
		if (($('#' + quoteToolForm + 'dobMonth').val() != "") && ($('#' + quoteToolForm + 'dobDay').val() != "") && ($('#' + quoteToolForm + 'dobYear').val() != "")) {
			var b = parseInt($('#' + quoteToolForm + 'dobMonth').val());
			var k = parseInt($('#' + quoteToolForm + 'dobDay').val());
			var m = parseInt($('#' + quoteToolForm + 'dobYear').val());
			var g = new Date();
			var e = g.getFullYear();
			var h = g.getMonth() + 1;
			var f = g.getDate();
			var c = 0;
			var a = 0;
			if (e > m) {
				l = e - m;
				c = e - m;
			}
			if (h < b) {
				l = l - 1;
				c = c - 1;
				a = 12 - (b - h);
				if (k > f) {
					a = a - 1;
				}
			} else {
				if (h == b) {
					if (f < k) {
						l = l - 1;
						c = c - 1;
						a = 12 - (b - h);
					}
				} else {
					if (h > b) {
						if (f >= k) {
							a = h - b;
						} else {
							a = (h - b) - 1;
						}
					}
				}

			}
			return l;
		}
	},
	redirectToQuoteResultsPage: function(url) {
		var url = $("[data-quoteTool='"+ quoteToolForm +"']").attr("data-path-to-results");
		window.location.href = url;
	},
	loadEventListeners: function(){
		QuoteToolServiceAPI.loadResults();
		//console.log("session storage");
		//for (var i = 0; i < sessionStorage.length; i++)   {
		//	console.log(sessionStorage.key(i) + "=[" + sessionStorage.getItem(sessionStorage.key(i)) + "]");
		//}
	},
	setSessionStorage: function(){
		var thisForm = $("[data-quoteTool='"+ quoteToolForm +"']");
		var numInputs = thisForm.find(".form-focus").length;

		if($('[name="'+quoteToolForm+'gender"]').length > 0){
			sessionStorage.setItem("gender", $('[name="'+quoteToolForm+'gender"]:checked').val());
		}

		if($('#' +quoteToolForm + 'userAge').length > 0){
			sessionStorage.setItem("age", $('#' +quoteToolForm + 'userAge').val());
		}

		if($('#' +quoteToolForm + 'coverageType').length > 0){
			sessionStorage.setItem("coverageType", $('#' +quoteToolForm + 'coverageType').val());
		}

		if($('#' +quoteToolForm + 'coverageText').length > 0 ){
			sessionStorage.setItem("coverage", $('#' +quoteToolForm + 'coverageText').val());
		}


		if($('#' +quoteToolForm + 'state').length > 0){
			sessionStorage.setItem("state", $('#' +quoteToolForm + 'state').val());
		}

		if($('#' +quoteToolForm + 'gender').length > 0){
			sessionStorage.setItem("gender", $('#' +quoteToolForm + 'gender').val());
		}

		if($('#' +quoteToolForm + 'coverageAmount').length > 0){
			sessionStorage.setItem("coverage", $('#' +quoteToolForm + 'coverageAmount').val());
		}

		if($('#' +quoteToolForm + 'termLengthSelect').length > 0){
			sessionStorage.setItem("term", $('#' +quoteToolForm + 'termLengthSelect').val());
		}

		if($('#' +quoteToolForm + 'termLengthText').length > 0 ){
			sessionStorage.setItem("term", $('#' +quoteToolForm + 'termLengthText').val());
		}

		if($('#' +quoteToolForm + 'incomeSelect').length > 0){
			sessionStorage.setItem("income", $('#' +quoteToolForm + 'incomeSelect').val());
		}

		if($('#' +quoteToolForm + 'incomeText').length > 0 ){
			sessionStorage.setItem("income", $('#' +quoteToolForm + 'incomeText').val());
		}

		if($('#' +quoteToolForm + 'dobMonth').length > 0 && $('#' +quoteToolForm + 'dobDay').length > 0  && $('#' +quoteToolForm + 'dobYear').length > 0 ){
			sessionStorage.setItem("dobMonth", $('#' +quoteToolForm + 'dobMonth').val());
			sessionStorage.setItem("dobDay", $('#' +quoteToolForm + 'dobDay').val());
			sessionStorage.setItem("dobYear", $('#' +quoteToolForm + 'dobYear').val());
		}

		for(var i = 1; i <= numInputs; i++){
			if($('#' +quoteToolForm + 'optionalSelect' + i).length > 0){
				sessionStorage.setItem("optionalSelect" + i, $('#' +quoteToolForm + 'optionalSelect' + i).val());
			}

			if($('[name="'+quoteToolForm+'radioGroup'+i+'"]').length > 0){
				sessionStorage.setItem("optionalRadio" + i, $('[name="'+quoteToolForm+'radioGroup'+ i +'"]').val());
			}
		}
	},
	preFillQuoteForm: function(){
		var thisForm = $("[data-quoteTool='"+ quoteToolForm +"']");
		var numInputs = thisForm.find(".form-focus").length;

		if($('[name="'+quoteToolForm+'gender"]').length > 0){
			$("#"+quoteToolForm+sessionStorage.getItem('gender')).attr("checked", true);
		}

		if($('#' +quoteToolForm + 'userAge').length > 0){
			$('#' +quoteToolForm + 'userAge').val(sessionStorage.getItem('age'));
		}

		if($('#' +quoteToolForm + 'coverageType').length > 0){
			$('#' +quoteToolForm + 'coverageType').val(sessionStorage.getItem('coverageType'));
		}

		if($('#' +quoteToolForm + 'coverageText').length > 0 ){
			var cov = parseInt(sessionStorage.getItem('coverage').replace(/\,/g,''));
			$('#' +quoteToolForm + 'coverageText').val(cov);
		}


		if($('#' +quoteToolForm + 'state').length > 0){
			$('#' +quoteToolForm + 'state').val(sessionStorage.getItem('state'));
			var state = $('#' +quoteToolForm + 'state').val();
		}

		if($('#' +quoteToolForm + 'gender').length > 0){
			$('#' +quoteToolForm + 'gender').val(sessionStorage.getItem('gender'));
		}

		if($('#' +quoteToolForm + 'coverageAmount').length > 0){
			var cov = parseInt(sessionStorage.getItem('coverage').replace(/\,/g,''));
			$('#' +quoteToolForm + 'coverageAmount').val(cov);
		}

		if($('#' +quoteToolForm + 'termLengthSelect').length > 0){
			$('#' +quoteToolForm + 'termLengthSelect').val(sessionStorage.getItem('term'));
		}

		if($('#' +quoteToolForm + 'termLengthText').length > 0 ){
			$('#' +quoteToolForm + 'termLengthText').val(sessionStorage.getItem('term'));
		}

		if($('#' +quoteToolForm + 'incomeSelect').length > 0){
			$('#' +quoteToolForm + 'incomeSelect').val(sessionStorage.getItem('income'))
		}

		if($('#' +quoteToolForm + 'incomeText').length > 0 ){
			$('#' +quoteToolForm + 'incomeText').val(sessionStorage.getItem('income'))

		}

		if($('#' +quoteToolForm + 'dobMonth').length > 0 && $('#' +quoteToolForm + 'dobDay').length > 0  && $('#' +quoteToolForm + 'dobYear').length > 0 ){
			$('#' +quoteToolForm + 'dobMonth').val(sessionStorage.getItem('dobMonth'));
			$('#' +quoteToolForm + 'dobDay').val(sessionStorage.getItem('dobDay'));
			$('#' +quoteToolForm + 'dobYear').val(sessionStorage.getItem('dobYear'));
		}

		for(var i = 1; i <= numInputs; i++){
			if($('#' +quoteToolForm + 'optionalSelect' + i).length > 0){
				$('#' +quoteToolForm + 'optionalSelect' + i).val(sessionStorage.getItem('optionalSelect' + i));
			}

			if($('[name="'+quoteToolForm+'radioGroup'+i+'"]').length > 0){
				$('[name="'+quoteToolForm+'radioGroup'+ i +'"]').val(sessionStorage.getItem('optionalRadio' + i)).attr("checked", true);
			}
		}
	},
	loopThroughInputs: function(){
		var thisForm = $("[data-quoteTool='"+ quoteToolForm +"']");
		var numInputs = thisForm.find(".form-focus").length;
		if($('[name="'+quoteToolForm+'gender"]').length > 0){
			var userGender = $('[name="'+quoteToolForm+'gender"]:checked').val();
			if(userGender === "" || userGender === " " || userGender === null || userGender === undefined){
				$('[name="'+quoteToolForm+'gender"]').parent().parent().find(".errorSpan").show().css("display" , "block");
			}else{
				$('[name="'+quoteToolForm+'gender"]').parent().parent().find(".errorSpan").hide();
				quoteUrl += ',"gender":"' + userGender +'"';
			}
		}

		if($('#' +quoteToolForm + 'userAge').length > 0){
			var age = $('#' +quoteToolForm + 'userAge').val();
			if($('#' +quoteToolForm + 'userAge')[0].selectedIndex === 0){
				$('#' +quoteToolForm + 'userAge').addClass("error").next().show().css("display" , "block");
			}else{
				quoteUrl += ',"age":"' + age +'"';
				$('#' +quoteToolForm + 'userAge').removeClass("error").next().hide();
			}
		}

		if($('#' +quoteToolForm + 'coverageType').length > 0){
			var coverageType = $('#' +quoteToolForm + 'coverageType').val();
			if($('#' +quoteToolForm + 'coverageType')[0].selectedIndex === 0){
				$('#' +quoteToolForm + 'coverageType').addClass("error").next().show().css("display" , "block");
			}else{
				quoteUrl += ',"coverageType":"' + coverageType +'"';
				$('#' +quoteToolForm + 'coverageType').removeClass("error").next().hide();
			}
		}

		if($('#' +quoteToolForm + 'coverageText').length > 0 ){
			var coverageText = $('#' +quoteToolForm + 'coverageText').val();
			if(isWhole(coverageText)=== true){
				quoteUrl += ',"coverage":"' + coverageText +'"';
				$('#' +quoteToolForm + 'coverageText').removeClass("error").next().hide();
			}else{
				$('#' +quoteToolForm + 'coverageText').addClass("error").next().show().css("display" , "block");
			}
		}

		if($('#' +quoteToolForm + 'state').length > 0){
			var state = $('#' +quoteToolForm + 'state').val();
			if($('#' +quoteToolForm + 'state')[0].selectedIndex === 0){
				$('#' +quoteToolForm + 'state').addClass("error").parent().find(".errorSpan").show().css("display" , "block");
			}else{
				quoteUrl += ',"state":"' + state +'"';
				$('#' +quoteToolForm + 'state').removeClass("error").parent().find(".errorSpan").hide();
			}
		}

		if($('#' +quoteToolForm + 'gender').length > 0){
			var gender = $('#' +quoteToolForm + 'gender').val();
			if($('#' +quoteToolForm + 'gender')[0].selectedIndex === 0){
				$('#' +quoteToolForm + 'gender').addClass("error").next().show().css("display" , "block");
			}else{
				quoteUrl += ',"gender":"' + gender +'"';
				$('#' +quoteToolForm + 'gender').removeClass("error").next().hide();
			}
		}

		if($('#' +quoteToolForm + 'coverageAmount').length > 0){
			var coverageAmount = $('#' +quoteToolForm + 'coverageAmount').val();
			if($('#' +quoteToolForm + 'coverageAmount')[0].selectedIndex === 0){
				$('#' +quoteToolForm + 'coverageAmount').addClass("error").next().show().css("display" , "block");
			}else{
				quoteUrl += ',"coverage":"' + coverageAmount +'"';
				$('#' +quoteToolForm + 'coverageAmount').removeClass("error").next().hide();
			}
		}

		if($('#' +quoteToolForm + 'termLengthSelect').length > 0){
			var termLengthSelect = $('#' +quoteToolForm + 'termLengthSelect').val();
			if($('#' +quoteToolForm + 'termLengthSelect')[0].selectedIndex === 0){
				$('#' +quoteToolForm + 'termLengthSelect').addClass("error").next().show().css("display" , "block");
			}else{
				quoteUrl += ',"term":"' + termLengthSelect +'"';
				$('#' +quoteToolForm + 'termLengthSelect').removeClass("error").next().hide();
			}
		}

		if($('#' +quoteToolForm + 'termLengthText').length > 0 ){
			var termLengthText = $('#' +quoteToolForm + 'termLengthText').val();
			if(isNonblank(termLengthText)=== true){
				$('#' +quoteToolForm + 'termLengthText').removeClass("error").next().hide();
				quoteUrl += ',"term":"' + termLengthText +'"';
			}else{
				$('#' +quoteToolForm + 'termLengthText').addClass("error").next().show().css("display" , "block");

			}
		}

		if($('#' +quoteToolForm + 'incomeSelect').length > 0){
			var income = $('#' +quoteToolForm + 'incomeSelect').val();
			if($('#' +quoteToolForm + 'incomeSelect')[0].selectedIndex === 0){
				$('#' +quoteToolForm + 'incomeSelect').addClass("error").next().show().css("display" , "block");
			}else{
				quoteUrl += ',"income":"' + income +'"';
				$('#' +quoteToolForm + 'incomeSelect').removeClass("error").next().hide();
			}
		}

		if($('#' +quoteToolForm + 'incomeText').length > 0 ){
			var incomeText = $('#' +quoteToolForm + 'incomeText').val();
			if(isNonblank(incomeText)=== true){
				$('#' +quoteToolForm + 'incomeText').removeClass("error").next().hide();
				quoteUrl += ',"income":"' + incomeText +'"';
			}else{
				$('#' +quoteToolForm + 'incomeText').addClass("error").next().show().css("display" , "block");

			}
		}

		if($('#' +quoteToolForm + 'dobMonth').length > 0 && $('#' +quoteToolForm + 'dobDay').length > 0  && $('#' +quoteToolForm + 'dobYear').length > 0 ){
			var age;
			if($('#' +quoteToolForm + 'dobMonth')[0].selectedIndex === 0){
				$('#' +quoteToolForm + 'dobMonth').addClass("error");
			}else{
				$('#' +quoteToolForm + 'dobMonth').removeClass("error");
			}

			if($('#' +quoteToolForm + 'dobDay')[0].selectedIndex === 0){
				$('#' +quoteToolForm + 'dobDay').addClass("error");
			}
			else{
				$('#' +quoteToolForm + 'dobDay').removeClass("error");
			}

			if($('#' +quoteToolForm + 'dobYear')[0].selectedIndex === 0){
				$('#' +quoteToolForm + 'dobYear').addClass("error");
			}
			else{
				$('#' +quoteToolForm + 'dobYear').removeClass("error");
			}

			if($('#' +quoteToolForm + 'dobMonth')[0].selectedIndex !== 0 && $('#' +quoteToolForm + 'dobDay')[0].selectedIndex !== 0 && $('#' +quoteToolForm + 'dobYear')[0].selectedIndex !== 0){
				age = QuoteToolServiceAPI.calculateAge();
				quoteUrl += ',"age":"' + age +'"';
			}
		}
		for(var i = 1; i <= numInputs; i++){
			if($('#' +quoteToolForm + 'optionalSelect' + i).length > 0){
				var optionalSelect = $('#' +quoteToolForm + 'optionalSelect' + i).val();
				if($('#' +quoteToolForm + 'optionalSelect' + i)[0].selectedIndex === 0){
					$('#' +quoteToolForm + 'optionalSelect' + i).addClass("error").next().show().css("display" , "block");
				}else{
					quoteUrl += ',"optionalSelect'+i+'":"' + optionalSelect +'"';
					$('#' +quoteToolForm + 'optionalSelect' + i).removeClass("error").next().hide();
				}
			}

			if($('[name="'+quoteToolForm+'radioGroup'+i+'"]').length > 0){
				var optionalRadio = $('[name="'+quoteToolForm+'radioGroup'+i+'"]:checked').val();
				if(optionalRadio === "" || optionalRadio === " " || optionalRadio === null || optionalRadio === undefined){
					$('[name="'+quoteToolForm+'radioGroup'+i+'"]').parent().parent().find(".errorSpan").show().css("display" , "block");
				}else{
					$('[name="'+quoteToolForm+'radioGroup'+i+'"]').parent().parent().find(".errorSpan").hide();
					quoteUrl += ',"optionalRadio'+i+'":"' + optionalRadio +'"';
				}
			}
		}
	},
	getQueryString: function(){
		var vars = [], hash;
		var hashes = window.location.href.slice(window.location.href.indexOf('#') + 1).split('&');
		for(var i = 0; i < hashes.length; i++)
		{
			hash = hashes[i].split('=');
			vars.push(hash[0]);
			vars[hash[0]] = hash[1];
		}
		return vars;
	},
	runSearch: function(input){
		var url = input;
		var querySearch = QuoteToolServiceAPI.getQueryString()["query"];
		console.log(querySearch)
		if(querySearch !== null && querySearch !== undefined && querySearch !== "" && querySearch !== " "){
			url += "?query=" + querySearch;
		}

		//var mySearchResults = $.getJSON("search.json", function(json) {
		//	var mySearchResults = json.response.docs;
		//			console.log(mySearchResults)
		//			var resultsListHTML = "";
		//			if (mySearchResults.length != 0) {
		//				$('.display_container').removeClass('hidden');
		//				$(".page-count").removeClass('hidden');
		//				$(".no-results").addClass('hidden');
		//				//results_content is the default component for listing out general results
		//				resultsListHTML += "<div class=\"results_content\">";
		//				console.log("response length" + mySearchResults.length)
		//				for (var i = 0; i < mySearchResults.length; i++) {
		//
		//					resultsListHTML += "<div class=\"article\">";
		//					resultsListHTML += "<p><a href=\"" + mySearchResults[i].url + "\">" + mySearchResults[i].title + "</a></p>";
		//					resultsListHTML += "<p>" + mySearchResults[i].content + "</p>";
		//					resultsListHTML += "</div>";
		//				}
		//				resultsListHTML += "</div>";
		//			}
		//			else {
		//				$('.display_container').removeClass('hidden');
		//				$(".page-count").addClass('hidden');
		//				$(".no-results").removeClass('hidden');
		//			}
		//			$(resultsListHTML).insertBefore($(".results_pagination"));
		//
		//});
		$.ajax({
			url: url,
			contentType: "application/json; charset=utf-8",
			async: true,
			dataType:'json',
			type: 'GET',
			success: function(data) {
				var mySearchResults = data.response.docs;
				console.log(mySearchResults)
				var resultsListHTML = "";
				if (mySearchResults.length != 0) {
					$('.display_container').removeClass('hidden');
					$(".page-count").removeClass('hidden');
					$(".no-results").addClass('hidden');
					//results_content is the default component for listing out general results
					resultsListHTML += "<div class=\"results_content\">";
					console.log("response length" + mySearchResults.length)
					for (var i = 0; i < mySearchResults.length; i++) {

						resultsListHTML += "<div class=\"article\">";
						resultsListHTML += "<p><a href=\"" + mySearchResults[i].url + "\">" + mySearchResults[i].title + "</a></p>";
						resultsListHTML += "<p>" + mySearchResults[i].content + "</p>";
						resultsListHTML += "</div>";
					}
					resultsListHTML += "</div>";
				}
				else {
					$('.display_container').removeClass('hidden');
					$(".page-count").addClass('hidden');
					$(".no-results").removeClass('hidden');
				}
				$(resultsListHTML).insertBefore($(".results_pagination"));

			},
			error: function(e) {
				console.log('error ',e);
			},
			timeout:30000
		});
	},
	redirectToSearchResultsPage: function(input){
		var searchTerm = sessionStorage.setItem("seachTerm" ,$(input).text);
		var url = $("#metSearchForm").attr("data-path-to-search-results");
		console.log(url);
		window.location.href = url;
	},
	searchResultsPageLoad: function(){
		var input = sessionStorage.getItem("searchTerm");
		if(input !== null && input !== undefined && input !== "" && input !== " "){

			if($("#searchInput").css("display") !== " none"){
				$("#searchInput").text(input);
				$(".search-button").click();
			}else{
				$("#searchInputMobile").text(input);
				$(".search-image").click();
			}
		}

	}
};