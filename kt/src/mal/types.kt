package mal

open class MalType

class MalList(val items: List<MalType>): MalType(), Iterable<MalType> by items
class MalVector(val items: List<MalType>): MalType(), Iterable<MalType> by items

class MalHash(ll: List<MalType>): MalType() {
  val items: Map<MalType, MalType> =
      ll.chunked(2) { it[0] to it[1] }
          .toMap()
}

open class MalScalar: MalType()

class MalInt(i: String): MalScalar() {
  val value = i.toInt()
}

class MalSymbol(val value: String): MalScalar()

class MalString(val value: String): MalScalar()

class MalBool(tf: String): MalScalar() {
  val value = tf == "true"
}

class MalNil(a: String): MalScalar()

class MalDeref(val symbol: MalSymbol): MalType()

class MalMeta(val meta: MalType, val with: MalType): MalType()

class MalQuote(val value: MalType): MalScalar()
class MalQuasiQuote(val value: MalType): MalScalar()
class MalUnquote(val value: MalType): MalScalar()
class MalSpliceUnquote(val value: MalType): MalScalar()
