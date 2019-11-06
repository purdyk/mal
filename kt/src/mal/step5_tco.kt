package mal

fun main() {
  Step5().main()
}

class Step5 {
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

  private fun eval(pt: MalType, pe: Env): MalType {
    var t = pt
    var e = pe
    while (true) {
      if (t !is MalList) return evalAst(t, e)
      if (t.items.isEmpty()) return t

      when ((t.items[0] as? MalSymbol)?.value) {
        //TCO
        "if" -> {
          val test = eval(t.items.getOrNull(1) ?: MalNil(), e)
          t = if (!(test is MalNil || (test is MalBool && !test.value))) {
            t.items.getOrNull(2) ?: MalNil()
          } else {
            t.items.getOrNull(3) ?: MalNil()
          }
        }

        // TCO
        "do" -> {
          t.items.subList(1, t.items.count() -2).forEach { evalAst(it, e) }
          t = t.items.last()
        }

        //TCO
        "fn*" -> {
          t = MalClosure(e, t.items[2], t.items[1])
        }

        // TCO
        "def!" -> {
          val v = eval(t.items[2], e)
          e.set(t.items[1], v)
          return v
        }

        //TCO
        "let*" -> {
          val ne = Env.withList(e, t.items[1] as MalList)
          (t.items[1] as? MalList)?.chunked(2)?.forEach {
            ne.set(it[0], eval(it[1], ne))
          }
          (t.items[1] as? MalVector)?.chunked(2)?.forEach {
            ne.set(it[0], eval(it[1], ne))
          }

          t = t.items[2]
          e = ne
        }

        else -> {
          val n = evalAst(t, e) as MalList
          val f = n.items[0]

          // wew lads
          if (f is MalClosure) {
            e = Env(f.env, MalVector(n.items.drop(1)), f.params as MalVector)
            t = f.ast
          } else {
            return when (f) {
              is MalMethod -> f.invoke(n.items.drop(1))
              else -> evalAst(n, e)
            }
          }
        }
      }
    }
  }


  private fun prin(i: MalType) = prStr(i)
}

