<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!-- geneAtlasDisplayer.jsp -->

<c:if test="${TypeDiccionary != '[]'}">
<div>
  <h3>GeneAtlas Expression Values</h3>
  <div id="geneatlas-viz"></div>

  <form id="gene-atlas-options" class="gene-atlas-options">
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
        <tr>
          <td>
            <button type="button" class="btn btn-primary" data-toggle="modal" id="compareButton">
              Compare Experiments
            </button>
          </td>
          <td>
            <button type="button" class="btn btn-success" data-toggle="modal" id="scatterButton">
              ScatterPlot
            </button>
          </td>
        </tr>
      </tbody>
    </table>
  </form>
</div>

<div id="ComparationChart">
  <h3>GeneAtlas Comparation</h3>
  <div id="Chart-comparation"></div>

  <form id="gene-atlas-options-compare" class="gene-atlas-options">
    <table cellspacing="0">
      <tbody>
        <tr>
          <td>
            <input type="radio" name="scaleCompare" value='normal' checked="checked"/> Normal Scale <br>
            <input type="radio" name="scaleCompare" value="zscore"/> Z score Scale
          </td>
          <td class="border-left">
            <input type="radio" name="orderExprCompare" value="name" checked="checked"/> Order by Name <br>
            <input type="radio" name="orderExprCompare" value="scoreA"/> Order by Score A <br>
            <input type="radio" name="orderExprCompare" value="scoreB"/> Order by Score B
          </td>
          <td class="border-left">
          </td>
          <td class="border-left">
          </td>
        </tr>
      </tbody>
    </table>
  </form>
</div>

<div id="ScattterChart">
  <h3>GeneAtlas Scatter Plot</h3>
  <div id="Chart-scatterplot"></div>

  <!--form id="gene-atlas-options-Scatter" class="gene-atlas-options">
    <table cellspacing="0">
      <tbody>
        <tr>
          <td>
            Pair of conditions:
            <select id="pairConditios" class="form-control">
              <option value="" selected="selected">-- SELECT --</option>
            </select>
          </td>
          <td class="border-left">
          </td>
        </tr>
      </tbody>
    </table>
  </form-->
</div>

<div>
  <!-- Modal -->
  <div class="modal fade" id="compare" tabindex="-1" role="dialog" aria-hidden="true" data-backdrop="static" data-keyboard="false">
    <div class="modal-dialog modal-lg" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="compareTitle">Compare Experiments</h5>
        </div>
        <div class="modal-body">
          <div class="container-fluid">
            <div class="row">
              <div class="col-md-12">
                <div class="col-md-2">
                  Type
                </div>
                <div class="col-md-4">
                  <select id="experimentType_first" class="form-control"></select>
                </div>
                <div class="col-md-2">
                  Type
                </div>
                <div class="col-md-4">
                  <select id="experimentType_second" class="form-control"></select>
                </div>
              </div>
            </div>
            <div class="row">
              <div class="col-md-12">
                <div class="col-md-2">
                  Experiment
                </div>
                <div class="col-md-4">
                  <select id="experimentSelect_first" class="form-control"></select>
                </div>
                <div class="col-md-2">
                  Experiment
                </div>
                <div class="col-md-4">
                  <select id="experimentSelect_second" class="form-control"></select>
                </div>
              </div>
            </div>
            <div class="row">
              <hr>
            </div>
            <div class="row">
              <div class="col-md-12 scrollPopUp">
                <div class="col-md-6">
                  <ul class="sortable sort" id="ul_first"></ul>
                  <ul class="sort">
                    <li class="ui-state-default">
                      <select class="items textOverflow" id="li_first"></select>
                    </li>
                  </ul>
                </div>
                <div class="col-md-6">
                  <ul class="sortable sort" id="ul_second"></ul>
                  <ul class="sort">
                    <li class="ui-state-default">
                      <select class="items textOverflow" id="li_second"></select>
                    </li>
                  </ul>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <div class="alert alert-danger footModalAlert" role="alert" id="alertFootModal">
            Has to be at least one condition for each experiment.
          </div>
          <div class="footModal">
            <button type="button" class="btn btn-danger" data-dismiss="modal">Cancel</button>
            <button type="button" class="btn btn-success" id="executeComparation">Continue</button>
          </div>
        </div>
      </div>
    </div>
  </div>

  <div class="modal fade" id="scatterplot" tabindex="-1" role="dialog" aria-hidden="true" data-backdrop="static" data-keyboard="false">
    <div class="modal-dialog modal-lg" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="compareTitle">Scatter Plot</h5>
        </div>
        <div class="modal-body">
          <div class="container-fluid">
            <div class="row">
              <div class="col-md-12">
                <div class="col-md-2">
                  Type
                </div>
                <div class="col-md-4">
                  <select id="experimentType_first_scatter" class="form-control"></select>
                </div>
                <div class="col-md-2">
                  Type
                </div>
                <div class="col-md-4">
                  <select id="experimentType_second_scatter" class="form-control"></select>
                </div>
              </div>
            </div>
            <div class="row">
              <div class="col-md-12">
                <div class="col-md-2">
                  Experiment
                </div>
                <div class="col-md-4">
                  <select id="experimentSelect_first_scatter" class="form-control"></select>
                </div>
                <div class="col-md-2">
                  Experiment
                </div>
                <div class="col-md-4">
                  <select id="experimentSelect_second_scatter" class="form-control"></select>
                </div>
              </div>
            </div>
            <div class="row">
              <hr>
            </div>
            <div class="row">
              <div class="col-md-12 scrollPopUp">
                <div class="col-md-6">
                  <ul class="sortable sort" id="ul_first_scatter"></ul>
                  <ul class="sort">
                    <li class="ui-state-default">
                      <select class="items textOverflow" id="li_first_scatter"></select>
                    </li>
                  </ul>
                </div>
                <div class="col-md-6">
                  <ul class="sortable sort" id="ul_second_scatter"></ul>
                  <ul class="sort">
                    <li class="ui-state-default">
                      <select class="items textOverflow" id="li_second_scatter"></select>
                    </li>
                  </ul>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <div class="alert alert-danger footModalAlert" role="alert" id="alertFootModal_scatter">
            Has to be at least one condition for each experiment and the same number of consitions in both experiments.
          </div>
          <div class="footModal">
            <button type="button" class="btn btn-danger" data-dismiss="modal">Cancel</button>
            <button type="button" class="btn btn-success" id="executeScatterPlot">Continue</button>
          </div>
        </div>
      </div>
    </div>
  </div>

</div>

<link rel="stylesheet" href="model/style/bootstrap.min.css">
<link rel="stylesheet" href="model/style/jquery-ui.css">
<link rel="stylesheet" href="model/style/geneAtlasDisplayer.css">

<script type="text/javascript" src="model/js/bootstrap.min.js"></script>
<!--script type="text/javascript" src="model/js/jquery-1.12.4.js"></script-->
<script type="text/javascript" src="model/js/jquery-ui.js"></script>

<script type="text/javascript">
  var TypesDiccionary    = ${TypeDiccionary};
  var api_key            = '${ApiKey}';
  var PrimaryIdentifier  = '${getPrimaryIdentifier}';
  var allConditions      = [];
  var firstExperiment    = [];
  var expressionValues   = [];
  var secondExperiment   = [];
  var experiment         = '';
  var typeSelected       = '';
  var orderBySignal      = 'name';
  var orderByComparation = 'name';
  var useLinearScale     = 'normal';
  var scaleComparation   = 'normal';
  var chartScatter;
  var expressionValuesA;
  var expressionValuesB;
  var optionSelectedButton;
</script>

<script type="text/javascript" src="model/js/libraryIntermine.js"></script>
<script type="text/javascript" src="model/js/geneAtlasDisplayer.js"></script>

</c:if>

<!-- /geneAtlasDisplayer.jsp -->
