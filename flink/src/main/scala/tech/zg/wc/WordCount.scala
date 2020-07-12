package tech.zg.wc

import org.apache.flink.api.scala._

/**
  * 批处理
  */
object WordCount {

  def main(args: Array[String]): Unit = {
    // 创建批处理执行环境
    val env = ExecutionEnvironment.getExecutionEnvironment

    // 文件中读取数据
    val inputPath = "C:\\work\\workspace\\research\\flink\\src\\main\\resources\\hello.txt"
    val inputDataSet = env.readTextFile(inputPath)

    // 分词之后做count
    val wordCountDateSet = inputDataSet.flatMap(_.split(" ")).map((_, 1)).groupBy(0).sum(1)

    // 打印输出
    wordCountDateSet.print();
  }
}
