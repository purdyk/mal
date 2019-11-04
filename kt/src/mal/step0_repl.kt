package mal

fun main() {
  Step0().main()
}

class Step0 {
  fun main() {
    var c = true
    while (c) {
      print("user> ")
      readLine()?.let {   rep(it) } ?: run {
        c = false
      }
    }
  }

  private fun rep(i: String): String {
    var d = read(i)
    d = eval(d)
    return prin(d)
  }

  private fun read(i: String) = i
  private fun eval(i: String) = i
  private fun prin(i: String) = i
}

