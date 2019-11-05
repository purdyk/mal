package mal

fun main() {
  Step4().main()
}

class Step4 {
  fun main() {
    var c = true
    val env = Core.baseEnv
    // Language defined functions
    rep("(def! not (fn* (a) (if a false true)))", env, false)

    while (c) {
      print("user> ")
      readLine()?.let { rep(it, env) } ?: run {
        c = false
      }
    }
  }

  private fun rep(i: String, e: Env, print: Boolean = true) {
    try {
      var d = read(i)
      d = eval(d, e)
      if (print) println(prin(d))
    } catch (e: IllegalStateException) {
      System.err.println(e.message ?: "WTF")
    } catch (e: NoSuchElementException) {
      System.err.println("'${e.message}' not found")
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
      is MalList -> evalList(t, e)
      else -> evalAst(t, e)
    }
  }

  private fun evalList(t: MalList, e: Env): MalType {
    return if (t.items.isNotEmpty()) {
      when ((t.items[0] as? MalSymbol)?.value) {
        "if" -> {
          val test = eval(t.items.getOrNull(1) ?: MalNil(), e)
          if (!(test is MalNil || (test is MalBool && !test.value))) {
            eval(t.items.getOrNull(2) ?: MalNil(), e)
          } else {
            eval(t.items.getOrNull(3) ?: MalNil(), e)
          }
        }

        "do" -> {
          t.reduce { _, it -> eval(it, e) }
        }

        "fn*" -> {
          MalMethod { l ->
            val ne = Env(e, MalVector(l), t.items[1] as MalVector)
            eval(t.items[2], ne)
          }
        }

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


  private fun let(t: MalList, e: Env): MalType {
    val ne = Env.withList(e, t.items[1] as MalList)
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

