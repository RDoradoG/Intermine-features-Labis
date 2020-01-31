/**
 * Query execute class.
 */
class QueryExecute{
	constructor() {
		this.api_key = '';
	}

	/**
	 * Set the API key.
	 * @param {String} key The API key.
	 */
	setApiKey(key){
		this.api_key = key;
	}

	/**
	 * Get the API key.
	 * @return {String} The API key.
	 */
	getApiKey(){
		return this.api_key
	}

	/**
	 * Execute a query in the data base.
	 * @param {String} query  The query.
	 * @param {String} idCall A id setted to the query.
	 * @param {number} sz     The max size of the answer
	 * @param {number} strt   The number of page
	 */
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

	/**
	 * Print the result of the query.
	 * @param  {array} result The answer of the query.
	 * @param  {String} idCall The id of the query.
	 */
	getResult(result, idCall) {
		console.log('There is not a callBack for: ' + idCall);
		console.log(result);
		console.log("---");
	}

	setChangeFunctions() {
		console.log("Ther is not fiels");
	}

}

class Organisms extends QueryExecute {
	constructor() {
		super();
		this.fieldOrganismsId    = '';
		this.timeMeasureFiledId  = '';
		this.timeMeasureSelected = '';
		this.organisms           = [];
		this.timeMeasures        = [];
		this.organismSelected    = {}
		this.taxes               = {time: {}}
	}

	setFieldOrganismsId(fieldOrganismsId) {
		this.fieldOrganismsId = fieldOrganismsId
	}

	setFieldTimeMeasureId(timeMeasureFiledId) {
		this.timeMeasureFiledId = timeMeasureFiledId;
	}

	setOrganisms(organisms) {
		this.organisms = organisms;
	}

	setTaxes(measureType, measure, rate, root) {
		if (!(measureType in this.taxes)) {
			this.taxes[measureType] = {};
		}
		this.taxes[measureType][measure] = rate;
	}

	setTimeMeasures(timeMeasures) {
		var measures = [];
		for (var i = 0; i < timeMeasures.length; i++) {
			measures.push(timeMeasures[i].timeMeasure);
			this.taxes.time[timeMeasures[i].timeMeasure] = timeMeasures[i].rate
		}
		this.timeMeasures = measures;
		this.fillTimeMeasureField();
	}

	setOrganimsSelected(value) {
		this.organismSelected[value] = !this.organismSelected[value];
	}

	getOrganimsSelected() {
		return this.organismSelected;
	}

	getSelectedConstraint(obj) {
		var constraint = '';
		for (var key in obj) {
			if (obj[key]) {
				constraint += '<value>' + key + '</value>';
			}
		}
		return constraint;
	}

	setTimeMeasureSelected(value) {
		this.timeMeasureSelected = value;
	}

	getTimeMeasureSelected() {
		return this.timeMeasureSelected;
	}

	fillOptionMultiSelect(fieldId, name, id, value, selected) {
		var div   = jQuery("<div></div>").attr("style", 'border-bottom: 1px solid #ddd;');
		var input = jQuery("<input />").attr("type", 'checkbox').attr('name', name).attr('value', id);
		var br    = jQuery("<br />");
		if (selected) {
			input.attr("checked", "checked");
		}
		div.append(input);
		div.append(value);
		div.append(br);
		jQuery('#' + fieldId).append(div);
	}

	fillOrganismsField() {
		var selected = true;
		for (var i = 0; i < this.organisms.length; i++) {
			this.fillOptionMultiSelect(this.fieldOrganismsId,  'organismCheckBox', this.organisms[i].taxonId, this.organisms[i].name, selected);
			this.organismSelected[this.organisms[i].taxonId] = false;
			if (selected) {
				this.organismSelected[this.organisms[i].taxonId] = true;
			}
			selected = false;
		}
		this.setExpeiments();
	}

	fillTimeMeasureField() {
		var selected = true;
		jQuery('#' + this.timeMeasureFiledId).html('');
		for (var i = 0; i < this.timeMeasures.length; i++) {
			fillAOption(this.timeMeasureFiledId, this.timeMeasures[i], selected);
			if (selected) {
				this.timeMeasureSelected = this.timeMeasures[i];
			}
			selected = false;
		}
	}
}

class Experiments extends Organisms {
	constructor() {
		super();
		this.fieldExperimentId   = '';
		this.experiments         = [];
		this.experimentSelected  = {};	
	}

	setFieldExperimentId(fieldExperimentId) {
		this.fieldExperimentId = fieldExperimentId;
	}

	setExpeiments() {
		var query = '<query model="genomic" view="ExperimentDescription.name" sortOrder="ExperimentDescription.experimentTitle ASC" >';
		query     += '<constraint path="ExperimentDescription.phenotypeValue.organism.taxonId" op="ONE OF">';
		query     += this.getSelectedConstraint(this.getOrganimsSelected()) + '</constraint>';
		query     += '</query>';
		this.APIExecuteQuery(query, {type: 'experiments', data: null});
	}

	setExperimentSelected(value) {
		this.experimentSelected[value] = !this.experimentSelected[value];
	}

	getExperimentSelected() {
		return this.experimentSelected;
	}

	fillExperimentsField(result) {
		jQuery('#' + this.fieldExperimentId).html('');
		var copy_result  = [];
		var selected     = [];
		var already_true = false;
		for (var i = (result.length - 1); i >= 0 ; i--) {
			var value_to_push = false;
			if (result[i]['ExperimentDescription.name'].value in this.experimentSelected) {
				value_to_push = this.experimentSelected[result[i]['ExperimentDescription.name'].value];
			} 
			already_true = (value_to_push) ? value_to_push : already_true;
			selected.push((i == 0 && !already_true) || value_to_push);
			copy_result.push(result[i]['ExperimentDescription.name'].value);
		}
		this.experimentSelected = {};
		for (var i = 0; i < copy_result.length; i++) {
			this.fillOptionMultiSelect(this.fieldExperimentId,  'experimentCheckBox', copy_result[i], copy_result[i], selected[i]);
			this.experimentSelected[copy_result[i]] = selected[i];
		}
		this.setIntermediateFunctions();
		this.setPhenotypeValues();
	}
}

class graprhs extends Experiments {
	constructor() {
		super();
		this.objectKeys                           = {phenotype: 'phenotype', condition: 'condition'};
		this.selection                            = {};
		this.selection[this.objectKeys.phenotype] = {};
		this.selection[this.objectKeys.condition] = {};
		this.graphOptions                         = {fitLineStyle: "solid", graphOrientation: "vertical", graphType: "Line", lineType: "spline", xAxisTitle: "Level", yAxisTitle: "Time", showLegend: false};
		this.data                                 = [];
		this.allLines                             = [];
		this.graphData                            = [];
		this.timeLine                             = [];
		this.dataBuffer                           = [];
		this.linesToHide                          = [];
		this.AllgraphData                         = [];
		this.linesToDelete                        = [];
		this.colorSpectrum                        = [];
		this.allLinesToShow                       = [];
		this.graph                                = null;
		this.actions                              = null;
		this.graphId                              = 'canvas_phenotype';
		this.checkPhenotypeGeneName               = "checkPhenotypeGeneName";
	}

	emptyLinesToHide() {
		this.linesToHide = [];
	}

	emptyLinesToDelete() {
		this.linesToDelete = [];
	}

	emptyLinesToShow() {
		this.allLinesToShow = [];
	}

	changeLinesToHide(){
		this.graphData = clone(this.AllgraphData);
		for (var phenotype in this.selection[this.objectKeys.phenotype]) {
			if (!this.selection[this.objectKeys.phenotype][phenotype]) {
				for (var i = (this.graphData.data.length - 1); i >= 0 ; i--) {
					if (this.graphData.data[i].phenotype == phenotype){
						this.graphData.data.splice(i,1);
					}
				}
			}
		}
		for (var condition in this.selection[this.objectKeys.condition]) {
			if (!this.selection[this.objectKeys.condition][condition]) {
				for (var i = (this.graphData.data.length - 1); i >= 0 ; i--) {
					if (this.graphData.data[i].condition == condition){
						this.graphData.data.splice(i,1);
					}
				}
			}
		}
		this.destroyGraph();
		this.drawGraph();
	}

	setColorSpectrum(colorSpectrum) {
		this.colorSpectrum = colorSpectrum;
	}

	prepareData(phenotypeValues, phenotypes, conditions) {
		var lineOptions      = {type: "line", markerType: "none"};
		var lineErrorOptions = {type: "error"};
		var line             = lineOptions;
		var lineError        = lineErrorOptions;
		var tempData         = {};
		var timeLineTmp      = [];
		this.data            = [];
		for (var i = 0; i < phenotypeValues.length; i++) {
			var theseTime                                                                                                                 = parseFloat(phenotypeValues[i].actual.time);
			timeLineTmp.pushIfNotExist(theseTime);
			tempData                                                                                                                      = addkey(tempData, phenotypeValues[i].taxon_id);
			tempData[phenotypeValues[i].taxon_id]                                                                                         = addkey(tempData[phenotypeValues[i].taxon_id], phenotypeValues[i].condition);
			tempData[phenotypeValues[i].taxon_id][phenotypeValues[i].condition]                                                           = addkey(tempData[phenotypeValues[i].taxon_id][phenotypeValues[i].condition], phenotypeValues[i].phenotype);
			tempData[phenotypeValues[i].taxon_id][phenotypeValues[i].condition][phenotypeValues[i].phenotype]                             = addkey(tempData[phenotypeValues[i].taxon_id][phenotypeValues[i].condition][phenotypeValues[i].phenotype], theseTime.toString());
			tempData[phenotypeValues[i].taxon_id][phenotypeValues[i].condition][phenotypeValues[i].phenotype][theseTime.toString()]       = {value: 0, min: 0, max:0};
			tempData[phenotypeValues[i].taxon_id][phenotypeValues[i].condition][phenotypeValues[i].phenotype][theseTime.toString()].value = phenotypeValues[i].actual.phenotypeValue;
			tempData[phenotypeValues[i].taxon_id][phenotypeValues[i].condition][phenotypeValues[i].phenotype][theseTime.toString()].min   = phenotypeValues[i].actual.maxPhenotypeValue;
			tempData[phenotypeValues[i].taxon_id][phenotypeValues[i].condition][phenotypeValues[i].phenotype][theseTime.toString()].max   = phenotypeValues[i].actual.minPhenotypeValue;
		}
		timeLineTmp.sort(function(a, b){return a - b;});
		var indexColorSpectrum = 0;
		for (var taxon in tempData) {
			for (var condition in tempData[taxon]) {
				for (var phenotype in tempData[taxon][condition]) {
					line.taxon_id          = lineError.taxon_id = taxon;
					line.condition         = lineError.condition = condition;
					line.phenotype         = lineError.phenotype = phenotype;
					line.name              = lineError.name = taxon + ':' + condition + ' | ' + phenotype;
					line.color             = lineError.color = this.colorSpectrum[indexColorSpectrum];
					indexColorSpectrum     = (indexColorSpectrum == this.colorSpectrum.length) ? 0 : indexColorSpectrum + 1;
					line.dataPoints        = [];
					lineError.dataPoints   = [];
					line.dataPoints.length = lineError.dataPoints.length = timeLineTmp.length;
					var row                = {y: null, x: ""};
					var rowError           = {y: [null, null], x: ""};
					var startPoint         = 0;
					var steps              = 0;
					var beforeElement      = null;
					for (var j = 0; j < timeLineTmp.length; j++) {
						if (timeLineTmp[j] in tempData[taxon][condition][phenotype]) {
							if (beforeElement != null) {
								var interest = (row.y - tempData[taxon][condition][phenotype][timeLineTmp[beforeElement]].value) / steps;
								for (var k = (beforeElement + 1); k < j; k++) {
									row.x                   = rowError.x = timeLineTmp[k];
									row.y                   = line.dataPoints[k - 1].value + interest;
									line.dataPoints[k]      = clone(row);
									lineError.dataPoints[k] = clone(rowError);
									row                     = {y: null, x: ""};
									rowError                = {y: [null, null], x: ""};
								}
								beforeElement = null;
							}
							row.x                   = rowError.x = timeLineTmp[j];
							row.y                   = tempData[taxon][condition][phenotype][timeLineTmp[j]].value;
							rowError.y[0]           = tempData[taxon][condition][phenotype][timeLineTmp[j]].min;
							rowError.y[1]           = tempData[taxon][condition][phenotype][timeLineTmp[j]].max;
							line.dataPoints[j]      = clone(row);
							lineError.dataPoints[j] = clone(rowError);
							row                     = {y: null, x: ""};
							rowError                = {y: [null, null], x: ""};

						} else {
							if (j > startPoint) {
								if (beforeElement != null) {
									steps++;
								} else {
									steps         = 2;
									beforeElement = j - 1;
								}
							} else {
								startPoint++;
								row.x                   = rowError.x = timeLineTmp[j];
								line.dataPoints[j]      = clone(row);
								lineError.dataPoints[j] = clone(rowError);
								row                     = {y: null, x: ""};
								rowError                = {y: [null, null], x: ""};
							}
						}
						row      = {y: null, x: ""};
						rowError = {y: [null, null], x: ""};
					}

					if (beforeElement != null) {
						for (var k = (beforeElement + 1); k < timeLineTmp.length; k++) {
							row.x                   = rowError.x = timeLineTmp[k];
							line.dataPoints[k]      = clone(row);
							lineError.dataPoints[k] = clone(rowError);
							row                     = {y: null, x: ""};
							rowError                = {y: [null, null], x: ""};
						}
					}
					this.data.push(clone(line));
					this.data.push(clone(lineError));
					line      = lineOptions;
					lineError = lineErrorOptions;
				}
			}
		}

		this.graphData = {
			animationEnabled: true,
			title:{
				text: "Phenotypes Level"
			},
			axisY:{
				title: "Level",
				includeZero: false
			},
			toolTip: {
				shared: true,
				contentFormatter: setTootlTip,
			},
			data: this.data
		};
		this.AllgraphData = clone(this.graphData);
		this.changeLinesToHide();
	}

	destroyGraph() {
		jQuery('#' + "canvas_phenotype_div").html('');
		jQuery('#' + "canvas_phenotype_div").html('<div id="chart_lines" style="height: 600px"></div>');
	}

	drawGraph() {
		var chart = new CanvasJS.Chart("chart_lines", this.graphData);
		chart.render();
		this.graph = chart;
	}
}

class PhenotypeValues extends graprhs {
	constructor() {
		super();
		this.phenotypes                           = [];
		this.conditions                           = [];
		this.measureType                          = [];
		this.phenotypeValues                      = [];
		this.checkboxNames                        = this.objectKeys;
		this.roots                                = {};
		this.measures                             = {};
		this.measuresSelectedObj                  = {};
		this.Initrow                              = {
			original: {
				time: 0,
				phenotypeValue: 0,
				maxPhenotypeValue: 0,
				minPhenotypeValue: 0,
				measure: '',
				timeMeasure: '',
			},
			actual: {
				time: 0,
				phenotypeValue: 0,
				maxPhenotypeValue: 0,
				minPhenotypeValue: 0,
				measure: '',
				timeMeasure: '',
			},
			measureType: '',
			condition: '',
			phenotype: '',
			SD: 0,
			taxon_id: ''
		};
	}

	setPhenotypeValues() {
		var query = '<query model="genomic" view="PhenotypeValues.phenotypeValue PhenotypeValues.phenotypeValueSD PhenotypeValues.time PhenotypeValues.experiment.name PhenotypeValues.condition.name PhenotypeValues.measure.measure PhenotypeValues.timeMeasure.timeMeasure PhenotypeValues.measure.measureType.measureType PhenotypeValues.phenotype.phenotype PhenotypeValues.organism.taxonId" sortOrder="PhenotypeValues.condition.name ASC" >';
		query     += '<constraint path="PhenotypeValues.organism.taxonId" op="ONE OF" >';
		query     += this.getSelectedConstraint(this.getOrganimsSelected()) + '</constraint>';
		query     += '<constraint path="PhenotypeValues.experiment.name" op="ONE OF" >';
		query     += this.getSelectedConstraint(this.getExperimentSelected()) + '</constraint>';
		query     += '</query>';
		this.APIExecuteQuery(query, {type: 'phenotypeValues', data: null});
	}

	resetTimeConfigRoot() {
		this.roots.time = this.getTimeMeasureSelected();
	}

	resetDataConfigRoot() {
		this.roots = this.measuresSelectedObj
		this.roots.time = this.getTimeMeasureSelected();
	}

	changeRootsConfig(key, value) {
		this.roots[key] = value;
	}

	setPhenotypes(result) {
		this.phenotypeValues = [];
		this.phenotypes      = {};
		this.conditions      = {};
		this.measureType     = [];
		for (var i = 0; i < result.length; i++) {
			if (!(result[i]['PhenotypeValues.phenotype.phenotype'].value in this.phenotypes)){
				this.phenotypes[result[i]['PhenotypeValues.phenotype.phenotype'].value] = {measureType: result[i]['PhenotypeValues.measure.measureType.measureType'].value, id: result[i]['PhenotypeValues.phenotype.phenotype'].value.replace(' ', '_').replace('/', '_')};
			}
			var condExp = result[i]['PhenotypeValues.condition.name'].value + ' - ' + result[i]['PhenotypeValues.experiment.name'].value;
			if (!(result[i]['PhenotypeValues.condition.name'].value in this.conditions)){
				this.conditions[condExp] = condExp;
			}
			this.measureType.pushIfNotExist(result[i]['PhenotypeValues.measure.measureType.measureType'].value);
			var row                        = clone(this.Initrow);
			row.original.phenotypeValue    = result[i]['PhenotypeValues.phenotypeValue'].value;
			row.original.maxPhenotypeValue = row.original.phenotypeValue + result[i]['PhenotypeValues.phenotypeValueSD'].value;
			row.original.minPhenotypeValue = row.original.phenotypeValue - result[i]['PhenotypeValues.phenotypeValueSD'].value;
			row.original.time              = result[i]['PhenotypeValues.time'].value;
			row.original.measure           = result[i]['PhenotypeValues.measure.measure'].value;
			row.original.timeMeasure       = result[i]['PhenotypeValues.timeMeasure.timeMeasure'].value;
			row.measureType                = result[i]['PhenotypeValues.measure.measureType.measureType'].value;
			row.condition                  = condExp;//result[i]['PhenotypeValues.condition.name'].value;
			row.phenotype                  = result[i]['PhenotypeValues.phenotype.phenotype'].value;
			row.SD                         = result[i]['PhenotypeValues.phenotypeValueSD'].value;
			row.taxon_id                   = result[i]['PhenotypeValues.organism.taxonId'].value;
			this.phenotypeValues.push(clone(row));
		}
		this.getmeasuresOfmesruesType(this.measureType.length);
	}

	getmeasuresOfmesruesType(i) {
		i--;
		this.measures[this.measureType[i]] = [];
		var query = '<query model="genomic" view="MeasureType.measureType MeasureType.measure.measure MeasureType.measure.rate MeasureType.measure.root" >';
		query     += '<constraint path="MeasureType.measureType" op="=" value="' + this.measureType[i] + '" code="A" />';
		query     += '</query>';
		this.APIExecuteQuery(query, {type: 'measureType', data: i});
	}

	getMeasursOfMeasuresTypeList(result, data) {
		for (var i = 0; i < result.length; i++) {
			this.measures[result[i]['MeasureType.measureType'].value].push(result[i]['MeasureType.measure.measure'].value);
			this.setTaxes(result[i]['MeasureType.measureType'].value, result[i]['MeasureType.measure.measure'].value, result[i]['MeasureType.measure.rate'].value, result[i]['MeasureType.measure.root'].value);
		}
		if (data > 0) {
			this.getmeasuresOfmesruesType(data);
		} else {
			this.emptyPhenotypeSelection();
			this.fillList(this.objectKeys.phenotype, this.phenotypes, 'phenotypeList', true);
			this.fillList(this.objectKeys.condition, this.conditions, 'conditionList', false);
			this.fillMeasuresFields();
			this.resetDataConfigRoot();
			this.setListFunctions();
			this.parameterizeData(this.roots);
		}
	}

	parameterizeData(objetive) {
		for (var i = 0; i < this.phenotypeValues.length; i++) {
			this.phenotypeValues[i].actual = this.changeParameter(this.phenotypeValues[i], objetive, 'timeMeasure', 'time', ['time'], 1);
			this.phenotypeValues[i].actual = this.changeParameter(this.phenotypeValues[i], objetive, 'measure', this.phenotypeValues[i].measureType, ['phenotypeValue', 'maxPhenotypeValue', 'minPhenotypeValue'], 3);
		}
		this.prepareData(this.phenotypeValues, this.phenotypes, this.conditions);
	}

	changeParameter(phenotypeValue, objetive, type, objType, whichValue, rounder) {
		if (phenotypeValue.actual[type] != objetive[objType]) {
			if (phenotypeValue.original[type] == objetive[objType]) {
				phenotypeValue.actual[type] = phenotypeValue.original[type];
				for (var i = 0; i < whichValue.length; i++) {
					phenotypeValue.actual[whichValue[i]] = phenotypeValue.original[whichValue[i]];
				}
			} else {
				phenotypeValue.actual[type] = objetive[objType];
				for (var i = 0; i < whichValue.length; i++) {
					var rootValue = phenotypeValue.original[whichValue[i]];
					if (phenotypeValue.original[type] != this.roots[objType]) {
						rootValue = rootValue * (this.taxes[objType][phenotypeValue.original[type]]);
					}
					phenotypeValue.actual[whichValue[i]] = roundNumber(rootValue / (this.taxes[objType][objetive[objType]]), rounder);
				}
			}
		}
		return phenotypeValue.actual
	}

	fillMeasuresFields(id) {
		for (var key in this.phenotypes) {
			this.fillMeasuresField(this.phenotypes[key].id, this.measures[this.phenotypes[key].measureType], this.phenotypes[key].measureType);
		}
	}

	fillMeasuresField(id, data, measureType) {
		var selected = true;
		for (var i = 0; i < data.length; i++) {
			fillAOption(id,  data[i], selected);
			if (selected) {
				this.measuresSelectedObj[measureType] = data[i];
			}
			selected = false;
		}
	}

	setMeasureSelection(id, value) {
		this.measuresSelectedObj[id] = value;
	}

	fillList(type, data, id, selectOpt) {
		var minsize = (selectOpt) ? 6 : 12;
		var maxsize = 12 - minsize;
		var html    = '';
		for (var key in data) {
			html += '<div style=" display: flex;  display: flex;"><div style="border-bottom: 1px solid #ddd; flex: 1; margin-top: 12px; padding: 5px;"><input type="checkbox" style="margin: auto;" name="' + this.checkboxNames[type] + '" class="col-md-1 ' + this.checkboxNames[type] + ' checkBoxPhenotype" value="' + key + '" checked><div class="col-md-11" style="padding: unset;">' + key + '</div></div>';
			if (selectOpt) {
				html += '<div style="border-bottom: 1px solid #ddd; flex: 1; padding: 5px;"><select id="' + data[key].id + '" name="' + data[key].measureType + '" class="form-control measureType" style="margin-top: 3px; margin-left: 1px;"></select></div>';
			}
			html += '</div>';
			this.addPhenotypeSelection(type, key);
		}
		jQuery('#' + id).html(html);
	}

	emptyPhenotypeSelection() {
		this.selection[this.objectKeys.phenotype] = {};
		this.selection[this.objectKeys.condition] = {};
	}

	addPhenotypeSelection(type, selection, opt) {
		this.selection[type][selection] = true;
	}

	changeCheckBox(name, value){
		this.selection[name][value] = !this.selection[name][value];
		this.changeLinesToHide();
	}

	changeAllCheckBox(type){
		this.selection[type]['All'] = !this.selection[type]['All'];
		var i                       = 0;
		for (key in this.selection[type]) {
			if (key == 'All') {continue;}
			this.selection[type][key]                         = this.selection[type]['All'];
			jQuery("." + this.checkboxNames[type])[i].checked = this.selection[type]['All'];
			i++;
			this.changeLinesToHide();
		}
	}

	verifyCheckBox(selection, className) {
		var checked = [];
		for (var key in selection) {
			if (selection[key]) {
				checked.push(key);
			}
			if (checked.length > 1) {break;}
		}
		if (checked.length < 2) {
			jQuery('[name ="' + className + '"][value="' + checked[0] + '"]').prop( "disabled", true );
		} else {
			jQuery('[name ="' + className + '"]').prop( "disabled", false );
		}
	}

	verifyCheckBoxPhenotype() {
		for (var type in this.selection) {
			var checked = [];
			for (var key in this.selection[type]) {
				if (this.selection[type][key]) {
					checked.push(key);
				}
				if (checked.length > 1) {break;}
			}
			if (checked.length < 2) {
				jQuery('[name ="' + this.checkboxNames[type] + '"][value="' + checked[0] + '"]').prop( "disabled", true );
			} else {
				jQuery('[name ="' + this.checkboxNames[type] + '"]').prop( "disabled", false );
			}
		}
	}

	setListFunctions() {
		var thisGeneralClass = this;
		thisGeneralClass.verifyCheckBoxPhenotype();
		jQuery('.checkBoxPhenotype').change(function() {
			thisGeneralClass.changeCheckBox(this.name, this.value);
			thisGeneralClass.verifyCheckBoxPhenotype();
		});
		jQuery('.measureType').change(function() {
			thisGeneralClass.setMeasureSelection(jQuery(this).attr('name'), this.value);
			thisGeneralClass.resetDataConfigRoot();
			thisGeneralClass.parameterizeData(thisGeneralClass.roots);
		});
	}

	setIntermediateFunctions() {
		var thisGeneralClass = this;
		thisGeneralClass.verifyCheckBox(thisGeneralClass.experimentSelected, 'experimentCheckBox');
		jQuery('[name ="experimentCheckBox"]').change(function() {
			thisGeneralClass.setExperimentSelected(this.value);
			thisGeneralClass.setPhenotypeValues();
			thisGeneralClass.verifyCheckBox(thisGeneralClass.experimentSelected, 'experimentCheckBox');
		});
	}

	
	setChangeFunctions() {
		var thisGeneralClass = this;
		thisGeneralClass.verifyCheckBox(thisGeneralClass.organismSelected, 'organismCheckBox');
		jQuery('[name ="organismCheckBox"]').change(function() {
			thisGeneralClass.setOrganimsSelected(this.value);
			thisGeneralClass.setExpeiments();
			thisGeneralClass.verifyCheckBox(thisGeneralClass.organismSelected, 'organismCheckBox');
		});
		jQuery('#' + this.timeMeasureFiledId).change(function() {
			thisGeneralClass.setTimeMeasureSelected(this.value);
			thisGeneralClass.resetTimeConfigRoot();
			thisGeneralClass.parameterizeData(thisGeneralClass.roots);
		});
	}

	getResult(result, idCall) {
		switch(idCall.type) {
			case 'experiments':
				this.fillExperimentsField(result)
			break;

			case 'phenotypeValues':
				this.setPhenotypes(result)
			break;

			case 'measureType':
				this.getMeasursOfMeasuresTypeList(result, idCall.data);
			break

			default:
				console.log("Option query does not exists.")
			break;
		}
	}
}

function roundNumber(num, r) {
	var multiplier = Math.pow(10, r);
	return Math.round(num * multiplier) / multiplier;
}

function inkeys(obj, compare) {
    for(key in obj) {
        if(key == compare) return true;
    }
    return false;
};

function addkey(obj, element) {
    if (!inkeys(obj, element)) {
        obj[element] = {}
    }
    return obj;
};

// check if an element exists in array using a comparer function
// comparer : function(currentElement)
Array.prototype.inArray = function(compare) {
    for(var i = 0; i < this.length; i++) {
        if(this[i] == compare) return true;
    }
    return false;
};

Array.prototype.pushIfNotExist = function(element) {
    if (!this.inArray(element)) {
        this.push(element);
    }
};

function Start() {
	let experiments = new PhenotypeValues();
	experiments.setApiKey(api_key);
	experiments.setColorSpectrum(colorSpectrum);
	experiments.setFieldExperimentId("experimentSelect");
	experiments.setFieldTimeMeasureId('timeMeasureSelect');
	experiments.setFieldOrganismsId("organismSelect");
	experiments.setOrganisms(organismsDB);
	experiments.setTimeMeasures(timeMeasures);
	experiments.fillOrganismsField();
	experiments.setChangeFunctions();
}

window.onload = function () {Start()}

function setTootlTip(e){
	var info         = [];
	var data         = {name: '', value: 0, color: ''};
	var tootlTipMssg = '';
	if (e.entries.length > 0) {
		tootlTipMssg = "<b>" + e.entries[0].dataPoint.x + "</b><br>";
		for (var i = 0; i < e.entries.length; i++) {
			if (!isNaN(e.entries[i].dataPoint.y) && toType(e.entries[i].dataPoint.y) != "array") {
				data.name  = e.entries[i].dataSeries.name;
				data.value = e.entries[i].dataPoint.y;
				data.color = e.entries[i].dataSeries.color;
				info.push(clone(data));
				data       = {name: '', value: 0, color: ''};
			}
		}
		for (var i = 0; i < e.entries.length; i++) {
			for(var j = 0; j < info.length; j++) {
				if (e.entries[i].dataSeries.name === info[j].name && toType(e.entries[i].dataPoint.y) == "array" ) {
					tootlTipMssg += "<span style=\"color:" + info[j].color + "\">" + info[j].name + "</span>: " + info[j].value + " <br><span style=\"color:#C0504E\">Error Line</span>: " + e.entries[i].dataPoint.y[0] + " - " + e.entries[i].dataPoint.y[1] + '<br>';
					break;
				}
			}
		}
	}
	return tootlTipMssg;
}

function toType(obj) {
  return ({}).toString.call(obj).match(/\s([a-zA-Z]+)/)[1].toLowerCase()
}