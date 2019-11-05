package mal

class Env(private val outer: Env?, binds: MalVector, exprs: MalVector) {
  private val symbols = HashMap<String, MalType>()

  init {
    for (it in exprs.withIndex()) {
      if ((it.value as? MalSymbol)?.value == "&") {
        set(exprs.items[it.index + 1], MalList(binds.items.drop(it.index)))
        break
      } else {
        set(it.value, binds.items[it.index])
      }
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
    fun withList(outer: Env?, load: MalVector): Env {
      val (f, s) = load.withIndex().partition { it.index.rem(2) == 1 }
      return Env(outer, MalList(f.map { it.value }), MalList(s.map { it.value }))
    }
  }
}