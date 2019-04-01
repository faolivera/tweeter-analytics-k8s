package com.github.faolivera.idservice

import java.time.Clock
import java.util.concurrent.atomic.AtomicInteger
import scala.concurrent.Future

class IdServiceImpl(configuration: IdServiceImpl.Configuration, clock: Clock)
    extends IdService {

  private val maxMachineId = getMaxValue(configuration.machineBits)
  private val maxSequentialValue = getMaxValue(configuration.sequentialBits)

  require(configuration.machineId <= maxMachineId,
          s"Invalid IdService.machineId should be less than $maxMachineId")

  private val sequentialGenerator = new AtomicInteger(0)

  private def getMaxValue(bits: Int): Long = {
    (Math.pow(2, configuration.machineBits) - 1).toInt
  }

  override def getId(in: IdRequest): Future[IdReply] = synchronized {
    val id = clock.millis().toString +
      configuration.machineId.toString +
      sequentialGenerator.getAndIncrement().toString
    if (sequentialGenerator.get() > maxSequentialValue) {
      sequentialGenerator.set(0)
    }
    return Future.successful(IdReply(id.toLong))
  }
}

object IdServiceImpl {
  case class Configuration(machineBits: Int,
                           sequentialBits: Int,
                           machineId: Int)
}
