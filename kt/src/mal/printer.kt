package mal


fun prStr(t: MalType): String =
    when (t) {
      is MalList -> t.joinToString(prefix = "(", postfix = ")", separator = " ") { prStr(it) }
      is MalVector -> t.joinToString(prefix = "[", postfix = "]", separator = " ") { prStr(it) }
      is MalHash -> t.items.entries.joinToString(prefix = "{", postfix = "}", separator = " ") {
            "${prStr(it.key)} ${prStr(it.value)}"
      }
      is MalInt -> t.value.toString()
      is MalSymbol -> t.value
      is MalString -> "\"${t.value}\""
      is MalQuote -> "(quote ${prStr(t.value)})"
      is MalQuasiQuote -> "(quasiquote ${prStr(t.value)})"
      is MalUnquote -> "(unquote ${prStr(t.value)})"
      is MalSpliceUnquote -> "(splice-unquote ${prStr(t.value)})"
      is MalBool -> t.value.toString()
      is MalNil -> "nil"
      is MalMeta -> "(with-meta ${prStr(t.with)} ${prStr(t.meta)})"
      is MalDeref -> "(deref ${t.symbol.value})"
      else -> "TYPE_ERROR"
    }