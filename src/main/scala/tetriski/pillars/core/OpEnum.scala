package tetriski.pillars.core

import tetriski.pillars.core.OpEnum.OpEnum

/** A object enumerating opcodes.
 * Not all opcodes can be performed by modules in our current hardware libs.
 */
object OpEnum extends Enumeration {
  type OpEnum = Value
  val NOP = Value("0")
  val SEXT = Value("1")
  val ZEXT = Value("2")
  val TRUNC = Value("3")
  val INPUT = Value("4")
  val OUTPUT = Value("5")
  val PHI = Value("6")
  val CONST = Value("7")
  val ADD = Value("8")
  val SUB = Value("9")
  val MUL = Value("10")
  val DIV = Value("11")
  val AND = Value("12")
  val OR = Value("13")
  val XOR = Value("14")
  val SHLL = Value("15")
  val SHRA = Value("16")
  val SHRL = Value("17")
  val LOAD = Value("18")
  val STORE = Value("19")
  val GEP = Value("20")
  val ICMP = Value("21")
  val SHR = Value("22")
  val SLT = Value("23")
  val SLTU = Value("24")
  val SHLA = Value("25")
  val INCR = Value("26")
}

/** A object translating opcodes in elements into the set of configuration of modules.
 */
object OpcodeTranslator {

  val aluOpcodeList = List(OpEnum.ADD,
    OpEnum.SUB,
    OpEnum.AND,
    OpEnum.OR,
    OpEnum.XOR,
    OpEnum.MUL,
    OpEnum.SLT,
    OpEnum.SHLL,
    OpEnum.SHLA,
    OpEnum.SHRL,
    OpEnum.SHRA,
    OpEnum.DIV)

  val commutativeSet = Set(OpEnum.ADD,
    OpEnum.AND,
    OpEnum.OR,
    OpEnum.XOR,
    OpEnum.MUL)

  /** Get the subset of optional operations of an ALU with a list of opcodes.
   *
   * @param opEnums   a list of opcodes
   * @param supBypass a parameter indicating whether the ALU should support bypass
   * @return the subset of optional operations of an ALU as Int
   */
  def getAluFunSelect(opEnums: List[OpEnum], supBypass: Boolean): Int = {
    var ret = 0
    opEnums.foreach(op => ret = ret | (1 << getModuleConfig(op)))
    if (supBypass) {
      ret = ret | (1 << 12)
      ret = ret | (1 << 13)
    }
    ret
  }

  /** Get the configuration of a module with a opcode.
   *
   * @param opEnum a opcode
   * @return the configuration
   */
  def getModuleConfig(opEnum: OpEnum): Int = {
    val ret = opEnum match {
      //ALU
      case OpEnum.ADD => 0
      case OpEnum.SUB => 1
      case OpEnum.AND => 2
      case OpEnum.OR => 3
      case OpEnum.XOR => 4
      case OpEnum.MUL => 5
      case OpEnum.SLT => 6
      case OpEnum.SHLL => 7
      case OpEnum.SHLA => 8
      case OpEnum.SHRL => 9
      case OpEnum.SHRA => 10
      case OpEnum.DIV => 11

      //LSU
      case OpEnum.LOAD => 0
      case OpEnum.STORE => 1

      //Const Unit
      case OpEnum.CONST => 0

      //Counter
      case OpEnum.INCR => 0
    }
    ret
  }

}


