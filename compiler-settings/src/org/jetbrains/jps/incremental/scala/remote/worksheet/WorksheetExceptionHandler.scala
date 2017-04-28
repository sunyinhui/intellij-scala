package org.jetbrains.jps.incremental.scala.remote.worksheet

import java.io.File

import org.jetbrains.jps.incremental.scala.remote.ServerException

/**
  * User: Dmitry.Naydanov
  * Date: 20.04.17.
  */
object WorksheetExceptionHandler {
  val RERUN_MARKER = "[RERUN]"
  
  def apply(cause: Throwable, dir: File, iteration: Int, rerun: Boolean,
            originalStackTrace: Option[Array[StackTraceElement]], pack: String): Exception = {
    val message = StringBuilder.newBuilder

    if (rerun) message.append(RERUN_MARKER)
    message.append(s"Worksheet Error: ${cause.getMessage} \n")
    
    Option(cause.getCause) foreach {
      c => message.append(s"Caused by ${c.getMessage}")
    }
    
    if (!dir.exists()) message.append(s"Worksheet source dir ${dir.getAbsolutePath} doesn't exist\n") else {
      val regex = """A\$A(\d+)\.class""".r.pattern

      val lastCompiledIteration = {
        val fileDir = if (pack.isEmpty) dir else new File(dir, pack)
        
        val a = fileDir.list().flatMap {
          fileName =>
            val matcher = regex.matcher(fileName)
            if (matcher.find() && matcher.start() == 0) Option(matcher.group(1)) else None
        }.map(Integer.parseInt)

        if (a.isEmpty) -1 else a.max
      }

      if (lastCompiledIteration != iteration)
        message.append(s"Worksheet iteration is $iteration but was compiled $lastCompiledIteration \n")
    }
    
    val stStrings = originalStackTrace match {
      case Some(stElements) => stElements.map(_.toString)
      case _ => Array.empty[String]
    }
    
    new ServerException(message.result(), stStrings)
  }
}
