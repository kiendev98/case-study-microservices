package com.kien.util.logs

import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger

inline fun <reified T> logWithClass(): Logger = getLogger(T::class.java)
