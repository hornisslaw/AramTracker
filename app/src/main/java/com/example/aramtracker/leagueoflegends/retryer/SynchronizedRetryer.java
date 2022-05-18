package com.example.aramtracker.leagueoflegends.retryer;

import java.util.Optional;
import java.util.concurrent.Callable;

public class SynchronizedRetryer {

//    private final static Logger logger = LoggerFactory.getLogger(SynchronizedRetryer.class);

    private final static int DEFAULT_NUMBER_OF_RETRIES = 10;
    private final static int DEFAULT_DELAY = 1000;

    public synchronized <T> Optional<T> callWithRetries(Callable<T> callable, int numberOfRetries) {
        if (numberOfRetries == 0) {
            return Optional.empty();
        }

        try {
            Thread.sleep(DEFAULT_DELAY);
            return Optional.ofNullable(callable.call());
        } catch (Exception e) {
//            logger.warn("Exception during retrying, " + numberOfRetries + " retries remains", e);
            return callWithRetries(callable, numberOfRetries - 1);
        }
    }

    public <T> Optional<T> callWithRetries(Callable<T> callable) {
        return callWithRetries(callable, DEFAULT_NUMBER_OF_RETRIES);
    }

}
