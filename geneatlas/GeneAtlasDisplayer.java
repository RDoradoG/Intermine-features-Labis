package org.phycomine.web.displayer;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import org.intermine.api.InterMineAPI;
import org.intermine.api.profile.Profile;
import org.intermine.api.query.PathQueryExecutor;
import org.intermine.api.results.ExportResultsIterator;
import org.intermine.api.results.ResultElement;
import org.intermine.metadata.Model;
import org.intermine.model.bio.Gene;
import org.intermine.objectstore.ObjectStore;
import org.intermine.objectstore.ObjectStoreException;
import org.intermine.pathquery.Constraints;
import org.intermine.pathquery.PathQuery;
import org.intermine.pathquery.OrderDirection;
import org.intermine.web.displayer.ReportDisplayer;
import org.intermine.web.logic.config.ReportDisplayerConfig;
import org.intermine.web.logic.results.ReportObject;
import org.intermine.web.logic.session.SessionMethods;

public class GeneAtlasDisplayer extends ReportDisplayer
{

    public GeneAtlasDisplayer(ReportDisplayerConfig config, InterMineAPI im) {
        super(config, im);
    }

    @Override
    public void display(HttpServletRequest request, ReportObject reportObject) {

        HttpSession session        = request.getSession();
        final InterMineAPI im      = SessionMethods.getInterMineAPI(session);
        ObjectStore os             = im.getObjectStore();
        Model model                = im.getModel();
        Profile profile            = SessionMethods.getProfile(session);
        PathQueryExecutor executor = im.getPathQueryExecutor(profile);

        String APIKey              = profile.getApiKey();
        String DayToken            = profile.getDayToken();
        String key                 = "";

        Gene gene                  = (Gene) reportObject.getObject();

        if (APIKey != null && !APIKey.isEmpty()) {
            key = APIKey;
        } else {
            key = DayToken;
        }

        List<String> allType = new ArrayList<String>();
        String JsonType      = "";
        String idGene        = gene.getPrimaryIdentifier();

        PathQuery queryType  = getQueryType(model, idGene);

        ExportResultsIterator resultType;
        try {
            resultType = executor.execute(queryType);
        } catch (ObjectStoreException e) {
            throw new RuntimeException("Error retrieving data.", e);
        }

        while (resultType.hasNext()) {
            List<ResultElement> rowType = resultType.next();
            String name                 = (String) rowType.get(0).getField();
            if(!allType.contains(name)){
                allType.add(name);
                String additon = "{name: '" + name + "'}";
                JsonType       = addToJsonList(JsonType, additon);
            }
        }

        JsonType = "[" + JsonType + "]";

        request.setAttribute("getPrimaryIdentifier", idGene);
        request.setAttribute("TypeDiccionary", JsonType);
        request.setAttribute("ApiKey", key);
    }

    private String addToJsonList(String value, String addition) {
        if(value.equals("")){
            return value + addition;
        } else {
            return value + ", " + addition;
        }
    }

    private PathQuery getQueryType(Model model, String geneId) {
        PathQuery query = new PathQuery(model);
        query.addView("ExpressionTypeDiccionary.name");
        query.addOrderBy("ExpressionTypeDiccionary.name", OrderDirection.ASC);
        query.addConstraint(Constraints.eq("ExpressionTypeDiccionary.expressionValue.gene.primaryIdentifier", geneId));
        return query;
    }

}
