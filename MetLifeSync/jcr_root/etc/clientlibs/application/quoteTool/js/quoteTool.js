var QuoteToolAPI = {} || QuoteToolAPI;

QuoteToolAPI = {
	selectedInsurance : null,
	selectedState : null,
	quoteToolType : null,
	gawliStates : "AL,AK,AZ,AR,CA,CO,CT,DE,DC,FL,GA,HI,ID,IL,IN,IA,KS,KY,LA,ME,MD,MI,MN,MS,MO,MT,NE,NV,NH,NJ,NM,NY,NC,ND,OH,OK,OR,PA,RI,SC,SD,TN,TX,UT,VT,VA,WV,WI,WY",
	gawliOnlineAvailableStates : "AL,AK,AZ,AR,CA,CO,CT,DE,DC,FL,GA,HI,ID,IL,IN,IA,KS,KY,LA,ME,MD,MI,MN,MS,MO,NE,NV,NH,NJ,NM,NC,ND,OH,OK,OR,PA,RI,SC,SD,TN,TX,UT,VT,VA,WV,WI,WY",
	sitStates : "AL,AK,AZ,AR,CO,CT,DE,DC,FL,GA,HI,ID,IL,IN,IA,KS,KY,LA,ME,MD,MA,MI,MN,MS,MO,MT,NE,NV,NH,NJ,NM,NC,ND,OH,OK,OR,PA,RI,SC,SD,TN,TX,UT,VT,VA,WV,WI,WY",
	sitOnlineAvailableStates : "AL,AK,AZ,AR,CO,CT,DE,DC,FL,GA,HI,ID,IL,IN,IA,KS,KY,LA,ME,MD,MA,MI,MN,MS,MO,NE,NV,NH,NJ,NM,NC,ND,OH,OK,OR,PA,RI,SC,SD,TN,TX,UT,VT,VA,WV,WI,WY",
	mltApprovedStates : "AL,AK,AZ,AR,CA,CO,CT,DC,DE,FL,GA,HI,IA,ID,IL,IN,KS,KY,LA,MA,MD,MI,MN,MO,MS,MT,NC,ND,NE,NH,NJ,NM,NV,NY,OH,OK,OR,PA,RI,SC,SD,TN,TX,UT,VA,VT,WA,WI,WV,WY",
	sitCompatibleAge : null,
	quoteOption : 'online',
	quoteToolStarted: false,
	gawliAgeError: false,
	coverage : {
		"GAWLIAmounts" : ["$2,500","$5,000","$7,500","$10,000","$15,000","$20,000","$30,000","$40,000","$50,000"],
		"GAWLIValues" : ["2500","5000","7500","10000","15000","20000","30000","40000","50000"],
		/*	"GLTAmounts" : ["$750,000","$1,000,000","$1,500,000","$2,000,000","$2,500,000","$3,000,000","$3,500,000","$4,000,000","$4,500,000","$5,000,000"],
		 "GLTValues" : ["750000","1000000","1500000","2000000","2500000","3000000","3500000","4000000","4500000","5000000"],
		 "MLTAmounts" : ["$100,000","$150,000","$200,000","$250,000","$300,000","$400,000","$500,000"],
		 "MLTValues" : ["100000","150000","200000","250000","300000","400000","500000"],
		 "SITAmounts" : ["$10,000","$20,000","$30,000","$50,000"],
		 "SITValues" : ["10000","20000","30000","50000"],*/
		"TERMAmounts" : ["$10,000","$20,000","$30,000","$50,000","$100,000","$150,000","$200,000","$250,000","$300,000","$400,000","$500,000","$750,000","$1,000,000","$1,500,000","$2,000,000","$2,500,000","$3,000,000","$3,500,000","$4,000,000","$4,500,000","$5,000,000"],
		"TERMValues" : ["10000","20000","30000","50000","100000","150000","200000","250000","300000","400000","500000","750000","1000000","1500000","2000000","2500000","3000000","3500000","4000000","4500000","5000000"],
		"MLTGLTAmounts" : ["$100,000","$150,000","$200,000","$250,000","$300,000","$400,000","$500,000","$750,000","$1,000,000","$1,500,000","$2,000,000","$2,500,000","$3,000,000","$3,500,000","$4,000,000","$4,500,000","$5,000,000"],
		"MLTGLTValues" : ["100000","150000","200000","250000","300000","400000","500000","750000","1000000","1500000","2000000","2500000","3000000","3500000","4000000","4500000","5000000"]
	},
	gawliAgeCriteria : {
		"AR" : [45,70],
		"MN" : [45,65],
		"MO" : [45,75],
		"NE" : [45,75],
		"NJ" : [45,75],
		"PA" : [56,70]
	},
	termLength : {
		51 : [10,15,20,30],
		66 : [10,15,20],
		71 : [10,15],
		76 : [10]
	},
	termLength_NY : {
		66 : [10,15,20],
		76 : [10]
	},
	termLength_WA : {
		51 : [10,15,20,30],
		66 : [10,15,20],
		71 : [10,15]
	}
};

$(document).ready(function(){
	QuoteToolAPI.loadEventListeners();
	if($("#submitBtn[data-page='quotes']").length = !0 && $("#recalculateQuote").attr('data-flow') != "GAWLI"){
		QuoteToolAPI.quoteToolType = 'SIT';
	}
});

QuoteToolAPI.loadEventListeners = function(){
	$("#insurance-type").change(function(){
		QuoteToolAPI.selectedInsurance = $(this).val();
		if(QuoteToolAPI.selectedInsurance == 'gawli'){
			QuoteToolAPI.quoteToolType = 'GAWLI';
			$('#term_txt').addClass('hidden');
			$('#gawli_txt').removeClass('hidden');
			$(this).removeClass('errorField errorArrow');
			var d = new Date();
			d = d.getFullYear();
			var r = QuoteToolAPI.gawliAgeCriteria[$("#state1-mmquote").val()];
			if(r != 'undefined')
			{
				if(typeof (r) == "object")
				{
					QuoteToolAPI.populateYearDropDown(d-r[1],18,"#year-mmquote");
				}
				else
				{
					QuoteToolAPI.populateYearDropDown(d-80,18,"#year-mmquote");
				}
			}
		}
		else if(QuoteToolAPI.selectedInsurance == 'term'){
			$(".error_state_coverage").hide();
			//console.log('term');
			QuoteToolAPI.quoteToolType = 'SIT';
			$('#term_txt').removeClass('hidden');
			$('#gawli_txt').addClass('hidden');
			$(this).removeClass('errorField errorArrow');
		}
		if(QuoteToolAPI.quoteToolStarted)
		{
			$(".quote_tool_form,.error_age_coverage,.error_state_coverage").hide();
			$("#ctaHeaderQuoteSubmit").show();
			$(".dob_cta_quote").removeClass('visible-lg');
			var quoteHeight = $(".cta_header_quote").height();
			var findXHeight = $(".cta_header_find_x").height();
			/*if ($(window).width() <= 751) {
			 //$(".icon_scroll_bar").css("margin-top", "325px");
			 }
			 if ($(window).width() >= 751 && $(window).width() <= 1024) {
			 $(".icon_scroll_bar").css("margin-top", "175px");
			 if (quoteHeight < findXHeight) {
			 $(".icon_scroll_bar").css("margin-top", "260px");
			 }
			 }
			 if ($(window).width() >= 1025) {
			 $(".icon_scroll_bar").css("margin-top", "122px");
			 if (quoteHeight < findXHeight) {
			 $(".icon_scroll_bar").css("margin-top", "212px");
			 }

			 }*/

			$("#month-mmquote,#day-mmquote,#year-mmquote,#gender-mmquote,#term-mmquote,#tobacco-mmquote,#health-mmquote").val("");
			$(".online_cta_check").attr("checked", true);
			$(".rep_cta_check").removeAttr("checked");
			$(".online_cta_check").prop("checked", true);
			$(".rep_cta_check").prop("checked");
			setRadioImage();
		}
		/* for LI quote */
		if($("#submitBtn[data-page='quotes']").length != 0){
			QuoteToolAPI.goOnBlur();
		}
		QuoteToolAPI.updateCoverageAmount("#coverage-mmquote");
	});
	$("#state1-mmquote").change(function(){
		QuoteToolAPI.selectedState = $(this).val();
		if(QuoteToolAPI.quoteToolStarted)
		{
			var quoteHeight = $(".cta_header_quote").height();
			var findXHeight = $(".cta_header_find_x").height();
			$(".quote_tool_form,.error_age_coverage,.error_state_coverage").hide();
			$("#ctaHeaderQuoteSubmit").show();
			$(".dob_cta_quote").removeClass('visible-lg');
			/*if ($(window).width() <= 751) {
			 $(".icon_scroll_bar").css("margin-top", "325px");
			 }
			 if ($(window).width() >= 751 && $(window).width() <= 1024) {
			 $(".icon_scroll_bar").css("margin-top", "235px");
			 if (quoteHeight < findXHeight) {
			 $(".icon_scroll_bar").css("margin-top", "260px");
			 }
			 }
			 if ($(window).width() >= 1025) {
			 $(".icon_scroll_bar").css("margin-top", "212px");
			 if (quoteHeight < findXHeight) {
			 $(".icon_scroll_bar").css("margin-top", "212px");
			 }*/
			$("#month-mmquote,#day-mmquote,#year-mmquote,#gender-mmquote,#term-mmquote,#tobacco-mmquote,#health-mmquote").val("");
			$(".online_cta_check").attr("checked", true);
			$(".rep_cta_check").removeAttr("checked");
			$(".online_cta_check").prop("checked", true);
			$(".rep_cta_check").prop("checked");
			setRadioImage();
		}

		if($("#state1-mmquote").val() != null || $("#state1-mmquote").val() != '')
		{
			$(".select_state_cta_quote").removeClass('errorField errorArrow');

		}

		/* for LI quote */
		if($("#submitBtn[data-page='quotes']").length != 0){
			QuoteToolAPI.goOnBlur();
			if(QuoteToolAPI.selectedInsurance == 'gawli'){
				$(".error_state_not_selected").hide();
				if(QuoteToolAPI.gawliStates.indexOf(QuoteToolAPI.selectedState) == -1){
					if($(".dob_label").is(':visible'))
					{
						$(".gawli_error_state").insertAfter(".select_state");
					}
					else{
						$(".gawli_error_state").insertAfter(".dob_input");
					}
					$(".error_state_coverage").show();

				}
				else
				{
					$(".error_state_coverage").hide();
				}
			}
			else
			{
				if(QuoteToolAPI.sitStates.indexOf(QuoteToolAPI.selectedState) == -1){
					$(".quoteBottom").show();
					$("#term-mmquote,#tobacco-mmquote,#health-mmquote").attr('data-validation',true);
				}
				else
				{
					$(".quoteBottom").hide();
					$("#term-mmquote,#tobacco-mmquote,#health-mmquote").removeAttr('data-validation').removeClass('errorField');
				}
			}
		}
		if(QuoteToolAPI.sitStates.indexOf(QuoteToolAPI.selectedState) == -1){
			QuoteToolAPI.verifyMLTorGLT();
		}
		QuoteToolAPI.updateCoverageAmount("#coverage-mmquote");
		QuoteToolAPI.selectedInsurance = $("#insurance-type").val();
		if(QuoteToolAPI.selectedInsurance == 'gawli'){
			QuoteToolAPI.quoteToolType = 'GAWLI';
			$('#term_txt').addClass('hidden');
			$('#gawli_txt').removeClass('hidden');
			$(this).removeClass('errorField errorArrow');
			var d = new Date();
			d = d.getFullYear();
			var r = QuoteToolAPI.gawliAgeCriteria[$("#state1-mmquote").val()];
			console.log(r != 'undefined');
			if(r != 'undefined')
			{
				if(typeof (r) == "object")
				{
					QuoteToolAPI.populateYearDropDown(d-r[1],18,"#year-mmquote");
				}
				else
				{
					QuoteToolAPI.populateYearDropDown(d-80,18,"#year-mmquote");
				}
			}
		}
	});

	$("#ctaHeaderQuoteSubmit").click(function(e){
		e.preventDefault();
		if($("#insurance-type").val() == null || $("#insurance-type").val()  == ''){
			$("#insurance-type").addClass('errorField errorArrow');
		}
		if($("#state1-mmquote").val() == null || $("#state1-mmquote").val() == '')
		{
			$(".select_state_cta_quote").addClass('errorField errorArrow');
		}
		else //if(!QuoteToolAPI.areErrorFieldsPresent)
		{
			QuoteToolAPI.selectedInsurance = $("#insurance-type").val();
			var d = new Date();
			d = d.getFullYear();
			if(QuoteToolAPI.selectedInsurance == 'gawli'){
				if(QuoteToolAPI.gawliStates.indexOf(QuoteToolAPI.selectedState) == -1){
					if($(".dob_label").is(':visible'))
					{
						$(".gawli_error_state").insertAfter(".select_state");
					}
					else{
						$(".gawli_error_state").insertAfter(".dob_input");
					}
					$(".error_state_coverage").show();

				}
				else{
					$(".quote_tool_form").show();
					$(".quoteBottom,.error_state_coverage").hide();
					$(".select_state_cta_quote").removeClass('errorField errorArrow');
					$("#insurance-type").removeClass('errorField errorArrow');
					$(".dob_cta_quote").insertBefore($(".state_wrapper")).addClass('visible-lg');
					$("#month-mmquote,#day-mmquote,#year-mmquote,#gender-mmquote,#coverage-mmquote").attr('data-validation',true);
					$(this).hide();
					QuoteToolAPI.quoteToolType = 'GAWLI';
					QuoteToolAPI.updateCoverageAmount("#coverage-mmquote");
					QuoteToolAPI.quoteToolStarted = true;
					var r = QuoteToolAPI.gawliAgeCriteria[QuoteToolAPI.selectedState];
					if(typeof (r) == "object")
					{
						QuoteToolAPI.populateYearDropDown(d-r[1],18,"#year-mmquote");
					}
					else
					{
						QuoteToolAPI.populateYearDropDown(d-80,18,"#year-mmquote");
					}
				}
				QuoteToolAPI.quoteToolType = 'GAWLI';
				QuoteToolAPI.sitCompatibleAge = false;
			}
			else if(QuoteToolAPI.selectedInsurance == 'term'){
				var d = new Date(), r = QuoteToolAPI.sitStates.indexOf(QuoteToolAPI.selectedState);
				d = d.getFullYear();
				$(".quote_tool_form").show();
				$(this,".error_state_coverage").hide();
				$(".dob_cta_quote").insertBefore($(".state_wrapper")).addClass('visible-lg');
				$(".select_state_cta_quote").removeClass('errorField errorArrow');
				$("#insurance-type").removeClass('errorField errorArrow');
				$("#month-mmquote,#day-mmquote,#year-mmquote,#gender-mmquote,#coverage-mmquote").attr('data-validation',true);
				QuoteToolAPI.quoteToolType = 'SIT';
				QuoteToolAPI.populateYearDropDown(d-75,18,"#year-mmquote");
				QuoteToolAPI.quoteToolStarted = true;
				QuoteToolAPI.updateTermLength("#term-mmquote","home");
				if(r == -1)
				{
					$(".quoteBottom").show();
					QuoteToolAPI.quoteToolType = 'MLT';
				}
				else
				{
					$(".quoteBottom").hide();
				}
				QuoteToolAPI.updateCoverageAmount("#coverage-mmquote");
			}

			/*var quoteHeight = $(".cta_header_quote").height();
			 var findXHeight = $(".cta_header_find_x").height();
			 if ($(window).width() <= 751) {
			 $(".icon_scroll_bar").css("margin-top", "325px");
			 }
			 if ($(window).width() >= 751 && $(window).width() <= 1024) {
			 if ($("#insurance-type").hasClass("errorField") || $("#state1-mmquote").hasClass("errorField")) {
			 if (quoteHeight >= findXHeight) {
			 $(".icon_scroll_bar").css("margin-top", "174px");
			 }
			 } else {
			 $(".icon_scroll_bar").css("margin-top", "410px");
			 }

			 }
			 if ($(window).width() >= 1025) {
			 if ($("#insurance-type").hasClass("errorField") || $("#state1-mmquote").hasClass("errorField")) {
			 if (quoteHeight >= findXHeight) {
			 $(".icon_scroll_bar").css("margin-top", "122px");
			 }
			 } else {
			 $(".icon_scroll_bar").css("margin-top", "252px");
			 }
			 }
			 if(($("#insurance-type").val() == "term")&&($("#state1-mmquote").val()== "CA" || $("#state1-mmquote").val()== "WA" || $("#state1-mmquote").val()== "NY" || $("#state1-mmquote").val()== "PR")){
			 if ($(window).width() >= 751 && $(window).width() <= 1024) {
			 $(".icon_scroll_bar").css("margin-top", "622px");

			 }
			 if ($(window).width() >= 1025) {
			 $(".icon_scroll_bar").css("margin-top", "422px");
			 }
			 }*/
		}
	});
	$("#month-mmquote,#day-mmquote,#year-mmquote").change(function(){
		var age = QuoteToolAPI.calculateAge();
		if(QuoteToolAPI.quoteToolType == 'GAWLI' && age != 0){
			var range = QuoteToolAPI.gawliAgeCriteria[QuoteToolAPI.selectedState];
			if(typeof(range) == "object")
			{
				if(age < range[0] || age > range[1])
				{
					$(".error_age_coverage").show();
					/*var quoteHeight = $(".cta_header_quote").height();
					 var findXHeight = $(".cta_header_find_x").height();
					 if ($(window).width() >= 751 && $(window).width() <= 1024) {
					 $(".icon_scroll_bar").css("margin-top", "478px");

					 }
					 if ($(window).width() >= 1025) {
					 $(".icon_scroll_bar").css("margin-top", "340px");

					 }*/
				}
				else
				{
					$(".error_age_coverage").hide();
				}
			}
			else if(age < 45 || age > 80)
			{
				$(".error_age_coverage").show();
				/*var quoteHeight = $(".cta_header_quote").height();
				 var findXHeight = $(".cta_header_find_x").height();
				 if ($(window).width() >= 751 && $(window).width() <= 1024) {
				 $(".icon_scroll_bar").css("margin-top", "478px");

				 }
				 if ($(window).width() >= 1025) {
				 $(".icon_scroll_bar").css("margin-top", "340px");

				 }*/
			}
			else
			{
				$(".error_age_coverage").hide();
			}
		}
		else {
			if(age > 70 && age < 76){// && $("#coverage-mmquote").val() < 750000){
				//console.log('71-75');
				$(".quoteBottom").show();
				$("#term-mmquote,#tobacco-mmquote,#health-mmquote").attr('data-validation',true);
				QuoteToolAPI.sitCompatibleAge = false;
				QuoteToolAPI.verifyMLTorGLT();
				QuoteToolAPI.updateCoverageAmount("#coverage-mmquote");
			}
			else if(age > 17 && age < 71){
				//console.log('18-70');
				if(QuoteToolAPI.sitStates.indexOf($("#state1-mmquote").val()) != -1)
				{
					if($("#coverage-mmquote").val() > 99999)
					{
						$(".quoteBottom").show();
						$("#term-mmquote,#tobacco-mmquote,#health-mmquote").attr('data-validation',true);
						QuoteToolAPI.sitCompatibleAge = false;
						QuoteToolAPI.updateTermLength("#term-mmquote","home");
						QuoteToolAPI.verifyMLTorGLT();
					}
					else
					{
						$(".quoteBottom").hide();
						$(".quoteTermLength,.quoteTobacco,.quoteHealth,.quoteOptions").addClass('hidden').removeClass('errorField');
						$("#term-mmquote,#tobacco-mmquote,#health-mmquote").removeAttr('data-validation');
						QuoteToolAPI.sitCompatibleAge = true;
						QuoteToolAPI.quoteToolType = 'SIT';
					}
				}
				QuoteToolAPI.updateCoverageAmount("#coverage-mmquote");
			}
			QuoteToolAPI.updateTermLength("#term-mmquote","home");
		}
	});
	$("#coverage-mmquote").change(function(){
		var val = $(this).val();
		if(QuoteToolAPI.quoteToolType != 'GAWLI')
		{
			if(parseInt(val) < 50001 && (QuoteToolAPI.calculateAge() == 0 || (QuoteToolAPI.calculateAge() > 17 && QuoteToolAPI.calculateAge() < 71)) && QuoteToolAPI.sitStates.indexOf(QuoteToolAPI.selectedState) != -1){
				$(".quoteBottom").hide();
				$("#term-mmquote,#tobacco-mmquote,#health-mmquote").removeAttr('data-validation').removeClass('errorField')
				QuoteToolAPI.quoteToolType = 'SIT';
				//less margin
				/*if ($(window).width() <= 751) {
				 $(".icon_scroll_bar").css("margin-top", "325px");
				 }
				 if ($(window).width() >= 751 && $(window).width() <= 1024) {
				 $(".icon_scroll_bar").css("margin-top", "415px");
				 }
				 if ($(window).width() >= 1025) {
				 $(".icon_scroll_bar").css("margin-top", "250px");
				 }*/
			}
			else{
				$(".quoteBottom").show();
				$("#term-mmquote,#tobacco-mmquote,#health-mmquote").attr('data-validation',true);
				QuoteToolAPI.verifyMLTorGLT();
				//more margin

				/*if ($(window).width() <= 751) {
				 $(".icon_scroll_bar").css("margin-top", "325px");
				 }
				 if ($(window).width() >= 751 && $(window).width() <= 1024) {
				 $(".icon_scroll_bar").css("margin-top", "670px");
				 }
				 if ($(window).width() >= 1025) {
				 $(".icon_scroll_bar").css("margin-top", "442px");
				 }*/
			}
		}
	});
	$("input[name='options']").change(function(){
		QuoteToolAPI.quoteOption = $(this).val();
	});
	$("#submitBtn").click(function(e){
		e.preventDefault();
		var birthMonth = $("#month-mmquote").val();
		var birthDay = $("#day-mmquote").val();
		var birthYear = $("#year-mmquote").val();
		var dob = new Date();

		if (birthMonth != "" && (isNaN(birthMonth) == false)) {
			dob.setMonth(birthMonth);
		}
		if (birthDay != "" && (isNaN(birthDay) == false)) {
			dob.setDate(birthDay);
		}
		if (birthYear != "" && (isNaN(birthYear) == false)) {
			dob.setYear(birthYear);
		}
		var cancelRedirect = QuoteToolAPI.validateFields();
		var age = QuoteToolAPI.calculateAge();
		if ($("#insurance-type").val() == "gawli") {
			if(QuoteToolAPI.selectedState)
			{
				if(QuoteToolAPI.gawliStates.indexOf(QuoteToolAPI.selectedState) != -1){
					var range = QuoteToolAPI.gawliAgeCriteria[QuoteToolAPI.selectedState];
					if(typeof(range) == "object" && (age < range[0] || age > range[1]))
					{
						$(".error_age_coverage").show();
						/*var quoteHeight = $(".cta_header_quote").height();
						 var findXHeight = $(".cta_header_find_x").height();
						 if ($(window).width() >= 751 && $(window).width() <= 1024) {
						 $(".icon_scroll_bar").css("margin-top", "478px");

						 }
						 if ($(window).width() >= 1025) {
						 $(".icon_scroll_bar").css("margin-top", "340px");

						 }*/
					}
					else if(age < 45 || age > 80){
						$(".error_age_coverage").show();
						/*var quoteHeight = $(".cta_header_quote").height();
						 var findXHeight = $(".cta_header_find_x").height();
						 if ($(window).width() >= 751 && $(window).width() <= 1024) {
						 $(".icon_scroll_bar").css("margin-top", "478px");

						 }
						 if ($(window).width() >= 1025) {
						 $(".icon_scroll_bar").css("margin-top", "340px");

						 }*/
					}
					else
					{
						$(".error_age_coverage").hide();
						if(!cancelRedirect)
						{
							QuoteToolAPI.getQuotePremiumGAWLI();
						}
					}
				}
			}
			else
			{
				$(".error_state_not_selected").show();
			}
		}
		else if ($("#insurance-type").val() == "term") {

			if(QuoteToolAPI.quoteToolType == 'SIT')
			{
				if(age < 18 || age > 70){
					$(".error_age_coverage").show();
					/*var quoteHeight = $(".cta_header_quote").height();
					 var findXHeight = $(".cta_header_find_x").height();
					 if ($(window).width() >= 751 && $(window).width() <= 1024) {
					 $(".icon_scroll_bar").css("margin-top", "478px");

					 }
					 if ($(window).width() >= 1025) {
					 $(".icon_scroll_bar").css("margin-top", "340px");

					 }*/
				}
				else
				{
					$(".error_age_coverage").hide();
					if(!cancelRedirect)
					{
						QuoteToolAPI.getQuotePremiumSIT();
					}
				}
			}
			else if(QuoteToolAPI.quoteToolType == 'MLT')
			{
				if(!cancelRedirect)
				{
					QuoteToolAPI.getQuotePremiumMLT();
				}
			}
			else if(QuoteToolAPI.quoteToolType == 'GLT')
			{
				if(!cancelRedirect)
				{
					QuoteToolAPI.getQuotePremiumGLT();
				}
			}
		}
	});
	$(document).on("blur","[data-validation='true']",function(){
		if($(this).val() != null && $(this).val() != '')
		{
			$(this).removeClass('errorField errorArrow');
			if($(this).hasClass('birth_month_cta_quote') || $(this).hasClass('birth_day_cta_quote') || $(this).hasClass('birth_year_cta_quote'))
			{
				var age = null;
				if($("#recalculateQuote").length != 0)
				{
					age = QuoteToolAPI.calculateAgeResults();
				}
				else
				{
					age = QuoteToolAPI.calculateAge();
				}
				if(age != 0)
				{
					$(".birth_month_cta_quote,.birth_day_cta_quote,.birth_year_cta_quote").removeClass('errorField errorArrow');
				}
			}
		}
	});
	$("#month-mmquote").on("change", function () {
		QuoteToolAPI.populateDaysDropDown("#");
	});
	$("#year-mmquote").on("change", function () {
		QuoteToolAPI.populateDaysDropDown("#");
	});
}

QuoteToolAPI.calculateAge = function(){
	var l = 0;
	if ((document.getElementById("month-mmquote").value != "") && (document.getElementById("day-mmquote").value != "") && (document.getElementById("year-mmquote").value != "")) {
		var b = parseInt(document.getElementById("month-mmquote").value);
		var k = parseInt(document.getElementById("day-mmquote").value);
		var m = parseInt(document.getElementById("year-mmquote").value);
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
		if (QuoteToolAPI.quoteToolType == 'GLT' && a>= 6) {l = l + 1}
	} else {}
	return l;
}

QuoteToolAPI.verifyMLTorGLT = function(){
	if(QuoteToolAPI.quoteOption == 'online')// && QuoteToolAPI.mltApprovedStates.indexOf(QuoteToolAPI.selectedState) != -1)
	{
		QuoteToolAPI.quoteToolType = 'MLT';
		/*if(QuoteToolAPI.mltApprovedStates.indexOf(QuoteToolAPI.selectedState) != -1)
		 {
		 QuoteToolAPI.quoteToolType = 'MLT';
		 }
		 else
		 {
		 QuoteToolAPI.quoteToolType = 'GLT';
		 }*/
	}
	else if(QuoteToolAPI.quoteOption == 'advisor')
	{
		QuoteToolAPI.quoteToolType = 'GLT';
	}
};

QuoteToolAPI.updateCoverageAmount = function(id) {
	var amounts,values,len,coverageOption = QuoteToolAPI.quoteToolType;
	//console.log(QuoteToolAPI.quoteToolType);
	//console.log(coverageOption);
	//console.log(QuoteToolAPI.sitStates.indexOf(QuoteToolAPI.selectedState));
	var age = null;
	if(id == "#coverage-mmquote")
	{
		age = QuoteToolAPI.calculateAge();
	}
	else if(id == "#edit-coverage-mmquote")
	{
		age = QuoteToolAPI.calculateAgeResults();
	}
	if(QuoteToolAPI.quoteToolType == 'GAWLI')
	{
		coverageOption = 'GAWLI';
	}
	else if((QuoteToolAPI.quoteToolType == 'GLT' || QuoteToolAPI.quoteToolType == 'MLT'))
	{
		if(((QuoteToolAPI.sitStates.indexOf(QuoteToolAPI.selectedState) != -1) && age>70) || (QuoteToolAPI.sitStates.indexOf(QuoteToolAPI.selectedState) == -1))
		{
			coverageOption = 'MLTGLT';
		}
		else
		{
			coverageOption = 'TERM';
		}
	}
	else//if(QuoteToolAPI.quoteToolType == 'SIT')
	{
		coverageOption = 'TERM';
	}

	amounts = QuoteToolAPI.coverage[coverageOption + "Amounts"];
	values = QuoteToolAPI.coverage[coverageOption + "Values"];
	//console.log(coverageOption);
	len = amounts.length;
	$(id).children().remove();
	$(id).append("<option value=''>Coverage Amount</option>");

	for(var i=0; i<len; i++)
	{
		$(id).append("<option value="+values[i]+">"+amounts[i]+"</option>");
	}
}

QuoteToolAPI.redirectToResultsPage = function(quotePremium) {
	var urlParamString = '';
	urlParamString += 's=' + $("#state1-mmquote").val() + ',';
	urlParamString += 'd=' + $("#day-mmquote").val() + ',';
	urlParamString += 'm=' + $("#month-mmquote").val() + ',';
	urlParamString += 'y=' + $("#year-mmquote").val() + ',';
	urlParamString += 'g=' + $("#gender-mmquote").val() + ',';
	urlParamString += 'c=' + $("#coverage-mmquote").val();
	if(QuoteToolAPI.quoteToolType == 'MLT' || QuoteToolAPI.quoteToolType == 'GLT')
	{
		urlParamString += ',t=' + $("#term-mmquote").val();
		urlParamString += ',n=' + $("#tobacco-mmquote").val() + ',';
		urlParamString += 'h=' + $("#health-mmquote").val();
	}
	urlParamString = QuoteToolAPI.base64Encode(urlParamString);
	console.log(urlParamString);
	var x = window.location.pathname;
	var urlBase = x.substring(0, x.lastIndexOf('/')+1);
	var sitRedirectPath = $('.quoteInsurance')[0].getAttribute('data-sit-redirect-path') || '#';
    var gawliRedirectPath = $('.quoteInsurance')[0].getAttribute('data-gawli-redirect-path') || '#';

	if($("#submitBtn").attr('data-page') && $("#submitBtn").attr('data-page') == 'quotes')
	{
		urlBase= x.substring(0, x.lastIndexOf('/insurance')+1);
	}
	var onlineAvailable = "n";
	if(QuoteToolAPI.quoteToolType == 'GAWLI'){
		if(QuoteToolAPI.gawliOnlineAvailableStates.indexOf(QuoteToolAPI.selectedState) != -1){
			onlineAvailable = "y";
		}
		//window.location.href = urlBase + "Other\\GAWLI Results\\guaranteed-acceptance.html?"+"ol="+QuoteToolAPI.base64Encode(onlineAvailable)+"&fv="+urlParamString+"&q="+QuoteToolAPI.base64Encode(quotePremium);
		//window.location.href = urlBase + "quote-results/guaranteed-acceptance.html?"+"ol="+QuoteToolAPI.base64Encode(onlineAvailable)+"&fv="+urlParamString+"&q="+QuoteToolAPI.base64Encode(quotePremium);
		window.location.href = gawliRedirectPath + "?ol="+QuoteToolAPI.base64Encode(onlineAvailable)+"&fv="+urlParamString+"&q="+QuoteToolAPI.base64Encode(quotePremium);
    }
	else if(QuoteToolAPI.quoteToolType == 'SIT'){
		if(QuoteToolAPI.sitOnlineAvailableStates.indexOf(QuoteToolAPI.selectedState) != -1){
			onlineAvailable = "y";
		}
		//window.location.href = urlBase + "Other\\SIT Results\\simplified-issue.html?"+"ol="+QuoteToolAPI.base64Encode(onlineAvailable)+"&fv="+urlParamString+"&q="+QuoteToolAPI.base64Encode(quotePremium);
		//window.location.href = urlBase + "quote-results/simplified-issue.html?"+"ol="+QuoteToolAPI.base64Encode(onlineAvailable)+"&fv="+urlParamString+"&q="+QuoteToolAPI.base64Encode(quotePremium);
		window.location.href = sitRedirectPath + "?ol="+QuoteToolAPI.base64Encode(onlineAvailable)+"&fv="+urlParamString+"&q="+QuoteToolAPI.base64Encode(quotePremium);

	}
	else if(QuoteToolAPI.quoteToolType == 'MLT'){
		if(QuoteToolAPI.selectedState != 'NY'){
			onlineAvailable = "y";
		}
		window.location.href = urlBase + "Other\\MLT Results\\term-life.html?"+"ol="+QuoteToolAPI.base64Encode(onlineAvailable)+"&fv="+urlParamString+"&q="+QuoteToolAPI.base64Encode(quotePremium);
		window.location.href = urlBase + "quote-results/term-life.html?"+"ol="+QuoteToolAPI.base64Encode(onlineAvailable)+"&fv="+urlParamString+"&q="+QuoteToolAPI.base64Encode(quotePremium);
	}
	else if(QuoteToolAPI.quoteToolType == 'GLT'){
		//window.location.href = urlBase + "Other\\GLT Results\\guaranteed-level.html?"+"ol="+QuoteToolAPI.base64Encode('')+"&fv="+urlParamString+"&q="+QuoteToolAPI.base64Encode(quotePremium);
		window.location.href = urlBase + "quote-results/guaranteed-level.html?"+"ol="+QuoteToolAPI.base64Encode('')+"&fv="+urlParamString+"&q="+QuoteToolAPI.base64Encode(quotePremium);
	}
}

QuoteToolAPI.validateFields = function() {
	var areErrorFieldsPresent = false;
	$("[data-validation='true']").each(function(){
		if($(this).val() == null || $(this).val() == '')
		{
			$(this).addClass('errorField errorArrow');
			areErrorFieldsPresent = true;
		}
		console.log(areErrorFieldsPresent);
	});
	return areErrorFieldsPresent;
}

QuoteToolAPI.populateYearDropDown = function(year,min,element) {
	var yearOptions = $(element);
	var yr = new Date();
	yr = yr.getFullYear() - min;
	$(element).children().remove();
	$(element).append("<option value=''>YYYY</option>");
	for (i = yr; i >= year; i--) {
		var optionValue = i;
		yearOptions.append($('<option>', {
			value: optionValue,
			text: optionValue.toString()
		}));
	}
}

QuoteToolAPI.populateDaysDropDown = function (id) {
	if (($(id+"month-mmquote").val() == "09") || ($(id+"month-mmquote").val() == "04") ||
		($(id+"month-mmquote").val() == "06") || ($(id+"month-mmquote").val() == "11")) {
		$(id+"day-mmquote option:eq(31)").remove();
	} else if ($(id+"month-mmquote").val() == "02") {

		if ((QuoteToolAPI.isLeapYear($(id+"year-mmquote").val()) == false) || $(id+"year-mmquote").val() == "") {
			$(id+"day-mmquote option:eq(31)").remove();
			$(id+"day-mmquote option:eq(30)").remove();
			$(id+"day-mmquote option:eq(29)").remove();
		} else {
			if (($(id+"day-mmquote option[value='29']").length > 0) == false) {
				$(id+"day-mmquote").append('<option value="29">29</option>');
			}
			$(id+"day-mmquote option:eq(31)").remove();
			$(id+"day-mmquote option:eq(30)").remove();
		}

	} else {
		if (($(id+"day-mmquote option[value='29']").length > 0) == false) {
			$(id+"day-mmquote").append('<option value="29">29</option>');
		}
		if (($(id+"day-mmquote option[value='30']").length > 0) == false) {
			$(id+"day-mmquote").append('<option value="30">30</option>');
		}
		if (($(id+"day-mmquote option[value='31']").length > 0) == false) {
			$(id+"day-mmquote").append('<option value="31">31</option>');
		}
	}
}

QuoteToolAPI.base64Encode = function(g) {
	if (typeof(btoa) == "function") {
		return btoa(g)
	}
	var h = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
	var m, l, k;
	var d, c, b, a;
	var o = new Array();
	var e = 0;
	for (var f = 0; f < g.length; f += 3) {
		m = g.charCodeAt(f);
		l = g.charCodeAt(f + 1);
		k = g.charCodeAt(f + 2);
		d = m >> 2;
		c = ((m & 3) << 4) | (l >> 4);
		b = ((l & 15) << 2) | (k >> 6);
		a = k & 63;
		if (isNaN(l)) {
			b = a = 64;
		} else {
			if (isNaN(k)) {
				a = 64;
			}
		}
		o[e++] = h.charAt(d) + h.charAt(c) + h.charAt(b) + h.charAt(a)
	}
	return o.join("");
}

QuoteToolAPI.isLeapYear = function(a) {
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
}

QuoteToolAPI.getQuotePremiumGAWLI = function(){
	var flag = null;
	var date = new Date();
	var hours = date.getHours();
	var minutes = date.getMinutes();
	var seconds = date.getSeconds();
	if (minutes < 10) {
		minutes = "0" + minutes
	}
	if (seconds < 10) {
		seconds = "0" + seconds
	}
	var todaydate = date.getFullYear() + "-" + (date.getMonth() + 1) + "-" + date.getDate() + " " + hours + ":" + minutes + ":" + seconds;
	var dob = $("#month-mmquote").val()+"-"+$("#day-mmquote").val()+"-"+$("#year-mmquote").val();
	console.log(typeof dob);
	var reqObjParam = {"transaction":{"metaData":{},"transactionType":"diagnosticTool","entities":{"user":{"personalInfo":{"firstName":"","middleName":"",	"lastName":"","email":"","street":"","city":"","zip":"","stateDesc": QuoteToolAPI.selectedState,"primaryPhone":"","alternatePhone":""},"inputFields":{"gender":$("#gender-mmquote").val(),"dateOfBirth":$("#month-mmquote").val()+"-"+$("#day-mmquote").val()+"-"+$("#year-mmquote").val(),"state":QuoteToolAPI.selectedState,"faceAmount":$("#coverage-mmquote").val(),"productType":"GIWL","termLength":"10","age":QuoteToolAPI.calculateAge(),"health":"Healthy","replacement":"No","healthClass":"Standard","tobacco":"No"},"agentId":"","agentName":"","appSrc":"ML.com","campaignCode":"","channelType":"BroadMarket","cRMID":"","submittedDateTime":todaydate}}}};
	reqObjParam =JSON.stringify(reqObjParam);

	$.ajax({
		//url: "/wps/qadiagnosticToolProxy/sales/getProdRcmdAndQuoteForJSONInput?CN=USA",
		url: "/bin/diagnosticToolProxy/sales/getProdRcmdAndQuoteForJSONInput?CN=USA",
		type: 'POST',
		dataType:'json',
		data: reqObjParam,
		contentType: "application/json",
		success: function(e) {

			QuoteToolAPI.redirectToResultsPage(QuoteToolAPI.formatQuotePremium(e["GIWLResp"]["premium"]));
		},
		error: function(e) {
			console.log('error ',e);//	handleServiceError()
		},
		timeout:30000
	});
}

QuoteToolAPI.getQuotePremiumSIT = function(){
	var flag = null;
	var date = new Date();
	var hours = date.getHours();
	var minutes = date.getMinutes();
	var seconds = date.getSeconds();
	if (minutes < 10) {
		minutes = "0" + minutes
	}
	if (seconds < 10) {
		seconds = "0" + seconds
	}
	var todaydate = date.getFullYear() + "-" + (date.getMonth() + 1) + "-" + date.getDate() + " " + hours + ":" + minutes + ":" + seconds;
	var dob = $("#month-mmquote").val()+"-"+$("#day-mmquote").val()+"-"+$("#year-mmquote").val();
	var reqObjParam = {"transaction":{"metaData":{},"transactionType":"diagnosticTool","entities":{"user":{"personalInfo":{"firstName":"","middleName":"",	"lastName":"","email":"","street":"","city":"","zip":"","stateDesc": QuoteToolAPI.selectedState,"primaryPhone":"","alternatePhone":""},"inputFields":{"gender":$("#gender-mmquote").val(),"dateOfBirth":$("#month-mmquote").val()+"-"+$("#day-mmquote").val()+"-"+$("#year-mmquote").val(),"state":QuoteToolAPI.selectedState,"faceAmount":$("#coverage-mmquote").val(),"productType":"SIT","termLength":"10","age":QuoteToolAPI.calculateAge(),"health":"Healthy","replacement":"No","healthClass":"Standard","tobacco":"No"},"agentId":"","agentName":"","appSrc":"ML.com","campaignCode":"","channelType":"BroadMarket","cRMID":"","submittedDateTime":todaydate}}}};
	reqObjParam =JSON.stringify(reqObjParam);
	$.ajax({
		//url: "/wps/qadiagnosticToolProxy/sales/getProdRcmdAndQuoteForJSONInput?CN=USA",
		url: "/bin/diagnosticToolProxy/sales/getProdRcmdAndQuoteForJSONInput?CN=USA",
		type: 'POST',
		dataType:'json',
		data: reqObjParam,
		contentType: "application/json",
		success: function(e) {
			console.log('success ',e);
			QuoteToolAPI.redirectToResultsPage(QuoteToolAPI.formatQuotePremium(e['SITResp']['WithoutRider']['monthlypremium'].split(",")[1]));
		},
		error: function(e) {
			console.log('error ',e);
		},
		timeout:30000
	});
}

QuoteToolAPI.getQuotePremiumMLT = function(){
	var  jsonData={"term":$("#term-mmquote").val(),"age":QuoteToolAPI.calculateAge(),"gender":$("#gender-mmquote").val(),"health":$("#health-mmquote").val(),"tobacco":$("#tobacco-mmquote").val(),
		"coverage":$("#coverage-mmquote").val(),
		"state":$("#state1-mmquote").val(),
		"lstPnPParameters":"state,DOB,coverage,term,tobacco,health,gender,age,lStatus",
		"lStatus":"Q",
		"rating":0,
		"mcid":""
	};
	var URL;
	console.log(jsonData);
	if($("#submitBtn[data-page='quotes']").length == 1)
	{
		URL = "../../../wps/proxy/MCPremiumQuoteWS/MCCDTPremiumQuote";
	}
	else
	{
		URL = "../wps/proxy/MCPremiumQuoteWS/MCCDTPremiumQuote";
	}
	$.ajax({
		url: URL,
		//url: window.location.origin+"/wps/proxy/MCPremiumQuoteWS/MCCDTPremiumQuote",
		type: 'GET',
		contentType:"json",
		data: jsonData,
		success: function(e) {
			console.log('success ',e);
			QuoteToolAPI.redirectToResultsPage(QuoteToolAPI.formatQuotePremium(JSON.parse(e.substring(e.indexOf("{"),e.indexOf("}")+1))["basepremium"]));
		},
		error: function(e) {
			console.log('error ',e);
		},
		timeout:30000
	});
}

QuoteToolAPI.getQuotePremiumGLT = function(){
	var genderIs = "";
	if ($("#state1-mmquote").val() == "MT"){genderIs = "U"}
	else {genderIs = $("#gender-mmquote").val()};
	var  jsonData={"term":$("#term-mmquote").val(),"age":QuoteToolAPI.calculateAge(),"gender":genderIs,"health":$("#health-mmquote").val(),"tobacco":$("#tobacco-mmquote").val(),
		"coverage":$("#coverage-mmquote").val(),
		"state":$("#state1-mmquote").val(),
		"lstPnPParameters":"state,DOB,coverage,term,tobacco,health,gender,age,lStatus",
		"lStatus":"Q",
		"rating":0,
		"mcid":""
	};
	if($("#submitBtn[data-page='quotes']").length == 1)
	{
		URL = "../../../wps/proxy/MCPremiumQuoteWS/MCPremiumQuote";
	}
	else
	{
		URL = "../wps/proxy/MCPremiumQuoteWS/MCPremiumQuote";
	}
	$.ajax({
		url: URL,
		//url: window.location.origin+"/wps/proxy/MCPremiumQuoteWS/MCPremiumQuote",
		type: 'GET',
		contentType: "json",
		data: jsonData,
		success: function(e) {
			console.log('success ',e);
			QuoteToolAPI.redirectToResultsPage(QuoteToolAPI.formatQuotePremium(JSON.parse(e.substring(e.indexOf("{"),e.indexOf("}")+1))["premium"]));
		},
		error: function(e) {
			console.log('error ',e);
		},
		timeout:30000
	});
}

QuoteToolAPI.updateTermLength = function(id,page){
	var terms = null,age,termRange,len=0;
	if(page == 'home')
	{
		age = QuoteToolAPI.calculateAge();
	}
	else if(page == 'results')
	{
		age = QuoteToolAPI.calculateAgeResults();
	}
	if(age != 0 && age > 0)
	{
		if(QuoteToolAPI.quoteToolType != 'GAWLI')
		{
			if(QuoteToolAPI.selectedState == 'NY')
			{
				terms = QuoteToolAPI.termLength_NY;
			}
			else if(QuoteToolAPI.selectedState == 'WA')
			{
				terms = QuoteToolAPI.termLength_WA;
			}
			else
			{
				terms = QuoteToolAPI.termLength;
			}
			$(id).children().remove();
			$(id).append("<option value=''>Term Length</option>");
			for (var key in terms) {
				if(age<key && age>17)
				{
					//console.log('if ',key);
					termRange = terms[key]
					break;
				}
				//console.log('loop');
			}
			len = termRange.length;
			for(var i=0; i<len; i++)
			{
				$(id).append("<option value="+termRange[i]+">"+termRange[i]+" Years</option>");
			}
		}
	}
}

QuoteToolAPI.goOnBlur = function(){
	/* for LI quote */
	if(($("#insurance-type").val() != "") && ($("#state1-mmquote").val() != "")){
		$('.quoteTop select').removeAttr('disabled');
	}
	if(QuoteToolAPI.selectedInsurance != 'term'){
		$(".quoteBottom,.error_age_coverage,.error_state_coverage").hide();
		$("#month-mmquote,#day-mmquote,#year-mmquote,#gender-mmquote,#term-mmquote,#tobacco-mmquote,#health-mmquote").val("");
		$(".online_cta_check").attr("checked", true);
		$(".rep_cta_check").removeAttr("checked");
		$(".online_cta_check").prop("checked", true);
		$(".rep_cta_check").prop("checked");
		setRadioImage();
	}
}

QuoteToolAPI.formatQuotePremium = function(premium){
	//if(premium != Math.round(premium)){
	var dec = parseFloat(Math.round(premium*100)/100).toFixed(2);
	return dec;
	/*} else{
	 return premium;
	 }*/
}