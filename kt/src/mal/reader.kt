package mal


fun readStr(str: String): MalType = readForm(Reader(tokenize(str)))

private val tkr = Regex(
    "[\\s,]*(~@|[\\[\\]{}()'`~^@]|\"(?:\\\\.|[^\\\\\"])*\"?|;.*|[^\\s\\[\\]{}('\"`,;)]*)")

private fun tokenize(str: String): List<String> =
    tkr.findAll(str)
        .map { it.value.trim(' ',',') }
        .filter { it.isNotEmpty() }
        .toList()
//        .also { println(it) }

private fun readForm(r: Reader): MalType =
    when (r.peek()[0]) {
      '(' -> readList(r)
      '[' -> readVector(r)
      '{' -> readHash(r)
      '"' -> readString(r)
      '\'', '`', '~' -> readQuote(r)
      '^' -> readMeta(r)
      '@' -> readDeref(r)
      else -> readAtom(r)
    }

private fun readList(r: Reader): MalList {
  return MalList(makeList(r))
}

private fun readVector(r: Reader): MalVector {
  return MalVector(makeList(r, "]"))
}

private fun readHash(r: Reader): MalHash {
  return MalHash(makeList(r, "}"))
}

private fun makeList(r: Reader, d: String = ")"): List<MalType> {
  r.read()
  val items: MutableList<MalType> = mutableListOf()
  while (r.peek() != d) {
    items.add(readForm(r))
    check(!r.empty()) { "unbalanced" }
  }
  r.read()
  return items
}

private fun readString(r: Reader): MalString {
  val s = r.read()
  check(s.length > 1) { "unbalanced" }
  check(s.endsWith("\"")) { "unbalanced" }
  check(!s.endsWith("\\\"")) { "unbalanced" }
  return MalString(s.trim('"'))
}

private fun readQuote(r: Reader): MalScalar {
  return when(r.read()) {
    "'" -> MalQuote(readForm(r))
    "`"-> MalQuasiQuote(readForm(r))
    "~@" -> MalSpliceUnquote(readForm(r))
    else -> MalUnquote(readForm(r))
  }
}

private fun readMeta(r: Reader): MalMeta {
  r.read()
  return MalMeta(readForm(r), readForm(r))
}

private fun readDeref(r: Reader): MalDeref {
  r.read()
  return (readForm(r) as? MalSymbol)?.let { MalDeref(it) } ?: run {
    throw IllegalArgumentException("non symbol dereference")
  }
}

private fun readAtom(r: Reader): MalScalar =
    when {
      r.peek().isNumber() -> MalInt(r.read())
      r.peek() == "nil" -> MalNil(r.read())
      r.peek() == "true" -> MalBool(r.read())
      r.peek() == "false" -> MalBool(r.read())
      else -> MalSymbol(r.read())
    }

private class Reader(private val data: List<String>) {
  var pos = 0
  fun peek() = data[pos]
  fun read() = data[pos++]
  fun empty() = pos == data.size
}

private val isN = Regex("-?\\d*\\.?\\d+")

private fun CharSequence.isNumber(): Boolean =
    isN.matches(this)
//    asSequence().firstOrNull {
//      when (it.toInt()) {
//        in '0'.toInt()..'9'.toInt() -> false
//        '-'.toInt() -> false
//        '.'.toInt() -> false
//        else -> true
//      }
//    } == null