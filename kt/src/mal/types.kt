package mal

open class MalType

class MalList(val items: List<MalType>): MalType(), Iterable<MalType> by items

open class MalScalar: MalType()

class MalInt(i: String): MalScalar() {
  val value = i.toInt()
}

class MalSymbol(val value: String): MalScalar()