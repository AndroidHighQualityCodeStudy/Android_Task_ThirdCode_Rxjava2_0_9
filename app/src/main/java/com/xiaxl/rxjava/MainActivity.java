package com.xiaxl.rxjava;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.logging.Logger;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "xiaxl: MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.Button0001).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rxScheduler();
            }
        });

        findViewById(R.id.Button0002).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rxSchedulerMap();
            }
        });


    }

    /**
     * http://blog.csdn.net/qq_35064774/article/details/53057332
     */
    public void rxScheduler() {
        // create
        Subscriber subscriber = new Subscriber<String>() {
            @Override
            public void onSubscribe(Subscription s) {
                // 需要注意的是，在onSubscribe中，我们需要调用request去请求资源；
                // 参数就是要请求的数量，一般如果不限制请求数量，可以写成Long.MAX_VALUE；
                // 如果不调用request，Subscriber的onNext和onComplete方法将不会被调用。
                s.request(Long.MAX_VALUE);
                Log.e(TAG, "Subscriber  onSubscribe");

            }

            @Override
            public void onNext(String s) {
                Log.e(TAG, "Subscriber  onNext-->" + s);
            }

            @Override
            public void onError(Throwable t) {
                Log.e(TAG, "Subscriber  onError-->" + t);
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "Subscriber  onComplete");
            }
        };

        // create a flowable
        Flowable<String> flowable = Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(FlowableEmitter<String> e) throws Exception {
                Log.e(TAG, "Flowable  subscribe");
                e.onNext("1234567890");
                e.onComplete();
            }
        }, BackpressureStrategy.BUFFER);

        //
        flowable.subscribe(subscriber);

    }

    /**
     * http://blog.csdn.net/qq_28195645/article/details/52564494
     * <p>
     * Schedulers.immediate(): 直接在当前线程运行，相当于不指定线程。这是默认的 Scheduler。
     * Schedulers.newThread(): 总是启用新线程，并在新线程执行操作。
     * Schedulers.io(): I/O 操作（读写文件、读写数据库、网络信息交互等）所使用的 Scheduler。行为模式和 new Thread() 差不多，区别在于 io() 的内部实现是是用一个无数量上限的线程池，可以重用空闲的线程，因此多数情况下 io() 比new Thread()更有效率。不要把计算工作放在 io() 中，可以避免创建不必要的线程。
     * Schedulers.computation(): 计算所使用的 Scheduler。这个计算指的是 CPU 密集型计算，即不会被 I/O 等操作限制性能的操作，例如图形的计算。这个 Scheduler 使用的固定的线程池，大小为 CPU 核数。不要把 I/O 操作放在computation()中，否则 I/O 操作的等待时间会浪费 CPU。
     * 另外， Android 还有一个专用的 AndroidSchedulers.mainThread()，它指定的操作将在 Android 主线程运行。
     */
    private void rxSchedulerMap() {
        Flowable<String> flowable = Flowable.just(false)
                .subscribeOn(Schedulers.io())
                .map(new Function<Boolean, String>() {
                    @Override
                    public String apply(Boolean flag) throws Exception {
                        Log.e(TAG, "这是在io线程做的bitmap绘制圆形");

                        return "1234567890";
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(String string) throws Exception {
                        Log.e(TAG, "这是在main线程做的UI操作-->"+string);
                    }
                });
        flowable.subscribe();
    }


}
