package domain.services

import java.time.Clock
import java.util.concurrent.atomic.AtomicInteger

import com.google.inject.ImplementedBy
import javax.inject.Inject

@ImplementedBy(classOf[IdServiceLocalImpl])
trait IdService {
  def getUniqueId(): Long
}

class IdServiceLocalImpl @Inject()(configuration: IdService.Configuration,
                                   clock: Clock)
    extends IdService {

  private val maxMachineId = getMaxValue(configuration.machineBits)
  private val maxSequentialValue = getMaxValue(configuration.sequentialBits)

  require(configuration.machineId <= maxMachineId,
          s"Invalid IdService.machineId should be less than $maxMachineId")

  private val sequentialGenerator = new AtomicInteger(0)

  override def getUniqueId(): Long = synchronized {
    val id = clock.millis().toString +
      configuration.machineId.toString +
      sequentialGenerator.getAndIncrement().toString
    if (sequentialGenerator.get() > maxSequentialValue) {
      sequentialGenerator.set(0)
    }
    return id.toLong
  }

  private def getMaxValue(bits: Int): Long = {
    (Math.pow(2, configuration.machineBits) - 1).toInt
  }
}

object IdService {
  case class Configuration(
      machineBits: Int,
      sequentialBits: Int,
      machineId: Int
  )
}
