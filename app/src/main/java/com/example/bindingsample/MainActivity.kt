package com.example.bindingsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.bindingsample.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

// link tham khảo flow:
//https://tech.miichisoft.net/ket-hop-flow-trong-coroutine-android/
// https://kotlinlang.org/docs/channels.html
//https://viblo.asia/p/cung-hoc-kotlin-coroutine-phan-11-channels-part-1-of-2-bJzKmJpXZ9N
//https://viblo.asia/p/cung-hoc-kotlin-coroutine-phan-7-xu-ly-exception-trong-coroutine-supervision-job-supervision-scope-naQZRDaG5vx

@InternalCoroutinesApi
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var job: Job
    private lateinit var flow: Flow<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.main = this
//
//        job = CoroutineScope(Dispatchers.IO).launchPeriodicAsync(500) {
//            println("Show result : ${System.currentTimeMillis()}")
//        }
    }

    private fun CoroutineScope.launchPeriodicAsync(repeatMillis: Long,action: () -> Unit) = this.async {
        if (repeatMillis > 0) {
            while (isActive) {
                action()
                delay(repeatMillis)
            }
        } else {
            action()
        }
    }

    public fun onClickEvent(view: View){
        binding.txtName = "test binding"
        binding.editName = "alo 123"
        main()
    }

    fun channelThreadError() = runBlocking {
        val channel = Channel<Int>()
        val job = launch {
            for (x in 1..5) {
                channel.send(x * x)
            }
        }

        println("Done! Coroutine is completed?: ${job.isCompleted} / Coroutine is active?: ${job.isActive}")
    }

    fun runTaskWithSupervisorJob() = runBlocking {
        val supervisor = SupervisorJob()
        with(CoroutineScope(coroutineContext + supervisor)) {
            // launch the first child -- its exception is ignored for this example (don't do this in practice!)
            val firstChild = launch(CoroutineExceptionHandler { _, _ ->  }) {
                println("First child is failing")
                throw AssertionError("First child is cancelled")
            }
            // launch the second child
            val secondChild = launch {
                firstChild.join()
                // Cancellation of the first child is not propagated to the second child
                println("First child is cancelled: ${firstChild.isCancelled}, but second one is still active")
                try {
                    delay(Long.MAX_VALUE)
                } finally {
                    // But cancellation of the supervisor is propagated
                    println("Second child is cancelled because supervisor is cancelled")
                }
            }
            // wait until the first child fails & completes
            firstChild.join()
            println("Cancelling supervisor")
            supervisor.cancel()
            secondChild.join()
        }
    }

    fun runTaskWithSupervisorScope() = runBlocking {
        val handler = CoroutineExceptionHandler { _, exception ->
            println("Caught $exception")
        }
        supervisorScope {
            val first = launch(handler) {
                println("Child throws an exception")
                throw AssertionError()
            }
            val second = launch {
                delay(100)
                println("Scope is completing")
            }
        }
        println("Scope is completed")
    }

    fun channelThreadError2() = runBlocking {
        val channel = Channel<Int>()
        val job = launch {
            for (x in 1..5) {
                channel.send(x * x)
            }
        }

        for (x in 1..10) {
            println("Coroutine is completed?: ${job.isCompleted} / Coroutine is active?: ${job.isActive}")
            println(channel.receive())
        }
        println("Done! Run blocking coroutine is active?: $isActive")
    }

    fun channelThread() = runBlocking {
        val channel = Channel<Int>()
        val job = launch {
            for (x in 1..4) {
                channel.send(x * x)
            }
        }
        // print 5 giá trị, trước khi nhận cho delay 1s
        delay(1000) // delay 1s
        println(channel.receive()) // nhận giá trị thứ 1
        delay(1000) // delay 1s
        println(channel.receive()) // nhận giá trị thứ 2
        delay(1000) // delay 1s
        println(channel.receive()) // nhận giá trị thứ 3
        delay(1000) // delay 1s
        println(channel.receive()) // nhận giá trị thứ 4
        delay(1000) // delay 1s
//        println(channel.receive()) // nhận giá trị thứ 5
        println("Done! Channel is empty?: ${channel.isEmpty} / Coroutine is completed?: ${job.isCompleted} / Coroutine is active?: ${job.isActive}")
    }

    fun channelThread2() = runBlocking {
        val channel = Channel<Int>()
        launch {
            for (x in 1..5) channel.send(x * x)
            channel.close() // we're done sending
        }
        // here we print received values using `for` loop (until the channel is closed)
        for (value in channel) println(value)
        println("Done!")
    }

    //
//    private fun launchIn() = runBlocking {
//        (1..2).asFlow()
//            .onEach {
//                delay(1000)
//            }.collect {
//                println(it)
//            }
//        println("DONE")
//    }

    private fun launchIn() = runBlocking { // xử lý song song
        val job = (1..20).asFlow()
            .onEach {
                delay(1000)
                println(it)
            }.launchIn(this)
        println("delay 5s")
        delay(5000)
        println("cancel")
        job.cancel()
    }

    public fun onClickDelay(view: View){

//        val job = lifecycleScope.launch(Dispatchers.Main) {
//            delay(3000)
//            Toast.makeText(this@MainActivity, "World", Toast.LENGTH_SHORT).show()
//        }
//        Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show()
//        job.cancel()
//        Thread.sleep(2000L)
//        lifecycleScope.launch (Dispatchers.Main){
//            println("Start delay")
//            delay(10000)
//            println("End delay")
//        }

//        job.cancel()

//        GlobalScope.launch {  // launch new coroutine in background and continue
//            delay(1000L) // non-blocking delay for 1 second (default time unit is ms)
//            println("World!") // print after delay
//            val sum1 = async { // non blocking sum1
//                delay(100L)
//                2 + 2
//            }
//            val sum2 = async { // non blocking sum2
//                delay(500L)
//                3 + 3
//            }
//            println("waiting concurrent sums")
//            val total = sum1.await() + sum2.await() // execution stops until both sums are calculated
//            println("Total is: $total")
//        }
//        println("Hello,")     // main thread continues while coroutine executes
//        Thread.sleep(2000L)   // block main thread for 2 seconds to keep JVM alive

//        test()

//        processValues()
//        lifecycleScope.launch {
//            startFlow()
//        }
//        CoroutineScope(Dispatchers.IO).launch {
//            startFlow().collect {
//                println("count: $it")
//            }
//            setupFlow().collect {
//                println("onEach: $it")
//            }
//        }
//        val flowOne = flowOf("Himanshu", "Amit", "Janishar").flowOn(Dispatchers.Default)
//        val flowTwo = flowOf("Singh", "Shekhar", "Ali").flowOn(Dispatchers.Default)
//        CoroutineScope(Dispatchers.Main).launch {
//            flowOne.zip(flowTwo) { firstString, secondString ->
//                "$firstString $secondString"
//            }.collect {
//                println("onEach: $it")
//            }
//        }
//                channelThread()
//        channelThreadError()
//        channelThreadError2()
//        channelThread2()

//        runTaskWithSupervisorJob()
        runTaskWithSupervisorScope()
    }

    private fun startFlow() = flow {
        println("Start Flow")
        repeat((0..10).count()) {
            delay(500)
            println("count: $it")
            emit(it)
        }
    }.flowOn(Dispatchers.IO)

//    private fun setupFlow() = (1..5).asFlow()
//        .onEach{
//            delay(300)
//        }
//        .flowOn(Dispatchers.Default)

    @ExperimentalCoroutinesApi
    private fun setupFlow() = channelFlow {
        (0..10).forEach {
            send(it)
        }
    }.flowOn(Dispatchers.Default)

    private fun test() = runBlocking {
        val startTime = System.currentTimeMillis()
        val job = launch(Dispatchers.IO) {
            var nextPrintTime = startTime
            var i = 0
            while (isActive) { // cancelable computation loop
                // print a message twice a second
                if (System.currentTimeMillis() >= nextPrintTime) {
                    println("I'm sleeping ${i++} ...")
                    nextPrintTime += 500L
                }
            }
        }
        delay(1300L) // delay a bit
        println("main: I'm tired of waiting!")
        job.cancelAndJoin() // cancels the job and waits for its completion
        println("main: Now I can quit.")
    }

    fun processValues() {
        runBlocking {
            val values = getValues()
            for (value in values) {
                println(value)
            }
        }
    }

//    suspend fun getValues(): List<Int> {
//        delay(1000)
//        return listOf(1, 2, 3)
//    }

    suspend fun getValues(): Sequence<Int> = sequence {
        Thread.sleep(250)
        yield(1)
        Thread.sleep(250)
        yield(2)
        Thread.sleep(250)
        yield(3)
    }

    fun simple(): Flow<Int> = flow {
        println("Flow started")
        for (i in 1..3) {
            delay(1000)
            emit(i)
        }
        emit(0)
    }

    fun main() = lifecycleScope.launch(Dispatchers.IO){
        println("Calling simple function...")
        val flow = simple()
        println("Calling collect...")
        flow.collect {
            println("test : $it")
        }
        println("Calling collect again...")
        flow.collect {
           println(it)
        }
    }
}