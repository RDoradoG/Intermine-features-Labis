<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ page import="java.net.URLEncoder" language="java" %>

<!-- scatterplotGenes.jsp -->
<html:xhtml />

<tiles:importAttribute />

<!--link rel="stylesheet" href="model/style/canvasXpress.css" type="text/css"/>
<script type="text/javascript" src="model/js/canvasXpress.min.js"></script-->

<div class="body" id="scatterplotGenes">
	<div id="scatterplotGenesContainer">
        <table>
          <tr>
            <td>
                <div style="padding: 0px 0px 5px 30px;">
                  
                  <span>Type:</span>
                  <select id="experimentType_scatterplotGenes_A"></select>

                  <span>Experiment:</span>
                  <select id="experimentSelect_scatterplotGenes_A"></select>

                  <span>Condition:</span>
                  <select id="conditionSelect_scatterplotGenes_A"></select>
                </div>                  
            </td>
          </tr>

          <tr>
            <td>
                <div style="padding: 0px 0px 5px 30px;">
                  
                  <span>Type:</span>
                  <select id="experimentType_scatterplotGenes_B"></select>

                  <span>Experiment:</span>
                  <select id="experimentSelect_scatterplotGenes_B"></select>

                  <span>Condition:</span>
                  <select id="conditionSelect_scatterplotGenes_B"></select>
                </div>
                                   
            </td>
          </tr>

          <tr>
            <td>
                <div id="set_canvas_scatterplotGenes">
                  <canvas id="scatterplotGenes_canvas" width="700" height="550"></canvas>
                </div>                      
            </td>
          </tr>

        </table>
    </div>
</div>

<script type="text/javascript">
	var types                              = ${type};
	var Genes                              = ${gene};
	var defaultValues                      = ${defaultValues};
	var bagName                            = '${ListName}';
	var webAppPath                         = "${WEB_PROPERTIES['webapp.path']}";
	var api_key                            = '${APIKey}';
	var feature_count                      = parseInt(${FeatureCount});
	var experimentSelectedScatterplotGenes = {};
	var typeSelectedScatterplotGenes       = {};
	var conditionScatterplotGenes          = {};
	var expressionsScatterplotGenes        = {};
	var typesId                            = ['A', 'B'];
</script>
<script type="text/javascript" src="model/js/libraryIntermine.js"></script>
<script type="text/javascript" src="model/js/scatterplotGenes.js"></script>