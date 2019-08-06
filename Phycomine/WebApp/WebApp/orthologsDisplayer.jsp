<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!-- orthologsDisplayer.jsp -->
<c:if test="${ortholog != ''}">
<div>

  <div id="im_aspect_MiscellaneousgeneFamily_table" class="collection-table">
    <a name="geneFamily" class="anchor"></a>
    <h3 id="im_aspect_MiscellaneousgeneFamily_table_h3">${number_rows} Orthologs: ${ortholog}</h3>
    <div class="clear"></div>
    <div id="coll_im_aspect_MiscellaneousgeneOrthologs">
      <div id="coll_im_aspect_MiscellaneousgeneOrthologs_inner" style="overflow-x:hidden;">
      <!-- reportCollectionTable -->
        <table>
          <thead>
            <tr>
              <th>
                <span title="">
                  <em style="font-size:9px;"></em>
                  DB identifier
                </span>
              </th>
              <th>
                <span title="">
                  <em style="font-size:9px;"></em>
                  Secondary Identifier
                </span>
              </th>
              <th>
                <span title="">
                  <em style="font-size:9px;"></em>
                  Symbol
                </span>
              </th>
              <th>
                <span title="">
                  <em style="font-size:9px;"></em>
                  Name
                </span>
              </th>
              <th>
                <span title="">
                  <em style="font-size:9px;"></em>
                  Organism . Short Name
                </span>
              </th>
            </tr>
          </thead>
          <tbody>
            ${genes_html}
          </tbody>
        </table>
        <!-- /reportCollectionTable -->
        <script type="text/javascript">
          trimTable('#coll_im_aspect_MiscellaneousgeneOrthologs_inner');
          (function($) {
            var EXPAND_ON_LOAD = true;
            $(function(){
              if(false && typeof(Storage)!=="undefined"){
                if (localStorage.coll_im_aspect_MiscellaneousgeneOrthologs==undefined) {
                  if (!EXPAND_ON_LOAD) {
                    $('#coll_im_aspect_MiscellaneousgeneOrthologs').hide();
                  }
                }
                if(localStorage.coll_im_aspect_MiscellaneousgeneOrthologs == "hide"){
                  $('#coll_im_aspect_MiscellaneousgeneOrthologs').hide();
                  localStorage.coll_im_aspect_MiscellaneousgeneOrthologs="hide";
                }
              } else {
                if (!EXPAND_ON_LOAD) {
                  $('#coll_im_aspect_MiscellaneousgeneOrthologs').hide();
                }
              }
              $('#im_aspect_MiscellaneousgeneFamily_table_h3').click(function(e){
                $('#coll_im_aspect_MiscellaneousgeneOrthologs').slideToggle('fast');
                if(false && typeof(Storage)!=="undefined"){
                  if(localStorage.coll_im_aspect_MiscellaneousgeneOrthologs=="hide"){
                    localStorage.coll_im_aspect_MiscellaneousgeneOrthologs="show";
                  }else{
                    localStorage.coll_im_aspect_MiscellaneousgeneOrthologs="hide";
                  }
                }
              });
            });
          })(window.jQuery);
        </script>
      </div>
      <div class="show-in-table" ${displayOff}>
        <a href="/phycomine/collectionDetails.do?id=${actual_gene_id}&amp;field=ortholog&amp;trail=">Show all in a table</a>
      </div>
    </div>
    <div class="clear"></div>
  </div>
</div>
</c:if>
<!-- /orthologsDisplayer.jsp -->