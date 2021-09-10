package cn.suyu.iot.enginemgmt.flink.yarn.clients;

import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.client.program.*;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.CoreOptions;
import org.apache.flink.configuration.DeploymentOptions;
import org.apache.flink.core.execution.DetachedJobExecutionResult;
import org.apache.flink.core.execution.PipelineExecutorServiceLoader;
import org.apache.flink.runtime.client.JobExecutionException;
import org.apache.flink.runtime.execution.librarycache.FlinkUserCodeClassLoaders;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobmaster.JobResult;
import org.apache.flink.util.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.apache.flink.util.FlinkUserCodeClassLoader.NOOP_EXCEPTION_HANDLER;
import static org.apache.flink.util.Preconditions.checkNotNull;

/**
 * Utility functions for Flink client.
 */
public enum CuiotClientUtils {
	;

	private static final Logger LOG = LoggerFactory.getLogger(CuiotClientUtils.class);

	public static ClassLoader buildUserCodeClassLoader(
			List<URL> jars,
			List<URL> classpaths,
			ClassLoader parent,
			Configuration configuration) {
		URL[] urls = new URL[jars.size() + classpaths.size()];
		for (int i = 0; i < jars.size(); i++) {
			urls[i] = jars.get(i);
		}
		for (int i = 0; i < classpaths.size(); i++) {
			urls[i + jars.size()] = classpaths.get(i);
		}
		String[] alwaysParentFirstLoaderPatterns = CoreOptions.getParentFirstLoaderPatterns(configuration);
		String classLoaderResolveOrder =
			configuration.getString(CoreOptions.CLASSLOADER_RESOLVE_ORDER);
		FlinkUserCodeClassLoaders.ResolveOrder resolveOrder =
			FlinkUserCodeClassLoaders.ResolveOrder.fromString(classLoaderResolveOrder);
		return FlinkUserCodeClassLoaders.create(resolveOrder, urls, parent, alwaysParentFirstLoaderPatterns, NOOP_EXCEPTION_HANDLER);
	}

	public static JobExecutionResult submitJob(
			ClusterClient<?> client,
			JobGraph jobGraph) throws ProgramInvocationException {
		checkNotNull(client);
		checkNotNull(jobGraph);
		try {
			return client
				.submitJob(jobGraph)
				.thenApply(DetachedJobExecutionResult::new)
				.get();
		} catch (InterruptedException | ExecutionException e) {
			ExceptionUtils.checkInterrupted(e);
			throw new ProgramInvocationException("Could not run job in detached mode.", jobGraph.getJobID(), e);
		}
	}

	public static JobExecutionResult submitJobAndWaitForResult(
			ClusterClient<?> client,
			JobGraph jobGraph,
			ClassLoader classLoader) throws ProgramInvocationException {
		checkNotNull(client);
		checkNotNull(jobGraph);
		checkNotNull(classLoader);

		JobResult jobResult;

		try {
			jobResult = client
				.submitJob(jobGraph)
				.thenCompose(client::requestJobResult)
				.get();
		} catch (InterruptedException | ExecutionException e) {
			ExceptionUtils.checkInterrupted(e);
			throw new ProgramInvocationException("Could not run job", jobGraph.getJobID(), e);
		}

		try {
			return jobResult.toJobExecutionResult(classLoader);
		} catch (JobExecutionException | IOException | ClassNotFoundException e) {
			throw new ProgramInvocationException("Job failed", jobGraph.getJobID(), e);
		}
	}

	public static void executeProgram(
			PipelineExecutorServiceLoader executorServiceLoader,
			Configuration configuration,
			PackagedProgram program,
			boolean enforceSingleJobExecution,
			boolean suppressSysout) throws ProgramInvocationException {
		checkNotNull(executorServiceLoader);
		ClassLoader userCodeClassLoader = program.getUserCodeClassLoader();
		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(userCodeClassLoader);

			LOG.info("Starting program (detached: {})", !configuration.getBoolean(DeploymentOptions.ATTACHED));

			ContextEnvironment.setAsContext(
				executorServiceLoader,
				configuration,
				userCodeClassLoader,
				enforceSingleJobExecution,
				suppressSysout);

			StreamContextEnvironment.setAsContext(
				executorServiceLoader,
				configuration,
				userCodeClassLoader,
				enforceSingleJobExecution,
				suppressSysout);

			try {
				program.invokeInteractiveModeForExecution();
			} finally {
				ContextEnvironment.unsetAsContext();
				StreamContextEnvironment.unsetAsContext();
			}
		} finally {
			Thread.currentThread().setContextClassLoader(contextClassLoader);
		}
	}
}
