package mal

fun main() {
  Step2().main()
}

class Step2 {
  fun main() {
    var c = true
    val env = Env()
    while (c) {
      print("user> ")
      readLine()?.let { println(rep(it, env)) } ?: run {
        c = false
      }
    }
  }

  private fun rep(i: String, e: Env): String = try {
    var d = read(i)
    d = eval(d, e)
    prin(d)
  } catch (e: IllegalStateException) {
    e.message ?: "WTF"
  } catch (e: NoSuchElementException) {
    "symbol not found"
  }

  private fun read(i: String) = readStr(i)

  private fun evalAst(t: MalType, e: Env): MalType {
    return when (t) {
      is MalSymbol -> e.symbols.getValue(t.value)
      is MalList -> MalList(t.items.map { eval(it, e) })
      is MalVector -> MalVector(t.items.map { eval(it, e) })
      is MalHash -> MalHash(t.items.flatMap { listOf(it.key, eval(it.value, e)) })
      else -> t
    }
  }

  private fun eval(t: MalType, e: Env): MalType {
    return when (t) {
      is MalList -> {
        if (t.items.isNotEmpty()) {
          val n = evalAst(t, e) as MalList
          (n.items[0] as MalMethod).invoke(n.items.drop(1))
        } else {
          t
        }
      }
      else -> evalAst(t, e)
    }
  }


  private fun prin(i: MalType) = prStr(i)

  class Env {
    val symbols: Map<String, MalMethod> =
        mapOf(
            "+" to MalMethod { l -> l.mapNotNull { it as? MalInt }.reduce { acc, i ->  MalInt(acc.value + i.value)}},
            "-" to MalMethod { l -> l.mapNotNull { it as? MalInt }.reduce { acc, i -> MalInt(acc.value - i.value )}},
            "*" to MalMethod { l -> l.mapNotNull { it as? MalInt }.reduce { acc, i -> MalInt(acc.value * i.value )}},
            "/" to MalMethod { l -> l.mapNotNull { it as? MalInt }.reduce { acc, i -> MalInt(acc.value / i.value )}}
        )
  }
}
