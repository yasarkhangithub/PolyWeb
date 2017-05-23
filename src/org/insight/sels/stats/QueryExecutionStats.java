package org.insight.sels.stats;


/**
 * 
 * @author Yasar Khan
 *
 */
public class QueryExecutionStats {
	
	private Long sourceSelectionTime;
	private Long queryPlanningTime;
	private Long queryExecutionTime;
	private Long queryTranslationTime;
	private Long resultTransformationTime;
	private Long totalExecuionTime;
	private Integer resultCount;
	
	
	public QueryExecutionStats() {
		sourceSelectionTime = new Long(0);
		queryPlanningTime = new Long(0);
		queryExecutionTime = new Long(0);
		queryTranslationTime = new Long(0);
		resultTransformationTime = new Long(0);
		totalExecuionTime = new Long(0);
		resultCount = 0;
	}
	
	
	
	public void printStats() {
		System.out.println();
		System.out.println("------------ Query Execution Statistics ------------");
		System.out.println();
		System.out.println("Source Selection Time (msec):      " + getSourceSelectionTime());
		System.out.println("Query Planning Time (msec):        " + getQueryPlanningTime());
		System.out.println("Query Plan Execution Time (msec):  " + getQueryExecutionTime());
		System.out.println("Query Translation Time (msec):     " + getQueryTranslationTime());
		System.out.println("Result Transformation Time (msec): " + getResultTransformationTime());
		System.out.println("Total Query Execution Time (msec): " + getTotalExecuionTime());
		System.out.println("Total Number of Results:           " + getResultCount());
		System.out.println();
		System.out.println("----------------------------------------------------");
	}
	
	
	public void calculateSourceSelectionTime(long startTime, long finishTime) {
		sourceSelectionTime = sourceSelectionTime + (finishTime-startTime);
	}
	
	
	public void calculateQueryPlanningTime(long startTime, long finishTime) {
		queryPlanningTime = queryPlanningTime + (finishTime-startTime);
	}
	
	
	public void calculateQueryTranslationTime(long startTime, long finishTime) {
		queryTranslationTime = queryTranslationTime + (finishTime-startTime);
	}
	
	
	public void calculateResultTransformationTime(long startTime, long finishTime) {
		resultTransformationTime = resultTransformationTime + (finishTime-startTime);
	}
	
	
	public void calculateQueryExecutionTime(long startTime, long finishTime) {
		queryExecutionTime = queryExecutionTime + (finishTime-startTime);
	}
	
	
	public Long calculateTotalExecutionTime() {
		return totalExecuionTime = sourceSelectionTime + queryPlanningTime + queryExecutionTime;
	}
	
	
	public void incrementResultCount() {
		resultCount++;
	}
	
	
	
	public Long getSourceSelectionTime() {
		return sourceSelectionTime;
	}
	
	
	
	public void setSourceSelectionTime(Long sourceSelectionTime) {
		this.sourceSelectionTime = sourceSelectionTime;
	}
	
	
	
	public Long getQueryPlanningTime() {
		return queryPlanningTime;
	}
	
	
	
	public void setQueryPlanningTime(Long queryPlanningTime) {
		this.queryPlanningTime = queryPlanningTime;
	}
	
	
	
	public Long getQueryExecutionTime() {
		return queryExecutionTime;
	}
	
	
	
	public void setQueryExecutionTime(Long queryExecutionTime) {
		this.queryExecutionTime = queryExecutionTime;
	}
	
	
	
	public Long getQueryTranslationTime() {
		return queryTranslationTime;
	}
	
	
	
	public void setQueryTranslationTime(Long queryTranslationTime) {
		this.queryTranslationTime = queryTranslationTime;
	}
	
	
	
	public Long getResultTransformationTime() {
		return resultTransformationTime;
	}
	
	
	
	public void setResultTransformationTime(Long resultTransformationTime) {
		this.resultTransformationTime = resultTransformationTime;
	}


	public Long getTotalExecuionTime() {
		return calculateTotalExecutionTime();
	}


	public void setTotalExecuionTime(Long totalExecuionTime) {
		this.totalExecuionTime = totalExecuionTime;
	}



	public Integer getResultCount() {
		return resultCount;
	}



	public void setResultCount(Integer resultCount) {
		this.resultCount = resultCount;
	}
	
	

}
