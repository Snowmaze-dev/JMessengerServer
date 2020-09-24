package jmessenger.jlanguage

abstract class Task(private val resultCallback: () -> Unit = {}) {

    var cancelled = false
    private set

    var running = false
    private set

    fun execute() {
        running = true
        run()
        running = false
        resultCallback()
    }

    protected abstract fun run()

    open fun cancel() {
        cancelled = true
    }

}