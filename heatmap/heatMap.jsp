<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ page import="java.net.URLEncoder" language="java" %>

<!-- heatMap.jsp -->
<html:xhtml />

<tiles:importAttribute />

<!--[if IE]><script type="text/javascript" src="model/canvasXpress/js/excanvas.js"></script><![endif]-->
<script type="text/javascript" src="model/canvasXpress/js/canvasXpress.min.js"></script>

<div class="body" id="expression_div">

<script type="text/javascript" charset="utf-8">
jQuery(document).ready(function () {
    var feature_count = parseInt(${FeatureCount});
    if (feature_count > 100) {
        jQuery("#heatmapGraph").hide();
    } else {
        jQuery("#heatmapGraph").show();
    }
    jQuery("#bro").click(function () {
       if(jQuery("#heatmapGraph").is(":hidden")) {
         jQuery("#oc").attr("src", "images/disclosed.gif");
       } else {
         jQuery("#oc").attr("src", "images/undisclosed.gif");
       }
       jQuery("#heatmapGraph").toggle("slow");
    });
})
</script>

<c:set var="MAX_CLUSTER" value="250" />
<c:set var="MAX_MAP" value="600" />
<c:set var="MAX_DEFAULT_OPEN" value="100" />
    <div id="heatmap_div">
        <p>
          <h2>
              <c:choose>
                <c:when test="${ExpressionType == 'gene'}">
                  ${WEB_PROPERTIES['heatmap.geneExpressionScoreTitle']}
                </c:when>
                <c:otherwise>
                  ${ExpressionType}
                </c:otherwise>
              </c:choose>
          </h2>
        </p>
        <p>
          <i>
            ${WEB_PROPERTIES['heatmap.expressionScoreSummary']}
            <a href="/${WEB_PROPERTIES['webapp.path']}/experiment.do?experiment=Drosophila Cell Line and Developmental Stage Gene and Exon Scores"> the Celniker group</a>
            and are log2 of the actual value.
            <br>Heatmap visualization powered by
            <a href="http://www.canvasxpress.org">canvasXpress</a>, learn more about the <a href="http://www.canvasxpress.org/heatmap.html">display options</a>.
          </i>
        </p>
        <br/>

        <html:link linkName="#" styleId="bro" style="cursor:pointer">
        <h3>
        <c:if test="${FeatureCount > MAX_DEFAULT_OPEN}">
        Your list is big and there could be issues with the display:
        </c:if>
        <b>Click to see/hide</b> the expression maps<img src="images/undisclosed.gif" id="oc"></h3>
        </html:link>


        <div id="heatmapGraph" style="display: block">

        <c:if test="${FeatureCount > MAX_CLUSTER}">
        Please note that clustering functions are not available for lists with more than ${MAX_CLUSTER} elements.
        <br>
        </c:if>

        <div id="heatmapContainer">
            <table>
              <tr>
                <td>
                    <div style="padding: 0px 0px 5px 30px;">
                      
                      <span>Type:</span>
                      <select id="experimentType"></select>

                      <span>Experiment:</span>
                      <select id="experimentSelect"></select>
                      <span>Cell Line Clustering - Hierarchical:</span>
                      <select id="hierarchicalSelect">
                        <option value="single" selected="selected">Single</option>
                        <option value="complete">Complete</option>
                        <option value="average">Average</option>
                      </select>
                      <span> and K-means:</span>
                      <select id="kMenasSelect">
                        <option value="3" selected="selected">3</option>
                      </select>
                    </div>
                    <div id="set_canvas">
                      <canvas id="canvas_cl" width="700" height="550"></canvas>
                    </div>                      
                </td>
              </tr>
            </table>
        </div>
        <div id="description_div">
            <table border="0">
                <tr>
                    <td ><h3 style="font-weight: bold; background: black; color: white;">More Information</h3></td>
                    <td ><h3 style="background: white;"><img src="images/disclosed.gif" id="co"></h3></td>
                </tr>
            </table>
        </div>
        <div id="description" style="padding: 5px">
            <i>
              <c:choose>
                <c:when test="${ExpressionType == 'gene'}">
                  ${WEB_PROPERTIES['heatmap.geneExpressionScoreDescription']}
                </c:when>
                <c:otherwise>
                  ${ExpressionType}
                </c:otherwise>
              </c:choose>
            <br>Further information: check the <a href="/${WEB_PROPERTIES['webapp.path']}/portal.do?class=Submission&externalids=modENCODE_3305">
modENCODE submission</a>, with links to the original score files for <a href="http://submit.modencode.org/submit/public/get_file/3305/extracted/Drosophila_Cell_Lines_and_Developmental_Stages_Gene_Scores.txt" target="_blank">genes</a>
            and <a href="http://submit.modencode.org/submit/public/get_file/3305/extracted/Drosophila_Cell_Lines_and_Developmental_Stages_Exon_Scores.txt" target="_blank">exons</a>.
            </i>
        </div>
    </div>
</div>
</div>

<script type="text/javascript">
  var types           = ${type};
  var Genes           = ${gene};
  var defaultValues   = ${defaultValues};
  var bagName         = '${ListName}';
  var webAppPath      = "${WEB_PROPERTIES['webapp.path']}";
  var api_key         = '${APIKey}';
  var feature_count   = parseInt(${FeatureCount});
  var max_cluster     = parseInt(${MAX_CLUSTER});
  var max_map         = parseInt(${MAX_MAP});
  
  var typeSelected    = '';
  var experiment      = '';
  var arrayConditions = [];
</script>
<script src="model/js/libraryIntermine.js" charset="UTF-8"></script>
<script type="text/javascript" src="model/js/heatMap.js"></script>

<!-- /heatMap.jsp -->