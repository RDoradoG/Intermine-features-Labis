/*===============================================================
=				Functionallity of Genes Network					=
=																=
▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒
= Labis - IQ, USP. São Paulo									=
= @author Rodrigo Dorado						█║█║║█║█║█║║█	=
===============================================================*/

jQuery('#familyTypeNetwork').change(function() {
	familyTypeNetworkChange(jQuery(this).val());
});

jQuery('#geneFamilyDataset').change(function() {
	geneFamilyDatasetChange(jQuery(this).val());
});

jQuery('#experimentTypeNetwork').change(function() {
	ChangeExperimentType_network(jQuery(this).val());
});

jQuery('#experimentNameNetwork').change(function() {
	ChangeExepreimentName_network(jQuery(this).val());
});

jQuery('#experimentCondicionNetwork').change(function() {
	ChangeConditionName_network(jQuery(this).val());
});

jQuery('#neighborhoodButtonList').click(function() {
	redirectBag('hrr');
});

jQuery('#GraphML').click(function() {
	exportGraphML();
});

jQuery('#clusterButtonList').click(function() {
	redirectBag('hcca');
});

jQuery('#neighborhoodButtonNetwork').click(function() {
	inClick_network('hrr');
});

jQuery('#clusterButtonNetWork').click(function() {
	inClick_network('hcca');
});

/*----------  Set the trigger to the radio buttons of the order by option  ----------*/
jQuery("input[name='networkOption']").change(function() {
	networkOption[jQuery(this).val()] = !networkOption[jQuery(this).val()]
	drawNet(typeNetwork);
});

function init_netWorkScript(){
	builtNetInfo('hrr');
	builtNetInfo('hcca');
	if (nets.hrr.length > 0) {
		setHidden('hrr_not_exists', true);
	} else {
		setHidden('hrr_exists', true);
	}

	if (nets.hcca.length > 0) {
		setHidden('hcca_not_exists', true);
	} else {
		setHidden('hcca_exists', true);
	}
}


function setHidden(id, value) {
	jQuery("#" + id).attr("hidden", value);
}

function geneFamilyDatasetChange(value) {
	geneFamilyDataset = value;
	getfamiliesOfGenes(geneFamily, geneFamilyDataset, false);
}

function familyTypeNetworkChange(value) {
	if (value == 'EggNOG') {
		setDisabled_network('taxonON');
	} else {
		setDisabled_network('taxonOFF');
	}
	geneFamily = value
	getfamiliesOfGenes(geneFamily, geneFamilyDataset, false);
}

function setLoading(value) {
	jQuery("input[name='networkOption']").attr("disabled", value);
	jQuery(".networkClass").attr("disabled", value);
	if (value) {
		openModal("loading_network");
	} else {
		if (geneFamily != 'EggNOG') {
			setDisabled_network('taxonOFF');
		}
		closeModal("loading_network");
	}
}

function inClick_network(type){
	setLoading(true);
	typeNetwork            = type;
	var networkTitleBuffer = networkTitle.replace('{{genes_number}}', geneList[typeNetwork].nodes.length);
	jQuery('#networkTitle').html("");
	jQuery('#networkTitle').html(networkTitleBuffer);
	openModal("network");
	var selected           = true;
	jQuery('#geneFamilyDataset').html('');
	for (var i = 0; i < geneFamilyDatasets.length; i++) {
		fillAOption("geneFamilyDataset", geneFamilyDatasets[i], selected);
		selected = false;
	}
	geneFamilyDataset = geneFamilyDatasets[0];
	var selected      = true;
	jQuery('#familyTypeNetwork').html('');
	for (var i = 0; i < geneFamilies.length; i++) {
		fillAOption("familyTypeNetwork", geneFamilies[i], selected);
		selected = false;
	}
	geneFamily = geneFamilies[0];
	getfamiliesOfGenes(geneFamily, geneFamilyDataset, true);
}

function setDisabled_network(networkOption, opt) {
	switch(networkOption) {
		case 'orthologs':
			disabledElement('expressionValuesOptions', true);
			disabledElement('geneFamiliesOptions', true);
			disabledElement('geneFamiliesOptionsTaxon', true);
		break;

		case 'geneFamilies':
			disabledElement('expressionValuesOptions', true);
			disabledElement('geneFamiliesOptions', false);
			familyTypeNetworkChange(jQuery('#familyTypeNetwork').val());
		break;

		case 'expressionValues':
			disabledElement('expressionValuesOptions', false);
			disabledElement('geneFamiliesOptions', true);
			disabledElement('geneFamiliesOptionsTaxon', true);
		break;

		case 'taxonON':
			disabledElement('geneFamiliesOptionsTaxon', false);
		break;

		case 'taxonOFF':
			disabledElement('geneFamiliesOptionsTaxon', true);
		break;
	}
}

function disabledElement(id, val) {
	jQuery("select[name='" + id + "']").attr("disabled", val);
}

function getfamiliesOfGenes(gene_family, Dataset, firstTime) {
	var listGenes = builtGeneListCondition(typeNetwork);
	var query     = '<constraint path="Gene.primaryIdentifier" op="ONE OF" code="A" >';
	query         += listGenes;
	query         += '</constraint>'
	query         += '<constraint path="Gene.geneFamily.type.name" op="=" value="' + gene_family + '" code="B" />'
	var queryInit = '<query model="genomic" view="Gene.primaryIdentifier Gene.geneFamily.name" sortOrder="Gene.primaryIdentifier ASC" constraintLogic="A and B';
	if (gene_family == 'EggNOG') {
		query     += '<constraint path="Gene.geneFamily.dataset.name" op="=" value="' + Dataset + '" code="C" />'
		queryInit += ' and C';
	}
	queryInit += '">';
	query     = queryInit + query + '</query>';
	if (firstTime) {
		APIExecuteQuery(query, 'firstGeneFamily', getResultAPINetwork);
	} else {
		APIExecuteQuery(query, 'geneFamily', getResultAPINetwork);
	}
}

function ChangeExperimentType_network(value) {
	setLoading(true);
	jQuery('#experimentTypeNetwork').val(value);
	experimentTypeSelected = value;
	var query              = '<query model="genomic" view="ExperimentDescription.name" sortOrder="ExperimentDescription.name ASC" >';
	query                  += '<constraint path="ExperimentDescription.expressionValue.type.name" op="=" value="' + experimentTypeSelected + '" code="A" />';
	query                  += '</query>';
	APIExecuteQuery(query, 'typeExperiment', getResultAPINetwork);
}

function ChangeExepreimentName_network(value) {
	setLoading(true);
	jQuery('#experimentNameNetwork').val(value);
	experimentNameSelected = value;
	var query              = '<query model="genomic" view="ExpressionValues.condition.name" sortOrder="ExpressionValues.condition.name ASC" constraintLogic="A and B" >';
	query                  += '<constraint path="ExpressionValues.experiment.name" op="=" value="' + experimentNameSelected + '" code="A" />';
	query                  += '<constraint path="ExpressionValues.type.name" op="=" value="' + experimentTypeSelected + '" code="B" />';
	query                  += '</query>';
	APIExecuteQuery(query, 'nameExperiment', getResultAPINetwork);
}

function ChangeConditionName_network(value) {
	setLoading(true);
	jQuery('#experimentCondicionNetwork').val(value);
	conditionSelected = value;
	var listGenes     = builtGeneListCondition(typeNetwork);
	var query         = '<query model="genomic" view="ExpressionValues.gene.primaryIdentifier ExpressionValues.expressionValue" sortOrder="ExpressionValues.gene.primaryIdentifier ASC" constraintLogic="A and B and C and D" >';
	query             += '<constraint path="ExpressionValues.type.name" op="=" value="' + experimentTypeSelected + '" code="A" />';
	query             += '<constraint path="ExpressionValues.experiment.name" op="=" value="' + experimentNameSelected + '" code="B" />';
	query             += '<constraint path="ExpressionValues.condition.name" op="=" value="' + conditionSelected + '" code="C" />';
	query             += '<constraint path="ExpressionValues.gene.primaryIdentifier" op="ONE OF" code="D">';
	query             += listGenes
	query             += '</constraint>'
	query             += '</query>';
	APIExecuteQuery(query, 'allExpressionsValues', getResultAPINetwork);
}

function builtGeneListCondition(key) {
	var result = '';
	for (var i = 0; i < geneList[key].nodes.length; i++) {
		result +=  '<value>' + geneList[key].nodes[i].name + '</value>'
	}
	return result;
}

function fillTypeExperiment(result) {
	var selected = true;
	jQuery('#experimentTypeNetwork').html('');
	for (var i = 0; i < experimentTypes.length; i++) {
		fillAOption("experimentTypeNetwork", experimentTypes[i], selected);
		selected = false;
	}
	if (!selected) {
		ChangeExperimentType_network(experimentTypes[0]);
	}
}

function resultTypeExperiment_network(result) {
	var selected = true;
	jQuery('#experimentNameNetwork').html('');
	for (var i = 0; i < result.length; i++) {
		fillAOption('experimentNameNetwork', result[i]['ExperimentDescription.name'].value, selected);
		selected = false;
	}
	if (!selected) {
		ChangeExepreimentName_network(result[0]['ExperimentDescription.name'].value);
	}
}

function resultNameExperiment_network(result) {
	var selected = true;
	jQuery('#experimentCondicionNetwork').html('');
	for (var i = 0; i < result.length; i++) {
		fillAOption('experimentCondicionNetwork', result[i]['ExpressionValues.condition.name'].value, selected);
		selected = false;
	}
	if (!selected) {
		ChangeConditionName_network(result[0]['ExpressionValues.condition.name'].value);
	}
}

function resultGeneFamily_network(result, to_continue) {
	resetSahpesPatterns();
	for (var i = 0; i < geneList[typeNetwork].nodes.length; i++) {
		var family = defaultValue;
		for (var j = 0; j < result.length; j++) {
			if (result[j]['Gene.primaryIdentifier'].value == geneList[typeNetwork].nodes[i].name) {
				var family = result[j]['Gene.geneFamily.name'].value;
				break;
			}
		}
		var shapePattern = getShapePatternNode(family);
		geneList[typeNetwork].nodes[i].family  = family;
		geneList[typeNetwork].nodes[i].shape   = shapePattern[0];
		geneList[typeNetwork].nodes[i].pattern = shapePattern[1];
	}

	if (to_continue) {
		fillTypeExperiment();
	} else {
		drawNet(typeNetwork);
		setLoading(false);
	}
}

function resultAllExpressionsValues_network(result) {
	for (var i = 0; i < geneList[typeNetwork].nodes.length; i++) {
		geneList[typeNetwork].nodes[i].expressionValue = defaultValue;
		for (var j = 0; j < result.length; j++) {
			if (result[j]['ExpressionValues.gene.primaryIdentifier'].value == geneList[typeNetwork].nodes[i].name) {
				geneList[typeNetwork].nodes[i].expressionValue = result[j]['ExpressionValues.expressionValue'].value
				break;
			}
		}
	}
	drawNet(typeNetwork);
	setLoading(false);
}

function getResultAPINetwork(result, idCall) {
	switch (idCall) {
		case 'typeExperiment':
			resultTypeExperiment_network(result);
		break;

		case 'nameExperiment':
			resultNameExperiment_network(result);
		break

		case 'allExpressionsValues':
			resultAllExpressionsValues_network(result);
		break;

		case 'firstGeneFamily':
			resultGeneFamily_network(result, true);
		break;

		case 'geneFamily':
			resultGeneFamily_network(result, false);
		break;
	}
}

function builtNet(key, rootNode, expressionValues) {
	var net = nets[key];
	if (rootNode == null) {
		rootNode = net[0]['gene_a'];
	}
}

function getColorNode(ortholog) {
	if (!(ortholog in colorOrtholog)) {
		colorOrtholog[ortholog] = nodesColors[countNodeColors];
		countNodeColors++;
	}
	return colorOrtholog[ortholog];
}

function getShapePatternNode(family) {
	if (!(family in shapePatternFamily)) {
		shapePatternFamily[family] = [shapes[countNodeShapes], patterns[countNodePatterns]];
		countNodeShapes++;
		if (countNodeShapes >= shapes.length) {
			countNodeShapes = 0;
			countNodePatterns++;
		}
	}
	return shapePatternFamily[family];
}

function resetSahpesPatterns() {
	countNodeShapes    = 1;
	countNodePatterns  = 0;
	shapePatternFamily = {};
}


function builtNetInfo(key) {
	var net       = nets[key];
	var nodes_dic = {};
	var edgeColor;
	var nodeColor;
	for (var i = 0; i < net.length; i++) {
		nodes_dic[net[i]['gene_a']] = {
			id: net[i]['gene_a'],
			db_id: net[i]['id_a'],
      		name: net[i]['gene_a'],
      		ortholog: net[i]['ortholog_a'],
      		expressionValue: defaultValue,
      		family: defaultValue,
      		hideLabel: false,
      		color: getColorNode(net[i]['ortholog_a']),
      		shape: deafultNodeShape,
			pattern: deafultNodePattern
		};

		nodes_dic[net[i]['gene_b']] = {
			id: net[i]['gene_b'],
			db_id: net[i]['id_b'],
      		name: net[i]['gene_b'],
      		ortholog: net[i]['ortholog_b'],
      		expressionValue: defaultValue,
      		family: defaultValue,
      		hideLabel: false,
      		color: getColorNode(net[i]['ortholog_b']),
      		shape: deafultNodeShape,
			pattern: deafultNodePattern
		};

		edgeColor = edgesColors[Math.floor(net[i]['rank'] / 10)];

		geneList[key].edges.push({
			id1: net[i]['gene_a'],
			id2: net[i]['gene_b'],
			rank: net[i]['rank'],
			color: edgeColor
		});
	}

	for (key_dic in nodes_dic) {
		geneList[key].nodes.push(nodes_dic[key_dic]);
	}
}

function getListOnly(key) {
	var listToSub   = '';
	for (var i = 0; i < geneList[key].nodes.length; i++) {
		if (listToSub == '') {
			listToSub = geneList[key].nodes[i].db_id;
		} else {
			listToSub += ' ' + geneList[key].nodes[i].db_id;
		}
	}
	return listToSub
}

function redirectBag(type) {
	if (bagNameExist[type].set) {
		var url_parts    = window.location.href.split('/')
		var new_location = url_parts[0] + '/' + url_parts[1] + '/' + url_parts[2] + '/' + url_parts[3] + '/bagDetails.do?scope=all&bagName=' + bagNameExist[type].name
		window.location.replace(new_location);
	} else {
		var listToSub = getListOnly(type);
		jQuery('#geneList_matchIDs').val(listToSub);
		jQuery('#geneList_newBagName').val(bagNameExist[type].name);
		document.getElementById("Network_bagUploadConfirmForm").submit();
	}
}

function drawNet(key) {
	rootNode    = PrimaryIdentifier;
	jQuery('#set_canvas_div').html('');
	jQuery('#set_canvas_div').html('<canvas id="canvas_hccaHrr" width="1000" height="750"></canvas>');
	var options = {
		barLollipopOpen: false,
		calculateLayout: false,
		confidenceIntervalColor: "rgb(50,50,50)",
		fitLineStyle: "solid",
		graphType: "Network",
		"layoutConfig": [],
		legendFontSize: 0,
		nodeSize: 10,
		networkNodeMinDistance: 12,
		showLegend: false,
		nodeFontColor: "rgb(29,34,43)",
		disableToolbar: true,
		disableConfigurator: true,
		networkRoot: rootNode,
		nodeScaleFontFactor: 0.75,
		standardDeviationType: "unbiased",
		subtitleFontSize: 26,
		summaryType: "network",
		swimHigh: false,
		zoom: 0.5
	};

	nodesDrawed = [];
	for (var i = 0; i < geneList[key].nodes.length; i++) {
		newNode = clone(geneList[key].nodes[i]);
		if (!networkOption['orthologs']) {
			newNode.color = deafultNodeColor;
		}
		if (!networkOption['geneFamilies']) {
			newNode.shape   = deafultNodeShape;
			newNode.pattern = deafultNodePattern;
		}
		nodesDrawed.push(newNode);
	}

	if (networkOption['expressionValues']) {
		options['sizeNodeBy'] = "expressionValue";
	}

	hm_cl = new CanvasXpress("canvas_hccaHrr", {
			nodes: nodesDrawed,
			edges: geneList[key].edges
		}, options, {
			mousemove: function(o, e, t) {
			    var info = showInfo(o);
    			t.showInfoSpan(e, info);
			},
		});

	hm_cl.draw();
}

function exportGraphML () {
	let graphMLNetwork = new GraphMLNetwork(nodesDrawed, geneList[typeNetwork].edges, false, typeNetwork);
	graphMLNetwork.setKeySourceEdge('id1');
	graphMLNetwork.setKeyTargetEdge('id2');
	graphMLNetwork.setGraphOtions(networkOption);
	graphMLNetwork.set_Relation(shapes, graphMLShapes, 'shapes');
	graphMLNetwork.set_Relation(patterns, graphMLPatterns, 'patterns');
	graphMLNetwork.set_Relation(edgesColors, edgeColorsGraphMl, 'edgeColors');
	graphMLNetwork.set_key('node', 'ortholog', 'Ortholog', 'string', '-');
	graphMLNetwork.set_key('node', 'family', 'Family', 'string', '-');
	graphMLNetwork.set_key('node', 'expressionValue', 'Expression', 'string', '-');
	graphMLNetwork.set_key('edge', 'rank', 'Rank', 'string', '');
	graphMLNetwork.builtGraphML();
	graphMLNetwork.builtGrephMLContent();
	graphMLNetwork.download_file();
}

function showInfo(obj) {
	for (key in obj) {
		switch (key) {
			case 'nodes':
				return showNodeInfo(obj[key][0]);
			break;

			case 'edges':
				return showEdgeInfo(obj[key][0]);
			break;
		}
		break;
	}
}

function showNodeInfo(node) {
	var family = (node.family == defaultValue) ? '-' : node.family;
	var expressionValue = (node.expressionValue == defaultValue) ? '-' : node.expressionValue;
	return "<b>Gene Id:</b> " + node.name + "<br><br><b>Family:</b> " + family + "<br><br><b>Ortholog:</b> " + node.ortholog + "<br><br><b>Expression Value:</b> " + expressionValue
}

function showEdgeInfo(edge) {
	return "<b>First Gene:</b> " + edge.id1 + "<br><br><b>Second Gene:</b> " + edge.id2 + "<br><br><b>Rank:</b> " + edge.rank
}

init_netWorkScript();

class GraphMLNetwork {

	constructor(nodes, edges, directed, id) {
		this.keyId        = 2;
		this.content      = '';
		this.graphKeys    = [];
		this.graphNodes   = [];
		this.graphEdges   = [];
		this.shapes       = {};
		this.keys         = {};
		this.patterns     = {};
		this.edgeColors   = {};
		this.graphOptions = {};
		this.id           = id;
		this.nodes        = nodes;
		this.edges        = edges;
		this.directed     = directed;
	}

	setGraphOtions(graphOptions) {
		this.graphOptions = graphOptions;
	}

	set_Relation(original, newones, key) {
		for (var i = 0; i < original.length; i++) {
			this[key][original[i]] = newones[i]
		}
	}

	get_header() {
		var header = '<?xml version="1.0" encoding="UTF-8" standalone="no"?>\n';
		header     += '<graphml xmlns="http://graphml.graphdrawing.org/xmlns" ';
		header     += 'xmlns:java="http://www.yworks.com/xml/yfiles-common/1.0/java" ';
		header     += 'xmlns:sys="http://www.yworks.com/xml/yfiles-common/markup/primitives/2.0" ';
		header     += 'xmlns:x="http://www.yworks.com/xml/yfiles-common/markup/2.0" ';
		header     += 'xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" ';
		header     += 'xmlns:y="http://www.yworks.com/xml/graphml" ';
		header     += 'xmlns:yed="http://www.yworks.com/xml/yed/3" ';
		header     += 'xsi:schemaLocation="http://graphml.graphdrawing.org/xmlns http://www.yworks.com/xml/schema/graphml/1.1/ygraphml.xsd">\n';
		header     += '<!--Created by phycomine -->\n';
		header     += '\t<key for="node" id="d0" yfiles.type="nodegraphics"/>\n';
		header     += '\t<key for="edge" id="d1" yfiles.type="edgegraphics"/>\n';
		return header
	}

	get_graphTag() {
		var direc;
		if (this.directed) {
			direc = 'directed';
		} else {
			direc = 'undirected';
		}
		return '\t<graph id="' + this.id + '" edgedefault="' + direc + '" >\n'
	}

	get_foodFile() {
		return '\t</graph>\n</graphml>';
	}

	set_key(aim, source, attribute, type, default_value) {
		var id = 'd' + this.keyId;
		this.keyId++;
		this.keys[id] = {
			aim: aim,
			source: source,
			attribute: attribute,
			type: type,
			default_value: default_value
		}
	}

	setKeySourceEdge(value) {
		this.keySourceEdge = value;
	}

	setKeyTargetEdge(value) {
		this.keyTargetEdge = value;
	}

	addContent(value) {
		this.content += value;
	}

	emptyContent() {
		this.content = '';
	}

	get_content() {
		return this.content;
	}

	builtGrephMLContent() {
		this.emptyContent();
		this.addContent(this.get_header());
		for (var i = 0; i < this.graphKeys.length; i++) {
			this.addContent(this.graphKeys[i]);
		}
		this.addContent(this.get_graphTag());
		for (var i = 0; i < this.graphNodes.length; i++) {
			this.addContent(this.graphNodes[i]);
		}
		for (var i = 0; i < this.graphEdges.length; i++) {
			this.addContent(this.graphEdges[i]);
		}
		this.addContent(this.get_foodFile());
	}

	builtGraphML() {
		this.builtKeys();
		this.builtNodes();
		this.builtEdges();
	}

	builtKeys() {
		this.graphKeys = [];
		for (var key in this.keys) {
			this.builtKey(key);
		}
	}

	builtKey(key) {
		var new_key = '\t<key id="' + key + '" for="' + this.keys[key].aim + '" attr.name="' + this.keys[key].attribute + '" attr.type="' + this.keys[key].type + '"';
		if (this.keys[key].default_value != '') {
			new_key += '>\n';
			new_key += '\t\t<default>' + this.keys[key].default_value + '</default>\n';
			new_key += '\t</key>\n';
		} else {
			new_key += '/>\n';
		}
		this.graphKeys.push(new_key);
	}

	builtNodes() {
		this.graphNodes = [];
		for (var i = 0; i < this.nodes.length; i++) {
			this.builtNode(i);
		}
	}

	builtNode(i) {
		var keysChoosen = this.getKeyOF('aim', 'node');
		var new_node    = '\t\t<node id="' + this.nodes[i].id + '">\n';
		for (var j = 0; j < keysChoosen.length; j++) {
			new_node += '\t\t\t<data key="' + keysChoosen[j].key + '">' + this.nodes[i][keysChoosen[j].data.source] + '</data>\n';
		}
		new_node += '\t\t</node>\n';
		this.graphNodes.push(new_node);
	}

	getNodeInfo(height, width, x, y, color, label, shape, lineType, widthLine) {
		var new_node_info = '\t\t\t<data key="d0">\n';
		new_node_info     += '\t\t\t\t<y:ShapeNode>\n';
		new_node_info     += '\t\t\t\t\t<y:Geometry height="' + height + '" width="' + width + '" x="' + x + '" y="' + y + '"/>\n';
		new_node_info     += '\t\t\t\t\t<y:Fill color="' + color + '"/>\n';
		new_node_info     += '\t\t\t\t\t<y:BorderStyle type="' + lineType + '" width="' + widthLine + '"/>\n';
		new_node_info     += '\t\t\t\t\t<y:NodeLabel>' + label + '</y:NodeLabel>\n';
		new_node_info     += '\t\t\t\t\t<y:Shape type="' + shape + '"/>\n';
		new_node_info     += '\t\t\t\t</y:ShapeNode>\n';
		new_node_info     += '\t\t\t</data>\n';
     	return new_node_info;
	}

	builtEdges() {
		this.graphEdges = [];
		for (var i = 0; i < this.edges.length; i++) {
			this.builtEdge(i);
		}
	}

	builtEdge(i) {
		var keysChoosen = this.getKeyOF('aim', 'edge');
		var new_edge    = '\t\t<edge id="e_' + i + '" source="' + this.edges[i][this.keySourceEdge] + '" target="' + this.edges[i][this.keyTargetEdge] + '">\n';
		for (var j = 0; j < keysChoosen.length; j++) {
			new_edge += '\t\t\t<data key="' + keysChoosen[j].key + '">' + this.edges[i][keysChoosen[j].data.source] + '</data>\n';
		}
		new_edge += '\t\t</edge>\n';
		this.graphEdges.push(new_edge);
	}

	getEdgeInfo(color) {
		var new_edge_info = '\t\t\t<data key="d1">\n';
		new_edge_info     += '\t\t\t\t<y:PolyLineEdge>\n';
		new_edge_info     += '\t\t\t\t\t<y:LineStyle color="' + color + '"/>\n';
		new_edge_info     += '\t\t\t\t\t<y:Arrows source="none" target="none"/>\n';
		new_edge_info     += '\t\t\t\t\t<y:BendStyle smoothed="false"/>\n';
		new_edge_info     += '\t\t\t\t</y:PolyLineEdge>\n';
		new_edge_info     += '\t\t\t</data>\n';
     	return new_edge_info;
	}

	getKeyOF (attr, value) {
		var keysChoosen = [];
		for (var key in this.keys) {
			if (this.keys[key][attr] == value) {
				keysChoosen.push({'key': key, 'data': this.keys[key]});
			}
		}
		return keysChoosen;
	}

	download_file() {
		var text = this.get_content();
		var blob = new Blob([text], {type: "text/xml"});
   		saveAs(blob, "testfile1.graphml");
	}
}