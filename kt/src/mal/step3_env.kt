package mal

fun main() {
  Step3().main()
}

class Step3 {
  fun main() {
    var c = true
    val env = Env.baseEnv
    while (c) {
      print("user> ")
      readLine()?.let { rep(it, env) } ?: run {
        c = false
      }
    }
  }

  private fun rep(i: String, e: Env) {
    try {
      var d = read(i)
      d = eval(d, e)
      println(prin(d))
    } catch (e: IllegalStateException) {
      System.err.println(e.message ?: "WTF")
    } catch (e: NoSuchElementException) {
      System.err.println(".*'${e.message}' not found.*")
    }
  }

  private fun read(i: String) = readStr(i)

  private fun evalAst(t: MalType, e: Env): MalType {
    return when (t) {
      is MalSymbol -> e.get(t.value)
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

          when ((t.items[0] as MalSymbol).value) {
            "def!" -> {
              val v = eval(t.items[2], e)
              e.set(t.items[1], v)
              v
            }
            "let*" -> {
              let(t, e)
            }
            else -> {
              val n = evalAst(t, e) as MalList
              (n.items[0] as? MalMethod)?.invoke(n.items.drop(1)) ?: evalAst(n, e)
            }
          }
        } else {
          t
        }
      }
      else -> evalAst(t, e)
    }
  }

  private fun let(t: MalList, e: Env): MalType {
    val ne = Env(e, t.items[1])
    (t.items[1] as? MalList)?.chunked(2)?.forEach {
      ne.set(it[0], eval(it[1], ne))
    }
    (t.items[1] as? MalVector)?.chunked(2)?.forEach {
      ne.set(it[0], eval(it[1], ne))
    }
    return eval(t.items[2], ne)
  }

  private fun prin(i: MalType) = prStr(i)
}

