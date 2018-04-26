// Author Rodrigo Dorado
function drawGeneAtlasChart(expressionValues, useLinearScale, orderBy) {
	var auxExpressionValues = clone(expressionValues);
	var enrichment_data     = new google.visualization.DataTable();

  enrichment_data.addColumn('string', 'Condition Name', 'condition');
  enrichment_data.addColumn('number', 'Expression Value', 'expressionValue');

   var baseUrl = "/${WEB_PROPERTIES['webapp.path']}/report.do?id=";

   var zscored = (useLinearScale == 'zscore');

   //Zscore
   auxExpressionValues = getDataForvisualization(auxExpressionValues, 'ExpressionValues.expressionValue', zscored);

	for (var i = 0; i < auxExpressionValues.length; i++) {
		var enrichment_row = [auxExpressionValues[i]['ExpressionValues.condition'].value, auxExpressionValues[i]['ExpressionValues.expressionValue'].value];
		enrichment_data.addRow(enrichment_row);
	}

  var data = enrichment_data;
  if (orderBy == 'name') {
    data.sort([{column: 0}]);
  } else {
  	data.sort([{column: 1}]);
  }

	var useLogScaleOption = false;
	var haxis             = "Expression Values";
	var maxH              = null;
	var baseLine          = null;
	var height            = 80 + (18 * auxExpressionValues.length);
	var viz               = new google.visualization.BarChart(document.getElementById('flyatlas-viz'));

  viz.draw(
  	data,
      {
      	isStacked: true,
				colors: ['#314bbc','#8931bc','#bc3162'],
				title: "Gene Atlas Expression",
				width: 920, height: height,
				legendTextStyle: {fontSize: 10},
				vAxis: {title: "Condition Name", textStyle: {fontSize: 11}},
				hAxis: {title: haxis, logScale: useLogScaleOption, maxValue: maxH, baseline: baseLine, baselineColor: '#8931bc'},
      }
    );
  
	google.visualization.events.addListener(viz, 'select', function() {
		console.log(viz.getSelection());
		/*var selection = viz.getSelection();
		for (var i = 0; i < selection.length; i++) {
			var item = selection[i];
			if (item.row != null && item.column != null) {
				// it is a cell
				var objectId = objectIds[item.row];
				//window.location.assign(baseUrl + objectId);
			}
		}*/
	});
};


google.load("visualization", "1", {"packages": ["corechart"], "callback": fillTypeExperiment});

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

function filTypeExperiment(result) {
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

function fillAOption(id, value, selected) {
	var option = jQuery("<option></option>").attr("value", value).text(value);
	if (selected) {
		option.attr("selected", "selected");
	}
	jQuery('#' + id).append(option);
}

function ChangeExperimentType(value) {
	typeSelected = value;
	var query    = '<query model="genomic" view="ExperimentDescription.description ExperimentDescription.name" sortOrder="ExperimentDescription.description ASC" ><constraint path="ExperimentDescription.ExpressionValue.type.name" op="=" value="' + typeSelected + '" code="A" /><constraint path="ExperimentDescription.ExpressionValue.gene.primaryIdentifier" op="=" value="' + PrimaryIdentifier + '" code="B" /></query>';
	APIExecuteQuery(query, 'typeExperiment');
}

function ChangeExepreiment(value) {
	experiment = value;
	var query  = '<query model="genomic" view="ExpressionValues.condition ExpressionValues.expressionValue" sortOrder="ExpressionValues.condition ASC" constraintLogic="A and B and C" ><constraint path="ExpressionValues.experiment.name" op="=" value="' + experiment + '" code="A" /><constraint path="ExpressionValues.gene.primaryIdentifier" op="=" value="' + PrimaryIdentifier + '" code="B" /><constraint path="ExpressionValues.type.name" op="=" value="' + typeSelected + '" code="C" /></query>';
	APIExecuteQuery(query, 'Experiment');
}

jQuery('#experimentType').change(function() {
	ChangeExperimentType(this.value);
});

jQuery('#experimentSelect').change(function() {
	ChangeExepreiment(this.value);
});

jQuery("input[name='scale']").change(function() {
	useLinearScale = jQuery(this).val();
	executeDraw();
});

jQuery("input[name='orderExpr']").change(function() {
	orderBySignal  = jQuery(this).val();
	executeDraw();
});

function executeDraw() {
	drawGeneAtlasChart(expressionValues, useLinearScale, orderBySignal)
}

function getResult(result, idCall){
	switch (idCall) {
		case 'typeExperiment':
			filTypeExperiment(result);
		break;

		case 'Experiment':
			expressionValues = result;
			executeDraw();
		break;
	}
}

function getDataForvisualization(arrayValues, key, zscored) {
	if (zscored) {
		var mean      = getMean(arrayValues, key);
		var Deviation = getDeviation(arrayValues, key, mean);
		arrayValues   = setZScores(arrayValues, key, mean, Deviation);
	}
	return arrayValues;
}

function getMean(arrayData, key) {
	var sum = 0;
	for (var i = 0; i < arrayData.length; i++) {
		sum += arrayData[i][key].value;
	}
	return sum / arrayData.length;
}

function getDeviation(arrayData, key, Mean) {
	var sum = 0;
	for (var i = 0; i < arrayData.length; i++) {
		aux = (arrayData[i][key].value - Mean);
		sum = sum + (aux * aux);
	}
	return Math.sqrt(sum / arrayData.length);
}

function setZScores(arrayData, key, Mean, Deviation) {
	for (var i = 0; i < arrayData.length; i++) {
		arrayData[i][key].value = getAZSCore(arrayData[i][key].value, Mean, Deviation);
	}
	return arrayData;
}

function getAZSCore(data, Mean, Deviation) {
	return (data - Mean) / Deviation;
}