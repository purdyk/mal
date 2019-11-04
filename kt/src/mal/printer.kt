package mal


fun prStr(t: MalType): String =
    when (t) {
      is MalList -> t.joinToString(prefix = "(", postfix = ")", separator = " ") { prStr(it) }
      is MalInt -> t.value.toString()
      is MalSymbol -> t.value
      else -> "TYPE_ERROR"
    }