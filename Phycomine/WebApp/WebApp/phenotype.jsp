<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>

<!-- phenotype.jsp -->
<html:xhtml/>

<div class="body" id="phenotypes">

	<h2>PHENOTYPES</h2>

	<br><br><br>
	
	<div class="col-md-12">
		<div class="col-md-3">
			<div class="col-md-3">
				<h5>Organism:</h5>
			</div>
			<div class="col-md-9">
				<!--select id="organismSelect" class="form-control hidden"></select-->
				<div style="overflow-y: auto; height: 110px;" id="organismSelect">
					<!--div style="border-bottom: 1px solid #ddd;">
						<input type="checkbox" name="organismCheckBox"> Chlamydomonas reinhardtii <br>
					</div>
					<div style="border-bottom: 1px solid #ddd;">
						<input type="checkbox" name="organismCheckBox"> Chlorella vulgaris <br>
					</div-->
				</div>
			</div>
		</div>

		<div class="col-md-3">
			<div class="col-md-3">
				<h5>Experiment:</h5>
			</div>
			<div class="col-md-9">
				<!--select id="experimentSelect" class="form-control"></select-->
				<div style="overflow-y: auto; height: 110px;" id="experimentSelect"></div>
			</div>
		</div>

		<div class="col-md-3">
			<div class="col-md-3">
				<h5>Time:</h5>
			</div>
			<div class="col-md-9">
				<select id="timeMeasureSelect" class="form-control"></select>
			</div>
		</div>
	</div>

	<div style="height: 60px;" class="col-md-12"></div>

	<div class="col-md-12" style="height: 600px">
		<div id="canvas_phenotype_div" class="col-md-7">
			<!--canvas id="canvas_phenotype" width="500" height="550"></canvas-->
			<div id="chart_lines" style="height: 600px"></div>
		</div>
		<div>

			<div class="col-md-5">
				<div>
					<h4 class="col-md-6" style="border-bottom: 2px solid #ddd;">Measurement</h4>
					<h4 class="col-md-6" style="border-bottom: 2px solid #ddd;">Measurement Unit</h4>
				</div>
				<div id="phenotypeList" style="overflow-y: auto; height: 200px; min-width: 1%;">
				</div>
				<div style="height: 60px;"></div>
				<div>
					<h4 style="border-bottom: 2px solid #ddd;">Conditions</h4>
				</div>
				<div id="conditionList" style="overflow-y: auto; height: 200px; min-width: 1%;">
				</div>
			</div>


			<!--div class="col-md-2">
				<div>
					<h4 style="border-bottom: 2px solid #ddd;">Conditions</h4>
				</div>
				<div id="conditionList" style="overflow-y: scroll; max-height: 520px; min-width: 1%;">
				</div>
			</div>
			<div class="col-md-4">
				<div>
					<h4 class="col-md-4" style="border-bottom: 2px solid #ddd;">Measurement</h4>
					<h4 class="col-md-8" style="border-bottom: 2px solid #ddd;">Measurement Unit</h4>
				</div>
				<div id="phenotypeList" style="overflow-y: scroll; max-height: 520px; min-width: 1%;">
				</div>
			</div-->
		</div>
	</div>
</div>


<link rel="stylesheet" href="model/style/jquery-ui.css">
<link rel="stylesheet" href="model/style/canvasXpress.css" type="text/css"/>
<link rel="stylesheet" href="model/style/bootstrap.min.css">
<link rel="stylesheet" href="model/style/geneAtlasDisplayer.css">
<link rel="stylesheet" href="model/style/visualizations.css">

<script type="text/javascript" src="model/js/jquery-ui.js"></script>
<script type="text/javascript" src="model/js/bootstrap.min.js"></script>
<script type="text/javascript" src="model/js/libraryIntermine.js"></script>
<script type="text/javascript">
  var organismsDB  = ${organisms};
  var timeMeasures = ${timeMeasures};
  var webAppPath = "${WEB_PROPERTIES['webapp.path']}";
  var api_key    = '${APIKey}';
  var colorSpectrum = ["#ffe500", "#ff0000", "#005eff", "#af7a65", "#c27800", "#ffb53d", "#009999", "#ff007f", "#00ff00", "#ffc87c", "#ca6c6c", "#e50000", "#00ffb0", "#ce2323", "#186b2e", "#005b96", "#0f52ba", "#9d3133", "#f9b02e", "#3cff00", "#ff00f4", "#ffae00", "#000099", "#ff5600", "#7ccd7c", "#c0392b", "#9b0404", "#ff2500", "#ffc361", "#004953", "#400040", "#000000", "#ce0e2d", "#e8ca93", "#007f7f"];
</script>
<script src="https://canvasjs.com/assets/script/canvasjs.min.js"></script>
<script type="text/javascript" src="model/js/phenotype.js"></script>
<!-- /phenotype.jsp -->