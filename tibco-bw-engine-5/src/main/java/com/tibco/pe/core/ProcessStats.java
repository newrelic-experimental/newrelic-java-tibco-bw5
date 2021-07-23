package com.tibco.pe.core;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.tibco.engine.instrumentation.NRProcessStats;

@Weave
public abstract class ProcessStats
{

  @NewField
  public String name;

  public abstract long getLatestElapsedTime();

  public abstract long getLatestInvokeTime();

  public abstract long getCounter(int paramInt);

  public abstract String getCounterName(int paramInt);

  @Trace(dispatcher=true)
  public void jobCompleted(Job job, boolean aborted, StatsCollector statsCollector)
  {
    Weaver.callOriginal();
    Long invokeTime = Long.valueOf(getLatestInvokeTime());
    Long elapsedTime = Long.valueOf(getLatestElapsedTime());
    Workflow workflow = job.getActualWorkflow();
    String processName = null;
    if (workflow != null) {
      processName = workflow.getName();
    } else {
      workflow = job.getWorkflow();
      if (workflow != null) {
        processName = workflow.getName();
      }
    }
    if(job.token != null) {
    	job.token.expire();
    	job.token = null;
    }
    NRProcessStats.addStats(processName, invokeTime, elapsedTime, aborted, 1);
  }

  public synchronized void incrementCounter(int paramInt) {
    Weaver.callOriginal();
    String metricName = "Custom/Process/" + this.name + "/" + getCounterName(paramInt);
    long counterValue = getCounter(paramInt);
    NewRelic.getAgent().getMetricAggregator().recordMetric(metricName, (float)counterValue);
    if (paramInt == 0)
      NRProcessStats.reportProcessStart();
  }

  public synchronized void invocationCompleted(long paramLong1, long paramLong2, boolean paramBoolean, long paramLong3)
  {
    Weaver.callOriginal();
    NRProcessStats.addStats(this.name, Long.valueOf(getLatestInvokeTime()), Long.valueOf(getLatestElapsedTime()), paramBoolean, 2);
  }
}