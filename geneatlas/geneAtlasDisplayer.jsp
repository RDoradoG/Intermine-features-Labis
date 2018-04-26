<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!-- geneAtlasDisplayer.jsp -->

<c:if test="${TypeDiccionary != '[]'}">
<div>
  <h3>GeneAtlas adult tissue expression</h3>
  <div id="flyatlas-viz"></div>

  <div id="flyatlas-viz2"></div>

  <form id="fly-atlas-options">
    <table cellspacing="0">
      <tbody>
        <tr>
          <td>
            <input type="radio" name="scale" value='normal' checked="checked"/> Normal Scale <br>
            <input type="radio" name="scale" value="zscore"/> Z score Scale
          </td>
          <td class="border-left">
            <input type="radio" name="orderExpr" value="name" checked="checked"/> Order by Name <br>
            <input type="radio" name="orderExpr" value="score"/> Order by Score
          </td>
          <td class="border-left">
            Type:
            <select id="experimentType" class="form-control"></select>
          </td>
          <td class="border-left">
            Experiment:
            <select id="experimentSelect" class="form-control">
            </select>
          </td>
        </tr>
      </tbody>
    </table>
  </form>
</div>

<link rel="stylesheet" href="model/style//bootstrap.min.css">
<script type="text/javascript" src="model/js/bootstrap.min.js"></script>
<script type="text/javascript">
  var PrimaryIdentifier = '${getPrimaryIdentifier}';
  var TypesDiccionary   = ${TypeDiccionary};
  var api_key           = '${ApiKey}';
  var typeSelected     = '';
  var experiment       = '';
  var useLinearScale   = 'normal';
  var orderBySignal    = 'name';
  var expressionValues = [];
</script>
<script type="text/javascript" src="model/js/libraryIntermine.js"></script>
<script type="text/javascript" src="model/js/geneAtlasDisplayer.js"></script>

</c:if>

<!-- /geneAtlasDisplayer.jsp -->