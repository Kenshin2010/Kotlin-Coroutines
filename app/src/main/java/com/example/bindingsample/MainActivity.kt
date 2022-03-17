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
import kotlinx.coroutines.flow.*

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
        val flowOne = flowOf("Himanshu", "Amit", "Janishar").flowOn(Dispatchers.Default)
        val flowTwo = flowOf("Singh", "Shekhar", "Ali").flowOn(Dispatchers.Default)
        CoroutineScope(Dispatchers.Main).launch {
            flowOne.zip(flowTwo) { firstString, secondString ->
                "$firstString $secondString"
            }.collect {
                println("onEach: $it")
            }
        }
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