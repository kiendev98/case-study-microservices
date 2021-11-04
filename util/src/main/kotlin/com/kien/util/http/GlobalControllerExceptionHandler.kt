package com.kien.util.http

import com.kien.api.exceptions.BadRequestException
import com.kien.api.exceptions.InvalidInputException
import com.kien.api.exceptions.NotFoundException
import com.kien.util.logs.logWithClass
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice


private val LOG = logWithClass<GlobalControllerExceptionHandler>()

@RestControllerAdvice
class GlobalControllerExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException::class)
    @ResponseBody
    fun handleBadRequestExceptions(
        request: ServerHttpRequest,
        ex: BadRequestException
    ): HttpErrorInfo {
        return createHttpErrorInfo(HttpStatus.BAD_REQUEST, request!!, ex)
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException::class)
    @ResponseBody
    fun handleNotFoundExceptions(
        request: ServerHttpRequest,
        ex: NotFoundException
    ): HttpErrorInfo =
        createHttpErrorInfo(HttpStatus.NOT_FOUND, request, ex)

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(InvalidInputException::class)
    @ResponseBody
    fun handleInvalidInputException(
        request: ServerHttpRequest,
        ex: InvalidInputException
    ): HttpErrorInfo =
        createHttpErrorInfo(HttpStatus.UNPROCESSABLE_ENTITY, request, ex)

    private fun createHttpErrorInfo(
        httpStatus: HttpStatus,
        request: ServerHttpRequest,
        ex: Exception
    ): HttpErrorInfo {
        val path = request.path.pathWithinApplication().value()
        val message = ex.message
        LOG.debug("Returning HTTP status: {} for path: {}, message: {}", httpStatus, path, message)
        return HttpErrorInfo(
            httpStatus = httpStatus,
            path = path,
            message = message
        )
    }
}
