/*===============================================================
=				Functionallity of Scatterplot					=
=																=
▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒
= Labis - IQ, USP. São Paulo									=
= @author Rodrigo Dorado						█║█║║█║█║█║║█	=
===============================================================*/

var experimentSelectedScatterplotGenes = {};
var typeSelectedScatterplotGenes       = {};
var conditionScatterplotGenes          = {};
var expressionsScatterplotGenes        = {};
var typesId                            = ['A', 'B'];

jQuery("#scatterPlotHideShow").click(function () {
	if(jQuery("#scatterplotGenesContainer").is(":hidden")) {
		jQuery("#os").attr("src", "images/disclosed.gif");
	} else {
		jQuery("#os").attr("src", "images/undisclosed.gif");
	}
	jQuery("#scatterplotGenesContainer").toggle("slow");
});

setInit();
main();

function fillTypeExperimentScartterPlotGenes(type) {
	var selected_type = '';
	for (var i = 0; i < types.length; i++) {
		if (types[i].name == defaultValues.type) {
			fillAOption('experimentType_scatterplotGenes_' + type, types[i].name, true);
			selected_type = types[i].name;
		} else {
			fillAOption('experimentType_scatterplotGenes_' + type, types[i].name, false);
		}
	}
	if (selected_type != '') {
		ChangeExperimentTypeScatterPlotGenes(selected_type, type);
	}
}

function fillTypeExperimentScatterplotGenes(result, type) {
	var selected_experiment = '';
	jQuery('#experimentSelect_scatterplotGenes_' + type).html('');
	for (var i = 0; i < result.length; i++) {
		if (result[i]['ExperimentDescription.name'].value == defaultValues.experiment) {
			fillAOption('experimentSelect_scatterplotGenes_' + type, result[i]['ExperimentDescription.name'].value, true);
			selected_experiment = result[i]['ExperimentDescription.name'].value;
		} else {
			fillAOption('experimentSelect_scatterplotGenes_' + type, result[i]['ExperimentDescription.name'].value, false);
		}
	}
	if (selected_experiment != '') {
		ChangeExperimentScatterPlotGenes(selected_experiment, type);
	} else {
		ChangeExperimentScatterPlotGenes(result[0]['ExperimentDescription.name'].value, type);
	}
}

function fillConditionScatterplotGenes(result, type) {
	var selected_condition = '';
	jQuery('#conditionSelect_scatterplotGenes_' + type).html('');
	for (var i = 0; i < result.length; i++) {
		if (result[i]['ExperimentConditions.name'].value == defaultValues.experiment) {
			fillAOption('conditionSelect_scatterplotGenes_' + type, result[i]['ExperimentConditions.name'].value, true);
			selected_condition = result[i]['ExperimentConditions.name'].value;
		} else {
			fillAOption('conditionSelect_scatterplotGenes_' + type, result[i]['ExperimentConditions.name'].value, false);
		}
	}
	if (selected_condition != '') {
		ChangeConditionScatterplotGenes(selected_condition, type);
	} else {
		ChangeConditionScatterplotGenes(result[0]['ExperimentConditions.name'].value, type);
	}
}

function setExpressions(result, type) {
	expressionsScatterplotGenes[type] = result;
	if (main()) {
		setArrayOfExpressionValues();
	}
}

function ChangeConditionScatterplotGenes(value, type) {
	conditionScatterplotGenes[type] = value;
	var query                       = '<query model="genomic" view="ExpressionValues.expressionValue ExpressionValues.gene.primaryIdentifier" sortOrder="ExpressionValues.expressionValue ASC" constraintLogic="A and B and C and D" >';
	query                           += '<constraint path="ExpressionValues.condition.name" op="=" value="' + conditionScatterplotGenes[type] + '" code="A" />';
	query                           += '<constraint path="ExpressionValues.experiment.name" op="=" value="' + experimentSelectedScatterplotGenes[type] + '" code="B" />';
	query                           += '<constraint path="ExpressionValues.gene" op="IN" value="' + bagName + '" code="C" />';
	query                           += '<constraint path="ExpressionValues.type.name" op="=" value="' + typeSelectedScatterplotGenes[type] + '" code="D" />';
	query                           += '</query>';
	APIExecuteQuery(query, {key: 'scatterplotGenes', type: type}, getResultScatterplotGenes);
}

function ChangeExperimentScatterPlotGenes(value, type) {
	experimentSelectedScatterplotGenes[type] = value;
	var query                                ='<query model="genomic" view="ExperimentConditions.name" sortOrder="ExperimentConditions.source ASC" constraintLogic="A and B and C" >';
	query                                    += '<constraint path="ExperimentConditions.expressionValue.experiment.name" op="=" value="' + experimentSelectedScatterplotGenes[type] + '" code="A" />';
	query                                    += '<constraint path="ExperimentConditions.expressionValue.type.name" op="=" value="' + typeSelectedScatterplotGenes[type] + '" code="B" />'
	query                                    += '<constraint path="ExperimentConditions.expressionValue.gene" op="IN" value="' + bagName + '" code="C" />';
	query                                    += '</query>';
	APIExecuteQuery(query, {key: 'expreimentSelect', type: type}, getResultScatterplotGenes);
}

function ChangeExperimentTypeScatterPlotGenes(value, type) {
	typeSelectedScatterplotGenes[type] = value;
	var query                          ='<query model="genomic" view="ExperimentDescription.description ExperimentDescription.name" sortOrder="ExperimentDescription.description ASC" constraintLogic="A and B" >';
	query                              += '<constraint path="ExperimentDescription.expressionValue.type.name" op="=" value="' + typeSelectedScatterplotGenes[type] + '" code="A" />';
	query                              += '<constraint path="ExperimentDescription.expressionValue.gene" op="IN" value="' + bagName + '" code="B" />';
	query                              += '</query>';
	APIExecuteQuery(query, {key: 'typeExperiment', type: type}, getResultScatterplotGenes);
}

function getResultScatterplotGenes(result, idFunction) {
	switch(idFunction.key) {
		case 'typeExperiment':
			fillTypeExperimentScatterplotGenes(result, idFunction.type);
		break;

		case 'expreimentSelect':
			fillConditionScatterplotGenes(result, idFunction.type);
		break;

		case 'scatterplotGenes':
			setExpressions(result,  idFunction.type);
		break;
	}
}

function main() {
	var pos = null;
	for (var i = 0; i < typesId.length; i++) {
		if (experimentSelectedScatterplotGenes[typesId[i]] == '') {
			pos = i;
			break;
		}
	}
	if (pos != null) {
		fillTypeExperimentScartterPlotGenes(typesId[pos]);
		return false;
	}
	return true;
}

function setInit() {
	jQuery('#experimentType_scatterplotGenes_' + 'A').change(function() {
		ChangeExperimentTypeScatterPlotGenes(this.value, 'A');
	});

	jQuery('#experimentSelect_scatterplotGenes_' + 'A').change(function() {
		ChangeExperimentScatterPlotGenes(this.value, 'A');
	});

	jQuery('#conditionSelect_scatterplotGenes_' + 'A').change(function() {
		ChangeConditionScatterplotGenes(this.value, 'A');
	});

	jQuery('#experimentType_scatterplotGenes_' + 'B').change(function() {
		ChangeExperimentTypeScatterPlotGenes(this.value, 'B');
	});

	jQuery('#experimentSelect_scatterplotGenes_' + 'B').change(function() {
		ChangeExperimentScatterPlotGenes(this.value, 'B');
	});

	jQuery('#conditionSelect_scatterplotGenes_' + 'B').change(function() {
		ChangeConditionScatterplotGenes(this.value, 'B');
	});

	experimentSelectedScatterplotGenes['A'] = '';
	typeSelectedScatterplotGenes['A']       = '';
	conditionScatterplotGenes['A']          = '';
	experimentSelectedScatterplotGenes['B'] = '';
	typeSelectedScatterplotGenes['B']       = '';
	conditionScatterplotGenes['B']          = '';
	expressionsScatterplotGenes['A']		= [];
	expressionsScatterplotGenes['B']		= [];
}

function setArrayOfExpressionValues(){
	var expressionValues = [];
	var typeMin          = getMin('A', 'B');
	var typeMax          = getMax('A', 'B');
	for (var i = 0; i < expressionsScatterplotGenes[typeMin].length; i++) {
		var expressionValue      = {};
		expressionValue['gene']  = expressionsScatterplotGenes[typeMin][i]['ExpressionValues.gene.primaryIdentifier'].value;
		expressionValue[typeMin] = expressionsScatterplotGenes[typeMin][i]['ExpressionValues.expressionValue'].value;
		for (var j = 0; j < expressionsScatterplotGenes[typeMax].length; j++) {
			if (expressionsScatterplotGenes[typeMax][j]['ExpressionValues.gene.primaryIdentifier'].value == expressionValue['gene']) {
				expressionValue[typeMax] = expressionsScatterplotGenes[typeMax][j]['ExpressionValues.expressionValue'].value;
				break;
			}
		}
		expressionValues.push(expressionValue);
	}
	destroyCanvas();
	scatterplotGenes(expressionValues);
}

function getMin(typeA, typeB) {
	return (expressionsScatterplotGenes[typeA].length <= expressionsScatterplotGenes[typeB].length) ? typeA : typeB;
}

function getMax(typeA, typeB) {
	return (expressionsScatterplotGenes[typeA].length <= expressionsScatterplotGenes[typeB].length) ? typeB : typeA;
}

function destroyCanvas() {
	CanvasXpress.destroy('scatterplotGenes_canvas');
	jQuery('#set_canvas_scatterplotGenes').html('<canvas id="scatterplotGenes_canvas" width="700" height="550"></canvas>');
}

function scatterplotGenes(expressionValues) {
	var smpsA       = experimentSelectedScatterplotGenes['A'];
	smpsA           += (experimentSelectedScatterplotGenes['A'] == experimentSelectedScatterplotGenes['B']) ? ' (A)' : '';
	var smpsB       = experimentSelectedScatterplotGenes['B'];
	smpsB           += (experimentSelectedScatterplotGenes['A'] == experimentSelectedScatterplotGenes['B']) ? ' (B)' : '';
	var smps        = [smpsA, smpsB];
	var Description = [conditionScatterplotGenes['A'], conditionScatterplotGenes['B']];
	var vars        = [];
	var data        = [];
	for (var i = 0; i < expressionValues.length; i++) {
		vars.push(expressionValues[i].gene);
		data.push([expressionValues[i].A, expressionValues[i].B]);
	}


	var scatterplotGenes_canvas = new CanvasXpress("scatterplotGenes_canvas", {
										  y: {
										    smps: smps,
										    vars: vars,
										    data: data
										  },
										  x: {
										    Description
										  }
										}, {
											axisTickFontSize: 11,
											axisTitleFontSize: 11,
											confidenceIntervalColor: "rgb(50,50,50)",
											decorationFontSize: 11,
											fitLineStyle: "solid",
											graphType: "Scatter2D",
											legendFontSize: 11,
											maxRows: 3,
											motionCurrentFontSize: 71,
											title: "",
											titleFontSize: 25,
											xAxis: [experimentSelectedScatterplotGenes['A']],
											xAxisTitle: conditionScatterplotGenes['A'],
											yAxis: [experimentSelectedScatterplotGenes['B']],
											yAxisTitle: conditionScatterplotGenes['B']
										});
	scatterplotGenes_canvas.draw();
}