package com.enterprise.pc.applicationlocationfree;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by PC on 2018-03-30.
 */

public class AppExecutors {

    private final Executor mStorageIO;

    private final Executor mNetworkIO;

    private final Executor mMainThread;

    private final Executor mDatabaseReadPeriodicallyThread;

    private AppExecutors(Executor storageIO, Executor networkIO, Executor mainThread, Executor mDatabaseReadPeriodicallyThread) {
        this.mStorageIO = storageIO;
        this.mNetworkIO = networkIO;
        this.mMainThread = mainThread;
        this.mDatabaseReadPeriodicallyThread = mDatabaseReadPeriodicallyThread;
    }

    public AppExecutors() {
        this(Executors.newSingleThreadExecutor(), Executors.newFixedThreadPool(3),
                new MainThreadExecutor(), Executors.newSingleThreadExecutor());
    }

    public Executor storageIO() {
        return mStorageIO;
    }

    public Executor networkIO() {
        return mNetworkIO;
    }

    public Executor mainThread() {
        return mMainThread;
    }

    public Executor databaseReadPeriodicallyThread() {
        return mDatabaseReadPeriodicallyThread;
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }

}
