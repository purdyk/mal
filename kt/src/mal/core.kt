package mal

class Core {


  companion object {
    val baseEnv
      get() = Env(null, MalList(emptyList()), MalList(emptyList())).apply {
        // Basic Math
        set("+", MalMethod { l ->
          l.mapNotNull { it as? MalInt }.reduce { acc, i ->  acc + i }
        })
        set("-", MalMethod { l ->
          l.mapNotNull { it as? MalInt }.reduce { acc, i -> acc - i }
        })
        set("*", MalMethod { l ->
          l.mapNotNull { it as? MalInt }.reduce { acc, i -> acc * i }
        })
        set("/", MalMethod { l ->
          l.mapNotNull { it as? MalInt }.reduce { acc, i -> acc / i }
        })

        // String Methods
        set("pr-str", MalMethod { l ->
          MalString(l.joinToString(separator = " ") { prStr(it, true) })
        })
        set("str", MalMethod { l ->
          MalString(l.joinToString(separator = "") { prStr(it, false) })
        })
        set("prn", MalMethod { l ->
          println(l.joinToString(separator = " ") { prStr(it, true) })
          MalNil()
        })
        set("println", MalMethod { l ->
          println(l.joinToString(separator = " ") { prStr(it, false) })
          MalNil()
        })

        // Comparisons
        set("=", MalMethod { l ->
          MalBool(isEqual(l[0], l[1]))
        })

        set("<", MalMethod { l ->
          MalBool((l[0] as MalInt).value < (l[1] as MalInt).value)
        })
        set("<=", MalMethod { l ->
          MalBool((l[0] as MalInt).value <= (l[1] as MalInt).value)
        })
        set(">", MalMethod { l ->
          MalBool((l[0] as MalInt).value > (l[1] as MalInt).value)
        })
        set(">=", MalMethod { l ->
          MalBool((l[0] as MalInt).value >= (l[1] as MalInt).value)
        })

        //List methods

        // Does this construct a list?
        set("list", MalMethod { l ->
          MalList(l)
        })

        set("list?", MalMethod { l ->
          MalBool(l[0] is MalList)
        })

        set("empty?", MalMethod { l ->
          MalBool((l[0] as MalVector).count() == 0)
        })

        set("count", MalMethod { l ->
          MalInt(when (val v = l[0]) {
            is MalVector -> v.count()
            else -> 0
          })
        })
      }

    private fun isEqual(l: MalType, r: MalType): Boolean {
      return when (l) {
        is MalInt -> l.value == (r as? MalInt)?.value
        is MalString -> l.value == (r as? MalString)?.value
        is MalBool -> l.value == (r as? MalBool)?.value
        is MalNil -> r is MalNil
        is MalVector -> {
          r is MalVector &&
              l.count() == r.count() &&
              l.withIndex().all {
                isEqual(it.value, r.items[it.index])
              }
        }
        else -> false
      }
    }
  }

}