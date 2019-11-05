package mal


fun prStr(t: MalType, pretty: Boolean = true): String =
    when (t) {
      is MalList -> t.joinToString(prefix = "(", postfix = ")", separator = " ") { prStr(it, pretty) }
      is MalVector -> t.joinToString(prefix = "[", postfix = "]", separator = " ") { prStr(it, pretty) }
      is MalHash -> t.items.entries.joinToString(prefix = "{", postfix = "}", separator = " ") {
        "${prStr(it.key, pretty)} ${prStr(it.value, pretty)}"
      }
      is MalInt -> t.value.toString()
      is MalSymbol -> t.value
      is MalQuote -> "(quote ${prStr(t.value, pretty)})"
      is MalQuasiQuote -> "(quasiquote ${prStr(t.value, pretty)})"
      is MalUnquote -> "(unquote ${prStr(t.value, pretty)})"
      is MalSpliceUnquote -> "(splice-unquote ${prStr(t.value, pretty)})"
      is MalBool -> t.value.toString()
      is MalNil -> "nil"
      is MalMeta -> "(with-meta ${prStr(t.with, pretty)} ${prStr(t.meta, pretty)})"
      is MalDeref -> "(deref ${t.symbol.value})"
      is MalMethod -> "#<fun>"

      is MalString -> {
        if (t.value.length > 1 && t.value[0] == '\u029E') {
          ":"+t.value.drop(1)
        } else {
          if (pretty) {
            "\"${
            t.value.replace("\\", "\\\\")
                .replace("\n", "\\n")
                .replace("\"", "\\\"")
            }\""
          } else {
            t.value
          }
        }
      }


      else -> "TYPE_ERROR"
    }