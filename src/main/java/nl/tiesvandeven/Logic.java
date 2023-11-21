package nl.tiesvandeven;

import nl.tiesvandeven.models.InitialResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Logic {
	private final Client client;

	public Logic(Client client) {
		this.client = client;
	}

	public void doLogic(){
		Long startTime = System.currentTimeMillis();
		try(ExecutorService service = Executors.newVirtualThreadPerTaskExecutor()) {
			List<InitialResult> initialResults = service.submit(client::doInitialCall).get();
			List<Callable<String>> secondaryActions = initialResults.stream().map(r -> (Callable<String>) () -> {
//				System.out.println("start call");
				var s = client.doSecondaryCall(r.name());
//				System.out.println("call done");
				return s;
			}).toList();
			List<Future<String>> futures = service.invokeAll(secondaryActions);
			List<String> results = new ArrayList<>();
			for(Future<String> a : futures){
				results.add(a.get());
			}
			System.out.println(results.size());
			System.out.println(results);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		System.out.println("Took: " + (System.currentTimeMillis() - startTime) + "ms");
	}

}
