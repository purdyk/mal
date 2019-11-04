package mal


fun readStr(str: String): MalType = readForm(Reader(tokenize(str)))

private val tkr = Regex.fromLiteral(
    "[\\s,]*(~@|[\\[\\]{}()'`~^@]|\"(?:\\\\.|[^\\\\\"])*\"?|;.*|[^\\s\\[\\]{}('\"`,;)]*)\n")

private fun tokenize(str: String): List<String> = tkr.matchEntire(str)?.groupValues ?: emptyList()

private fun readForm(r: Reader): MalType =
    when (r.peek()[0]) {
      '(' -> readList(r)
      else -> readAtom(r)
    }

private fun readList(r: Reader): MalList {
  r.read()
  val items: MutableList<MalType> = mutableListOf()
  while (r.peek() != ")") {
    items.add(readForm(r))
  }
  return MalList(items)
}

private fun readAtom(r: Reader): MalScalar =
    when {
      r.peek().isNumber() -> MalInt(r.read())
      else -> MalSymbol(r.read())
    }

private class Reader(private val data: List<String>) {
  var pos = 0
  fun peek() = data[pos]
  fun read() = data[pos++]
}

private fun CharSequence.isNumber(): Boolean =
    asSequence().firstOrNull {
      when (it.toInt()) {
        in "0".toInt().."9".toInt() -> false
        "-".toInt() -> false
        ".".toInt() -> false
        else -> true
      }
    } == null