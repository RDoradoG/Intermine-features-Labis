//Autor: Rodrigo Dorado

fillTypeExperiment();

function fillTypeExperiment() {
	var selected = true;
	for (var i = 0; i < types.length; i++) {
		fillAOption('experimentType', types[i].name, selected);
		selected = false;
	}
	if (!selected) {
		ChangeExperimentType(types[0].name);
	}
}

function filTypeExperiment(result) {
	var selected = true;
	jQuery('#experimentSelect').html('');
	for (var i = 0; i < result.length; i++) {
		fillAOption('experimentSelect', result[i]['ExperimentDescription.name'], selected);
		selected = false;
	}
	if (!selected) {
		ChangeExepreiment(result[0]['ExperimentDescription.name']);
	}
}

function fillAOption(id, value, selected) {
	var option = jQuery("<option></option>").attr("value", value).text(value);
	if (selected) {
		option.attr("selected", "selected");
	}
	jQuery('#' + id).append(option);
}


jQuery('#experimentType').change(function() {
	ChangeExperimentType(this.value);
});

jQuery('#experimentSelect').change(function() {
	ChangeExepreiment(this.value);
});

function ChangeExepreiment(value) {
	experiment = value;
	var query = '<query model="genomic" view="ExpressionValues.condition ExpressionValues.expressionValue ExpressionValues.gene.primaryIdentifier ExpressionValues.gene.secondaryIdentifier ExpressionValues.gene.symbol ExpressionValues.gene.name" sortOrder="ExpressionValues.gene.primaryIdentifier ASC" constraintLogic="A and B and C" ><constraint path="ExpressionValues.gene" op="IN" value="' + bagName + '" code="A" /><constraint path="ExpressionValues.experiment.name" op="=" value="' + experiment + '" code="B" /><constraint path="ExpressionValues.type.name" op="=" value="' + typeSelected + '" code="C" /></query>';
	APIExecuteQuery(query, 'Experiment');
}

function getConditions() {
	var query = '<query model="genomic" view="ExpressionValues.condition" sortOrder="ExpressionValues.condition ASC" constraintLogic="A and B and C" ><constraint path="ExpressionValues.experiment.name" op="=" value="' + experiment + '" code="A" /><constraint path="ExpressionValues.gene" op="IN" value="' + bagName + '" code="B" /><constraint path="ExpressionValues.type.name" op="=" value="' + typeSelected + '" code="C" /></query>';
	APIExecuteQuery(query, 'conditionsOfBag');
}

function setConditions(result) {
	arrayConditions = [];
	if (result.length > 0) {
		arrayConditions.push(result[0]['ExpressionValues.condition']);
		for (var i = 1; i < result.length; i++) {
			if (result[i]['ExpressionValues.condition'] != result[i - 1]['ExpressionValues.condition']) {
				arrayConditions.push(result[i]['ExpressionValues.condition']);
			}
		}
	}
	getExpressionValues()

}

function getExpressionValues() {
	var query = '<query model="genomic" view="ExpressionValues.condition ExpressionValues.expressionValue ExpressionValues.gene.primaryIdentifier" sortOrder="ExpressionValues.condition ASC ExpressionValues.gene.primaryIdentifier ASC" constraintLogic="A and B and C" ><constraint path="ExpressionValues.experiment.name" op="=" value="' + experiment + '" code="A" /><constraint path="ExpressionValues.type.name" op="=" value="' + typeSelected + '" code="B" /><constraint path="ExpressionValues.gene" op="IN" value="' + bagName + '" code="C" /></query>';
	APIExecuteQuery(query, 'ExpressionValues');
}

function ChangeExperimentType(value) {
	typeSelected = value;
	var query ='<query model="genomic" view="ExperimentDescription.description ExperimentDescription.name" sortOrder="ExperimentDescription.description ASC" constraintLogic="A and B" ><constraint path="ExperimentDescription.ExpressionValue.type.name" op="=" value="' + typeSelected + '" code="A" /><constraint path="ExperimentDescription.ExpressionValue.gene" op="IN" value="' + bagName + '" code="B" /></query>';
	APIExecuteQuery(query, 'typeExperiment');
}

function getArrayData(result) {
	var dataCanvas = [];
	var rowCanvas  = [];
	var sum        = 0;
	var max        = 0;
	var min        = 0;
	if (result.length > 0) {
		max = min = sum = result[0]['ExpressionValues.expressionValue'];
		rowCanvas.push(sum);
		for (var i = 1; i < result.length; i++) {
			if (result[i]['ExpressionValues.condition'] != result[i - 1]['ExpressionValues.condition']) {
				dataCanvas.push(rowCanvas);
				rowCanvas = [];
			}
			var field = result[i]['ExpressionValues.expressionValue'];
			rowCanvas.push(field);
			sum       = sum + field;
			if(field > max) {max = field;}
			if(field < min) {min = field;}
		}
		if (rowCanvas.length > 0) {
			dataCanvas.push(rowCanvas);
		}
	}
	var mean          = sum / result.length;
	var dataForCanvas = {
		y: {
			vars: arrayConditions,
			smps: Genes,
			desc: ['Intensity'],
			data: dataCanvas
		}
	};
	jQuery('#set_canvas').html('');
	jQuery('#set_canvas').html('<canvas id="canvas_cl" width="700" height="550"></canvas>');
	drawCanvas(dataForCanvas, mean, max, min, result.length, experiment);
}

function getResult(result, idFunction) {
	switch(idFunction) {
		case 'typeExperiment':
			filTypeExperiment(result);
		break;

		case 'Experiment':
			getConditions(result);
		break;

		case 'conditionsOfBag':
			setConditions(result)
		break;

		case 'ExpressionValues':
			getArrayData(result)
		break;
	}
}

function drawCanvas(dataForCanvas, Mean, max, min, sizeData, title){
	if (sizeData < 1) {
		jQuery('#heatmap_div').remove();
		jQuery('#expression_div').html('<i>Expression scores are not available</i>');
	} else {
		if (feature_count > max_map) {
			jQuery('#heatmap_div').remove();
			jQuery('#expression_div').html('<i>Too many elements, please select a subset to see the heat maps.</i>');
		}
		jQuery("#description").hide();
		jQuery("#description_div").click(function () {
			if(jQuery("#description").is(":hidden")) {
				jQuery("#co").attr("src", "images/disclosed.gif");
			} else {
				jQuery("#co").attr("src", "images/undisclosed.gif");
			}
				jQuery("#description").toggle("slow");
		});

		jQuery('#hierarchicalSelect').val('single');
		jQuery('#kMenasSelect').val('3');

		var Deviation                    = getDeviation(dataForCanvas.y.data, sizeData, Mean);
		dataForCanvas.y.data             = setZScores(dataForCanvas.y.data, Mean, Deviation);
		max                              = getAZSCore(max, Mean, Deviation);
		min                              = getAZSCore(min, Mean, Deviation) * (-1);
		max                              = Math.ceil(max);
		min                              = Math.ceil(min);

		if (max < min) {
			max = min;
		}

		// hm - heatmap; cl - cellline; ds - developmentalstage; hc - hierarchical clustering; km - kmeans
		min = max * (-1);
		var hm_cl = new CanvasXpress(
			'canvas_cl',
			dataForCanvas,
			{
				graphType: 'Heatmap',
				title: title,
				// heatmapType: 'yellow-purple',
				dendrogramSpace: 6,
				smpDendrogramPosition: 'right',
				varDendrogramPosition: 'bottom',
				setMin: min,
				setMax: max,
				varLabelRotate: 45,
				centerData: false,
				//colorSpectrum: ['#920000', '#009292'],
				autoExtend: true
			},
			{
				click: function(o) {
					if (o != undefined) {
						var featureId    = o.y.smps;
						var condition    = o.y.vars;
						var query        = '<query model="genomic" view="ExpressionValues.condition ExpressionValues.expressionValue ExpressionValues.experiment.name ExpressionValues.gene.primaryIdentifier ExpressionValues.gene.secondaryIdentifier ExpressionValues.gene.symbol ExpressionValues.gene.name ExpressionValues.gene.organism.shortName" sortOrder="ExpressionValues.condition ASC" > <constraint path="ExpressionValues.condition" op="=" value="' + condition + '" code="A" /> <constraint path="ExpressionValues.experiment.name" op="=" value="' + title + '" code="B" /> <constraint path="ExpressionValues.gene.primaryIdentifier" op="=" value="' + featureId + '" /> <constraint path="ExpressionValues.type.name" op="=" value="' + typeSelected + '" /> </query>';
						var encodedQuery = encodeURIComponent(query);
						encodedQuery     = encodedQuery.replace("%20", "+");
						window.open("/" + webAppPath + "/loadQuery.do?skipBuilder=true&query=" + encodedQuery + "%0A&trail=%7Cquery&method=xml");
					}
				}
			}
		);

		// cluster on gene/exons
		if (feature_count > max_cluster) {
			jQuery("#hierarchicalSelect").attr('disabled', true);
		}

		jQuery('#kMenasSelect').html('<option value="3" selected="selected">3</option>');
		if (feature_count > 3 && feature_count <= max_cluster) {
			hm_cl.clusterSamples();
			hm_cl.kmeansSamples();
			for (var i=4; i < feature_count; ++i) {
				jQuery('#kMenasSelect').
				append(jQuery("<option></option>").
				attr("value",i).
				text(i));
			}
		} else {
			jQuery("#kMenasSelect").attr('disabled', true);
		}

		// cluster on conditions
		if (feature_count <= max_cluster) {
			hm_cl.clusterVariables(); // clustering method will call draw action within it.
			hm_cl.draw();
		}

		jQuery('#hierarchicalSelect').change(function() {
			hm_cl.linkage = this.value;
			if (feature_count >= 3) { hm_cl.clusterSamples(); }
			hm_cl.clusterVariables();
			hm_cl.draw();
		});

		jQuery('#kMenasSelect').change(function() {
			hm_cl.kmeansClusters = parseInt(this.value);
			hm_cl.kmeansSamples();
			hm_cl.draw();
		});
	}
}

function getDeviation(arrayData, sizeData, Mean) {
	var sum = 0;
	for (var i = 0; i < arrayData.length; i++) {
		for (var j = 0; j < arrayData[i].length; j++) {
			aux = (arrayData[i][j] - Mean);
			sum = sum + (aux * aux);
		}
	}
	return Math.sqrt(sum / sizeData);
}

function setZScores(arrayData, Mean, Deviation) {
	for (var i = 0; i < arrayData.length; i++) {
		for (var j = 0; j < arrayData[i].length; j++) {
			arrayData[i][j] = getAZSCore(arrayData[i][j], Mean, Deviation);
		}
	}
	return arrayData;
}

function getAZSCore(data, Mean, Deviation) {
	return (data - Mean) / Deviation;
}