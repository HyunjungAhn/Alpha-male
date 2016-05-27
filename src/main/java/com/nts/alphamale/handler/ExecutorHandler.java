/**
 * AlphaMale for web
Copyright (C) 2016 NHN Technology Services

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

 */

package com.nts.alphamale.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nts.alphamale.shell.AdbShellExecutor;


public class ExecutorHandler {

	static Logger log = LogManager.getLogger(ExecutorHandler.class);
	
	protected ExecutorService synchExecutor;
	protected ExecutorService asynchExecutor;
	protected ExecutorService singleExecutor;
	protected ExecutorService multiExecutor;
	protected ScheduledExecutorService scheduledExecutor;
	protected ScheduledExecutorService multipleScheduledExecutor;
	
	public ScheduledExecutorService getMultipleScheduledExecutor() {
		return multipleScheduledExecutor;
	}
	public ExecutorService getAsynchExecutor(){
		return asynchExecutor;
	}
	public ExecutorService getSingleExecutor() {
		return singleExecutor;
	}

	public ExecutorService getMultiExecutor() {
		return multiExecutor;
	}

	public ScheduledExecutorService getScheduledExecutor() {
		return scheduledExecutor;
	}

	
	/**
	 * parallel execute synchronous commandline
	 * @param cmdList
	 * @param timeoutSecond
	 * @return
	 */
	public List<Map<String, Object>> executeParallel(List<CommandLine> cmdList, int timeoutSecond){
		ExecutorService executor = Executors.newCachedThreadPool();
		List<Future<Map<String, Object>>> resultList;
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		List<SynchronousTask> taskList = new ArrayList<SynchronousTask>();
		for(CommandLine cmd:cmdList){
			taskList.add(new SynchronousTask(cmd, timeoutSecond*1000));
		}
		try {
			resultList = executor.invokeAll(taskList, timeoutSecond+10, TimeUnit.SECONDS);
			for(Future<Map<String, Object>> result:resultList){
				results.add(result.get());
			}
		} catch (InterruptedException e) {
			log.error(e.getMessage());
		} catch (ExecutionException e) {
			log.error(e.getMessage());
		}
		if(!executor.isShutdown()){
			executor.shutdown();
		}
		return results;
	}
	
	/**
	 * execute asynchronous commandline
	 * @param cmdList
	 * @param timeout
	 * @return
	 */
	public Map<String, Object> executeAsynchronous(CommandLine cmd, long timeout){
		asynchExecutor = Executors.newCachedThreadPool();
		Future<Map<String, Object>> future =asynchExecutor.submit(new AsynchronousTask(cmd,ExecuteWatchdog.INFINITE_TIMEOUT));
		try{
			return future.get();
		} catch (InterruptedException e) {
			future.cancel(true);
		} catch (ExecutionException e) {
			log.error(e.getMessage());
		}
		return null;
	}
	
	/*public Object FollowerRpcRequest(FollowerRpcClient client, Job job){
		asynchExecutor = Executors.newCachedThreadPool();
		Future<Object> future = asynchExecutor.submit(new FollowerRpcRequest(client, job));
		try{
			return future.get();
		} catch (InterruptedException e) {
			future.cancel(true);
		} catch (ExecutionException e) {
			log.error(e.getMessage());
		}
		return null;
	}*/
	
	public void executeSingle(Runnable rt){
		singleExecutor = Executors.newSingleThreadExecutor();
		singleExecutor.submit(rt);
	}
	
	public void executeMultiple(List<Runnable> rt, int multi){
		multiExecutor = Executors.newFixedThreadPool(multi);
		for(Runnable r:rt)
			multiExecutor.submit(r);
	}
	
	public void executePeriodically(Runnable rt, long period){
		scheduledExecutor = Executors.newScheduledThreadPool(1);
		scheduledExecutor.scheduleAtFixedRate(rt, 3*1000, period, TimeUnit.MILLISECONDS);
	}
	
	public void executePeriodically(List<Runnable> rt, long period){
		multipleScheduledExecutor = Executors.newScheduledThreadPool(rt.size());
		for(Runnable r:rt){
			multipleScheduledExecutor.scheduleAtFixedRate(r, 3*1000, period, TimeUnit.MILLISECONDS);
		}
	}
	
	public void shutdownExecutor(ExecutorService executor, int awaitTermination){
		if(null!=executor && !executor.isShutdown()){
			executor.shutdown();
			try {
				if(!executor.awaitTermination(awaitTermination, TimeUnit.SECONDS)){
					executor.shutdownNow();
					if(!executor.awaitTermination(awaitTermination, TimeUnit.SECONDS)){
						log.error("Executor did not terminate");
					}
				}
			} catch (InterruptedException e) {
				executor.shutdownNow();
			}
		}
	}
}


class SynchronousTask  implements Callable<Map<String, Object>> {
	
	CommandLine adbShell;
	long timeout;
	
	public SynchronousTask(CommandLine adbShell, long timeout){
		this.adbShell = adbShell;
		this.timeout = timeout;
	}

	public Map<String, Object> call() throws Exception {
		return new AdbShellExecutor().execute(adbShell, true, timeout);
	}
}

class AsynchronousTask  implements Callable<Map<String, Object>> {
	
	CommandLine adbShell;
	long timeout;
	
	public AsynchronousTask(CommandLine adbShell, long timeout){
		this.adbShell = adbShell;
		this.timeout = timeout;
	}

	public Map<String, Object> call() throws Exception {
		return new AdbShellExecutor().execute(adbShell, false, timeout);
	}
}

/*class FollowerRpcRequest implements Callable<Object>{
	FollowerRpcClient client;
	Job job;

	public FollowerRpcRequest(FollowerRpcClient client, Job job){
		this.client = client;
		this.job = job;
	}
	
	public Object call(){
		try {
			return client.invoke(job);
		} catch (Throwable e) {
		}
		return null;
	}
	
}*/