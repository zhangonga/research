package tech.zg.wc

import org.apache.flink.streaming.api.scala._

object WorkCountStream {

  def main(args: Array[String]): Unit = {
    val environment = StreamExecutionEnvironment.getExecutionEnvironment

    // 接受 socket 数据流
    val textDataStream = environment.socketTextStream("localhost", 18888)

    // 逐一读取数据，进行 workCount
    val workCountDataStream = textDataStream.flatMap(_.split("\\s"))
      .filter(_.nonEmpty)
      .map( (_, 1) )
      .keyBy(0)
      .sum(1)

    // 打印输出
    workCountDataStream.print()

    // 执行任务
    environment.execute("workCountStream")
  }
}
