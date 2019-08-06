<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ page import="java.net.URLEncoder" language="java" %>

<!-- visualizations.jsp -->

<html:xhtml />

<tiles:importAttribute />
<c:set var="MAX_CLUSTER" value="250" />
<c:set var="MAX_MAP" value="600" />
<c:set var="MAX_CONDITIONS_GENES" value="10000" />
<c:set var="MAX_DEFAULT_OPEN" value="100" />


<div class="body" id="visualizations">
  <div id="title">
    <p>
      <h2>
        Visualizations
      </h2>
    </p>
    <p>
      <i>
        <br/>Visualizations powered by
        <a href="http://www.canvasxpress.org">canvasXpress</a>.
      </i>
    </p>
    <br/>
  </div>
  <div id="heatMap"></div>
  <div id="scatterPlot"></div>
</div>

<link rel="stylesheet" href="model/style/visualizations.css">
<link rel="stylesheet" href="model/style/canvasXpress.css" type="text/css"/>

<script type="text/javascript" src="model/js/canvasXpress.js"></script>
<script type="text/javascript" src="model/js/libraryIntermine.js"></script>
<script type="text/javascript">
  var types                = ${type};
  var Genes                = ${gene};
  var defaultValues        = ${defaultValues};
  var bagName              = '${ListName}';
  var webAppPath           = "${WEB_PROPERTIES['webapp.path']}";
  var api_key              = '${APIKey}';
  var feature_count        = parseInt(${FeatureCount});
  var max_cluster          = parseInt(${MAX_CLUSTER});
  var max_map              = parseInt(${MAX_MAP});
  var max_consitions_genes = parseInt(${MAX_CONDITIONS_GENES});

  jQuery(document).ready(function () {
    jQuery("#heatMap").load("model/heatMap.html");
    jQuery("#scatterPlot").load("model/scatterplotGenes.html");
  });
</script>

<!-- /visualizations.jsp -->