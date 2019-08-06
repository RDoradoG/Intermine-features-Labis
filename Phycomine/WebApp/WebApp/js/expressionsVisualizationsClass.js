/*===============================================================
=				Functionallity of Visualizations				=
=																=
▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒
= Labis - IQ, USP. São Paulo									=
= @author Rodrigo Dorado						█║█║║█║█║█║║█	=
===============================================================*/

class Bag {
	constructor() {
		this.bagName = '';
	}

	setBagName(bagName) {
		this.bagName = bagName;
	}

	getBagName() {
		return this.bagName;
	}
}

class QueryExecute extends Bag {
	constructor() {
		super();
		this.api_key = ''
	}

	setApiKey(key){
		this.api_key = key
	}

	APIExecuteQuery(query, idCall, sz, strt) {
		var size     = (sz == undefined) ? 10000 : sz;
		var start    = (strt == undefined) ? 0 : strt;
		var settings = {
		  async: true,
		  url:  'service/query/results/tablerows',
		  method: 'POST',
		  headers: {
		    "Content-Type": 'application/x-www-form-urlencoded',
		    Accept: 'application/json',
		    Authorization: 'Token ' + this.api_key
		  },
		  data: {
		    start: start,
		    size: size,
		    query: query,
		    format: 'json'
		  }
		}

		var thisQueryExecute = this;
		jQuery.ajax(settings).done(function (response) {
		  var result = getResultFormat(response);
		  thisQueryExecute.getResult(result, idCall);
		});
	}


	getResult(result, idCall) {
		console.log('There is not a callBack for: ' + idCall);
		console.log(result);
		console.log("---");
	}
}

class Graphs extends QueryExecute {
	constructor() {
		super();
		this.min                = 0;
		this.max                = 0;
		this.heatMapId          = '';
		this.heatMapTitle       = '';
		this.heatMapDivId       = '';
		this.geneProfileId      = '';
		this.geneProfileDivId   = '';
		this.expressions        = [];
		this.grpahs             = {};
		this.heatMapOptions     = {};
		this.geneProfileOptions = {};
	}

	setExpressions(expressions){
		this.expressions = expressions;
	}

	setMax(max){
		this.max = max;
	}

	getGraphs() {
		return this.grpahs;
	}

	setMin(min){
		this.min = min;
	}

	setHeatMapId(id) {
		this.heatMapId = id;
	}

	setHeatMapDivId(id) {
		this.heatMapDivId = id;
	}

	setHeatMapTitle(title) {
		this.heatMapTitle = title;
	}

	setGeneProfileId(id) {
		this.geneProfileId = id;
	}

	setGeneProfileDivId(id) {
		this.geneProfileDivId = id;
	}

	setGeneProfileOptions(options) {
		this.geneProfileOptions = options;
	}

	setHeatMapOptions(options) {
		this.heatMapOptions = options;
	}

	destroyHeatMap() {
		jQuery('#' + this.heatMapDivId).html('');
		jQuery('#' + this.heatMapDivId).html('<canvas id="' + this.heatMapId + '" width="700" height="550"></canvas>');
	}

	destroyGeneProfile() {
		jQuery('#' + this.geneProfileDivId).html('');
		jQuery('#' + this.geneProfileDivId).html('<canvas id="' + this.geneProfileId + '" width="700" height="550"></canvas>');
	}

	getURL(result) {
		if(result.length > 0) {
			var url = result[0]['Gene.primaryIdentifier'].url;
			window.open("/" + webAppPath + url);
		} else {
			console.error('error', 'No gene url.');
		}
	}

	drawGraph(id, data, options, actions, genesToHide) {
		var canvasXpress = new CanvasXpress(
			id,
			data,
			options,
			actions
		);
		canvasXpress.draw();

		if (genesToHide.length > 0) {
			canvasXpress.filterVariables("vars", "exact", genesToHide, false, true, true);
		}

		this.grpahs[id] = canvasXpress;
	}

	heatMapActions() {
		var thisGraphs = this;
		return  {
			click: function(o) {
				if (o != undefined) {
					var featureId = o.y.smps;
					var query     = '<query model="genomic" view="Gene.primaryIdentifier" sortOrder="Gene.primaryIdentifier ASC" >';
					query += '<constraint path="Gene.primaryIdentifier" op="=" value="' + featureId + '" code="A" />';
					query += '</query>';
					thisGraphs.APIExecuteQuery(query, 'getURL');
				}
			}
		};
	}

	geneProfileActions() {
		return  null;
	}

	drawHeatMap(genes, conditions){
		var dataForCanvas = {
			y: {
				vars: conditions,
				smps: genes,
				desc: ['Intensity'],
				data: this.expressions
			}
		};
		this.heatMapOptions['setMin'] = this.min;
		this.heatMapOptions['setMax'] = this.max;
		this.heatMapOptions['title']  = this.heatMapTitle;
		this.drawGraph(this.heatMapId, dataForCanvas, this.heatMapOptions, this.heatMapActions(), []);
	}

	drawGeneProfile(genes, conditions, conditionsMeans, genesSelected){
		var genesToHide = [];
		for (var key in genesSelected) {
			if (!genesSelected[key]) {
				genesToHide.push(key);
			}
		}
		var expressionsTranpost = this.transpose(this.expressions);
		expressionsTranpost.push(conditionsMeans);
		var dataForCanvas       = {
			y: {
				vars: genes.concat('Average'),
				smps: conditions,
				data: expressionsTranpost
			}
		};
		this.drawGraph(this.geneProfileId, dataForCanvas, this.geneProfileOptions, this.geneProfileActions(), genesToHide);
	}

	transpose(a) {
	  var w = a.length || 0;
	  var h = a[0] instanceof Array ? a[0].length : 0;
	  if(h === 0 || w === 0) { return []; }
	  var t = [];
	  for (var i = 0; i < h; i++) {
	    t[i] = [];
	    for(var j = 0; j < w; j++) {
	      t[i][j] = a[j][i];
	    }
	  }
	  return t;
	}

}

class CanvasVisualization extends Graphs {
	constructor() {
		super();
		this.ZscoreOpt        = true;
		this.checkUnchekAll   = false;
		this.experimentName   = '';
		this.genes            = [];
		this.conditions       = [];
		this.conditionMeans   = [];
		this.expressionValues = [];
		this.means            = {};
		this.dataShowed       = {};
		this.genesSelected    = {};
	}

	addGeneSelection(geneSelected, opt) {
		this.genesSelected[geneSelected] = opt;
	}

	setGeneSelection(geneSelected) {
		this.genesSelected[geneSelected] = !this.genesSelected[geneSelected];
		this.destroyGeneProfile();
		this.drawGeneProfile(this.genes, this.conditions, this.dataShowed.conditionsMeans, this.genesSelected);
		this.finishLoading();
	}

	setCheckUncheckAll() {
		this.checkUnchekAll = !this.checkUnchekAll;
		for (var geneSelected in this.genesSelected) {
			this.genesSelected[geneSelected] = this.checkUnchekAll;
		}
		this.destroyGeneProfile();
		this.drawGeneProfile(this.genes, this.conditions, this.dataShowed.conditionsMeans, this.genesSelected);
		this.finishLoading();
	}

	getNextCheckUncheckAll() {
		return !this.checkUnchekAll;
	}

	setExperimentName(experimentName) {
		this.experimentName = experimentName;
	}

	getExperimentName() {
		return this.experimentName;
	}

	setZscore(ZscoreOpt) {
		this.ZscoreOpt = ZscoreOpt;
	}

	changeZscore(value) {
		this.ZscoreOpt = !this.ZscoreOpt;
		this.drawCanvas();
		this.finishLoading();
	}

	setGenes(genes) {
		this.genes = genes;
	}

	getGenes() {
		return this.genes;
	}

	setConditions(conditions) {
		this.conditions = conditions;
	}

	getConditions() {
		return this.conditions;
	}

	setMeans(means) {
		this.means = means;
	}

	setConditionMeans(conditionMeans){
		this.conditionMeans = conditionMeans;
	}

	setMax(max) {
		this.max = max;
	}

	setMin(min) {
		this.min = min;
	}

	setExpressionValues(expressionValues) {
		this.expressionValues = expressionValues;
	}

	drawCanvas() {
		if (this.ZscoreOpt) {
			var sum;
			var minimum          = 0;
			var maximum          = 0;
			var expressionToDraw = [];
			var condnMeans       = [];
			var deviations       = this.getDeviation();
			var geneSize         = this.expressionValues[0].length;
			for (var i = 0; i < this.expressionValues.length; i++) {
				expressionToDraw[i] = [];
				sum                 = 0
				for (var j = 0; j < this.expressionValues[i].length; j++) {
					if (!isNaN(this.expressionValues[i][j])) {
						expressionToDraw[i][j] = this.getAZSCore(this.expressionValues[i][j], this.means[this.genes[j]], deviations[j]);
						sum += expressionToDraw[i][j];
						if (expressionToDraw[i][j] > maximum) { maximum = expressionToDraw[i][j]; }
						if (expressionToDraw[i][j] < minimum) { minimum = expressionToDraw[i][j]; }
					} else {
						expressionToDraw[i][j] = NaN;
					}
				}
				condnMeans.push(sum/geneSize);
			}
			minimum = minimum * (-1);
			if (minimum > maximum) {
				maximum = minimum;
			}
			minimum = maximum * (-1);
			this.saveDataShowed(expressionToDraw, maximum, minimum, condnMeans);
		} else {
			this.saveDataShowed(this.expressionValues, this.max, this.min, this.conditionMeans);
		}
	}

	saveDataShowed(expressions, max, min, conditionsMeans) {
		this.dataShowed = {
			expressions: expressions,
			max: max,
			min: min,
			conditionsMeans: conditionsMeans
		};
		this.sendToCanvas();
	}

	sendToCanvas() {
		this.destroyHeatMap();
		this.destroyGeneProfile();
		this.setExpressions(this.dataShowed.expressions);
		this.setMax(this.dataShowed.max);
		this.setMin(this.dataShowed.min);
		this.setHeatMapTitle(this.experimentName);
		this.drawGeneProfile(this.genes, this.conditions, this.dataShowed.conditionsMeans, this.genesSelected);
		this.drawHeatMap(this.genes, this.conditions);
	}

	getDeviation() {
		var sum = [];
		var dev = []
		for (var i = 0; i < this.expressionValues[0].length; i++) {
			sum.push(0)
		}

		for (var i = 0; i < this.expressionValues.length; i++) {
			for (var j = 0; j < this.expressionValues[i].length; j++) {
				if (!isNaN(this.expressionValues[i][j])) {
					var aux = this.expressionValues[i][j] - this.means[this.genes[j]];
					sum[j]  = sum[j] + (aux * aux);
				}

				if ((i + 1) == this.expressionValues.length) {
					dev.push(Math.sqrt(sum[j] / this.expressionValues.length))
				}
			}
		}

		return dev
	}

	getAZSCore(data, Mean, deviation) {
		if (deviation == 0) {
			return data - Mean
		}
		return (data - Mean) / deviation;
	}
}


class ExpressionVisualizations extends CanvasVisualization {
	constructor() {
		super();
		this.loading                = false;
		this.maxMap                 = '';
		this.bagName                = '';
		this.ZscoreField            = '';
		this.featureCount           = '';
		this.geneListField          = '';
		this.checkBoxGeneName       = '';
		this.experimentNameField    = '';
		this.experimentTypeField    = '';
		this.experimentTypeSelected = '';
		this.averageName            = 'Average';
		this.genes                  = [];
		this.experiemntTypes        = [];
		this.genesSelected          = {};
	}

	setLoading(load) {
		this.loading                                                  = load;
		jQuery('#' + this.experimentTypeField)[0].disabled            = load;
		jQuery('#' + this.experimentNameField)[0].disabled            = load;
		jQuery('#' + this.ZscoreField)[0].disabled                    = load;
		this.setLoadingGenesBox(load)
		jQuery('.' + this.checkBoxGeneName + '_checkall')[0].disabled = load;
	}

	getLoading() {
		return this.loading;
	}

	setLoadingGenesBox(value) {
		jQuery('.' + this.checkBoxGeneName)[0].disabled = value;
		for (var i = 0; i < this.genes.length; i++) {
			jQuery('.' + this.checkBoxGeneName)[i + 1].disabled = value;
		}
	}

	startLoading() {
		this.setLoading(true);
	}

	finishLoading() {
		this.setLoading(false);
	}

	fillGeneList() {
		var html = '<input type="checkbox" name="' + this.checkBoxGeneName + '_checkall" class="' + this.checkBoxGeneName + '_checkall" value="checkAll"> Check/Uncheck All <br>';
		html     += '<input type="checkbox" name="' + this.checkBoxGeneName + '" class="' + this.checkBoxGeneName + '" value="' + this.averageName + '" checked> ' + this.averageName + ' <br>';
		this.addGeneSelection(this.averageName, true);
		for (var i = 0; i < this.genes.length; i++) {
			html += '<input type="checkbox" name="' + this.checkBoxGeneName + '" class="' + this.checkBoxGeneName + '" value="' + this.genes[i] + '"> ' + this.genes[i] + ' <br>';
			this.addGeneSelection(this.genes[i], false);
		}
		jQuery('#' + this.geneListField).html(html);
	}

	setGeneListField(id) {
		this.geneListField = id;
	}

	setAverageName(averageName) {
		this.averageName = averageName;
	}

	setCheckBoxGeneName(checkBoxGeneName) {
		this.checkBoxGeneName = checkBoxGeneName;
	}

	setExperimentTypeField(id) {
		this.experimentTypeField = id;
	}

	setExperimentNameField(id) {
		this.experimentNameField = id;
	}

	setZscoreField(id) {
		this.ZscoreField = id;
	}

	setFeatureCount(featureCount) {
		this.featureCount = featureCount;
	}

	setMaxMap(maxMap) {
		this.maxMap = maxMap;
	}

	setZscoreChecked() {
		jQuery('#' + this.ZscoreField).attr('checked','checked');
		this.setZscore(true);
	}

	setGeneListAction() {
		var thisExpressionVisualizations = this;
		jQuery('.' + this.checkBoxGeneName).click(function() {
			thisExpressionVisualizations.changeGenesSelected(this.value);
		});
		jQuery('.' + this.checkBoxGeneName + '_checkall').click(function() {
			thisExpressionVisualizations.checkUncheckAllGenes();
		});
	}

	setExpreimentTypeAction() {
		var thisExpressionVisualizations = this;
		jQuery('#' + this.experimentTypeField).change(function() {
			thisExpressionVisualizations.changeExperimentType(this.value);
		});
	}

	setExpreimentAction() {
		var thisExpressionVisualizations = this;
		jQuery('#' + this.experimentNameField).change(function() {
			thisExpressionVisualizations.changeExepreiment(this.value);
		});
	}

	setZscoreAction() {
		var thisExpressionVisualizations = this;
		jQuery('#' + this.ZscoreField).change(function() {
			thisExpressionVisualizations.startLoading();
			thisExpressionVisualizations.changeZscore(this.value);
		});
	}

	setExperiemtnsType(types) {
		this.experiemntTypes = types;
	}

	changeGenesSelected(gene_id) {
		this.startLoading();
		this.setGeneSelection(gene_id);
	}

	checkUncheckAllGenes() {
		this.startLoading();
		var nextCheckUncheckAll = this.getNextCheckUncheckAll();
		jQuery("." + this.checkBoxGeneName)[0].checked = nextCheckUncheckAll;
		for (var i = 0; i < this.genes.length; i++) {
			jQuery("." + this.checkBoxGeneName)[i + 1].checked = nextCheckUncheckAll;
		}
		this.setCheckUncheckAll();
	}

	changeExperimentType(value) {
		this.startLoading();
		this.experimentTypeSelected = value;
		var query                   ='<query model="genomic" view="ExperimentDescription.name" sortOrder="ExperimentDescription.name ASC" constraintLogic="A and B" >';
		query                       += '<constraint path="ExperimentDescription.expressionValue.type.name" op="=" value="' + this.experimentTypeSelected + '" code="A" />';
		query                       += '<constraint path="ExperimentDescription.expressionValue.gene" op="IN" value="' + this.getBagName() + '" code="B" />';
		query                       += '</query>';
		this.APIExecuteQuery(query, 'typeExperiment');
	}

	changeExepreiment(value, addID) {
		this.startLoading();
		this.setExperimentName(value)
		var query = '<query model="genomic" view="ExpressionValues.condition.name" sortOrder="ExpressionValues.condition.name ASC" constraintLogic="A and B and C" >';
		query     += '<constraint path="ExpressionValues.experiment.name" op="=" value="' + this.getExperimentName() + '" code="A" />';
		query     += '<constraint path="ExpressionValues.gene" op="IN" value="' + this.getBagName() + '" code="B" />';
		query     += '<constraint path="ExpressionValues.type.name" op="=" value="' + this.experimentTypeSelected + '" code="C" />';
		query     += '</query>';
		this.APIExecuteQuery(query, 'conditions');
	}

	getExpressionValues() {
		var query = '<query model="genomic" view="ExpressionValues.condition.name ExpressionValues.expressionValue ExpressionValues.gene.primaryIdentifier" sortOrder="ExpressionValues.gene.primaryIdentifier ASC ExpressionValues.condition.name ASC" constraintLogic="A and B and C" >';
		query     += '<constraint path="ExpressionValues.experiment.name" op="=" value="' + this.getExperimentName() + '" code="A" />';
		query     += '<constraint path="ExpressionValues.type.name" op="=" value="' + this.experimentTypeSelected + '" code="B" />';
		query     += '<constraint path="ExpressionValues.gene" op="IN" value="' + this.getBagName() + '" code="C" />';
		query     += '</query>';
		this.APIExecuteQuery(query, 'ExpressionValues');
	}

	fillTypeExperiment() {
		this.startLoading();
		var selected_type = '';
		for (var i = 0; i < this.experiemntTypes.length; i++) {
			if (this.experiemntTypes[i].name == defaultValues.type) {
				fillAOption(this.experimentTypeField, this.experiemntTypes[i].name, true);
				selected_type = this.experiemntTypes[i].name;
			} else {
				fillAOption(this.experimentTypeField, this.experiemntTypes[i].name, false);
			}
		}
		if (selected_type != '') {
			this.changeExperimentType(selected_type);
		}
	}

	fillExperiment(result) {
		var selected_experiment = '';
		jQuery('#' + this.experimentNameField).html('');
		for (var i = 0; i < result.length; i++) {
			if (result[i]['ExperimentDescription.name'].value == defaultValues.experiment) {
				fillAOption(this.experimentNameField, result[i]['ExperimentDescription.name'].value, true);
				selected_experiment = result[i]['ExperimentDescription.name'].value;
			} else {
				fillAOption(this.experimentNameField, result[i]['ExperimentDescription.name'].value, false);
			}
		}
		if (selected_experiment != '') {
			this.changeExepreiment(selected_experiment);
		} else {
			this.changeExepreiment(result[0]['ExperimentDescription.name'].value);
		}
	}

	fillConditions(result) {
		var conditions = [];
		if (result.length > 0) {
			conditions.push(result[0]['ExpressionValues.condition.name'].value);
			for (var i = 1; i < result.length; i++) {
				if (result[i]['ExpressionValues.condition.name'].value != result[i - 1]['ExpressionValues.condition.name'].value) {
					conditions.push(result[i]['ExpressionValues.condition.name'].value);
				}
			}
		}
		this.setConditions(conditions);
		this.getExpressionValues()
	}

	getArrayData(result) {
		var genes                  = this.getGenes();
		var conditions             = this.getConditions();
		var valid                  = true;
		var line                   = [];
		var conditionMeans         = [];
		var expressionValues       = [];
		var expressionValuesZscore = [];
		var means                  = {};

		for (var i = 0; i < conditions.length; i++) {
			line = [];
			for (var j = 0; j < genes.length; j++) {
				line.push(NaN);
			}
			expressionValues.push(line);
			expressionValuesZscore.push(line);
			conditionMeans.push(0);
		}

		if (result.length > 0) {
			var actual_condition = result[0]['ExpressionValues.condition.name'].value;
			var expression       = result[0]['ExpressionValues.expressionValue'].value;
			var actual_gene      = result[0]['ExpressionValues.gene.primaryIdentifier'].value;
			var sum              = 0;
			var posGene          = 0;
			var posCondition     = 0;
			var validGenes = 0;
			while (actual_gene != genes[posGene]) {
				posGene++;
				if (posGene == genes.length) {
					console.error('Error in data');
					break;
				}
			}
			while (actual_condition != conditions[posCondition]) {
				posCondition++;
				if (posCondition == conditions.length) {
					console.error('Error in data');
					valid = false;
					break;
				}

			}
			expressionValues[posCondition][posGene] = expression;
			var max                                 = expression;
			var min                                 = expression;
			sum                                     += expression;
			conditionMeans[posCondition]            += expression;
			validGenes++;
			posCondition++;
			for (var i = 1; i < result.length; i++) {
				var this_gene      = result[i]['ExpressionValues.gene.primaryIdentifier'].value;
				var this_condition = result[i]['ExpressionValues.condition.name'].value;
				expression         = result[i]['ExpressionValues.expressionValue'].value;
				while (this_gene != genes[posGene]) {
					means[genes[posGene]] = sum / validGenes;
					posGene++;
					validGenes            = 0;
					posCondition          = 0;
					sum                   = 0;
				}

				while (this_condition != conditions[posCondition]) {
					posCondition++;
					if (posCondition == conditions.length) {
						console.error('Error in data');
						valid = false;
						break;
						break;
					}
				}

				expressionValues[posCondition][posGene] = expression;
				conditionMeans[posCondition]            += expression;
				if (max < expression) { max = expression; }
				if (min > expression) { min = expression; }
				sum += expression;
				validGenes++;
				posCondition++;
				if (posCondition == conditions.length) {
					posCondition = 0;
				}
			}
			//Mean of last gene
			means[genes[posGene]] = sum / validGenes;
			for (var i = 0; i < conditionMeans.length; i++) {
				conditionMeans[i] = conditionMeans[i] /  genes.length;
			}
		} else {
			console.error('Error in data');
			valid = false;
		}

		if (this.verifyData(expressionValues, valid)) {
			this.setMax(max);
			this.setMin(min);
			this.setMeans(means);
			this.setConditionMeans(conditionMeans);
			this.setExpressionValues(expressionValues);
			this.drawCanvas();
		}
		this.finishLoading();
	}

	verifyData(expressionValues, valid) {
		if (!valid || expressionValues.length < 1) {
			jQuery('#heatmap_div').remove();
			jQuery('#expression_div').html('<i>Expression scores are not available</i>');
			return false;
		}

		if (this.featureCount > this.maxMap) {
			jQuery('#heatmap_div').remove();
			jQuery('#expression_div').html('<i>Too many elements, please select a subset to see the heat maps.</i>');
			return false;
		}

		return true
	}

	getResult(result, idCall) {
		switch(idCall) {
			case 'typeExperiment':
				this.fillExperiment(result)
			break;

			case 'conditions':
				this.fillConditions(result)
			break;

			case 'ExpressionValues':
				this.getArrayData(result)
			break;

			case 'getURL':
				this.getURL(result)
			break;
		}
	}

}

jQuery("#idSecundary").toggle("slow");

jQuery("#heatMapHideShowButton").click(function () {
	if(jQuery("#heatmapGraph").is(":hidden")) {
		jQuery("#oc").attr("src", "images/disclosed.gif");
	} else {
		jQuery("#oc").attr("src", "images/undisclosed.gif");
	}
	jQuery("#heatmapGraph").toggle("slow");
});

jQuery("#heatMapHideShowButtonSecond").click(function () {
	if(jQuery("#idSecundary").is(":hidden")) {
		jQuery("#ocsecundary").attr("src", "images/disclosed.gif");
	} else {
		jQuery("#ocsecundary").attr("src", "images/undisclosed.gif");
	}
	jQuery("#idSecundary").toggle("slow");
});

var geneProfileOptions = {
		fitLineStyle: "solid",
		graphOrientation: "vertical",
		graphType: "Line",
		lineType: "spline",
		showLegend: false
		};
var heatMapOptions = {
				graphType: 'Heatmap',
				dendrogramSpace: 6,
				smpDendrogramPosition: 'right',
				varDendrogramPosition: 'bottom',
				varLabelRotate: 45,
				centerData: false,
				missingDataColor: 'rgb(255, 255, 0)',
				autoExtend: true
			}
/* Init Grsphs */

let expressionVisualizations = new ExpressionVisualizations();

expressionVisualizations.setExperimentTypeField('experimentType');
expressionVisualizations.setExperimentNameField('experimentSelect');
expressionVisualizations.setZscoreField('Zscore');
expressionVisualizations.setGeneListField('geneListGeneProfile');
expressionVisualizations.setCheckBoxGeneName('geneCheckbox');


expressionVisualizations.setHeatMapDivId('set_canvas_heatMap');
expressionVisualizations.setHeatMapId('canvas_heatMap');


expressionVisualizations.setGeneProfileDivId('set_canvas_geneProfile');
expressionVisualizations.setGeneProfileId('canvas_geneProfile');



expressionVisualizations.setGeneProfileOptions(geneProfileOptions);
expressionVisualizations.setHeatMapOptions(heatMapOptions);


expressionVisualizations.setZscoreChecked();

expressionVisualizations.setExperiemtnsType(types);
expressionVisualizations.setBagName(bagName);
expressionVisualizations.setApiKey(api_key);
expressionVisualizations.setGenes(Genes);
expressionVisualizations.setFeatureCount(feature_count);
expressionVisualizations.setMaxMap(max_map);

expressionVisualizations.fillGeneList();

expressionVisualizations.setExpreimentTypeAction();
expressionVisualizations.setExpreimentAction();
expressionVisualizations.setZscoreAction();
expressionVisualizations.setGeneListAction();

expressionVisualizations.fillTypeExperiment();

/* Second graphs */

let expressionVisualizations_second = new ExpressionVisualizations();

expressionVisualizations_second.setExperimentTypeField('experimentType_secundary');
expressionVisualizations_second.setExperimentNameField('experimentSelect_secundary');
expressionVisualizations_second.setZscoreField('Zscore_second');
expressionVisualizations_second.setGeneListField('geneListGeneProfile_secondary');
expressionVisualizations_second.setCheckBoxGeneName('geneCheckbox_secondary');


expressionVisualizations_second.setHeatMapDivId('set_canvas_heatMap_secundary');
expressionVisualizations_second.setHeatMapId('canvas_heatMap_secundary');


expressionVisualizations_second.setGeneProfileDivId('set_canvas_geneProfile_secundary');
expressionVisualizations_second.setGeneProfileId('canvas_geneProfile_secundary');



expressionVisualizations_second.setGeneProfileOptions(geneProfileOptions);
expressionVisualizations_second.setHeatMapOptions(heatMapOptions);


expressionVisualizations_second.setZscoreChecked();

expressionVisualizations_second.setExperiemtnsType(types);
expressionVisualizations_second.setBagName(bagName);
expressionVisualizations_second.setApiKey(api_key);
expressionVisualizations_second.setGenes(Genes);
expressionVisualizations_second.setFeatureCount(feature_count);
expressionVisualizations_second.setMaxMap(max_map);

expressionVisualizations_second.fillGeneList();

expressionVisualizations_second.setExpreimentTypeAction();
expressionVisualizations_second.setExpreimentAction();
expressionVisualizations_second.setZscoreAction();
expressionVisualizations_second.setGeneListAction();

expressionVisualizations_second.fillTypeExperiment();