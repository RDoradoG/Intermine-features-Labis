//Autor: Rodrigo Dorado

fillTypeExperiment('');

fillTypeExperiment('_secundary');

function fillTypeExperiment(addId) {
	var selected_type = ''; 
	for (var i = 0; i < types.length; i++) {
		if (types[i].name == defaultValues.type) {
			fillAOption('experimentType' + addId, types[i].name, true);
			selected_type = types[i].name;
		} else {
			fillAOption('experimentType' + addId, types[i].name, false);
		}
	}
	if (selected_type != '') {
		ChangeExperimentType(selected_type, addId);
	}
}

function filTypeExperiment(result, addID) {
	var selected_experiment = '';
	jQuery('#experimentSelect' + addID).html('');
	for (var i = 0; i < result.length; i++) {
		if (result[i]['ExperimentDescription.name'].value == defaultValues.experiment) {
			fillAOption('experimentSelect' + addID, result[i]['ExperimentDescription.name'].value, true);
			selected_experiment = result[i]['ExperimentDescription.name'].value;
		} else {
			fillAOption('experimentSelect' + addID, result[i]['ExperimentDescription.name'].value, false);
		}
	}
	if (selected_experiment != '') {
		ChangeExepreiment(selected_experiment, addID);
	} else {
		ChangeExepreiment(result[0]['ExperimentDescription.name'].value, addID);
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
	ChangeExperimentType(this.value, '');
});

jQuery('#experimentSelect').change(function() {
	ChangeExepreiment(this.value, '');
});

jQuery('#experimentType_secundary').change(function() {
	ChangeExperimentType(this.value, '_secundary');
});

jQuery('#experimentSelect_secundary').change(function() {
	ChangeExepreiment(this.value, '_secundary');
});


function ChangeExepreiment(value, addID) {
	experiment = value;
	getConditions(addID);
}

function getConditions(addID) {
	var query = '<query model="genomic" view="ExpressionValues.condition" sortOrder="ExpressionValues.condition ASC" constraintLogic="A and B and C" ><constraint path="ExpressionValues.experiment.name" op="=" value="' + experiment + '" code="A" /><constraint path="ExpressionValues.gene" op="IN" value="' + bagName + '" code="B" /><constraint path="ExpressionValues.type.name" op="=" value="' + typeSelected + '" code="C" /></query>';
	//APIExecuteQuery(query, 'conditionsOfBag');
	APIExecuteQuery(query, {key: 'conditionsOfBag', addID: addID});
}

function setConditions(result, addID) {
	arrayConditions = [];
	if (result.length > 0) {
		arrayConditions.push(result[0]['ExpressionValues.condition'].value);
		for (var i = 1; i < result.length; i++) {
			if (result[i]['ExpressionValues.condition'].value != result[i - 1]['ExpressionValues.condition'].value) {
				arrayConditions.push(result[i]['ExpressionValues.condition'].value);
			}
		}
	}
	getExpressionValues(addID)

}

function getExpressionValues(addID) {
	var query = '<query model="genomic" view="ExpressionValues.condition ExpressionValues.expressionValue ExpressionValues.gene.primaryIdentifier" sortOrder="ExpressionValues.condition ASC ExpressionValues.gene.primaryIdentifier ASC" constraintLogic="A and B and C" ><constraint path="ExpressionValues.experiment.name" op="=" value="' + experiment + '" code="A" /><constraint path="ExpressionValues.type.name" op="=" value="' + typeSelected + '" code="B" /><constraint path="ExpressionValues.gene" op="IN" value="' + bagName + '" code="C" /></query>';
	//APIExecuteQuery(query, 'ExpressionValues');
	APIExecuteQuery(query, {key: 'ExpressionValues', addID: addID});
}

function ChangeExperimentType(value, addID) {
	typeSelected = value;
	var query ='<query model="genomic" view="ExperimentDescription.description ExperimentDescription.name" sortOrder="ExperimentDescription.description ASC" constraintLogic="A and B" ><constraint path="ExperimentDescription.ExpressionValue.type.name" op="=" value="' + typeSelected + '" code="A" /><constraint path="ExperimentDescription.ExpressionValue.gene" op="IN" value="' + bagName + '" code="B" /></query>';
	//APIExecuteQuery(query, 'typeExperiment');
	APIExecuteQuery(query, {key: 'typeExperiment', addID: addID});
}

function getArrayData(result, addID) {
	var dataCanvas = [];
	var rowCanvas  = [];
	var sum        = 0;
	var max        = 0;
	var min        = 0;

	var iGenes = 0;

	if (result.length > 0) {
		max = min = sum = result[0]['ExpressionValues.expressionValue'].value;
		while (result[0]['ExpressionValues.gene.primaryIdentifier'].value != Genes[iGenes]) {
			rowCanvas.push(NaN);
			iGenes++;
		}
		rowCanvas.push(sum);
		iGenes++;
		for (var i = 1; i < result.length; i++) {
			if (result[i]['ExpressionValues.condition'].value != result[i - 1]['ExpressionValues.condition'].value) {
				//verify null
				while (iGenes < Genes.length) {
					rowCanvas.push(NaN);
					iGenes++;
				}
				iGenes = 0;
				//verify null
				dataCanvas.push(rowCanvas);
				rowCanvas = [];
			}
			var field = result[i]['ExpressionValues.expressionValue'].value;
			//verify null
			while (result[i]['ExpressionValues.gene.primaryIdentifier'].value != Genes[iGenes]) {
				rowCanvas.push(NaN);
				iGenes++;
			}
			iGenes++;
			//verify null
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
	var idCanvasHTML = "canvas_cl" + addID
	jQuery('#set_canvas' + addID).html('');
	jQuery('#set_canvas' + addID).html('<canvas id="' + idCanvasHTML + '" width="700" height="550"></canvas>');
	drawCanvas(dataForCanvas, mean, max, min, result.length, experiment, addID);
}

function getResult(result, idFunction) {
	//{key: 'typeExperiment', addID: addID}
	switch(idFunction.key) {
		case 'typeExperiment':
			filTypeExperiment(result, idFunction.addID);
		break;

		case 'conditionsOfBag':
			setConditions(result, idFunction.addID)
		break;

		case 'ExpressionValues':
			getArrayData(result, idFunction.addID)
		break;

		case 'getURL':
			getURL(result)
		break;
	}
}

function getURL(result) {
	if(result.length > 0) {
		var url = result[0]['Gene.primaryIdentifier'].url;
		window.open("/" + webAppPath + url);
	} else {
		console.error('error', 'No gene url.');
	}
}

function drawCanvas(dataForCanvas, Mean, max, min, sizeData, title, addID){
	if (sizeData < 1) {
		jQuery('#heatmap_div').remove();
		jQuery('#expression_div').html('<i>Expression scores are not available</i>');
	} else {
		if (feature_count > max_map) {
			jQuery('#heatmap_div').remove();
			jQuery('#expression_div').html('<i>Too many elements, please select a subset to see the heat maps.</i>');
		} else {
			jQuery("#description").hide();
			jQuery("#description_div").click(function () {
				if(jQuery("#description").is(":hidden")) {
					jQuery("#co").attr("src", "images/disclosed.gif");
				} else {
					jQuery("#co").attr("src", "images/undisclosed.gif");
				}
					jQuery("#description").toggle("slow");
			});

			jQuery('#hierarchicalSelect' + addID).val('single');
			jQuery('#kMenasSelect' + addID).val('3');

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
				'canvas_cl' + addID,
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
					missingDataColor: 'rgb(255, 255, 0)',
					//colorSpectrum: ['#920000', '#009292'],
					autoExtend: true
				},
				{
					click: function(o) {
						if (o != undefined) {
							var featureId    = o.y.smps;
							var query        = '<query model="genomic" view="Gene.primaryIdentifier" sortOrder="Gene.primaryIdentifier ASC" ><constraint path="Gene.primaryIdentifier" op="=" value="' + featureId + '" code="A" /></query>';
							//APIExecuteQuery(query, 'getURL');
							APIExecuteQuery(query, {key: 'getURL', addID: addID});
						}
					}
				}
			);

			// cluster on gene/exons
			if (feature_count > max_cluster) {
				jQuery("#hierarchicalSelect" + addID).attr('disabled', true);
			}

			jQuery('#kMenasSelect' + addID).html('<option value="3" selected="selected">3</option>');
			if (feature_count > 3 && feature_count <= max_cluster) {
				hm_cl.clusterSamples();
				hm_cl.kmeansSamples();
				for (var i=4; i < feature_count; ++i) {
					jQuery('#kMenasSelect' + addID).
					append(jQuery("<option></option>").
					attr("value",i).
					text(i));
				}
			} else {
				jQuery("#kMenasSelect" + addID).attr('disabled', true);
			}

			// cluster on conditions
			if (feature_count <= max_cluster) {
				hm_cl.clusterVariables(); // clustering method will call draw action within it.
				hm_cl.draw();
			}

			jQuery('#hierarchicalSelect' + addID).change(function() {
				hm_cl.linkage = this.value;
				if (feature_count >= 3) { hm_cl.clusterSamples(); }
				hm_cl.clusterVariables();
				hm_cl.draw();
			});

			jQuery('#kMenasSelect' + addID).change(function() {
				hm_cl.kmeansClusters = parseInt(this.value);
				hm_cl.kmeansSamples();
				hm_cl.draw();
			});
		}
	}
}

function getDeviation(arrayData, sizeData, Mean) {
	var sum = 0;
	for (var i = 0; i < arrayData.length; i++) {
		for (var j = 0; j < arrayData[i].length; j++) {
			if (!isNaN(arrayData[i][j])) {
				aux = (arrayData[i][j] - Mean);
				sum = sum + (aux * aux);
			}
		}
	}
	return Math.sqrt(sum / sizeData);
}

function setZScores(arrayData, Mean, Deviation) {
	for (var i = 0; i < arrayData.length; i++) {
		for (var j = 0; j < arrayData[i].length; j++) {
			if (!isNaN(arrayData[i][j])) {
				arrayData[i][j] = getAZSCore(arrayData[i][j], Mean, Deviation);
			}
		}
	}
	return arrayData;
}

function getAZSCore(data, Mean, Deviation) {
	return (data - Mean) / Deviation;
}
