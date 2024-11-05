package com.training.ecommerce.data.models

sealed class Resource<T>(
    val data: T? = null, val exception: Exception? = null
) {
    class Success<T> (data: T): Resource<T>(data)

    class Loading<T> (data: T? = null): Resource<T>(data)

    class Error<T> (massage : Exception, data: T? = null): Resource<T>(data, massage)
}