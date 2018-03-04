package com.martemyanova.brainmap

abstract class ABTesting() {

    abstract fun setDefault(defaults: Map<String, String>)

    abstract fun fetch(onComplete: (isSuccessful: Boolean) -> Unit)

    abstract fun getLong(key: String): Long

    abstract fun getString(key: String): String
}

class ABTestingDummyImpl(): ABTesting() {

    private lateinit var defaults: Map<String, String>

    override fun setDefault(defaults: Map<String, String>) { this.defaults = defaults }

    override fun fetch(onComplete: (isSuccessful: Boolean) -> Unit) { onComplete(true) }

    override fun getLong(key: String): Long = defaults.get(key)!!.toLong()

    override fun getString(key: String): String = defaults.get(key)!!

}