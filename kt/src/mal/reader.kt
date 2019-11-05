package mal

import java.io.CharArrayWriter

fun readStr(str: String): MalType = Reader(str).form()

private class Reader(str: String) {
  var pos = 0
  fun peek() = data[pos]
  fun read() = data[pos++]
  fun empty() = pos == data.size

  private val tkr = Regex(
      "[\\s,]*(~@|[\\[\\]{}()'`~^@]|\"(?:\\\\.|[^\\\\\"])*\"?|;.*|[^\\s\\[\\]{}('\"`,;)]*)")

  private val data: List<String> = tkr.findAll(str)
      .map { it.value.trim(' ', ',') }
      .filter { it.isNotEmpty() }
      .toList()
//        .also { println(it) }

  fun form(): MalType =
      when (peek()[0]) {
        '(' -> list()
        '[' -> vector()
        '{' -> hash()
        '"' -> string()
        '\'', '`', '~' -> quote()
        '^' -> meta()
        '@' -> deref()
        ':' -> keyword()
        else -> atom()
      }

  private fun list(): MalList = MalList(makeList())
  private fun vector(): MalVector = MalVector(makeList())
  private fun hash(): MalHash = MalHash(makeList())
  private fun makeList(): List<MalType> {
    val d = when (read()) {
      "(" -> ")"
      "[" -> "]"
      "{" -> "}"
      else -> throw java.lang.IllegalStateException("unsupported list type")
    }

    val items: MutableList<MalType> = mutableListOf()
    while (peek() != d) {
      items.add(form())
      check(!empty()) { "unbalanced" } // Emptied list without terminator
    }
    read() // Discard terminator

    return items
  }

  // I'm not proud of this :'(
  private fun string(prefix: String = ""): MalString {
    val s = read().drop(1)

    check(s.isNotEmpty()) { "unbalanced" }

    val chs = CharArrayWriter()
    val it = s.iterator()
    var hadLast = false
    while (it.hasNext()) {
      when (val nc = it.nextChar()) {
        '\\' -> {
          when (it.nextChar()) {
            'n' -> chs.append('\n')
            '\\' -> chs.append('\\')
            '\"' -> chs.append('\"')
            else -> throw IllegalStateException("illegal escape")
          }
        }

        '\"' -> hadLast = true

        else -> chs.append(nc)
      }
    }

    val d = chs.toString()

    check(hadLast) { "unbalanced" }

    return MalString(prefix + d)
  }

  private fun keyword(): MalString {
    return MalString("\u029E" + read().drop(1))
  }

  // All quotes take the next whole form
// They may be nested
  private fun quote(): MalScalar {
    return when (read()) {
      "'" -> MalQuote(form())
      "`" -> MalQuasiQuote(form())
      "~@" -> MalSpliceUnquote(form())
      else -> MalUnquote(form())
    }
  }

  private fun meta(): MalMeta {
    read()
    return MalMeta(form(), form())
  }

  private fun deref(): MalDeref {
    read()
    return (form() as? MalSymbol)?.let { MalDeref(it) } ?: run {
      throw IllegalArgumentException("non symbol dereference")
    }
  }

  private fun atom(): MalScalar =
      peek().let { i ->
        when {
          i.isNumber() -> MalInt(read())
          i == "nil" -> MalNil().also { read() }
          i == "true" -> MalBool(read())
          i == "false" -> MalBool(read())
          else -> MalSymbol(read())
        }
      }

  private val isN = Regex("-?\\d*\\.?\\d+")

  private fun CharSequence.isNumber(): Boolean =
      isN.matches(this)
}


