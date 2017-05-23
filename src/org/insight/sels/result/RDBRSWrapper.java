package org.insight.sels.result;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.insight.sels.config.Config;
import org.insight.sels.query.TPGroup;
import org.insight.sels.result.transform.ResultTransform;
import org.insight.sels.result.transform.ResultTransformFactory;

/**
 * 
 * @author Yasar Khan
 *
 */
public class RDBRSWrapper implements ResultSetWrapper {

	private ResultSet resultSet;
	private ResultTransform resultTransformer;
	
	@Override
	public PolyQuerySolution getNextResult() {
		PolyQuerySolution polyQS = null;
		try {
			if(resultSet != null) {
				if(resultSet.next()) {
					polyQS = resultTransformer.tranform(resultSet);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return polyQS;
	}

	@Override
	public void setResultSet(Object resultSet) {
		this.resultSet = (ResultSet) resultSet;
	}

	@Override
	public void setSubQuery(TPGroup subQuery) {
		ResultTransformFactory rsTransfFactory = new ResultTransformFactory();
		this.resultTransformer = rsTransfFactory.getResultTransform("RDB");
		this.resultTransformer.setSubQuery(subQuery);
	}

}
