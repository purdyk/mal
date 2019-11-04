package mal

class Env(private val outer: Env?, load: MalType? = null) {
  private val symbols = HashMap<String, MalType>()

  init {
    (load as? MalList)?.chunked(2)?.forEach {
      set(it[0], it[1])
    }
  }

  fun set(k: String, v: MalType) {
    symbols[k] = v
  }

  fun set(k: MalType, v: MalType) {
    symbols[(k as MalSymbol).value] = v
  }

  fun find(k: String): Boolean = symbols.containsKey(k) || outer?.find(k) == true

  fun get(k: String): MalType = symbols[k] ?: outer?.get(k) ?: throw NoSuchElementException(k)

  companion object {
    val baseEnv
      get() = Env(null).apply {
        set("+", MalMethod { l ->
          l.mapNotNull { it as? MalInt }.reduce { acc, i ->
            MalInt(acc.value + i.value)
          }
        })
        set("-", MalMethod { l ->
          l.mapNotNull { it as? MalInt }.reduce { acc, i ->
            MalInt(acc.value - i.value)
          }
        })
        set("*", MalMethod { l ->
          l.mapNotNull { it as? MalInt }.reduce { acc, i ->
            MalInt(acc.value * i.value)
          }
        })
        set("/", MalMethod { l ->
          l.mapNotNull { it as? MalInt }.reduce { acc, i ->
            MalInt(acc.value / i.value)
          }
        })
      }
  }
}