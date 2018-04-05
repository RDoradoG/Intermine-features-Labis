//Autor: Rodrigo Dorado

console.log(canvasData);
fillExperimentsOptions();
drawCanvas(0);

jQuery('#experimentSelect').change(function() {
	ChangeExepreiment(this.value);
});

function ChangeExepreiment(experiment) {
	for(var i = 0; i < JsonExepriemnts.length; i++) {
		if(JsonExepriemnts[i].name == experiment) {
			jQuery('#set_canvas').html('');
			jQuery('#set_canvas').html('<canvas id="canvas_cl" width="700" height="550"></canvas>');
			drawCanvas(i);
			break;
		}
	}
}

function fillExperimentsOptions() {
	for (var i = 0; i < JsonExepriemnts.length; i++) {
		fillExperimentsAOption(JsonExepriemnts[i].name);
	}
}

function fillExperimentsAOption(value) {
	jQuery('#experimentSelect').append(jQuery("<option></option>").attr("value", value).text(value));
}

function drawCanvas(position){
	var dataForCanvas = canvasData[position].data;
	var min           = canvasData[position].min;
	var max           = canvasData[position].max;
	var title         = canvasData[position].name;
	var sizeData      = canvasData[position].numberData;
	var Mean          = canvasData[position].mean;
	var Deviation     = canvasData[position].deviation;
	if (sizeExpressions < 10) {
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

		if (Deviation == 0) {
			Deviation                        = getDeviation(dataForCanvas.y.data, sizeData, Mean);
			dataForCanvas.y.data             = setZScores(dataForCanvas.y.data, Mean, Deviation);
			max                              = getAZSCore(max, Mean, Deviation);
			min                              = getAZSCore(min, Mean, Deviation) * (-1);
			max                              = Math.ceil(max);
			min                              = Math.ceil(min);
			if (max < min) {max = min;}
			canvasData[position].deviation   = Deviation;
			canvasData[position].data.y.data = dataForCanvas.y.data;
			canvasData[position].max         = max;
			canvasData[position].min         = min;
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
						var query        = '<query model="genomic" view="ExpressionValues.condition ExpressionValues.expressionValue ExpressionValues.experiment.name ExpressionValues.gene.primaryIdentifier ExpressionValues.gene.secondaryIdentifier ExpressionValues.gene.symbol ExpressionValues.gene.name ExpressionValues.gene.organism.shortName" sortOrder="ExpressionValues.condition ASC" > <constraint path="ExpressionValues.condition" op="=" value="' + condition + '" code="A" /> <constraint path="ExpressionValues.experiment.name" op="=" value="' + title + '" code="B" /> <constraint path="ExpressionValues.gene.primaryIdentifier" op="=" value="' + featureId + '" /> </query>';
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
