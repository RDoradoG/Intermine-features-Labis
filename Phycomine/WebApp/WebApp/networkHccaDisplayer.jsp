<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!-- networkHccaDisplayer.jsp -->
<c:if test="${hrrNet != '[]'}">

<div>
  <h3>Co-expression Networks</h3>
  <div id="network-viz"></div>

  <form id="network-options" class="network-options">
    <table border="1">
      <thead>
        <th>Type</th>
        <th>Description</th>
        <th>Actions</th>
      </thead>
      <tr id="hrr_exists">
        <td>Neighborhood</td>
        <td>Hrr network (cutoff 50): <b>${PrimaryIdentifier}</b></td>
        <td>
          <button type="button" class="btn btn-primary" data-toggle="modal" id="neighborhoodButtonList">
            <span class="glyphicon glyphicon-th-list"></span>
          </button>
          <button type="button" class="btn btn-primary" data-toggle="modal" id="neighborhoodButtonNetwork">
            <span class="glyphicon glyphicon-eye-open"></span>
          </button>
        </td>
      </tr>

      <tr id="hrr_not_exists">
        <td>Neighborhood</td>
        <td>Hrr network (cutoff 50): <b>-</b></td>
        <td>
        </td>
      </tr>

      <tr id="hcca_exists">
        <td>Cluster</td>
        <td>HCCA Clusters: <b>Cluster ${cluster}</b></td>
        <td>
          <button type="button" class="btn btn-primary" data-toggle="modal" id="clusterButtonList">
            <span class="glyphicon glyphicon-th-list"></span>
          </button>
          <button type="button" class="btn btn-primary" data-toggle="modal" id="clusterButtonNetWork">
            <span class="glyphicon glyphicon-eye-open"></span>
          </button>
        </td>
      </tr>

      <tr id="hcca_not_exists">
        <td>Cluster</td>
        <td>HCCA Clusters: <b>-</b></td>
        <td>
        </td>
      </tr>
    </table>
  </form>

  <form id="Network_bagUploadConfirmForm" method="post" action="/phycomine/bagUploadConfirm.do" enctype="multipart/form-data" _lpchecked="1">
    <input type="hidden" name="bagType" id="geneList_bagType" value="Gene">
    <input type="hidden" name="matchIDs" id="geneList_matchIDs" value="">
    <input type="hidden" name="newBagName" id="geneList_newBagName" value="">
  </form>

  <div class="modal fade" id="network" tabindex="-1" role="dialog" aria-hidden="true" data-backdrop="static" data-keyboard="false">
    <div class="modal-dialog modal-dialog-centered modal-xlg" role="document">
      <div class="modal-content">
        <div class="modal-header" id="networkTitle">
        </div>
        <div class="modal-body">
          <div class="container-fluid">
            <div class="row">
              <div class="col-md-3">
                <h4>Group Genes By: </h4>
                <input type="checkbox" id="Orthologs" class="networkClass" name="networkOption" value="orthologs" checked="checked"/> Orthologs (Color)<br>
                <input type="checkbox" id="geneFamilies" class="networkClass" name="networkOption" value="geneFamilies" checked="checked"/> Gene Families (Form)<br>
                <input type="checkbox" id="expressionValues" class="networkClass" name="networkOption" value="expressionValues" checked="checked"/> Expression Values (Size)<br>
                <br><br>
                <div>
                  <h4>Genes Families Options: </h4>
                  Family Type: <select id="familyTypeNetwork" class="form-control networkClass" name="geneFamiliesOptions"></select> <br>
                  EggNog Taxon: <select id="geneFamilyDataset" class="form-control networkClass" name="geneFamiliesOptionsTaxon" disabled></select> <br>
                </div>
                <br><br>
                <div>
                  <h4>Expression Values Options: </h4>
                  Experiment Type: <select id="experimentTypeNetwork" class="form-control networkClass" name="expressionValuesOptions"></select> <br>
                  Experiment Name: <select id="experimentNameNetwork" class="form-control networkClass" name="expressionValuesOptions"></select> <br>
                  Condition:       <select id="experimentCondicionNetwork" class="form-control networkClass" name="expressionValuesOptions"></select> <br>
                </div>
                <br><br><br>
                <div style="text-align: right;">
                  <button type="button" class="btn btn-primary" id="GraphML">Download GraphML Network</button>
                </div>
              </div>
              <div class="col-md-9">
                <div id="set_canvas_div" class="network-style">
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <div class="footModal">
            <button type="button" class="btn btn-primary" data-dismiss="modal">Close</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>

<div class="modal fade" id="loading_network" tabindex="-1" role="dialog" aria-hidden="true" data-backdrop="static" data-keyboard="false">
  <div class="modal-dialog modal-dialog-centered modal-sm" role="document">
    <div class="modal-content">
      <div class="modal-body">
        Loading ...
      </div>
    </div>
  </div>
</div>

<link rel="stylesheet" href="model/style/jquery-ui.css">
<link rel="stylesheet" href="model/style/bootstrap.min.css">
<link rel="stylesheet" href="model/style/geneAtlasDisplayer.css">
<link rel="stylesheet" href="model/style/canvasXpress.css" type="text/css"/>

<!--script type="text/javascript" src="model/js/canvasXpress.min.js"></script-->
<script type="text/javascript" src="model/js/jquery-ui.js"></script>
<script type="text/javascript" src="model/js/canvasXpress.js"></script>
<script type="text/javascript" src="model/js/bootstrap.min.js"></script>

<script type="text/javascript">
  var hm_cl;
  var geneFamily;
  var typeNetwork;
  var listToSubmit;
  var geneFamilyDataset;
  var experimentTypeSelected;
  var experimentNameSelected;
  var countNodeColors        = 0;
  var countNodeShapes        = 1;
  var countNodePatterns      = 0;
  var nodesDrawed            = [];
  var colorOrtholog          = {};
  var shapePatternFamily     = {};
  var defaultValue           = 'null';
  var deafultNodeSizeGraphML = '25.0';
  var deafultNodePattern     = 'solid';
  var deafultNodeShape       = 'sphere';
  var deafultNodeColor       = '#699696';
  var hrrNet                 = ${hrrNet};
  var hccaNet                = ${hccaNet};
  var geneFamilies           = ${geneFamilies};
  var experimentTypes        = ${experimentTypes};
  var geneFamilyDatasets     = ${geneFamilyDataset};
  var PrimaryIdentifier      = "${PrimaryIdentifier}";
  var nets                   = {hcca: hccaNet, hrr: hrrNet};
  var edgesColors            = ['green','chartreuse','yellow','orange','red'];
  var edgeColorsGraphMl      = ['#008000','#7FFF00','#FFFF00','#FFA500','#FF0000']
  var geneList               = {hcca: {nodes: [], edges: []}, hrr: {nodes: [], edges: []}};
  var networkOption          = {orthologs: true, geneFamilies: true, expressionValues: true};
  var geneListTile           = '<h3 class="modal-title">Gene List</h3> {{genes_number}} Gene(s)'
  var networkTitle           = '<h3 class="modal-title"><b>NetWork:</b> {{genes_number}} Gene(s)</h3>'
  var shapes                 = ["sphere","square","triangle","star","rhombus","octagon","plus","pacman","pacman2","mdavid","pentagon","arc","rect3"];
  var bagNameExist           = {'hrr': {'name': '${bagName_HRR}', 'set': ${bagNameExists_HRR}}, 'hcca': {'name': '${bagName_HCCA}', 'set': ${bagNameExists_HCCA}}};
  var graphMLShapes          = ["ellipse","rectangle","triangle","star5","diamond","octagon","star8","parallelogram","parallelogram2","star6","trapezoid","fatarrow","hexagon",];
  var patterns               = ["solid","hatchForward","stripeHorizontal","polkaDot","squares","circles","hatchReverse","stripeVertical","crossHatch","crossStripe",
                                "plus","minus","bars","squiglesVertical","squiglesHorizontal","brickForward","brickReverse","art","pcx","hatchForward3","hatchReverse3"];
  var graphMLPatterns        = [['line',1.0],['dashed',1.0],['dotted',1.0],['dashed_dotted',1.0],['line',7.0],['dashed',5.0],['dotted',5.0],['dashed_dotted',5.0],['line',4.0],['dashed',3.0],
                                ['dotted',3.0],['dashed_dotted',3.0],['line',3.0],['dashed',2.0],['dotted',2.0],['dashed_dotted',2.0],['line',5.0],['dashed',4.0],['dotted',4.0],['dashed_dotted',4.0],
                                ['line',2.0],['line',6.0]];
  var nodesColors            = ["#FFFF00","#1CE6FF","#FF34FF","#FF4A46","#008941","#006FA6","#A30059","#FFDBE5","#7A4900","#0000A6","#63FFAC","#B79762","#004D43","#8FB0FF","#997D87","#5A0007","#809693","#FEFFE6","#1B4400","#4FC601",
                                "#3B5DFF","#4A3B53","#FF2F80","#61615A","#BA0900","#6B7900","#00C2A0","#FFAA92","#FF90C9","#B903AA","#D16100","#DDEFFF","#000035","#7B4F4B","#A1C299","#300018","#0AA6D8","#013349","#00846F","#372101",
                                "#FFB500","#C2FFED","#A079BF","#CC0744","#C0B9B2","#C2FF99","#001E09","#00489C","#6F0062","#0CBD66","#EEC3FF","#456D75","#B77B68","#7A87A1","#788D66","#885578","#FAD09F","#FF8A9A","#D157A0","#BEC459",
                                "#456648","#0086ED","#886F4C","#34362D","#B4A8BD","#00A6AA","#452C2C","#636375","#A3C8C9","#FF913F","#938A81","#575329","#00FECF","#B05B6F","#8CD0FF","#3B9700","#04F757","#C8A1A1","#1E6E00","#7900D7",
                                "#A77500","#6367A9","#A05837","#6B002C","#772600","#D790FF","#9B9700","#549E79","#FFF69F","#201625","#72418F","#BC23FF","#99ADC0","#3A2465","#922329","#5B4534","#FDE8DC","#404E55","#0089A3","#CB7E98",
                                "#A4E804","#324E72","#6A3A4C","#83AB58","#001C1E","#D1F7CE","#004B28","#C8D0F6","#A3A489","#806C66","#222800","#BF5650","#E83000","#66796D","#DA007C","#FF1A59","#8ADBB4","#1E0200","#5B4E51","#C895C5",
                                "#320033","#FF6832","#66E1D3","#CFCDAC","#D0AC94","#7ED379","#012C58","#7A7BFF","#D68E01","#353339","#78AFA1","#FEB2C6","#75797C","#837393","#943A4D","#B5F4FF","#D2DCD5","#9556BD","#6A714A","#001325",
                                "#02525F","#0AA3F7","#E98176","#DBD5DD","#5EBCD1","#3D4F44","#7E6405","#02684E","#962B75","#8D8546","#9695C5","#E773CE","#D86A78","#3E89BE","#CA834E","#518A87","#5B113C","#55813B","#E704C4","#00005F",
                                "#A97399","#4B8160","#59738A","#FF5DA7","#F7C9BF","#643127","#513A01","#6B94AA","#51A058","#A45B02","#1D1702","#E20027","#E7AB63","#4C6001","#9C6966","#64547B","#97979E","#006A66","#391406","#F4D749",
                                "#0045D2","#006C31","#DDB6D0","#7C6571","#9FB2A4","#00D891","#15A08A","#BC65E9","#FFFFFE","#C6DC99","#203B3C","#671190","#6B3A64","#F5E1FF","#FFA0F2","#CCAA35","#374527","#8BB400","#797868","#C6005A",
                                "#3B000A","#C86240","#29607C","#402334","#7D5A44","#CCB87C","#B88183","#AA5199","#B5D6C3","#A38469","#9F94F0","#A74571","#B894A6","#71BB8C","#00B433","#789EC9","#6D80BA","#953F00","#5EFF03","#E4FFFC",
                                "#1BE177","#BCB1E5","#76912F","#003109","#0060CD","#D20096","#895563","#29201D","#5B3213","#A76F42","#89412E","#1A3A2A","#494B5A","#A88C85","#F4ABAA","#A3F3AB","#00C6C8","#EA8B66","#958A9F","#BDC9D2",
                                "#9FA064","#BE4700","#658188","#83A485","#453C23","#47675D","#3A3F00","#061203","#DFFB71","#868E7E","#98D058","#6C8F7D","#D7BFC2","#3C3E6E","#D83D66","#2F5D9B","#6C5E46","#D25B88","#5B656C","#00B57F",
                                "#545C46","#866097","#365D25","#252F99","#00CCFF","#674E60","#FC009C","#92896B"];
</script>

<script type="text/javascript" src="model/js/libraryIntermine.js"></script>
<script type="text/javascript" src="model/js/networkHccaDisplayer.js"></script>

</c:if>
<!-- /networkHccaDisplayer.jsp -->
