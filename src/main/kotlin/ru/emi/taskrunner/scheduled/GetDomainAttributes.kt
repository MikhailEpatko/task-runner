package ru.emi.taskrunner.scheduled

import kotlinx.coroutines.delay
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.emi.taskrunner.common.SchedulingTask
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

@Service
class GetDomainAttributes : SchedulingTask() {
    override val name: String = javaClass.simpleName
    private val log: Logger = LoggerFactory.getLogger(javaClass.simpleName)

    override suspend fun execute() {
        log.info("--== $name ==--")
        delay(5.seconds)
        check(Random.nextInt() % 5 != 0) { "Не повезло" }
    }
}