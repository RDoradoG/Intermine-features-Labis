/*===============================================================
=				Functionallity of Visualizations				=
=																=
▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒
= Labis - IQ, USP. São Paulo									=
= @author Rodrigo Dorado						█║█║║█║█║█║║█	=
===============================================================*/

/**
 *  Class fot the pop up menu
 */
class geneAtlas{

	/**
	 * Construct class geneAtlas
	 * @param  {array} 	TypesDiccionary Types of the experiments.
	 * @param  {String} addID          The extra Sting of the id.
	 * @return {null}
	 * @author Rodrigo Dorado
	 */
	constructor(TypesDiccionary, addID) {
		this.TypesDiccionary = TypesDiccionary;
		this.addID           = addID;
		this.idCall          = '';
	}

	/**
	 * Fill the dropdown with the types of the experiments
	 * @return {null}
	 * @author Rodrigo Dorado
	 */
	fillTypeExperiment() {
		var selected = true;
		jQuery('#experimentType' + this.addID).html('');
		for (var i = 0; i < this.TypesDiccionary.length; i++) {
			fillAOption('experimentType' + this.addID, this.TypesDiccionary[i].name, selected);
			selected = false;
		}
		if (!selected) {
			this.ChangeExperimentType(this.TypesDiccionary[0].name);
		}
	}

	/**
	 * Fill the dropdown of the experiments with their names
	 * @param  {array} result The name of the experiments
	 * @return {null}
	 * @author Rodrigo Dorado
	 */
	fillExperiment(result) {
		var selected = true;
		jQuery('#experimentSelect' + this.addID).html('');
		for (var i = 0; i < result.length; i++) {
			fillAOption('experimentSelect' + this.addID, result[i]['ExperimentDescription.name'].value, selected);
			selected = false;
		}
		if (!selected) {
			this.ChangeExepreiment(result[0]['ExperimentDescription.name'].value);
		}
	}

	/**
	 * Execute the query to search the experiments with the type of the experiment
	 * @param {String} value The type of the experiments
	 * @return {null}
	 * @author Rodrigo Dorado
	 */
	ChangeExperimentType(value) {
		typeSelected = value;
		var query    = '<query model="genomic" view="ExperimentDescription.description ExperimentDescription.name" sortOrder="ExperimentDescription.description ASC" >';
		query        += '<constraint path="ExperimentDescription.expressionValue.type.name" op="=" value="' + typeSelected + '" code="A" />';
		query        += '<constraint path="ExperimentDescription.expressionValue.gene.primaryIdentifier" op="=" value="' + PrimaryIdentifier + '" code="B" />';
		query        += '</query>';
		this.APIExecuteQuery(query, 'typeExperiment');
	}

	/**
	 * Execute the query to get the expression values and the conditions with the type of the experiment and the name of the experiment
	 * @param {String} value Name of the experiment
	 * @return {null}
	 * @author Rodrigo Dorado
	 */
	ChangeExepreiment(value) {
		experiment = value;
		var query  = '<query model="genomic" view="ExpressionValues.condition.name ExpressionValues.expressionValue" sortOrder="ExpressionValues.condition.name ASC" constraintLogic="A and B and C" >';
		query      += '<constraint path="ExpressionValues.experiment.name" op="=" value="' + experiment + '" code="A" />';
		query      += '<constraint path="ExpressionValues.gene.primaryIdentifier" op="=" value="' + PrimaryIdentifier + '" code="B" />';
		query      += '<constraint path="ExpressionValues.type.name" op="=" value="' + typeSelected + '" code="C" />';
		query      += '</query>';
		this.APIExecuteQuery(query, 'Experiment');
	}

	/**
	 * Fill the dropdown of the conditions
	 * @param  {array} result The name of the conditions
	 * @return {null}
	 * @author Rodrigo Dorado
	 */
	fillData(result) {
		jQuery('#li' + this.addID).html('');
		jQuery('#ul' + this.addID).html('');
		var option = jQuery("<option></option>").attr("value", "").text("-- SELECT --");
		jQuery('#li' + this.addID).append(option);
		for(var i = 0; i < result.length; i++) {
			var value  = result[i]['ExpressionValues.condition.name'].value;
			var option = jQuery("<option></option>").attr("value", value).text(value);
			jQuery('#li' + this.addID).append(option);
		}
	}

	/**
	 * Send the query result to their functions
	 * @param  {array} 			result Result of the query
	 * @param  {array, String} 	idCall Data passed before executing the query
	 * @return {[type]}        [description]
	 */
	getResult(result, idCall){
		switch (idCall) {
			case 'typeExperiment':
				this.fillExperiment(result);
			break;

			case 'Experiment':
				this.fillData(result)
			break;
		}
	}

	/**
	 * Execute a query in the Intermine system
	 * @param {String} 			query  Query in format xml.
	 * @param {array, String} 	idCall variable to pass after the execution of query.
	 * @param {int} 			sz     Max size of the answer.
	 * @param {int} 			strt   Number of page of the answer.
	 * @author Rodrigo Dorado
	 */
	APIExecuteQuery(query, idCall, sz, strt) {
		var size     = (sz == undefined) ? 1000 : sz;
		var start    = (strt == undefined) ? 0 : strt;
		var settings = {
		  async: true,
		  url:  'service/query/results/tablerows',
		  method: 'POST',
		  headers: {
		    "Content-Type": 'application/x-www-form-urlencoded',
		    Accept: 'application/json',
		    Authorization: 'Token ' + api_key
		  },
		  data: {
		    start: start,
		    size: size,
		    query: query,
		    format: 'json'
		  }
		}
		var thisClass = this;
		jQuery.ajax(settings).done(function (response) {
		  	var result = getResultFormat(response);
		  	thisClass.getResult(result, idCall);
		});
	}
}

/**
 * Prepare data of Gene Atlas to be drawn
 * @param  {array} 	expressionValues The expression values of the gene in the experiment and type selected.
 * @param  {String} useLinearScale   If the draw will be with z-score
 * @param  {String} orderBy          Which column to order
 * @return {null}
 * @author Rodrigo Dorado
 */
function drawGeneAtlasChart(expressionValues, useLinearScale, orderBy){
	var auxExpressionValues = clone(expressionValues);
	var enrichment_data     = new google.visualization.DataTable();
	var baseUrl             = "/${WEB_PROPERTIES['webapp.path']}/report.do?id=";
	var zscored             = (useLinearScale == 'zscore');

	//Zscore
	auxExpressionValues     = getDataForvisualization(auxExpressionValues, 'ExpressionValues.expressionValue', zscored);

	enrichment_data.addColumn('string', 'Condition Name', 'condition');
	enrichment_data.addColumn('number', 'Expression Value', 'expressionValue');

	for (var i = 0; i < auxExpressionValues.length; i++) {
		var enrichment_row = [auxExpressionValues[i]['ExpressionValues.condition.name'].value, auxExpressionValues[i]['ExpressionValues.expressionValue'].value];
		enrichment_data.addRow(enrichment_row);
	}

	var data = enrichment_data;
	if (orderBy == 'name') {
		data.sort([{column: 0}]);
	} else {
		data.sort([{column: 1}]);
	}

	drawChart(data, expressionValues.length, 'geneatlas-viz', true);
}

/**
 * Get the Strign of the conditions for the query to get the expressions values
 * @param  {array} set all the conditions to get the expression values
 * @return {String}     the xml format of the conditions
 * @author Rodrigo Dorado
 */
function getConditionsStr(set) {
	var strConditions = '';
	for (var i = 0; i < set.length; i++) {
		strConditions = strConditions + '<value>' + set[i] + '</value>'
	}
	return strConditions;
}

/**
 * Get the values of the fields of the form to execute the comparation of two experiments
 * @param {array} 	firstExperiment  	set of values of the first experiment
 * @param {array} 	secondExperiment 	set of values of the second experiment
 * @param {String} 	addId           	Extra String to add to the i of the fields
 * @author Rodrigo Dorado
 */
function setValuesComparation(firstExperiment, secondExperiment, addId) {
	var typeA   = jQuery("#experimentType_first" + addId).val();
	var typeB   = jQuery("#experimentType_second" + addId).val();
	var expeA   = jQuery("#experimentSelect_first" + addId).val();
	var expeB   = jQuery("#experimentSelect_second" + addId).val();
	setComparation(firstExperiment, secondExperiment, typeA, typeB, expeA, expeB);
}

/**
 * Set the querys to get the expression values of the experiments to compare and execute the first one
 * @param {array} 	seta          set of conditions of the first experiment
 * @param {array} 	setb          set of conditions of the second experiment
 * @param {String} 	typeSelectedA Type of the first experiment
 * @param {String} 	typeSelectedB Type of the second experiment
 * @param {String} 	experimentA   Name of the first experiment
 * @param {String} 	experimentB   Name of the second experiment
 * @author Rodrigo Dorado
 */
function setComparation(seta, setb, typeSelectedA, typeSelectedB, experimentA, experimentB) {
	var strConditionsA = getConditionsStr(seta);
	var strConditionsB = getConditionsStr(setb);
	var queryA         = '<query model="genomic" view="ExpressionValues.condition.name ExpressionValues.expressionValue" sortOrder="ExpressionValues.condition.name ASC" constraintLogic="A and B and C and D" >';
	queryA             += '<constraint path="ExpressionValues.experiment.name" op="=" value="' + experimentA + '" code="A" />';
	queryA             += '<constraint path="ExpressionValues.gene.primaryIdentifier" op="=" value="' + PrimaryIdentifier + '" code="B" />';
	queryA             += '<constraint path="ExpressionValues.type.name" op="=" value="' + typeSelectedA + '" code="C" />';
	queryA             += '<constraint path="ExpressionValues.condition.name" op="ONE OF" code="D">' + strConditionsA + '</constraint>';
	queryA             += '</query>';
	var queryB         = '<query model="genomic" view="ExpressionValues.condition.name ExpressionValues.expressionValue" sortOrder="ExpressionValues.condition.name ASC" constraintLogic="A and B and C and D" >';
	queryB             += '<constraint path="ExpressionValues.experiment.name" op="=" value="' + experimentB + '" code="A" />';
	queryB             += '<constraint path="ExpressionValues.gene.primaryIdentifier" op="=" value="' + PrimaryIdentifier + '" code="B" />';
	queryB             += '<constraint path="ExpressionValues.type.name" op="=" value="' + typeSelectedB + '" code="C" />';
	queryB             += '<constraint path="ExpressionValues.condition.name" op="ONE OF" code="D">' + strConditionsB + '</constraint>';
	queryB             += '</query>';
	this.APIExecuteQuery(queryA, {id: 'Experiment_comparationA', data: {queryB: queryB, ExperimentA: experimentA, ExperimentB: experimentB}});
}

/**
 * Save both datasets to compare and execute comparation
 * @param  {array} dataB Data set of the first experiment
 * @param  {array} dataA Data set of the second experiment
 * @return {null}
 * @author Rodrigo Dorado
 */
function executeDrawComparation(dataB, dataA, ExperimentA, ExperimentB) {
	expressionValuesA      = clone(dataB);
	expressionValuesB      = clone(dataA);
	ExperimentComparisionA = clone(ExperimentA);
	ExperimentComparisionB = clone(ExperimentB);
	drawComparation(expressionValuesB, expressionValuesA, ExperimentA, ExperimentB);
}

/**
 * Prepare data of the comparation to get drawn
 * @param  {array} dataB Data set of the first experiment
 * @param  {array} dataA Data set of the second experiment
 * @return {null}
 * @author Rodrigo Dorado
 */
function drawComparation(dataB, dataA, ExperimentA, ExperimentB) {
	var enrichment_data = new google.visualization.DataTable();

	enrichment_data.addColumn('string', 'Condition Name', 'condition');
	enrichment_data.addColumn('number', ExperimentA, 'expressionValue A');
	enrichment_data.addColumn('number', ExperimentB, 'expressionValue B');

	if(dataA.length >= dataB.length) {
		bigLength = dataA.length;
	} else {
		bigLength = dataB.length;
	}

	var zscored             = (scaleComparation == 'zscore'); //Verify if the option 'zscore' was selected
	var auxExpressionValues = clone(dataA.concat(clone(dataB))); //concat data of the sets
	auxExpressionValues     = getDataForvisualization(auxExpressionValues, 'ExpressionValues.expressionValue', zscored);

	for (var i = 0; i < bigLength; i++) {
		var opt = i + 1;
		if (i < dataA.length) {
			var firstValue = auxExpressionValues[i]['ExpressionValues.expressionValue'].value;
		} else {
			var firstValue = 0;
		}
		if ((i + dataA.length) < auxExpressionValues.length) {
			var secondValue = auxExpressionValues[i + dataA.length]['ExpressionValues.expressionValue'].value;
		} else {
			var secondValue = 0;
		}
		var enrichment_row = ["Option" + opt, firstValue, secondValue];
		enrichment_data.addRow(enrichment_row);
	}

	var data = enrichment_data;
	switch(orderByComparation) {
		case 'name':
			data.sort([{column: 0}]);
		break;

		case 'scoreA':
			data.sort([{column: 1}]);
		break;

		case 'scoreB':
			data.sort([{column: 2}]);
		break;
	}

	drawChart(data, bigLength, 'Chart-comparation', false);
}

/**
 * Execute Google visualitation function to draw the charts
 * @param  {array} 		data     Data to draw
 * @param  {int} 		lngth    Length of the data
 * @param  {String} 	id       The id of the div to be drawn
 * @param  {boolean} 	addEvent If the draw will have a listener to execute
 * @return {null}
 * @author Rodrigo Dorado
 */
function drawChart(data, lngth, id, addEvent) {
	var useLogScaleOption = false;
	var haxis             = "Expression Values";
	var maxH              = null;
	var baseLine          = null;
	var height            = 80 + (18 * lngth);
	var viz               = new google.visualization.BarChart(document.getElementById(id));
 	viz.draw(
  		data,
      	{
	      	isStacked: false,
	      	chartArea: {width: '50%'},
			colors: ['#314bbc','#8931bc','#bc3162'],
			title: "Gene Atlas Expression",
			width: 920,
			height: height,
			legendTextStyle: {fontSize: 10},
			vAxis: {title: "Condition Name", textStyle: {fontSize: 11}},
			hAxis: {title: haxis, logScale: useLogScaleOption, maxValue: maxH, baseline: baseLine, baselineColor: '#8931bc'},
      	}
    );

  	if(addEvent) {
		google.visualization.events.addListener(viz, 'select', function() {
			console.log(viz.getSelection());
		});
  	}
};

/**
 * Fill the dropdown of the type of the experiments
 * @return {null}
 * @author Rodrigo Dorado
 */
function fillTypeExperiment() {
	var selected = true;
	for (var i = 0; i < TypesDiccionary.length; i++) {
		fillAOption('experimentType', TypesDiccionary[i].name, selected);
		selected = false;
	}
	if (!selected) {
		ChangeExperimentType(TypesDiccionary[0].name);
	}
}

/**
 * Fill the dropdown of experiments with their names
 * @param  {array} result The experiments to show
 * @return {null}
 * @author Rodrigo Dorado
 */
function fillExperiment(result) {
	var selected = true;
	jQuery('#experimentSelect').html('');
	for (var i = 0; i < result.length; i++) {
		fillAOption('experimentSelect', result[i]['ExperimentDescription.name'].value, selected);
		selected = false;
	}
	if (!selected) {
		ChangeExepreiment(result[0]['ExperimentDescription.name'].value);
	}
}

/**
 * Add an option to a dropdown
 * @param  {String} 	id       I of the dropdown
 * @param  {String} 	value    Value and label of the option
 * @param  {boolean} 	selected If the option is selected
 * @return {null}
 * @author Rodrigo Dorado
 */
function fillAOption(id, value, selected) {
	var option = jQuery("<option></option>").attr("value", value).text(value);
	if (selected) {
		option.attr("selected", "selected");
	}
	jQuery('#' + id).append(option);
}

/**
 * Execute the query to search experiments when the dropdown of type is changed
 * @param {String} value Type of the experiments
 * @return {null}
 * @author Rodrigo Dorado
 */
function ChangeExperimentType(value) {
	typeSelected = value;
	var query    = '<query model="genomic" view="ExperimentDescription.description ExperimentDescription.name" sortOrder="ExperimentDescription.description ASC" >';
	query        += '<constraint path="ExperimentDescription.expressionValue.type.name" op="=" value="' + typeSelected + '" code="A" />';
	query        += '<constraint path="ExperimentDescription.expressionValue.gene.primaryIdentifier" op="=" value="' + PrimaryIdentifier + '" code="B" />';
	query        += '</query>';
	APIExecuteQuery(query, {id: 'typeExperiment'});
}

/**
 * Execute the query to search the expression values of an experiment
 * @param {String} value The name of the experiment selected
 * @return {null}
 * @author Rodrigo Dorado
 */
function ChangeExepreiment(value) {
	experiment = value;
	var query  = '<query model="genomic" view="ExpressionValues.condition.name ExpressionValues.expressionValue" sortOrder="ExpressionValues.condition.name ASC" constraintLogic="A and B and C" >';
	query      += '<constraint path="ExpressionValues.experiment.name" op="=" value="' + experiment + '" code="A" />';
	query      += '<constraint path="ExpressionValues.gene.primaryIdentifier" op="=" value="' + PrimaryIdentifier + '" code="B" />';
	query      += '<constraint path="ExpressionValues.type.name" op="=" value="' + typeSelected + '" code="C" />';
	query      += '</query>';
	APIExecuteQuery(query, {id: 'Experiment'});
}

/**
 * Execute drawGeneAtlasChart
 * @return {null}
 * @author Rodrigo Dorado
 */
function executeDraw() {
	drawGeneAtlasChart(expressionValues, useLinearScale, orderBySignal)
}

/**
 * Executed function after a execution of the a query
 * @param  {array} result Result of the query
 * @param  {array} idCall Data send before the query execution
 * @return {null}
 * @author Rodrigo Dorado
 */
function getResult(result, idCall){
	switch (idCall.id) {
		case 'typeExperiment':
			fillExperiment(result);
		break;

		case 'Experiment':
			expressionValues = result;
			executeDraw();
		break;

		case 'Experiment_comparationA':
			this.APIExecuteQuery(idCall.data.queryB, {id: 'Experiment_comparationB', data: {resultA: result, ExperimentA: idCall.data.ExperimentA, ExperimentB: idCall.data.ExperimentB}});
		break;

		case 'Experiment_comparationB':
			switch (optionSelectedButton) {
				case 'compare': executeDrawComparation(result, idCall.data.resultA, idCall.data.ExperimentA, idCall.data.ExperimentB); break;
				case 'scatter': setScatterPlot(result, idCall.data.resultA, idCall.data); break;
			}
		break;
	}
}

/**
 * If the zscore was selected, get the data with zscores of the expression values
 * @param  {array} 		arrayValues Expression values
 * @param  {String} 	key         Key in arrayValues where the expression values are
 * @param  {boolean} 	zscored     If zscore was selected
 * @return {null}
 * @author Rodrigo Dorado
 */
function getDataForvisualization(arrayValues, key, zscored) {
	if (zscored) {
		var mean      = getMean(arrayValues, key);
		var Deviation = getDeviation(arrayValues, key, mean);
		arrayValues   = setZScores(arrayValues, key, mean, Deviation);
	}
	return arrayValues;
}

/**
 * Get the mean of a set of data
 * @param  {array} 	arrayData Set of data
 * @param  {String} key       Key of the set of data inside arrayData
 * @return {double}           Mean of the set of data
 * @author Rodrigo Dorado
 */
function getMean(arrayData, key) {
	var sum = 0;
	for (var i = 0; i < arrayData.length; i++) {
		sum += arrayData[i][key].value;
	}
	return sum / arrayData.length;
}

/**
 * Get the standar derivation of a set of data
 * @param  {array} 	arrayData The set of data
 * @param  {String} key       The key of the set of data inside arrayData
 * @param  {double} Mean      The mean of the set of data
 * @return {double}           The standard derivation
 * @author Rodrigo Dorado
 */
function getDeviation(arrayData, key, Mean) {
	var sum = 0;
	for (var i = 0; i < arrayData.length; i++) {
		aux = (arrayData[i][key].value - Mean);
		sum = sum + (aux * aux);
	}
	return Math.sqrt(sum / arrayData.length);
}

/**
 * Set the zscores to a set of data
 * @param {array} arrayData The set of data
 * @param {String} key       The key of the set of data inside arrayData
 * @param {double} Mean      The mena of the set of data
 * @param {double} Deviation The standard derivation of the set of data
 * @return {array} 			 The set of data with the zscores of the set
 * @author Rodrigo Dorado
 */
function setZScores(arrayData, key, Mean, Deviation) {
	for (var i = 0; i < arrayData.length; i++) {
		arrayData[i][key].value = getAZSCore(arrayData[i][key].value, Mean, Deviation);
	}
	return arrayData;
}

/**
 * Get the zscore of a value
 * @param  {double} data      Original value
 * @param  {double} Mean      The mean of the set of data
 * @param  {double} Deviation The standard derivation of the set of data
 * @return {double}           The zscore of the value
 * @author Rodrigo Dorado
 */
function getAZSCore(data, Mean, Deviation) {
	return (data - Mean) / Deviation;
}

/**
 * Get all the data in the modal
 * @param  {Strng} addId Extra string of the id
 * @return {null}
 * @author Rodrigo Dorado
 */
function getModalData(addId) {
	setDataToClass("_first" + addId);
	setDataToClass("_second" + addId);
}

/**
 * Create geneAtlas class and execute it
 * @param {String} opt 	Id of the class
 * @return {null}
 * @author Rodrigo Dorado
 */
function setDataToClass(opt) {
	let gAtlas = new geneAtlas(TypesDiccionary, opt);
	gAtlas.fillTypeExperiment();
	/*----------  Trigger when the dropdown of type of experiments changes  ----------*/
	jQuery('#experimentType' + opt).change(function() {
		gAtlas.ChangeExperimentType(this.value);
	});
	/*----------  Trigger when the dropdown of experiments changes  ----------*/
	jQuery('#experimentSelect' + opt).change(function() {
		gAtlas.ChangeExepreiment(this.value);
	});
}

/**
 * Get all the items in the list
 * @param  {String} id Id of the list
 * @return {array}	   All the items in the list
 * @author Rodrigo Dorado
 */
function getListData(id) {
	var result = [];
	jQuery('#ul_' + id + ' li a').each(function() {
		result.push(jQuery(this).text())
  	});
  	return result;
}

/**
 * Prepare the datato draw th scatter plot
 * @param 	{array} seta Set of data of the first experiment
 * @param 	{array} setb Set of data of the second experiment
 * @return 	{null}
 * @author Rodrigo Dorado
 */
function setScatterPlot(seta, setb, experimentsNames) {
	/*----------  Get data of the form  ----------*/
	var experimentType_first    = jQuery('#experimentType_first_scatter').val();
	var experimentType_second   = jQuery('#experimentType_second_scatter').val();
	var experimentSelect_first  = jQuery('#experimentSelect_first_scatter').val();
	var experimentSelect_second = jQuery('#experimentSelect_second_scatter').val();
	/*----------  Prepare labes  ----------*/
	var firtsLabel              = experimentSelect_first + ' (' + experimentType_first +')';
	var secondLabel             = experimentSelect_second + ' (' + experimentType_second +')';
	var data                    = new google.visualization.DataTable();

	data.addColumn('number', 'Expression Value A', 'expressionValueA');
	var first_array = [];
	first_array.push(null);
	for (var i = 0; i < seta.length; i++) {
		data.addColumn('number', seta[i]['ExpressionValues.condition.name'].value + ' - ' + setb[i]['ExpressionValues.condition.name'].value);
		first_array.push(null);
	}

	var allDataScatter = [];
	for (var i = 0; i < seta.length; i++) {
		var this_array    = clone(first_array)
		this_array[0]     = seta[i]['ExpressionValues.expressionValue'].value;
		this_array[i + 1] = setb[i]['ExpressionValues.expressionValue'].value
		allDataScatter.push(this_array);
	}

	data.addRows(allDataScatter);

	var options = {
		title: 'Scatter Plot',
		chartArea: {width: '50%'},
		hAxis: {title: firtsLabel},
		vAxis: {title: secondLabel}
	};

	chartScatter = new google.visualization.ScatterChart(document.getElementById('Chart-scatterplot'));
	chartScatter.draw(data, options);
}

/**
 * Set the value of the dropdown in the conditions selected
 * @param 	{int} i position in the condition array
 * @return 	{null}
 * @author Rodrigo Dorado
 */
function setValuePairConditios(i) {
	jQuery('#pairConditios option:eq(' + i + ')').prop('selected', true)
}

/**
 * Get the position of the condition in the poiont selected
 * @param  {String} opt Point selected
 * @return {int}	    Position in the conditions
 * @author Rodrigo Dorado
 */
function getIndexValueAllDataScatter(opt) {
	for (var i = 0; i < allConditions.length; i++) {
		var label = allConditions[i][0] + ' - ' + allConditions[i][1];
		if (label == opt) {
			return i;
		}
	}
	return -1
}

/*----------  Set css values to the HTML elements  ----------*/
jQuery('#ComparationChart').css('display', 'none');
jQuery('#ScattterChart').css('display', 'none');
jQuery('.sortable').sortable();
jQuery('.sortable').disableSelection();
jQuery('[data-toggle="tooltip"]').tooltip();

/*----------  Set the trigger to the radio buttons of the order by option  ----------*/
jQuery("input[name='orderExprCompare']").change(function() {
	orderByComparation = jQuery(this).val();
	drawComparation(expressionValuesB, expressionValuesA, ExperimentComparisionA, ExperimentComparisionB);
});

jQuery("input[name='orderExpr']").change(function() {
	orderBySignal  = jQuery(this).val();
	executeDraw();
});

/*----------  Set the trigger of the radio buttons of the scale option  ----------*/
jQuery("input[name='scaleCompare']").change(function() {
	scaleComparation =jQuery(this).val();
	drawComparation(expressionValuesB, expressionValuesA, ExperimentComparisionA, ExperimentComparisionB);
});

jQuery("input[name='scale']").change(function() {
	useLinearScale = jQuery(this).val();
	executeDraw();
});

/*----------  Set trigger of the dropdown of type of experiments  ----------*/
jQuery('#experimentType').change(function() {
	ChangeExperimentType(this.value);
});

/*----------  Set trigger of the dropdown of experiments  ----------*/
jQuery('#experimentSelect').change(function() {
	ChangeExepreiment(this.value);
});

/*----------  Set trigger of dropdown of the list of conditions  ----------*/
jQuery('.items').change(function() {
	if (this.value != '') {
		var li   = jQuery("<li></li>").attr("class", "ui-state-default textOverflow");
		var a    = jQuery("<a></a>").attr("href", "#").attr("data-toggle", "tooltip").attr("class", "textOverflowLi").attr("title", this.value).text(this.value);
		var span = jQuery("<span></span>").attr("class", "close").text("×");
		li.append(a);
		li.append(span);
		/*----------  Verify the list  ----------*/
		switch(this.id) {
			case 'li_first': jQuery("#ul_first").append(li); break;
			case 'li_second': jQuery("#ul_second").append(li); break;
			case 'li_first_scatter': jQuery("#ul_first_scatter").append(li); break;
			case 'li_second_scatter': jQuery("#ul_second_scatter").append(li); break;
		}
	  	this.value = ""
	}
	/*----------  Set the click function of the remove span  ----------*/
	jQuery('.close').click(function() {
		this.parentElement.remove();
	});
});

/*----------  Set the click function to the compare button  ----------*/
jQuery("#compareButton").click(function() {
	getModalData('');
	//openModal('scatterplot');
});

/*----------  Set the click function to the scatter plot button  ----------*/
jQuery("#scatterButton").click(function() {
	getModalData('_scatter');
	//openModal('scatterplot');
});

/*----------  Set the click function to the execute comparation button  ----------*/
jQuery('#executeComparation').click(function() {
	firstExperiment  = getListData('first');
	secondExperiment = getListData('second');
	if(firstExperiment.length < 1 || secondExperiment.length < 1){
		jQuery('#alertFootModal').css("display", "inline-block");
		setTimeout(function(){
			jQuery('#alertFootModal').css("display", "none");
		}, 5000);
	}else{
		jQuery("#ComparationChart").css("display", "block");
		closeModal('compare');
		optionSelectedButton = 'compare';
		setValuesComparation(firstExperiment, secondExperiment, '');
	}
});

/*----------  Set the trigger to the dropdown of the pair of conditions in the scatter plot  ----------*/
jQuery('#pairConditios').change(function() {
	var i = getIndexValueAllDataScatter(this.value);
	if (i >= 0) {
		chartScatter.setSelection([{row: i, column: 1}]);
	} else {
		chartScatter.setSelection([]);
	}
});

/*----------  Set the click function to the compare button  ----------*/
jQuery('#compareButton').click(function() {
	openModal('compare');
});

/*----------  Set the click function to the scatter plot button  ----------*/
jQuery('#scatterButton').click(function() {
	openModal('scatterplot');
});

/*----------  Set the click function to the execute scatter plot button  ----------*/
jQuery('#executeScatterPlot').click(function() {
	firstExperiment  = getListData('first_scatter');
	secondExperiment = getListData('second_scatter');
	if(firstExperiment.length < 1 || secondExperiment.length < 1 || firstExperiment.length != secondExperiment.length){
		jQuery('#alertFootModal_scatter').css("display", "inline-block");
		setTimeout(function(){
			jQuery('#alertFootModal_scatter').css("display", "none");
		}, 5000);
	}else{
		closeModal('scatterplot');
		jQuery("#ScattterChart").css("display", "block");
		optionSelectedButton = 'scatter';
		setValuesComparation(firstExperiment, secondExperiment, '_scatter');
	}
});

/*----------  Load the google library of visualization and start the script  ----------*/
google.load("visualization", "1", {"packages": ["corechart"], "callback": fillTypeExperiment});