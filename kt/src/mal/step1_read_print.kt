package mal

fun main() {
  Step1().main()
}

class Step1 {
  fun main() {
    var c = true
    while (c) {
      print("user> ")
      readLine()?.let { println(rep(it)) } ?: run {
        c = false
      }
    }
  }

  private fun rep(i: String): String {
    try {
      var d = read(i)
      d = eval(d)
      return prin(d)
    } catch (e: IllegalStateException) {
      return e.message ?: "WTF"
    }
  }

  private fun read(i: String) = readStr(i)
  private fun eval(i: MalType) = i
  private fun prin(i: MalType) = prStr(i)
}