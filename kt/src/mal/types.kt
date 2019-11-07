package mal

open class MalType

// Collections
open class MalVector(val items: List<MalType>) : MalType(), Iterable<MalType> by items
class MalList(items: List<MalType>) : MalVector(items)

class MalHash(ll: List<MalType>) : MalType() {
  val items: Map<MalType, MalType> =
      ll.chunked(2) { it[0] to it[1] }
          .toMap()
}

// Meta
class MalAtom(var symbol: MalType): MalType()

class MalMeta(val meta: MalType, val with: MalType) : MalType()

// Scalar
open class MalScalar : MalType()

class MalInt(val value: Int) : MalScalar() {
  constructor(i: String) : this(i.toInt())

  operator fun plus(other: MalInt) = MalInt(value + other.value)
  operator fun minus(other: MalInt) = MalInt(value - other.value)
  operator fun times(other: MalInt) = MalInt(value * other.value)
  operator fun div(other: MalInt) = MalInt(value / other.value)

}

class MalSymbol(val value: String) : MalScalar()
class MalString(val value: String) : MalScalar()

class MalBool(val value: Boolean) : MalScalar() {
  constructor(v: String): this(v == "true")
  constructor(v: MalInt): this(v.value != 0)
}

class MalNil : MalScalar()

class MalQuote(val value: MalType) : MalScalar()
class MalQuasiQuote(val value: MalType) : MalScalar()
class MalUnquote(val value: MalType) : MalScalar()
class MalSpliceUnquote(val value: MalType) : MalScalar()

class MalMethod(val m: (List<MalType>) -> MalType) : MalType() {
  fun invoke(args: List<MalType>) = m.invoke(args)
}

class MalClosure(val env: Env, val ast: MalType, val params: MalType): MalType() { }