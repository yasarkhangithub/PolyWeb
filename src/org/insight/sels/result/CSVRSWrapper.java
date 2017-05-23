package org.insight.sels.result;

import java.sql.SQLException;
import java.util.Iterator;

import org.insight.sels.config.Config;
import org.insight.sels.query.TPGroup;
import org.insight.sels.result.transform.ResultTransform;
import org.insight.sels.result.transform.ResultTransformFactory;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * 
 * @author Yasar Khan
 *
 */
public class CSVRSWrapper implements ResultSetWrapper {

	private Iterator<JsonNode> resultSet;
	private ResultTransform resultTransformer;

	@Override
	public PolyQuerySolution getNextResult() {
		PolyQuerySolution polyQS = null;

		try {
			if (resultSet.hasNext()) {
				polyQS = resultTransformer.tranform(resultSet.next());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return polyQS;
	}

	@Override
	public void setResultSet(Object resultSet) {
		this.resultSet = (Iterator<JsonNode>) resultSet;
	}

	@Override
	public void setSubQuery(TPGroup subQuery) {
		ResultTransformFactory rsTransfFactory = new ResultTransformFactory();
		this.resultTransformer = rsTransfFactory.getResultTransform("CSV");
		this.resultTransformer.setSubQuery(subQuery);
	}

}
