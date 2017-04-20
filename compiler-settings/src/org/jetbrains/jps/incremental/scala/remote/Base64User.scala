package org.jetbrains.jps.incremental.scala.remote

import com.intellij.util.Base64Converter
/**
  * User: Dmitry.Naydanov
  * Date: 19.04.17.
  */

trait Base64User {
  def decodeBase64(s: String): String = Base64Converter.decode(s)
  def decodeBase64(bts: Array[Byte]): Array[Byte] = Base64Converter.decode(bts)
  def encodeBase64(s: String): String = Base64Converter.encode(s)
  def encodeBase64(octetString: Array[Byte]): String = Base64Converter.encode(octetString)
}
