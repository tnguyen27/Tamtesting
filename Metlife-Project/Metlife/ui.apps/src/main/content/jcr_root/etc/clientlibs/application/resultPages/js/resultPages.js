QuoteToolAPI.loadEventListenersForResults = function() {
	$("#edit-month-mmquote").on("change", function () {
		QuoteToolAPI.populateDaysDropDown("#edit-");
	});
	$("#edit-year-mmquote").on("change", function () {
		QuoteToolAPI.populateDaysDropDown("#edit-");
	});
	$("#edit-coverage-mmquote").change(function(){
		var val = $(this).val();
		if(QuoteToolAPI.quoteToolType != 'GAWLI')
		{
			if(parseInt(val) < 50001 && (QuoteToolAPI.calculateAgeResults() == 0 || (QuoteToolAPI.calculateAgeResults() > 17 && QuoteToolAPI.calculateAgeResults() < 71)) && QuoteToolAPI.sitStates.indexOf($("#edit-state1-mmquote").val()) != -1){
				$(".term_length,.tobacco_nicotine_users,.your_health,.apply_option,.apply_type").addClass('hidden');
				$("#edit-term-mmquote,#edit-tobacco-mmquote,#edit-health-mmquote").removeAttr('data-validation').removeClass('errorField');
				QuoteToolAPI.quoteToolType = 'SIT';
				//less margin
			}
			else{
				$(".term_length,.tobacco_nicotine_users,.your_health,.apply_option,.apply_type").removeClass('hidden');
				$("#edit-term-mmquote,#edit-tobacco-mmquote,#edit-health-mmquote").attr('data-validation',true);
				QuoteToolAPI.verifyMLTorGLT();
			}
		}
	});
	$("input[name='applyType']").change(function(){
		if($(this).val() ==0)
		{
			QuoteToolAPI.quoteToolType = 'MLT';
		}
		else
		{
			QuoteToolAPI.quoteToolType = 'GLT';
		}
	});
	$("#edit-state1-mmquote").change(function(){
		QuoteToolAPI.selectedState = $(this).val();
		if(QuoteToolAPI.quoteToolType != 'GAWLI')
		{
			/*if(QuoteToolAPI.sitStates.indexOf(QuoteToolAPI.selectedState) != -1)
			 {
			 if(QuoteToolAPI.calculateAgeResults() < 71 && $("#edit-coverage-mmquote").val()<50001)
			 {
			 $(".term_length,.tobacco_nicotine_users,.your_health,.apply_option,.apply_type").addClass('hidden');
			 $("#edit-term-mmquote,#edit-tobacco-mmquote,#edit-health-mmquote").removeAttr('data-validation');
			 QuoteToolAPI.quoteToolType = 'SIT';
			 }
			 else
			 {
			 QuoteToolAPI.verifyMLTorGLT();
			 }
			 $(".error_state_coverage").hide();
			 }
			 else if
			 {
			 $(".error_state_coverage").show();
			 }*/
			if(QuoteToolAPI.quoteToolType == 'SIT')
			{
				if(QuoteToolAPI.sitStates.indexOf(QuoteToolAPI.selectedState) == -1)
				{
					//$(".term_length,.tobacco_nicotine_users,.your_health,.apply_option,.apply_type").addClass('hidden');
					//$("#edit-term-mmquote,#edit-tobacco-mmquote,#edit-health-mmquote").removeAttr('data-validation');
					QuoteToolAPI.verifyMLTorGLT();
				}
			}
			else
			{

			}
		}
		if(QuoteToolAPI.quoteToolStarted)
		{
			$(".quote_tool_form,.error_age_coverage").hide();
			$(".error_state_coverage,#ctaHeaderQuoteSubmit").show();
			$(".dob_cta_quote").removeClass('visible-lg');
			$(".icon_scroll_bar").css("margin-top", "235px");
		}
		if(QuoteToolAPI.quoteToolType != 'GAWLI')
		{
			QuoteToolAPI.updateCoverageAmount("#edit-coverage-mmquote");
		}
		else
		{
			var d = new Date();
			d = d.getFullYear();
			var r = QuoteToolAPI.gawliAgeCriteria[$("#edit-state1-mmquote").val()];
			if(r != 'undefined')
			{
				if(typeof (r) == "object")
				{
					QuoteToolAPI.populateYearDropDown(d-r[1],18,"#edit-year-mmquote");
				}
				else
				{
					QuoteToolAPI.populateYearDropDown(d-80,18,"#edit-year-mmquote");
				}
			}
		}
	});
}

QuoteToolAPI.splitParams = function(){
	var paramslist = window.location.href.split("?")[1],online,editFields;
	paramslist = paramslist.split("&");
	online = QuoteToolAPI.base64Decode(paramslist[0].split("=")[1]);
	editFields = QuoteToolAPI.base64Decode(paramslist[1].split("=")[1]);
	$("#QuoteValue").html(QuoteToolAPI.base64Decode(paramslist[2].split("=")[1]).replace(/(\d)(?=(\d{3})+(?!\d))/g, "$1,"));
	$("#premium").val(QuoteToolAPI.base64Decode(paramslist[2].split("=")[1]));
	if(online == "y"){
		$(".online_list,.online_form_title,#resultsBuyNow").removeClass('hidden');
	}
	else{
		$(".online_na_list,.online_na_form_title,#resultsSubmit").removeClass('hidden');
	}
	QuoteToolAPI.prefillEditQuoteFields(editFields);
}

QuoteToolAPI.base64Decode = function(g) {
	g = g.replace(/[^a-z0-9\+\/=]/ig, "");
	if (typeof(atob) == "function") {
		return atob(g)
	}
	var h = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
	var m, l, k;
	var d, c, b, a;
	var o = new Array();
	var e = 0;
	while ((g.length % 4) != 0) {
		g += "="
	}
	for (var f = 0; f < g.length; f += 4) {
		d = h.indexOf(g.charAt(f));
		c = h.indexOf(g.charAt(f + 1));
		b = h.indexOf(g.charAt(f + 2));
		a = h.indexOf(g.charAt(f + 3));
		m = (d << 2) | (c >> 4);
		l = ((c & 15) << 4) | (b >> 2);
		k = ((b & 3) << 6) | a;
		o[e++] = String.fromCharCode(m);
		if (b != 64) {
			o[e++] = String.fromCharCode(l)
		}
		if (a != 64) {
			o[e++] = String.fromCharCode(k)
		}
	}
	return o.join("")
}

QuoteToolAPI.prefillEditQuoteFields  = function(preFillValues){
	var fieldMapping = {
		s : "edit-state1-mmquote",
		d : "edit-day-mmquote",
		m : "edit-month-mmquote",
		y : "edit-year-mmquote",
		g : "edit-gender-mmquote",
		c : "edit-coverage-mmquote",
		t : "edit-term-mmquote",
		n :	"edit-tobacco-mmquote",
		h : "edit-health-mmquote"
	}
	var hiddenFieldMapping = {
		g : "gender",
		h : "healthClass",
		t : "term",
		c : "coverage",
		n : "tobacco",
		s : "state"
	}
	preFillValues = preFillValues.split(",");
	var len = preFillValues.length;
	for(var i=0;i<len;i++)	{
		var id = "#" + fieldMapping[preFillValues[i].split("=")[0]];
		var hiddenID = "#" + hiddenFieldMapping[preFillValues[i].split("=")[0]];
		var prefillValue = preFillValues[i].split("=")[1];
		$(id).val(prefillValue);
		$(hiddenID).val(prefillValue);
	}
	$("#dob").val($("#edit-month-mmquote").val()+"-"+$("#edit-day-mmquote").val()+"-"+$("#edit-year-mmquote").val());
	$("#CoverageAmt").html("$"+preFillValues[5].split("=")[1].replace(/(\d)(?=(\d{3})+(?!\d))/g, "$1,"));
	$("#smoker").val($("#tobacco").val());
	QuoteToolAPI.quoteToolType = $("#recalculateQuote").attr('data-flow');
	if(QuoteToolAPI.quoteToolType != "GAWLI" && QuoteToolAPI.quoteToolType != 'SIT')
	{
		var val = $("#edit-term-mmquote").val();
		QuoteToolAPI.updateTermLength("#edit-term-mmquote","results");
		$("#edit-term-mmquote").val(val);
		$("#TermLengthValue").html(val);
	}
	QuoteToolAPI.selectedState = $("#edit-state1-mmquote").val();
	var val = $("#edit-coverage-mmquote").val();
	QuoteToolAPI.updateCoverageAmount("#edit-coverage-mmquote");
	$("#edit-coverage-mmquote").val(val);
}

QuoteToolAPI.calculateAgeResults = function(){
	var l = 0;
	if ((document.getElementById("edit-month-mmquote").value != "") && (document.getElementById("edit-day-mmquote").value != "") && (document.getElementById("edit-year-mmquote").value != "")) {
		var b = parseInt(document.getElementById("edit-month-mmquote").value);
		var k = parseInt(document.getElementById("edit-day-mmquote").value);
		var m = parseInt(document.getElementById("edit-year-mmquote").value);
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

QuoteToolAPI.recalculateQuote = function(flow){
	if(QuoteToolAPI.quoteToolType == "GAWLI")
	{
		var age = QuoteToolAPI.calculateAgeResults();
		var range = QuoteToolAPI.gawliAgeCriteria[QuoteToolAPI.selectedState];
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
		else if(typeof(range) == "object" && (age < range[0] || age > range[1]))
		{
			$(".error_age_coverage").show();
		}
		else if(age < 45 || age > 80){
			$(".error_age_coverage").show();
		}
		else if(!QuoteToolAPI.gawliAgeError){
			$(".error_state_coverage,.error_age_coverage").hide();
			QuoteToolAPI.getQuotePremiumGAWLIResults();
		}
	}
	else if(QuoteToolAPI.quoteToolType == "SIT")
	{
		if(QuoteToolAPI.sitStates.indexOf(QuoteToolAPI.selectedState) == -1){
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
			QuoteToolAPI.getQuotePremiumSITResults();
		}
	}
	else if(QuoteToolAPI.quoteToolType == "MLT")
	{
		if(QuoteToolAPI.mltApprovedStates.indexOf(QuoteToolAPI.selectedState) == -1){
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
			QuoteToolAPI.getQuotePremiumMLTResults();
		}
	}
	else if(QuoteToolAPI.quoteToolType == "GLT")
	{
		QuoteToolAPI.getQuotePremiumGLTResults();
	}
}

QuoteToolAPI.getQuotePremiumGAWLIResults = function(){
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
	var dob = $("#edit-month-mmquote").val()+"-"+$("#edit-day-mmquote").val()+"-"+$("#edit-year-mmquote").val();
	//console.log(typeof dob);
	var reqObjParam = {"transaction":{"metaData":{},"transactionType":"diagnosticTool","entities":{"user":{"personalInfo":{"firstName":"","middleName":"",	"lastName":"","email":"","street":"","city":"","zip":"","stateDesc": $("#edit-state1-mmquote").val(),"primaryPhone":"","alternatePhone":""},"inputFields":{"gender":$("#edit-gender-mmquote").val(),"dateOfBirth":$("#edit-month-mmquote").val()+"-"+$("#edit-day-mmquote").val()+"-"+$("#edit-year-mmquote").val(),"state":$("#edit-state1-mmquote").val(),"faceAmount":$("#edit-coverage-mmquote").val(),"productType":"GIWL","termLength":"10","age":QuoteToolAPI.calculateAgeResults(),"health":"Healthy","replacement":"No","healthClass":"Standard","tobacco":"No"},"agentId":"","agentName":"","appSrc":"ML.com","campaignCode":"","channelType":"BroadMarket","cRMID":"","submittedDateTime":todaydate}}}};
	reqObjParam =JSON.stringify(reqObjParam);

	$.ajax({
		//url: "/wps/qadiagnosticToolProxy/sales/getProdRcmdAndQuoteForJSONInput?CN=USA",
		url: "/wps/diagnosticToolProxy/sales/getProdRcmdAndQuoteForJSONInput?CN=USA",
		type: 'POST',
		dataType:'json',
		data: reqObjParam,
		contentType: "application/json",
		success: function(e) {
			console.log('success ',e);
			QuoteToolAPI.redirectToResultsPageInResults(QuoteToolAPI.formatQuotePremium(e["GIWLResp"]["premium"]));
		},
		error: function(e) {
			console.log('error ',e);//	handleServiceError()
		},
		timeout:30000
	});
}

QuoteToolAPI.getQuotePremiumSITResults = function(){
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
	var dob = $("#edit-month-mmquote").val()+"-"+$("#edit-day-mmquote").val()+"-"+$("#edit-year-mmquote").val();
	var reqObjParam = {"transaction":{"metaData":{},"transactionType":"diagnosticTool","entities":{"user":{"personalInfo":{"firstName":"","middleName":"",	"lastName":"","email":"","street":"","city":"","zip":"","stateDesc": $("#edit-state1-mmquote").val(),"primaryPhone":"","alternatePhone":""},"inputFields":{"gender":$("#edit-gender-mmquote").val(),"dateOfBirth":$("#edit-month-mmquote").val()+"-"+$("#edit-day-mmquote").val()+"-"+$("#edit-year-mmquote").val(),"state":$("#edit-state1-mmquote").val(),"faceAmount":$("#edit-coverage-mmquote").val(),"productType":"SIT","termLength":"10","age":QuoteToolAPI.calculateAgeResults(),"health":"Healthy","replacement":"No","healthClass":"Standard","tobacco":"No"},"agentId":"","agentName":"","appSrc":"ML.com","campaignCode":"","channelType":"BroadMarket","cRMID":"","submittedDateTime":todaydate}}}};
	reqObjParam =JSON.stringify(reqObjParam);
	$.ajax({
		//url: "/wps/qadiagnosticToolProxy/sales/getProdRcmdAndQuoteForJSONInput?CN=USA",
		url: "/wps/diagnosticToolProxy/sales/getProdRcmdAndQuoteForJSONInput?CN=USA",
		type: 'POST',
		dataType:'json',
		data: reqObjParam,
		contentType: "application/json",
		success: function(e) {
			console.log('success ',e);
			QuoteToolAPI.redirectToResultsPageInResults(QuoteToolAPI.formatQuotePremium(e['SITResp']['WithoutRider']['monthlypremium'].split(",")[1]));
		},
		error: function(e) {
			console.log('error ',e);
		},
		timeout:30000
	});
}

QuoteToolAPI.getQuotePremiumMLTResults = function(){
	var  jsonData={"term":$("#edit-term-mmquote").val(),"age":QuoteToolAPI.calculateAgeResults(),"gender":$("#edit-gender-mmquote").val(),"health":$("#edit-health-mmquote").val(),"tobacco":$("#edit-tobacco-mmquote").val(),
		"coverage":$("#edit-coverage-mmquote").val(),
		"state":$("#edit-state1-mmquote").val(),
		"lstPnPParameters":"state,DOB,coverage,term,tobacco,health,gender,age,lStatus",
		"lStatus":"Q",
		"rating":0,
		"mcid":""
	};
	console.log(jsonData);
	$.ajax({
		url: "../../wps/proxy/MCPremiumQuoteWS/MCCDTPremiumQuote",
		//url: window.location.origin+"/wps/proxy/MCPremiumQuoteWS/MCCDTPremiumQuote",
		type: 'GET',
		contentType:"json",
		data: jsonData,
		success: function(e) {
			console.log('success ',e);
			QuoteToolAPI.redirectToResultsPageInResults(QuoteToolAPI.formatQuotePremium(JSON.parse(e.substring(e.indexOf("{"),e.indexOf("}")+1))["basepremium"]));
		},
		error: function(e) {
			console.log('error ',e);
		},
		timeout:30000
	});
}

QuoteToolAPI.getQuotePremiumGLTResults = function(){
	var genderIs = "";
	if ($("#edit-state1-mmquote").val() == "MT"){genderIs = "U"}
	else {genderIs = $("#edit-gender-mmquote").val()};
	var  jsonData={"term":$("#edit-term-mmquote").val(),"age":QuoteToolAPI.calculateAgeResults(),"gender":genderIs,"health":$("#edit-health-mmquote").val(),"tobacco":$("#edit-tobacco-mmquote").val(),
		"coverage":$("#edit-coverage-mmquote").val(),
		"state":$("#edit-state1-mmquote").val(),
		"lstPnPParameters":"state,DOB,coverage,term,tobacco,health,gender,age,lStatus",
		"lStatus":"Q",
		"rating":0,
		"mcid":""
	};
	$.ajax({
		url: "../../wps/proxy/MCPremiumQuoteWS/MCPremiumQuote",
		//url: window.location.origin+"/wps/proxy/MCPremiumQuoteWS/MCPremiumQuote",
		type: 'GET',
		contentType: "json",
		data: jsonData,
		success: function(e) {
			console.log('success ',e);
			QuoteToolAPI.redirectToResultsPageInResults(QuoteToolAPI.formatQuotePremium(JSON.parse(e.substring(e.indexOf("{"),e.indexOf("}")+1))["premium"]));
		},
		error: function(e) {
			console.log('error ',e);
		},
		timeout:30000
	});
}

QuoteToolAPI.redirectToResultsPageInResults = function(quotePremium) {
	var urlParamString = '';
	urlParamString += 's=' + $("#edit-state1-mmquote").val() + ',';
	urlParamString += 'd=' + $("#edit-day-mmquote").val() + ',';
	urlParamString += 'm=' + $("#edit-month-mmquote").val() + ',';
	urlParamString += 'y=' + $("#edit-year-mmquote").val() + ',';
	urlParamString += 'g=' + $("#edit-gender-mmquote").val() + ',';
	urlParamString += 'c=' + $("#edit-coverage-mmquote").val();
	if(QuoteToolAPI.quoteToolType == 'MLT' || QuoteToolAPI.quoteToolType == 'GLT')
	{
		urlParamString += ',t=' + $("#edit-term-mmquote").val();
		urlParamString += ',n=' + $("#edit-tobacco-mmquote").val() + ',';
		urlParamString += 'h=' + $("#edit-health-mmquote").val();
	}
	urlParamString = QuoteToolAPI.base64Encode(urlParamString);

	var x = window.location.pathname;
	var urlBase = x.substring(0, x.lastIndexOf('/')+1);
	var onlineAvailable = "n";
	if(QuoteToolAPI.quoteToolType == 'GAWLI'){
		if(QuoteToolAPI.gawliOnlineAvailableStates.indexOf(QuoteToolAPI.selectedState) != -1){
			onlineAvailable = "y";
		}
		//window.location.href = urlBase + "Other\\GAWLI Results\\guaranteed-acceptance.html?"+"ol="+QuoteToolAPI.base64Encode(onlineAvailable)+"&fv="+urlParamString+"&q="+QuoteToolAPI.base64Encode(quotePremium);
		window.location.href = urlBase + "guaranteed-acceptance.html?ol="+QuoteToolAPI.base64Encode(onlineAvailable)+"&fv="+urlParamString+"&q="+QuoteToolAPI.base64Encode(quotePremium);
	}
	else if(QuoteToolAPI.quoteToolType == 'SIT'){
		if(QuoteToolAPI.sitOnlineAvailableStates.indexOf(QuoteToolAPI.selectedState) != -1){
			onlineAvailable = "y";
		}
		window.location.href = urlBase + "simplified-issue.html?ol="+QuoteToolAPI.base64Encode(onlineAvailable)+"&fv="+urlParamString+"&q="+QuoteToolAPI.base64Encode(quotePremium);
	}
	else if(QuoteToolAPI.quoteToolType == 'MLT'){
		if(QuoteToolAPI.selectedState != 'NY'){
			onlineAvailable = "y";
		}
		window.location.href = urlBase + "term-life.html?ol="+QuoteToolAPI.base64Encode(onlineAvailable)+"&fv="+urlParamString+"&q="+QuoteToolAPI.base64Encode(quotePremium);
	}
	else if(QuoteToolAPI.quoteToolType == 'GLT'){
		window.location.href = urlBase + "guaranteed-level.html?ol="+QuoteToolAPI.base64Encode('')+"&fv="+urlParamString+"&q="+QuoteToolAPI.base64Encode(quotePremium);
	}
}

$(document).ready(function(){
	var d = new Date();
	d = d.getFullYear();
	if($("#recalculateQuote[data-flow='GAWLI']").length == 0){
		QuoteToolAPI.populateYearDropDown(d-75,18,"#edit-year-mmquote");
	}
	else{
		QuoteToolAPI.populateYearDropDown(d-80,18,"#edit-year-mmquote");
	}
	QuoteToolAPI.splitParams();
	QuoteToolAPI.quoteToolType = $("#recalculateQuote").attr('data-flow');
	QuoteToolAPI.loadEventListenersForResults();
});

$("#edit-month-mmquote,#edit-day-mmquote,#edit-year-mmquote").change(function(){
	var age = QuoteToolAPI.calculateAgeResults();
	if(QuoteToolAPI.quoteToolType == 'GAWLI' && age != 0){
		var range = QuoteToolAPI.gawliAgeCriteria[$("#edit-state1-mmquote").val()];
		if(typeof(range) == "object" && (age < range[0] || age > range[1]))
		{
			$(".error_age_coverage").show();
			QuoteToolAPI.gawliAgeError = true;
		}
		else if(age < 45 || age > 80){
			$(".error_age_coverage").show();
			QuoteToolAPI.gawliAgeError = true;
		}
		else
		{
			$(".error_age_coverage").hide();
			QuoteToolAPI.gawliAgeError = false;
		}
	}
	else {
		if(age > 70 && age < 76){
			//console.log('71-75');
			$(".term_length,.tobacco_nicotine_users,.your_health,.apply_option,.apply_type").removeClass('hidden');
			$("#edit-term-mmquote,#edit-tobacco-mmquote,#edit-health-mmquote").attr('data-validation',true);
			QuoteToolAPI.sitCompatibleAge = false;
			QuoteToolAPI.verifyMLTorGLT();
		}
		else if(age > 17 && age < 71){
			//console.log('18-70');
			/*$(".term_length,.tobacco_nicotine_users,.your_health,.apply_option,.apply_type").addClass('hidden');
			 $("#edit-term-mmquote,#edit-tobacco-mmquote,#edit-health-mmquote").removeAttr('data-validation');
			 QuoteToolAPI.sitCompatibleAge = true;
			 QuoteToolAPI.quoteToolType = 'SIT';*/
			if(QuoteToolAPI.sitStates.indexOf($("#edit-state1-mmquote").val()) != -1)
			{
				/* code added for recalculating by Anusha*/
				if($("#edit-coverage-mmquote").val() > 99999)
				{
					$(".quoteBottom").show();
					$("#edit-term-mmquote,#edit-tobacco-mmquote,#edit-health-mmquote").attr('data-validation',true);
					QuoteToolAPI.sitCompatibleAge = false;
					QuoteToolAPI.updateTermLength("#edit-term-mmquote","results");
					QuoteToolAPI.verifyMLTorGLT();
				}
				else
				{
					$(".quoteBottom").hide();
					$(".term_length,.tobacco_nicotine_users,.your_health,.apply_option,.apply_type").addClass('hidden');
					$("#edit-term-mmquote,#edit-tobacco-mmquote,#edit-health-mmquote").removeAttr('data-validation');
					QuoteToolAPI.sitCompatibleAge = true;
					QuoteToolAPI.quoteToolType = 'SIT';
				}
				/* code added for recalculating by Anusha ends here*/
			}
		}
		QuoteToolAPI.updateCoverageAmount("#edit-coverage-mmquote");
	}
	QuoteToolAPI.updateTermLength("#edit-term-mmquote","results");
});

$("#recalculateQuote").click(function(e){
	e.preventDefault();
	var flag = false;
	$("#recalculateQuote").parents("form").find("select[data-validation='true']").each(function(){
		if($(this).val() == null || $(this).val() == '')
		{
			flag = true;
			$(this).addClass("errorField");
		}
		else
		{
			$(this).removeClass("errorField");
		}
	});
	if(!flag)
		QuoteToolAPI.recalculateQuote();
});