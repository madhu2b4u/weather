package com.demo.core.di

/**
 * A sealed class representing the result of an operation, encapsulating the state
 * of the result (Success, Error, or Loading).
 *
 * @param T The type of data being held.
 */

sealed class Result<out T> {

    /**
     * Represents a successful result.
     *
     * @param data The data associated with the success.
     */
    data class Success<out T>(val data: T) : Result<T>()

    data class Empty<out T>(val title: String, val message: String) : Result<T>()

    /**
     * Represents an error result.
     *
     * @param message A message describing the error.
     * @param data Optional data that might be available despite the error.
     */
    data class Error<out T>(val message: String, val data: T? = null) : Result<T>()

    /**
     * Represents a loading state.
     */
    data object Loading : Result<Nothing>()

    companion object {

        /**
         * Creates a Success instance.
         *
         * @param data The data to be wrapped in the Success result.
         * @return A Success instance with the provided data.
         */
        fun <T> success(data: T): Result<T> = Success(data)

        fun <T> empty(title: String, message: String): Result<T> = Empty(title, message)

        /**
         * Creates an Error instance.
         *
         * @param msg The error message.
         * @param data Optional data to be included with the error.
         * @return An Error instance with the provided message and optional data.
         */
        fun <T> error(msg: String, data: T? = null): Result<T> = Error(msg, data)

        /**
         * Creates a Loading instance.
         *
         * @return A Loading instance.
         */
        fun <T> loading(): Result<T> = Loading
    }
}