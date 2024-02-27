package com.dafay.demo.lib.base.net

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

/**
 * 枚举网络请求的各种结果(密封类通常用于\color{Red} {代替枚举类}。密封类的\color{Red} {优点在于可以更灵活地定义子类，而枚举类的每个成员都是固定的}。密封类还可以帮助你\color{Red} {编写更加类型安全的代码}。)
 * @param T
 */
sealed class Result<out T> {
    data class Success<out T>(val value: T) : Result<T>()
    data class Error(val code: Int? = null, val error: String? = null) : Result<Nothing>()
    object Loading : Result<Nothing>()
    object NetworkError : Result<Nothing>()
}

suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher,
    apiCall: suspend () -> T
): Result<T> {
    return withContext(dispatcher) {
        try {
            Result.Success(apiCall.invoke())
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> Result.NetworkError
                is HttpException -> {
                    val code = throwable.code()
                    val errorResponse = throwable.response()?.errorBody()?.string()
                    Result.Error(code, errorResponse)
                }

                else -> Result.Error(null, throwable.message)
            }
        }
    }
}
